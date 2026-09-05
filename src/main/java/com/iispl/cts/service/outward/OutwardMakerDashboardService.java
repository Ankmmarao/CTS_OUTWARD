package com.iispl.cts.service.outward;

import java.util.Collections;
import java.util.List;

import com.iispl.cts.dao.outward.OutwardMakerDashboardDAO;
import com.iispl.cts.model.outward.OutwardBatch;
import com.iispl.cts.model.outward.OutwardCheque;
import com.iispl.cts.model.outward.OutwardValidationResult;

/**
 * =========================================================
 * OUTWARD MAKER DASHBOARD SERVICE
 * =========================================================
 *
 * Architecture:
 *
 * ZUL
 *   ↓
 * Controller
 *   ↓
 * Service
 *   ↓
 * DAO
 *   ↓
 * Database
 *
 * Responsibilities:
 *
 * 1. Business logic for Maker dashboard
 * 2. Get batches
 * 3. Get cheques
 * 4. Assign batch to Maker
 * 5. Validate batch
 * 6. Move valid batch forward
 *
 * IMPORTANT:
 *
 * - No SQL in this class
 * - No JDBC code in this class
 * - All database operations go through DAO
 *
 */
public class OutwardMakerDashboardService {

    // =========================================================
    // DAO
    // =========================================================

    private final OutwardMakerDashboardDAO dao;

    // =========================================================
    // VALIDATION SERVICE
    // =========================================================

    private final OutwardValidationService validationService;

    // =========================================================
    // CONSTRUCTOR
    // =========================================================

    public OutwardMakerDashboardService() {

        dao = new OutwardMakerDashboardDAO();

        validationService =
                new OutwardValidationService();
    }

    // =========================================================
    // GET MAKER DASHBOARD BATCHES
    // =========================================================
    //
    // Controller calls:
    //
    //     service.getBatches()
    //
    // Service calls:
    //
    //     dao.getBatches()
    //
    // =========================================================

    public List<OutwardBatch> getBatches() throws Exception {

        System.out.println(
                "================================================="
        );

        System.out.println(
                "OUTWARD MAKER SERVICE : Loading batches from DB"
        );

        System.out.println(
                "================================================="
        );

        try {

            List<OutwardBatch> batches =
                    dao.getBatches();

            // =================================================
            // DAO RETURNED NULL
            // =================================================

            if (batches == null) {

                System.err.println(
                        "OUTWARD MAKER SERVICE : DAO returned NULL"
                );

                return Collections.emptyList();
            }

            // =================================================
            // PRINT TOTAL
            // =================================================

            System.out.println(
                    "OUTWARD MAKER SERVICE : Batches loaded = "
                            + batches.size()
            );

            // =================================================
            // DEBUG LOG
            // =================================================

            for (OutwardBatch batch : batches) {

                if (batch == null) {
                    continue;
                }

                System.out.println(
                        "---------------------------------------------"
                );

                System.out.println(
                        "Batch Number        : "
                                + batch.getBatchNumber()
                );

                System.out.println(
                        "Branch Code         : "
                                + batch.getBranchCode()
                );

                System.out.println(
                        "Number Of Cheques   : "
                                + batch.getNumberOfCheques()
                );

                System.out.println(
                        "Total Amount        : "
                                + batch.getTotalAmount()
                );

                System.out.println(
                        "Batch Status        : "
                                + batch.getBatchStatus()
                );

                System.out.println(
                        "Created By          : "
                                + batch.getCreatedBy()
                );

                System.out.println(
                        "Maker User Number   : "
                                + batch.getMakerUserNumber()
                );

                System.out.println(
                        "Maker Started At    : "
                                + batch.getMakerStartedAt()
                );

                System.out.println(
                        "Maker Completed At  : "
                                + batch.getMakerCompletedAt()
                );

                System.out.println(
                        "Checker User Number : "
                                + batch.getCheckerUserNumber()
                );

                System.out.println(
                        "Lock Status         : "
                                + batch.getLockStatus()
                );

                System.out.println(
                        "Locked By           : "
                                + batch.getLockedBy()
                );

                System.out.println(
                        "Locked At           : "
                                + batch.getLockedAt()
                );
            }

            System.out.println(
                    "================================================="
            );

            return batches;

        } catch (Exception e) {

            System.err.println(
                    "================================================="
            );

            System.err.println(
                    "OUTWARD MAKER SERVICE : ERROR LOADING BATCHES"
            );

            System.err.println(
                    "Exception : "
                            + e.getClass().getName()
            );

            System.err.println(
                    "Message : "
                            + e.getMessage()
            );

            e.printStackTrace();

            System.err.println(
                    "================================================="
            );

            /*
             * Important:
             *
             * Do not hide the database exception.
             * Pass it back to the Controller.
             */
            throw e;
        }
    }

