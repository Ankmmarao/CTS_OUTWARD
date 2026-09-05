package com.iispl.cts.service.outward;

import java.util.List;

import com.iispl.cts.dao.outward.OutwardMakerSendCheckerDAO;
import com.iispl.cts.model.outward.OutwardBatch;

public class OutwardMakerSendCheckerService {

    private final OutwardMakerSendCheckerDAO dao;


    public OutwardMakerSendCheckerService() {

        dao = new OutwardMakerSendCheckerDAO();
    }


    /**
     * Get batches which are ready for Checker
     * and belong to the current Maker.
     */
    public List<OutwardBatch> getReadyBatches(
            String userId) throws Exception {

        if (userId == null
                || userId.trim().isEmpty()) {

            throw new IllegalArgumentException(
                    "Invalid Maker user ID."
            );
        }

        return dao.getReadyBatches(userId);
    }


    /**
     * Send batch to Checker.
     */
    public boolean sendToChecker(
            String batchId,
            String userId) throws Exception {

        if (batchId == null
                || batchId.trim().isEmpty()) {

            throw new IllegalArgumentException(
                    "Batch ID is required."
            );
        }


        if (userId == null
                || userId.trim().isEmpty()) {

            throw new IllegalArgumentException(
                    "Maker user ID is required."
            );
        }


        /*
         * DAO performs:
         *
         * 1. Check batch belongs to Maker
         * 2. Check status = READY_FOR_CHECKER
         * 3. Change status
         */
        return dao.sendToChecker(
                batchId,
                userId
        );
    }
}