package com.iispl.cts.service.outward.checker;

import java.util.List;
import java.util.Map;

import com.iispl.cts.dao.outward.checker.CheckerChequeDAO;
import com.iispl.cts.model.outward.ChequeProcessing;
import com.iispl.cts.model.outward.OutwardCheque;
import com.iispl.cts.model.outward.ReturnReason;

public class CheckerProcessingService {

    private final CheckerChequeDAO chequeDao;

    public CheckerProcessingService() {
        this.chequeDao = new CheckerChequeDAO();
    }

    /*
     * Get current cheque.
     */
    public OutwardCheque getCheque(
            String batchNumber,
            String chequeNumber) {

        return chequeDao.getCheque(
                batchNumber,
                chequeNumber);
    }

    /*
     * Get Maker/Checker processing details.
     */
    public ChequeProcessing getChequeProcessing(
            String batchNumber,
            String chequeNumber) {

        return chequeDao.getChequeProcessing(
                batchNumber,
                chequeNumber);
    }

    /*
     * Validate presenting account in CBS.
     *
     * ACCOUNT_NOT_FOUND
     * ACCOUNT_INACTIVE
     * PASS
     */
    public String validateCbsAccount(
            String accountNumber) {

        Map<String, String> account =
                chequeDao.getCbsAccount(accountNumber);

        if (account == null) {
            return "ACCOUNT_NOT_FOUND";
        }

        String accountStatus =
                account.get("accountStatus");

        if (!"ACTIVE".equalsIgnoreCase(accountStatus)) {
            return "ACCOUNT_INACTIVE";
        }

        return "PASS";
    }

    /*
     * Get CBS validation message for UI.
     */
    public String getCbsValidationMessage(
            String cbsResult) {

        if ("ACCOUNT_NOT_FOUND".equals(cbsResult)) {
            return "Presenting account does not exist in CBS records.";
        }

        if ("ACCOUNT_INACTIVE".equals(cbsResult)) {
            return "Presenting account is inactive.";
        }

        if ("PASS".equals(cbsResult)) {
            return "Presenting account exists and is active.";
        }

        return "CBS validation failed.";
    }

    /*
     * Accept is allowed only when CBS validation passes.
     */
    public boolean isAcceptAllowed(
            String cbsResult) {

        return "PASS".equals(cbsResult);
    }

    /*
     * Get active Reject / Send Back reasons.
     */
    public List<ReturnReason> getReturnReasons() {

        return chequeDao.getReturnReasons();
    }

    /*
     * Save Checker decision.
     *
     * ACCEPT:
     *     Allowed only when CBS validation passed.
     *
     * REJECT:
     *     Reason mandatory.
     *
     * SEND_BACK:
     *     Reason mandatory.
     */
    public boolean saveCheckerDecision(
            String batchNumber,
            String chequeNumber,
            int checkerId,
            String checkerAction,
            Integer checkerReasonId,
            String cbsResult) {

        /*
         * Do not allow ACCEPT when CBS validation failed.
         */
        if ("ACCEPT".equalsIgnoreCase(checkerAction)
                && !isAcceptAllowed(cbsResult)) {

            return false;
        }

        return chequeDao.saveCheckerDecision(
                batchNumber,
                chequeNumber,
                checkerId,
                checkerAction,
                checkerReasonId);
    }
}