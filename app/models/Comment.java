package models;

import play.*;
import play.db.jpa.*;

import javax.persistence.*;
import java.util.*;

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
