package com.iispl.cts.dao.outward.checker;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import com.iispl.cts.data.CTSStaticData;
import com.iispl.cts.model.outward.OutwardBatch;

public class CheckerDashboardDAO {


/**
 * Get batches which have been sent
 * from Maker to Checker.
 */
public List<OutwardBatch> getCheckerBatches() {

    List<OutwardBatch> batches =
            new ArrayList<>();

    String sql =
            "SELECT "
            + "batch_id, "
            + "total_cheques, "
            + "status, "
            + "user_id, "
            + "assignment "
            + "FROM public.outward_batch "
            + "WHERE status = 'READY_FOR_CHECKER' "
            + "ORDER BY batch_id";

    try (
            Connection con =
                    CTSStaticData.getConnection();

            PreparedStatement ps =
                    con.prepareStatement(sql);

            ResultSet rs =
                    ps.executeQuery()
    ) {

        while (rs.next()) {

            OutwardBatch batch =
                    new OutwardBatch();

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

            batch.setAssignment(
                    rs.getString("assignment")
            );

            batches.add(batch);
        }

    } catch (Exception e) {

        e.printStackTrace();
    }

    return batches;
}

/**
 * Number of batches waiting for Checker.
 */
public int getQueueCount() {

    String sql =
            "SELECT COUNT(*) "
            + "FROM public.outward_batch "
            + "WHERE status = 'READY_FOR_CHECKER'";

    try (
            Connection con =
                    CTSStaticData.getConnection();

            PreparedStatement ps =
                    con.prepareStatement(sql);

            ResultSet rs =
                    ps.executeQuery()
    ) {

        if (rs.next()) {
            return rs.getInt(1);
        }

    } catch (Exception e) {

        e.printStackTrace();
    }

    return 0;
}

/**
 * Total cheques waiting for Checker.
 */
public int getPendingChequeCount() {

    String sql =
            "SELECT COALESCE("
            + "SUM(total_cheques), 0) "
            + "FROM public.outward_batch "
            + "WHERE status = 'READY_FOR_CHECKER'";

    try (
            Connection con =
                    CTSStaticData.getConnection();

            PreparedStatement ps =
                    con.prepareStatement(sql);

            ResultSet rs =
                    ps.executeQuery()
    ) {

        if (rs.next()) {
            return rs.getInt(1);
        }

    } catch (Exception e) {

        e.printStackTrace();
    }

    return 0;
}

/**
 * Accepted cheque count.
 *
 * This will be connected to the actual
 * checker status storage when your existing
 * database structure is used.
 */
public int getAcceptedCount() {

    return 0;
}

/**
 * Rejected cheque count.
 *
 * This will be connected to the actual
 * checker status storage when your existing
 * database structure is used.
 */
public int getRejectedCount() {

    return 0;
}


}
