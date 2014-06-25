package models;

import play.*;
import play.db.jpa.*;

import javax.persistence.*;
import java.util.*;

@Entity
public class GraphicTemplate extends Model {
	
	public String type;
	
	public Blob graphic;
	
	public GraphicTemplate(String type){
		this.type=type;
	}
    
}
