package models;

import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;

import play.db.jpa.Blob;
import play.db.jpa.Model;

@Entity
public class Post extends Model {

	public String title;

	@Lob
	public String content;

	public Blob icon;
	
	public Blob teaserIcon;

	public Date publishedAt;

	public Date modifiedAt;

	public String author;

	@OneToMany(mappedBy = "parent")
	public List<Comment> comments;

	@ManyToMany(cascade = CascadeType.PERSIST)
	public Set<Tag> tags;

	public Post() {
		publishedAt = new Date();
		modifiedAt = new Date();
		author = "آرش رادمنش";
	}

	public Post tagItWith(String name) {
		tags.add(Tag.findOrCreateByName(name));
		return this;
	}

	public static List<Post> findTaggedWith(String tag) {
		return Post.find("select distinct p from Post p join p.tags as t where t.name = ?", tag).fetch();
	}

	public static List<Post> findTaggedWith(String... tags) {
		return Post.find(
		                "select distinct p.id from Post p join p.tags as t where t.name in (:tags) group by p.id having count(t.id) = :size")
		                .bind("tags", tags).bind("size", tags.length).fetch();
	}

}
