package com.dbms_project.frontend.ui;

public interface PanelSwitcher {
    void showBooking(int userId, int flightId);
    void showPayment(int bookingId);
    void showStatus(int bookingId);
    void showLogin();
}
