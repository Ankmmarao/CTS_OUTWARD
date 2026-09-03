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
import com.iispl.cts.service.outward.OutwardMakerAmountAccountService;

public class OutwardMakerAmountAccountController
        extends SelectorComposer<Component> {

    private static final long serialVersionUID = 1L;

    @Wire
    private Listbox batchListbox;

    private OutwardMakerAmountAccountService service;

    @Override
    public void doAfterCompose(Component comp)
            throws Exception {

        super.doAfterCompose(comp);

        service = new OutwardMakerAmountAccountService();

        loadBatches();
    }

    private void loadBatches() {

        batchListbox.getItems().clear();

        List<OutwardBatch> batches =
                service.getBatches();

        for (OutwardBatch batch : batches) {

            Listitem item = new Listitem();

            // Batch ID
            item.appendChild(
                    new Listcell(batch.getBatchId()));

            // Total Cheques
            item.appendChild(
                    new Listcell(
                            String.valueOf(
                                    batch.getTotalCheques())));

            // Action column
            Listcell actionCell = new Listcell();

            Button open = new Button("Open");

            open.setWidth("90px");

            open.addEventListener(
                    "onClick",
                    event -> {

                        Executions.sendRedirect(
                                "outward-maker-amount-account-detail.zul"
                                + "?batchId="
                                + batch.getBatchId());
                    });

            actionCell.appendChild(open);

            item.appendChild(actionCell);

            batchListbox.appendChild(item);
        }
    }
}