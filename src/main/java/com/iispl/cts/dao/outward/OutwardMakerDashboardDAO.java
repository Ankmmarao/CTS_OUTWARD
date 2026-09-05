package com.iispl.cts.dao.outward;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.iispl.cts.data.CTSStaticData;
import com.iispl.cts.model.outward.OutwardBatch;
import com.iispl.cts.model.outward.OutwardCheque;

public class OutwardMakerDashboardDAO {

    // =========================================================
    // LOAD ALL BATCHES FOR MAKER DASHBOARD
    // =========================================================

    public List<OutwardBatch> getBatches() throws SQLException {

        List<OutwardBatch> batches = new ArrayList<>();

        /*
         * Do not use batch_id directly here because the current
         * database reported:
         *
         * ERROR: column "batch_id" does not exist
         *
         * Therefore the actual columns are read dynamically.
         */
        String sql =
                "SELECT * " +
                "FROM public.outward_batch " +
                "ORDER BY 1";

        System.out.println("======================================");
        System.out.println("MAKER DASHBOARD DAO");
        System.out.println("Loading batches...");
        System.out.println("SQL = " + sql);
        System.out.println("======================================");

        try (
                Connection con = CTSStaticData.getConnection();
                PreparedStatement ps = con.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()
        ) {

            ResultSetMetaData metaData =
                    rs.getMetaData();

            System.out.println(
                    "outward_batch columns:"
            );

            for (int i = 1;
                    i <= metaData.getColumnCount();
                    i++) {

                System.out.println(
                        i + " -> "
                        + metaData.getColumnName(i)
                );
            }

            System.out.println("--------------------------------------");

            while (rs.next()) {

                OutwardBatch batch =
                        new OutwardBatch();

                // -------------------------------------------------
                // BATCH NUMBER
                // -------------------------------------------------

                String batchNumber =
                        getString(
                                rs,
                                "batch_number",
                                "batch_no",
                                "batch_id",
                                "batchid"
                        );

                batch.setBatchNumber(batchNumber);

                // -------------------------------------------------
                // BRANCH CODE
                // -------------------------------------------------

                batch.setBranchCode(
                        getString(
                                rs,
                                "branch_code",
                                "branch"
                        )
                );

                // -------------------------------------------------
                // NUMBER OF CHEQUES
                // -------------------------------------------------

                Integer numberOfCheques =
                        getInteger(
                                rs,
                                "number_of_cheques",
                                "total_cheques",
                                "cheque_count",
                                "total_cheque"
                        );

                if (numberOfCheques == null) {
                    numberOfCheques = 0;
                }

                batch.setNumberOfCheques(
                        numberOfCheques
                );

                // -------------------------------------------------
                // TOTAL AMOUNT
                // -------------------------------------------------

                batch.setTotalAmount(
                        getBigDecimal(
                                rs,
                                "total_amount"
                        )
                );

                // -------------------------------------------------
                // BATCH FOLDER
                // -------------------------------------------------

                batch.setBatchFolderPath(
                        getString(
                                rs,
                                "batch_folder_path",
                                "batch_folder"
                        )
                );

                // -------------------------------------------------
                // XML FILE
                // -------------------------------------------------

                batch.setXmlFilePath(
                        getString(
                                rs,
                                "xml_file_path",
                                "xml_file"
                        )
                );

                // -------------------------------------------------
                // CREATED BY
                // -------------------------------------------------

                batch.setCreatedBy(
                        getString(
                                rs,
                                "created_by"
                        )
                );

                // -------------------------------------------------
                // CREATED AT
                // -------------------------------------------------

                batch.setCreatedAt(
                        getLocalDateTime(
                                rs,
                                "created_at"
                        )
                );

                // -------------------------------------------------
                // UPDATED BY
                // -------------------------------------------------

                batch.setUpdatedBy(
                        getString(
                                rs,
                                "updated_by"
                        )
                );

                // -------------------------------------------------
                // UPDATED AT
                // -------------------------------------------------

                batch.setUpdatedAt(
                        getLocalDateTime(
                                rs,
                                "updated_at"
                        )
                );

                // -------------------------------------------------
                // BATCH STATUS
                // -------------------------------------------------

                String status =
                        getString(
                                rs,
                                "batch_status",
                                "status"
                        );

                if (status != null) {
                    status = status.trim();
                }

                batch.setBatchStatus(status);

                // -------------------------------------------------
                // MAKER USER
                // -------------------------------------------------

                String makerUser =
                        getString(
                                rs,
                                "maker_user_number",
                                "maker_user_id",
                                "user_id"
                        );

                if (makerUser != null) {
                    makerUser = makerUser.trim();
                }

                batch.setMakerUserNumber(
                        makerUser
                );

                // -------------------------------------------------
                // MAKER STARTED AT
                // -------------------------------------------------

                batch.setMakerStartedAt(
                        getLocalDateTime(
                                rs,
                                "maker_started_at"
                        )
                );

                // -------------------------------------------------
                // MAKER COMPLETED AT
                // -------------------------------------------------

                batch.setMakerCompletedAt(
                        getLocalDateTime(
                                rs,
                                "maker_completed_at"
                        )
                );

                // -------------------------------------------------
                // CHECKER USER
                // -------------------------------------------------

                batch.setCheckerUserNumber(
                        getString(
                                rs,
                                "checker_user_number",
                                "checker_user_id"
                        )
                );

                // -------------------------------------------------
                // CHECKER STARTED AT
                // -------------------------------------------------

                batch.setCheckerStartedAt(
                        getLocalDateTime(
                                rs,
                                "checker_started_at"
                        )
                );

                // -------------------------------------------------
                // CHECKER COMPLETED AT
                // -------------------------------------------------

                batch.setCheckerCompletedAt(
                        getLocalDateTime(
                                rs,
                                "checker_completed_at"
                        )
                );

                // -------------------------------------------------
                // LOCKED BY
                // -------------------------------------------------

                String lockedBy =
                        getString(
                                rs,
                                "locked_by"
                        );

                /*
                 * If there is no separate locked_by column,
                 * use the Maker user as the fallback.
                 */
                if (lockedBy == null
                        || lockedBy.trim().isEmpty()) {

                    lockedBy = makerUser;
                }

                batch.setLockedBy(lockedBy);

                // -------------------------------------------------
                // LOCKED AT
                // -------------------------------------------------

                batch.setLockedAt(
                        getLocalDateTime(
                                rs,
                                "locked_at"
                        )
                );

                // -------------------------------------------------
                // LOCK STATUS
                // -------------------------------------------------

                String lockStatus =
                        getString(
                                rs,
                                "lock_status"
                        );

                String assignment =
                        getString(
                                rs,
                                "assignment",
                                "batch_assignment"
                        );

                if (lockStatus == null
                        || lockStatus.trim().isEmpty()) {

                    lockStatus = assignment;
                }

                if (lockStatus != null) {
                    lockStatus = lockStatus.trim();
                }

                if (lockStatus == null
                        || lockStatus.isEmpty()) {

                    batch.setLockStatus(null);

                } else if (
                        "ASSIGNED".equalsIgnoreCase(
                                lockStatus)
                        || "IN_PROGRESS".equalsIgnoreCase(
                                lockStatus)
                        || "LOCKED".equalsIgnoreCase(
                                lockStatus)
                ) {

                    batch.setLockStatus("LOCKED");

                } else {

                    batch.setLockStatus(lockStatus);
                }

                // -------------------------------------------------
                // ADD BATCH
                // -------------------------------------------------

                batches.add(batch);

                // -------------------------------------------------
                // DEBUG
                // -------------------------------------------------

                System.out.println(
                        "Batch loaded: "
                        + batch.getBatchNumber()
                        + " | Cheques="
                        + batch.getNumberOfCheques()
                        + " | Status="
                        + batch.getBatchStatus()
                        + " | Maker="
                        + batch.getMakerUserNumber()
                        + " | LockStatus="
                        + batch.getLockStatus()
                        + " | LockedBy="
                        + batch.getLockedBy()
                );
            }
        }

        System.out.println(
                "Total batches loaded = "
                + batches.size()
        );

        System.out.println("======================================");
        System.out.println(
                "MAKER DASHBOARD DAO LOAD COMPLETE"
        );
        System.out.println("======================================");

        return batches;
    }


