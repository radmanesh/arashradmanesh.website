package controllers;

import controllers.security.Check;
import controllers.security.Secure;
import models.*;
import play.Logger;
import play.data.FileUpload;
import play.i18n.Messages;
import play.libs.Codec;
import play.libs.Files;
import play.mvc.*;
import play.vfs.VirtualFile;
import utils.Utils;

import java.io.File;
import java.util.Date;
import java.util.List;

@With(Secure.class)
@Check(AccountRole.ADMINISTRATOR)
public class Admin extends Controller {

    @Before
    public static void debugBefore(){
        try {
            Logger.info("Action: %s , Params: %s",request.action,request.params.allSimple());

        }catch (Exception ex){
            Logger.debug(ex,"debugBefore");
        }
    }

    @After
    public static void debugAfter(){

    }

    public static void index() {
        List<Post> posts = Post.all().fetch();
        render(posts);
    }

    public static void newPost(Post post) {
        List<GalleryIcon> teasers = GalleryIcon.find("byType", "teaser").fetch();
        List<GalleryIcon> headers = GalleryIcon.find("byType", "header").fetch();

        if (post == null || request.current().method == "GET") {
            render(post, teasers, headers);
        }

        validation.valid(post);
        if (validation.hasErrors()) {
            validation.keep();
            params.flash();
            renderArgs.put("error", Messages.get("blog.error"));
            render(post, teasers, headers);
        }
        post.publishedAt = new Date();
        post.modifiedAt = new Date();

        if (params._contains("teaser-icon-id")) {
            try {
                Long teaserId = Long.valueOf(params.get("teaser-icon-id"));
                GalleryIcon gt = GalleryIcon.findById(Long.valueOf(teaserId));
                if (gt != null && gt.graphic.exists()) {
                    post.teaserIcon = gt.graphic;
                }
            } catch (Exception e) {
            }
        }

        if (params._contains("header-icon-id")) {
            try {
                Long headerId = Long.valueOf(params.get("header-icon-id"));
                GalleryIcon gt = GalleryIcon.findById(headerId);
                if (gt != null && gt.graphic.exists()) {
                    post.icon = gt.graphic;
                }
            } catch (Exception e) {
            }
        }

//        if (post.teaserIconUrl != null && !post.teaserIconUrl.isEmpty()) {
//            GalleryIcon.createFromUrl(post.teaserIconUrl, "teaser");
//        }
//
//        if (post.iconUrl != null && !post.iconUrl.isEmpty()) {
//            GalleryIcon.createFromUrl(post.iconUrl, "header");
//        }

        post.save();

        flash.success(Messages.get("blog.success"));
        index();
    }

    public static void updatePost(Long id) {
        Post post = Post.findById(id);
        notFoundIfNull(post);

        if (request.current().method.equals("GET")) {
            render(post);
        }

        String teaserIcon = post.teaserIconUrl;
        String postIcon = post.iconUrl;

        post.edit(params.getRootParamNode(), "post");

        if(!Utils.isEmptyString(postIcon) && Utils.isEmptyString(post.iconUrl))
            post.iconUrl = postIcon;
        if(!Utils.isEmptyString(teaserIcon) && Utils.isEmptyString(post.teaserIconUrl))
            post.teaserIconUrl = teaserIcon;

        validation.valid(post);
        if (validation.hasErrors()) {
            params.flash();
            validation.keep();
            render(post);
        }

        post.modifiedAt = new Date();
        post.save();
        flash.success(Messages.get("blog.success"));

        if (Utils.isEmptyString(post.teaserIconUrl)) {
            GalleryIcon.createFromUrl(post.teaserIconUrl, "teaser");
        }
        if (Utils.isEmptyString(post.iconUrl)) {
            GalleryIcon.createFromUrl(post.iconUrl, "header");
        }

        showBlogPost(id);
    }

    public static void deletePost(Long id) {
        Post post = Post.findById(id);
        notFoundIfNull(post);
        try {
            post.delete();
            flash.success(Messages.get("blog.success"));
            index();
        } catch (Exception e) {
            flash.error(play.i18n.Messages.get("post.deleteError"));
            index();
        }
    }

    public static void showBlogPost(Long id) {
        Post post = Post.findById(id);
        notFoundIfNull(post);

        render(post);
    }

    public static void getPostIcon(Long id) {
        Blog.getPostIcon(id);
    }

    public static void getPostTeaserIcon(Long id) {
        Blog.getPostTeaserIcon(id);
    }

    public static void uploadEditorImage(File file) {
        String name = file.getName();
        String ext = name.substring(name.lastIndexOf("."));
        File to = new File("public/upload/images/" + Codec.UUID() + ext);
        Files.copy(file, to);
        String url = Router.reverse(VirtualFile.open(to));
        renderText(Router.getBaseUrl() + url);
    }

    public static void renderGraphicTemplate(Long id) {
        Blog.renderGraphicTemplate(id);
    }

    public static void setCommentConfirmation(Long id,boolean value){
        Comment cmt = Comment.findById(id);
        if(cmt!=null){
            cmt.confirmed = value;
            cmt.save();
            //TODO: reload comments via eldarion
            showBlogPost(cmt.getPost().id);
        }
        index();
    }

    public static void setCommentDeletion(Long id,boolean value){
        Comment cmt = Comment.findById(id);
        if(cmt!=null){
            Long postId = cmt.getPost().id; 
            cmt.deleted= value;
            cmt.save();
            //TODO: reload comments via eldarion
            showBlogPost(postId);
        }
        index();
    }

    public static void showTags(){
        List<Tag> tags = Tag.findAll();
        render(tags);
    }

    public static void addTag(){
        String tagName = request.params.get("name");
        if(!Utils.isEmptyString(tagName)){
            Tag.findOrCreateByName(tagName).save();
        }
        showTags();
    }

    public static void addAttachmentToPost(Long postId,FileUpload attachment){
        notFoundIfNull(postId);
        Post post = Post.findById(postId);
        notFoundIfNull(post);
        notFoundIfNull(attachment);
        Attachment attach = new Attachment(post,attachment);
        post.attachments.add(attach);
        attach.save();

        updatePost(postId);
        //renderText(Router.reverse(VirtualFile.open( post.addAttachment(upload.asFile()) )));
    }

    public static void removeAttachment(Long id){
        Attachment attachment = Attachment.findById(id);
        notFoundIfNull(attachment);
        final Long postId = attachment.parent.id;
        attachment.parent.attachments.remove(attachment);
        attachment.parent.save();
        attachment.delete();
        updatePost(postId);
    }
}
