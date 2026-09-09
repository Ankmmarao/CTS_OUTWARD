package com.iispl.cts.controller.outward;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zul.Button;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Textbox;

import com.iispl.cts.model.outward.UserSession;
import com.iispl.cts.service.outward.LoginService;

public class LoginController extends GenericForwardComposer<Component> {

    private static final long serialVersionUID = 1L;

    public static final String SESSION_USER = "CTS_USER_SESSION";

    @Wire
    private Textbox username;

    @Wire
    private Textbox password;

    @Wire
    private Button loginButton;

    private LoginService loginService;


    @Override
    public void doAfterCompose(Component comp) throws Exception {

        super.doAfterCompose(comp);

        loginService = new LoginService();

        /*
         * Check if user is already logged in.
         */
        UserSession existingSession = getCurrentUserSession();

        if (existingSession != null) {
            redirectByRole(existingSession.getRoleId());
        }
    }


    /*
     * Login button event
     *
     * GenericForwardComposer automatically forwards
     * the onClick event from loginButton to this method.
     */
    public void onClick$loginButton() {

        String userNameValue = username.getValue();
        String passwordValue = password.getValue();


        /*
         * Username validation
         */
        if (userNameValue == null
                || userNameValue.trim().isEmpty()) {

            Messagebox.show(
                    "Please enter username.",
                    "Validation",
                    Messagebox.OK,
                    Messagebox.EXCLAMATION
            );

            return;
        }


        /*
         * Password validation
         */
        if (passwordValue == null
                || passwordValue.isEmpty()) {

            Messagebox.show(
                    "Please enter password.",
                    "Validation",
                    Messagebox.OK,
                    Messagebox.EXCLAMATION
            );

            return;
        }


        try {

            /*
             * Authenticate user.
             */
            UserSession userSession =
                    loginService.authenticate(
                            userNameValue.trim(),
                            passwordValue
                    );


            /*
             * Authentication failed.
             */
            if (userSession == null) {

                Messagebox.show(
                        "Invalid username or password, "
                        + "or user is inactive.",
                        "Login Failed",
                        Messagebox.OK,
                        Messagebox.ERROR
                );

                password.setValue("");

                return;
            }


            /*
             * Store authenticated user
             * in ZK session.
             */
            Sessions.getCurrent().setAttribute(
                    SESSION_USER,
                    userSession
            );


            System.out.println(
                    "LOGIN SUCCESS: "
                    + "userId=" + userSession.getUserId()
                    + ", username=" + userSession.getUsername()
                    + ", roleId=" + userSession.getRoleId()
            );


            /*
             * Open common outward shell.
             */
            redirectByRole(
                    userSession.getRoleId()
            );


        } catch (Exception e) {

            e.printStackTrace();

            Messagebox.show(
                    "Unable to login.\n\nError: "
                    + e.getMessage(),
                    "Login Error",
                    Messagebox.OK,
                    Messagebox.ERROR
            );
        }
    }


    /*
     * Role-based access.
     *
     * 3 = Outward Maker
     * 4 = Outward Checker
     * 5 = Capture Operator
     *
     * All three roles enter the same common shell.
     */
    private void redirectByRole(int roleId) {

        switch (roleId) {

            case 3:
                // Outward Maker
                Executions.sendRedirect(
                        "/outward/common/outwardMain.zul"
                );
                break;


            case 4:
                // Outward Checker
                Executions.sendRedirect(
                        "/outward/common/outwardMain.zul"
                );
                break;


            case 5:
                // Capture Operator
                Executions.sendRedirect(
                        "/outward/common/outwardMain.zul"
                );
                break;


            default:

                Sessions.getCurrent()
                        .removeAttribute(SESSION_USER);

                Messagebox.show(
                        "This user role is not enabled "
                        + "for the current application.",
                        "Access Denied",
                        Messagebox.OK,
                        Messagebox.ERROR
                );

                break;
        }
    }


    /*
     * Get logged-in user.
     */
    public static UserSession getCurrentUserSession() {

        Object sessionObject =
                Sessions.getCurrent()
                        .getAttribute(SESSION_USER);

        if (sessionObject instanceof UserSession) {

            return (UserSession) sessionObject;
        }

        return null;
    }


    /*
     * Get logged-in user ID.
     */
    public static int getCurrentUserId() {

        UserSession user = getCurrentUserSession();

        return user == null
                ? 0
                : user.getUserId();
    }


    /*
     * Get logged-in role ID.
     */
    public static int getCurrentRoleId() {

        UserSession user = getCurrentUserSession();

        return user == null
                ? 0
                : user.getRoleId();
    }


    /*
     * Check login status.
     */
    public static boolean isLoggedIn() {

        return getCurrentUserSession() != null;
    }


    /*
     * Logout.
     */
    public static void logout() {

        Sessions.getCurrent()
                .removeAttribute(SESSION_USER);

        Executions.sendRedirect(
                "/outward/common/login.zul"
        );
    }
}