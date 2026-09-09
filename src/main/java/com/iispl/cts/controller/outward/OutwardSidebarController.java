package com.iispl.cts.controller.outward;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zul.Div;
import org.zkoss.zul.Vlayout;

import com.iispl.cts.model.outward.UserSession;

public class OutwardSidebarController
        extends GenericForwardComposer<Vlayout> {

    private static final long serialVersionUID = 1L;

    private Div navDashboard;

    private Div navMicrRepair;
    private Div navDataEntry;
    private Div navAmountAccount;
    private Div navSendToChecker;

    private Div navBatchesQueue;
    private Div navReports;
    private Div navSendToNPCI;

    private Div navBatchCapture;
    private Div navCapturedBatches;


    @Override
    public void doAfterCompose(Vlayout component)
            throws Exception {

        super.doAfterCompose(component);

        navDashboard = (Div) component.getFellow("navDashboard");

        navMicrRepair = (Div) component.getFellow("navMicrRepair");
        navDataEntry = (Div) component.getFellow("navDataEntry");
        navAmountAccount = (Div) component.getFellow("navAmountAccount");
        navSendToChecker = (Div) component.getFellow("navSendToChecker");

        navBatchesQueue = (Div) component.getFellow("navBatchesQueue");
        navReports = (Div) component.getFellow("navReports");
        navSendToNPCI = (Div) component.getFellow("navSendToNPCI");

        navBatchCapture = (Div) component.getFellow("navBatchCapture");
        navCapturedBatches = (Div) component.getFellow("navCapturedBatches");

        loadMenuByRole();
    }


    private void loadMenuByRole() {

        UserSession userSession =
                LoginController.getCurrentUserSession();

        if (userSession == null) {
            return;
        }

        int roleId = userSession.getRoleId();

        hideAllMenus();


        // Outward Maker
        if (roleId == 3) {

            navDashboard.setVisible(true);
            navMicrRepair.setVisible(true);
            navDataEntry.setVisible(true);
            navAmountAccount.setVisible(true);
            navSendToChecker.setVisible(true);
        }


        // Outward Checker
        else if (roleId == 4) {

            navDashboard.setVisible(true);
            navBatchesQueue.setVisible(true);
            navReports.setVisible(true);
            navSendToNPCI.setVisible(true);
        }


        // Capture Operator
        else if (roleId == 5) {

            navBatchCapture.setVisible(true);
            navCapturedBatches.setVisible(true);
        }
    }


    private void hideAllMenus() {

        navDashboard.setVisible(false);

        navMicrRepair.setVisible(false);
        navDataEntry.setVisible(false);
        navAmountAccount.setVisible(false);
        navSendToChecker.setVisible(false);

        navBatchesQueue.setVisible(false);
        navReports.setVisible(false);
        navSendToNPCI.setVisible(false);

        navBatchCapture.setVisible(false);
        navCapturedBatches.setVisible(false);
    }
}