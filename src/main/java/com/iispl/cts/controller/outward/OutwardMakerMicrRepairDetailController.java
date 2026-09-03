package com.iispl.cts.controller.outward;

import java.util.List;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Button;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Textbox;

import com.iispl.cts.model.outward.OutwardCheque;
import com.iispl.cts.service.outward.OutwardMakerMicrRepairDetailService;

public class OutwardMakerMicrRepairDetailController
        extends SelectorComposer<Component> {

    private static final long serialVersionUID = 1L;

    @Wire
    private Label batchIdLabel;

    @Wire
    private Label chequeProgressLabel;

    @Wire
    private Textbox chequeNumberTextbox;

    @Wire
    private Textbox scannedMicrTextbox;

    @Wire
    private Textbox correctMicrTextbox;

    @Wire
    private Textbox repairReasonTextbox;

    @Wire
    private Textbox remarksTextbox;

    @Wire
    private Checkbox micrCorrectedCheckbox;

    @Wire
    private Checkbox frontVerifiedCheckbox;

    @Wire
    private Checkbox backVerifiedCheckbox;

    @Wire
    private Button prevButton;

    @Wire
    private Button nextButton;

    private OutwardMakerMicrRepairDetailService service;

    private List<OutwardCheque> cheques;

    private int currentIndex = 0;

    private String batchId;


    @Override
    public void doAfterCompose(Component comp)
            throws Exception {

        super.doAfterCompose(comp);

        service =
                new OutwardMakerMicrRepairDetailService();

        batchId =
                Executions.getCurrent()
                        .getParameter("batchId");

        if (batchId == null ||
            batchId.trim().isEmpty()) {

            batchId = "B008";
        }

        cheques =
                service.getCheques(batchId);

        if (cheques == null ||
            cheques.isEmpty()) {

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

        OutwardCheque cheque =
                cheques.get(currentIndex);

        batchIdLabel.setValue(
                cheque.getBatchId()
        );

        chequeProgressLabel.setValue(
                "Cheque "
                + (currentIndex + 1)
                + " of "
                + cheques.size()
        );

        chequeNumberTextbox.setValue(
                cheque.getChequeNumber()
        );

        scannedMicrTextbox.setValue(
                cheque.getAccountNumber()
        );

        correctMicrTextbox.setValue(
                cheque.getCorrectMicr()
        );

        repairReasonTextbox.setValue(
                cheque.getRepairReason()
        );

        remarksTextbox.setValue(
                cheque.getRemarks()
        );

        micrCorrectedCheckbox.setChecked(
                cheque.isMicrCorrected()
        );

        frontVerifiedCheckbox.setChecked(
                cheque.isFrontVerified()
        );

        backVerifiedCheckbox.setChecked(
                cheque.isBackVerified()
        );

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

        if (currentIndex <
                cheques.size() - 1) {

            currentIndex++;

            loadCheque();
        }
    }


    @Listen("onClick = #saveNextButton")
    public void saveAndNext() {

        OutwardCheque cheque =
                cheques.get(currentIndex);

        String micr =
                correctMicrTextbox.getValue();

        String reason =
                repairReasonTextbox.getValue();


        if (micr == null ||
            micr.trim().isEmpty()) {

            Messagebox.show(
                    "Please enter Correct MICR.",
                    "Validation",
                    Messagebox.OK,
                    Messagebox.EXCLAMATION
            );

            return;
        }


        if (reason == null ||
            reason.trim().isEmpty()) {

            Messagebox.show(
                    "Please enter Repair Reason.",
                    "Validation",
                    Messagebox.OK,
                    Messagebox.EXCLAMATION
            );

            return;
        }


        if (!micrCorrectedCheckbox.isChecked()) {

            Messagebox.show(
                    "Please confirm MICR corrected.",
                    "Validation",
                    Messagebox.OK,
                    Messagebox.EXCLAMATION
            );

            return;
        }


        if (!frontVerifiedCheckbox.isChecked()) {

            Messagebox.show(
                    "Please verify Front Image.",
                    "Validation",
                    Messagebox.OK,
                    Messagebox.EXCLAMATION
            );

            return;
        }


        if (!backVerifiedCheckbox.isChecked()) {

            Messagebox.show(
                    "Please verify Back Image.",
                    "Validation",
                    Messagebox.OK,
                    Messagebox.EXCLAMATION
            );

            return;
        }


        cheque.setCorrectMicr(micr);

        cheque.setRepairReason(reason);

        cheque.setRemarks(
                remarksTextbox.getValue()
        );

        cheque.setMicrCorrected(
                micrCorrectedCheckbox.isChecked()
        );

        cheque.setFrontVerified(
                frontVerifiedCheckbox.isChecked()
        );

        cheque.setBackVerified(
                backVerifiedCheckbox.isChecked()
        );


        service.saveCheque(cheque);


        if (currentIndex <
                cheques.size() - 1) {

            currentIndex++;

            loadCheque();

        } else {

            Messagebox.show(
                    "All "
                    + cheques.size()
                    + " MICR cheques completed.",
                    "Batch Completed",
                    Messagebox.OK,
                    Messagebox.INFORMATION,
                    event -> {

                        Executions.sendRedirect(
                                "outward-maker-micr-repair.zul"
                        );
                    }
            );
        }
    }


    @Listen("onClick = #rejectButton")
    public void rejectCheque() {

        Messagebox.show(
                "Enter rejection reason:",
                "Reject Cheque",
                Messagebox.OK
                | Messagebox.CANCEL,
                Messagebox.EXCLAMATION,
                event -> {

                    if (event.getName()
                            .equals("onOK")) {

                        showRejectReason();
                    }
                }
        );
    }


    private void showRejectReason() {

        final Textbox reasonBox =
                new Textbox();

        reasonBox.setRows(4);

        reasonBox.setWidth("400px");


        org.zkoss.zul.Window window =
                new org.zkoss.zul.Window();

        window.setTitle(
                "Reject Cheque"
        );

        window.setWidth(
                "500px"
        );

        window.setBorder(
                "normal"
        );

        window.setClosable(
                true
        );


        org.zkoss.zul.Vlayout layout =
                new org.zkoss.zul.Vlayout();

        layout.setSpacing(
                "12px"
        );

        layout.setStyle(
                "padding:20px;"
        );


        layout.appendChild(
                new Label(
                        "Rejection Reason"
                )
        );

        layout.appendChild(
                reasonBox
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


                    window.detach();


                    Messagebox.show(
                            "Cheque rejected and sent back to Maker.",
                            "Rejected",
                            Messagebox.OK,
                            Messagebox.INFORMATION,
                            e -> {

                                Executions.sendRedirect(
                                        "outward-maker-micr-repair.zul"
                                );
                            }
                    );
                }
        );


        layout.appendChild(confirm);

        window.appendChild(layout);

        window.doModal();
    }


    @Listen("onClick = #backButton")
    public void backToList() {

        Executions.sendRedirect(
                "outward-maker-micr-repair.zul"
        );
    }
}