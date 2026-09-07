package com.iispl.cts.dao.outward;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.iispl.cts.data.CTSStaticData;
import com.iispl.cts.model.outward.OutwardBatch;

public class OutwardMakerMicrRepairDAO {

    // Stores MICR error count for each batch
    private Map<String, Integer> micrErrorCounts = new HashMap<>();

    public List<OutwardBatch> getMicrErrorBatches() {

        List<OutwardBatch> batches = new ArrayList<>();

        String sql =
                "SELECT ob.batch_number, " +
                "       ob.cheque_count, " +
                "       COUNT(oc.cheque_number) AS micr_error_count " +
                "FROM outward_batch ob " +
                "INNER JOIN outward_cheque oc " +
                "        ON ob.batch_number = oc.batch_number " +
                "WHERE oc.cheque_status = 'MICR_ERROR' " +
                "GROUP BY ob.batch_number, ob.cheque_count " +
                "ORDER BY ob.batch_number";

        try (Connection connection = CTSStaticData.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            // Clear old values
            micrErrorCounts.clear();

            while (rs.next()) {

                String batchNumber = rs.getString("batch_number");

                int chequeCount = rs.getInt("cheque_count");

                int micrErrorCount = rs.getInt("micr_error_count");

                // Set values to OutwardBatch
                OutwardBatch batch = new OutwardBatch();

                batch.setBatchNumber(batchNumber);
                batch.setNumberOfCheques(chequeCount);

                batches.add(batch);

                // Store MICR error count separately
                micrErrorCounts.put(batchNumber,micrErrorCount);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return batches;
    }

    public int getMicrErrorCount(String batchNumber) {

        return micrErrorCounts.getOrDefault(batchNumber,0);
    }
}