    // =========================================================
    // ASSIGN BATCH TO MAKER
    // =========================================================

    public boolean assignBatch(
            String batchNumber,
            String userId) throws SQLException {

        if (batchNumber == null
                || batchNumber.trim().isEmpty()) {

            return false;
        }

        if (userId == null
                || userId.trim().isEmpty()) {

            return false;
        }

        String batchColumn =
                findColumn(
                        "batch_number",
                        "batch_no",
                        "batch_id",
                        "batchid"
                );

        String userColumn =
                findColumn(
                        "maker_user_number",
                        "maker_user_id",
                        "user_id"
                );

        String assignmentColumn =
                findColumn(
                        "assignment",
                        "batch_assignment"
                );

        if (batchColumn == null) {

            throw new SQLException(
                    "Batch number column not found "
                    + "in public.outward_batch."
            );
        }

        if (userColumn == null) {

            throw new SQLException(
                    "Maker/user column not found "
                    + "in public.outward_batch."
            );
        }

        if (assignmentColumn == null) {

            throw new SQLException(
                    "Assignment column not found "
                    + "in public.outward_batch."
            );
        }

        String sql =
                "UPDATE public.outward_batch "
                + "SET " + userColumn + " = ?, "
                + assignmentColumn
                + " = 'IN_PROGRESS' "
                + "WHERE " + batchColumn + " = ? "
                + "AND " + userColumn + " IS NULL "
                + "AND ("
                + assignmentColumn
                + " IS NULL OR TRIM("
                + assignmentColumn
                + ") = '')";

        System.out.println(
                "Assigning batch "
                + batchNumber
                + " to maker "
                + userId
        );

        try (
                Connection con =
                        CTSStaticData.getConnection();
                PreparedStatement ps =
                        con.prepareStatement(sql)
        ) {

            ps.setString(
                    1,
                    userId.trim()
            );

            ps.setString(
                    2,
                    batchNumber.trim()
            );

            int rows =
                    ps.executeUpdate();

            System.out.println(
                    "Assignment rows updated = "
                    + rows
            );

            return rows == 1;
        }
    }


