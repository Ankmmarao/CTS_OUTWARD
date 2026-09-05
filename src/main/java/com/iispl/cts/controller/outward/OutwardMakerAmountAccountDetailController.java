package com.iispl.cts.controller.outward;

import java.util.List;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Wire;

import org.zkoss.zul.Image;
import org.zkoss.zul.Label;
import org.zkoss.zul.Textbox;

import com.iispl.cts.model.outward.OutwardCheque;
import com.iispl.cts.service.outward.OutwardMakerAmountAccountService;

public class OutwardMakerAmountAccountDetailController
        extends SelectorComposer<Component> {

    private static final long serialVersionUID = 1L;

    // Batch ID coming from URL
    private String batchId;

    // List of cheques for this batch
    private List<OutwardCheque> cheques;

    // Current cheque position
    private int currentIndex;

    // Service object
    private OutwardMakerAmountAccountService service;


    // =====================================================
    // ZUL COMPONENTS
    // =====================================================

    @Wire
    private Label batchLabel;

    @Wire
    private Label chequeCountLabel;

    @Wire
    private Image chequeImage;

    @Wire
    private Textbox accountNumberTextbox;

    @Wire
    private Textbox chequeDateTextbox;

    @Wire
    private Textbox amountTextbox;


    // =====================================================
    // PAGE LOAD
    // =====================================================

    @Override
    public void doAfterCompose(Component comp) throws Exception {

        // Call parent method
        super.doAfterCompose(comp);

        // Create service object
        service =
                new OutwardMakerAmountAccountService();

        // Get batchId from URL
        batchId =
                Executions.getCurrent()
                        .getParameter("batchId");

        System.out.println(
                "BATCH ID RECEIVED: "
                + batchId);

        // Get cheques from database
        cheques =
                service.getAmountAccountCheques(batchId);

        System.out.println(
                "TOTAL CHEQUES: "
                + cheques.size());

        // Start from first cheque
        currentIndex = 0;

        // Display first cheque
        displayCurrentCheque();
    }


    // =====================================================
    // DISPLAY CURRENT CHEQUE
    // =====================================================

    private void displayCurrentCheque() {

        // Check whether cheques are available
        if (cheques == null || cheques.isEmpty()) {

            System.out.println("NO CHEQUES FOUND");

            chequeImage.setSrc("/images/no-image.png");

            return;
        }


        // Get the current cheque
        OutwardCheque cheque = cheques.get(currentIndex);


        // Display Batch ID
        batchLabel.setValue(batchId);


        // Display Cheque Count
        chequeCountLabel.setValue(
                "Cheque "
                + (currentIndex + 1)
                + " of "
                + cheques.size()
        );


        // Display Account Number
        accountNumberTextbox.setValue(
                cheque.getAccountNumber()
        );


        // Display Cheque Date
        chequeDateTextbox.setValue(
                cheque.getChequeDate()
        );


        // Display Amount
        amountTextbox.setValue(
                cheque.getAmount()
        );


        // ================================
        // DISPLAY CHEQUE IMAGE
        // ================================

        String imageName = cheque.getFrontImage();


        System.out.println(
                "IMAGE FROM DATABASE: " + imageName
        );


        // If image value is empty
        if (imageName == null || imageName.trim().isEmpty()) {

            chequeImage.setSrc("/images/no-image.png");

            return;
        }


        // Remove extra spaces
        imageName = imageName.trim();


        // If database contains only filename
        // Example: cheque1.png
        if (!imageName.startsWith("http://")
                && !imageName.startsWith("https://")
                && !imageName.startsWith("/")) {

            imageName = "/images/" + imageName;
        }


        // Display image
        chequeImage.setSrc(imageName);


        System.out.println(
                "FINAL IMAGE PATH: " + imageName
        );
    }
}