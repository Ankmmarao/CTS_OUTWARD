
package com.iispl.cts.service.outward;

import java.util.List;

import com.iispl.cts.dao.outward.OutwardMakerDashboardDAO;
import com.iispl.cts.model.outward.OutwardBatch;
import com.iispl.cts.model.outward.OutwardCheque;
import com.iispl.cts.model.outward.OutwardValidationResult;

public class OutwardMakerDashboardService {

    private final OutwardMakerDashboardDAO dao;

    private final OutwardValidationService validationService;


    // =========================================================
    // CONSTRUCTOR
    // =========================================================

    public OutwardMakerDashboardService() {

        dao =
                new OutwardMakerDashboardDAO();

        validationService =
                new OutwardValidationService();
    }


    // =========================================================
    // GET MAKER DASHBOARD BATCHES
    // =========================================================

    public List<OutwardBatch> getBatches() {

        return dao.getBatches();
    }


    // =========================================================
    // GET CHEQUES OF A BATCH
    // =========================================================

    public List<OutwardCheque> getCheques(
            String batchId) {

        if (isBlank(batchId)) {

            return List.of();
        }

        return dao.getCheques(batchId);
    }


    // =========================================================
    // ASSIGN AND VALIDATE
    // =========================================================

    public OutwardValidationResult assignAndValidate(
            String batchId,
            String userId) {


        /*
         * Validate input first.
         */
        if (isBlank(batchId)) {

            return createEmptyResult();
        }

        if (isBlank(userId)) {

            return createEmptyResult();
        }


        /*
         * STEP 1
         *
         * Assign batch to Maker.
         */
        boolean assigned =
                dao.assignBatch(
                        batchId,
                        userId
                );


        /*
         * Assignment failed.
         *
         * For example:
         * - batch does not exist
         * - another Maker already assigned it
         * - batch is not AVAILABLE
         */
        if (!assigned) {

            return createEmptyResult();
        }


        /*
         * STEP 2
         *
         * Load actual cheque values from DB.
         */
        List<OutwardCheque> cheques =
                dao.getCheques(batchId);


        /*
         * STEP 3
         *
         * Dynamically validate actual values.
         *
         * No manually inserted errorType is required.
         */
        OutwardValidationResult result =
                validationService.validate(
                        cheques
                );


        /*
         * STEP 4
         *
         * If there are no errors, the batch can
         * move directly to Checker.
         */
        if (isValid(result)) {

            dao.updateBatchIfCompleted(
                    batchId
            );
        }


        return result;
    }


    // =========================================================
    // VALIDATE EXISTING ASSIGNED BATCH
    // =========================================================

    public OutwardValidationResult
    validateBatch(String batchId) {

        if (isBlank(batchId)) {

            return createEmptyResult();
        }


        /*
         * Load current DB values.
         *
         * This is important after a repair.
         * We always validate the latest values.
         */
        List<OutwardCheque> cheques =
                dao.getCheques(batchId);


        OutwardValidationResult result =
                validationService.validate(
                        cheques
                );


        /*
         * If everything is valid,
         * move batch to Checker.
         */
        if (isValid(result)) {

            dao.updateBatchIfCompleted(
                    batchId
            );
        }


        return result;
    }


    // =========================================================
    // CHECK WHETHER BATCH IS VALID
    // =========================================================

    public boolean isBatchValid(
            String batchId) {

        if (isBlank(batchId)) {

            return false;
        }

        return dao.isBatchValid(
                batchId
        );
    }


    // =========================================================
    // MOVE VALID BATCH TO CHECKER
    // =========================================================

    public boolean sendToChecker(
            String batchId) {

        if (isBlank(batchId)) {

            return false;
        }


        /*
         * Never blindly change the status.
         *
         * First validate actual DB values.
         */
        if (!dao.isBatchValid(batchId)) {

            return false;
        }


        return dao.updateBatchIfCompleted(
                batchId
        );
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
    // EMPTY RESULT
    // =========================================================

    private OutwardValidationResult
    createEmptyResult() {

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

