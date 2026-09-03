
package com.iispl.cts.service.outward;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;

import com.iispl.cts.model.outward.OutwardCheque;
import com.iispl.cts.model.outward.OutwardValidationResult;

public class OutwardValidationService {

    /*
     * Validate all cheques in a batch.
     *
     * IMPORTANT:
     * We do NOT depend on cheque.getErrorType().
     *
     * Errors are detected from the actual cheque values.
     */
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

        int dataEntryErrors = 0;
        int micrErrors = 0;
        int amountAccountErrors = 0;


        for (OutwardCheque cheque : cheques) {

            if (cheque == null) {
                continue;
            }


            /*
             * DATA ENTRY VALIDATION
             */
            if (hasDataEntryError(cheque)) {

                dataEntryErrors++;
            }


            /*
             * MICR VALIDATION
             */
            if (hasMicrError(cheque)) {

                micrErrors++;
            }


            /*
             * AMOUNT / ACCOUNT VALIDATION
             */
            if (hasAmountAccountError(cheque)) {

                amountAccountErrors++;
            }
        }


        result.setDataEntryErrors(
                dataEntryErrors
        );

        result.setMicrErrors(
                micrErrors
        );

        result.setAmountAccountErrors(
                amountAccountErrors
        );


        return result;
    }


    // =========================================================
    // DATA ENTRY VALIDATION
    // =========================================================

    private boolean hasDataEntryError(
            OutwardCheque cheque) {

        /*
         * CHEQUE NUMBER
         */
        String chequeNumber =
                cheque.getChequeNumber();

        if (isBlank(chequeNumber)) {

            return true;
        }


        /*
         * CHEQUE DATE
         */
        String chequeDate =
                cheque.getChequeDate();

        if (isBlank(chequeDate)) {

            return true;
        }


        /*
         * DATE FORMAT
         */
        try {

            LocalDate.parse(
                    chequeDate
            );

        } catch (DateTimeParseException e) {

            return true;
        }


        return false;
    }


    // =========================================================
    // MICR VALIDATION
    // =========================================================

    private boolean hasMicrError(
            OutwardCheque cheque) {

        String micr =
                cheque.getMicr();


        /*
         * MICR cannot be empty.
         */
        if (isBlank(micr)) {

            return true;
        }


        /*
         * MICR must contain exactly 9 digits.
         */
        if (!micr.matches(
                "^[0-9]{9}$")) {

            return true;
        }


        return false;
    }


    // =========================================================
    // ACCOUNT / AMOUNT VALIDATION
    // =========================================================

    private boolean hasAmountAccountError(
            OutwardCheque cheque) {


        // -----------------------------------------------------
        // ACCOUNT NUMBER
        // -----------------------------------------------------

        String accountNumber =
                cheque.getAccountNumber();

        if (isBlank(accountNumber)) {

            return true;
        }


        /*
         * Current project rule:
         * account number = exactly 12 digits.
         */
        if (!accountNumber.matches(
                "^[0-9]{12}$")) {

            return true;
        }


        // -----------------------------------------------------
        // AMOUNT
        // -----------------------------------------------------

        String amount =
                cheque.getAmount();

        if (isBlank(amount)) {

            return true;
        }


        try {

            BigDecimal amountValue =
                    new BigDecimal(amount);


            /*
             * Amount must be greater than zero.
             */
            if (amountValue.compareTo(
                    BigDecimal.ZERO) <= 0) {

                return true;
            }

        } catch (NumberFormatException e) {

            return true;
        }


        return false;
    }


    // =========================================================
    // BLANK VALIDATION
    // =========================================================

    private boolean isBlank(
            String value) {

        return value == null
                || value.trim().isEmpty();
    }
}

