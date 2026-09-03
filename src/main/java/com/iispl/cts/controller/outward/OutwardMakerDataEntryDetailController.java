package com.iispl.cts.controller.outward;

import java.util.List;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Button;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Decimalbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.iispl.cts.model.outward.OutwardCheque;
import com.iispl.cts.service.outward.OutwardMakerDataEntryDetailService;

public class OutwardMakerDataEntryDetailController
        extends SelectorComposer<Component> {

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
    private Button prevButton;

    @Wire
    private Button nextButton;

    private OutwardMakerDataEntryDetailService service;

    private List<OutwardCheque> cheques;

    private int currentIndex = 0;

    private String batchId;


    @Override
    public void doAfterCompose(Component comp)
            throws Exception {

        super.doAfterCompose(comp);

        service =
                new OutwardMakerDataEntryDetailService();

        batchId =
                Executions.getCurrent()
                          .getParameter("batchId");

        if (batchId == null || batchId.trim().isEmpty()) {

            batchId = "B008";
        }

        cheques =
                service.getCheques(batchId);

        if (cheques == null || cheques.isEmpty()) {

            Messagebox.show(
                    "No cheque data available.",
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

        OutwardCheque cheque =
                cheques.get(currentIndex);

        batchIdLabel.setValue(
                cheque.getBatchId());

        chequeProgressLabel.setValue(
                "Cheque "
                + (currentIndex + 1)
                + " of "
                + cheques.size()
        );

        accountNumberTextbox.setValue(
                cheque.getAccountNumber());

        chequeDateTextbox.setValue(
                cheque.getChequeDate());

        try {

            amountTextbox.setValue(
                    new java.math.BigDecimal(
                            cheque.getAmount()
                    )
            );

        } catch (Exception e) {

            amountTextbox.setValue("");
        }

        updateButtons();
    }


    private void updateButtons() {

        prevButton.setDisabled(
                currentIndex == 0
        );

        nextButton.setDisabled(
                currentIndex >= cheques.size() - 1
        );
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

        OutwardCheque cheque =
                cheques.get(currentIndex);

        String account =
                accountNumberTextbox.getValue();

        String date =
                chequeDateTextbox.getValue();

        if (account == null ||
            account.trim().isEmpty()) {

            Messagebox.show(
                    "Please enter Account Number.",
                    "Validation",
                    Messagebox.OK,
                    Messagebox.EXCLAMATION
            );

            return;
        }

        if (date == null ||
            date.trim().isEmpty()) {

            Messagebox.show(
                    "Please enter Cheque Date.",
                    "Validation",
                    Messagebox.OK,
                    Messagebox.EXCLAMATION
            );

            return;
        }

        if (amountTextbox.getValue() == null) {

            Messagebox.show(
                    "Please enter Amount.",
                    "Validation",
                    Messagebox.OK,
                    Messagebox.EXCLAMATION
            );

            return;
        }

        cheque.setCorrectAccountNumber(account);

        cheque.setCorrectChequeDate(date);

        cheque.setCorrectAmount(
                amountTextbox.getValue().toString()
        );

        service.saveCheque(cheque);

        if (currentIndex < cheques.size() - 1) {

            currentIndex++;

            loadCheque();

        } else {

            Messagebox.show(
                    "All 5 cheques in batch "
                    + batchId
                    + " have been completed.",
                    "Batch Completed",
                    Messagebox.OK,
                    Messagebox.INFORMATION,
                    event -> {

                        Executions.sendRedirect(
                                "outward-maker-data-entry.zul"
                        );
                    }
            );
        }
    }


    @Listen("onClick = #rejectButton")
    public void rejectCheque() {

        Window rejectWindow =
                new Window();

        rejectWindow.setTitle(
                "Reject Cheque"
        );

        rejectWindow.setWidth(
                "450px"
        );

        rejectWindow.setClosable(
                true
        );

        rejectWindow.setBorder(
                "normal"
        );

        rejectWindow.setParent(
                Executions.getCurrent()
                          .getDesktop()
                          .getFirstPage()
                          .getFirstRoot()
        );

        org.zkoss.zul.Vlayout layout =
                new org.zkoss.zul.Vlayout();

        layout.setSpacing(
                "12px"
        );

        layout.setStyle(
                "padding:20px;"
        );

        Label label =
                new Label(
                        "Enter rejection reason:"
                );

        Textbox reasonBox =
                new Textbox();

        reasonBox.setRows(4);

        reasonBox.setWidth(
                "100%"
        );

        Button confirm =
                new Button(
                        "Confirm Reject"
                );

        confirm.setWidth(
                "140px"
        );

        confirm.addEventListener(
                "onClick",
                event -> {

                    String reason =
                            reasonBox.getValue();

                    if (reason == null ||
                        reason.trim().isEmpty()) {

                        Messagebox.show(
                                "Please enter rejection reason.",
                                "Validation",
                                Messagebox.OK,
                                Messagebox.EXCLAMATION
                        );

                        return;
                    }

                    OutwardCheque cheque =
                            cheques.get(currentIndex);

                    service.rejectCheque(
                            cheque,
                            reason
                    );

                    rejectWindow.detach();

                    Messagebox.show(
                            "Cheque "
                            + (currentIndex + 1)
                            + " rejected and sent back to Maker.",
                            "Rejected",
                            Messagebox.OK,
                            Messagebox.INFORMATION,
                            e -> {

                                Executions.sendRedirect(
                                        "outward-maker-data-entry.zul"
                                );
                            }
                    );
                }
        );

        layout.appendChild(label);
        layout.appendChild(reasonBox);
        layout.appendChild(confirm);

        rejectWindow.appendChild(
                layout
        );

        rejectWindow.doModal();
    }


    @Listen("onClick = #backButton")
    public void backToList() {

        Executions.sendRedirect(
                "outward-maker-data-entry.zul"
        );
    }
}