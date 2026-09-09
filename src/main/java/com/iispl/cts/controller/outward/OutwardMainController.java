package com.iispl.cts.controller.outward;

import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zul.Borderlayout;
import org.zkoss.zul.Include;

import com.iispl.cts.model.outward.UserSession;

public class OutwardMainController extends GenericForwardComposer<Borderlayout> {

    private static final long serialVersionUID = 1L;

    private Include contentArea;

    @Override
    public void doAfterCompose(Borderlayout component) throws Exception {
        super.doAfterCompose(component);

        contentArea = (Include) component.getFellow("contentArea");

        loadInitialPage();
    }

    private void loadInitialPage() {

        UserSession userSession =
                LoginController.getCurrentUserSession();

        if (userSession == null) {
            return;
        }

        int roleId = userSession.getRoleId();

        switch (roleId) {

            case 3:
                // Outward Maker
                contentArea.setSrc(
                    "/outward/maker/outward-maker-dashboard.zul"
                );
                break;

            case 4:
                // Outward Checker
                contentArea.setSrc(
                    "/outward/checker/dashboard.zul"
                );
                break;

            case 5:
                // Capture Operator
                contentArea.setSrc(
                    "/capture-operator-batch-capture.zul"
                );
                break;

            default:
                break;
        }
    }
}