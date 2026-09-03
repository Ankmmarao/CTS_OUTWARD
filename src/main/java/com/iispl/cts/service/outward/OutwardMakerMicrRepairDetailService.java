package com.iispl.cts.service.outward;

import java.util.List;

import com.iispl.cts.dao.outward.OutwardMakerMicrRepairDetailDAO;
import com.iispl.cts.model.outward.OutwardCheque;

public class OutwardMakerMicrRepairDetailService {

    private final OutwardMakerMicrRepairDetailDAO dao;

    public OutwardMakerMicrRepairDetailService() {

        dao = new OutwardMakerMicrRepairDetailDAO();
    }

    public List<OutwardCheque> getCheques(
            String batchId) {

        return dao.getCheques(batchId);
    }

    public void saveCheque(
            OutwardCheque cheque) {

        dao.saveCheque(cheque);
    }

    public void rejectCheque(
            OutwardCheque cheque,
            String reason) {

        dao.rejectCheque(cheque, reason);
    }
}