package com.iispl.cts.dao.outward;

import java.util.List;

import com.iispl.cts.data.CTSStaticData;
import com.iispl.cts.model.outward.OutwardBatch;

public class OutwardMakerDataEntryDAO {

    public List<OutwardBatch> getBatches() {

        return CTSStaticData.getDataEntryBatches();
    }
}