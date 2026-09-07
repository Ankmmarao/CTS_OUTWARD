package com.iispl.cts.dao.outward.checker;

import com.iispl.cts.model.outward.OutwardBatch;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import com.iispl.cts.data.CTSStaticData;

public class CheckerReportsDAO {

    /**
     * Get only batches completed by Checker.
     */
    public List<OutwardBatch>
    getCheckerCompletedBatches() {

        List<OutwardBatch> batches =
                new ArrayList<>();

        String sql =
                "SELECT " +
                "batch_number, " +
                "branch_code, " +
                "cheque_count, " +
                "total_amount, " +
                "batch_folder_path, " +
                "xml_file_path, " +
                "created_by, " +
                "created_at, " +
                "updated_by, " +
                "updated_at, " +
                "batch_status, " +
                "maker_user_number, " +
                "maker_started_at, " +
                "maker_completed_at, " +
                "checker_user_number, " +
                "checker_started_at, " +
                "checker_completed_at, " +
                "locked_by, " +
                "locked_at, " +
                "lock_status " +
                "FROM public.outward_batch " +
                "WHERE batch_status = ? " +
                "ORDER BY checker_completed_at DESC";

        try (Connection connection =
                     CTSStaticData.getConnection();

             PreparedStatement ps =
                     connection.prepareStatement(sql)) {

            ps.setString(
                    1,
                    "ASSIGNED"
            );

            try (ResultSet rs =
                         ps.executeQuery()) {

                while (rs.next()) {

                    OutwardBatch batch =
                            new OutwardBatch();

                    batch.setBatchNumber(
                            rs.getString(
                                    "batch_number"
                            )
                    );

                    batch.setBranchCode(
                            rs.getString(
                                    "branch_code"
                            )
                    );

                    batch.setNumberOfCheques(
                            rs.getInt(
                                    "cheque_count"
                            )
                    );

                    batch.setTotalAmount(
                            rs.getBigDecimal(
                                    "total_amount"
                            )
                    );

                    batch.setBatchFolderPath(
                            rs.getString(
                                    "batch_folder_path"
                            )
                    );

                    batch.setXmlFilePath(
                            rs.getString(
                                    "xml_file_path"
                            )
                    );

                    batch.setCreatedBy(
                            rs.getString(
                                    "created_by"
                            )
                    );

                    if (rs.getTimestamp(
                            "created_at") != null) {

                        batch.setCreatedAt(
                                rs.getTimestamp(
                                        "created_at"
                                ).toLocalDateTime()
                        );
                    }

                    batch.setUpdatedBy(
                            rs.getString(
                                    "updated_by"
                            )
                    );

                    if (rs.getTimestamp(
                            "updated_at") != null) {

                        batch.setUpdatedAt(
                                rs.getTimestamp(
                                        "updated_at"
                                ).toLocalDateTime()
                        );
                    }

                    batch.setBatchStatus(
                            rs.getString(
                                    "batch_status"
                            )
                    );

                    batch.setMakerUserNumber(
                            rs.getString(
                                    "maker_user_number"
                            )
                    );

                    if (rs.getTimestamp(
                            "maker_started_at") != null) {

                        batch.setMakerStartedAt(
                                rs.getTimestamp(
                                        "maker_started_at"
                                ).toLocalDateTime()
                        );
                    }

                    if (rs.getTimestamp(
                            "maker_completed_at") != null) {

                        batch.setMakerCompletedAt(
                                rs.getTimestamp(
                                        "maker_completed_at"
                                ).toLocalDateTime()
                        );
                    }

                    batch.setCheckerUserNumber(
                            rs.getString(
                                    "checker_user_number"
                            )
                    );

                    if (rs.getTimestamp(
                            "checker_started_at") != null) {

                        batch.setCheckerStartedAt(
                                rs.getTimestamp(
                                        "checker_started_at"
                                ).toLocalDateTime()
                        );
                    }

                    if (rs.getTimestamp(
                            "checker_completed_at") != null) {

                        batch.setCheckerCompletedAt(
                                rs.getTimestamp(
                                        "checker_completed_at"
                                ).toLocalDateTime()
                        );
                    }

                    batch.setLockedBy(
                            rs.getString(
                                    "locked_by"
                            )
                    );

                    if (rs.getTimestamp(
                            "locked_at") != null) {

                        batch.setLockedAt(
                                rs.getTimestamp(
                                        "locked_at"
                                ).toLocalDateTime()
                        );
                    }

                    batch.setLockStatus(
                            rs.getString(
                                    "lock_status"
                            )
                    );

                    batches.add(batch);
                }
            }

        } catch (Exception e) {

            e.printStackTrace();
        }

        return batches;
    }
}