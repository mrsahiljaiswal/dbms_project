package com.dbms_project.frontend.ui;

import com.dbms_project.frontend.db.Database;
import java.awt.*;
import java.sql.*;
import javax.swing.*;

public class RegisterPanel extends JPanel {
    private final PanelSwitcher switcher;
    public RegisterPanel(PanelSwitcher switcher) {
        this.switcher = switcher;
        setLayout(new GridBagLayout());
        setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        JLabel lblTitle = new JLabel("Register");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblTitle.setForeground(new Color(33, 150, 243));
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2; gbc.insets = new Insets(10,10,20,10);
        add(lblTitle, gbc);
        gbc.gridwidth = 1; gbc.insets = new Insets(5,10,5,10);
        gbc.gridx = 0; gbc.gridy = 1;
        add(new JLabel("Name:"), gbc);
        JTextField txtName = new JTextField(20);
        gbc.gridx = 1;
        add(txtName, gbc);
        gbc.gridx = 0; gbc.gridy = 2;
        add(new JLabel("Email:"), gbc);
        JTextField txtEmail = new JTextField(20);
        gbc.gridx = 1;
        add(txtEmail, gbc);
        gbc.gridx = 0; gbc.gridy = 3;
        add(new JLabel("Password:"), gbc);
        JPasswordField txtPassword = new JPasswordField(20);
        gbc.gridx = 1;
        add(txtPassword, gbc);
        JButton btnRegister = new JButton("Register");
        btnRegister.setBackground(new Color(33, 150, 243));
        btnRegister.setForeground(Color.WHITE);
        btnRegister.setFocusPainted(false);
        btnRegister.setFont(new Font("Segoe UI", Font.BOLD, 16));
        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 2; gbc.insets = new Insets(20,10,10,10);
        add(btnRegister, gbc);
        JButton btnBack = new JButton("Back to Login");
        btnBack.setBackground(new Color(200, 200, 200));
        btnBack.setForeground(Color.BLACK);
        btnBack.setFocusPainted(false);
        btnBack.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        gbc.gridy = 5; gbc.insets = new Insets(5,10,10,10);
        add(btnBack, gbc);
        btnRegister.addActionListener(e -> {
            String name = txtName.getText();
            String email = txtEmail.getText();
            String password = new String(txtPassword.getPassword());
            if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please fill all fields.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            try (Connection conn = Database.getConnection()) {
                PreparedStatement ps = conn.prepareStatement("INSERT INTO Users (name, email, password) VALUES (?, ?, ?)");
                ps.setString(1, name);
                ps.setString(2, email);
                ps.setString(3, password);
                ps.executeUpdate();
                JOptionPane.showMessageDialog(this, "Registration successful!");
                if (switcher != null) switcher.showLogin();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "DB Error: " + ex.getMessage());
            }
        });
        btnBack.addActionListener(e -> {
            if (switcher != null) switcher.showLogin();
        });
    }
}
