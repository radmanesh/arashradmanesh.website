package models;

import javax.persistence.Entity;

import play.Logger;
import play.db.jpa.Blob;
import play.db.jpa.Model;
import play.jobs.Job;
import play.libs.WS;
import play.libs.WS.HttpResponse;

@Entity
public class GalleryIcon extends Model {
	
	public String type;
	
	public Blob graphic;

	public String description;

	public String originalUrl;
	
	public String fileName="N/A";

	public GalleryIcon(String type){
		this.type=type;
	}
	
	public static void createFromUrl(final String url,final String type){
	    try{
            if(GalleryIcon.count("originalUrl", url)>0)
                return;
            Job createJob = new Job(){
                /* (non-Javadoc)
                 * @see play.jobs.Job#doJob()
                 */
                @Override
                public void doJob() throws Exception {
                    try {
                        HttpResponse response = WS.url(url).get();
                        if(response.success() ){ //&& response.getContentType()==
                            GalleryIcon gt = new GalleryIcon(type);
                            Logger.info("responseType: %s",response.getContentType());
                            gt.graphic.set(response.getStream(), response.getContentType());
                            gt.originalUrl = url;
                            gt.fileName = utils.Utils.extractFileNameFromUrl(url);
                            gt.description = gt.fileName;
                            Logger.info("createFromUrl , contentType: %s , url: %s", response.getContentType(),url);
                            gt.save();
                        }
                    } catch (Exception e) {
                        Logger.warn(e, "createFromUrl");
                    }
                }
            };
            createJob.now();

        }catch (Exception e){
            Logger.info(e,"createFromUrl: %s , type: %s ",url,type);
        }
	}
}
