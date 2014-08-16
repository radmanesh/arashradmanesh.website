package controllers;

import java.io.File;
import java.util.Date;
import java.util.List;

import models.Comment;
import models.GraphicTemplate;
import models.Post;
import play.i18n.Messages;
import play.libs.Codec;
import play.libs.Files;
import play.mvc.Controller;
import play.mvc.Router;
import play.vfs.VirtualFile;

public class Blog extends Controller {

    public static void index() {
    	List<Post> posts = Post.all().fetch();
        render(posts);
    }
    
    public static void newPost(Post post){
    	List<GraphicTemplate> teasers = GraphicTemplate.find("byType", "teaser").fetch();
    	List<GraphicTemplate> headers = GraphicTemplate.find("byType", "header").fetch();
    	
        if(post==null || request.current().method=="GET") {
            render(post,teasers,headers);
        }

        validation.valid(post);
        if(validation.hasErrors()){
            System.err.println(validation.errors());
            renderArgs.put("error",Messages.get("blog.error"));
            render(post,teasers,headers);
        }
        post.publishedAt = new Date();
        post.modifiedAt = new Date();
        
        String teaserId = params.get("teaser-icon-id");
        System.out.println(teaserId);
        GraphicTemplate gt = GraphicTemplate.findById(Long.valueOf(teaserId));
        if(gt!=null || gt.graphic.exists()){
        	post.teaserIcon = gt.graphic;
        }

        String headerId = params.get("header-icon-id");
        System.out.println(headerId);
        gt = GraphicTemplate.findById(Long.valueOf(headerId));
        if(gt!=null || gt.graphic.exists()){
        	post.icon = gt.graphic;
        }

        
        post.save();

        flash.success(Messages.get("blog.success"));
        index();
    }
    
    public static void updatePost(Long id){
        Post post = Post.findById(id);
        if(post==null)
            notFound();

        if (request.current().method.equals("GET")) {
            render(post);
        }
        
        post.edit(params.getRootParamNode(), "post");

        validation.valid(post);
        if(validation.hasErrors()){
            params.flash();
            validation.keep();
            render(post);
        }

        post.modifiedAt = new Date();
        post.save();

        flash.success(Messages.get("blog.success"));
        index();
    }

    public static void deletePost(Long id) {
        Post post=Post.findById(id);
        if(post==null)
            notFound();
        try {
            post.delete();
            flash.success(Messages.get("blog.success"));
            index();
        } catch (Exception e) {
            flash.error(play.i18n.Messages.get("post.deleteError"));
            index();
        }
    }
    
    public static void showBlogPost(Long id){
    	Post post = Post.findById(id);
    	System.out.println(post.teaserIcon.getFile().getAbsolutePath());
    	if(post==null)
    		notFound();
    	
    	render(post);
    }
    
    public static void getPostIcon(Long id){
    	final Post post = Post.findById(id);
    	if(post==null)
    		notFound();
    	if(!post.icon.exists())
    		renderBinary(new File("public/img/hands-big.png"));
    	
    	response.setContentTypeIfNotSet(post.icon.type());
    	java.io.InputStream binaryData = post.icon.get();
    	renderBinary(binaryData);
    }

    
    public static void getPostTeaserIcon(Long id){
    	final Post post = Post.findById(id);
    	if(post==null)
    		notFound();
    	if(!post.teaserIcon.exists())
    		renderBinary(new File("public/img/hands.png"));
    	
    	response.setContentTypeIfNotSet(post.teaserIcon.type());
    	java.io.InputStream binaryData = post.teaserIcon.get();
    	renderBinary(binaryData);
    }

    
    public static void uploadEditorImage(File file){
        String name = file.getName();
        String ext = name.substring(name.lastIndexOf("."));
        File to = new File("public/upload/images/" + Codec.UUID() + ext);
        Files.copy(file, to);
        String url = Router.reverse(VirtualFile.open(to));
        renderText("http://localhost:9000"+url);        
    }
    
    public static void sendComment(Long id){
    	Comment comment = new Comment(id);
    	comment.edit(params.getRootParamNode(), "comment");
    	comment.save();
    	
    	showBlogPost(id);
    }
    
    public static void renderGraphicTemplate(Long id){
    	final GraphicTemplate gt = GraphicTemplate.findById(id);
    	if(gt==null)
    		notFound();
    	
    	response.setContentTypeIfNotSet(gt.graphic.type());
    	java.io.InputStream binaryData = gt.graphic.get();
    	renderBinary(binaryData);
    }

}
