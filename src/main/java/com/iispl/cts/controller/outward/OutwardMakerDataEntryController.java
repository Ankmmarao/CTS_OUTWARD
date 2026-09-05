package com.iispl.cts.controller.outward;

import java.util.List;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Button;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;

import com.iispl.cts.dao.outward.OutwardMakerDataEntryDAO;
import com.iispl.cts.data.CTSStaticData;
import com.iispl.cts.model.outward.OutwardBatch;

public class OutwardMakerDataEntryController extends SelectorComposer<Component> {

    private static final long serialVersionUID = 1L;

    @Wire
    private Listbox batchListbox;

    @Override
    public void doAfterCompose(Component comp) throws Exception {
        super.doAfterCompose(comp);
        loadBatches();
    }

    private OutwardMakerDataEntryDAO dataEntryDAO = new OutwardMakerDataEntryDAO();

    private void loadBatches() {
        if (batchListbox == null) return;
        batchListbox.getItems().clear();

        List<OutwardBatch> batches = dataEntryDAO.getAllBatches();

        if (batches != null) {
            for (OutwardBatch batch : batches) {
                Listitem item = new Listitem();

                // 1. Batch ID
                String batchNumber = batch.getBatchNumber();
                Listcell cellBatchId = new Listcell(batchNumber);
                cellBatchId.setStyle("font-weight: 600; color: #1E293B;");
                item.appendChild(cellBatchId);

                // 2. Total Cheques
                item.appendChild(new Listcell(String.valueOf(batch.getNumberOfCheques())));

                // 3. Batch Status
                Listcell cellStatus = new Listcell();
                String status = batch.getBatchStatus() != null ? batch.getBatchStatus() : "UNKNOWN";
                Label lblStatus = new Label(status);
                lblStatus.setSclass("status-badge " + ("COMPLETED".equalsIgnoreCase(status) ? "badge-completed" : "badge-assigned"));
                cellStatus.appendChild(lblStatus);
                item.appendChild(cellStatus);

                // 4. Action Button
                Listcell cellAction = new Listcell();
                Button btn = new Button("Process");
                btn.setSclass("action-btn");
                btn.addEventListener("onClick", e -> {
                    Executions.sendRedirect("outward-maker-data-entry-detail.zul?batchId=" + batchNumber);
                });
                cellAction.appendChild(btn);
                item.appendChild(cellAction);

                batchListbox.appendChild(item);
            }
        }
    }
}