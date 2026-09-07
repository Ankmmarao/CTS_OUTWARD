package com.iispl.cts.controller.outward;

import java.util.List;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Image;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Messagebox;

import com.iispl.cts.model.outward.OutwardCheque;
import com.iispl.cts.service.outward.OutwardMakerMicrRepairDetailService;

public class OutwardMakerMicrRepairDetailController
        extends SelectorComposer<Component> {

    private static final long serialVersionUID = 1L;

    @Wire
    private Label batchIdLabel;

    @Wire
    private Listbox chequeList;

    @Wire
    private Image frontImage;

    @Wire
    private Image backImage;

    @Wire
    private Textbox chequeNumberTextbox;

    @Wire
    private Label currentStatusLabel;

    @Wire
    private Textbox cityCodeTextbox;

    @Wire
    private Textbox bankCodeTextbox;

    @Wire
    private Textbox branchCodeTextbox;

    @Wire
    private Textbox originalMicrTextbox;

    private OutwardMakerMicrRepairDetailService service;

    private List<OutwardCheque> cheques;

    private int currentIndex = 0;

    private String batchNumber;


    @Override
    public void doAfterCompose(Component comp)
            throws Exception {

        super.doAfterCompose(comp);

        service = new OutwardMakerMicrRepairDetailService();

        batchNumber =
                Executions.getCurrent()
                        .getParameter("batchNumber");

        if (batchNumber == null ||
                batchNumber.trim().isEmpty()) {

            Messagebox.show(
                    "Batch number is missing.",
                    "Error",
                    Messagebox.OK,
                    Messagebox.ERROR);

            return;
        }

        batchNumber = batchNumber.trim();

        batchIdLabel.setValue(batchNumber);

        loadCheques();
    }


    private void loadCheques() {

        cheques =
                service.getMicrErrorCheques(batchNumber);

        if (cheques == null ||
                cheques.isEmpty()) {

            Messagebox.show(
                    "No MICR error cheques found for this batch.",
                    "Information",
                    Messagebox.OK,
                    Messagebox.INFORMATION);

            return;
        }

        populateChequeList();

        currentIndex = 0;

        loadCurrentCheque();
    }


    private void loadCurrentCheque() {

        if (cheques == null ||
                cheques.isEmpty()) {
            return;
        }

        OutwardCheque cheque =
                cheques.get(currentIndex);

        chequeNumberTextbox.setValue(
                cheque.getChequeNumber());

        currentStatusLabel.setValue(
                cheque.getChequeStatus());

        cityCodeTextbox.setValue(
                cheque.getCityCode() == null
                        ? ""
                        : cheque.getCityCode());

        bankCodeTextbox.setValue(
                cheque.getBankCode() == null
                        ? ""
                        : cheque.getBankCode());

        branchCodeTextbox.setValue(
                cheque.getBranchCode() == null
                        ? ""
                        : cheque.getBranchCode());

        originalMicrTextbox.setValue(
                buildMicr(
                        cheque.getCityCode(),
                        cheque.getBankCode(),
                        cheque.getBranchCode()));

        frontImage.setSrc(
                cheque.getFrontImagePath());

        backImage.setSrc(
                cheque.getBackImagePath());

        chequeList.setSelectedIndex(currentIndex);
    }


    private String buildMicr(
            String cityCode,
            String bankCode,
            String branchCode) {

        String city =
                cityCode == null ? "" : cityCode;

        String bank =
                bankCode == null ? "" : bankCode;

        String branch =
                branchCode == null ? "" : branchCode;

        return city + bank + branch;
    }


    @Listen("onClick = #saveNextButton")
    public void saveAndNext() {

        if (cheques == null ||
                cheques.isEmpty()) {
            return;
        }

        OutwardCheque cheque =
                cheques.get(currentIndex);

        boolean updated =
                service.updateCorrectedMicr(
                        batchNumber,
                        cheque.getChequeNumber(),
                        cityCodeTextbox.getValue().trim(),
                        bankCodeTextbox.getValue().trim(),
                        branchCodeTextbox.getValue().trim());

        if (!updated) {

            Messagebox.show(
                    "MICR repair could not be saved.",
                    "Error",
                    Messagebox.OK,
                    Messagebox.ERROR);

            return;
        }

        cheque.setCityCode(
                cityCodeTextbox.getValue().trim());

        cheque.setBankCode(
                bankCodeTextbox.getValue().trim());

        cheque.setBranchCode(
                branchCodeTextbox.getValue().trim());

        cheque.setChequeStatus(
                "MICR_CORRECTED");

        currentStatusLabel.setValue(
                "MICR_CORRECTED");


        boolean remaining =
                service.hasRemainingMicrErrors(
                        batchNumber);

        if (!remaining) {

            /*
             * Change this status to the exact next
             * batch status used in your workflow.
             */
            service.updateBatchStatus(
                    batchNumber,
                    "VALIDATED");
        }


        if (currentIndex < cheques.size() - 1) {

            currentIndex++;

            loadCurrentCheque();

        } else {

            Messagebox.show(
                    "MICR repair completed.",
                    "Success",
                    Messagebox.OK,
                    Messagebox.INFORMATION);

            Executions.getCurrent().sendRedirect(
                    "outward-maker-micr-repair.zul");
        }
    }


    @Listen("onClick = #prevButton")
    public void previousCheque() {

        if (cheques == null ||
                cheques.isEmpty()) {
            return;
        }

        if (currentIndex > 0) {

            currentIndex--;

            loadCurrentCheque();

        } else {

            Messagebox.show(
                    "This is the first cheque.",
                    "Information",
                    Messagebox.OK,
                    Messagebox.INFORMATION);
        }
    }


    @Listen("onClick = #backButton")
    public void back() {

        Executions.getCurrent().sendRedirect(
                "outward-maker-micr-repair.zul");
    }


    @Listen("onSelect = #chequeList")
    public void selectCheque() {

        int index =
                chequeList.getSelectedIndex();

        if (index >= 0 &&
                index < cheques.size()) {

            currentIndex = index;

            loadCurrentCheque();
        }
    }
    
    
    
    private void populateChequeList() {

        chequeList.getItems().clear();

        for (OutwardCheque cheque : cheques) {

            org.zkoss.zul.Listitem item =
                    new org.zkoss.zul.Listitem();

            item.setLabel(cheque.getChequeNumber());

            chequeList.appendChild(item);
        }
    }
}