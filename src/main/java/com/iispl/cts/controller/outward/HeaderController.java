package com.iispl.cts.controller.outward;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.select.Selectors;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zul.Button;
import org.zkoss.zul.Div;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Popup;

import com.iispl.cts.model.outward.Notification;
import com.iispl.cts.model.outward.UserSession;
import com.iispl.cts.service.outward.NotificationService;

public class HeaderController extends GenericForwardComposer<Component> {

    private static final long serialVersionUID = 1L;

    private static final String LAST_LOGIN =
            "09-09-2026 09:15 AM";

    private static final DateTimeFormatter DATE_TIME_FORMATTER =
            DateTimeFormatter.ofPattern("dd-MM-yyyy hh:mm a");


    @Wire
    private Label headerDate;

    @Wire
    private Label headerUserName;

    @Wire
    private Label headerLastLogin;

    @Wire
    private Label headerNotifBadge;

    @Wire
    private Div notificationButton;

    @Wire
    private Popup notificationPopup;

    @Wire
    private Listbox notificationListbox;

    @Wire
    private Button markAllReadButton;


    private NotificationService notificationService;


    @Override
    public void doAfterCompose(Component component) throws Exception {

        super.doAfterCompose(component);

        /*
         * Wire all components inside headerRoot.
         */
        Selectors.wireComponents(
                component,
                this,
                false
        );


        /*
         * Check logged-in user.
         */
        UserSession sessionUser =
                LoginController.getCurrentUserSession();


        if (sessionUser == null) {

            Executions.sendRedirect(
                    "/outward/common/login.zul"
            );

            return;
        }


        /*
         * Notification service.
         */
        notificationService =
                new NotificationService();


        /*
         * Current date and time.
         */
        headerDate.setValue(
                LocalDateTime.now()
                        .format(DATE_TIME_FORMATTER)
        );


        /*
         * Logged-in username.
         */
        headerUserName.setValue(
                sessionUser.getUsername()
        );


        /*
         * Last login.
         */
        headerLastLogin.setValue(
                "Last Login: " + LAST_LOGIN
        );


        /*
         * Load notifications.
         */
        loadNotifications(
                sessionUser.getUserId()
        );


        /*
         * Notification button.
         */
        notificationButton.addEventListener(
                "onClick",
                event -> {

                    try {

                        openNotifications();

                    } catch (Exception e) {

                        e.printStackTrace();
                    }
                }
        );


        /*
         * Mark all as read.
         */
        markAllReadButton.addEventListener(
                "onClick",
                event -> {

                    try {

                        markAllAsRead();

                    } catch (Exception e) {

                        e.printStackTrace();
                    }
                }
        );
    }


    private void loadNotifications(int userId)
            throws Exception {

        List<Notification> notifications =
                notificationService.getNotifications(userId);


        notificationListbox.getItems().clear();


        for (Notification notification : notifications) {

            Listitem item =
                    new Listitem();


            Listcell cell =
                    new Listcell();


            String message =
                    notification.getMessage();


            if (!notification.isRead()) {

                message =
                        "🔵 " + message;
            }


            cell.setLabel(message);


            item.appendChild(cell);


            item.setAttribute(
                    "notificationId",
                    notification.getNotificationId()
            );


            item.setAttribute(
                    "read",
                    notification.isRead()
            );


            notificationListbox.appendChild(item);
        }


        updateUnreadCount(userId);
    }


    private void updateUnreadCount(int userId)
            throws Exception {

        int unreadCount =
                notificationService.getUnreadCount(userId);


        if (unreadCount > 0) {

            headerNotifBadge.setValue(
                    String.valueOf(unreadCount)
            );

            headerNotifBadge.setVisible(true);

        } else {

            headerNotifBadge.setVisible(false);
        }
    }


    private void openNotifications()
            throws Exception {

        UserSession sessionUser =
                LoginController.getCurrentUserSession();


        if (sessionUser == null) {

            Executions.sendRedirect(
                    "/outward/common/login.zul"
            );

            return;
        }


        loadNotifications(
                sessionUser.getUserId()
        );


        notificationPopup.open(
                notificationButton,
                "after_start"
        );
    }


    private void markAllAsRead()
            throws Exception {

        UserSession sessionUser =
                LoginController.getCurrentUserSession();


        if (sessionUser == null) {

            Executions.sendRedirect(
                    "/outward/common/login.zul"
            );

            return;
        }


        notificationService.markAllAsRead(
                sessionUser.getUserId()
        );


        loadNotifications(
                sessionUser.getUserId()
        );
    }
}