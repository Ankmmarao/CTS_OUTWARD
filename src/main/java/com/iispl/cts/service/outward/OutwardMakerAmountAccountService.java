package com.iispl.cts.service.outward;

import java.util.List;

import com.iispl.cts.dao.outward.OutwardMakerAmountAccountDAO;
import com.iispl.cts.model.outward.OutwardBatch;

public class OutwardMakerAmountAccountService {

    private OutwardMakerAmountAccountDAO dao;

    public OutwardMakerAmountAccountService() {

        dao = new OutwardMakerAmountAccountDAO();
    }

    public List<OutwardBatch> getBatches() {

        return dao.getBatches();
    }
}