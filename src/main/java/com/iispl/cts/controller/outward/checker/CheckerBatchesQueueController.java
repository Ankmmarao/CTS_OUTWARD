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
                "Queue ZUL URL = "
                + Executions.getCurrent()
                        .getDesktop()
                        .getRequestPath()
        );

        service = new CheckerBatchService();

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
                        "CHECKER QUEUE: DAO returned NULL"
                );

                return;
            }

            System.out.println(
                    "CHECKER QUEUE: Batch count = "
                    + batches.size()
            );

            queueListbox.getItems().clear();

            for (OutwardBatch batch : batches) {

                System.out.println(
                        "CHECKER QUEUE: Adding batch"
                        + " | Batch = "
                        + batch.getBatchNumber()
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
                    "CHECKER QUEUE ERROR"
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

    private void createBatchRow(
            final OutwardBatch batch) {

        Listitem item =
                new Listitem();

        // =====================================================
        // BATCH NUMBER
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
        // TOTAL CHEQUES
        // =====================================================

        Listcell chequeCell =
                new Listcell();

        chequeCell.appendChild(
                new Label(
                        String.valueOf(
                                batch.getNumberOfCheques()
                        )
                )
        );

        item.appendChild(
                chequeCell
        );

        // =====================================================
        // STATUS
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
        // ACTION
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
    // SAFE VALUE
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