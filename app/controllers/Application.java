package controllers;

import java.util.List;

import models.GalleryIcon;
import play.mvc.Controller;

public class Application extends Controller {

    public static void index() {
        Blog.index();
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