    // =========================================================
    // GET CHEQUES OF A BATCH
    // =========================================================
    //
    // IMPORTANT:
    //
    // New terminology is batchNumber instead of batchId.
    //
    // =========================================================

    public List<OutwardCheque> getCheques(
            String batchNumber) throws Exception {

        if (isBlank(batchNumber)) {

            System.err.println(
                    "getCheques : batchNumber is empty"
            );

            return Collections.emptyList();
        }

        try {

            return dao.getCheques(batchNumber);

        } catch (Exception e) {

            System.err.println(
                    "ERROR loading cheques for batch: "
                            + batchNumber
            );

            e.printStackTrace();

            throw e;
        }
    }

    // =========================================================
    // ASSIGN AND VALIDATE
    // =========================================================
    //
    // Called when Maker clicks:
    //
    //                 OPEN
    //
    // There is NO separate "Assign To Me" button.
    //
    // Open performs:
    //
    // 1. Assign batch
    // 2. Load cheques
    // 3. Validate cheques
    // 4. Move valid batch forward
    //
    // =========================================================

    public OutwardValidationResult assignAndValidate(
            String batchNumber,
            String userId) throws Exception {

        // =====================================================
        // VALIDATE INPUT
        // =====================================================

        if (isBlank(batchNumber)) {

            System.err.println(
                    "assignAndValidate : batchNumber is empty"
            );

            return createEmptyResult();
        }

        if (isBlank(userId)) {

            System.err.println(
                    "assignAndValidate : userId is empty"
            );

            return createEmptyResult();
        }

        batchNumber =
                batchNumber.trim();

        userId =
                userId.trim();

        System.out.println(
                "================================================="
        );

        System.out.println(
                "OUTWARD MAKER SERVICE : OPEN BATCH"
        );

        System.out.println(
                "Batch Number : "
                        + batchNumber
        );

        System.out.println(
                "User ID      : "
                        + userId
        );

        System.out.println(
                "================================================="
        );

        // =====================================================
        // STEP 1
        // ASSIGN BATCH TO MAKER
        // =====================================================
        //
        // DAO is responsible for the actual DB operation.
        //
        // The DAO must ensure that only an AVAILABLE,
        // unassigned and unlocked batch can be assigned.
        //
        // If another Maker already took the batch,
        // assignBatch() returns false.
        //
        // =====================================================

        boolean assigned =
                dao.assignBatch(
                        batchNumber,
                        userId
                );

        if (!assigned) {

            System.err.println(
                    "Batch assignment failed : "
                            + batchNumber
            );

            System.err.println(
                    "Batch may already be assigned, "
                            + "locked or unavailable."
            );

            return createEmptyResult();
        }

        System.out.println(
                "Batch assigned successfully : "
                        + batchNumber
                        + " -> Maker "
                        + userId
        );

        // =====================================================
        // STEP 2
        // LOAD CHEQUES
        // =====================================================

        List<OutwardCheque> cheques =
                dao.getCheques(batchNumber);

        if (cheques == null) {

            cheques =
                    Collections.emptyList();
        }

        System.out.println(
                "Cheque records loaded for "
                        + batchNumber
                        + " = "
                        + cheques.size()
        );

        // =====================================================
        // STEP 3
        // VALIDATE ACTUAL DB VALUES
        // =====================================================

        OutwardValidationResult result =
                validationService.validate(
                        cheques
                );

        if (result == null) {

            System.err.println(
                    "Validation service returned NULL."
            );

            return createEmptyResult();
        }

        System.out.println(
                "Validation completed for batch "
                        + batchNumber
        );

        System.out.println(
                "Data Entry Errors : "
                        + result.getDataEntryErrors()
        );

        System.out.println(
                "MICR Errors       : "
                        + result.getMicrErrors()
        );

        System.out.println(
                "Amount/Account     : "
                        + result.getAmountAccountErrors()
        );

        // =====================================================
        // STEP 4
        // MOVE VALID BATCH FORWARD
        // =====================================================

        if (isValid(result)) {

            boolean updated =
                    dao.updateBatchIfCompleted(
                            batchNumber
                    );

            System.out.println(
                    "Batch validation successful : "
                            + batchNumber
            );

            System.out.println(
                    "Status update = "
                            + updated
            );

        } else {

            System.out.println(
                    "Batch "
                            + batchNumber
                            + " contains validation errors."
            );
        }

        // =====================================================
        // RETURN RESULT TO CONTROLLER
        // =====================================================

        return result;
    }

