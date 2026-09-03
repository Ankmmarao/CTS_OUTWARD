package com.iispl.cts.controller.outward;

import java.util.List;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Button;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;

import com.iispl.cts.model.outward.OutwardBatch;
import com.iispl.cts.service.outward.OutwardMakerMicrRepairService;

public class OutwardMakerMicrRepairController
        extends SelectorComposer<Component> {

    private static final long serialVersionUID = 1L;

    @Wire
    private Listbox batchListbox;

    private OutwardMakerMicrRepairService service;

    @Override
    public void doAfterCompose(Component comp)
            throws Exception {

        super.doAfterCompose(comp);

        service = new OutwardMakerMicrRepairService();

        loadBatches();
    }

    private void loadBatches() {

        batchListbox.getItems().clear();

        List<OutwardBatch> batches =
                service.getBatches();

        for (OutwardBatch batch : batches) {

            Listitem item = new Listitem();

            item.appendChild(
                    new Listcell(batch.getBatchId()));

            item.appendChild(
                    new Listcell(
                            String.valueOf(
                                    batch.getTotalCheques())));

            item.appendChild(
                    new Listcell(
                            String.valueOf(
                                    batch.getErrorCount())));

            Listcell actionCell = new Listcell();

            Button openButton =
                    new Button("Open");

            openButton.setWidth("90px");

            openButton.addEventListener(
                    "onClick",
                    event -> openBatch(batch)
            );

            actionCell.appendChild(openButton);

            item.appendChild(actionCell);

            batchListbox.appendChild(item);
        }
    }

    private void openBatch(OutwardBatch batch) {

        Executions.sendRedirect(
                "outward-maker-micr-repair-detail.zul"
                + "?batchId="
                + batch.getBatchId()
        );
    }
}