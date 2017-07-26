package models;

import play.data.Upload;
import play.data.validation.Required;
import play.db.jpa.Blob;
import play.db.jpa.Model;
import play.mvc.Router;
import play.vfs.VirtualFile;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

@Entity
public class Attachment extends Model {

    @Required
    public String fileName;

    public Blob file;

    @Required
    @ManyToOne
    public Post parent;

    public Attachment(Post post){
        parent = post;
    }

    public Attachment(Post post, Upload upload){
        parent = post;
        file = new Blob();
        file.set(upload.asStream(),upload.getContentType());
        fileName = upload.getFileName();
    }

    public String getUrl(){
        return Router.reverse(VirtualFile.open(file.getFile()));
    }
}
