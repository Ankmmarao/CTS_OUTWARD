
package com.iispl.cts.dao.outward;

import java.util.List;

import com.iispl.cts.data.CTSStaticData;
import com.iispl.cts.model.outward.OutwardBatch;
import com.iispl.cts.model.outward.OutwardCheque;

public class OutwardMakerDashboardDAO {

    /**
     * Get batches for Maker Dashboard.
     *
     * Data comes directly from PostgreSQL.
     */
    public List<OutwardBatch> getBatches() {

        return CTSStaticData.getMakerDashboardBatches();
    }


    /**
     * Get all cheques belonging to a particular batch.
     *
     * Actual cheque values are loaded from DB.
     */
    public List<OutwardCheque> getCheques(
            String batchId) {

        return CTSStaticData.getMakerDashboardCheques(
                batchId
        );
    }


    /**
     * Assign a batch to the logged-in Maker.
     *
     * Returns:
     * true  -> assignment successful
     * false -> assignment failed/already assigned
     */
    public boolean assignBatch(
            String batchId,
            String userId) {

        return CTSStaticData.assignBatch(
                batchId,
                userId
        );
    }


    /**
     * Check whether the complete batch is valid.
     *
     * After repairs, this is used to determine
     * whether the batch can move to Checker.
     */
    public boolean isBatchValid(
            String batchId) {

        return CTSStaticData.isBatchValid(
                batchId
        );
    }


    /**
     * Move a fully valid batch to Checker.
     *
     * Status becomes:
     *
     * READY_FOR_CHECKER
     */
    public boolean updateBatchIfCompleted(
            String batchId) {

        return CTSStaticData.updateBatchIfCompleted(
                batchId
        );
    }
}
