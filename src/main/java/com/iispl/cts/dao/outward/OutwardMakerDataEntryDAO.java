package com.iispl.cts.dao.outward;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.iispl.cts.data.CTSStaticData;
import com.iispl.cts.model.outward.OutwardBatch;

public class OutwardMakerDataEntryDAO {

    public List<OutwardBatch> getAllBatches() {
        List<OutwardBatch> batches = new ArrayList<>();
        String sql = "SELECT batch_number, cheque_count, batch_status "
                   + "FROM public.outward_batch "
                   + "ORDER BY batch_number ASC";

        try (Connection con = CTSStaticData.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                OutwardBatch batch = new OutwardBatch();
                batch.setBatchNumber(rs.getString("batch_number"));
                batch.setNumberOfCheques(rs.getInt("cheque_count"));
                batch.setBatchStatus(rs.getString("batch_status"));
                batches.add(batch);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return batches;
    }
}