package com.iispl.cts.service.outward.checker;

import java.util.List;

import com.iispl.cts.dao.outward.checker.CheckerAssignmentDAO;
import com.iispl.cts.dao.outward.checker.CheckerBatchDAO;
import com.iispl.cts.model.outward.OutwardBatch;

public class CheckerBatchService {

    private final CheckerBatchDAO batchDao;
    private final CheckerAssignmentDAO assignmentDao;

    public CheckerBatchService() {
        this.batchDao = new CheckerBatchDAO();
        this.assignmentDao = new CheckerAssignmentDAO();
    }

    /**
     * Get batches currently assigned to the Checker.
     */
    public List<OutwardBatch> getCheckerBatches(String checkerUserId) {

        return batchDao.getCheckerBatches(checkerUserId);
    }

    /**
     * Find a batch by batch number.
     */
    public OutwardBatch findBatch(String batchNumber) {

        return batchDao.getBatchByNumber(batchNumber);
    }

    /**
     * Take/assign a batch to the current Checker.
     */
    public boolean assignBatch(
            String batchNumber,
            int checkerUserId) {

        return assignmentDao.takeBatch(
                batchNumber,
                checkerUserId
        );
    }

    /**
     * Check whether a batch is assigned to any Checker.
     */
    public boolean isBatchAssigned(String batchNumber) {

        return assignmentDao.isBatchAssigned(batchNumber);
    }

    /**
     * Check whether the batch belongs to this Checker.
     */
    public boolean isAssignedToChecker(
            String batchNumber,
            int checkerUserId) {

        return assignmentDao.isBatchAssignedToChecker(
                batchNumber,
                checkerUserId
        );
    }

    /**
     * Complete the Checker assignment for a batch.
     */
    public boolean completeBatch(
            String batchNumber,
            int checkerUserId) {

        return assignmentDao.completeBatch(
                batchNumber,
                checkerUserId
        );
    }
}