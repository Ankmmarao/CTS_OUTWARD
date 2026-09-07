package com.iispl.cts.service.outward.checker;

import java.util.Collections;
import java.util.List;

import com.iispl.cts.dao.outward.checker.CheckerBatchDAO;
import com.iispl.cts.model.outward.OutwardBatch;
import com.iispl.cts.model.outward.OutwardCheque;

public class CheckerBatchService {

    // ============================================================
    // DAO
    // ============================================================

    private final CheckerBatchDAO batchDAO;


    // ============================================================
    // CONSTRUCTOR
    // ============================================================

    public CheckerBatchService() {

        batchDAO =
                new CheckerBatchDAO();
    }


    // ============================================================
    // GET CURRENT CHECKER'S LOCKED BATCHES
    // ============================================================

    public List<OutwardBatch> getCheckerQueueBatches(
            String checkerUserId) {

        // Validate Checker ID

        if (checkerUserId == null
                || checkerUserId.trim().isEmpty()) {

            return Collections.emptyList();
        }

        // Get batches assigned to Checker

        return batchDAO.getCheckerBatches(
                checkerUserId.trim()
        );
    }


    // ============================================================
    // GET CHEQUES BY BATCH NUMBER
    // ============================================================

    public List<OutwardCheque> getChequesByBatchNumber(
            String batchNumber) {

        // Validate Batch Number

        if (batchNumber == null
                || batchNumber.trim().isEmpty()) {

            return Collections.emptyList();
        }

        return batchDAO.getChequesByBatchNumber(
                batchNumber.trim()
        );
    }


    // ============================================================
    // GET CHEQUES BY BATCH ID
    // ============================================================
    //
    // This is kept because your existing
    // CheckerBatchVerificationController uses:
    //
    // batchService.getChequesByBatchId(batchId)
    //
    // ============================================================

    public List<OutwardCheque> getChequesByBatchId(
            String batchId) {

        // Validate Batch ID

        if (batchId == null
                || batchId.trim().isEmpty()) {

            return Collections.emptyList();
        }

        return batchDAO.getChequesByBatchId(
                batchId.trim()
        );
    }

}