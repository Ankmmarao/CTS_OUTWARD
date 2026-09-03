package com.iispl.cts.service.outward;

import java.util.List;

import com.iispl.cts.dao.outward.OutwardMakerMicrRepairDAO;
import com.iispl.cts.model.outward.OutwardBatch;

public class OutwardMakerMicrRepairService {

    private final OutwardMakerMicrRepairDAO dao;

    public OutwardMakerMicrRepairService() {

        dao =
                new OutwardMakerMicrRepairDAO();
    }

    public List<OutwardBatch> getBatches() {

        return dao.getBatches();
    }
}