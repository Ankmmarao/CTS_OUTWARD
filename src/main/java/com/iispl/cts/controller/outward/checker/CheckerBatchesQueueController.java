package com.iispl.cts.controller.outward.checker;

import java.util.List;
import java.util.stream.Collectors;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Button;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Textbox;

import com.iispl.cts.model.outward.OutwardBatch;
import com.iispl.cts.service.outward.checker.CheckerDashboardService;

public class CheckerBatchesQueueController
extends SelectorComposer<Component> {

private static final long serialVersionUID = 1L;

@Wire
private Textbox batchSearch;

@Wire
private Button searchButton;

@Wire
private Button refreshButton;

@Wire
private Listbox batchListbox;

private CheckerDashboardService checkerService;

private List<OutwardBatch> allBatches;

@Override
public void doAfterCompose(Component comp)
        throws Exception {

    super.doAfterCompose(comp);

    checkerService =
            new CheckerDashboardService();

    loadBatches();
}

/**
 * Load all batches which are ready
 * for Checker verification.
 */
private void loadBatches() {

    allBatches =
            checkerService.getCheckerBatches();

    if (allBatches == null) {

        allBatches =
                java.util.Collections.emptyList();
    }

    displayBatches(allBatches);
}

/**
 * Display batches in Listbox.
 */
private void displayBatches(
        List<OutwardBatch> batches) {

    ListModelList<OutwardBatch> model =
            new ListModelList<>();

    model.addAll(batches);

    batchListbox.setModel(model);

    batchListbox.setItemRenderer(
            new ListitemRenderer<OutwardBatch>() {

                @Override
                public void render(
                        Listitem item,
                        OutwardBatch batch,
                        int index) {

                    /*
                     * Batch ID
                     */
                    item.appendChild(
                            new Listcell(
                                    safe(
                                            batch.getBatchId()
                                    )
                            )
                    );

                    /*
                     * Total Cheques
                     */
                    item.appendChild(
                            new Listcell(
                                    String.valueOf(
                                            batch.getTotalCheques()
                                    )
                            )
                    );

                    /*
                     * Accepted
                     *
                     * Currently 0 because
                     * checker decision persistence
                     * is handled separately.
                     */
                    item.appendChild(
                            new Listcell("0")
                    );

                    /*
                     * Rejected
                     */
                    item.appendChild(
                            new Listcell("0")
                    );

                    /*
                     * Batch Status
                     */
                    item.appendChild(
                            new Listcell(
                                    safe(
                                            batch.getStatus()
                                    )
                            )
                    );

                    /*
                     * Action
                     */
                    Listcell actionCell =
                            new Listcell();

                    Button openButton =
                            new Button();

                    openButton.setLabel("OPEN");

                    openButton.setSclass(
                            "primary-button"
                    );

                    final String currentBatchId =
                            batch.getBatchId();

                    openButton.addEventListener(
                            "onClick",
                            event -> openBatch(
                                    currentBatchId
                            )
                    );

                    actionCell.appendChild(
                            openButton
                    );

                    item.appendChild(
                            actionCell
                    );
                }
            }
    );
}

/**
 * Search Batch ID.
 */
@Listen("onClick=#searchButton")
public void searchBatch() {

    String text =
            batchSearch.getValue();

    if (text == null ||
        text.trim().isEmpty()) {

        displayBatches(allBatches);
        return;
    }

    String search =
            text.trim().toLowerCase();

    List<OutwardBatch> filtered =
            allBatches.stream()
                    .filter(batch ->
                            batch.getBatchId() != null
                            &&
                            batch.getBatchId()
                                    .toLowerCase()
                                    .contains(search)
                    )
                    .collect(Collectors.toList());

    displayBatches(filtered);
}

/**
 * Refresh.
 */
@Listen("onClick=#refreshButton")
public void refreshQueue() {

    batchSearch.setValue("");

    loadBatches();
}

/**
 * Open Batch Verification.
 */
private void openBatch(
        String batchId) {

    if (batchId == null ||
        batchId.trim().isEmpty()) {

        return;
    }

    String contextPath =
            Executions.getCurrent()
                    .getContextPath();

    String url =
            contextPath
            + "/outward/checker/"
            + "batchVerification.zul"
            + "?batchId="
            + batchId;

    Executions.sendRedirect(url);
}

/**
 * Dashboard navigation.
 */
@Listen("onClick=#dashboardButton")
public void openDashboard() {

    navigate(
            "/outward/checker/dashboard.zul"
    );
}

/**
 * Queue navigation.
 */
@Listen("onClick=#queueButton")
public void openQueue() {

    navigate(
            "/outward/checker/batchesQueue.zul"
    );
}

/**
 * Reports navigation.
 */
@Listen("onClick=#reportsButton")
public void openReports() {

    navigate(
            "/outward/checker/reports.zul"
    );
}

/**
 * NPCI navigation.
 */
@Listen("onClick=#npciButton")
public void openNPCI() {

    navigate(
            "/outward/checker/sendToNPCI.zul"
    );
}

private void navigate(String page) {

    String contextPath =
            Executions.getCurrent()
                    .getContextPath();

    Executions.sendRedirect(
            contextPath + page
    );
}

private String safe(String value) {

    if (value == null ||
        value.trim().isEmpty()) {

        return "-";
    }

    return value;
}


}
