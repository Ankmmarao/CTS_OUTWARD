package com.iispl.cts.service.outward;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import com.iispl.cts.model.outward.OutwardCheque;
import com.iispl.cts.model.outward.OutwardValidationResult;

public class OutwardValidationService {

    /*
     * Validate all cheques in a batch.
     *
     * Validation is performed using the actual cheque data.
     *
     * City Code   -> exactly 3 digits
     * Bank Code   -> exactly 3 digits
     * Branch Code -> exactly 3 digits
     *
     * Any alphabet, special character, blank value,
     * less than 3 digits or more than 3 digits = error.
     *
     * IMPORTANT:
     * No old MICR validation is used.
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
        int amountAccountErrors = 0;

        for (OutwardCheque cheque : cheques) {

            if (cheque == null) {
                continue;
            }

            /*
             * DATA / CITY / BANK / BRANCH VALIDATION
             */
            if (hasDataEntryError(cheque)) {
                dataEntryErrors++;
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

        /*
         * MICR is no longer validated.
         */
        result.setMicrErrors(0);

        result.setAmountAccountErrors(
                amountAccountErrors
        );

        return result;
    }


    // =========================================================
    // DATA ENTRY / CITY / BANK / BRANCH VALIDATION
    // =========================================================

    private boolean hasDataEntryError(
            OutwardCheque cheque) {

        /*
         * -----------------------------------------------------
         * CHEQUE NUMBER
         * -----------------------------------------------------
         */

        String chequeNumber =
                cheque.getChequeNumber();

        if (isBlank(chequeNumber)) {
            return true;
        }


        /*
         * -----------------------------------------------------
         * CHEQUE DATE
         * -----------------------------------------------------
         *
         * chequeDate is already LocalDate.
         *
         * No parsing is required.
         */

        LocalDate chequeDate =
                cheque.getChequeDate();

        if (chequeDate == null) {
            return true;
        }


        /*
         * -----------------------------------------------------
         * CITY CODE
         * -----------------------------------------------------
         *
         * Must contain exactly 3 digits.
         *
         * 123   -> VALID
         * 12    -> ERROR
         * 1234  -> ERROR
         * ABC   -> ERROR
         * 1A3   -> ERROR
         * 1@3   -> ERROR
         */

        String cityCode =
                cheque.getCityCode();

        if (!isExactlyThreeDigits(cityCode)) {
            return true;
        }


        /*
         * -----------------------------------------------------
         * BANK CODE
         * -----------------------------------------------------
         *
         * Must contain exactly 3 digits.
         */

        String bankCode =
                cheque.getBankCode();

        if (!isExactlyThreeDigits(bankCode)) {
            return true;
        }


        /*
         * -----------------------------------------------------
         * BRANCH CODE
         * -----------------------------------------------------
         *
         * Must contain exactly 3 digits.
         */

        String branchCode =
                cheque.getBranchCode();

        if (!isExactlyThreeDigits(branchCode)) {
            return true;
        }


        return false;
    }


    // =========================================================
    // AMOUNT / ACCOUNT VALIDATION
    // =========================================================

    private boolean hasAmountAccountError(
            OutwardCheque cheque) {

        /*
         * -----------------------------------------------------
         * DRAWER ACCOUNT NUMBER
         * -----------------------------------------------------
         */

        String drawerAccountNumber =
                cheque.getDrawerAccountNumber();

        if (isBlank(drawerAccountNumber)) {
            return true;
        }

        /*
         * Account number = exactly 12 digits.
         */

        if (!drawerAccountNumber.matches(
                "^[0-9]{12}$")) {

            return true;
        }


        /*
         * -----------------------------------------------------
         * PAYEE ACCOUNT NUMBER
         * -----------------------------------------------------
         */

        String payeeAccountNumber =
                cheque.getDepositorAccountNumber();

        if (isBlank(payeeAccountNumber)) {
            return true;
        }

        /*
         * Payee account number = exactly 12 digits.
         */

        if (!payeeAccountNumber.matches(
                "^[0-9]{12}$")) {

            return true;
        }


        /*
         * -----------------------------------------------------
         * AMOUNT
         * -----------------------------------------------------
         */
     // -----------------------------------------------------
     // AMOUNT
     // -----------------------------------------------------

     BigDecimal amount =
             cheque.getAmount();

     if (amount == null) {
         return true;
     }

     /*
      * Amount must be greater than zero.
      */
     if (amount.compareTo(
             BigDecimal.ZERO) <= 0) {

         return true;
     }

     return false;
    }

    // =========================================================
    // EXACTLY 3 DIGITS
    // =========================================================

    private boolean isExactlyThreeDigits(
            String value) {

        if (value == null) {
            return false;
        }

        /*
         * Exactly 3 numeric digits.
         *
         * 123    -> VALID
         * 12     -> ERROR
         * 1234   -> ERROR
         * 1A3    -> ERROR
         * 1@3    -> ERROR
         * ABC    -> ERROR
         * " 123" -> ERROR
         * "123 " -> ERROR
         */

        return value.matches(
                "^[0-9]{3}$"
        );
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