package controllers.security;

import java.lang.reflect.InvocationTargetException;

import models.Account;
import models.AccountRole;
import play.mvc.Controller;
import play.utils.Java;

public class Security extends Controller {

  /**
   * This method is called during the authentication process. This is where you
   * check if the user is allowed to log in into the system. This is the actual
   * authentication process against a third party system (most of the time a
   * DB).
   *
   * @param username
   * @param password
   * @return true if the authentication process succeeded
   */
  static boolean authenticate(String username, String password) {
    Account account = Account.find("byEmail", username).first();
    return (account != null) && (account.confirmed) && (!account.deleted)
        && account.checkPassword(password);
  }

  /**
   * This method checks that a role is allowed to view this page/method. This
   * method is called prior to the method's controller annotated with the @Check
   * method.
   *
   * @param role
   * @return true if you are allowed to execute this controller method.
   */
  static boolean check(AccountRole role) {
    Account account = Account.find("byEmail", connected()).first();
    if (role != null && role.ordinal() <= account.role.ordinal())
      return true;
    return false;
  }

  public static Account connectedAccount() {
    return Account.find("byEmail", connected()).first();
  }

  /**
   * This method returns the current connected username
   * 
   * @return
   */
  public static String connected() {
    return session.get("username");
  }

  /**
   * Indicate if a user is currently connected
   * 
   * @return true if the user is connected
   */
  public static boolean isConnected() {
    return session.contains("username");
  }

  /**
   * This method is called after a successful authentication. You need to
   * override this method if you with to perform specific actions (eg. Record
   * the time the user signed in)
   */
  static void onAuthenticated() {
  }

  /**
   * This method is called before a user tries to sign off. You need to override
   * this method if you wish to perform specific actions (eg. Record the name of
   * the user who signed off)
   */
  static void onDisconnect() {
  }

  /**
   * This method is called after a successful sign off. You need to override
   * this method if you wish to perform specific actions (eg. Record the time
   * the user signed off)
   */
  static void onDisconnected() {
  }

  /**
   * This method is called if a check does not succeed. By default it shows the
   * not allowed page (the controller forbidden method).
   * 
   * @param profile
   */
  static void onCheckFailed() {
    forbidden();
  }

  protected static Object invoke(String m, Object... args) throws Throwable {

    try {
      return Java.invokeChildOrStatic(Security.class, m, args);
    } catch (InvocationTargetException e) {
      throw e.getTargetException();
    }
  }

}
