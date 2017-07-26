package models;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.persistence.*;

import play.Logger;
import play.data.Upload;
import play.data.validation.URL;
import play.db.jpa.Blob;
import play.db.jpa.Model;
import play.libs.Codec;
import play.libs.Files;
import play.libs.MimeTypes;
import play.mvc.Router;
import play.vfs.VirtualFile;
import utils.Utils;

@Entity
public class Post extends Model {

    public String title;

    @Lob
    public String content;

    @Lob
    public String excerpt;

    public PostType type;

    public Blob icon;

    @URL
    public String iconUrl;

    public Blob teaserIcon;

    @URL
    public String teaserIconUrl;

    public Date publishedAt;

    public Date modifiedAt;

    public String author;

    @Column(columnDefinition = "tinyint(1) default 0")
    public Boolean isPublished = Boolean.FALSE;

    @OneToMany(mappedBy = "post")
    public List<Comment> comments;

    @ManyToMany(cascade = CascadeType.PERSIST)
    public Set<Tag> tags;

    @OneToMany(mappedBy = "parent", orphanRemoval = true)
    public List<Attachment> attachments = new ArrayList<>();

    public Post() {
        publishedAt = new Date();
        modifiedAt = new Date();
        author = "آرش رادمنش";
        type = PostType.BLOG;
    }

    public Post(PostType postType) {
        type = postType;
        publishedAt = new Date();
        modifiedAt = new Date();
        author = "آرش رادمنش";
    }

    public Post tagItWith(String name) {
        tags.add(Tag.findOrCreateByName(name));
        return this;
    }

    public String createExcerpt() {
        if (Utils.isEmptyString(excerpt)) {
            if (content != null)
                if (content.length() < 101)
                    return content;
                else
                    return content.substring(0, 100);
            else
                return "";

        } else {
            return excerpt;
        }
    }

    public int countConfirmedComments() {
        int count = 0;
        try {
            long c = Comment.count("post=?1 and confirmed=?2 and deleted=?3", this, true, false);
            if (c > 0)
                count = (int) c;
        } catch (Exception e) {
            Logger.warn(e, "countConfirmedComments");
        }
        return count;
    }

    public List<Post> relatedPosts() {
        String[] tagsArray = new String[tags.size()];
        int i = 0;
        for (Tag t : tags)
            tagsArray[i++] = t.name;
        return Post.findByAnyTagsAndType(type,tagsArray);
    }

    public static List<Post> findByType(PostType type,int size){
        return Post.find("type = ?1 and isPublished = ?2", type, true).fetch(size);
    }

    public static List<Post> findByTagAndType(PostType type, String tag) {
        return Post.find("select distinct p from Post p join p.tags as t where t.name = ?1 and p.type = ?2 and isPublished = true", tag, type).fetch();
    }

    public static List<Post> findByAnyTagsAndType(PostType type, String... tags) {
        return Post.find(
            "select distinct p from Post p join p.tags as t where t.name in (:tags) and p.type=:type and isPublished = true")
            .bind("tags", tags).bind("type",type).fetch();
    }

    public static List<Post> findAllWithType(PostType postType) {
        return Post.find("type=?1", postType).fetch();
    }

    public static List<Post> findWithType(PostType postType, boolean published) {
        return Post.find("type = ?1 and isPublished = ?2", postType, published).fetch();
    }

    public static List<Post> findTaggedWith(String tag) {
        return Post.find("select distinct p from Post p join p.tags as t where t.name = ? and isPublished = true", tag).fetch();
    }

    public static List<Post> findTaggedWithAny(String... tags) {
        return Post.find(
            "select distinct p from Post p join p.tags as t where t.name in (:tags) and isPublished = true")
            .bind("tags", tags).fetch();
    }

    public static List<Post> findTaggedWithAll(String... tags) {
        return Post.find(
            "select distinct p from Post p join p.tags as t where t.name in (:tags) and isPublished = true group by p.id having count(t.id) = :size")
            .bind("tags", tags).bind("size", tags.length).fetch();
    }

}
