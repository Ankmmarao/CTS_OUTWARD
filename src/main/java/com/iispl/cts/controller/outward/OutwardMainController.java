package com.iispl.cts.controller.outward;

import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Div;
import org.zkoss.zul.Include;

import com.iispl.cts.model.outward.UserSession;

public class OutwardMainController extends SelectorComposer<Div> {

    private static final long serialVersionUID = 1L;

    @Wire
    private Include contentArea;

    @Override
    public void doAfterCompose(Div component) throws Exception {

        super.doAfterCompose(component);

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
                    "/outward/maker/dashboard.zul"
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
                    "/outward/capture/dashboard.zul"
                );
                break;

            default:
                break;
        }
    }
}