package com.iispl.cts.service.outward.checker;

import java.util.List;

import com.iispl.cts.dao.outward.checker.CheckerDashboardDAO;
import com.iispl.cts.model.outward.OutwardBatch;

public class CheckerDashboardService {

    private final CheckerDashboardDAO dao;

    public CheckerDashboardService() {
        this.dao = new CheckerDashboardDAO();
    }

    /**
     * Load all batches available for Checker.
     */
    public List<OutwardBatch> getBatches() {

        return dao.getCheckerBatches();
    }

    /**
     * Find a particular batch.
     */
    public OutwardBatch findBatch(String batchNumber) {

        if (batchNumber == null ||
            batchNumber.trim().isEmpty()) {

            return null;
        }

        return dao.findBatch(batchNumber);
    }
}