    // =========================================================
    // LOAD CHEQUES FOR BATCH
    // =========================================================

    public List<OutwardCheque> getCheques(
            String batchNumber) throws SQLException {

        List<OutwardCheque> cheques =
                new ArrayList<>();

        if (batchNumber == null
                || batchNumber.trim().isEmpty()) {

            return cheques;
        }

        String batchColumn =
                findChequeColumn(
                        "batch_number",
                        "batch_no",
                        "batch_id"
                );

        if (batchColumn == null) {

            throw new SQLException(
                    "Batch column not found "
                    + "in public.outward_cheque."
            );
        }

        String sql =
                "SELECT * "
                + "FROM public.outward_cheque "
                + "WHERE " + batchColumn + " = ? "
                + "ORDER BY 1";

        try (
                Connection con =
                        CTSStaticData.getConnection();
                PreparedStatement ps =
                        con.prepareStatement(sql)
        ) {

            ps.setString(
                    1,
                    batchNumber.trim()
            );

            try (ResultSet rs =
                    ps.executeQuery()) {

                while (rs.next()) {

                    OutwardCheque cheque =
                            new OutwardCheque();

                    cheque.setBatchNumber(
                            getString(
                                    rs,
                                    "batch_number",
                                    "batch_no",
                                    "batch_id"
                            )
                    );

                    cheque.setChequeNumber(
                            getString(
                                    rs,
                                    "cheque_number",
                                    "cheque_no"
                            )
                    );

                    cheque.setDrawerAccountNumber(
                            getString(
                                    rs,
                                    "drawer_account_number",
                                    "account_number"
                            )
                    );

                    cheque.setAmount(
                            getBigDecimal(
                                    rs,
                                    "amount"
                            )
                    );

                    java.sql.Date date =
                            getDate(
                                    rs,
                                    "cheque_date"
                            );

                    if (date != null) {

                        cheque.setChequeDate(
                                date.toLocalDate()
                        );
                    }

                    cheque.setFrontImagePath(
                            getString(
                                    rs,
                                    "front_image"
                            )
                    );

                    cheque.setBackImagePath(
                            getString(
                                    rs,
                                    "back_image"
                            )
                    );

                    cheques.add(cheque);
                }
            }
        }

        return cheques;
    }


    // =========================================================
    // VALIDATE BATCH
    // =========================================================

    public boolean isBatchValid(
            String batchNumber) throws SQLException {

        List<OutwardCheque> cheques =
                getCheques(batchNumber);

        if (cheques.isEmpty()) {
            return false;
        }

        for (OutwardCheque cheque : cheques) {

            if (cheque.getDrawerAccountNumber() == null
                    || cheque.getDrawerAccountNumber()
                            .trim().isEmpty()) {

                return false;
            }

            if (cheque.getChequeDate() == null) {
                return false;
            }

            if (cheque.getAmount() == null) {
                return false;
            }
        }

        return true;
    }


    // =========================================================
    // UPDATE COMPLETED BATCH
    // =========================================================

