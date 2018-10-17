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
import static scheduling.model.ConsultantAppointmentList.getConsultantAppointmentList;
import static scheduling.model.CustomerList.getCustomerList;

/**
 * FXML Controller class
 *
 * @author root
 */
public class ConsultantScheduleReportController implements Initializable {

    @FXML
    private TableView<Appointment> consultantScheduleTable;
    @FXML
    private TableColumn<Appointment, String> descriptionColumn;
    @FXML
    private TableColumn<Appointment, String> locationColumn;
    @FXML
    private TableColumn<Appointment, LocalDateTime> startColumn;
    @FXML
    private TableColumn<Appointment, LocalDateTime> endColumn;
    ObservableList<Appointment> consultantScheduleAppointments = getConsultantAppointmentList();
    
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {

        
        consultantScheduleTable.setItems(consultantScheduleAppointments);
        // lambda to set customer table name column cells more efficiently 
        descriptionColumn.setCellValueFactory(cellData -> cellData.getValue().descriptionProperty());        
        locationColumn.setCellValueFactory(cellData -> cellData.getValue().locationProperty());       
        startColumn.setCellValueFactory(cellData -> cellData.getValue().startProperty());        
        endColumn.setCellValueFactory(cellData -> cellData.getValue().endProperty());     
    }
}
