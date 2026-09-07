package com.iispl.cts.controller.outward.checker;

import com.iispl.cts.model.outward.OutwardBatch;
import com.iispl.cts.service.outward.checker.CheckerReportsService;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;

import java.util.List;

public class CheckerReportsController
        extends SelectorComposer<Component> {

    private static final long serialVersionUID = 1L;

    @Wire
    private Listbox reportListbox;

    private CheckerReportsService service;

    @Override
    public void doAfterCompose(Component comp)
            throws Exception {

        super.doAfterCompose(comp);

        service = new CheckerReportsService();

        loadCompletedBatches();
    }

    /**
     * Load ONLY CHECKER_COMPLETED batches.
     */
    private void loadCompletedBatches() {

        reportListbox.getItems().clear();

        try {

            List<OutwardBatch> batches =
                    service.getCheckerCompletedBatches();

            if (batches == null || batches.isEmpty()) {
                return;
            }

            for (OutwardBatch batch : batches) {

                addBatchToList(batch);
            }

        } catch (Exception e) {

            e.printStackTrace();
        }
    }

    private void addBatchToList(
            OutwardBatch batch) {

        Listitem item = new Listitem();

        // =========================
        // BATCH NUMBER
        // =========================

        Listcell batchCell = new Listcell();

        batchCell.appendChild(
                new Label(
                        safe(batch.getBatchNumber())
                )
        );

        item.appendChild(batchCell);


        // =========================
        // TOTAL CHEQUES
        // =========================

        Listcell totalCell = new Listcell();

        Integer count =
                batch.getNumberOfCheques();

        totalCell.appendChild(
                new Label(
                        count == null
                                ? "0"
                                : String.valueOf(count)
                )
        );

        item.appendChild(totalCell);


        // =========================
        // ACCEPTED
        // =========================

        Listcell acceptedCell =
                new Listcell();

        acceptedCell.appendChild(
                new Label("—")
        );

        item.appendChild(acceptedCell);


        // =========================
        // REJECTED
        // =========================

        Listcell rejectedCell =
                new Listcell();

        rejectedCell.appendChild(
                new Label("—")
        );

        item.appendChild(rejectedCell);


        // =========================
        // RETURNED
        // =========================

        Listcell returnedCell =
                new Listcell();

        returnedCell.appendChild(
                new Label("—")
        );

        item.appendChild(returnedCell);


        // =========================
        // STATUS
        // =========================

        Listcell statusCell =
                new Listcell();

        statusCell.appendChild(
                new Label(
                        safe(batch.getBatchStatus())
                )
        );

        item.appendChild(statusCell);


        reportListbox.appendChild(item);
    }

    private String safe(String value) {

        return value == null
                ? ""
                : value;
    }
}