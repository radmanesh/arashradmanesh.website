package controllers;

import java.io.File;
import java.util.Date;
import java.util.List;

import controllers.security.Check;
import controllers.security.Secure;
import models.AccountRole;
import models.Comment;
import models.GalleryIcon;
import models.Post;
import play.i18n.Messages;
import play.libs.Codec;
import play.libs.Files;
import play.mvc.*;
import play.vfs.VirtualFile;

@With(Secure.class)
@Check(AccountRole.ADMINISTRATOR)
public class Admin extends Controller {

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

        if (post.teaserIconUrl != null && !post.teaserIconUrl.isEmpty()) {
            GalleryIcon.createFromUrl(post.teaserIconUrl, "teaser");
        }

        if (post.iconUrl != null && !post.iconUrl.isEmpty()) {
            GalleryIcon.createFromUrl(post.iconUrl, "header");
        }

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

        post.edit(params.getRootParamNode(), "post");

        validation.valid(post);
        if (validation.hasErrors()) {
            params.flash();
            validation.keep();
            render(post);
        }

        if (post.teaserIconUrl != null && !post.teaserIconUrl.isEmpty()) {
            GalleryIcon.createFromUrl(post.teaserIconUrl, "teaser");
        }
        if (post.iconUrl != null && !post.iconUrl.isEmpty()) {
            GalleryIcon.createFromUrl(post.iconUrl, "header");
        }

        post.modifiedAt = new Date();
        post.save();

        flash.success(Messages.get("blog.success"));
        index();
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
        Post post = null;
        post = Post.findById(id);
        notFoundIfNull(post);

        render(post);
    }

    public static void getPostIcon(Long id) {
        final Post post = Post.findById(id);
        if (post == null)
            notFound();
        if (!post.icon.exists())
            renderBinary(new File("public/img/hands-big.png"));

        response.setContentTypeIfNotSet(post.icon.type());
        java.io.InputStream binaryData = post.icon.get();
        renderBinary(binaryData);
    }

    public static void getPostTeaserIcon(Long id) {
        final Post post = Post.findById(id);
        if (post == null)
            notFound();
        if (!post.teaserIcon.exists())
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

    public static void renderGraphicTemplate(Long id) {
        final GalleryIcon gt = GalleryIcon.findById(id);
        if (gt == null)
            notFound();

        response.setContentTypeIfNotSet(gt.graphic.type());
        java.io.InputStream binaryData = gt.graphic.get();
        renderBinary(binaryData);
    }
}
