package com.iispl.cts.dao.outward;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import com.iispl.cts.data.CTSStaticData;
import com.iispl.cts.model.outward.OutwardBatch;

public class OutwardMakerSendCheckerDAO {

    // =========================================================
    // GET BATCHES READY FOR CHECKER
    // =========================================================

    public List<OutwardBatch> getReadyBatches(String userId)
            throws Exception {

        List<OutwardBatch> batches = new ArrayList<>();

        String sql =
                "SELECT "
                + "ob.batch_id, "
                + "ob.total_cheques, "
                + "ob.status, "
                + "ob.user_id, "
                + "ob.assignment "
                + "FROM public.outward_batch ob "
                + "WHERE ob.status = ? "
                + "AND ob.user_id = ? "
                + "ORDER BY ob.batch_id";

        System.out.println("======================================");
        System.out.println("SEND TO CHECKER - LOAD READY BATCHES");
        System.out.println("======================================");
        System.out.println("Maker User ID = " + userId);
        System.out.println("SQL = " + sql);

        try (
                Connection con = CTSStaticData.getConnection();
                PreparedStatement ps = con.prepareStatement(sql)
        ) {

            ps.setString(1, "READY_FOR_CHECKER");
            ps.setString(2, userId);

            try (ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {

                    OutwardBatch batch = new OutwardBatch();

                    batch.setBatchId(
                            rs.getString("batch_id")
                    );

                    batch.setTotalCheques(
                            rs.getInt("total_cheques")
                    );

                    batch.setStatus(
                            rs.getString("status")
                    );

                    batch.setUserId(
                            rs.getString("user_id")
                    );

                    /*
                     * Set assignment only if your
                     * OutwardBatch model has this property.
                     */
                    batch.setAssignment(
                            rs.getString("assignment")
                    );

                    batches.add(batch);
                }
            }

            System.out.println(
                    "Ready batches found = " + batches.size()
            );

        } catch (Exception e) {

            System.err.println(
                    "ERROR WHILE LOADING READY BATCHES"
            );

            e.printStackTrace();

            throw e;
        }

        return batches;
    }


    // =========================================================
    // SEND BATCH TO CHECKER
    // =========================================================

    public boolean sendToChecker(
            String batchId,
            String userId) throws Exception {

        String sql =
                "UPDATE public.outward_batch "
                + "SET status = ? "
                + "WHERE batch_id = ? "
                + "AND user_id = ? "
                + "AND status = ?";

        System.out.println("======================================");
        System.out.println("SEND BATCH TO CHECKER");
        System.out.println("======================================");
        System.out.println("Batch ID = " + batchId);
        System.out.println("Maker ID = " + userId);
        System.out.println("SQL = " + sql);

        try (
                Connection con = CTSStaticData.getConnection();
                PreparedStatement ps = con.prepareStatement(sql)
        ) {

            ps.setString(
                    1,
                    "SENT_TO_CHECKER"
            );

            ps.setString(
                    2,
                    batchId
            );

            ps.setString(
                    3,
                    userId
            );

            ps.setString(
                    4,
                    "READY_FOR_CHECKER"
            );

            int rowsUpdated = ps.executeUpdate();

            System.out.println(
                    "Rows Updated = " + rowsUpdated
            );

            if (rowsUpdated == 1) {

                System.out.println(
                        "Batch " + batchId
                        + " successfully sent to Checker."
                );

                return true;
            }

            System.out.println(
                    "Batch " + batchId
                    + " was NOT sent to Checker."
            );

            return false;

        } catch (Exception e) {

            System.err.println(
                    "ERROR WHILE SENDING BATCH TO CHECKER"
            );

            e.printStackTrace();

            throw e;
        }
    }
}