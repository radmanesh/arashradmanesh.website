
package models;

import play.data.validation.*;
import play.db.jpa.Model;
import play.i18n.Messages;
import play.libs.Codec;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "account")
public class Account extends Model {

    @Email
    @Required
    @Unique
    public String email;

    @Password
    @Required
    public String passwordHash;

    @Transient
    @Min(6)
    public String password;

    @Required
    public String fullname;

    @Column(columnDefinition = "boolean default false")
    public boolean confirmed = false;

    @Column(columnDefinition = "boolean default false")
    public boolean deleted = false;

    public String needConfirmation;

    @URL
    public String website;

    /**
     * User specific configurations
     */
    @OneToMany
    public List<Configuration> configs = new ArrayList<Configuration>();

    public AccountRole role = AccountRole.USER;

    /**
     * Constructor
     *
     * @param email
     * @param password
     * @param name
     */
    public Account(String fullname, String email, String password) {
        this.email = email.toLowerCase();
        this.password = password;
        this.passwordHash = Codec.hexSHA1(password);
        this.fullname = fullname;
        this.confirmed = false;
        this.deleted = false;
        this.role = AccountRole.USER;
        this.needConfirmation = Codec.UUID();
    }

    public boolean isActive() {
        return (confirmed && !deleted);
    }

    /**
     * Verifies user password
     *
     * @param password
     * @return {@code true} if password matches, {@code false} if password doesn't
     * match
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
     * @param email user email address
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
     * {@code false} otherwise.
     */
    public static boolean isEmailAvailable(String email) {
        return findByEmail(email.toLowerCase()) == null;
    }

    public static Account findByRegistrationUUID(String uuid) {
        return find("needConfirmation", uuid).first();
    }

    public String toString() {
        return "Account (" + email + ")";
    }

}