package com.iispl.cts.controller.outward;

import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Div;

public class OutwardSidebarController extends SelectorComposer<Div> {

    private static final long serialVersionUID = 1L;

    @Wire
    private Div navDashboard;

    @Wire
    private Div navDataEntry;

    @Wire
    private Div navMicrRepair;

    @Wire
    private Div navAmountAccount;

    @Wire
    private Div navSendToChecker;


    @Override
    public void doAfterCompose(Div component) throws Exception {

        super.doAfterCompose(component);

        loadMenuByRole();
    }


    private void loadMenuByRole() {

        int roleId = LoginController.getCurrentRoleId();


        /*
         * OUTWARD MAKER
         * Role ID = 3
         */
        if (roleId == 3) {

            navDashboard.setVisible(true);
            navDataEntry.setVisible(true);
            navMicrRepair.setVisible(true);
            navAmountAccount.setVisible(true);
            navSendToChecker.setVisible(true);
        }


        /*
         * OUTWARD CHECKER
         * Role ID = 4
         *
         * Checker menu will be added later.
         */
        else if (roleId == 4) {

            navDashboard.setVisible(true);

            navDataEntry.setVisible(false);
            navMicrRepair.setVisible(false);
            navAmountAccount.setVisible(false);
            navSendToChecker.setVisible(false);
        }


        /*
         * CAPTURE OPERATOR
         * Role ID = 5
         *
         * Capture menu will be added later.
         */
        else if (roleId == 5) {

            navDashboard.setVisible(true);

            navDataEntry.setVisible(false);
            navMicrRepair.setVisible(false);
            navAmountAccount.setVisible(false);
            navSendToChecker.setVisible(false);
        }


        /*
         * NO VALID ROLE
         */
        else {

            navDashboard.setVisible(false);
            navDataEntry.setVisible(false);
            navMicrRepair.setVisible(false);
            navAmountAccount.setVisible(false);
            navSendToChecker.setVisible(false);
        }
    }
}