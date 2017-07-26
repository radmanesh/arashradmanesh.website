package controllers;

import models.*;
import play.mvc.Controller;

import java.util.List;

public class Blog extends Controller {

    public static void index() {
        List<Post> posts = Post.find("isPublished=?1 and type=?2",true, PostType.BLOG).fetch();
        render(posts);
    }

    public static void tagged(String tag){
        List<Post> posts = Post.findByTagAndType(PostType.BLOG,tag);
        render(posts,tag);
    }

    public static void showBlogPost(Long id) {
        Post post = Post.findById(id);
        notFoundIfNull(post);

        render(post);
    }

    public static void getPostIcon(Long id) {
        final Post post = Post.findById(id);
        if (post == null)
            renderBinary(DefaultConstants.DEFAULT_POST_ICON_FILE);

        if(post.iconUrl!=null && !post.iconUrl.isEmpty())
            redirect(post.iconUrl);

        if (post.icon==null || !post.icon.exists())
            renderBinary(DefaultConstants.DEFAULT_POST_ICON_FILE);

        response.setContentTypeIfNotSet(post.icon.type());
        java.io.InputStream binaryData = post.icon.get();
        renderBinary(binaryData);
    }

    public static void getPostTeaserIcon(Long id) {
        final Post post = Post.findById(id);
        if (post == null)
            renderBinary(DefaultConstants.DEFAULT_POST_TEASER_ICON_FILE);

        if(post.teaserIconUrl!=null && !post.teaserIconUrl.isEmpty())
            redirect(post.teaserIconUrl);

        if (post.teaserIcon==null || !post.teaserIcon.exists())
            renderBinary(DefaultConstants.DEFAULT_POST_TEASER_ICON_FILE);

        response.setContentTypeIfNotSet(post.teaserIcon.type());
        java.io.InputStream binaryData = post.teaserIcon.get();
        renderBinary(binaryData);
    }

    public static void sendComment(Long id) {
        Comment comment = new Comment(id);
        comment.edit(params.getRootParamNode(), "comment");
        validation.valid(comment);
        if(validation.hasErrors()){
            params.flash();
            validation.keep();
            showBlogPost(id);
        }
        comment.save();

        showBlogPost(id);
    }
    
    public static void replyComment(Long commentId,String authorName, String content){
        Comment comment = Comment.findById(commentId);
        comment.reply(authorName,content);
        showBlogPost(comment.getRoot().getPost().id);
    }

    public static void renderGraphicTemplate(Long id) {
        final GalleryIcon gt = GalleryIcon.findById(id);
        if (gt == null)
            notFound();

        response.setContentTypeIfNotSet(gt.graphic.type());
        renderBinary(gt.graphic.get());
    }

    public static void downloadAttachment(Long id){
        Attachment attachment = Attachment.findById(id);
        notFoundIfNull(attachment);
        renderBinary(attachment.file.getFile(),attachment.fileName);
    }

}
