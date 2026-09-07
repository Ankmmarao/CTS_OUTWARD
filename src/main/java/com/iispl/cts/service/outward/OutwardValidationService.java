
package com.iispl.cts.service.outward;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import com.iispl.cts.model.outward.OutwardCheque;
import com.iispl.cts.model.outward.OutwardValidationResult;


public class OutwardValidationService {

    public OutwardValidationResult validate(List<OutwardCheque> cheques) {
        OutwardValidationResult result = new OutwardValidationResult();

        if (cheques == null) {
            return result;
        }

        result.setTotalCheques(cheques.size());

        int dataEntryErrors = 0;
        int micrErrors = 0;
        int amountAccountErrors = 0;


        for (OutwardCheque cheque : cheques) {

            if (cheque == null) {
                continue;
            }

             // DATA ENTRY VALIDATION
            if (hasDataEntryError(cheque)) {
                dataEntryErrors++;
            }
          
             // MICR VALIDATION         
            if (hasMicrError(cheque)) {
                micrErrors++;
            }

             // AMOUNT / ACCOUNT VALIDATION
            if (hasAmountAccountError(cheque)) {
                amountAccountErrors++;
            }
        }


        result.setDataEntryErrors(dataEntryErrors);
        result.setMicrErrors(micrErrors);
        result.setAmountAccountErrors(amountAccountErrors);
        
        return result;
    }


    // =========================================================
    // DATA ENTRY VALIDATION
    // =========================================================
    private boolean hasDataEntryError(OutwardCheque cheque) {

        // CHEQUE NUMBER
        String chequeNumber = cheque.getChequeNumber();

        if (isBlank(chequeNumber)) {
            return true;
        }


        // CHEQUE DATE
        LocalDate chequeDate = cheque.getChequeDate();

        if (chequeDate == null) {
            return true;
        }

        return false;
    }
    
    
    // =========================================================
    // MICR VALIDATION
    // =========================================================
    private boolean hasMicrError(OutwardCheque cheque) {

        // CITY CODE
        String cityCode = cheque.getCityCode();

        if (isBlank(cityCode)) {
            return true;
        }

        if (!cityCode.matches("^[0-9]{3}$")) {
            return true;
        }


        // BANK CODE
        String bankCode = cheque.getBankCode();

        if (isBlank(bankCode)) {
            return true;
        }

        if (!bankCode.matches("^[0-9]{3}$")) {
            return true;
        }


        // BRANCH CODE
        String branchCode = cheque.getBranchCode();

        if (isBlank(branchCode)) {
            return true;
        }

        if (!branchCode.matches("^[0-9]{3}$")) {
            return true;
        }

        return false;
    }
    

    // =========================================================
    // ACCOUNT / AMOUNT VALIDATION
    // =========================================================
    private boolean hasAmountAccountError(OutwardCheque cheque) {
    	
        // ACCOUNT NUMBER
    	String accountNumber = cheque.getDrawerAccountNumber();

        if (isBlank(accountNumber)) {
            return true;
        }


        
         //Current project rule:
         //account number = exactly 12 digits.
        if (!accountNumber.matches("^[0-9]{12}$")) {
            return true;
        }


        // AMOUNT
        BigDecimal amount = cheque.getAmount();

        if (amount == null) {
            return true;
        }

        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            return true;
        }

        return false;
    }


    // =========================================================
    // BLANK VALIDATION
    // =========================================================
    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}

