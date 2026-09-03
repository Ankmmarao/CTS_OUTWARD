package com.iispl.cts.service.outward;

import java.util.List;

import com.iispl.cts.model.outward.OutwardCheque;
import com.iispl.cts.model.outward.OutwardValidationResult;

public class OutwardValidationService {

    public OutwardValidationResult validate(
            List<OutwardCheque> cheques) {

        OutwardValidationResult result =
                new OutwardValidationResult();

        if (cheques == null) {
            return result;
        }

        result.setTotalCheques(
                cheques.size()
        );

        int dataEntry = 0;
        int micr = 0;
        int amountAccount = 0;

        for (OutwardCheque cheque :
                cheques) {

            if (cheque.isSaved()
                    || cheque.isRejected()) {

                continue;
            }

            String errorType =
                    cheque.getErrorType();

            if ("DATA_ENTRY_ERROR".equals(
                    errorType)) {

                dataEntry++;
            }

            else if ("MICR_ERROR".equals(
                    errorType)) {

                micr++;
            }

            else if ("AMOUNT_ACCOUNT_ERROR".equals(
                    errorType)) {

                amountAccount++;
            }
        }

        result.setDataEntryErrors(dataEntry);

        result.setMicrErrors(micr);

        result.setAmountAccountErrors(
                amountAccount
        );

        return result;
    }
}