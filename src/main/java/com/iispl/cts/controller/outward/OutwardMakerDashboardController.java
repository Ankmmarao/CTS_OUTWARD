package com.iispl.cts.controller.outward;

import java.util.List;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Button;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Vlayout;

import com.iispl.cts.model.outward.OutwardBatch;
import com.iispl.cts.model.outward.OutwardValidationResult;
import com.iispl.cts.service.outward.OutwardMakerDashboardService;

public class OutwardMakerDashboardController
        extends SelectorComposer<Component> {

    private static final long serialVersionUID = 1L;

    @Wire
    private Listbox batchListbox;

    private OutwardMakerDashboardService service;

    @Override
    public void doAfterCompose(
            Component comp)
            throws Exception {

        super.doAfterCompose(comp);

        service =
                new OutwardMakerDashboardService();

        loadBatches();
    }

    private void loadBatches() {

        batchListbox.getItems().clear();

        List<OutwardBatch> batches =
                service.getBatches();

        if (batches == null) {
            return;
        }

        for (OutwardBatch batch :
                batches) {

            Listitem item =
                    new Listitem();

            // =================================================
            // BATCH NO
            // =================================================

            item.appendChild(
                    new Listcell(
                            batch.getBatchId()
                    )
            );

            // =================================================
            // TOTAL CHEQUES + ERROR SUMMARY
            // =================================================

            Listcell totalCell =
                    new Listcell();

            Vlayout totalLayout =
                    new Vlayout();

            Label totalLabel =
                    new Label(
                            String.valueOf(
                                    batch.getTotalCheques()
                            )
                    );

            totalLabel.setStyle(
                    "font-size:14px;"
                    + "font-weight:bold;"
            );

            totalLayout.appendChild(
                    totalLabel
            );

            Label dataEntryLabel =
                    new Label(
                            "Data Entry Error : "
                            + batch.getDataEntryErrorCount()
                    );

            dataEntryLabel.setStyle(
                    "font-size:11px;"
                    + "color:#64748B;"
            );

            totalLayout.appendChild(
                    dataEntryLabel
            );

            Label micrLabel =
                    new Label(
                            "MICR Error : "
                            + batch.getMicrErrorCount()
                    );

            micrLabel.setStyle(
                    "font-size:11px;"
                    + "color:#64748B;"
            );

            totalLayout.appendChild(
                    micrLabel
            );

            Label amountLabel =
                    new Label(
                            "Amount & Account Error : "
                            + batch.getAmountAccountErrorCount()
                    );

            amountLabel.setStyle(
                    "font-size:11px;"
                    + "color:#64748B;"
            );

            totalLayout.appendChild(
                    amountLabel
            );

            totalCell.appendChild(
                    totalLayout
            );

            item.appendChild(
                    totalCell
            );

            // =================================================
            // STATUS
            // =================================================

            Listcell statusCell =
                    new Listcell();

            Label statusLabel =
                    new Label(
                            batch.getStatus()
                    );

            if ("AVAILABLE".equals(
                    batch.getStatus())) {

                statusLabel.setStyle(
                        "background:#16A34A;"
                        + "color:white;"
                        + "padding:6px 12px;"
                        + "border-radius:15px;"
                        + "font-size:11px;"
                        + "font-weight:bold;"
                );

            } else if ("ASSIGNED".equals(
                    batch.getStatus())) {

                statusLabel.setStyle(
                        "background:#2563EB;"
                        + "color:white;"
                        + "padding:6px 12px;"
                        + "border-radius:15px;"
                        + "font-size:11px;"
                        + "font-weight:bold;"
                );

            } else if ("COMPLETED".equals(
                    batch.getStatus())) {

                statusLabel.setStyle(
                        "background:#16A34A;"
                        + "color:white;"
                        + "padding:6px 12px;"
                        + "border-radius:15px;"
                        + "font-size:11px;"
                        + "font-weight:bold;"
                );
            }

            statusCell.appendChild(
                    statusLabel
            );

            item.appendChild(
                    statusCell
            );

            // =================================================
            // USER ID
            // =================================================

            String userId =
                    batch.getUserId();

            if (userId == null
                    || userId.trim().isEmpty()) {

                userId = "-";
            }

            item.appendChild(
                    new Listcell(userId)
            );

            // =================================================
            // ASSIGNMENT
            // =================================================

            Listcell assignmentCell =
                    new Listcell();

            if ("AVAILABLE".equals(
                    batch.getStatus())) {

                Button assignButton =
                        new Button("Assign to Me");

                assignButton.setWidth(
                        "120px"
                );

                assignButton.setStyle(
                        "background:white;"
                        + "color:#2563EB;"
                        + "border:1px solid #2563EB;"
                        + "border-radius:5px;"
                        + "font-weight:bold;"
                );

                final String currentBatchId =
                        batch.getBatchId();

                assignButton.addEventListener(
                        "onClick",
                        event -> {

                            assignAndValidate(
                                    currentBatchId
                            );
                        }
                );

                assignmentCell.appendChild(
                        assignButton
                );

            } else {

                String assignment =
                        batch.getAssignment();

                if (assignment == null) {
                    assignment = "-";
                }

                assignmentCell.appendChild(
                        new Label(
                                assignment
                        )
                );
            }

            item.appendChild(
                    assignmentCell
            );

            // =================================================
            // ACTION
            // =================================================

            Listcell actionCell =
                    new Listcell();

            Button openButton =
                    new Button("Open");

            openButton.setWidth(
                    "85px"
            );

            final String currentBatchId =
                    batch.getBatchId();

            if ("AVAILABLE".equals(
                    batch.getStatus())) {

                openButton.setDisabled(true);

                openButton.setStyle(
                        "background:#B8C4D4;"
                        + "color:white;"
                        + "border:none;"
                );

            } else {

                openButton.setDisabled(false);

                openButton.setStyle(
                        "background:#2563EB;"
                        + "color:white;"
                        + "border:none;"
                        + "font-weight:bold;"
                );

                openButton.addEventListener(
                        "onClick",
                        event -> {

                            openAssignedBatch(
                                    currentBatchId
                            );
                        }
                );
            }

            actionCell.appendChild(
                    openButton
            );

            item.appendChild(
                    actionCell
            );

            batchListbox.appendChild(
                    item
            );
        }
    }

    // =========================================================
    // ASSIGN + VALIDATE
    // =========================================================

    private void assignAndValidate(
            String batchId) {

        String userId =
                (String) Sessions.getCurrent()
                        .getAttribute("userId");

        if (userId == null
                || userId.trim().isEmpty()) {

            userId = "maker456";
        }

        OutwardValidationResult result =
                service.assignAndValidate(
                        batchId,
                        userId
                );

        String message =
                "Validation completed successfully."
                + "\n\n"
                + "Batch No : "
                + batchId
                + "\n"
                + "Total Cheques : "
                + result.getTotalCheques()
                + "\n\n"
                + "Data Entry Error : "
                + result.getDataEntryErrors()
                + "\n"
                + "MICR Error : "
                + result.getMicrErrors()
                + "\n"
                + "Amount & Account Error : "
                + result.getAmountAccountErrors()
                + "\n\n"
                + "Total Errors : "
                + result.getTotalErrors();

        Messagebox.show(
                message,
                "Validation Result",
                Messagebox.OK,
                Messagebox.INFORMATION,
                event -> loadBatches()
        );
    }

    // =========================================================
    // OPEN ASSIGNED BATCH
    // =========================================================

    private void openAssignedBatch(
            String batchId) {

        OutwardBatch batch =
                findBatch(batchId);

        if (batch == null) {
            return;
        }

        if (batch.getDataEntryErrorCount() > 0) {

            Executions.sendRedirect(
                    "outward-maker-data-entry.zul"
            );

            return;
        }

        if (batch.getMicrErrorCount() > 0) {

            Executions.sendRedirect(
                    "outward-maker-micr-repair.zul"
            );

            return;
        }

        if (batch.getAmountAccountErrorCount() > 0) {

            Executions.sendRedirect(
                    "outward-maker-amount-account.zul"
            );
        }
    }

    // =========================================================
    // FIND BATCH
    // =========================================================

    private OutwardBatch findBatch(
            String batchId) {

        List<OutwardBatch> batches =
                service.getBatches();

        if (batches == null) {
            return null;
        }

        for (OutwardBatch batch :
                batches) {

            if (batchId.equals(
                    batch.getBatchId())) {

                return batch;
            }
        }

        return null;
    }
}