    public boolean updateBatchIfCompleted(
            String batchNumber) throws SQLException {

        if (batchNumber == null
                || batchNumber.trim().isEmpty()) {

            return false;
        }

        String batchColumn =
                findColumn(
                        "batch_number",
                        "batch_no",
                        "batch_id",
                        "batchid"
                );

        String statusColumn =
                findColumn(
                        "batch_status",
                        "status"
                );

        if (batchColumn == null
                || statusColumn == null) {

            throw new SQLException(
                    "Batch/status column not found "
                    + "in public.outward_batch."
            );
        }

        String assignmentColumn =
                findColumn(
                        "assignment",
                        "batch_assignment"
                );

        String sql;

        if (assignmentColumn != null) {

            sql =
                    "UPDATE public.outward_batch "
                    + "SET " + statusColumn
                    + " = 'COMPLETED', "
                    + assignmentColumn
                    + " = 'COMPLETED' "
                    + "WHERE " + batchColumn
                    + " = ? "
                    + "AND " + statusColumn
                    + " <> 'COMPLETED'";

        } else {

            sql =
                    "UPDATE public.outward_batch "
                    + "SET " + statusColumn
                    + " = 'COMPLETED' "
                    + "WHERE " + batchColumn
                    + " = ? "
                    + "AND " + statusColumn
                    + " <> 'COMPLETED'";
        }

        try (
                Connection con =
                        CTSStaticData.getConnection();
                PreparedStatement ps =
                        con.prepareStatement(sql)
        ) {

            ps.setString(
                    1,
                    batchNumber.trim()
            );

            int rows =
                    ps.executeUpdate();

            return rows > 0;
        }
    }


    // =========================================================
    // FIND COLUMN IN OUTWARD_BATCH
    // =========================================================

    private String findColumn(
            String... possibleColumns)
            throws SQLException {

        String sql =
                "SELECT * "
                + "FROM public.outward_batch "
                + "LIMIT 0";

        try (
                Connection con =
                        CTSStaticData.getConnection();
                PreparedStatement ps =
                        con.prepareStatement(sql);
                ResultSet rs =
                        ps.executeQuery()
        ) {

            ResultSetMetaData md =
                    rs.getMetaData();

            for (String possible :
                    possibleColumns) {

                for (int i = 1;
                        i <= md.getColumnCount();
                        i++) {

                    if (possible.equalsIgnoreCase(
                            md.getColumnName(i))) {

                        return md.getColumnName(i);
                    }
                }
            }
        }

        return null;
    }


    // =========================================================
    // FIND COLUMN IN OUTWARD_CHEQUE
    // =========================================================

    private String findChequeColumn(
            String... possibleColumns)
            throws SQLException {

        String sql =
                "SELECT * "
                + "FROM public.outward_cheque "
                + "LIMIT 0";

        try (
                Connection con =
                        CTSStaticData.getConnection();
                PreparedStatement ps =
                        con.prepareStatement(sql);
                ResultSet rs =
                        ps.executeQuery()
        ) {

            ResultSetMetaData md =
                    rs.getMetaData();

            for (String possible :
                    possibleColumns) {

                for (int i = 1;
                        i <= md.getColumnCount();
                        i++) {

                    if (possible.equalsIgnoreCase(
                            md.getColumnName(i))) {

                        return md.getColumnName(i);
                    }
                }
            }
        }

        return null;
    }


    // =========================================================
    // GET STRING
    // =========================================================

    private String getString(
            ResultSet rs,
            String... columns)
            throws SQLException {

        for (String column : columns) {

            if (hasColumn(rs, column)) {

                return rs.getString(column);
            }
        }

        return null;
    }


    // =========================================================
    // GET INTEGER
    // =========================================================

    private Integer getInteger(
            ResultSet rs,
            String... columns)
            throws SQLException {

        for (String column : columns) {

            if (hasColumn(rs, column)) {

                int value =
                        rs.getInt(column);

                if (rs.wasNull()) {
                    return null;
                }

                return value;
            }
        }

        return null;
    }


    // =========================================================
    // GET BIG DECIMAL
    // =========================================================

    private java.math.BigDecimal getBigDecimal(
            ResultSet rs,
            String... columns)
            throws SQLException {

        for (String column : columns) {

            if (hasColumn(rs, column)) {

                return rs.getBigDecimal(column);
            }
        }

        return null;
    }


    // =========================================================
    // GET DATE
    // =========================================================

    private java.sql.Date getDate(
            ResultSet rs,
            String... columns)
            throws SQLException {

        for (String column : columns) {

            if (hasColumn(rs, column)) {

                return rs.getDate(column);
            }
        }

        return null;
    }


    // =========================================================
    // GET LOCAL DATE TIME
    // =========================================================

    private java.time.LocalDateTime getLocalDateTime(
            ResultSet rs,
            String... columns)
            throws SQLException {

        for (String column : columns) {

            if (hasColumn(rs, column)) {

                java.sql.Timestamp timestamp =
                        rs.getTimestamp(column);

                if (timestamp != null) {
                    return timestamp
                            .toLocalDateTime();
                }
            }
        }

        return null;
    }


    // =========================================================
    // CHECK COLUMN
    // =========================================================

    private boolean hasColumn(
            ResultSet rs,
            String column)
            throws SQLException {

        ResultSetMetaData md =
                rs.getMetaData();

        for (int i = 1;
                i <= md.getColumnCount();
                i++) {

            if (column.equalsIgnoreCase(
                    md.getColumnName(i))) {

                return true;
            }
        }

        return false;
    }
}