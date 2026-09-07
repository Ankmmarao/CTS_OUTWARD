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
import com.iispl.cts.model.outward.OutwardCheque;
import com.iispl.cts.model.outward.UserSession;
import com.iispl.cts.service.outward.checker.CheckerBatchService;

public class CheckerBatchesQueueController
        extends SelectorComposer<Component> {

    private static final long serialVersionUID = 1L;

    // ============================================================
    // ZUL COMPONENT
    // ============================================================

    @Wire
    private Listbox queueListbox;

    // ============================================================
    // SERVICE
    // ============================================================

    private CheckerBatchService service;

    // ============================================================
    // CURRENT LOGGED-IN CHECKER
    // ============================================================

    private String currentCheckerUser;

    // ============================================================
    // PAGE LOAD
    // ============================================================

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

        // ========================================================
        // GET CURRENT LOGGED-IN USER
        // ========================================================

        UserSession sessionUser =
                LoginController.getCurrentUserSession();

        // No session
        if (sessionUser == null) {

            Executions.sendRedirect(
                    "/login.zul"
            );

            return;
        }

        // ========================================================
        // CHECK ROLE
        // Role ID 4 = Outward Checker
        // ========================================================

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

        // ========================================================
        // GET CURRENT CHECKER USER ID
        // ========================================================

        currentCheckerUser =
                String.valueOf(
                        sessionUser.getUserId()
                );

        System.out.println(
                "CURRENT CHECKER USER ID = "
                        + currentCheckerUser
        );

        // ========================================================
        // CREATE SERVICE
        // ========================================================

        service =
                new CheckerBatchService();

        // ========================================================
        // LOAD BATCHES
        // ========================================================

        loadBatches();
    }

    // ============================================================
    // LOAD BATCHES
    // ============================================================

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

            // ====================================================
            // GET BATCHES LOCKED BY THIS CHECKER
            // ====================================================

            List<OutwardBatch> batches =
                    service.getCheckerQueueBatches(
                            currentCheckerUser
                    );

            // Clear existing rows
            queueListbox.getItems().clear();

            System.out.println(
                    "TOTAL BATCHES FOUND = "
                            + batches.size()
            );

            // ====================================================
            // NO BATCHES
            // ====================================================

            if (batches.isEmpty()) {

                System.out.println(
                        "NO LOCKED BATCHES FOUND FOR CHECKER "
                                + currentCheckerUser
                );

                return;
            }

            // ====================================================
            // CREATE ROW FOR EVERY BATCH
            // ====================================================

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

    // ============================================================
    // CREATE TABLE ROW
    // ============================================================

    private void createBatchRow(
            final OutwardBatch batch) {

        // ========================================================
        // CREATE ROW
        // ========================================================

        Listitem item =
                new Listitem();

        // ========================================================
        // BATCH NUMBER
        // ========================================================

        Listcell batchNumberCell =
                new Listcell();

        batchNumberCell.setLabel(
                batch.getBatchNumber()
        );

        item.appendChild(
                batchNumberCell
        );

        // ========================================================
        // TOTAL CHEQUES
        // ========================================================

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

        // ========================================================
        // STATUS
        // ========================================================

        Listcell statusCell =
                new Listcell();

        statusCell.setLabel(
                "Locked by Checker"
        );

        item.appendChild(
                statusCell
        );

        // ========================================================
        // ACTION
        // ========================================================

        Listcell actionCell =
                new Listcell();

        Button openButton =
                new Button("Open");

        openButton.setSclass(
                "btn btn-primary"
        );

        // ========================================================
        // OPEN BUTTON CLICK
        // ========================================================

        openButton.addEventListener(

                Events.ON_CLICK,

                new EventListener<Event>() {

                    @Override
                    public void onEvent(Event event)
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

        // ========================================================
        // ADD ROW TO LISTBOX
        // ========================================================

        queueListbox.appendChild(
                item
        );

        System.out.println(
                "DISPLAYED BATCH = "
                        + batch.getBatchNumber()
        );
    }

    // ============================================================
    // OPEN BATCH
    // ============================================================

    private void openBatch(
            String batchNumber) {

        // ========================================================
        // VALIDATE BATCH NUMBER
        // ========================================================

        if (batchNumber == null
                || batchNumber.trim().isEmpty()) {

            return;
        }

        System.out.println(
                "OPENING BATCH = "
                        + batchNumber
        );

        try {

            // ====================================================
            // GET CHEQUES FOR THIS BATCH
            // ====================================================

            List<OutwardCheque> cheques =
                    service.getChequesByBatchNumber(
                            batchNumber
                    );

            // ====================================================
            // CHECK WHETHER CHEQUES EXIST
            // ====================================================

            if (cheques == null
                    || cheques.isEmpty()) {

                Messagebox.show(
                        "No cheques found in this batch.",
                        "No Cheques",
                        Messagebox.OK,
                        Messagebox.INFORMATION
                );

                return;
            }

            // ====================================================
            // GET FIRST CHEQUE
            //
            // We directly open the Cheque Verification page.
            // ====================================================

            OutwardCheque firstCheque =
                    cheques.get(0);

            String chequeNumber =
                    firstCheque.getChequeNumber();

            // ====================================================
            // BUILD URL
            // ====================================================

            String url =
                    "/outward/checker/chequeVerification.zul"
                            + "?batchId="
                            + Executions.encodeURL(
                                    batchNumber
                            )
                            + "&chequeNumber="
                            + Executions.encodeURL(
                                    chequeNumber
                            );

            System.out.println(
                    "OPENING CHEQUE VERIFICATION URL = "
                            + url
            );

            // ====================================================
            // REDIRECT DIRECTLY TO CHEQUE VERIFICATION
            // ====================================================

            Executions.sendRedirect(
                    url
            );

        } catch (Exception e) {

            e.printStackTrace();

            Messagebox.show(
                    "Unable to open batch.",
                    "Error",
                    Messagebox.OK,
                    Messagebox.ERROR
            );
        }
    }
}