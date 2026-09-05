package com.iispl.cts.controller.outward;

import java.util.ArrayList;
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
    // ZUL COMPONENTS
    // =========================================================

    @Wire
    private Listbox batchListbox;

    @Wire
    private Label pendingDataEntryCount;

    @Wire
    private Label micrRepairCount;

    @Wire
    private Label readyToSubmitCount;


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

        // -----------------------------------------------------
        // CREATE SERVICE
        // -----------------------------------------------------

        service = new OutwardMakerDashboardService();

        System.out.println(
                "Service created successfully."
        );

        // -----------------------------------------------------
        // CHECK LISTBOX
        // -----------------------------------------------------

        System.out.println(
                "batchListbox = "
                + (
                    batchListbox == null
                    ? "NULL"
                    : "FOUND"
                )
        );

        // -----------------------------------------------------
        // LOAD DASHBOARD
        // -----------------------------------------------------

        loadDashboard();

        System.out.println(
                "doAfterCompose() END"
        );

        System.out.println(
                "======================================"
        );
    }


    // =========================================================
    // LOAD COMPLETE DASHBOARD
    // =========================================================

    private void loadDashboard() {

        loadBatches();

        /*
         * Summary counts should come from Service / DAO.
         *
         * Do not put database code here.
         *
         * When count methods are available in the Service,
         * they can be called here:
         *
         * pendingDataEntryCount
         * micrRepairCount
         * readyToSubmitCount
         */
    }


    // =========================================================
    // LOAD BATCHES
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
            // GET BATCHES THROUGH SERVICE
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

                batches = new ArrayList<>();

            } else {

                System.out.println(
                        "Batches returned from service = "
                        + batches.size()
                );
            }

            // -------------------------------------------------
            // PRINT DATABASE VALUES
            // -------------------------------------------------

            for (OutwardBatch batch : batches) {

                if (batch == null) {
                    continue;
                }

                System.out.println(
                        "--------------------------------------"
                );

                System.out.println(
                        "Batch Number       : "
                        + batch.getBatchNumber()
                );

                System.out.println(
                        "Number Of Cheques  : "
                        + batch.getNumberOfCheques()
                );

                System.out.println(
                        "Batch Status       : "
                        + batch.getBatchStatus()
                );

                System.out.println(
                        "Created By         : "
                        + batch.getCreatedBy()
                );

                System.out.println(
                        "Maker User Number  : "
                        + batch.getMakerUserNumber()
                );

                System.out.println(
                        "Checker User Number: "
                        + batch.getCheckerUserNumber()
                );

                System.out.println(
                        "Lock Status        : "
                        + batch.getLockStatus()
                );

                System.out.println(
                        "Locked By          : "
                        + batch.getLockedBy()
                );
            }


            // -------------------------------------------------
            // CREATE MODEL
            // -------------------------------------------------

            ListModelList<OutwardBatch> model =
                    new ListModelList<>();

            model.addAll(batches);


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
                                batch.getBatchNumber()
                        )
                )
        );

        item.appendChild(batchCell);


        // =====================================================
        // NUMBER OF CHEQUES
        // =====================================================

        Listcell totalCell =
                new Listcell();

        totalCell.appendChild(
                new Label(
                        String.valueOf(
                                batch.getNumberOfCheques()
                        )
                )
        );

        item.appendChild(totalCell);


        // =====================================================
        // BATCH STATUS
        // =====================================================

        Listcell statusCell =
                new Listcell();

        statusCell.appendChild(
                new Label(
                        safeValue(
                                batch.getBatchStatus()
                        )
                )
        );

        item.appendChild(statusCell);


        // =====================================================
        // MAKER USER
        // =====================================================

        Listcell makerCell =
                new Listcell();

        String makerUser =
                batch.getMakerUserNumber();

        makerCell.appendChild(
                new Label(
                        safeValue(makerUser)
                )
        );

        item.appendChild(makerCell);


        // =====================================================
        // DETERMINE WHETHER BATCH IS AVAILABLE
        // =====================================================

        String batchStatus =
                batch.getBatchStatus();

        String makerUserNumber =
                batch.getMakerUserNumber();

        String lockStatus =
                batch.getLockStatus();

        String lockedBy =
                batch.getLockedBy();


        boolean hasMakerAssignment =
                makerUserNumber != null
                && !makerUserNumber.trim().isEmpty();


        boolean isLocked =
                (
                    lockStatus != null
                    && (
                        "LOCKED".equalsIgnoreCase(
                                lockStatus.trim()
                        )
                        ||
                        "IN_PROGRESS".equalsIgnoreCase(
                                lockStatus.trim()
                        )
                    )
                )
                ||
                (
                    lockedBy != null
                    && !lockedBy.trim().isEmpty()
                );


        boolean isAvailable =
                "AVAILABLE".equalsIgnoreCase(
                        safeValue(batchStatus)
                )
                && !hasMakerAssignment
                && !isLocked;


        // =====================================================
        // ASSIGNMENT COLUMN
        // =====================================================

        Listcell assignmentCell =
                new Listcell();


        if (hasMakerAssignment) {

            assignmentCell.appendChild(
                    new Label(
                            makerUserNumber
                    )
            );

        } else if (isLocked) {

            String displayLockedBy =
                    lockedBy;

            if (displayLockedBy == null
                    || displayLockedBy.trim().isEmpty()) {

                displayLockedBy = "Locked";
            }

            assignmentCell.appendChild(
                    new Label(
                            displayLockedBy
                    )
            );

        } else {

            assignmentCell.appendChild(
                    new Label("Available")
            );
        }

        item.appendChild(assignmentCell);


        // =====================================================
        // ACTION COLUMN
        // =====================================================

        Listcell actionCell =
                new Listcell();


        // -----------------------------------------------------
        // AVAILABLE
        // -----------------------------------------------------

        if (isAvailable) {

            Button openButton =
                    new Button("Open");

            openButton.setWidth("75px");

            openButton.setHeight("32px");

            openButton.setStyle(
                    "background:#12B76A;"
                    + "color:white;"
                    + "border:none;"
                    + "border-radius:5px;"
                    + "font-weight:bold;"
                    + "cursor:pointer;"
            );


            openButton.addEventListener(
                    Events.ON_CLICK,
                    event ->
                            openAndAssignBatch(
                                    batch.getBatchNumber()
                            )
            );


            actionCell.appendChild(
                    openButton
            );


        }

        // -----------------------------------------------------
        // ASSIGNED / LOCKED
        // -----------------------------------------------------

        else {

            Label lockedLabel =
                    new Label("🔒 Locked");

            lockedLabel.setStyle(
                    "color:#E74C3C;"
                    + "font-weight:bold;"
            );

            actionCell.appendChild(
                    lockedLabel
            );
        }


        item.appendChild(actionCell);
    }


    // =========================================================
    // OPEN + AUTOMATICALLY ASSIGN BATCH
    // =========================================================

    private void openAndAssignBatch(
            String batchNumber) {

        if (batchNumber == null
                || batchNumber.trim().isEmpty()) {

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
                Executions
                        .getCurrent()
                        .getSession();


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
        // GET CURRENT LOGGED-IN USER
        // =====================================================

        Object sessionUser =
                session.getAttribute("userId");


        if (sessionUser == null
                || sessionUser.toString()
                        .trim()
                        .isEmpty()) {

            Messagebox.show(
                    "Logged-in user was not found.",
                    "User",
                    Messagebox.OK,
                    Messagebox.ERROR
            );

            return;
        }


        String userId =
                sessionUser
                        .toString()
                        .trim();


        System.out.println(
                "======================================"
        );

        System.out.println(
                "OPEN BATCH REQUEST"
        );

        System.out.println(
                "Batch : "
                + batchNumber
        );

        System.out.println(
                "User  : "
                + userId
        );

        System.out.println(
                "======================================"
        );


        try {

            // =================================================
            // SERVICE HANDLES ASSIGNMENT
            // =================================================

            OutwardValidationResult result =
                    service.assignAndValidate(
                            batchNumber,
                            userId
                    );


            // =================================================
            // ASSIGNMENT FAILED
            // =================================================

            if (result == null) {

                Messagebox.show(
                        "Batch "
                        + batchNumber
                        + " could not be opened.\n\n"
                        + "It may already be assigned "
                        + "to another Maker.",
                        "Batch Locked",
                        Messagebox.OK,
                        Messagebox.ERROR
                );

                loadBatches();

                return;
            }


            // =================================================
            // RELOAD DASHBOARD
            // =================================================

            loadBatches();


            // =================================================
            // GET VALIDATION COUNTS
            // =================================================

            int dataEntry =
                    result.getDataEntryErrors();

            int micr =
                    result.getMicrErrors();

            int amountAccount =
                    result.getAmountAccountErrors();


            // =================================================
            // DATA ENTRY
            // =================================================

            if (dataEntry > 0) {

                openDataEntry(
                        batchNumber
                );

                return;
            }


            // =================================================
            // MICR REPAIR
            // =================================================

            if (micr > 0) {

                openMicrRepair(
                        batchNumber
                );

                return;
            }


            // =================================================
            // AMOUNT / ACCOUNT
            // =================================================

            if (amountAccount > 0) {

                openAmountAccount(
                        batchNumber
                );

                return;
            }


            // =================================================
            // NO ERRORS
            // =================================================

            Messagebox.show(
                    "Batch "
                    + batchNumber
                    + " is valid and ready for Checker.",
                    "Batch Ready",
                    Messagebox.OK,
                    Messagebox.INFORMATION
            );


        } catch (Exception e) {

            e.printStackTrace();

            Messagebox.show(
                    "Unable to open batch "
                    + batchNumber
                    + ".\n\n"
                    + "Error: "
                    + e.getMessage(),
                    "Open Batch Error",
                    Messagebox.OK,
                    Messagebox.ERROR
            );
        }
    }


    // =========================================================
    // SHOW VALIDATION RESULT
    // =========================================================

    private void showValidationResult(
            String batchNumber,
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
                    + batchNumber
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
                + batchNumber
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
    // OPEN DATA ENTRY
    // =========================================================

    private void openDataEntry(
            String batchNumber) {

        Executions.sendRedirect(
                "/outward-maker-data-entry.zul"
                + "?batchNumber="
                + encode(batchNumber)
        );
    }


    // =========================================================
    // OPEN MICR REPAIR
    // =========================================================

    private void openMicrRepair(
            String batchNumber) {

        Executions.sendRedirect(
                "/outward-maker-micr-repair.zul"
                + "?batchNumber="
                + encode(batchNumber)
        );
    }


    // =========================================================
    // OPEN AMOUNT / ACCOUNT
    // =========================================================

    private void openAmountAccount(
            String batchNumber) {

        Executions.sendRedirect(
                "/outward-maker-amount-account.zul"
                + "?batchNumber="
                + encode(batchNumber)
        );
    }


    // =========================================================
    // FIND BATCH
    // =========================================================

    private OutwardBatch findBatch(
            String batchNumber) {

        if (batchNumber == null
                || batchNumber.trim().isEmpty()) {

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
                        && batchNumber.equals(
                                batch.getBatchNumber()
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