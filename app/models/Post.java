package models;

import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.persistence.*;

import play.Logger;
import play.data.validation.URL;
import play.db.jpa.Blob;
import play.db.jpa.Model;
import utils.Utils;

@Entity
public class Post extends Model {

    public String title;

    @Lob
    public String content;

    @Lob
    public String excerpt;

    public Blob icon;

    public Blob teaserIcon;

    @URL
    public String teaserIconUrl;

    @URL
    public String iconUrl;

    public Date publishedAt;

    public Date modifiedAt;

    public String author;

    @Column(columnDefinition = "tinyint(1) default 0")
    public Boolean isPublished = Boolean.FALSE;

    @OneToMany(mappedBy = "post")
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

    public String createExcerpt(){
        if(Utils.isEmptyString(excerpt)){
            if(content!=null)
                if(content.length()<101)
                    return content;
                else
                    return content.substring(0,100);
            else
                return "";

        }else{
            return excerpt;
        }
    }

    public int countConfirmedComments(){
        int count = 0;
        try {
            long c = Comment.count("post=?1 and confirmed=?2 and deleted=?3",this,true,false);
            if(c>0)
                count = (int)c;
        }catch (Exception e){
            Logger.warn(e,"countConfirmedComments");
        }
        return count;
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
