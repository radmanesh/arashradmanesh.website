
package models;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import play.data.validation.Email;
import play.data.validation.Password;
import play.data.validation.Required;
import play.data.validation.Unique;
import play.db.jpa.Model;
import play.libs.Codec;

@Entity
@Table(schema = "public", name = "account")
public class Account extends Model {

  @Email
  @Required
  @Unique
  public String email;

  @Password
  @Required
  public String passwordHash;

  @Required
  public String fullname;

  public boolean confirmed = false;

  public boolean deleted = false;

  /**
   * User specific configurations
   */
  @OneToMany
  public List<Configuration> configs = new ArrayList<Configuration>();

  public AccountRole role = AccountRole.CUSTOMER;

  /**
   * Constructor
   * 
   * @param email
   * @param password
   * @param name
   */
  public Account(String fullname, String email, String password) {
    this.email = email.toLowerCase();
    this.passwordHash = Codec.hexSHA1(password);
    this.fullname = fullname;
    this.confirmed = false;
    this.deleted = false;
    this.role = AccountRole.CUSTOMER;
  }

  /**
   * Verifies user password
   * 
   * @param password
   * @return {@code true} if password matches, {@code false} if password doesn't
   *         match
   */
  public boolean checkPassword(String password) {
    return passwordHash.equals(Codec.hexSHA1(password));
  }

  public void setPassword(String newPassword) {
    passwordHash = Codec.hexSHA1(newPassword);
  }

  /**
   * Finds a user by email address
   * 
   * @param email
   *          user email address
   * @return A {@code User}
   */
  public static Account findByEmail(String email) {
    return find("byEmail", email.toLowerCase()).first();
  }

  /**
   * Checks if the given email is available for sign up
   * 
   * @param email
   * @return {@code true} if there is no existing user with the given email,
   *         {@code false} otherwise.
   */
  public static boolean isEmailAvailable(String email) {
    return findByEmail(email.toLowerCase()) == null;
  }

  public String toString() {
    return "Account (" + email + ")";
  }

}