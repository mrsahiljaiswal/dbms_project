package com.dbms_project.frontend.ui;

import com.dbms_project.frontend.db.Database;
import java.awt.*;
import java.sql.*;
import javax.swing.*;

public class PaymentPanel extends JPanel {
    private final PanelSwitcher switcher;
    private final int bookingId;
    public PaymentPanel(int bookingId, PanelSwitcher switcher) {
        this.bookingId = bookingId;
        this.switcher = switcher;
        setLayout(new GridBagLayout());
        setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        JLabel lblTitle = new JLabel("Payment");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblTitle.setForeground(new Color(33, 150, 243));
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2; gbc.insets = new Insets(10,10,20,10);
        add(lblTitle, gbc);
        gbc.gridwidth = 1; gbc.insets = new Insets(5,10,5,10);
        gbc.gridx = 0; gbc.gridy = 1;
        add(new JLabel("Amount:"), gbc);
        JTextField txtAmount = new JTextField(10);
        gbc.gridx = 1;
        add(txtAmount, gbc);
        gbc.gridx = 0; gbc.gridy = 2;
        add(new JLabel("Payment Method:"), gbc);
        JComboBox<String> methodBox = new JComboBox<>(new String[]{"Card", "UPI", "Wallet", "NetBanking"});
        gbc.gridx = 1;
        add(methodBox, gbc);
        JButton btnPay = new JButton("Pay");
        btnPay.setBackground(new Color(33, 150, 243));
        btnPay.setForeground(Color.WHITE);
        btnPay.setFocusPainted(false);
        btnPay.setFont(new Font("Segoe UI", Font.BOLD, 16));
        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2; gbc.insets = new Insets(20,10,10,10);
        add(btnPay, gbc);
        btnPay.addActionListener(e -> {
            String amountStr = txtAmount.getText();
            String method = (String) methodBox.getSelectedItem();
            if (amountStr.isEmpty() || method.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter amount and select payment method.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            try {
                double amount = Double.parseDouble(amountStr);
                try (Connection conn = Database.getConnection()) {
                    PreparedStatement ps = conn.prepareStatement(
                        "INSERT INTO Payments (booking_id, amount, payment_method, payment_status) VALUES (?, ?, ?, 'Success')");
                    ps.setInt(1, bookingId);
                    ps.setDouble(2, amount);
                    ps.setString(3, method);
                    ps.executeUpdate();
                    PreparedStatement ps2 = conn.prepareStatement(
                        "UPDATE Bookings SET booking_status='Confirmed' WHERE booking_id=?");
                    ps2.setInt(1, bookingId);
                    ps2.executeUpdate();
                    JOptionPane.showMessageDialog(this, "Payment successful!");
                    if (switcher != null) switcher.showStatus(bookingId);
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Invalid amount.");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "DB Error: " + ex.getMessage());
            }
        });
    }
}
