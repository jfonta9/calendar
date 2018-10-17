/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package scheduling.view;

import java.net.URL;
import java.time.LocalDateTime;
import java.util.ResourceBundle;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import scheduling.model.Appointment;
import static scheduling.model.AppointmentCustomerList.getAppointmentCustomerList;
import static scheduling.model.ConsultantAppointmentList.getConsultantAppointmentList;
import scheduling.model.Customer;

/**
 * FXML Controller class
 *
 * @author root
 */
public class AppointmentCustomerReportController implements Initializable {
    @FXML
    private TableView<Customer> appointmentCustomerTable;
    @FXML
    private TableColumn<Customer, String> nameColumn;
    @FXML
    private TableColumn<Customer, Integer> appointmentCountColumn;
    ObservableList<Customer> appointmentCustomerList = getAppointmentCustomerList();
    
    
    
    
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        appointmentCustomerTable.setItems(appointmentCustomerList);
        // lambda to set customer table name column cells more efficiently 
        nameColumn.setCellValueFactory(cellData -> cellData.getValue().customerNameProperty());        
        appointmentCountColumn.setCellValueFactory(cellData -> cellData.getValue().countProperty().asObject()); 
    }    
    
}
