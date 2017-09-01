package models;

import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.persistence.*;

import play.data.validation.Min;
import play.data.validation.Required;
import play.db.jpa.Blob;
import play.db.jpa.Model;
import utils.DefaultConstants;

@Entity
public class Publication extends Model {

    @OneToOne
    @Required
    public Post post;

    @Min(DefaultConstants.DEFAULT_MIN_PRICE)
    public Integer price;

    @Min(DefaultConstants.DEFAULT_MIN_PRICE)
    public Integer minPrice = new Integer((int)DefaultConstants.DEFAULT_MIN_PRICE);

    //@Column(columnDefinition = "tinyint(1) default 0")
    public Boolean freeDownload = Boolean.FALSE;

    //@Column(columnDefinition = "tinyint(1) default 0")
    public Boolean fixedPrice = Boolean.FALSE;

    public Publication() {
        post = new Post(PostType.PUBLICATION);
    }

    public Publication tagItWith(String name) {
        post.tagItWith(name);
        return this;
    }

    public static List<Publication> findTaggedWith(String tag) {
        return Publication.find("select distinct p from Publication p join p.post.tags as t where t.name = ?", tag).fetch();
    }

    public static List<Publication> findTaggedWith(String... tags) {
        return Publication.find(
                "select distinct p.id from from Publication p join p.post.tags as t where t.name in (:tags) group by p.id having count(t.id) = :size")
                .bind("tags", tags).bind("size", tags.length).fetch();
    }

}
