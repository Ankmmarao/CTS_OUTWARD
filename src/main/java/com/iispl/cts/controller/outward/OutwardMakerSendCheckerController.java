
package com.iispl.cts.controller.outward;

import java.util.List;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Session;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Button;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Messagebox;

import com.iispl.cts.model.outward.OutwardBatch;
import com.iispl.cts.service.outward.OutwardMakerSendCheckerService;

public class OutwardMakerSendCheckerController
        extends SelectorComposer<Component> {

    private static final long serialVersionUID = 1L;

    @Wire
    private Listbox batchListbox;

    private OutwardMakerSendCheckerService service;

    private String userId;


    // =========================================================
    // PAGE LOAD
    // =========================================================

    @Override
    public void doAfterCompose(Component comp) throws Exception {

        super.doAfterCompose(comp);

        /*
         * Get current ZK session
         */
        Session session =
                Executions.getCurrent().getSession();


        /*
         * Get logged-in Maker
         */
        Object userObject =
                session.getAttribute("userId");


        /*
         * Temporary testing user.
         *
         * Remove this once Login Controller
         * stores userId in session.
         */
        if (userObject == null) {

            userId = "maker001";

            System.out.println(
                    "WARNING: userId not found in session."
            );

            System.out.println(
                    "Using temporary test user: "
                    + userId
            );

        } else {

            userId = userObject.toString();
        }


        System.out.println(
                "Send To Checker - Current Maker = "
                + userId
        );


        /*
         * Create service
         */
        service =
                new OutwardMakerSendCheckerService();


        /*
         * Load READY_FOR_CHECKER batches
         */
        loadReadyBatches();
    }


    // =========================================================
    // LOAD READY FOR CHECKER BATCHES
    // =========================================================

    private void loadReadyBatches() {

        try {

            if (batchListbox == null) {

                System.out.println(
                        "batchListbox is null."
                );

                return;
            }


            if (userId == null
                    || userId.trim().isEmpty()) {

                Messagebox.show(
                        "Maker session is not available.",
                        "Error",
                        Messagebox.OK,
                        Messagebox.ERROR
                );

                return;
            }


            System.out.println(
                    "Loading READY_FOR_CHECKER batches..."
            );


            /*
             * Service
             */
            List<OutwardBatch> batches =
                    service.getReadyBatches(userId);


            System.out.println(
                    "Batches received = "
                    + batches.size()
            );


            /*
             * Convert List into ZK model
             */
            ListModelList<OutwardBatch> model =
                    new ListModelList<>(batches);


            /*
             * Renderer
             */
            batchListbox.setItemRenderer(
            	    (Listitem item, OutwardBatch batch, int index) -> {

            	        renderBatchRow(item, batch);

            	    }
            	);
           


            /*
             * Set model
             */
            batchListbox.setModel(model);


        } catch (Exception e) {

            e.printStackTrace();

            Messagebox.show(
                    "Unable to load batches ready for Checker.\n"
                    + e.getMessage(),
                    "Error",
                    Messagebox.OK,
                    Messagebox.ERROR
            );
        }
    }


    // =========================================================
    // RENDER ONE BATCH ROW
    // =========================================================

    private void renderBatchRow(
            Listitem item,
            OutwardBatch batch) {


        // -----------------------------------------------------
        // BATCH ID
        // -----------------------------------------------------

        Listcell batchIdCell =
                new Listcell();

        Label batchIdLabel =
                new Label(
                        safeValue(
                                batch.getBatchId()
                        )
                );

        batchIdLabel.setStyle(
                "font-weight:bold;"
                + "color:#122B49;"
        );

        batchIdCell.appendChild(
                batchIdLabel
        );

        item.appendChild(
                batchIdCell
        );


        // -----------------------------------------------------
        // TOTAL CHEQUES
        // -----------------------------------------------------

        Listcell chequeCell =
                new Listcell();

        Label chequeLabel =
                new Label(
                        String.valueOf(
                                batch.getTotalCheques()
                        )
                );

        chequeCell.appendChild(
                chequeLabel
        );

        item.appendChild(
                chequeCell
        );


        // -----------------------------------------------------
        // STATUS
        // -----------------------------------------------------

        Listcell statusCell =
                new Listcell();

        Label statusLabel =
                new Label(
                        "✓ Ready to Submit"
                );

        statusLabel.setStyle(
                "background:#D1FADF;"
                + "color:#039855;"
                + "padding:8px 15px;"
                + "border-radius:5px;"
                + "font-weight:bold;"
                + "display:inline-block;"
        );

        statusCell.appendChild(
                statusLabel
        );

        item.appendChild(
                statusCell
        );


        // -----------------------------------------------------
        // ACTION
        // -----------------------------------------------------

        Listcell actionCell =
                new Listcell();


        Button sendButton =
                new Button(
                        "Send to Checker"
                );


        sendButton.setWidth(
                "160px"
        );


        sendButton.setStyle(
                "background:#2457D6;"
                + "color:white;"
                + "border:none;"
                + "padding:8px 15px;"
                + "cursor:pointer;"
        );


        /*
         * Button click
         */
        sendButton.addEventListener(
                Events.ON_CLICK,
                event -> {

                    sendToChecker(
                            batch.getBatchId()
                    );
                }
        );


        actionCell.appendChild(
                sendButton
        );

        item.appendChild(
                actionCell
        );
    }


    // =========================================================
    // SEND TO CHECKER - CONFIRMATION
    // =========================================================

    private void sendToChecker(
            String batchId) {

        try {

            if (batchId == null
                    || batchId.trim().isEmpty()) {

                Messagebox.show(
                        "Invalid batch.",
                        "Error",
                        Messagebox.OK,
                        Messagebox.ERROR
                );

                return;
            }


            /*
             * Ask confirmation
             */
            Messagebox.show(

                    "Are you sure you want to send batch "
                    + batchId
                    + " to Checker?",

                    "Confirm Submission",

                    Messagebox.YES
                    | Messagebox.NO,

                    Messagebox.QUESTION,

                    event -> {

                        if (Messagebox.ON_YES
                                .equals(
                                        event.getName()
                                )) {

                            processSend(
                                    batchId
                            );
                        }
                    }
            );


        } catch (Exception e) {

            e.printStackTrace();

            Messagebox.show(
                    "Unable to send batch.",
                    "Error",
                    Messagebox.OK,
                    Messagebox.ERROR
            );
        }
    }


    // =========================================================
    // ACTUAL SEND OPERATION
    // =========================================================

    private void processSend(
            String batchId) {

        try {

            System.out.println(
                    "Sending batch "
                    + batchId
                    + " to Checker..."
            );


            /*
             * Service
             *
             * Service will call DAO.
             */
            boolean success =
                    service.sendToChecker(
                            batchId,
                            userId
                    );


            if (success) {

                Messagebox.show(

                        "Batch "
                        + batchId
                        + " has been successfully "
                        + "sent to Checker.",

                        "Success",

                        Messagebox.OK,

                        Messagebox.INFORMATION
                );


                /*
                 * Reload the table.
                 *
                 * Because status is now:
                 *
                 * SENT_TO_CHECKER
                 *
                 * the batch will no longer satisfy:
                 *
                 * READY_FOR_CHECKER
                 *
                 * therefore it disappears.
                 */
                loadReadyBatches();


            } else {

                Messagebox.show(

                        "Batch "
                        + batchId
                        + " could not be sent.\n\n"
                        + "It may already have been sent "
                        + "or it is not assigned to you.",

                        "Send Failed",

                        Messagebox.OK,

                        Messagebox.EXCLAMATION
                );


                /*
                 * Refresh in case another process
                 * changed the status.
                 */
                loadReadyBatches();
            }


        } catch (Exception e) {

            e.printStackTrace();

            Messagebox.show(

                    "Error while sending batch to Checker.\n"
                    + e.getMessage(),

                    "Error",

                    Messagebox.OK,

                    Messagebox.ERROR
            );
        }
    }


    // =========================================================
    // SAFE STRING
    // =========================================================

    private String safeValue(
            String value) {

        if (value == null
                || value.trim().isEmpty()) {

            return "-";
        }

        return value;
    }
}

