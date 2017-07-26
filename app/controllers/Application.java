package controllers;

import java.util.List;

import models.GalleryIcon;
import models.Post;
import models.PostType;
import play.mvc.Controller;

public class Application extends Controller {

    public static void index() {
        List<Post> blogPosts = Post.findWithType(PostType.BLOG,true);
        List<Post> libraryPosts = Post.findWithType(PostType.LIBRARY,true);
        render(blogPosts,libraryPosts);
    }

    public static void blog() {
        render();
    }

    public static void gallery() {
        List<GalleryIcon> teasers = GalleryIcon.find("byType", "teaser").fetch();
        List<GalleryIcon> headers = GalleryIcon.find("byType", "header").fetch();

        render(teasers, headers);
    }

    public static void newTemplate() {
        System.out.println(params.allSimple());
        GalleryIcon gt = new GalleryIcon("teaser");
        gt.edit(params.getRootParamNode(), "icon");
        gt.save();
        gallery();
    }

    public static void removeTemplate(Long id) {
        GalleryIcon gt = GalleryIcon.findById(id);
        if (gt != null) {
            gt.delete();
        }
        gallery();
    }

}