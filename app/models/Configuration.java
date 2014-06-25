/*******************************************************************************
 *        File: Configuration.java
 *    Revision: 4
 *      Author: Morteza Ansarinia <ansarinia@me.com>
 *  Created on: Oct 9, 2014
 *     Project: onto.ads
 *   Copyright: See the file "LICENSE.md" for the full license governing this code.
 *******************************************************************************/
package models;

import javax.persistence.Entity;

import play.db.jpa.Model;

@Entity
public class Configuration extends Model {

  public String key;
  public String value;
  private String name;

  /**
   * Main constructor.
   * 
   * @param key
   * @param value
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
   * @return
   */
  public static String get(String key, String defaultValue) {
    Configuration c = find("key = ?", key).first();
    if (c != null) {
      return c.value;
    }
    return defaultValue;
  }

  /**
   * Returns integer value of configuration with the desired key.
   * 
   * @param key
   * @return Integer
   */
  public static Integer get(String key, Integer defaultValue) {
    Configuration c = find("key = ?", key).first();
    if (c != null) {
      return Integer.valueOf(c.value);
    }
    return defaultValue;
  }

  public static void set(String name, String key, String value) {
    Configuration c = find("key = ?", key).first();
    if (c == null) {
      c = new Configuration(name, key, value);
    } else {
      c.name = name;
      c.value = value;
    }
    c.save();
  }

  public String toString() {
    return "Configuration[" + key + "]=" + value + "";
  }
}
