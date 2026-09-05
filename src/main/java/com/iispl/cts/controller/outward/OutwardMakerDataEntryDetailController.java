package com.iispl.cts.controller.outward;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Button;
import org.zkoss.zul.Decimalbox;
import org.zkoss.zul.Image;
import org.zkoss.zul.Label;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Vlayout;
import org.zkoss.zul.Window;

import com.iispl.cts.model.outward.OutwardCheque;
import com.iispl.cts.service.outward.OutwardMakerDataEntryDetailService;

public class OutwardMakerDataEntryDetailController extends SelectorComposer<Component> {

    private static final long serialVersionUID = 1L;

    @Wire
    private Label batchIdLabel;

    @Wire
    private Label chequeProgressLabel;

    @Wire
    private Textbox accountNumberTextbox;

    @Wire
    private Textbox chequeDateTextbox;

    @Wire
    private Decimalbox amountTextbox;

    @Wire
    private Textbox drawerNameTextbox;

    @Wire
    private Textbox payeeNameTextbox;

    @Wire
    private Image frontImage;

    @Wire
    private Image backImage;

    @Wire
    private Button prevButton;

    @Wire
    private Button nextButton;

    private OutwardMakerDataEntryDetailService service;

    private List<OutwardCheque> cheques;

    private int currentIndex = 0;

    private String batchId;

    @Override
    public void doAfterCompose(Component comp) throws Exception {
        super.doAfterCompose(comp);

        service = new OutwardMakerDataEntryDetailService();

        batchId = Executions.getCurrent().getParameter("batchId");

        if (batchId == null || batchId.trim().isEmpty()) {
            batchId = "BATCH001";
        }

        cheques = service.getCheques(batchId);

        if (cheques == null || cheques.isEmpty()) {
            Messagebox.show(
                    "No cheque data available for batch " + batchId + ".",
                    "Information",
                    Messagebox.OK,
                    Messagebox.INFORMATION
            );
            return;
        }

        loadCheque();
    }

    private void loadCheque() {
        if (cheques == null || cheques.isEmpty()) {
            return;
        }

        OutwardCheque cheque = cheques.get(currentIndex);

        // 1. Batch & Progress Info
        if (batchIdLabel != null) {
            batchIdLabel.setValue(cheque.getBatchNumber());
        }

        if (chequeProgressLabel != null) {
            chequeProgressLabel.setValue(
                    "Cheque " + (currentIndex + 1) + " of " + cheques.size()
            );
        }

        // 2. Account Number
        if (accountNumberTextbox != null) {
            accountNumberTextbox.setValue(
                    cheque.getDrawerAccountNumber() != null ? cheque.getDrawerAccountNumber() : ""
            );
        }

        // 3. Cheque Date (LocalDate -> String for Textbox)
        if (chequeDateTextbox != null) {
            chequeDateTextbox.setValue(
                    cheque.getChequeDate() != null ? cheque.getChequeDate().toString() : ""
            );
        }

        // 4. Amount (Direct BigDecimal assignment to Decimalbox)
        if (amountTextbox != null) {
            amountTextbox.setValue(cheque.getAmount());
        }

        // 5. Optional Name Fields (if wired in ZUL)
        if (drawerNameTextbox != null) {
            drawerNameTextbox.setValue(cheque.getDrawerName() != null ? cheque.getDrawerName() : "");
        }
        if (payeeNameTextbox != null) {
            payeeNameTextbox.setValue(cheque.getPayeeName() != null ? cheque.getPayeeName() : "");
        }

        // 6. Optional Image Paths (if wired in ZUL)
        if (frontImage != null && cheque.getFrontImagePath() != null) {
            frontImage.setSrc(cheque.getFrontImagePath());
        }
        if (backImage != null && cheque.getBackImagePath() != null) {
            backImage.setSrc(cheque.getBackImagePath());
        }

        updateButtons();
    }

    private void updateButtons() {
        if (prevButton != null) {
            prevButton.setDisabled(currentIndex == 0);
        }

        if (nextButton != null) {
            nextButton.setDisabled(currentIndex >= cheques.size() - 1);
        }
    }

