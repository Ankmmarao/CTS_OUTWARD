package com.iispl.cts.dao.outward.checker;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;

import com.iispl.cts.data.CTSStaticData;

public class CheckerAssignmentDAO {

    /*
     * Take a batch for Checker processing.
     *
     * Returns true when the batch was successfully assigned.
     * Returns false when another Checker already has an active assignment.
     */
    public boolean takeBatch(String batchNumber, int checkerUserId) {

        String checkSql =
                "SELECT assignment_id " +
                "FROM outward_batch_assignment " +
                "WHERE batch_number = ? " +
                "AND UPPER(assignment_role) = 'CHECKER' " +
                "AND UPPER(assignment_status) IN ('ASSIGNED', 'IN_PROGRESS') " +
                "LIMIT 1";

        String insertSql =
                "INSERT INTO outward_batch_assignment " +
                "(batch_number, user_id, assignment_role, assigned_at, " +
                " started_at, assignment_status) " +
                "VALUES (?, ?, 'CHECKER', CURRENT_TIMESTAMP, " +
                " CURRENT_TIMESTAMP, 'IN_PROGRESS')";

        try (Connection connection = CTSStaticData.getConnection()) {

            /*
             * Lock the batch assignment rows while checking.
             */
            connection.setAutoCommit(false);

            try {

                try (PreparedStatement checkStatement =
                             connection.prepareStatement(checkSql)) {

                    checkStatement.setString(1, batchNumber);

                    try (ResultSet rs = checkStatement.executeQuery()) {

                        if (rs.next()) {
                            connection.rollback();
                            return false;
                        }
                    }
                }

                try (PreparedStatement insertStatement =
                             connection.prepareStatement(insertSql)) {

                    insertStatement.setString(1, batchNumber);
                    insertStatement.setInt(2, checkerUserId);

                    int rows = insertStatement.executeUpdate();

                    if (rows == 1) {
                        connection.commit();
                        return true;
                    }

                    connection.rollback();
                    return false;
                }

            } catch (Exception e) {
                connection.rollback();
                throw e;
            }

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(
                    "Error while taking Checker batch: " + batchNumber,
                    e
            );
        }
    }


    /*
     * Check whether a batch is currently assigned to any Checker.
     */
    public boolean isBatchAssigned(String batchNumber) {

        String sql =
                "SELECT 1 " +
                "FROM outward_batch_assignment " +
                "WHERE batch_number = ? " +
                "AND UPPER(assignment_role) = 'CHECKER' " +
                "AND UPPER(assignment_status) IN ('ASSIGNED', 'IN_PROGRESS') " +
                "LIMIT 1";

        try (Connection connection = CTSStaticData.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setString(1, batchNumber);

            try (ResultSet rs = statement.executeQuery()) {
                return rs.next();
            }

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(
                    "Error while checking batch assignment: " + batchNumber,
                    e
            );
        }
    }


    /*
     * Check whether this particular Checker has the batch.
     */
    public boolean isBatchAssignedToChecker(
            String batchNumber,
            int checkerUserId) {

        String sql =
                "SELECT 1 " +
                "FROM outward_batch_assignment " +
                "WHERE batch_number = ? " +
                "AND user_id = ? " +
                "AND UPPER(assignment_role) = 'CHECKER' " +
                "AND UPPER(assignment_status) IN ('ASSIGNED', 'IN_PROGRESS') " +
                "LIMIT 1";

        try (Connection connection = CTSStaticData.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setString(1, batchNumber);
            statement.setInt(2, checkerUserId);

            try (ResultSet rs = statement.executeQuery()) {
                return rs.next();
            }

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(
                    "Error while checking Checker assignment: " + batchNumber,
                    e
            );
        }
    }


    /*
     * Get the user ID of the Checker currently holding the batch.
     *
     * Returns null when no Checker has the batch.
     */
    public Integer getAssignedChecker(String batchNumber) {

        String sql =
                "SELECT user_id " +
                "FROM outward_batch_assignment " +
                "WHERE batch_number = ? " +
                "AND UPPER(assignment_role) = 'CHECKER' " +
                "AND UPPER(assignment_status) IN ('ASSIGNED', 'IN_PROGRESS') " +
                "ORDER BY assigned_at DESC " +
                "LIMIT 1";

        try (Connection connection = CTSStaticData.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setString(1, batchNumber);

            try (ResultSet rs = statement.executeQuery()) {

                if (rs.next()) {
                    return rs.getInt("user_id");
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(
                    "Error while getting assigned Checker: " + batchNumber,
                    e
            );
        }

        return null;
    }


    /*
     * Mark the current Checker assignment as completed.
     */
    public boolean completeBatch(
            String batchNumber,
            int checkerUserId) {

        String sql =
                "UPDATE outward_batch_assignment " +
                "SET assignment_status = 'COMPLETED', " +
                "    completed_at = CURRENT_TIMESTAMP " +
                "WHERE batch_number = ? " +
                "AND user_id = ? " +
                "AND UPPER(assignment_role) = 'CHECKER' " +
                "AND UPPER(assignment_status) IN ('ASSIGNED', 'IN_PROGRESS')";

        try (Connection connection = CTSStaticData.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setString(1, batchNumber);
            statement.setInt(2, checkerUserId);

            return statement.executeUpdate() == 1;

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(
                    "Error while completing Checker assignment: " + batchNumber,
                    e
            );
        }
    }


    /*
     * Get assignment ID for the current Checker.
     */
    public Long getAssignmentId(
            String batchNumber,
            int checkerUserId) {

        String sql =
                "SELECT assignment_id " +
                "FROM outward_batch_assignment " +
                "WHERE batch_number = ? " +
                "AND user_id = ? " +
                "AND UPPER(assignment_role) = 'CHECKER' " +
                "AND UPPER(assignment_status) IN ('ASSIGNED', 'IN_PROGRESS') " +
                "ORDER BY assigned_at DESC " +
                "LIMIT 1";

        try (Connection connection = CTSStaticData.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setString(1, batchNumber);
            statement.setInt(2, checkerUserId);

            try (ResultSet rs = statement.executeQuery()) {

                if (rs.next()) {
                    return rs.getLong("assignment_id");
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(
                    "Error while getting assignment ID: " + batchNumber,
                    e
            );
        }

        return null;
    }
}