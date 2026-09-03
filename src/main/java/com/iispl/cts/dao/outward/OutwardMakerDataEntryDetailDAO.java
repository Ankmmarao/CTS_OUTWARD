package com.iispl.cts.dao.outward;

import java.util.List;

import com.iispl.cts.data.CTSStaticData;
import com.iispl.cts.model.outward.OutwardCheque;

public class OutwardMakerDataEntryDetailDAO {

    public List<OutwardCheque> getCheques(
            String batchId) {

        return CTSStaticData.getDataEntryCheques(
                batchId
        );
    }

    public void saveCheque(
            OutwardCheque cheque) {

        CTSStaticData.completeDataEntryCheque(
                cheque.getBatchId(),
                cheque.getChequeId()
        );
    }

    public void rejectCheque(
            OutwardCheque cheque,
            String reason) {

        CTSStaticData.rejectDataEntryCheque(
                cheque.getBatchId(),
                cheque.getChequeId(),
                reason
        );
    }
}