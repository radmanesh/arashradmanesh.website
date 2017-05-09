package models;

import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.persistence.*;

import play.data.validation.Min;
import play.db.jpa.Blob;
import play.db.jpa.Model;
import utils.DefaultConstants;

@Entity
public class Publication extends Model {

    public String title;

    @Lob
    public String description;

    public Blob icon;

    public Blob attachment;

    public Date publishedAt;

    @Min(DefaultConstants.DEFAULT_MIN_PRICE)
    public Integer price;

    @Min(DefaultConstants.DEFAULT_MIN_PRICE)
    public Integer minPrice = DefaultConstants.DEFAULT_MIN_PRICE;

    @ManyToMany(cascade = CascadeType.PERSIST)
    public Set<Tag> tags;

    @Column(columnDefinition = "tinyint(1) default 0")
    public Boolean freeDownload = Boolean.FALSE;

    @Column(columnDefinition = "tinyint(1) default 0")
    public Boolean fixedPrice = Boolean.FALSE;

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
