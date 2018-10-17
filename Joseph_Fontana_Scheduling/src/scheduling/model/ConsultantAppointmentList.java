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
public class ConsultantAppointmentList {
    private static ObservableList<Appointment> consultantAppointmentList = FXCollections.observableArrayList();

    // Getter for customerRoster
    public static ObservableList<Appointment> getConsultantAppointmentList() {
        return consultantAppointmentList;
    }
}
