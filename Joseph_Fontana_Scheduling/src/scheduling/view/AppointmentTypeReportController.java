/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package scheduling.view;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import scheduling.model.Appointment;
import static scheduling.model.AppointmentCustomerList.getAppointmentCustomerList;
import static scheduling.model.AppointmentTypeList.getAppointmentTypeList;
import scheduling.model.Customer;

/**
 * FXML Controller class
 *
 * @author root
 */
public class AppointmentTypeReportController implements Initializable {

    
    @FXML
    private TableView<Appointment> appointmentTypeTable;
    @FXML
    private TableColumn<Appointment, String> descriptionColumn;
    @FXML
    private TableColumn<Appointment, Integer> appointmentCountColumn;
    ObservableList<Appointment> appointmentTypeList = getAppointmentTypeList();
    
    
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        appointmentTypeTable.setItems(appointmentTypeList);
        // lambda to set customer table name column cells more efficiently 
        descriptionColumn.setCellValueFactory(cellData -> cellData.getValue().descriptionProperty());        
        appointmentCountColumn.setCellValueFactory(cellData -> cellData.getValue().countProperty().asObject()); 
    }    
    
}
