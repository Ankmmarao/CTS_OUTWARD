package com.iispl.cts.service.outward.checker;

import java.util.Collections;
import java.util.List;

import com.iispl.cts.dao.outward.checker.CheckerBatchDAO;
import com.iispl.cts.model.outward.OutwardBatch;

public class CheckerBatchService {

    private final CheckerBatchDAO batchDAO;


    /*
     * ============================================================
     * CONSTRUCTOR
     * ============================================================
     */

    public CheckerBatchService() {

        batchDAO =
                new CheckerBatchDAO();
    }


    /*
     * ============================================================
     * GET CURRENT CHECKER'S LOCKED BATCHES
     * ============================================================
     */

    public List<OutwardBatch> getCheckerQueueBatches(
            String checkerUserId) {

        /*
         * Validate Checker ID
         */

        if (checkerUserId == null
                || checkerUserId.trim().isEmpty()) {

            return Collections.emptyList();
        }

        /*
         * Get batches locked by this Checker only
         */

        return batchDAO.getCheckerBatches(
                checkerUserId.trim()
        );
    }
}