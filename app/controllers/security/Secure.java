package controllers.security;

import java.util.Date;

import models.AccountRole;
import models.Log;
import play.Logger;
import play.Play;
import play.data.validation.Required;
import play.libs.Crypto;
import play.libs.Time;
import play.mvc.Before;
import play.mvc.Controller;
import play.mvc.Http;

public class Secure extends Controller {

    @Before(unless = {"login", "authenticate", "logout"})
    static void checkAccess() throws Throwable {
        // Authenticate
        if (!session.contains("username")) {
            flash.put("url", "GET".equals(request.method) ? request.url
                : Play.ctxPath + "/"); // seems a good default
            login();
        }
        // Checks
        Check check = getActionAnnotation(Check.class);
        if (check != null) {
            check(check);
        }

        check = getControllerInheritedAnnotation(Check.class);
        if (check != null) {
            check(check);
        }
    }

    /**
     * Logic-OR all the roles, and return true if desired role is mentioned in the
     * list.
     *
     * @param check
     * @throws Throwable
     */
    private static void check(Check check) throws Throwable {
        boolean roleExists = false;
        for (AccountRole role : check.value()) {
            boolean hasRole = (Boolean) Security.invoke("check", role);
            roleExists = roleExists | hasRole;
            if (roleExists)
                return;
        }
        Security.invoke("onCheckFailed");
    }

    // ~~~ Login
    public static void login() {
        // TODO REMOVE Store a log. User event module instead.
        Log log = new Log(Log.LogType.MISC, "secure.login",
            "Secure.login requested.");
        log.actorIp = request.remoteAddress;
        log.timestamp = request.date;
        log.save();

        try {
            Http.Cookie remember = request.cookies.get("rememberme");
            if (remember != null) {
                int firstIndex = remember.value.indexOf("-");
                int lastIndex = remember.value.lastIndexOf("-");
                if (lastIndex > firstIndex) {
                    String sign = remember.value.substring(0, firstIndex);
                    String restOfCookie = remember.value.substring(firstIndex + 1);
                    String username = remember.value.substring(firstIndex + 1, lastIndex);
                    String time = remember.value.substring(lastIndex + 1);
                    Date expirationDate = new Date(Long.parseLong(time)); // surround with
                    // try/catch?
                    Date now = new Date();
                    if (expirationDate == null || expirationDate.before(now)) {
                        logout();
                    }
                    if (Crypto.sign(restOfCookie).equals(sign)) {
                        session.put("username", username);
                        redirectToOriginalURL();
                    }
                }
            }
        } catch (Throwable t) {
            Logger.error(t, "login");
        }
        flash.keep("url");
        renderTemplate("Security/login.html");
    }

    public static void authenticate(@Required String username, String password, boolean remember) throws Throwable {
        // Check tokens
        Boolean allowed = (Boolean) Security.invoke("authenticate", username,
            password);
        if (validation.hasErrors() || !allowed) {
            flash.keep("url");
            flash.error("security.LoginError");
            params.flash();
            login();
        }
        // Mark user as connected
        session.put("username", username);
        // Remember if needed
        if (remember) {
            Date expiration = new Date();
            String duration = Play.configuration.getProperty(
                "secure.rememberme.duration", "30d");
            expiration.setTime(expiration.getTime() + Time.parseDuration(duration)
                * 1000);
            response.setCookie("rememberme",
                Crypto.sign(username + "-" + expiration.getTime()) + "-" + username
                    + "-" + expiration.getTime(), duration);

        }
        // Redirect to the original URL (or /)
        redirectToOriginalURL();
    }

    public static void logout() throws Throwable {
        Security.invoke("onDisconnect");
        session.clear();
        response.removeCookie("rememberme");
        Security.invoke("onDisconnected");
        flash.success("security.SignedOut");
        login();
    }

    // ~~~ Utils

    static void redirectToOriginalURL() throws Throwable {
        Security.invoke("onAuthenticated");
        String url = flash.get("url");
        if (url == null) {
            url = Play.ctxPath + "/";
        }
        redirect(url);
    }

}
