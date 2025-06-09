package com.dbms_project.frontend.ui;

import com.dbms_project.frontend.db.Database;
import javax.swing.*;
import java.awt.*;
import java.sql.*;
import java.util.*;

public class FlightSearchPanel extends JPanel {
    private final PanelSwitcher switcher;
    private final int userId;
    private final String userName;

    public FlightSearchPanel(int userId, String userName, PanelSwitcher switcher) {
        this.userId = userId;
        this.userName = userName;
        this.switcher = switcher;
        setLayout(new GridBagLayout());
        setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        JLabel lblTitle = new JLabel("Search Flights");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblTitle.setForeground(new Color(33, 150, 243));
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2; gbc.insets = new Insets(10,10,20,10);
        add(lblTitle, gbc);
        gbc.gridwidth = 1; gbc.insets = new Insets(5,10,5,10);
        gbc.gridx = 0; gbc.gridy = 1;
        add(new JLabel("Source City:"), gbc);
        JComboBox<String> sourceBox = new JComboBox<>();
        gbc.gridx = 1;
        add(sourceBox, gbc);
        gbc.gridx = 0; gbc.gridy = 2;
        add(new JLabel("Destination City:"), gbc);
        JComboBox<String> destBox = new JComboBox<>();
        gbc.gridx = 1;
        add(destBox, gbc);
        gbc.gridx = 0; gbc.gridy = 3;
        add(new JLabel("Date (YYYY-MM-DD):"), gbc);
        JTextField dateField = new JTextField(10);
        gbc.gridx = 1;
        add(dateField, gbc);
        JButton btnSearch = new JButton("Search");
        btnSearch.setBackground(new Color(33, 150, 243));
        btnSearch.setForeground(Color.WHITE);
        btnSearch.setFocusPainted(false);
        btnSearch.setFont(new Font("Segoe UI", Font.BOLD, 16));
        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 2; gbc.insets = new Insets(20,10,10,10);
        add(btnSearch, gbc);
        JTextArea resultArea = new JTextArea(10, 40);
        resultArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(resultArea);
        gbc.gridx = 0; gbc.gridy = 5; gbc.gridwidth = 2;
        add(scrollPane, gbc);
        JButton btnBook = new JButton("Book Selected Flight");
        btnBook.setBackground(new Color(76, 175, 80));
        btnBook.setForeground(Color.WHITE);
        btnBook.setFocusPainted(false);
        btnBook.setFont(new Font("Segoe UI", Font.BOLD, 16));
        gbc.gridy = 6; gbc.insets = new Insets(10,10,10,10);
        add(btnBook, gbc);
        // Populate source/destination from Airports table (unique cities)
        try (Connection conn = Database.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery("SELECT DISTINCT city FROM Airports")) {
            HashSet<String> cities = new HashSet<>();
            while (rs.next()) {
                String city = rs.getString("city");
                if (cities.add(city)) {
                    sourceBox.addItem(city);
                    destBox.addItem(city);
                }
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, "DB Error: " + ex.getMessage());
        }
        final java.util.List<Integer> foundFlightIds = new java.util.ArrayList<>();
        final java.util.List<String> foundFlightSummaries = new java.util.ArrayList<>();
        btnSearch.addActionListener(e -> {
            String source = (String) sourceBox.getSelectedItem();
            String dest = (String) destBox.getSelectedItem();
            String date = dateField.getText();
            resultArea.setText("");
            foundFlightIds.clear();
            foundFlightSummaries.clear();
            if (source == null || dest == null || source.equals(dest)) {
                resultArea.setText("Please select different source and destination cities.");
                return;
            }
            try (Connection conn = Database.getConnection()) {
                String sql = "SELECT f.flight_id, f.flight_number, f.departure_time, f.status, f.total_seats, a1.city as source_city, a2.city as dest_city, f.airline_name, f.arrival_time " +
                        "FROM Flights f " +
                        "JOIN Airports a1 ON f.origin_airport_id = a1.airport_id " +
                        "JOIN Airports a2 ON f.destination_airport_id = a2.airport_id " +
                        "WHERE a1.city=? AND a2.city=? AND DATE(f.departure_time)=?";
                PreparedStatement ps = conn.prepareStatement(sql);
                ps.setString(1, source);
                ps.setString(2, dest);
                ps.setString(3, date);
                ResultSet rs = ps.executeQuery();
                boolean found = false;
                while (rs.next()) {
                    found = true;
                    int flightId = rs.getInt("flight_id");
                    foundFlightIds.add(flightId);
                    String summary = "Flight ID: " + flightId +
                        ", Number: " + rs.getString("flight_number") +
                        ", Airline: " + rs.getString("airline_name") +
                        ", Departure: " + rs.getString("departure_time") +
                        ", Arrival: " + rs.getString("arrival_time") +
                        ", Status: " + rs.getString("status") +
                        ", Seats: " + rs.getInt("total_seats") +
                        ", From: " + rs.getString("source_city") +
                        ", To: " + rs.getString("dest_city");
                    foundFlightSummaries.add(summary);
                    resultArea.append(summary + "\n");
                }
                if (!found) {
                    resultArea.setText("No flights found for the selected route and date.");
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(null, "DB Error: " + ex.getMessage());
            }
        });
        btnBook.addActionListener(e -> {
            int selectedLine = 0;
            try {
                selectedLine = resultArea.getCaretPosition() == 0 ? 0 : resultArea.getLineOfOffset(resultArea.getCaretPosition());
            } catch (javax.swing.text.BadLocationException ex) {
                JOptionPane.showMessageDialog(this, "Error selecting flight: " + ex.getMessage());
                return;
            }
            if (foundFlightIds.isEmpty() || selectedLine >= foundFlightIds.size()) {
                JOptionPane.showMessageDialog(this, "Please select a flight line in the results to book.");
                return;
            }
            int flightId = foundFlightIds.get(selectedLine);
            if (switcher != null) switcher.showBooking(userId, flightId);
        });
    }
}
