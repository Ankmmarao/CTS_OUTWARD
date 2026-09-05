
package com.iispl.cts.controller.outward.checker;

import java.util.ArrayList;
import java.util.List;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Button;
import org.zkoss.zul.Label;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;
import org.zkoss.zul.Listbox;

import com.iispl.cts.model.outward.OutwardCheque;
import com.iispl.cts.service.outward.checker.CheckerBatchService;

public class CheckerBatchVerificationController
        extends SelectorComposer<Component> {

    private static final long serialVersionUID = 1L;

    // =========================================================
    // ZUL COMPONENTS
    // =========================================================

    @Wire
    private Label batchIdLabel;

    @Wire
    private Label totalChequeLabel;

    @Wire
    private Label acceptedChequeLabel;

    @Wire
    private Label rejectedChequeLabel;

    @Wire
    private Label pendingChequeLabel;

    @Wire
    private Listbox chequeListbox;

    // =========================================================
    // SERVICE
    // =========================================================

    private CheckerBatchService batchService;

    private String batchId;

    private List<OutwardCheque> chequeList;

    // =========================================================
    // INITIALIZE
    // =========================================================

    @Override
    public void doAfterCompose(Component comp)
            throws Exception {

        super.doAfterCompose(comp);

        batchService =
                new CheckerBatchService();

        // Get batchId from URL
        batchId =
                Executions
                        .getCurrent()
                        .getParameter("batchId");

        // If batchId is missing, return to queue
        if (batchId == null ||
            batchId.trim().isEmpty()) {

            goBackToQueue();

            return;
        }

        // Load batch data
        loadBatch();
    }

    // =========================================================
    // LOAD BATCH
    // =========================================================

    private void loadBatch() {

        try {

            chequeList =
                    batchService.getChequesByBatchId(
                            batchId
                    );

            if (chequeList == null) {

                chequeList =
                        new ArrayList<>();
            }

            // Display batch ID
            batchIdLabel.setValue(batchId);

            // Update summary
            updateSummary();

            // Display cheque list
            displayCheques();

        } catch (Exception e) {

            e.printStackTrace();
        }
    }

    // =========================================================
    // UPDATE SUMMARY
    // =========================================================

    private void updateSummary() {

        int total =
                chequeList.size();

        int accepted = 0;

        int rejected = 0;

        int pending = 0;

        for (OutwardCheque cheque : chequeList) {

            /*
             * Your OutwardCheque model does not have
             * a status field.
             *
             * Therefore we use the existing boolean fields.
             */

            if (cheque.isRejected()) {

                rejected++;

            } else if (cheque.isSaved()) {

                accepted++;

            } else {

                pending++;
            }
        }

        totalChequeLabel.setValue(
                String.valueOf(total)
        );

        acceptedChequeLabel.setValue(
                String.valueOf(accepted)
        );

        rejectedChequeLabel.setValue(
                String.valueOf(rejected)
        );

        pendingChequeLabel.setValue(
                String.valueOf(pending)
        );
    }

    // =========================================================
    // DISPLAY CHEQUES
    // =========================================================

    private void displayCheques() {

        ListModelList<OutwardCheque> model =
                new ListModelList<>();

        model.addAll(chequeList);

        chequeListbox.setModel(model);

        chequeListbox.setItemRenderer(
                new ListitemRenderer<OutwardCheque>() {

                    @Override
                    public void render(
                            Listitem item,
                            OutwardCheque cheque,
                            int index) {

                        // =====================================
                        // S.NO
                        // =====================================

                        item.appendChild(
                                new Listcell(
                                        String.valueOf(
                                                index + 1
                                        )
                                )
                        );

                        // =====================================
                        // CHEQUE NUMBER
                        // =====================================

                        item.appendChild(
                                new Listcell(
                                        safe(
                                                cheque.getChequeNumber()
                                        )
                                )
                        );

                        // =====================================
                        // ACCOUNT NUMBER
                        // =====================================

                        item.appendChild(
                                new Listcell(
                                        safe(
                                                cheque.getAccountNumber()
                                        )
                                )
                        );

                        // =====================================
                        // AMOUNT
                        // =====================================

                        item.appendChild(
                                new Listcell(
                                        safe(
                                                cheque.getAmount()
                                        )
                                )
                        );

                        // =====================================
                        // CHEQUE DATE
                        // =====================================

                        item.appendChild(
                                new Listcell(
                                        safe(
                                                cheque.getChequeDate()
                                        )
                                )
                        );

                        // =====================================
                        // MICR
                        // =====================================

                        item.appendChild(
                                new Listcell(
                                        safe(
                                                cheque.getMicr()
                                        )
                                )
                        );

                        // =====================================
                        // STATUS
                        // =====================================

                        String status =
                                getDisplayStatus(cheque);

                        item.appendChild(
                                new Listcell(status)
                        );

                        // =====================================
                        // ACTION
                        // =====================================

                        Listcell actionCell =
                                new Listcell();

                        Button openButton =
                                new Button();

                        openButton.setLabel("OPEN");

                        openButton.setSclass(
                                "primary-button"
                        );

                        // Keep current values for this row
                        final String currentBatchId =
                                batchId;

                        final String currentChequeId =
                                cheque.getChequeId();

                        // =====================================
                        // OPEN CHEQUE
                        // =====================================

                        openButton.addEventListener(
                                "onClick",
                                event -> {

                                    String contextPath =
                                            Executions
                                                    .getCurrent()
                                                    .getContextPath();

                                    String url =
                                            contextPath
                                            + "/outward/checker/"
                                            + "chequeVerification.zul"
                                            + "?batchId="
                                            + currentBatchId
                                            + "&chequeId="
                                            + currentChequeId;

                                    Executions.sendRedirect(
                                            url
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

    // =========================================================
    // GET DISPLAY STATUS
    // =========================================================

    private String getDisplayStatus(
            OutwardCheque cheque) {

        if (cheque.isRejected()) {

            return "REJECTED";
        }

        if (cheque.isSaved()) {

            return "ACCEPTED";
        }

        return "PENDING";
    }

    // =========================================================
    // SAFE STRING
    // =========================================================

    private String safe(String value) {

        if (value == null ||
            value.trim().isEmpty()) {

            return "-";
        }

        return value;
    }

    // =========================================================
    // BACK TO QUEUE
    // =========================================================

    @Listen("onClick=#backButton")
    public void backButton() {

        goBackToQueue();
    }

    @Listen("onClick=#backButtonBottom")
    public void backButtonBottom() {

        goBackToQueue();
    }

    private void goBackToQueue() {

        String contextPath =
                Executions
                        .getCurrent()
                        .getContextPath();

        Executions.sendRedirect(
                contextPath
                + "/outward/checker/batchesQueue.zul"
        );
    }

    // =========================================================
    // SIDEBAR - DASHBOARD
    // =========================================================

    @Listen("onClick=#dashboardButton")
    public void openDashboard() {

        navigate(
                "/outward/checker/dashboard.zul"
        );
    }

    // =========================================================
    // SIDEBAR - QUEUE
    // =========================================================

    @Listen("onClick=#queueButton")
    public void openQueue() {

        navigate(
                "/outward/checker/batchesQueue.zul"
        );
    }

    // =========================================================
    // SIDEBAR - REPORTS
    // =========================================================

    @Listen("onClick=#reportsButton")
    public void openReports() {

        navigate(
                "/outward/checker/reports.zul"
        );
    }

    // =========================================================
    // SIDEBAR - SEND TO NPCI
    // =========================================================

    @Listen("onClick=#npciButton")
    public void openNPCI() {

        navigate(
                "/outward/checker/sendToNPCI.zul"
        );
    }

    // =========================================================
    // COMMON NAVIGATION
    // =========================================================

    private void navigate(String page) {

        String contextPath =
                Executions
                        .getCurrent()
                        .getContextPath();

        Executions.sendRedirect(
                contextPath + page
        );
    }
}

