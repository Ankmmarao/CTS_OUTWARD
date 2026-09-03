package com.iispl.cts.model.outward;

public class OutwardCheque {

    private String batchId;
    private String chequeId;
    private String chequeNumber;

    private String accountNumber;
    private String chequeDate;
    private String amount;

    private String errorType;

    private String frontImage;
    private String backImage;

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

    public String getBatchId() {
        return batchId;
    }

    public void setBatchId(String batchId) {
        this.batchId = batchId;
    }

    public String getChequeId() {
        return chequeId;
    }

    public void setChequeId(String chequeId) {
        this.chequeId = chequeId;
    }

    public String getChequeNumber() {
        return chequeNumber;
    }

    public void setChequeNumber(String chequeNumber) {
        this.chequeNumber = chequeNumber;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public String getChequeDate() {
        return chequeDate;
    }

    public void setChequeDate(String chequeDate) {
        this.chequeDate = chequeDate;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getErrorType() {
        return errorType;
    }

    public void setErrorType(String errorType) {
        this.errorType = errorType;
    }

    public String getFrontImage() {
        return frontImage;
    }

    public void setFrontImage(String frontImage) {
        this.frontImage = frontImage;
    }

    public String getBackImage() {
        return backImage;
    }

    public void setBackImage(String backImage) {
        this.backImage = backImage;
    }

    public String getCorrectAccountNumber() {
        return correctAccountNumber;
    }

    public void setCorrectAccountNumber(String value) {
        this.correctAccountNumber = value;
    }

    public String getCorrectChequeDate() {
        return correctChequeDate;
    }

    public void setCorrectChequeDate(String value) {
        this.correctChequeDate = value;
    }

    public String getCorrectAmount() {
        return correctAmount;
    }

    public void setCorrectAmount(String value) {
        this.correctAmount = value;
    }

    public String getCorrectMicr() {
        return correctMicr;
    }

    public void setCorrectMicr(String value) {
        this.correctMicr = value;
    }

    public String getRepairReason() {
        return repairReason;
    }

    public void setRepairReason(String value) {
        this.repairReason = value;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String value) {
        this.remarks = value;
    }

    public String getRejectReason() {
        return rejectReason;
    }

    public void setRejectReason(String value) {
        this.rejectReason = value;
    }

    public boolean isMicrCorrected() {
        return micrCorrected;
    }

    public void setMicrCorrected(boolean value) {
        this.micrCorrected = value;
    }

    public boolean isFrontVerified() {
        return frontVerified;
    }

    public void setFrontVerified(boolean value) {
        this.frontVerified = value;
    }

    public boolean isBackVerified() {
        return backVerified;
    }

    public void setBackVerified(boolean value) {
        this.backVerified = value;
    }

    public boolean isSaved() {
        return saved;
    }

    public void setSaved(boolean value) {
        this.saved = value;
    }

    public boolean isRejected() {
        return rejected;
    }

    public void setRejected(boolean value) {
        this.rejected = value;
    }
}