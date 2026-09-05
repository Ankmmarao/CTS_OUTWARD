package com.iispl.cts.dao.outward;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.iispl.cts.data.CTSStaticData;
import com.iispl.cts.model.outward.OutwardCheque;

public class OutwardMakerDataEntryDetailDAO {

    public List<OutwardCheque> getCheques(String batchNumber) {
        List<OutwardCheque> cheques = new ArrayList<>();
        String sql = "SELECT batch_number, cheque_number, drawer_account_number, drawer_name, "
                   + "       payee_account_number, payee_name, amount, amount_in_words, "
                   + "       cheque_date, front_image_path, back_image_path, cheque_status "
                   + "FROM public.outward_cheque "
                   + "WHERE batch_number = ? "
                   + "ORDER BY cheque_number ASC";

        try (Connection con = CTSStaticData.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, batchNumber);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    OutwardCheque cheque = new OutwardCheque();
                    cheque.setBatchNumber(rs.getString("batch_number"));
                    cheque.setChequeNumber(rs.getString("cheque_number"));
                    cheque.setDrawerAccountNumber(rs.getString("drawer_account_number"));
                    cheque.setDrawerName(rs.getString("drawer_name"));
                    cheque.setDepositorAccountNumber(rs.getString("payee_account_number"));
                    cheque.setPayeeName(rs.getString("payee_name"));
                    cheque.setAmount(rs.getBigDecimal("amount"));
                    cheque.setAmountInWords(rs.getString("amount_in_words"));

                    Date dt = rs.getDate("cheque_date");
                    if (dt != null) {
                        cheque.setChequeDate(dt.toLocalDate());
                    }

                    cheque.setFrontImagePath(rs.getString("front_image_path"));
                    cheque.setBackImagePath(rs.getString("back_image_path"));
                    cheque.setChequeStatus(rs.getString("cheque_status"));
                    cheques.add(cheque);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return cheques;
    }

    public void saveCheque(OutwardCheque cheque) {
        String updateCheque = "UPDATE public.outward_cheque "
                            + "SET drawer_account_number = ?, drawer_name = ?, payee_name = ?, "
                            + "    amount = ?, amount_in_words = ?, cheque_date = ?, cheque_status = 'VERIFIED' "
                            + "WHERE batch_number = ? AND cheque_number = ?";

        String insertVerification = "INSERT INTO public.cheque_verification "
                                  + "(batch_number, cheque_number, verified_by, verification_status, verified_at) "
                                  + "VALUES (?, ?, 103, 'VERIFIED', CURRENT_TIMESTAMP) "
                                  + "ON CONFLICT (batch_number, cheque_number, verified_by) "
                                  + "DO UPDATE SET verification_status = 'VERIFIED', verified_at = CURRENT_TIMESTAMP";

        try (Connection con = CTSStaticData.getConnection();
             PreparedStatement ps1 = con.prepareStatement(updateCheque);
             PreparedStatement ps2 = con.prepareStatement(insertVerification)) {

            ps1.setString(1, cheque.getDrawerAccountNumber());
            ps1.setString(2, cheque.getDrawerName());
            ps1.setString(3, cheque.getPayeeName());
            ps1.setBigDecimal(4, cheque.getAmount());
            ps1.setString(5, cheque.getAmountInWords());
            ps1.setDate(6, cheque.getChequeDate() != null ? Date.valueOf(cheque.getChequeDate()) : null);
            ps1.setString(7, cheque.getBatchNumber());
            ps1.setString(8, cheque.getChequeNumber());
            ps1.executeUpdate();

            ps2.setString(1, cheque.getBatchNumber());
            ps2.setString(2, cheque.getChequeNumber());
            ps2.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void rejectCheque(OutwardCheque cheque, String reason) {
        String updateCheque = "UPDATE public.outward_cheque "
                            + "SET cheque_status = 'REJECTED' "
                            + "WHERE batch_number = ? AND cheque_number = ?";

        String insertRejection = "INSERT INTO public.cheque_rejection "
                               + "(batch_number, cheque_number, rejected_by, rejection_reason) "
                               + "VALUES (?, ?, 103, ?) "
                               + "ON CONFLICT (batch_number, cheque_number) "
                               + "DO UPDATE SET rejected_by = EXCLUDED.rejected_by, rejection_reason = EXCLUDED.rejection_reason";

        try (Connection con = CTSStaticData.getConnection();
             PreparedStatement ps1 = con.prepareStatement(updateCheque);
             PreparedStatement ps2 = con.prepareStatement(insertRejection)) {

            ps1.setString(1, cheque.getBatchNumber());
            ps1.setString(2, cheque.getChequeNumber());
            ps1.executeUpdate();

            ps2.setString(1, cheque.getBatchNumber());
            ps2.setString(2, cheque.getChequeNumber());
            ps2.setString(3, reason);
            ps2.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}