package com.iispl.cts.service.outward;

import com.iispl.cts.dao.outward.OutwardMakerDashboardDAO;
import com.iispl.cts.model.outward.OutwardBatch;
import com.iispl.cts.model.outward.OutwardCheque;
import com.iispl.cts.model.outward.OutwardValidationResult;

import java.sql.SQLException;
import java.util.List;

public class OutwardMakerDashboardService {

    private final OutwardMakerDashboardDAO dao;

    private final OutwardValidationService validationService;


    // ============================================================
    // CONSTRUCTOR
    // ============================================================

    public OutwardMakerDashboardService() {

        this.dao =
                new OutwardMakerDashboardDAO();

        this.validationService =
                new OutwardValidationService();
    }


    // ============================================================
    // GET BATCHES
    // ============================================================

    public List<OutwardBatch> getBatches()
            throws SQLException {

        return dao.getBatches();
    }


    // ============================================================
    // FIND BATCH
    // ============================================================

    public OutwardBatch findBatch(
            String batchNumber)
            throws SQLException {

        if (batchNumber == null
                || batchNumber.trim().isEmpty()) {

            return null;
        }

        List<OutwardBatch> batches =
                dao.getBatches();

        if (batches == null) {

            return null;
        }

        for (OutwardBatch batch : batches) {

            if (batch != null
                    && batchNumber.trim()
                            .equalsIgnoreCase(
                                    batch.getBatchNumber()
                            )) {

                return batch;
            }
        }

        return null;
    }


    // ============================================================
    // ASSIGN BATCH
    // ============================================================

    public boolean assignBatch(
            String batchNumber,
            String userId)
            throws SQLException {

        if (batchNumber == null
                || batchNumber.trim().isEmpty()) {

            return false;
        }

        if (userId == null
                || userId.trim().isEmpty()) {

            return false;
        }

        return dao.assignBatch(
                batchNumber.trim(),
                userId.trim()
        );
    }


    // ============================================================
    // GET CHEQUES
    // ============================================================

    public List<OutwardCheque> getCheques(
            String batchNumber)
            throws SQLException {

        if (batchNumber == null
                || batchNumber.trim().isEmpty()) {

            return null;
        }

        return dao.getCheques(
                batchNumber.trim()
        );
    }


    // ============================================================
    // VALIDATE BATCH
    // ============================================================

    public boolean isBatchValid(
            String batchNumber)
            throws SQLException {

        if (batchNumber == null
                || batchNumber.trim().isEmpty()) {

            return false;
        }

        return dao.isBatchValid(
                batchNumber.trim()
        );
    }


    // ============================================================
    // ASSIGN + VALIDATE
    // ============================================================
    //
    // Maker clicks OPEN.
    //
    // 1. Assign batch to logged-in Maker
    // 2. Load all cheques
    // 3. Run current validation service
    // 4. Update batch status
    // 5. Return validation result to Controller
    //
    // ============================================================

    public OutwardValidationResult assignAndValidate(
            String batchNumber,
            String userId)
            throws SQLException {

        // --------------------------------------------------------
        // BASIC VALIDATION
        // --------------------------------------------------------

        if (batchNumber == null
                || batchNumber.trim().isEmpty()) {

            return null;
        }

        if (userId == null
                || userId.trim().isEmpty()) {

            return null;
        }

        String cleanBatchNumber =
                batchNumber.trim();

        String cleanUserId =
                userId.trim();


        // --------------------------------------------------------
        // STEP 1
        // ASSIGN BATCH TO CURRENT MAKER
        // --------------------------------------------------------

        boolean assigned =
                dao.assignBatch(
                        cleanBatchNumber,
                        cleanUserId
                );


        // --------------------------------------------------------
        // ASSIGNMENT FAILED
        // --------------------------------------------------------

        if (!assigned) {

            return null;
        }


        // --------------------------------------------------------
        // STEP 2
        // LOAD CHEQUES
        // --------------------------------------------------------

        List<OutwardCheque> cheques =
                dao.getCheques(
                        cleanBatchNumber
                );


        // --------------------------------------------------------
        // NO CHEQUES
        // --------------------------------------------------------

        if (cheques == null
                || cheques.isEmpty()) {

            return createEmptyValidationResult();
        }


        // --------------------------------------------------------
        // STEP 3
        // RUN CURRENT VALIDATION SERVICE
        // --------------------------------------------------------
        //
        // IMPORTANT:
        //
        // OutwardValidationService now performs:
        //
        // Data Entry:
        //     Cheque Number
        //     Cheque Date
        //     City Code
        //     Bank Code
        //     Branch Code
        //
        // Amount / Account:
        //     Drawer Account Number
        //     Payee Account Number
        //     Amount
        //
        // MICR:
        //     NOT VALIDATED
        //
        // --------------------------------------------------------

        OutwardValidationResult result =
                validationService.validate(
                        cheques
                );


        // --------------------------------------------------------
        // STEP 4
        // UPDATE BATCH STATUS
        // --------------------------------------------------------

        int dataEntryErrors =
                result.getDataEntryErrors();

        int amountAccountErrors =
                result.getAmountAccountErrors();


        // --------------------------------------------------------
        // DATA ENTRY ERRORS
        // --------------------------------------------------------

        if (dataEntryErrors > 0) {

            dao.updateBatchStatusAfterValidation(
                    cleanBatchNumber,
                    "DATA_ENTRY"
            );

        }

        // --------------------------------------------------------
        // AMOUNT / ACCOUNT ERRORS
        // --------------------------------------------------------

        else if (amountAccountErrors > 0) {

            dao.updateBatchStatusAfterValidation(
                    cleanBatchNumber,
                    "AMOUNT_ACCOUNT"
            );

        }

        // --------------------------------------------------------
        // NO ERRORS
        // --------------------------------------------------------

        else {

            dao.updateBatchStatusAfterValidation(
                    cleanBatchNumber,
                    "READY_FOR_CHECKER"
            );
        }


        // --------------------------------------------------------
        // RETURN VALIDATION RESULT
        // --------------------------------------------------------

        return result;
    }


    // ============================================================
    // EMPTY VALIDATION RESULT
    // ============================================================

    private OutwardValidationResult
    createEmptyValidationResult() {

        OutwardValidationResult result =
                new OutwardValidationResult();

        result.setTotalCheques(0);

        result.setDataEntryErrors(0);

        result.setMicrErrors(0);

        result.setAmountAccountErrors(0);

        return result;
    }


    // ============================================================
    // COMPLETE BATCH
    // ============================================================

    public void completeBatch(
            String batchNumber)
            throws SQLException {

        if (batchNumber == null
                || batchNumber.trim().isEmpty()) {

            return;
        }

        dao.updateBatchIfCompleted(
                batchNumber.trim()
        );
    }
}