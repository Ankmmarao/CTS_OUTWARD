package com.iispl.cts.controller.outward;

import java.util.List;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Button;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.ListModelList;

import com.iispl.cts.model.outward.OutwardBatch;
import com.iispl.cts.service.outward.OutwardMakerMicrRepairService;

public class OutwardMakerMicrRepairController
        extends SelectorComposer<Component> {

    private static final long serialVersionUID = 1L;

    @Wire
    private Listbox batchListbox;

    private OutwardMakerMicrRepairService service;

    @Override
    public void doAfterCompose(Component comp) throws Exception {
        super.doAfterCompose(comp);

        service = new OutwardMakerMicrRepairService();

        loadBatches();
    }

    private void loadBatches() {

        batchListbox.setItemRenderer(
            new ListitemRenderer<OutwardBatch>() {

                @Override
                public void render(
                        Listitem item,
                        OutwardBatch batch,
                        int index) {

                    item.appendChild(
                        new Listcell(batch.getBatchNumber())
                    );

                    item.appendChild(
                        new Listcell(
                            String.valueOf(batch.getNumberOfCheques())
                        )
                    );

                    // Get number of MICR error cheques
                    int micrErrorCount =
                        service.getMicrErrorCount(
                            batch.getBatchNumber()
                        );

                    item.appendChild(
                        new Listcell(
                            String.valueOf(micrErrorCount)
                        )
                    );

                    Listcell actionCell = new Listcell();

                    Button openButton = new Button("Open");

                    openButton.setWidth("90px");

                    openButton.addEventListener(
                        "onClick",
                        event -> openBatch(batch)
                    );

                    actionCell.appendChild(openButton);

                    item.appendChild(actionCell);
                }
            }
        );

        List<OutwardBatch> batches =
            service.getMicrRepairBatches();

        ListModelList<OutwardBatch> model =
            new ListModelList<>(batches);

        batchListbox.setModel(model);
    }

    private void openBatch(OutwardBatch batch) {

        /*
         * Change batch status to MICR_REPAIR
         * when Maker starts MICR Repair.
         */
        service.startMicrRepair(
            batch.getBatchNumber()
        );

        Executions.sendRedirect(
            "outward-maker-micr-repair-detail.zul"
            + "?batchNumber="
            + batch.getBatchNumber()
        );
    }
}