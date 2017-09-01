package jobs;

import models.Account;
import models.AccountRole;
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
      //Fixtures.loadModels("data.yml");
        Account acc = new Account("Administrator","admin@local","hello123");
        acc.confirmed=true;
        acc.role = AccountRole.SUPERUSER;
        acc.needConfirmation = null;
        acc.save();
    }

    // Set language
    String lang = Configuration.get("websiteLanguage", "fa");
    play.i18n.Lang.change(lang);
    play.i18n.Lang.set(lang);

  }
}
