package com.iispl.cts.service.outward;

import java.util.List;

import com.iispl.cts.dao.outward.OutwardMakerDashboardDAO;
import com.iispl.cts.model.outward.OutwardBatch;
import com.iispl.cts.model.outward.OutwardCheque;
import com.iispl.cts.model.outward.OutwardValidationResult;

public class OutwardMakerDashboardService {

    private final OutwardMakerDashboardDAO dao;

    private final OutwardValidationService validationService;

    public OutwardMakerDashboardService() {

        dao =
                new OutwardMakerDashboardDAO();

        validationService =
                new OutwardValidationService();
    }

    public List<OutwardBatch> getBatches() {

        return dao.getBatches();
    }

    public OutwardValidationResult assignAndValidate(
            String batchId,
            String userId) {

        dao.assignBatch(
                batchId,
                userId
        );

        List<OutwardCheque> cheques =
                dao.getCheques(batchId);

        OutwardValidationResult result =
                validationService.validate(
                        cheques
                );

        return result;
    }
}