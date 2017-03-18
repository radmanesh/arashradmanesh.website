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

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import play.data.validation.Email;
import play.data.validation.URL;
import play.db.jpa.Model;

@Entity
public class Comment extends Model {

  @ManyToOne
  public Account author;
  
  @ManyToOne
  public Post post;
  
  @OneToMany(mappedBy="parent", cascade=CascadeType.ALL)
  public List<Comment> comments;
  
  @ManyToOne
  public Comment parent ;

  @Lob
  public String content;

  public Boolean confirmed = false;
  
  public Boolean deleted = false;

  public String authorName;
  
  @Email
  public String email;
  
  @URL
  public String website;

  public Date publishedAt;
  public Date modifiedAt;
  
  public Comment(Long postId){
      post = Post.findById(postId);
      comments = new ArrayList<Comment>();
      publishedAt = new Date();
      modifiedAt =  new Date();
  }
  
  public Comment(Post post,String content){
      comments = new ArrayList<Comment>();
      this.post = post;
      this.content = content;
      publishedAt  = new Date();
      modifiedAt = new Date();
  }
  
  public Comment(Comment cmnt,String content){
      comments = new ArrayList<Comment>();
      parent = cmnt;
      this.post = cmnt.getPost();
      this.content = content;
      publishedAt  = new Date();
      modifiedAt = new Date();
  }

  public Comment reply(String content) {
      Comment reply = new Comment(this, content);
      this.comments.add(reply);
      reply.save();
      return reply;

  }
  
  public Comment getRoot() {
      if (parent != null) {
          return parent.getRoot();
      }else {
          return this;
      }
  }

  public Post getPost() {
      if (post != null)
          return post;
      return getRoot().post;
  }

  
  public List<Comment> allDescendantComments(){
      List<Comment> allComments = new ArrayList<Comment>();
      if(comments==null || comments.isEmpty())
          return allComments;
      for(Comment c:comments){
          if(!c.deleted){
              allComments.add(c);
              allComments.addAll(c.allDescendantComments());
          }
      }
      return allComments;
  }
  
  public List<Comment> allDescendantConfirmedComments(){
      List<Comment> result = new ArrayList<Comment>();
      if(comments==null || comments.isEmpty())
          return result;

      for(Comment c:comments){
          if(!c.deleted){
              if(c.confirmed){ // Comment may have children
                  result.add(c);
                  result.addAll(c.allDescendantConfirmedComments());
              }
          }
      }
      return result;
  }
  
  public List<Comment> allDescendantPendingComments(){
      List<Comment> result = new ArrayList<Comment>();
      if(comments==null || comments.isEmpty())
          return result;

      for(Comment c:comments){
          if(!c.deleted){
              if(c.confirmed){ // Comment may have children
                  result.addAll(c.allDescendantPendingComments());
              }else{ // Comment cannot have children
                  result.add(c);
              }
          }
      }
      return result;
  }
}