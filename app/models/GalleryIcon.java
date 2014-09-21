package models;

import javax.persistence.Entity;

import play.db.jpa.Blob;
import play.db.jpa.Model;

@Entity
public class GalleryIcon extends Model {
	
	public String type;
	
	public Blob graphic;
	
	public GalleryIcon(String type){
		this.type=type;
	}
    
}
