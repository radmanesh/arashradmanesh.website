package controllers;

import play.*;
import play.mvc.*;

import java.util.*;

import models.*;

public class Application extends Controller {

    public static void index() {
        blog();
    }
    
    public static void blog(){
    	render();
    }
    
    public static void showBlogPost(){
    	render();
    }

}