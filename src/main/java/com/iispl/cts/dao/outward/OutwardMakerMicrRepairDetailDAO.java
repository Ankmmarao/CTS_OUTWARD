package com.iispl.cts.dao.outward;

import java.util.List;

import com.iispl.cts.data.CTSStaticData;
import com.iispl.cts.model.outward.OutwardCheque;

public class OutwardMakerMicrRepairDetailDAO {

    public List<OutwardCheque> getCheques(
            String batchId) {

        return CTSStaticData.getMicrCheques(
                batchId
        );
    }

    public void saveCheque(
            OutwardCheque cheque) {

        CTSStaticData.completeMicrCheque(
                cheque.getBatchId(),
                cheque.getChequeId()
        );
    }

    public void rejectCheque(
            OutwardCheque cheque,
            String reason) {

        CTSStaticData.rejectMicrCheque(
                cheque.getBatchId(),
                cheque.getChequeId(),
                reason
        );
    }
}