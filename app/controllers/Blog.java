package controllers;

import java.io.File;
import java.io.InputStream;
import java.util.Date;
import java.util.List;

import models.Comment;
import models.Configuration;
import models.GalleryIcon;
import models.Post;
import play.Logger;
import play.i18n.Messages;
import play.libs.Codec;
import play.libs.Files;
import play.libs.WS;
import play.libs.WS.HttpResponse;
import play.mvc.Controller;
import play.mvc.Router;
import play.vfs.VirtualFile;

public class Blog extends Controller {

    public static void index() {
        List<Post> posts = Post.all().fetch();
        render(posts);
    }

    public static void showBlogPost(Long id) {
        Post post = null;
        post = Post.findById(id);
        notFoundIfNull(post);

        render(post);
    }

    public static void getPostIcon(Long id) {
        final Post post = Post.findById(id);
        if (post == null)
            notFound();

        if(post.iconUrl!=null && !post.iconUrl.isEmpty())
            redirect(post.iconUrl);

        if (post.icon==null || !post.icon.exists())
            renderBinary(new File("public/img/hands-big.png"));

        response.setContentTypeIfNotSet(post.icon.type());
        java.io.InputStream binaryData = post.icon.get();
        renderBinary(binaryData);
    }

    public static void getPostTeaserIcon(Long id) {
        final Post post = Post.findById(id);
        if (post == null)
            notFound();

        if(post.iconUrl!=null && !post.iconUrl.isEmpty())
            redirect(post.iconUrl);

        if (post.teaserIcon==null || !post.teaserIcon.exists())
            renderBinary(new File("public/img/hands.png"));

        response.setContentTypeIfNotSet(post.teaserIcon.type());
        java.io.InputStream binaryData = post.teaserIcon.get();
        renderBinary(binaryData);
    }

    public static void uploadEditorImage(File file) {
        String name = file.getName();
        String ext = name.substring(name.lastIndexOf("."));
        File to = new File("public/upload/images/" + Codec.UUID() + ext);
        Files.copy(file, to);
        String url = Router.reverse(VirtualFile.open(to));
        renderText(Router.getBaseUrl() + url);
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
    
    public static void replyComment(Long commentId,String content){
        Comment comment = Comment.findById(commentId);
        comment.reply(content);
        showBlogPost(comment.getRoot().getPost().id);
    }

    public static void renderGraphicTemplate(Long id) {
        final GalleryIcon gt = GalleryIcon.findById(id);
        if (gt == null)
            notFound();

        response.setContentTypeIfNotSet(gt.graphic.type());
        java.io.InputStream binaryData = gt.graphic.get();
        renderBinary(binaryData);
    }

}
