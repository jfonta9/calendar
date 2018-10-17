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
public class CustomerList {
    private static ObservableList<Customer> customerList = FXCollections.observableArrayList();

    // Getter for customerRoster
    public static ObservableList<Customer> getCustomerList() {
        return customerList;
    }
}
