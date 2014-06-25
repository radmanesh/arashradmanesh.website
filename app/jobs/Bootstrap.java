/*******************************************************************************
 *        File: Bootstrap.java
 *      Author: Morteza Ansarinia <ansarinia@me.com>
 *  Created on: Feb 24, 2014
 *     Project: onto.ads
 *   Copyright: See the file "LICENSE.md" for the full license governing this code.
 *******************************************************************************/
package jobs;

import models.Account;
import models.Configuration;
import play.jobs.Job;
import play.jobs.OnApplicationStart;
import play.test.Fixtures;

@OnApplicationStart
public class Bootstrap extends Job {
  @Override
  public void doJob() throws Exception {

    // Create default account
    if (Account.count() == 0) {
      Fixtures.loadModels("data.yml");
    }

    // Set language
    String lang = Configuration.get("websiteLanguage", "fa");
    play.i18n.Lang.change(lang);
    play.i18n.Lang.set(lang);

  }
}
