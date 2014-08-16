package models;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;

import org.hibernate.annotations.Type;

import play.data.validation.IPv4Address;
import play.data.validation.Required;
import play.db.jpa.Model;
import controllers.security.Security;

@Entity
public class Log extends Model {

  public enum LogType {
    SYSTEM, USER, MISC, FRONTEND, BACKEND
  }

  @Required
  public LogType type;

  public Log(LogType type, String title, String message) {
    this.actor = Security.connectedAccount();
    this.timestamp = new Date();
    this.type = type;
    this.title = title;
    this.message = message;
    this.deleted = false;
  }

  public Date timestamp = new Date();

  @Required
  public String title;

  @Lob
  @Type(type = "org.hibernate.type.TextType")
  public String message;

  @ManyToOne
  public Account actor;

  @IPv4Address
  public String actorIp;

  public Boolean deleted = false;
}
