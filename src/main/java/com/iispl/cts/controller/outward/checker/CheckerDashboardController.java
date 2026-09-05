package com.iispl.cts.controller.outward.checker;

import com.iispl.cts.model.outward.OutwardBatch;
import com.iispl.cts.service.outward.checker.CheckerDashboardService;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.select.annotation.Listen;

import org.zkoss.zul.Button;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;
import org.zkoss.zul.ListModelList;

import java.time.LocalDate;
import java.util.List;

public class CheckerDashboardController
        extends SelectorComposer<Component> {

    private static final long serialVersionUID = 1L;


    // ==============================
    // ZUL COMPONENTS
    // ==============================

    @Wire
    private Label currentDate;

    @Wire
    private Label queueCount;

    @Wire
    private Label pendingChequeCount;

    @Wire
    private Label acceptedCount;

    @Wire
    private Label rejectedCount;

    @Wire
    private Listbox recentBatchListbox;


    // ==============================
    // SERVICE
    // ==============================

    private CheckerDashboardService dashboardService;


    // ==============================
    // INIT
    // ==============================

    @Override
    public void doAfterCompose(Component comp) throws Exception {

        super.doAfterCompose(comp);

        dashboardService =
                new CheckerDashboardService();

        loadDashboard();
    }


    // ==============================
    // LOAD DASHBOARD
    // ==============================

    private void loadDashboard() {

        try {

            loadDate();

            loadCounts();

            loadRecentBatches();

        } catch (Exception e) {

            e.printStackTrace();

        }
    }


    // ==============================
    // DATE
    // ==============================

    private void loadDate() {

        currentDate.setValue(
                LocalDate.now().toString()
        );
    }


    // ==============================
    // COUNTS
    // ==============================

    private void loadCounts() {

        int queue =
                dashboardService.getQueueCount();

        int pending =
                dashboardService.getPendingChequeCount();

        int accepted =
                dashboardService.getAcceptedCount();

        int rejected =
                dashboardService.getRejectedCount();


        queueCount.setValue(
                String.valueOf(queue)
        );

        pendingChequeCount.setValue(
                String.valueOf(pending)
        );

        acceptedCount.setValue(
                String.valueOf(accepted)
        );

        rejectedCount.setValue(
                String.valueOf(rejected)
        );
    }


    // ==============================
    // RECENT BATCHES
    // ==============================

    private void loadRecentBatches() {

        List<OutwardBatch> batches =
                dashboardService.getCheckerBatches();


        ListModelList<OutwardBatch> model =
                new ListModelList<>();

        model.addAll(batches);


        recentBatchListbox.setModel(model);


        /*
         * Renderer tells ZK:
         *
         * "For every OutwardBatch,
         *  create one Listitem."
         */

        recentBatchListbox.setItemRenderer(
                new ListitemRenderer<OutwardBatch>() {

                    @Override
                    public void render(
                            Listitem item,
                            OutwardBatch batch,
                            int index) {

                        // --------------------------
                        // Batch ID
                        // --------------------------

                        Listcell batchIdCell =
                                new Listcell(
                                        batch.getBatchId()
                                );

                        item.appendChild(
                                batchIdCell
                        );


                        // --------------------------
                        // Total Cheques
                        // --------------------------

                        Listcell totalCell =
                                new Listcell(
                                        String.valueOf(
                                                batch.getTotalCheques()
                                        )
                                );

                        item.appendChild(
                                totalCell
                        );


                        // --------------------------
                        // Status
                        // --------------------------

                        Listcell statusCell =
                                new Listcell(
                                        batch.getStatus()
                                );

                        item.appendChild(
                                statusCell
                        );


                        // --------------------------
                        // Assignment
                        // --------------------------

                        Listcell assignmentCell =
                                new Listcell(
                                        batch.getAssignment()
                                );

                        item.appendChild(
                                assignmentCell
                        );


                        // --------------------------
                        // OPEN BUTTON
                        // --------------------------

                        Listcell actionCell =
                                new Listcell();

                        Button openButton =
                                new Button();

                        openButton.setLabel(
                                "OPEN"
                        );

                        openButton.setSclass(
                                "primary-button"
                        );


                        openButton.addEventListener(
                                "onClick",
                                event -> {

                                    String batchId =
                                            batch.getBatchId();

                                    Executions.sendRedirect(
                                            "/outward/checker/batchVerification.zul"
                                            + "?batchId="
                                            + batchId
                                    );
                                }
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
}