package com.iispl.cts.controller.outward.checker;

import java.util.List;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Button;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Messagebox;

import com.iispl.cts.controller.outward.LoginController;
import com.iispl.cts.model.outward.OutwardBatch;
import com.iispl.cts.model.outward.UserSession;
import com.iispl.cts.service.outward.checker.CheckerBatchService;

public class CheckerBatchesQueueController
        extends SelectorComposer<Component> {

    private static final long serialVersionUID = 1L;


    /*
     * ============================================================
     * ZUL COMPONENT
     * ============================================================
     */

    @Wire
    private Listbox queueListbox;


    /*
     * ============================================================
     * SERVICE
     * ============================================================
     */

    private CheckerBatchService service;


    /*
     * ============================================================
     * CURRENT LOGGED-IN CHECKER
     * ============================================================
     */

    private String currentCheckerUser;


    /*
     * ============================================================
     * PAGE LOAD
     * ============================================================
     */

    @Override
    public void doAfterCompose(Component comp)
            throws Exception {

        super.doAfterCompose(comp);

        System.out.println();
        System.out.println(
                "=========================================="
        );
        System.out.println(
                "CHECKER BATCHES QUEUE CONTROLLER STARTED"
        );
        System.out.println(
                "=========================================="
        );


        /*
         * --------------------------------------------------------
         * GET CURRENT LOGGED-IN USER
         * --------------------------------------------------------
         */

        UserSession sessionUser =
                LoginController.getCurrentUserSession();


        /*
         * No session
         */

        if (sessionUser == null) {

            Executions.sendRedirect(
                    "/login.zul"
            );

            return;
        }


        /*
         * Check role.
         *
         * Role ID 4 = Outward Checker
         */

        if (sessionUser.getRoleId() != 4) {

            Messagebox.show(
                    "Access denied. Outward Checker access is required.",
                    "Access Denied",
                    Messagebox.OK,
                    Messagebox.ERROR
            );

            Executions.sendRedirect(
                    "/login.zul"
            );

            return;
        }


        /*
         * Get actual logged-in Checker ID.
         *
         * Example:
         *
         * userId = 104
         */

        currentCheckerUser =
                String.valueOf(
                        sessionUser.getUserId()
                );


        System.out.println(
                "CURRENT CHECKER USER ID = "
                + currentCheckerUser
        );


        /*
         * Create Service
         */

        service =
                new CheckerBatchService();


        /*
         * Load batches locked by this Checker
         */

        loadBatches();
    }


    /*
     * ============================================================
     * LOAD BATCHES
     * ============================================================
     */

    private void loadBatches() {

        try {

            System.out.println();
            System.out.println(
                    "=========================================="
            );
            System.out.println(
                    "LOADING BATCHES LOCKED BY CHECKER"
            );
            System.out.println(
                    "Checker User ID = "
                    + currentCheckerUser
            );
            System.out.println(
                    "=========================================="
            );


            /*
             * Get only batches locked by this Checker.
             */

            List<OutwardBatch> batches =
                    service.getCheckerQueueBatches(
                            currentCheckerUser
                    );


            /*
             * Clear old rows
             */

            queueListbox.getItems().clear();


            /*
             * Print number of batches
             */

            System.out.println(
                    "TOTAL BATCHES FOUND = "
                    + batches.size()
            );


            /*
             * If no batches
             */

            if (batches.isEmpty()) {

                System.out.println(
                        "NO LOCKED BATCHES FOUND FOR CHECKER "
                        + currentCheckerUser
                );

                return;
            }


            /*
             * Create table rows
             */

            for (OutwardBatch batch : batches) {

                createBatchRow(batch);
            }


            System.out.println(
                    "TOTAL BATCHES DISPLAYED = "
                    + batches.size()
            );

        } catch (Exception e) {

            e.printStackTrace();

            Clients.showNotification(
                    "Unable to load Batches Queue.",
                    Clients.NOTIFICATION_TYPE_ERROR,
                    null,
                    "top_center",
                    4000
            );
        }
    }


    /*
     * ============================================================
     * CREATE TABLE ROW
     * ============================================================
     */

    private void createBatchRow(
            final OutwardBatch batch) {


        /*
         * Create row
         */

        Listitem item =
                new Listitem();


        /*
         * --------------------------------------------------------
         * BATCH NUMBER
         * --------------------------------------------------------
         */

        Listcell batchNumberCell =
                new Listcell();

        batchNumberCell.setLabel(
                batch.getBatchNumber()
        );

        item.appendChild(
                batchNumberCell
        );


        /*
         * --------------------------------------------------------
         * TOTAL CHEQUES
         * --------------------------------------------------------
         */

        Listcell chequeCell =
                new Listcell();

        chequeCell.setLabel(
                String.valueOf(
                        batch.getNumberOfCheques()
                )
        );

        item.appendChild(
                chequeCell
        );


        /*
         * --------------------------------------------------------
         * STATUS
         * --------------------------------------------------------
         *
         * Always display:
         *
         * Locked by Checker
         */

        Listcell statusCell =
                new Listcell();

        statusCell.setLabel(
                "Locked by Checker"
        );

        item.appendChild(
                statusCell
        );


        /*
         * --------------------------------------------------------
         * ACTION
         * --------------------------------------------------------
         */

        Listcell actionCell =
                new Listcell();


        Button openButton =
                new Button("Open");


        openButton.setSclass(
                "btn btn-primary"
        );


        /*
         * Open selected batch
         */

        openButton.addEventListener(

                Events.ON_CLICK,

                new EventListener<Event>() {

                    @Override
                    public void onEvent(
                            Event event)
                            throws Exception {

                        openBatch(
                                batch.getBatchNumber()
                        );
                    }
                }
        );


        actionCell.appendChild(
                openButton
        );

        item.appendChild(
                actionCell
        );


        /*
         * Add row to Listbox
         */

        queueListbox.appendChild(
                item
        );


        System.out.println(
                "DISPLAYED BATCH = "
                + batch.getBatchNumber()
        );
    }


    /*
     * ============================================================
     * OPEN BATCH
     * ============================================================
     */

    private void openBatch(
            String batchNumber) {

        if (batchNumber == null
                || batchNumber.trim().isEmpty()) {

            return;
        }


        System.out.println(
                "OPENING BATCH = "
                + batchNumber
        );


        /*
         * Go to Batch Verification page.
         */

        Executions.sendRedirect(

                "/outward/checker/batchVerification.zul"
                + "?batchNumber="
                + Executions.encodeURL(
                        batchNumber
                )
        );
    }
}