    @Listen("onClick = #prevButton")
    public void previousCheque() {
        if (currentIndex > 0) {
            currentIndex--;
            loadCheque();
        }
    }

    @Listen("onClick = #nextButton")
    public void nextCheque() {
        if (currentIndex < cheques.size() - 1) {
            currentIndex++;
            loadCheque();
        }
    }

    @Listen("onClick = #saveNextButton")
    public void saveAndNext() {
        OutwardCheque cheque = cheques.get(currentIndex);

        String account = accountNumberTextbox != null ? accountNumberTextbox.getValue() : null;
        String date = chequeDateTextbox != null ? chequeDateTextbox.getValue() : null;
        BigDecimal amount = amountTextbox != null ? amountTextbox.getValue() : null;

        // Validations
        if (account == null || account.trim().isEmpty()) {
            Messagebox.show("Please enter Account Number.", "Validation", Messagebox.OK, Messagebox.EXCLAMATION);
            return;
        }

        if (date == null || date.trim().isEmpty()) {
            Messagebox.show("Please enter Cheque Date.", "Validation", Messagebox.OK, Messagebox.EXCLAMATION);
            return;
        }

        if (amount == null) {
            Messagebox.show("Please enter Amount.", "Validation", Messagebox.OK, Messagebox.EXCLAMATION);
            return;
        }

        // Apply changes to current OutwardCheque model
        cheque.setDrawerAccountNumber(account.trim());

        try {
            cheque.setChequeDate(LocalDate.parse(date.trim()));
        } catch (Exception e) {
            Messagebox.show("Invalid Cheque Date format. Expected YYYY-MM-DD.", "Validation", Messagebox.OK, Messagebox.EXCLAMATION);
            return;
        }

        cheque.setAmount(amount);

        // Persist via Service / DAO
        service.saveCheque(cheque);

        // Advance or Complete
        if (currentIndex < cheques.size() - 1) {
            currentIndex++;
            loadCheque();
        } else {
            Messagebox.show(
                    "All cheques in batch " + batchId + " have been completed.",
                    "Batch Completed",
                    Messagebox.OK,
                    Messagebox.INFORMATION,
                    event -> Executions.sendRedirect("outward-maker-data-entry.zul")
            );
        }
    }

    @Listen("onClick = #rejectButton")
    public void rejectCheque() {
        Window rejectWindow = new Window();
        rejectWindow.setTitle("Reject Cheque");
        rejectWindow.setWidth("450px");
        rejectWindow.setClosable(true);
        rejectWindow.setBorder("normal");
        rejectWindow.setParent(
                Executions.getCurrent().getDesktop().getFirstPage().getFirstRoot()
        );

        Vlayout layout = new Vlayout();
        layout.setSpacing("12px");
        layout.setStyle("padding:20px;");

        Label label = new Label("Enter rejection reason:");
        Textbox reasonBox = new Textbox();
        reasonBox.setRows(4);
        reasonBox.setWidth("100%");

        Button confirm = new Button("Confirm Reject");
        confirm.setWidth("140px");

        confirm.addEventListener("onClick", event -> {
            String reason = reasonBox.getValue();
            if (reason == null || reason.trim().isEmpty()) {
                Messagebox.show("Please enter rejection reason.", "Validation", Messagebox.OK, Messagebox.EXCLAMATION);
                return;
            }

            OutwardCheque cheque = cheques.get(currentIndex);
            service.rejectCheque(cheque, reason.trim());

            rejectWindow.detach();

            Messagebox.show(
                    "Cheque " + (currentIndex + 1) + " rejected.",
                    "Rejected",
                    Messagebox.OK,
                    Messagebox.INFORMATION,
                    e -> Executions.sendRedirect("outward-maker-data-entry.zul")
            );
        });

        layout.appendChild(label);
        layout.appendChild(reasonBox);
        layout.appendChild(confirm);
        rejectWindow.appendChild(layout);

        rejectWindow.doModal();
    }

    @Listen("onClick = #backButton")
    public void backToList() {
        Executions.sendRedirect("outward-maker-data-entry.zul");
    }
}