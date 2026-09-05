package com.iispl.cts.dao.outward;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import com.iispl.cts.data.CTSStaticData;
import com.iispl.cts.model.outward.OutwardBatch;

public class OutwardMakerMicrRepairDAO {

    /*
     * 1. Get ALL batches
     */
    public List<OutwardBatch> getAllBatches() {

        List<OutwardBatch> batches = new ArrayList<>();

        String sql =
                "SELECT batch_number, " +
                "       branch_code, " +
                "       cheque_count, " +
                "       batch_folder_path, " +
                "       created_by, " +
                "       created_at, " +
                "       batch_status " +
                "FROM outward_batch " +
                "ORDER BY batch_number";

        try (Connection connection = CTSStaticData.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {

                OutwardBatch batch = new OutwardBatch();

                batch.setBatchNumber(
                        rs.getString("batch_number"));

                batch.setBranchCode(
                        rs.getString("branch_code"));

                batch.setChequeCount(
                        rs.getInt("cheque_count"));

                batch.setBatchFolderPath(
                        rs.getString("batch_folder_path"));

                batch.setCreatedBy(
                        rs.getInt("created_by"));

                if (rs.getTimestamp("created_at") != null) {
                    batch.setCreatedAt(
                            rs.getTimestamp("created_at")
                              .toLocalDateTime());
                }

                batch.setBatchStatus(
                        rs.getString("batch_status"));

                batches.add(batch);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return batches;
    }


    /*
     * 2. Get all MICR errors after validation
     */
    public List<String> getMicrErrorCheques(String batchNumber) {

        List<String> chequeNumbers = new ArrayList<>();

        String sql =
                "SELECT cheque_number " +
                "FROM outward_cheque " +
                "WHERE batch_number = ? " +
                "AND cheque_status = 'MICR_ERROR' " +
                "ORDER BY cheque_number";

        try (Connection connection = CTSStaticData.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setString(1, batchNumber);

            try (ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {
                    chequeNumbers.add(
                            rs.getString("cheque_number"));
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return chequeNumbers;
    }


    /*
     * 3. Get batches which contain MICR errors
     *
     * This is called AFTER validation.
     */
    public List<OutwardBatch> getMicrRepairBatches() {

        List<OutwardBatch> batches = new ArrayList<>();

        String sql =
                "SELECT DISTINCT " +
                "       ob.batch_number, " +
                "       ob.branch_code, " +
                "       ob.cheque_count, " +
                "       ob.batch_folder_path, " +
                "       ob.created_by, " +
                "       ob.created_at, " +
                "       ob.batch_status " +
                "FROM outward_batch ob " +
                "JOIN outward_cheque oc " +
                "ON ob.batch_number = oc.batch_number " +
                "WHERE oc.cheque_status = 'MICR_ERROR' " +
                "ORDER BY ob.batch_number";

        try (Connection connection = CTSStaticData.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {

                OutwardBatch batch = new OutwardBatch();

                batch.setBatchNumber(
                        rs.getString("batch_number"));

                batch.setBranchCode(
                        rs.getString("branch_code"));

                batch.setChequeCount(
                        rs.getInt("cheque_count"));

                batch.setBatchFolderPath(
                        rs.getString("batch_folder_path"));

                batch.setCreatedBy(
                        rs.getInt("created_by"));

                if (rs.getTimestamp("created_at") != null) {
                    batch.setCreatedAt(
                            rs.getTimestamp("created_at")
                              .toLocalDateTime());
                }

                batch.setBatchStatus(
                        rs.getString("batch_status"));

                batches.add(batch);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return batches;
    }


    /*
     * 4. Update cheque status after MICR repair
     */
    public void updateChequeStatus(
            String batchNumber,
            String chequeNumber,
            String status) {

        String sql =
                "UPDATE outward_cheque " +
                "SET cheque_status = ? " +
                "WHERE batch_number = ? " +
                "AND cheque_number = ?";

        try (Connection connection = CTSStaticData.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setString(1, status);
            ps.setString(2, batchNumber);
            ps.setString(3, chequeNumber);

            ps.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /*
     * 5. Update batch status
     */
    public void updateBatchStatus(
            String batchNumber,
            String status) {

        String sql =
                "UPDATE outward_batch " +
                "SET batch_status = ? " +
                "WHERE batch_number = ?";

        try (Connection connection = CTSStaticData.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setString(1, status);
            ps.setString(2, batchNumber);

            ps.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}