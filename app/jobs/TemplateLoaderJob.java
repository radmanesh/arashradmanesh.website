/*******************************************************************************
 *        File: TemplateLoader.java
 *    Revision: 1
 * Description: 
 *      Author: Arman Radmanesh <arman@ratnic.se>
 *  Created on: Oct 21, 2014
 *     Project: ratnic.quizzer
 *   Copyright: See the file "LICENSE" for the full license governing this code.
 *******************************************************************************/

package jobs;

import java.util.Arrays;
import java.util.List;

import models.Configuration;
import play.Logger;
import play.jobs.Job;
import play.jobs.OnApplicationStart;
import play.mvc.Controller;

// TODO: Auto-generated Javadoc
/**
 * The Class TemplateLoader.
 *
 * @author: Arman Radmanesh <arman@ratnic.se>
 * @version: 1
 */

@OnApplicationStart
public class TemplateLoaderJob extends Job {
    
    //TODO: change this way
    /** The Constant templates. */
    public static final List<String> templates = Arrays.asList("","template2");
    
    @Override
    public void doJob() throws Exception {
        String templateN = Configuration.get("arashradmanesh.template","");
        if(templateN==null){
            Controller.registerTemplateNameResolver(null);
        }else{
            Controller.registerTemplateNameResolver(new PrefixTemplateNameResolver(templateN));
        }
    }
    
    /**
     * The Class PrefixTemplateNameResolver.
     *
     * @author: Arman Radmanesh <arman@ratnic.se>
     * @version: 1
     */
    public class PrefixTemplateNameResolver implements Controller.ITemplateNameResolver{
        
        /** The prefix. */
        private String prefix = "";
        
        /**
         * Instantiates a new prefix template name resolver.
         *
         * @param prefixString the prefix string
         */
        public PrefixTemplateNameResolver(String prefixString) {
            prefix=prefixString;
        }
        
        public String resolveTemplateName(String templateName) {
            if(prefix.equalsIgnoreCase("")){
                return templateName;
            }
            try {
                String candidate = "templates"+System.getProperty("file.separator")+ prefix+ System.getProperty("file.separator") +templateName;
                if(play.vfs.VirtualFile.fromRelativePath("app/views/"+candidate).exists()){
                    return candidate;
                }else{
                    Logger.warn("cant find template: %s , using path: %s ", candidate, play.vfs.VirtualFile.fromRelativePath(candidate).relativePath());
                    return templateName;
                }
            } catch (Exception e) {
                Logger.warn(e , "exception occured when resolving template ");
                return templateName;
            }
                
        }
        
    }
    
}
