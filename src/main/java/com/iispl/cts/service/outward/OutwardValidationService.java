package com.iispl.cts.service.outward;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.iispl.cts.model.outward.OutwardCheque;
import com.iispl.cts.model.outward.OutwardValidationResult;

public class OutwardValidationService {

    /*
     * =========================================================
     * VALIDATE ENTIRE BATCH
     * =========================================================
     *
     * Validation categories:
     *
     * 1. DATA ENTRY
     *    - Cheque Number
     *    - Cheque Date
     *
     * 2. MICR
     *    - City Code
     *    - Bank Code
     *    - Branch Code
     *
     * 3. AMOUNT / ACCOUNT
     *    - Drawer Account Number
     *    - Payee Account Number
     *    - Amount
     *
     * MICR codes must contain exactly 3 numeric digits.
     */
    public OutwardValidationResult validate(
            List<OutwardCheque> cheques) {

        OutwardValidationResult result =
                new OutwardValidationResult();

        if (cheques == null) {

            result.setTotalCheques(0);
            result.setDataEntryErrors(0);
            result.setMicrErrors(0);
            result.setAmountAccountErrors(0);

            return result;
        }

        result.setTotalCheques(
                cheques.size()
        );

        int dataEntryErrors = 0;
        int micrErrors = 0;
        int amountAccountErrors = 0;

        /*
         * Store cheque numbers having MICR errors.
         */
        List<String> micrErrorChequeNumbers =
                new ArrayList<>();


        /*
         * Validate EVERY cheque in the batch.
         */
        for (OutwardCheque cheque : cheques) {

            if (cheque == null) {
                continue;
            }


            /*
             * -------------------------------------------------
             * DATA ENTRY
             * -------------------------------------------------
             */
            if (hasDataEntryError(cheque)) {

                dataEntryErrors++;
            }


            /*
             * -------------------------------------------------
             * MICR
             * -------------------------------------------------
             */
            if (hasMicrError(cheque)) {

                micrErrors++;

                /*
                 * Store the cheque number so that
                 * MICR module can show only this cheque.
                 */
                String chequeNumber =
                        cheque.getChequeNumber();

                if (!isBlank(chequeNumber)) {

                    micrErrorChequeNumbers.add(
                            chequeNumber
                    );
                }
            }


            /*
             * -------------------------------------------------
             * AMOUNT / ACCOUNT
             * -------------------------------------------------
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

        result.setMicrErrorChequeNumbers(
                micrErrorChequeNumbers
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
        LocalDate chequeDate =
                cheque.getChequeDate();

        if (chequeDate == null) {

            return true;
        }


        return false;
    }


    // =========================================================
    // MICR VALIDATION
    // =========================================================

    private boolean hasMicrError(
            OutwardCheque cheque) {

        /*
         * CITY CODE
         */
        String cityCode =
                cheque.getCityCode();

        if (!isExactlyThreeDigits(cityCode)) {

            return true;
        }


        /*
         * BANK CODE
         */
        String bankCode =
                cheque.getBankCode();

        if (!isExactlyThreeDigits(bankCode)) {

            return true;
        }


        /*
         * BRANCH CODE
         */
        String branchCode =
                cheque.getBranchCode();

        if (!isExactlyThreeDigits(branchCode)) {

            return true;
        }


        /*
         * All MICR fields are valid.
         */
        return false;
    }


    // =========================================================
    // AMOUNT / ACCOUNT VALIDATION
    // =========================================================

    private boolean hasAmountAccountError(
            OutwardCheque cheque) {

        /*
         * DRAWER ACCOUNT NUMBER
         */
        String drawerAccountNumber =
                cheque.getDrawerAccountNumber();

        if (isBlank(drawerAccountNumber)) {

            return true;
        }

        if (!drawerAccountNumber.matches(
                "^[0-9]{12}$")) {

            return true;
        }


        /*
         * PAYEE ACCOUNT NUMBER
         */
        String payeeAccountNumber =
                cheque.getDepositorAccountNumber();

        if (isBlank(payeeAccountNumber)) {

            return true;
        }

        if (!payeeAccountNumber.matches(
                "^[0-9]{12}$")) {

            return true;
        }


        /*
         * AMOUNT
         */
        BigDecimal amount =
                cheque.getAmount();

        if (amount == null) {

            return true;
        }

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
         * 123  -> VALID
         * 001  -> VALID
         * 987  -> VALID
         *
         * 12   -> ERROR
         * 1234 -> ERROR
         * ABC  -> ERROR
         * 12A  -> ERROR
         * A12  -> ERROR
         * 1@3  -> ERROR
         * 123  -> ERROR if it contains spaces
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