/**
 * 
 */
package utils;

/**
 * @author Arman Radmanesh
 * @date May 1, 2017
 * @version 1
 * 
 */
public class Utils {
    public static boolean isEmptyString(String s){
        if(s==null || s.isEmpty())
            return true;
        return false;
    }
    
    public static String extractFileNameFromUrl(String url){
        String res ="N/A";
        int lastIndex = url.lastIndexOf("/");
        if(lastIndex>0 && lastIndex<url.length()){
            res = url.substring(lastIndex);
        }
        return res;
    }
}
