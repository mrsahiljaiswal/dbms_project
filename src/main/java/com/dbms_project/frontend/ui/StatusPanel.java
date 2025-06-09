package com.dbms_project.frontend.ui;

import com.dbms_project.frontend.db.Database;
import java.awt.*;
import java.sql.*;
import javax.swing.*;

public class StatusPanel extends JPanel {
    private final PanelSwitcher switcher;
    private final int bookingId;
    public StatusPanel(int bookingId, PanelSwitcher switcher) {
        this.bookingId = bookingId;
        this.switcher = switcher;
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);
        JTextArea detailsArea = new JTextArea(12, 40);
        detailsArea.setEditable(false);
        detailsArea.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        JScrollPane scrollPane = new JScrollPane(detailsArea);
        add(scrollPane, BorderLayout.CENTER);
        JButton btnBack = new JButton("Back to Search");
        btnBack.setBackground(new Color(33, 150, 243));
        btnBack.setForeground(Color.WHITE);
        btnBack.setFocusPainted(false);
        btnBack.setFont(new Font("Segoe UI", Font.BOLD, 16));
        add(btnBack, BorderLayout.SOUTH);
        // Query DB for booking, flight, passenger details
        try (Connection conn = Database.getConnection()) {
            PreparedStatement psBooking = conn.prepareStatement(
                "SELECT b.booking_id, b.booking_status, b.booking_date, f.flight_number, f.airline_name, f.departure_time, f.arrival_time, a1.city as origin_city, a2.city as dest_city " +
                "FROM Bookings b " +
                "JOIN Flights f ON b.flight_id = f.flight_id " +
                "JOIN Airports a1 ON f.origin_airport_id = a1.airport_id " +
                "JOIN Airports a2 ON f.destination_airport_id = a2.airport_id " +
                "WHERE b.booking_id = ?");
            psBooking.setInt(1, bookingId);
            ResultSet rsBooking = psBooking.executeQuery();
            if (rsBooking.next()) {
                detailsArea.append("Booking ID: " + rsBooking.getInt("booking_id") + "\n");
                detailsArea.append("Status: " + rsBooking.getString("booking_status") + "\n");
                detailsArea.append("Booking Date: " + rsBooking.getString("booking_date") + "\n");
                detailsArea.append("Flight Number: " + rsBooking.getString("flight_number") + "\n");
                detailsArea.append("Airline: " + rsBooking.getString("airline_name") + "\n");
                detailsArea.append("From: " + rsBooking.getString("origin_city") + "\n");
                detailsArea.append("To: " + rsBooking.getString("dest_city") + "\n");
                detailsArea.append("Departure: " + rsBooking.getString("departure_time") + "\n");
                detailsArea.append("Arrival: " + rsBooking.getString("arrival_time") + "\n");
            }
            detailsArea.append("\nPassenger(s):\n");
            PreparedStatement psPass = conn.prepareStatement(
                "SELECT full_name, age, gender, seat_number, seat_class FROM Passengers WHERE booking_id = ?");
            psPass.setInt(1, bookingId);
            ResultSet rsPass = psPass.executeQuery();
            int pNum = 1;
            while (rsPass.next()) {
                detailsArea.append("  " + pNum + ". Name: " + rsPass.getString("full_name") + ", Age: " + rsPass.getInt("age") + ", Gender: " + rsPass.getString("gender") + ", Seat: " + rsPass.getString("seat_number") + ", Class: " + rsPass.getString("seat_class") + "\n");
                pNum++;
            }
        } catch (Exception ex) {
            detailsArea.setText("Error loading booking details: " + ex.getMessage());
        }
        btnBack.addActionListener(e -> {
            if (switcher != null) switcher.showLogin();
        });
    }
}
