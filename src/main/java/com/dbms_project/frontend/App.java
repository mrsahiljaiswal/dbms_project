package com.dbms_project.frontend;

import com.dbms_project.frontend.ui.*;
import com.formdev.flatlaf.FlatLightLaf;
import javax.swing.*;

public class App {
    private JFrame frame;

    public static void main(String[] args) {
        FlatLightLaf.setup();
        SwingUtilities.invokeLater(() -> new App().start());
    }

    private void start() {
        frame = new JFrame("Flight Booking System");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600);
        frame.setLocationRelativeTo(null);
        showLoginPanel();
        frame.setVisible(true);
    }

    private void setContentPanel(JPanel panel) {
        frame.getContentPane().removeAll();
        frame.getContentPane().add(panel);
        frame.revalidate();
        frame.repaint();
    }

    private void showLoginPanel() {
        setContentPanel(new LoginPanel(new PanelSwitcher() {
            @Override
            public void showBooking(int userId, int flightId) {
                if (flightId == -1) {
                    // After login, show flight search
                    // Need userName, so pass null and handle in LoginPanel
                    // Instead, refactor to pass userName as a field
                    // We'll use a workaround: store userName in a static field
                    showFlightSearchPanel(userId, getUserNameById(userId));
                } else {
                    showBookingPanel(userId, flightId);
                }
            }
            @Override
            public void showPayment(int bookingId) {
                showPaymentPanel(bookingId);
            }
            @Override
            public void showStatus(int bookingId) {
                showStatusPanel(bookingId);
            }
            @Override
            public void showLogin() {
                showRegisterPanel();
            }
        }));
    }

    private void showRegisterPanel() {
        setContentPanel(new RegisterPanel(new PanelSwitcher() {
            @Override
            public void showBooking(int userId, int flightId) {}
            @Override
            public void showPayment(int bookingId) {}
            @Override
            public void showStatus(int bookingId) {}
            @Override
            public void showLogin() {
                showLoginPanel();
            }
        }));
    }

    private void showFlightSearchPanel(int userId, String userName) {
        setContentPanel(new FlightSearchPanel(userId, userName, new PanelSwitcher() {
            @Override
            public void showBooking(int userId, int flightId) {
                showBookingPanel(userId, flightId);
            }
            @Override
            public void showPayment(int bookingId) {
                showPaymentPanel(bookingId);
            }
            @Override
            public void showStatus(int bookingId) {
                showStatusPanel(bookingId);
            }
            @Override
            public void showLogin() {
                showLoginPanel();
            }
        }));
    }

    private void showBookingPanel(int userId, int flightId) {
        setContentPanel(new BookingPanel(userId, flightId, new PanelSwitcher() {
            @Override
            public void showBooking(int userId, int flightId) {}
            @Override
            public void showPayment(int bookingId) {
                showPaymentPanel(bookingId);
            }
            @Override
            public void showStatus(int bookingId) {
                showStatusPanel(bookingId);
            }
            @Override
            public void showLogin() {
                showLoginPanel();
            }
        }));
    }

    private void showPaymentPanel(int bookingId) {
        setContentPanel(new PaymentPanel(bookingId, new PanelSwitcher() {
            @Override
            public void showBooking(int userId, int flightId) {}
            @Override
            public void showPayment(int bookingId) {}
            @Override
            public void showStatus(int bookingId) {
                showStatusPanel(bookingId);
            }
            @Override
            public void showLogin() {
                showLoginPanel();
            }
        }));
    }

    private void showStatusPanel(int bookingId) {
        setContentPanel(new StatusPanel(bookingId, new PanelSwitcher() {
            @Override
            public void showBooking(int userId, int flightId) {}
            @Override
            public void showPayment(int bookingId) {}
            @Override
            public void showStatus(int bookingId) {}
            @Override
            public void showLogin() {
                showLoginPanel();
            }
        }));
    }

    private String getUserNameById(int userId) {
        try {
            java.sql.Connection conn = com.dbms_project.frontend.db.Database.getConnection();
            java.sql.PreparedStatement ps = conn.prepareStatement("SELECT name FROM Users WHERE user_id=?");
            ps.setInt(1, userId);
            java.sql.ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getString("name");
            }
        } catch (Exception ex) {
            // ignore or log
        }
        return "";
    }
}