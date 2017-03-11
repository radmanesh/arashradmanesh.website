/*******************************************************************************
 *        File: Configuration.java
 *    Revision: 4
 *      Author: Morteza Ansarinia <ansarinia@me.com>
 *  Created on: Oct 9, 2014
 *     Project: onto.ads
 *   Copyright: See the file "LICENSE.md" for the full license governing this code.
 *******************************************************************************/
package models;

import javax.persistence.Column;
import javax.persistence.Entity;

import play.Logger;
import play.db.jpa.Model;

@Entity
public class Configuration extends Model {

    /** The key. */
    @Column(name = "configKey")
    public String key;

    /** The value. */
    @Column(name = "configValue")
    public String value;

    /** The name. */
    @Column(name = "configName")
    private String name;

    /**
     * Main constructor.
     *
     * @param name
     *            the name
     * @param key
     *            the key
     * @param value
     *            the value
     */
    public Configuration(String name, String key, String value) {
        this.name = name;
        this.key = key;
        this.value = value;
    }

    public String getName() {
        if (this.name != null) {
            return this.name;
        }
        return this.key;
    }

    /**
     * Returns value of configuration with the desired key.
     *
     * @param key
     *            the key
     * @param defaultValue
     *            the default value
     * @return the string
     */
    public static String get(String key, String defaultValue) {
        Configuration c = Configuration.find("key", key).first();
        if (c != null && c.value != null && !c.value.isEmpty()) {
            return c.value;
        }
        return defaultValue;
    }

    /**
     * Returns integer value of configuration with the desired key.
     *
     * @param key
     *            the key
     * @param defaultValue
     *            the default value
     * @return Integer
     */
    public static Integer get(String key, Integer defaultValue) {
        Configuration c = find("key", key).first();
        if (c != null) {
            try {
                return Integer.valueOf(c.value);
            }catch (Exception e) {
                Logger.debug(e, "");
            }
        }
        return defaultValue;
    }

    /**
     * Sets the.
     *
     * @param name
     *            the name
     * @param key
     *            the key
     * @param value
     *            the value
     */
    public static void set(String name, String key, String value) {
        Configuration c = find("key", key).first();
        if (c == null) {
            c = new Configuration(name, key, value);
        }else {
            c.name = name;
            c.value = value;
        }
        c.save();
    }

    /*
     * (non-Javadoc)
     * @see play.db.jpa.JPABase#toString()
     */
    public String toString() {
        return "Configuration[" + key + "]=" + value + "";
    }
}
