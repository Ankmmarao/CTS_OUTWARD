package com.iispl.cts.service.outward;

import java.sql.SQLException;
import java.util.List;

import com.iispl.cts.dao.outward.OutwardMakerDashboardDAO;
import com.iispl.cts.model.outward.OutwardBatch;
import com.iispl.cts.model.outward.OutwardCheque;
import com.iispl.cts.model.outward.OutwardValidationResult;

public class OutwardMakerDashboardService {

    private final OutwardMakerDashboardDAO dao;

    private final OutwardValidationService validationService;


    public OutwardMakerDashboardService() {

        this.dao =
                new OutwardMakerDashboardDAO();

        this.validationService =
                new OutwardValidationService();
    }


    // =========================================================
    // GET BATCHES
    // =========================================================

    public List<OutwardBatch> getBatches()
            throws SQLException {

        return dao.getBatches();
    }


    // =========================================================
    // FIND BATCH
    // =========================================================

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

        String cleanBatchNumber =
                batchNumber.trim();

        for (OutwardBatch batch : batches) {

            if (batch != null
                    && cleanBatchNumber.equalsIgnoreCase(
                            batch.getBatchNumber())) {

                return batch;
            }
        }

        return null;
    }


    // =========================================================
    // ASSIGN BATCH
    // =========================================================

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


    // =========================================================
    // GET CHEQUES
    // =========================================================

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


    // =========================================================
    // CHECK BATCH
    // =========================================================

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


    // =========================================================
    // ASSIGN + VALIDATE
    // =========================================================

    public OutwardValidationResult assignAndValidate(
            String batchNumber,
            String userId)
            throws SQLException {

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


        /*
         * -----------------------------------------------------
         * STEP 1
         * -----------------------------------------------------
         *
         * Assign the batch to the current Maker.
         */
        boolean assigned =
                dao.assignBatch(
                        cleanBatchNumber,
                        cleanUserId
                );

        if (!assigned) {

            return null;
        }


        /*
         * -----------------------------------------------------
         * STEP 2
         * -----------------------------------------------------
         *
         * Get ALL cheques from the batch.
         */
        List<OutwardCheque> cheques =
                dao.getCheques(
                        cleanBatchNumber
                );

        if (cheques == null
                || cheques.isEmpty()) {

            return createEmptyValidationResult();
        }


        /*
         * -----------------------------------------------------
         * STEP 3
         * -----------------------------------------------------
         *
         * Validate the entire batch.
         */
        OutwardValidationResult result =
                validationService.validate(
                        cheques
                );


        int micrErrors =
                result.getMicrErrors();

        int dataEntryErrors =
                result.getDataEntryErrors();

        int amountAccountErrors =
                result.getAmountAccountErrors();


        /*
         * -----------------------------------------------------
         * STEP 4 - BATCH STATUS
         * -----------------------------------------------------
         *
         * MICR gets FIRST PRIORITY.
         *
         * If even one MICR error exists,
         * the batch goes to MICR_REPAIR.
         *
         * Only when there are NO MICR errors do
         * we check Data Entry.
         *
         * Only when there are NO MICR and NO Data Entry
         * errors do we check Amount/Account.
         */
        if (micrErrors > 0) {

            dao.updateBatchStatusAfterValidation(
                    cleanBatchNumber,
                    "MICR_REPAIR"
            );

        } else if (dataEntryErrors > 0) {

            dao.updateBatchStatusAfterValidation(
                    cleanBatchNumber,
                    "DATA_ENTRY"
            );

        } else if (amountAccountErrors > 0) {

            dao.updateBatchStatusAfterValidation(
                    cleanBatchNumber,
                    "AMOUNT_ACCOUNT"
            );

        } else {

            dao.updateBatchStatusAfterValidation(
                    cleanBatchNumber,
                    "READY_FOR_CHECKER"
            );
        }


        return result;
    }


    // =========================================================
    // EMPTY RESULT
    // =========================================================

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


    // =========================================================
    // COMPLETE BATCH
    // =========================================================

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