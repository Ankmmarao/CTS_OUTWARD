package com.iispl.cts.model.outward;

public class OutwardValidationResult {

    private int totalCheques;
    private int dataEntryErrors;
    private int micrErrors;
    private int amountAccountErrors;

    public OutwardValidationResult() {
    }

    public int getTotalCheques() {
        return totalCheques;
    }

    public void setTotalCheques(int totalCheques) {
        this.totalCheques = totalCheques;
    }

    public int getDataEntryErrors() {
        return dataEntryErrors;
    }

    public void setDataEntryErrors(int dataEntryErrors) {
        this.dataEntryErrors = dataEntryErrors;
    }

    public int getMicrErrors() {
        return micrErrors;
    }

    public void setMicrErrors(int micrErrors) {
        this.micrErrors = micrErrors;
    }

    public int getAmountAccountErrors() {
        return amountAccountErrors;
    }

    public void setAmountAccountErrors(
            int amountAccountErrors) {

        this.amountAccountErrors =
                amountAccountErrors;
    }

    public int getTotalErrors() {

        return dataEntryErrors
                + micrErrors
                + amountAccountErrors;
    }
}