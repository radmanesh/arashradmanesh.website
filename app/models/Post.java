package models;

import play.*;
import play.db.jpa.*;

import javax.persistence.*;
import java.util.*;

@Entity
public class Post extends Model {
	
	public String title;
    
	@Lob
	public String content;
	
	public Date publishedAt;
	
	public Date editedAt;
	
	public String author;
	
	@OneToMany(mappedBy="parent")
	public List<Comment> comments;
}
