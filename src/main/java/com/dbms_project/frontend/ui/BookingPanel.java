package com.dbms_project.frontend.ui;

import com.dbms_project.frontend.db.Database;
import java.awt.*;
import java.sql.*;
import javax.swing.*;

public class BookingPanel extends JPanel {
    private final PanelSwitcher switcher;
    private final int userId;
    private final int flightId;
    public BookingPanel(int userId, int flightId, PanelSwitcher switcher) {
        this.userId = userId;
        this.flightId = flightId;
        this.switcher = switcher;
        setLayout(new GridBagLayout());
        setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        JLabel lblTitle = new JLabel("Book Flight");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblTitle.setForeground(new Color(33, 150, 243));
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2; gbc.insets = new Insets(10,10,20,10);
        add(lblTitle, gbc);
        gbc.gridwidth = 1; gbc.insets = new Insets(5,10,5,10);
        gbc.gridx = 0; gbc.gridy = 1;
        add(new JLabel("Passenger Name:"), gbc);
        JTextField txtName = new JTextField(20);
        gbc.gridx = 1;
        add(txtName, gbc);
        gbc.gridx = 0; gbc.gridy = 2;
        add(new JLabel("Age:"), gbc);
        JTextField txtAge = new JTextField(5);
        gbc.gridx = 1;
        add(txtAge, gbc);
        gbc.gridx = 0; gbc.gridy = 3;
        add(new JLabel("Gender:"), gbc);
        JComboBox<String> genderBox = new JComboBox<>(new String[]{"M", "F", "O"});
        gbc.gridx = 1;
        add(genderBox, gbc);
        gbc.gridx = 0; gbc.gridy = 4;
        add(new JLabel("Seat Number:"), gbc);
        JTextField txtSeat = new JTextField(5);
        gbc.gridx = 1;
        add(txtSeat, gbc);
        gbc.gridx = 0; gbc.gridy = 5;
        add(new JLabel("Seat Class:"), gbc);
        JComboBox<String> seatClassBox = new JComboBox<>(new String[]{"Economy", "Business", "First"});
        gbc.gridx = 1;
        add(seatClassBox, gbc);
        JButton btnBook = new JButton("Book");
        btnBook.setBackground(new Color(33, 150, 243));
        btnBook.setForeground(Color.WHITE);
        btnBook.setFocusPainted(false);
        btnBook.setFont(new Font("Segoe UI", Font.BOLD, 16));
        gbc.gridx = 0; gbc.gridy = 6; gbc.gridwidth = 2; gbc.insets = new Insets(20,10,10,10);
        add(btnBook, gbc);
        btnBook.addActionListener(e -> {
            String passengerName = txtName.getText();
            String ageStr = txtAge.getText();
            String gender = (String) genderBox.getSelectedItem();
            String seatNumber = txtSeat.getText();
            String seatClass = (String) seatClassBox.getSelectedItem();
            if (passengerName.isEmpty() || ageStr.isEmpty() || gender.isEmpty() || seatNumber.isEmpty() || seatClass.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please fill all fields.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            try {
                int age = Integer.parseInt(ageStr);
                try (Connection conn = Database.getConnection()) {
                    PreparedStatement psBooking = conn.prepareStatement(
                        "INSERT INTO Bookings (user_id, flight_id, booking_status) VALUES (?, ?, 'Pending')", Statement.RETURN_GENERATED_KEYS);
                    psBooking.setInt(1, userId);
                    psBooking.setInt(2, flightId);
                    psBooking.executeUpdate();
                    ResultSet rs = psBooking.getGeneratedKeys();
                    int bookingId = -1;
                    if (rs.next()) {
                        bookingId = rs.getInt(1);
                    }
                    if (bookingId == -1) {
                        JOptionPane.showMessageDialog(this, "Failed to create booking.");
                        return;
                    }
                    PreparedStatement psPassenger = conn.prepareStatement(
                        "INSERT INTO Passengers (booking_id, full_name, age, gender, seat_number, seat_class) VALUES (?, ?, ?, ?, ?, ?)");
                    psPassenger.setInt(1, bookingId);
                    psPassenger.setString(2, passengerName);
                    psPassenger.setInt(3, age);
                    psPassenger.setString(4, gender);
                    psPassenger.setString(5, seatNumber);
                    psPassenger.setString(6, seatClass);
                    psPassenger.executeUpdate();
                    JOptionPane.showMessageDialog(this, "Booking successful! Proceed to payment.");
                    if (switcher != null) switcher.showPayment(bookingId);
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Invalid age.");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "DB Error: " + ex.getMessage());
            }
        });
    }
}
