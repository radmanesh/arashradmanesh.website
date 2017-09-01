/*******************************************************************************
 *        File: Comment.java
 *      Author: Arman Radmanesh <arman@ratnic.se>
 *  Created on: Feb 23, 2014
 *     Project: cms
 *   Copyright: See the file "LICENSE" for the full license governing this code.
 *******************************************************************************/
package models;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.*;

import org.hibernate.annotations.Type;
import play.data.validation.Email;
import play.data.validation.URL;
import play.db.jpa.Model;

@Entity
public class Comment extends Model {

    @ManyToOne
    public Account author;

    @ManyToOne
    public Post post;

    @OneToMany(mappedBy = "parent")
    public List<Comment> children;

    @ManyToOne(targetEntity = Comment.class)
    public Comment parent;

    @Lob
    @Type(type="org.hibernate.type.TextType")
    public String content;

    //@Column(columnDefinition = "tinyint(1) default 0")
    @Column(columnDefinition = "boolean default false")
    public Boolean confirmed = false;

    //@Column(columnDefinition = "tinyint(1) default 0")
    @Column(columnDefinition = "boolean default false")
    public Boolean deleted = false;

    public String authorName;

    @Email
    public String email;

    @URL
    public String website;

    public Date publishedAt;

    public Date modifiedAt;

    public Comment(Long postId) {
        post = Post.findById(postId);
        children = new ArrayList<Comment>();
        publishedAt = new Date();
        modifiedAt = new Date();
    }

    public Comment(Post post, String content) {
        children = new ArrayList<Comment>();
        this.post = post;
        this.content = content;
        publishedAt = new Date();
        modifiedAt = new Date();
    }

    public Comment(Comment parentComment, String content) {
        children = new ArrayList<Comment>();
        parent = parentComment;
        //post = parentComment.getPost();
        this.content = content;
        publishedAt = new Date();
        modifiedAt = new Date();
    }

    public Comment reply(String content) {
        Comment reply = new Comment(this, content);
        reply.save();
        this.children.add(reply);
        reply.save();
        return reply;
    }

    public Comment reply(String authorName,String content) {
        Comment reply = new Comment(this, content);
        reply.authorName = authorName;
        reply.save();
        this.children.add(reply);
        reply.save();
        return reply;
    }

    public Comment getRoot() {
        if (parent != null) {
            return parent.getRoot();
        } else {
            return this;
        }
    }

    public Post getPost() {
        if (post != null)
            return post;
        return getRoot().post;
    }

    public List<Comment> allDescendantComments() {
        List<Comment> allComments = new ArrayList<Comment>();
        if (children == null || children.isEmpty())
            return allComments;
        for (Comment c : children) {
            if (!c.deleted) {
                allComments.add(c);
                allComments.addAll(c.allDescendantComments());
            }
        }
        return allComments;
    }

    public List<Comment> allDescendantConfirmedComments() {
        List<Comment> result = new ArrayList<Comment>();
        if (children == null || children.isEmpty())
            return result;

        for (Comment c : children) {
            if (!c.deleted) {
                if (c.confirmed) { // Comment may have children
                    result.add(c);
                    result.addAll(c.allDescendantConfirmedComments());
                }
            }
        }
        return result;
    }

    public List<Comment> allDescendantPendingComments() {
        List<Comment> result = new ArrayList<Comment>();
        if (children == null || children.isEmpty())
            return result;

        for (Comment c : children) {
            if (!c.deleted) {
                if (c.confirmed) { // Comment may have children
                    result.addAll(c.allDescendantPendingComments());
                } else { // Comment cannot have children
                    result.add(c);
                }
            }
        }
        return result;
    }
}