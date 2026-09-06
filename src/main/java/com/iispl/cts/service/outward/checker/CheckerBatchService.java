package com.iispl.cts.service.outward.checker;

import java.util.Collections;
import java.util.List;

import com.iispl.cts.dao.outward.checker.CheckerBatchDAO;
import com.iispl.cts.model.outward.OutwardBatch;
import com.iispl.cts.model.outward.OutwardCheque;

public class CheckerBatchService {

    private final CheckerBatchDAO batchDAO;

    // =========================================================
    // CONSTRUCTOR
    // =========================================================

    public CheckerBatchService() {

        batchDAO = new CheckerBatchDAO();
    }

    // =========================================================
    // GET CHEQUES BY BATCH NUMBER
    // =========================================================

    public List<OutwardCheque> getChequesByBatchId(
            String batchId) {

        if (batchId == null ||
            batchId.trim().isEmpty()) {

            return Collections.emptyList();
        }

        return batchDAO.getChequesByBatchId(
                batchId.trim()
        );
    }

    // =========================================================
    // GET CHECKER QUEUE BATCHES
    // =========================================================

    public List<OutwardBatch> getCheckerQueueBatches() {

        List<OutwardBatch> batches =
                batchDAO.getCheckerBatches();

        if (batches == null) {

            return Collections.emptyList();
        }

        return batches;
    }
}