    // =========================================================
    // VALIDATE EXISTING ASSIGNED BATCH
    // =========================================================
    //
    // Used when a Maker already has the batch assigned and
    // validation needs to be performed again.
    //
    // =========================================================

    public OutwardValidationResult validateBatch(
            String batchNumber) throws Exception {

        if (isBlank(batchNumber)) {

            return createEmptyResult();
        }

        batchNumber =
                batchNumber.trim();

        System.out.println(
                "Validating existing batch : "
                        + batchNumber
        );

        // =====================================================
        // LOAD CHEQUES
        // =====================================================

        List<OutwardCheque> cheques =
                dao.getCheques(batchNumber);

        if (cheques == null) {

            cheques =
                    Collections.emptyList();
        }

        // =====================================================
        // VALIDATE
        // =====================================================

        OutwardValidationResult result =
                validationService.validate(
                        cheques
                );

        // =====================================================
        // IF VALID -> MOVE FORWARD
        // =====================================================

        if (isValid(result)) {

            boolean updated =
                    dao.updateBatchIfCompleted(
                            batchNumber
                    );

            System.out.println(
                    "Batch "
                            + batchNumber
                            + " validation successful."
            );

            System.out.println(
                    "Batch status updated = "
                            + updated
            );
        }

        return result;
    }

    // =========================================================
    // CHECK WHETHER BATCH IS VALID
    // =========================================================

    public boolean isBatchValid(
            String batchNumber) throws Exception {

        if (isBlank(batchNumber)) {

            return false;
        }

        batchNumber =
                batchNumber.trim();

        return dao.isBatchValid(
                batchNumber
        );
    }

    // =========================================================
    // MOVE VALID BATCH TO CHECKER
    // =========================================================
    //
    // Before moving the batch to Checker:
    //
    // 1. Verify batch number
    // 2. Verify batch is actually valid
    // 3. Ask DAO to update status
    //
    // =========================================================

    public boolean sendToChecker(
            String batchNumber) throws Exception {

        if (isBlank(batchNumber)) {

            return false;
        }

        batchNumber =
                batchNumber.trim();

        System.out.println(
                "Sending batch to Checker : "
                        + batchNumber
        );

        // =====================================================
        // ALWAYS VALIDATE DATABASE DATA
        // =====================================================

        if (!dao.isBatchValid(batchNumber)) {

            System.err.println(
                    "Batch is not valid : "
                            + batchNumber
            );

            return false;
        }

        // =====================================================
        // UPDATE STATUS
        // =====================================================

        boolean updated =
                dao.updateBatchIfCompleted(
                        batchNumber
                );

        System.out.println(
                "Batch sent to Checker : "
                        + batchNumber
                        + " | Updated = "
                        + updated
        );

        return updated;
    }

    // =========================================================
    // CHECK VALIDATION RESULT
    // =========================================================

    private boolean isValid(
            OutwardValidationResult result) {

        if (result == null) {

            return false;
        }

        return result.getDataEntryErrors() == 0
                && result.getMicrErrors() == 0
                && result.getAmountAccountErrors() == 0;
    }

    // =========================================================
    // CREATE EMPTY VALIDATION RESULT
    // =========================================================

    private OutwardValidationResult createEmptyResult() {

        return new OutwardValidationResult();
    }

    // =========================================================
    // BLANK CHECK
    // =========================================================

    private boolean isBlank(
            String value) {

        return value == null
                || value.trim().isEmpty();
    }
}