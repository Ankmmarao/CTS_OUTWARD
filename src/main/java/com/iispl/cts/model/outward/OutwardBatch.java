package com.iispl.cts.model.outward;

public class OutwardBatch {

    private String batchId;
    private int totalCheques;
    private int errorCount;

    private int dataEntryErrorCount;
    private int micrErrorCount;
    private int amountAccountErrorCount;

    private String status;
    private String userId;
    private String assignment;

    public OutwardBatch() {
    }

    public OutwardBatch(
            String batchId,
            int totalCheques,
            int errorCount,
            String status) {

        this.batchId = batchId;
        this.totalCheques = totalCheques;
        this.errorCount = errorCount;
        this.status = status;
    }

    public String getBatchId() {
        return batchId;
    }

    public void setBatchId(String batchId) {
        this.batchId = batchId;
    }

    public int getTotalCheques() {
        return totalCheques;
    }

    public void setTotalCheques(int totalCheques) {
        this.totalCheques = totalCheques;
    }

    public int getErrorCount() {
        return errorCount;
    }

    public void setErrorCount(int errorCount) {
        this.errorCount = errorCount;
    }

    public int getDataEntryErrorCount() {
        return dataEntryErrorCount;
    }

    public void setDataEntryErrorCount(int dataEntryErrorCount) {
        this.dataEntryErrorCount = dataEntryErrorCount;
    }

    public int getMicrErrorCount() {
        return micrErrorCount;
    }

    public void setMicrErrorCount(int micrErrorCount) {
        this.micrErrorCount = micrErrorCount;
    }

    public int getAmountAccountErrorCount() {
        return amountAccountErrorCount;
    }

    public void setAmountAccountErrorCount(
            int amountAccountErrorCount) {

        this.amountAccountErrorCount =
                amountAccountErrorCount;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getAssignment() {
        return assignment;
    }

    public void setAssignment(String assignment) {
        this.assignment = assignment;
    }

    public void refreshTotalErrorCount() {

        this.errorCount =
                dataEntryErrorCount
                + micrErrorCount
                + amountAccountErrorCount;
    }
}