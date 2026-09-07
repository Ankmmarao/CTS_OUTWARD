package com.iispl.cts.controller.outward;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Label;

import com.iispl.cts.model.outward.UserSession;

public class HeaderController
        extends SelectorComposer<org.zkoss.zk.ui.Component> {

    private static final long serialVersionUID = 1L;

    @Wire
    private Label dateLabel;

    @Wire
    private Label lastLoginLabel;

    @Wire
    private Label usernameLabel;

    private static final DateTimeFormatter DATE_FORMAT =
            DateTimeFormatter.ofPattern("dd-MM-yyyy");

    private static final DateTimeFormatter DATE_TIME_FORMAT =
            DateTimeFormatter.ofPattern("dd-MM-yyyy hh:mm a");

    @Override
    public void doAfterCompose(
            org.zkoss.zk.ui.Component comp) throws Exception {

        super.doAfterCompose(comp);

        UserSession sessionUser =
                LoginController.getCurrentUserSession();

        if (sessionUser == null) {

            Executions.sendRedirect("/login.zul");

            return;
        }

        /*
         * Current Date
         */
        LocalDateTime now =
                LocalDateTime.now();

        dateLabel.setValue(
                "▣  Date : "
                + now.format(DATE_FORMAT));

        /*
         * Last Login
         */
        LocalDateTime lastLogin =
                sessionUser.getLastLogin();

        if (lastLogin != null) {

            lastLoginLabel.setValue(
                    "◷  Last Login : "
                    + lastLogin.format(DATE_TIME_FORMAT));

        } else {

            lastLoginLabel.setValue(
                    "◷  Last Login : Not Available");
        }

        /*
         * Dynamic Username
         */
        usernameLabel.setValue(
                "♙  " + sessionUser.getUsername());
    }

    /*
     * Logout button
     */
    @Listen("onClick=#logoutButton")
    public void logout() {

        LoginController.logout();
    }
}