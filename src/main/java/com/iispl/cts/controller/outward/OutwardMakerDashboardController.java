
package com.iispl.cts.controller.outward;

import java.util.List;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Session;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Button;
import org.zkoss.zul.Label;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;
import org.zkoss.zul.Messagebox;

import com.iispl.cts.model.outward.OutwardBatch;
import com.iispl.cts.model.outward.OutwardValidationResult;
import com.iispl.cts.service.outward.OutwardMakerDashboardService;

public class OutwardMakerDashboardController
        extends SelectorComposer<Component> {

    private static final long serialVersionUID = 1L;

    // =========================================================
    // ZUL COMPONENT
    // =========================================================

    @Wire
    private Listbox batchListbox;

    // =========================================================
    // SERVICE
    // =========================================================

    private OutwardMakerDashboardService service;

    // =========================================================
    // AFTER COMPOSE
    // =========================================================

    @Override
    public void doAfterCompose(Component comp) throws Exception {

        super.doAfterCompose(comp);

        System.out.println("======================================");
        System.out.println("OUTWARD MAKER DASHBOARD CONTROLLER");
        System.out.println("doAfterCompose() START");
        System.out.println("======================================");

        // =====================================================
        // TEMPORARY TEST USER
        // =====================================================
        //
        // You currently do not have a Login Controller.
        // Therefore no userId is being stored in the session.
        //
        // This creates a temporary session user so that you can
        // test Assign to Me and Open functionality.
        //
        // IMPORTANT:
        // Change "maker001" to your actual Maker user ID.
        //
        // Remove this section when real login is implemented.
        // =====================================================

        Session session =
                Executions.getCurrent().getSession();

        if (session != null) {

            Object existingUser =
                    session.getAttribute("userId");

            if (existingUser == null
                    || existingUser.toString().trim().isEmpty()) {

                session.setAttribute(
                        "userId",
                        "maker001"
                );

                System.out.println(
                        "Temporary test user created: maker001"
                );

            } else {

                System.out.println(
                        "Existing session user: "
                        + existingUser
                );
            }

        } else {

            System.out.println(
                    "WARNING: ZK session is NULL."
            );
        }

        // =====================================================
        // CREATE SERVICE
        // =====================================================

        service = new OutwardMakerDashboardService();

        System.out.println(
                "Service created successfully."
        );

        System.out.println(
                "batchListbox = "
                + (
                    batchListbox == null
                    ? "NULL"
                    : "FOUND"
                  )
        );

        // =====================================================
        // LOAD DATABASE BATCHES
        // =====================================================

        loadBatches();

        System.out.println("doAfterCompose() END");
        System.out.println("======================================");
    }

    // =========================================================
    // LOAD BATCHES FROM DATABASE
    // =========================================================

    private void loadBatches() {

        System.out.println();
        System.out.println("======================================");
        System.out.println("LOAD BATCHES START");
        System.out.println("======================================");

        // -----------------------------------------------------
        // CHECK LISTBOX
        // -----------------------------------------------------

        if (batchListbox == null) {

            System.out.println(
                    "ERROR: batchListbox is NULL."
            );

            System.out.println(
                    "Check id=\"batchListbox\" in ZUL."
            );

            return;
        }

        // -----------------------------------------------------
        // CHECK SERVICE
        // -----------------------------------------------------

        if (service == null) {

            System.out.println(
                    "ERROR: service is NULL."
            );

            return;
        }

        try {

            // -------------------------------------------------
            // GET BATCHES
            // -------------------------------------------------

            System.out.println(
                    "Calling service.getBatches()..."
            );

            List<OutwardBatch> batches =
                    service.getBatches();

            // -------------------------------------------------
            // CHECK RESULT
            // -------------------------------------------------

            if (batches == null) {

                System.out.println(
                        "ERROR: service.getBatches() returned NULL."
                );

                batches =
                        new java.util.ArrayList<>();

            } else {

                System.out.println(
                        "Batches returned from service = "
                        + batches.size()
                );
            }

            // -------------------------------------------------
            // PRINT EVERY BATCH
            // -------------------------------------------------

            if (batches.isEmpty()) {

                System.out.println(
                        "WARNING: No batches returned."
                );

                System.out.println(
                        "Expected sample batches: B001, B002, B003"
                );

            } else {

                System.out.println(
                        "========== DATABASE BATCHES =========="
                );

                for (OutwardBatch batch : batches) {

                    if (batch == null) {

                        System.out.println(
                                "WARNING: NULL batch found."
                        );

                        continue;
                    }

                    System.out.println(
                            "Batch ID       : "
                            + batch.getBatchId()
                    );

                    System.out.println(
                            "Total Cheques  : "
                            + batch.getTotalCheques()
                    );

                    System.out.println(
                            "Status         : "
                            + batch.getStatus()
                    );

                    System.out.println(
                            "User ID        : "
                            + batch.getUserId()
                    );

                    System.out.println(
                            "Assignment     : "
                            + batch.getAssignment()
                    );

                    System.out.println(
                            "Data Entry Err : "
                            + batch.getDataEntryErrorCount()
                    );

                    System.out.println(
                            "MICR Err       : "
                            + batch.getMicrErrorCount()
                    );

                    System.out.println(
                            "Amount/Account : "
                            + batch.getAmountAccountErrorCount()
                    );

                    System.out.println(
                            "--------------------------------------"
                    );
                }

                System.out.println(
                        "======================================"
                );
            }

            // -------------------------------------------------
            // CREATE MODEL
            // -------------------------------------------------

            ListModelList<OutwardBatch> model =
                    new ListModelList<>();

            model.addAll(batches);

            System.out.println(
                    "ListModelList size = "
                    + model.getSize()
            );

            // -------------------------------------------------
            // SET RENDERER
            // -------------------------------------------------

            batchListbox.setItemRenderer(
                    new ListitemRenderer<OutwardBatch>() {

                        @Override
                        public void render(
                                Listitem item,
                                OutwardBatch batch,
                                int index)
                                throws Exception {

                            System.out.println(
                                    "Rendering row: "
                                    + index
                                    + " | Batch: "
                                    + (
                                        batch == null
                                        ? "NULL"
                                        : batch.getBatchId()
                                      )
                            );

                            renderBatchRow(
                                    item,
                                    batch
                            );
                        }
                    }
            );

            // -------------------------------------------------
            // SET MODEL
            // -------------------------------------------------

            batchListbox.setModel(model);

            System.out.println(
                    "Listbox model successfully assigned."
            );

            System.out.println(
                    "Final model size = "
                    + model.getSize()
            );

            System.out.println(
                    "LOAD BATCHES FINISHED SUCCESSFULLY"
            );

        } catch (Exception e) {

            System.out.println(
                    "ERROR INSIDE loadBatches()"
            );

            e.printStackTrace();

            Messagebox.show(
                    "Unable to load batches from database.\n\n"
                    + "Error: "
                    + e.getMessage(),
                    "Dashboard Error",
                    Messagebox.OK,
                    Messagebox.ERROR
            );
        }

        System.out.println(
                "======================================"
        );

        System.out.println(
                "LOAD BATCHES END"
        );

        System.out.println(
                "======================================"
        );
    }

    // =========================================================
    // RENDER ONE BATCH ROW
    // =========================================================

    private void renderBatchRow(
            Listitem item,
            OutwardBatch batch) {

        if (batch == null) {
            return;
        }

        // =====================================================
        // BATCH NUMBER
        // =====================================================

        Listcell batchCell =
                new Listcell();

        batchCell.appendChild(
                new Label(
                        safeValue(
                                batch.getBatchId()
                        )
                )
        );

        item.appendChild(batchCell);

        // =====================================================
        // TOTAL CHEQUES
        // =====================================================

        Listcell totalCell =
                new Listcell();

        totalCell.appendChild(
                new Label(
                        String.valueOf(
                                batch.getTotalCheques()
                        )
                )
        );

        item.appendChild(totalCell);

        // =====================================================
        // ERROR SUMMARY
        // =====================================================

        Listcell errorCell =
                new Listcell();

        String errorSummary =
                "Data Entry: "
                + batch.getDataEntryErrorCount()
                + " | MICR: "
                + batch.getMicrErrorCount()
                + " | Amount/Account: "
                + batch.getAmountAccountErrorCount();

        errorCell.appendChild(
                new Label(errorSummary)
        );

        item.appendChild(errorCell);

        // =====================================================
        // STATUS
        // =====================================================

        Listcell statusCell =
                new Listcell();

        statusCell.appendChild(
                new Label(
                        safeValue(
                                batch.getStatus()
                        )
                )
        );

        item.appendChild(statusCell);

        // =====================================================
        // USER
        // =====================================================

        Listcell userCell =
                new Listcell();

        userCell.appendChild(
                new Label(
                        safeValue(
                                batch.getUserId()
                        )
                )
        );

        item.appendChild(userCell);

        // =====================================================
        // ASSIGNMENT
        // =====================================================

        Listcell assignmentCell =
                new Listcell();

        String assignment =
                batch.getAssignment();

        boolean available =
                "AVAILABLE".equalsIgnoreCase(
                        safeValue(
                                batch.getStatus()
                        )
                )
                &&
                (
                    assignment == null
                    || assignment.trim().isEmpty()
                    || "AVAILABLE".equalsIgnoreCase(
                            assignment
                       )
                );

        if (available) {

            Button assignButton =
                    new Button("Assign to Me");

            assignButton.setWidth("110px");

            assignButton.addEventListener(
                    Events.ON_CLICK,
                    event ->
                            assignAndValidate(
                                    batch.getBatchId()
                            )
            );

            assignmentCell.appendChild(
                    assignButton
            );

        } else {

            assignmentCell.appendChild(
                    new Label(
                            safeValue(
                                    assignment
                            )
                    )
            );
        }

        item.appendChild(assignmentCell);

        // =====================================================
        // ACTION
        // =====================================================

        Listcell actionCell =
                new Listcell();

        Button openButton =
                new Button("Open");

        openButton.setWidth("70px");

        if (available) {

            openButton.setDisabled(true);

        } else {

            openButton.addEventListener(
                    Events.ON_CLICK,
                    event ->
                            openAssignedBatch(
                                    batch.getBatchId()
                            )
            );
        }

        actionCell.appendChild(
                openButton
        );

        item.appendChild(actionCell);
    }

    // =========================================================
    // ASSIGN BATCH
    // =========================================================

    private void assignAndValidate(
            String batchId) {

        if (batchId == null
                || batchId.trim().isEmpty()) {

            Messagebox.show(
                    "Invalid batch number.",
                    "Batch",
                    Messagebox.OK,
                    Messagebox.ERROR
            );

            return;
        }

        // =====================================================
        // GET SESSION
        // =====================================================

        Session session =
                Executions.getCurrent().getSession();

        if (session == null) {

            Messagebox.show(
                    "Session expired. Please login again.",
                    "Session",
                    Messagebox.OK,
                    Messagebox.ERROR
            );

            return;
        }

        // =====================================================
        // GET LOGGED-IN USER
        // =====================================================

        Object sessionUser =
                session.getAttribute("userId");

        if (sessionUser == null) {

            Messagebox.show(
                    "Logged-in user was not found in session.",
                    "User",
                    Messagebox.OK,
                    Messagebox.ERROR
            );

            return;
        }

        String userId =
                sessionUser.toString().trim();

        if (userId.isEmpty()) {

            Messagebox.show(
                    "Invalid logged-in user.",
                    "User",
                    Messagebox.OK,
                    Messagebox.ERROR
            );

            return;
        }

        System.out.println(
                "Assigning batch "
                + batchId
                + " to user "
                + userId
        );

        // =====================================================
        // ASSIGN + VALIDATE
        // =====================================================

        try {

            OutwardValidationResult result =
                    service.assignAndValidate(
                            batchId,
                            userId
                    );

            // Reload latest database values
            loadBatches();

            if (result == null
                    || result.getTotalCheques() == 0) {

                Messagebox.show(
                        "Batch "
                        + batchId
                        + " could not be assigned.\n\n"
                        + "It may already be assigned "
                        + "to another Maker.",
                        "Assignment Failed",
                        Messagebox.OK,
                        Messagebox.ERROR
                );

                return;
            }

            showValidationResult(
                    batchId,
                    result
            );

        } catch (Exception e) {

            e.printStackTrace();

            Messagebox.show(
                    "Unable to assign batch "
                    + batchId
                    + ".\n\n"
                    + "Error: "
                    + e.getMessage(),
                    "Assignment Error",
                    Messagebox.OK,
                    Messagebox.ERROR
            );
        }
    }

    // =========================================================
    // SHOW VALIDATION RESULT
    // =========================================================

    private void showValidationResult(
            String batchId,
            OutwardValidationResult result) {

        if (result == null) {
            return;
        }

        int dataEntry =
                result.getDataEntryErrors();

        int micr =
                result.getMicrErrors();

        int amountAccount =
                result.getAmountAccountErrors();

        int totalErrors =
                dataEntry
                + micr
                + amountAccount;

        // =====================================================
        // NO ERRORS
        // =====================================================

        if (totalErrors == 0) {

            Messagebox.show(
                    "Batch "
                    + batchId
                    + " has no validation errors.\n\n"
                    + "The batch is ready for Checker.",
                    "Validation Successful",
                    Messagebox.OK,
                    Messagebox.INFORMATION
            );

            return;
        }

        // =====================================================
        // ERRORS FOUND
        // =====================================================

        String message =
                "Batch "
                + batchId
                + " validation completed.\n\n"

                + "Total Cheques: "
                + result.getTotalCheques()

                + "\n\n"

                + "Data Entry Errors: "
                + dataEntry

                + "\n"

                + "MICR Errors: "
                + micr

                + "\n"

                + "Amount / Account Errors: "
                + amountAccount;

        Messagebox.show(
                message,
                "Validation Required",
                Messagebox.OK,
                Messagebox.EXCLAMATION
        );
    }

    // =========================================================
    // OPEN ASSIGNED BATCH
    // =========================================================

    private void openAssignedBatch(
            String batchId) {

        if (batchId == null
                || batchId.trim().isEmpty()) {

            return;
        }

        // =====================================================
        // FIND BATCH
        // =====================================================

        OutwardBatch batch =
                findBatch(batchId);

        if (batch == null) {

            Messagebox.show(
                    "Batch "
                    + batchId
                    + " was not found.",
                    "Batch",
                    Messagebox.OK,
                    Messagebox.ERROR
            );

            return;
        }

        // =====================================================
        // GET SESSION
        // =====================================================

        Session session =
                Executions.getCurrent().getSession();

        if (session == null) {

            Messagebox.show(
                    "Session expired. Please login again.",
                    "Session",
                    Messagebox.OK,
                    Messagebox.ERROR
            );

            return;
        }

        // =====================================================
        // GET USER
        // =====================================================

        Object sessionUser =
                session.getAttribute("userId");

        if (sessionUser == null) {

            Messagebox.show(
                    "Logged-in user was not found in session.",
                    "User",
                    Messagebox.OK,
                    Messagebox.ERROR
            );

            return;
        }

        String loggedInUser =
                sessionUser.toString().trim();

        String assignedUser =
                batch.getUserId();

        // =====================================================
        // CHECK ASSIGNMENT
        // =====================================================

        if (assignedUser == null
                || assignedUser.trim().isEmpty()
                || !loggedInUser.equals(
                        assignedUser
                   )) {

            Messagebox.show(
                    "This batch is assigned to another Maker.",
                    "Access Denied",
                    Messagebox.OK,
                    Messagebox.ERROR
            );

            return;
        }

        // =====================================================
        // REVALIDATE ACTUAL DATABASE VALUES
        // =====================================================

        try {

            OutwardValidationResult result =
                    service.validateBatch(batchId);

            if (result == null) {
                return;
            }

            int dataEntry =
                    result.getDataEntryErrors();

            int micr =
                    result.getMicrErrors();

            int amountAccount =
                    result.getAmountAccountErrors();

            // =================================================
            // DATA ENTRY FIRST
            // =================================================

            if (dataEntry > 0) {

                openDataEntry(batchId);

                return;
            }

            // =================================================
            // MICR SECOND
            // =================================================

            if (micr > 0) {

                openMicrRepair(batchId);

                return;
            }

            // =================================================
            // AMOUNT / ACCOUNT THIRD
            // =================================================

            if (amountAccount > 0) {

                openAmountAccount(batchId);

                return;
            }

            // =================================================
            // NO ERRORS
            // =================================================

            Messagebox.show(
                    "All cheques in batch "
                    + batchId
                    + " are valid.\n\n"
                    + "Batch is READY_FOR_CHECKER.",
                    "Batch Ready",
                    Messagebox.OK,
                    Messagebox.INFORMATION
            );

        } catch (Exception e) {

            e.printStackTrace();

            Messagebox.show(
                    "Unable to validate batch "
                    + batchId
                    + ".\n\n"
                    + "Error: "
                    + e.getMessage(),
                    "Validation Error",
                    Messagebox.OK,
                    Messagebox.ERROR
            );
        }
    }

    // =========================================================
    // OPEN DATA ENTRY SCREEN
    // =========================================================

    private void openDataEntry(
            String batchId) {

        Executions.sendRedirect(
                "/outward-maker-data-entry.zul"
                + "?batchId="
                + encode(batchId)
        );
    }

    // =========================================================
    // OPEN MICR REPAIR SCREEN
    // =========================================================

    private void openMicrRepair(
            String batchId) {

        Executions.sendRedirect(
                "/outward-maker-micr-repair.zul"
                + "?batchId="
                + encode(batchId)
        );
    }

    // =========================================================
    // OPEN AMOUNT / ACCOUNT SCREEN
    // =========================================================

    private void openAmountAccount(
            String batchId) {

        Executions.sendRedirect(
                "/outward-maker-amount-account.zul"
                + "?batchId="
                + encode(batchId)
        );
    }

    // =========================================================
    // FIND BATCH
    // =========================================================

    private OutwardBatch findBatch(
            String batchId) {

        if (batchId == null
                || batchId.trim().isEmpty()) {

            return null;
        }

        try {

            List<OutwardBatch> batches =
                    service.getBatches();

            if (batches == null) {
                return null;
            }

            for (OutwardBatch batch : batches) {

                if (batch != null
                        && batchId.equals(
                                batch.getBatchId()
                           )) {

                    return batch;
                }
            }

        } catch (Exception e) {

            e.printStackTrace();
        }

        return null;
    }

    // =========================================================
    // SAFE STRING
    // =========================================================

    private String safeValue(
            String value) {

        if (value == null
                || value.trim().isEmpty()) {

            return "-";
        }

        return value;
    }

    // =========================================================
    // URL ENCODING
    // =========================================================

    private String encode(
            String value) {

        try {

            return java.net.URLEncoder.encode(
                    value,
                    java.nio.charset.StandardCharsets.UTF_8
            );

        } catch (Exception e) {

            return value;
        }
    }
}

