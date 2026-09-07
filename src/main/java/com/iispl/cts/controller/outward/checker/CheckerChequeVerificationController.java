package com.iispl.cts.controller.outward.checker;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.zkoss.image.AImage;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Button;
import org.zkoss.zul.Image;
import org.zkoss.zul.Label;

import com.iispl.cts.model.outward.OutwardCheque;
import com.iispl.cts.service.outward.checker.CheckerBatchService;

public class CheckerChequeVerificationController
        extends SelectorComposer<Component> {

    private static final long serialVersionUID = 1L;


    // =========================================================
    // ZUL COMPONENTS
    // =========================================================

    @Wire
    private Label verificationBatchId;

    @Wire
    private Label chequeSequence;

    @Wire
    private Image chequeImage;

    @Wire
    private Label chequeNumberLabel;

    @Wire
    private Label accountNumberLabel;

    @Wire
    private Label drawerNameLabel;

    @Wire
    private Label payeeNameLabel;

    @Wire
    private Label amountLabel;

    @Wire
    private Label amountInWordsLabel;

    @Wire
    private Label chequeDateLabel;

    @Wire
    private Label micrLabel;

    @Wire
    private Label statusLabel;

    @Wire
    private Button previousButton;

    @Wire
    private Button nextButton;


    // =========================================================
    // VARIABLES
    // =========================================================

    private CheckerBatchService batchService;

    private String batchNumber;

    private String selectedChequeNumber;

    private List<OutwardCheque> chequeList;

    private int currentIndex = 0;


    // =========================================================
    // INITIALIZE
    // =========================================================

    @Override
    public void doAfterCompose(Component comp)
            throws Exception {

        super.doAfterCompose(comp);

        System.out.println("==========================================");
        System.out.println("CHEQUE VERIFICATION CONTROLLER STARTED");
        System.out.println("==========================================");


        // Create Service

        batchService = new CheckerBatchService();


        // Get Batch Number from URL

        batchNumber = Executions.getCurrent()
                .getParameter("batchId");


        // Get Selected Cheque Number from URL

        selectedChequeNumber = Executions.getCurrent()
                .getParameter("chequeNumber");


        System.out.println("BATCH NUMBER = " + batchNumber);
        System.out.println("SELECTED CHEQUE = " + selectedChequeNumber);


        // Validate Batch Number

        if (batchNumber == null
                || batchNumber.trim().isEmpty()) {

            System.out.println("BATCH NUMBER IS EMPTY");

            goBackToQueue();

            return;
        }


        // Load Cheques

        loadCheques();
    }


    // =========================================================
    // LOAD ALL CHEQUES FROM BATCH
    // =========================================================

    private void loadCheques() {

        try {

            System.out.println("==========================================");
            System.out.println("LOADING CHEQUES FOR BATCH");
            System.out.println("BATCH = " + batchNumber);
            System.out.println("==========================================");


            chequeList = batchService
                    .getChequesByBatchNumber(batchNumber);


            // Null Safety

            if (chequeList == null) {

                chequeList = new ArrayList<>();
            }


            System.out.println(
                    "TOTAL CHEQUES FOUND = "
                            + chequeList.size()
            );


            // No Cheques

            if (chequeList.isEmpty()) {

                System.out.println(
                        "NO CHEQUES FOUND"
                );

                return;
            }


            // =================================================
            // FIND THE CHEQUE USER CLICKED OPEN ON
            // =================================================

            if (selectedChequeNumber != null
                    && !selectedChequeNumber.trim().isEmpty()) {

                for (int i = 0;
                        i < chequeList.size();
                        i++) {

                    String chequeNumber =
                            chequeList.get(i)
                                    .getChequeNumber();


                    if (selectedChequeNumber.equals(
                            chequeNumber)) {

                        currentIndex = i;

                        System.out.println(
                                "SELECTED CHEQUE FOUND AT INDEX = "
                                        + currentIndex
                        );

                        break;
                    }
                }
            }


            // Display Selected Cheque

            displayCurrentCheque();


        } catch (Exception e) {

            e.printStackTrace();
        }
    }


    // =========================================================
    // DISPLAY CURRENT CHEQUE
    // =========================================================

    private void displayCurrentCheque() {

        if (chequeList == null
                || chequeList.isEmpty()) {

            return;
        }


        OutwardCheque cheque =
                chequeList.get(currentIndex);


        System.out.println("==========================================");
        System.out.println("DISPLAYING CHEQUE");
        System.out.println(
                "CHEQUE NUMBER = "
                        + cheque.getChequeNumber()
        );
        System.out.println(
                "CURRENT INDEX = "
                        + currentIndex
        );
        System.out.println("==========================================");


        // =====================================================
        // BATCH ID
        // =====================================================

        verificationBatchId.setValue(
                batchNumber
        );


        // =====================================================
        // CHEQUE POSITION
        // =====================================================

        chequeSequence.setValue(

                "Cheque "
                        + (currentIndex + 1)
                        + " of "
                        + chequeList.size()
        );


        // =====================================================
        // CHEQUE NUMBER
        // =====================================================

        chequeNumberLabel.setValue(

                safe(
                        cheque.getChequeNumber()
                )
        );


        // =====================================================
        // ACCOUNT NUMBER
        // =====================================================

        accountNumberLabel.setValue(

                safe(
                        cheque.getDrawerAccountNumber()
                )
        );


        // =====================================================
        // DRAWER NAME
        // =====================================================

        drawerNameLabel.setValue(

                safe(
                        cheque.getDrawerName()
                )
        );


        // =====================================================
        // PAYEE NAME
        // =====================================================

        payeeNameLabel.setValue(

                safe(
                        cheque.getPayeeName()
                )
        );


        // =====================================================
        // AMOUNT
        // =====================================================

        if (cheque.getAmount() != null) {

            amountLabel.setValue(

                    cheque.getAmount()
                            .toPlainString()
            );

        } else {

            amountLabel.setValue("-");
        }


        // =====================================================
        // AMOUNT IN WORDS
        // =====================================================

        amountInWordsLabel.setValue(

                safe(
                        cheque.getAmountInWords()
                )
        );


        // =====================================================
        // CHEQUE DATE
        // =====================================================

        if (cheque.getChequeDate() != null) {

            chequeDateLabel.setValue(

                    cheque.getChequeDate()
                            .toString()
            );

        } else {

            chequeDateLabel.setValue("-");
        }


        // =====================================================
        // MICR
        // =====================================================

        String micr =

                safeValue(cheque.getCityCode())
                        + "-"
                        + safeValue(cheque.getBankCode())
                        + "-"
                        + safeValue(cheque.getBranchCode());


        micrLabel.setValue(micr);


        // =====================================================
        // STATUS
        // =====================================================

        statusLabel.setValue(

                getStatus(
                        cheque.getChequeStatus()
                )
        );


        // =====================================================
        // LOAD CHEQUE IMAGE
        // =====================================================

        loadChequeImage(cheque);


        // =====================================================
        // PREVIOUS BUTTON
        // =====================================================

        previousButton.setDisabled(

                currentIndex == 0
        );


        // =====================================================
        // NEXT BUTTON
        // =====================================================

        nextButton.setDisabled(

                currentIndex
                        == chequeList.size() - 1
        );
    }


    // =========================================================
    // LOAD CHEQUE IMAGE
    // =========================================================

    private void loadChequeImage(
            OutwardCheque cheque) {

        try {

            String imagePath =
                    cheque.getFrontImagePath();


            System.out.println(
                    "FRONT IMAGE PATH = "
                            + imagePath
            );


            // No Image Path

            if (imagePath == null
                    || imagePath.trim().isEmpty()) {

                chequeImage.setSrc(null);

                return;
            }


            File imageFile =
                    new File(imagePath);


            // =================================================
            // IF DATABASE CONTAINS ACTUAL FILE PATH
            // =================================================

            if (imageFile.exists()
                    && imageFile.isFile()) {

                chequeImage.setContent(

                        new AImage(imageFile)
                );

            }

            // =================================================
            // IF DATABASE CONTAINS WEB PATH
            // Example:
            // /images/cheque.jpg
            // =================================================

            else {

                chequeImage.setSrc(
                        imagePath
                );
            }


        } catch (Exception e) {

            System.out.println(
                    "ERROR LOADING CHEQUE IMAGE"
            );

            e.printStackTrace();

            chequeImage.setSrc(null);
        }
    }


    // =========================================================
    // PREVIOUS CHEQUE
    // =========================================================

    @Listen("onClick=#previousButton")
    public void previousCheque() {

        if (currentIndex > 0) {

            currentIndex--;

            displayCurrentCheque();
        }
    }


    // =========================================================
    // NEXT CHEQUE
    // =========================================================

    @Listen("onClick=#nextButton")
    public void nextCheque() {

        if (currentIndex
                < chequeList.size() - 1) {

            currentIndex++;

            displayCurrentCheque();
        }
    }


    // =========================================================
    // BACK TO BATCH
    // =========================================================

    @Listen("onClick=#backButton")
    public void backButton() {

        goBackToBatch();
    }


    // =========================================================
    // BACK TO BATCH PAGE
    // =========================================================

    private void goBackToBatch() {

        String contextPath =

                Executions.getCurrent()
                        .getContextPath();


        String url =

                contextPath
                        + "/outward/checker/"
                        + "batchVerification.zul"
                        + "?batchId="
                        + batchNumber;


        Executions.sendRedirect(url);
    }


    // =========================================================
    // BACK TO QUEUE
    // =========================================================

    private void goBackToQueue() {

        String contextPath =

                Executions.getCurrent()
                        .getContextPath();


        Executions.sendRedirect(

                contextPath
                        + "/outward/checker/"
                        + "batchesQueue.zul"
        );
    }


    // =========================================================
    // STATUS
    // =========================================================

    private String getStatus(
            String status) {

        if (status == null
                || status.trim().isEmpty()) {

            return "PENDING";
        }

        return status.toUpperCase();
    }


    // =========================================================
    // SAFE STRING
    // =========================================================

    private String safe(
            String value) {

        if (value == null
                || value.trim().isEmpty()) {

            return "-";
        }

        return value;
    }


    // =========================================================
    // SAFE VALUE FOR MICR
    // =========================================================

    private String safeValue(
            String value) {

        if (value == null
                || value.trim().isEmpty()) {

            return "-";
        }

        return value;
    }
}