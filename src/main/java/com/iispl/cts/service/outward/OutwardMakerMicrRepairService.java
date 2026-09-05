package com.iispl.cts.service.outward;

import java.util.List;

import com.iispl.cts.dao.outward.OutwardMakerMicrRepairDAO;
import com.iispl.cts.model.outward.OutwardBatch;

public class OutwardMakerMicrRepairService {

    private final OutwardMakerMicrRepairDAO dao;

    public OutwardMakerMicrRepairService() {
        dao = new OutwardMakerMicrRepairDAO();
    }

    public List<OutwardBatch> getMicrRepairBatches() {

        return dao.getMicrRepairBatches();
    }

    public int getMicrErrorCount(String batchNumber) {

        return dao.getMicrErrorCount(batchNumber);
    }

    public void startMicrRepair(String batchNumber) {

        dao.updateBatchStatus(
            batchNumber,
            "MICR_REPAIR"
        );
    }

    /*
     * Called after repairing a cheque.
     */
    public void repairCheque(
            String batchNumber,
            String chequeNumber) {

        dao.updateChequeStatus(
            batchNumber,
            chequeNumber,
            "REPAIRED"
        );

        /*
         * Check whether any MICR_ERROR cheque
         * is still remaining in this batch.
         */
        int remainingErrors =
            dao.getMicrErrorCount(batchNumber);

        if (remainingErrors == 0) {

            dao.updateBatchStatus(
                batchNumber,
                "MICR_REPAIRED"
            );
        }
    }
}