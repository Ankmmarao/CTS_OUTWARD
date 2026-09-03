package com.iispl.cts.dao.outward;

import java.util.List;

import com.iispl.cts.data.CTSStaticData;
import com.iispl.cts.model.outward.OutwardBatch;
import com.iispl.cts.model.outward.OutwardCheque;

public class OutwardMakerDashboardDAO {

    public List<OutwardBatch> getBatches() {

        return CTSStaticData.getMakerDashboardBatches();
    }

    public List<OutwardCheque> getCheques(
            String batchId) {

        return CTSStaticData.getMakerDashboardCheques(
                batchId
        );
    }

    public void assignBatch(
            String batchId,
            String userId) {

        CTSStaticData.assignBatch(
                batchId,
                userId
        );
    }
}