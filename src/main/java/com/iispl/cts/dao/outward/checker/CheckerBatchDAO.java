package com.iispl.cts.dao.outward.checker;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import com.iispl.cts.data.CTSStaticData;
import com.iispl.cts.model.outward.OutwardBatch;

public class CheckerBatchDAO {

    /*
     * ============================================================
     * GET BATCHES LOCKED BY CURRENT CHECKER
     * ============================================================
     */

    public List<OutwardBatch> getCheckerBatches(
            String checkerUserId) {

        List<OutwardBatch> batches =
                new ArrayList<>();

        /*
         * This query gets only batches assigned to
         * the currently logged-in Checker.
         *
         * The Dashboard assignBatch() method creates:
         *
         * assignment_role   = CHECKER
         * assignment_status = IN_PROGRESS
         *
         * Therefore we search for both:
         * ASSIGNED and IN_PROGRESS
         */

        String sql =
                "SELECT " +
                "    ob.batch_number, " +
                "    ob.cheque_count, " +
                "    cba.user_id " +
                "FROM public.outward_batch ob " +
                "INNER JOIN public.outward_batch_assignment cba " +
                "    ON ob.batch_number = cba.batch_number " +
                "WHERE cba.user_id = ? " +
                "AND UPPER(cba.assignment_role) = 'CHECKER' " +
                "AND UPPER(cba.assignment_status) " +
                "    IN ('ASSIGNED', 'IN_PROGRESS') " +
                "ORDER BY cba.assigned_at DESC";

        try (
                Connection con =
                        CTSStaticData.getConnection();

                PreparedStatement ps =
                        con.prepareStatement(sql)
        ) {

            /*
             * Set currently logged-in Checker ID.
             */

            ps.setInt(
                    1,
                    Integer.parseInt(checkerUserId)
            );

            try (
                    ResultSet rs =
                            ps.executeQuery()
            ) {

                while (rs.next()) {

                    OutwardBatch batch =
                            new OutwardBatch();

                    /*
                     * Batch Number
                     */

                    batch.setBatchNumber(
                            rs.getString(
                                    "batch_number"
                            )
                    );

                    /*
                     * Total Cheques
                     */

                    batch.setNumberOfCheques(
                            rs.getInt(
                                    "cheque_count"
                            )
                    );

                    /*
                     * Checker who locked the batch
                     */

                    int checkerId =
                            rs.getInt("user_id");

                    batch.setCheckerUserNumber(
                            String.valueOf(checkerId)
                    );

                    /*
                     * Display Status
                     */

                    batch.setBatchStatus(
                            "Locked by Checker"
                    );

                    /*
                     * Lock information
                     */

                    batch.setLockedBy(
                            String.valueOf(checkerId)
                    );

                    batch.setLockStatus(
                            "LOCKED"
                    );

                    /*
                     * Add batch to list
                     */

                    batches.add(batch);
                }
            }

        } catch (Exception e) {

            e.printStackTrace();

            throw new RuntimeException(
                    "Unable to load batches locked by Checker.",
                    e
            );
        }

        return batches;
    }
}