package models;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

import play.db.jpa.Model;

@Entity
public class Comment extends Model {
    
	public String content;
	
	public Date publishedAt;
	
	public String name;
	
	public String email;
	
	public String website;
	
	@ManyToOne
	public Post parent;
	
	public Comment(Long parentId) throws NullPointerException{
		Post p = Post.findById(parentId);
		if(p!=null)
			this.parent = p;
		else throw new NullPointerException("Post with the given id was not found!") ;
		
		publishedAt = new Date();
	}
}
