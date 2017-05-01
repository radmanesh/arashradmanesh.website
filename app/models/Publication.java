package models;

import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.persistence.ManyToMany;

import play.db.jpa.Blob;
import play.db.jpa.Model;

@Entity
public class Publication extends Model {

    public String title;

    @Lob
    public String description;

    public Blob icon;

    public Blob attachment;

    public Date publishedAt;

    public Double price;

    @ManyToMany(cascade = CascadeType.PERSIST)
    public Set<Tag> tags;

    public Publication() {
        publishedAt = new Date();
    }

    public Publication tagItWith(String name) {
        tags.add(Tag.findOrCreateByName(name));
        return this;
    }

    public static List<Publication> findTaggedWith(String tag) {
        return Publication.find("select distinct p from Post p join p.tags as t where t.name = ?", tag).fetch();
    }

    public static List<Publication> findTaggedWith(String... tags) {
        return Publication.find(
                "select distinct p.id from Post p join p.tags as t where t.name in (:tags) group by p.id having count(t.id) = :size")
                .bind("tags", tags).bind("size", tags.length).fetch();
    }

}
