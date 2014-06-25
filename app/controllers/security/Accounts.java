package controllers.security;

import java.util.List;

import models.Account;
import models.AccountRole;
import play.i18n.Messages;
import play.mvc.Controller;
import play.mvc.With;

@With({ Secure.class})
@Check(AccountRole.ADMINISTRATOR)
public class Accounts extends Controller {

  public static void index() {
    List<Account> accounts = Account.findAll();
    renderTemplate("Security/Accounts/index.html", accounts);
  }

  public static void register(String fullname, String email, String password) {
    // TODO show for for guests on GET, and saves it on POST
    // TODO validate
    Account account = new Account(fullname, email, password);
    validation.valid(account);
    if (validation.hasErrors()) {
      for (play.data.validation.Error e : validation.errors()) {
        System.err.println(e.message());
      }
      params.flash();
      validation.keep();
      flash.error(Messages.get("accounts.AccountRegistrationFailed"));
      index();
    }
    account.save();
    index();
  }

  public static void edit(Long id) {
    if (Security.connectedAccount().role.ordinal() >= AccountRole.ADMINISTRATOR
        .ordinal() || (Security.connectedAccount().id == id)) {
      Account account = Account.findById(id);
      renderTemplate("Security/Accounts/edit.html", account);
    }
    forbidden();
  }

  public static void update(Long id) {
    Account account = Account.findById(id);
    if (account == null)
      notFound();

    account.edit(params.getRootParamNode(), "account");

    validation.valid(account);

    if (validation.hasErrors()) {
      for (play.data.validation.Error e : validation.errors()) {
        System.err.println(e.message());
      }
      params.flash();
      validation.keep();
      renderTemplate("Security/Accounts/edit.html", account);
    }

    // Update account.passwordHash if request[password] is not empty
    String password = params.get("password");
    if (password != null && password.trim().length() > 7) {
      account.passwordHash = play.libs.Codec.hexSHA1(password);
    }

    account.save();

    flash.success(Messages.get("accounts.AccountUpdated"));
    index();
  }

  public static void delete(Long id) {
    Account account = Account.findById(id);
    if (account.role.ordinal() >= AccountRole.ADMINISTRATOR.ordinal()) {
      flash.error(Messages.get("accounts.AccountCannotBeDeleted"));
      flash.keep();
      index();
    }

    // TODO @Check if current account or admin account
    account.deleted = true;
    account.save();

    flash.success(Messages.get("accounts.AccountDeleted"));
    flash.keep();
    index();
  }

  public static void confirm(Long id) {
    Account account = Account.findById(id);
    // TODO @Check if current account or admin account
    account.confirmed = true;
    account.save();

    flash.success(Messages.get("accounts.AccountConfirmed"));
    flash.keep();
    index();
  }

  public static void activate(Long id) {
    confirm(id);
  }

  public static void deactivate(Long id) {
    Account account = Account.findById(id);
    // TODO @Check if current account or admin account
    account.confirmed = false;
    account.save();

    flash.success(Messages.get("accounts.AccountDeactivated"));
    flash.keep();
    index();
  }

}
