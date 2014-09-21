package controllers;

import java.util.List;
import java.util.Map;

import models.AccountRole;
import models.Configuration;
import play.Play;
import play.i18n.Messages;
import play.mvc.Controller;
import play.mvc.With;
import controllers.security.Check;
import controllers.security.Secure;

@With( Secure.class)
@Check(AccountRole.ADMINISTRATOR)
public class Settings extends Controller {

  public static void index() {
    List<Configuration> configs = Configuration.findAll();
    for (Configuration config : configs) {
      String key = /* CONFIG_PREFIX + "." + */config.key;
      renderArgs.put(config.getName(), config.get(key, ""));
    }
    render();
  }

  public static void all() {
    List<Configuration> configs = Configuration.findAll();
    render(configs);
  }

  public static void deleteAll() {
    Configuration.deleteAll();

    flash.success(Messages.get("ratnic.framework.SettingsSaved"));
    flash.keep();
    index();
  }

  public static void submit() {
    for (Map.Entry<String, String> entry : params.allSimple().entrySet()) {
      String key = entry.getKey();
      String value = entry.getValue();

      // Ignore common Play! request parameters (body, controller, action).
      if (!("body".equalsIgnoreCase(key) || "controller".equalsIgnoreCase(key) || "action"
          .equals(key))) {

        // Append application name as prefix to the config keys.
        String CONFIG_PREFIX = Play.configuration
            .getProperty("application.name");
        key = CONFIG_PREFIX + "." + key;

        Configuration.set(entry.getKey(), key, value);

        // Update language on changing its value
        if ("language".equalsIgnoreCase(entry.getKey()))
          updateCurrentLanguage();
      }
    }

    flash.success(Messages.get("ratnic.framework.SettingsSaved"));
    flash.keep();
    index();
  }

  private static void updateCurrentLanguage() {
    String appName = Play.configuration.getProperty("application.name");
    String lang = Configuration.get(appName + ".language", "fa");
    play.i18n.Lang.change(lang);
    play.i18n.Lang.set(lang);
  }
}
