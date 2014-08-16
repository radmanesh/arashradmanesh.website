package controllers;

import java.util.List;

import models.GraphicTemplate;
import play.mvc.Controller;

public class Application extends Controller {

    public static void index() {
        Blog.index();
    }
    
    public static void blog(){
    	render();
    }
    
    public static void gallery(){
    	List<GraphicTemplate> teasers = GraphicTemplate.find("byType", "teaser").fetch();
    	List<GraphicTemplate> headers = GraphicTemplate.find("byType", "header").fetch();

    	render(teasers,headers);
    }
    
    public static void newTemplate(){
    	System.out.println(params.allSimple());
    	GraphicTemplate gt = new GraphicTemplate("teaser");
    	gt.edit(params.getRootParamNode(), "icon");
    	gt.save();
    	gallery();
    }
    
    public static void removeTemplate(Long id){
    	GraphicTemplate gt = GraphicTemplate.findById(id);
    	if(gt!=null){
    		gt.delete();
    	}
    	gallery();
    }

}