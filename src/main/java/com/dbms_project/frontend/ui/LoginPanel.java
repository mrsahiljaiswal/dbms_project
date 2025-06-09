package com.dbms_project.frontend.ui;

import com.dbms_project.frontend.db.Database;
import java.awt.*;
import java.sql.*;
import javax.swing.*;

public class LoginPanel extends JPanel {
    private final PanelSwitcher switcher;
    public LoginPanel(PanelSwitcher switcher) {
        this.switcher = switcher;
        setLayout(new GridBagLayout());
        setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        JLabel lblTitle = new JLabel("Login");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblTitle.setForeground(new Color(33, 150, 243));
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2; gbc.insets = new Insets(10,10,20,10);
        add(lblTitle, gbc);
        gbc.gridwidth = 1; gbc.insets = new Insets(5,10,5,10);
        gbc.gridx = 0; gbc.gridy = 1;
        add(new JLabel("Email:"), gbc);
        JTextField txtEmail = new JTextField(20);
        gbc.gridx = 1;
        add(txtEmail, gbc);
        gbc.gridx = 0; gbc.gridy = 2;
        add(new JLabel("Password:"), gbc);
        JPasswordField txtPassword = new JPasswordField(20);
        gbc.gridx = 1;
        add(txtPassword, gbc);
        JButton btnLogin = new JButton("Login");
        btnLogin.setBackground(new Color(33, 150, 243));
        btnLogin.setForeground(Color.WHITE);
        btnLogin.setFocusPainted(false);
        btnLogin.setFont(new Font("Segoe UI", Font.BOLD, 16));
        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2; gbc.insets = new Insets(20,10,10,10);
        add(btnLogin, gbc);
        JButton btnRegister = new JButton("Register");
        btnRegister.setBackground(new Color(200, 200, 200));
        btnRegister.setForeground(Color.BLACK);
        btnRegister.setFocusPainted(false);
        btnRegister.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        gbc.gridy = 4; gbc.insets = new Insets(5,10,10,10);
        add(btnRegister, gbc);
        btnLogin.addActionListener(e -> {
            String email = txtEmail.getText();
            String password = new String(txtPassword.getPassword());
            if (email.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter email and password.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            try (Connection conn = Database.getConnection()) {
                PreparedStatement ps = conn.prepareStatement("SELECT * FROM Users WHERE email=? AND password=?");
                ps.setString(1, email);
                ps.setString(2, password);
                ResultSet rs = ps.executeQuery();
                if (rs.next()) {
                    int userId = rs.getInt("user_id");
                    String userName = rs.getString("name");
                    JOptionPane.showMessageDialog(this, "Login successful!");
                    if (switcher != null) switcher.showBooking(userId, -1); // Will be interpreted as showFlightSearchPanel
                } else {
                    JOptionPane.showMessageDialog(this, "Invalid credentials.");
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "DB Error: " + ex.getMessage());
            }
        });
        btnRegister.addActionListener(e -> {
            if (switcher != null) switcher.showLogin(); // Should be showRegister if you want register panel
        });
    }
}
