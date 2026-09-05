
package com.iispl.cts.dao.outward.checker;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import com.iispl.cts.data.CTSStaticData;
import com.iispl.cts.model.outward.OutwardCheque;

public class CheckerBatchDAO {

    // =========================================================
    // GET CHEQUES BY BATCH ID
    // =========================================================

    public List<OutwardCheque> getChequesByBatchId(String batchId) {

        List<OutwardCheque> cheques = new ArrayList<>();

        String sql =
                "SELECT " +
                "    batch_id, " +
                "    cheque_id, " +
                "    cheque_number, " +
                "    account_number, " +
                "    cheque_date, " +
                "    amount, " +
                "    scanned_micr, " +
                "    front_image, " +
                "    back_image " +
                "FROM public.outward_cheque " +
                "WHERE batch_id = ? " +
                "ORDER BY cheque_id";

        try (
                Connection con = CTSStaticData.getConnection();
                PreparedStatement ps = con.prepareStatement(sql)
        ) {

            ps.setString(1, batchId);

            try (ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {

                    OutwardCheque cheque = new OutwardCheque();

                    // =================================================
                    // BATCH ID
                    // =================================================

                    cheque.setBatchId(
                            rs.getString("batch_id")
                    );

                    // =================================================
                    // CHEQUE ID
                    // =================================================

                    cheque.setChequeId(
                            rs.getString("cheque_id")
                    );

                    // =================================================
                    // CHEQUE NUMBER
                    // =================================================

                    cheque.setChequeNumber(
                            rs.getString("cheque_number")
                    );

                    // =================================================
                    // ACCOUNT NUMBER
                    // =================================================

                    cheque.setAccountNumber(
                            rs.getString("account_number")
                    );

                    // =================================================
                    // CHEQUE DATE
                    // =================================================

                    cheque.setChequeDate(
                            rs.getString("cheque_date")
                    );

                    // =================================================
                    // AMOUNT
                    // =================================================

                    cheque.setAmount(
                            rs.getString("amount")
                    );

                    // =================================================
                    // SCANNED MICR
                    // =================================================

                    cheque.setMicr(
                            rs.getString("scanned_micr")
                    );

                    // =================================================
                    // FRONT IMAGE
                    // =================================================

                    cheque.setFrontImage(
                            rs.getString("front_image")
                    );

                    // =================================================
                    // BACK IMAGE
                    // =================================================

                    cheque.setBackImage(
                            rs.getString("back_image")
                    );

                    // =================================================
                    // DEFAULT CHECKER FLAGS
                    // =================================================

                    cheque.setFrontVerified(false);
                    cheque.setBackVerified(false);
                    cheque.setSaved(false);
                    cheque.setRejected(false);

                    cheques.add(cheque);
                }
            }

        } catch (Exception e) {

            e.printStackTrace();
        }

        return cheques;
    }
}

