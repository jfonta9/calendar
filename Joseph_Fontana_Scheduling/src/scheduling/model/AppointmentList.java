/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package scheduling.model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 *
 * @author root
 */
public class AppointmentList {
    private static ObservableList<Appointment> appointmentList = FXCollections.observableArrayList();

    // Getter for customerRoster
    public static ObservableList<Appointment> getAppointmentList() {
        return appointmentList;
    }
}
