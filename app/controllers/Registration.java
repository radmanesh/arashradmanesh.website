package controllers;

import controllers.security.Secure;
import controllers.security.Security;
import models.Account;
import notifiers.Notifier;
import play.Logger;
import play.data.validation.*;
import play.i18n.Messages;
import play.mvc.Before;
import play.mvc.Controller;

import static controllers.security.Secure.login;

public class Registration extends Controller {

    @Before
    static void notLoggedIn() {
        if (Security.isConnected()) {
            flash.error(Messages.get("security.alreadyLoggedIn", Security.connected()));
            flash.keep();
            Application.index();
        }
    }

    public static void signup(){
            render();
    }

    public static void register(@Required @Email String email, @Required @MinSize(5) String password, @Equals("password") String password2, @Required String fullname,@URL String website){
        validation.equals(Account.count("byEmail",email.toLowerCase()),0).message(Messages.get("register.emailNotAvailable"));
        if (validation.hasErrors()) {
            validation.keep();
            params.flash();
            flash.error("Please correct these errors !");
            signup();
        }

        Account acc = new Account(fullname,email,password);
        acc.website = website;
        acc.save();
        try {
            if (Notifier.welcome(acc)) {
                flash.success("Your account is created. Please check your emails ...");
                login();
            }
        } catch (Exception e) {
            Logger.error(e, "Mail error");
        }
        flash.error("Oops ... (the email cannot be sent)");
        Secure.login();
    }

    public static void confirmRegistration(String uuid) {
        Account account = Account.findByRegistrationUUID(uuid);
        notFoundIfNull(account);
        account.needConfirmation = null;
        account.save();
        flash.success("Welcome %s !", account.fullname);
        Application.index();
    }

    public static void resendConfirmation(String uuid) {
        Account account = Account.findByRegistrationUUID(uuid);
        notFoundIfNull(account);
        try {
            if (Notifier.welcome(account)) {
                flash.success("Please check your emails ...");
                flash.put("email", account.email);
                login();
            }
        } catch (Exception e) {
            Logger.error(e, "Mail error");
        }
        flash.error("Oops (the email cannot be sent)...");
        flash.put("email", account.email);
        login();
    }
}
