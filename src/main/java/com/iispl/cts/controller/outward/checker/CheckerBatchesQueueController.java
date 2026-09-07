package com.iispl.cts.controller.outward.checker;

import java.util.List;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Button;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Messagebox;

import com.iispl.cts.model.outward.OutwardBatch;
import com.iispl.cts.service.outward.checker.CheckerBatchService;

public class CheckerBatchesQueueController
        extends SelectorComposer<Component> {

    private static final long serialVersionUID = 1L;

    @Wire
    private Listbox queueListbox;

    private CheckerBatchService service;

    @Override
    public void doAfterCompose(Component comp)
            throws Exception {

        super.doAfterCompose(comp);

        System.out.println();
        System.out.println(
                "=================================================="
        );
        System.out.println(
                "CHECKER BATCH QUEUE CONTROLLER STARTED"
        );
        System.out.println(
                "=================================================="
        );

        System.out.println(
                "Queue Request Path = "
                + Executions.getCurrent()
                        .getDesktop()
                        .getRequestPath()
        );

        System.out.println(
                "queueListbox = "
                + queueListbox
        );

        service =
                new CheckerBatchService();

        System.out.println(
                "CheckerBatchService created successfully."
        );

        loadBatches();

        System.out.println(
                "CHECKER BATCH QUEUE CONTROLLER FINISHED"
        );

        System.out.println(
                "=================================================="
        );
    }

    // =========================================================
    // LOAD ALL BATCHES
    // =========================================================

    private void loadBatches() {

        try {

            System.out.println();
            System.out.println(
                    "CHECKER QUEUE: Loading ALL batches..."
            );

            List<OutwardBatch> batches =
                    service.getCheckerQueueBatches();

            if (batches == null) {

                System.out.println(
                        "CHECKER QUEUE: Service returned NULL"
                );

                return;
            }

            System.out.println(
                    "CHECKER QUEUE: Batch count = "
                    + batches.size()
            );

            if (queueListbox == null) {

                System.out.println(
                        "CHECKER QUEUE ERROR: "
                        + "queueListbox is NULL"
                );

                return;
            }

            queueListbox.getItems().clear();

            for (OutwardBatch batch : batches) {

                if (batch == null) {

                    System.out.println(
                            "CHECKER QUEUE: NULL batch skipped"
                    );

                    continue;
                }

                System.out.println(
                        "CHECKER QUEUE: Adding batch"
                        + " | Batch = "
                        + batch.getBatchNumber()
                        + " | Branch = "
                        + batch.getBranchCode()
                        + " | Cheques = "
                        + batch.getNumberOfCheques()
                        + " | Status = "
                        + batch.getBatchStatus()
                );

                createBatchRow(batch);
            }

            System.out.println(
                    "CHECKER QUEUE: UI row count = "
                    + queueListbox.getItemCount()
            );

        } catch (Exception e) {

            System.out.println();
            System.out.println(
                    "=================================================="
            );

            System.out.println(
                    "CHECKER QUEUE ERROR"
            );

            System.out.println(
                    "=================================================="
            );

            e.printStackTrace();

            Messagebox.show(
                    "Unable to load checker batches.\n\n"
                    + e.getMessage(),
                    "Checker Queue",
                    Messagebox.OK,
                    Messagebox.ERROR
            );
        }
    }

    // =========================================================
    // CREATE BATCH ROW
    // =========================================================

    private void createBatchRow(
            final OutwardBatch batch) {

        Listitem item =
                new Listitem();

        // =====================================================
        // 1. BATCH NUMBER
        // =====================================================

        Listcell batchCell =
                new Listcell();

        Label batchLabel =
                new Label(
                        safe(
                                batch.getBatchNumber()
                        )
                );

        batchLabel.setStyle(
                "font-weight:bold;"
                + "color:#173B63;"
        );

        batchCell.appendChild(
                batchLabel
        );

        item.appendChild(
                batchCell
        );

        // =====================================================
        // 2. BRANCH
        // =====================================================

        Listcell branchCell =
                new Listcell();

        Label branchLabel =
                new Label(
                        safe(
                                batch.getBranchCode()
                        )
                );

        branchLabel.setStyle(
                "color:#173B63;"
        );

        branchCell.appendChild(
                branchLabel
        );

        item.appendChild(
                branchCell
        );

        // =====================================================
        // 3. TOTAL CHEQUES
        // =====================================================

        Listcell chequeCell =
                new Listcell();

        Label chequeLabel =
                new Label(
                        String.valueOf(
                                batch.getNumberOfCheques()
                        )
                );

        chequeCell.appendChild(
                chequeLabel
        );

        item.appendChild(
                chequeCell
        );

        // =====================================================
        // 4. STATUS
        // =====================================================

        Listcell statusCell =
                new Listcell();

        Label statusLabel =
                new Label(
                        safe(
                                batch.getBatchStatus()
                        )
                );

        statusLabel.setStyle(
                "font-weight:bold;"
                + "color:#173B63;"
        );

        statusCell.appendChild(
                statusLabel
        );

        item.appendChild(
                statusCell
        );

        // =====================================================
        // 5. ACTION
        // =====================================================

        Listcell actionCell =
                new Listcell();

        Button openButton =
                new Button(
                        "Open"
                );

        openButton.setWidth(
                "80px"
        );

        openButton.setHeight(
                "34px"
        );

        openButton.setStyle(
                "background:#078FE5;"
                + "color:white;"
                + "border:none;"
                + "border-radius:4px;"
                + "font-weight:bold;"
                + "cursor:pointer;"
        );

        openButton.addEventListener(
                Events.ON_CLICK,
                new EventListener<Event>() {

                    @Override
                    public void onEvent(
                            Event event)
                            throws Exception {

                        openBatch(
                                batch.getBatchNumber()
                        );
                    }
                }
        );

        actionCell.appendChild(
                openButton
        );

        item.appendChild(
                actionCell
        );

        // =====================================================
        // ADD ROW TO LIST
        // =====================================================

        queueListbox.appendChild(
                item
        );
    }

    // =========================================================
    // OPEN BATCH
    // =========================================================

    private void openBatch(
            String batchNumber) {

        if (batchNumber == null
                || batchNumber.trim().isEmpty()) {

            Messagebox.show(
                    "Invalid batch number.",
                    "Batch",
                    Messagebox.OK,
                    Messagebox.ERROR
            );

            return;
        }

        System.out.println(
                "CHECKER QUEUE: Opening batch = "
                + batchNumber
        );

        try {

            Executions.sendRedirect(
                    "/outward/checker/batchVerification.zul"
                    + "?batchNumber="
                    + Executions.encodeURL(
                            batchNumber.trim()
                    )
            );

        } catch (Exception e) {

            e.printStackTrace();

            Messagebox.show(
                    "Unable to open batch.\n\n"
                    + e.getMessage(),
                    "Batch",
                    Messagebox.OK,
                    Messagebox.ERROR
            );
        }
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

        return value.trim();
    }
}