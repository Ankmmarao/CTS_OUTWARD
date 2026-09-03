
package com.iispl.cts.model.outward;

public class OutwardCheque {

    private String batchId;

    private String chequeId;

    private String chequeNumber;

    private String accountNumber;

    private String chequeDate;

    private String amount;

    /*
     * Original MICR value coming from outward_cheque.scanned_micr
     */
    private String micr;

    /*
     * Kept for compatibility with existing code.
     *
     * IMPORTANT:
     * Validation will no longer depend on errorType.
     * Error type will be determined from actual cheque values.
     */
    private String errorType;

    private String frontImage;

    private String backImage;

    /*
     * Corrected values entered during repair.
     */
    private String correctAccountNumber;

    private String correctChequeDate;

    private String correctAmount;

    private String correctMicr;

    private String repairReason;

    private String remarks;

    private String rejectReason;

    private boolean micrCorrected;

    private boolean frontVerified;

    private boolean backVerified;

    private boolean saved;

    private boolean rejected;


    // =========================================================
    // BATCH ID
    // =========================================================

    public String getBatchId() {
        return batchId;
    }

    public void setBatchId(String batchId) {
        this.batchId = batchId;
    }


    // =========================================================
    // CHEQUE ID
    // =========================================================

    public String getChequeId() {
        return chequeId;
    }

    public void setChequeId(String chequeId) {
        this.chequeId = chequeId;
    }


    // =========================================================
    // CHEQUE NUMBER
    // =========================================================

    public String getChequeNumber() {
        return chequeNumber;
    }

    public void setChequeNumber(String chequeNumber) {
        this.chequeNumber = chequeNumber;
    }


    // =========================================================
    // ACCOUNT NUMBER
    // =========================================================

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }


    // =========================================================
    // CHEQUE DATE
    // =========================================================

    public String getChequeDate() {
        return chequeDate;
    }

    public void setChequeDate(String chequeDate) {
        this.chequeDate = chequeDate;
    }


    // =========================================================
    // AMOUNT
    // =========================================================

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }


    // =========================================================
    // ORIGINAL MICR
    // =========================================================

    public String getMicr() {
        return micr;
    }

    public void setMicr(String micr) {
        this.micr = micr;
    }


    // =========================================================
    // ERROR TYPE
    // =========================================================

    public String getErrorType() {
        return errorType;
    }

    public void setErrorType(String errorType) {
        this.errorType = errorType;
    }


    // =========================================================
    // FRONT IMAGE
    // =========================================================

    public String getFrontImage() {
        return frontImage;
    }

    public void setFrontImage(String frontImage) {
        this.frontImage = frontImage;
    }


    // =========================================================
    // BACK IMAGE
    // =========================================================

    public String getBackImage() {
        return backImage;
    }

    public void setBackImage(String backImage) {
        this.backImage = backImage;
    }


    // =========================================================
    // CORRECT ACCOUNT NUMBER
    // =========================================================

    public String getCorrectAccountNumber() {
        return correctAccountNumber;
    }

    public void setCorrectAccountNumber(String value) {
        this.correctAccountNumber = value;
    }


    // =========================================================
    // CORRECT CHEQUE DATE
    // =========================================================

    public String getCorrectChequeDate() {
        return correctChequeDate;
    }

    public void setCorrectChequeDate(String value) {
        this.correctChequeDate = value;
    }


    // =========================================================
    // CORRECT AMOUNT
    // =========================================================

    public String getCorrectAmount() {
        return correctAmount;
    }

    public void setCorrectAmount(String value) {
        this.correctAmount = value;
    }


    // =========================================================
    // CORRECT MICR
    // =========================================================

    public String getCorrectMicr() {
        return correctMicr;
    }

    public void setCorrectMicr(String value) {
        this.correctMicr = value;
    }


    // =========================================================
    // REPAIR REASON
    // =========================================================

    public String getRepairReason() {
        return repairReason;
    }

    public void setRepairReason(String value) {
        this.repairReason = value;
    }


    // =========================================================
    // REMARKS
    // =========================================================

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String value) {
        this.remarks = value;
    }


    // =========================================================
    // REJECT REASON
    // =========================================================

    public String getRejectReason() {
        return rejectReason;
    }

    public void setRejectReason(String value) {
        this.rejectReason = value;
    }


    // =========================================================
    // MICR CORRECTED
    // =========================================================

    public boolean isMicrCorrected() {
        return micrCorrected;
    }

    public void setMicrCorrected(boolean value) {
        this.micrCorrected = value;
    }


    // =========================================================
    // FRONT VERIFIED
    // =========================================================

    public boolean isFrontVerified() {
        return frontVerified;
    }

    public void setFrontVerified(boolean value) {
        this.frontVerified = value;
    }


    // =========================================================
    // BACK VERIFIED
    // =========================================================

    public boolean isBackVerified() {
        return backVerified;
    }

    public void setBackVerified(boolean value) {
        this.backVerified = value;
    }


    // =========================================================
    // SAVED
    // =========================================================

    public boolean isSaved() {
        return saved;
    }

    public void setSaved(boolean value) {
        this.saved = value;
    }


    // =========================================================
    // REJECTED
    // =========================================================

    public boolean isRejected() {
        return rejected;
    }

    public void setRejected(boolean value) {
        this.rejected = value;
    }
}

