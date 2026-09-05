package com.iispl.cts.service.outward.checker;

import java.util.Collections;
import java.util.List;

import com.iispl.cts.dao.outward.checker.CheckerDashboardDAO;
import com.iispl.cts.model.outward.OutwardBatch;

public class CheckerDashboardService {

    private final CheckerDashboardDAO dashboardDAO;

    public CheckerDashboardService() {
        dashboardDAO = new CheckerDashboardDAO();
    }

    public List<OutwardBatch> getCheckerBatches() {

        List<OutwardBatch> batches =
                dashboardDAO.getCheckerBatches();

        if (batches == null) {
            return Collections.emptyList();
        }

        return batches;
    }

    public int getQueueCount() {
        return dashboardDAO.getQueueCount();
    }

    public int getPendingChequeCount() {
        return dashboardDAO.getPendingChequeCount();
    }

    public int getAcceptedCount() {
        return dashboardDAO.getAcceptedCount();
    }

    public int getRejectedCount() {
        return dashboardDAO.getRejectedCount();
    }
}