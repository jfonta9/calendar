package scheduling.view;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.fxml.JavaFXBuilderFactory;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import scheduling.model.Appointment;
import static scheduling.model.AppointmentList.getAppointmentList;
import scheduling.model.Customer;
import scheduling.model.User;

/**
 * FXML Controller class
 *
 * @author root
 */
public class AddAppointmentController implements Initializable {
    @FXML
    private TextField customerTextField;
    @FXML
    private TextField titleTextField;
    @FXML
    private TextField descriptionTextField;
    @FXML
    private TextField locationTextField;
    @FXML
    private TextField contactTextField;
    @FXML
    private Button cancelButton;
    @FXML
    private DatePicker startDatePicker;
    @FXML
    private DatePicker endDatePicker;
    @FXML
    private ComboBox<String> startHourCombo;
    @FXML
    private ComboBox<String> startMinCombo;
    @FXML
    private ComboBox<String> endHourCombo;
    @FXML
    private ComboBox<String> endMinCombo;
    private Customer customer;
    private static final String dburl = "jdbc:mysql://52.206.157.109/U04tA4";
    private static final String dbuser = "U04tA4";
    private static final String dbpass = "53688341124";
    private static User user;
    ObservableList<Appointment> appointmentList = getAppointmentList();
    ObservableList<String> hours = FXCollections.observableArrayList();
    ObservableList<String> minutes = FXCollections.observableArrayList();

    /**
     * adds appointment to db, and creates appointment object
     * @param title
     * @param description
     * @param location
     * @param contact
     * @param start
     * @param end
     * @throws SQLException 
     */
    public void addAppointment( String title, String description, String location,
            String contact, LocalDateTime start, LocalDateTime end) throws SQLException {
        try (Connection conn = DriverManager.getConnection(dburl, dbuser, dbpass)) {
            System.out.println("Open");
            PreparedStatement ps = conn.prepareStatement("SELECT MAX(appointmentId) FROM appointment");
            ResultSet rs = ps.executeQuery();
            rs.next();
            int nextAppointmentId = rs.getInt(1) + 1;
            ps = conn.prepareStatement("insert into appointment(appointmentId,"
                    + "userId,customerId,title,description,location,contact,url,start,"
                    + "end,createDate,createdBy,lastUpdate,lastUpdateBy)values(?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
            ps.setInt(1, nextAppointmentId);
            ps.setInt(2, user.getUserID()); 
            ps.setInt(3, customer.getCustomerID());
            ps.setString(4, title);
            ps.setString(5, description);
            ps.setString(6, location);
            ps.setString(7, contact);
            ps.setString(8, "none"); 
            ps.setString(9, String.valueOf(start));
            ps.setString(10, String.valueOf(end));
            ps.setString(11, String.valueOf(LocalDateTime.now()));
            ps.setString(12, user.getUsername()); 
            ps.setString(13, String.valueOf(LocalDateTime.now()));
            ps.setString(14, user.getUsername()); 
            ps.execute();
            Appointment appointment = new Appointment();
            appointment.setAppointmentID(nextAppointmentId);
            appointment.setCustomerID(customer.getCustomerID());
            appointment.setUserID(user.getUserID());
            appointment.setTitle(title);
            appointment.setDescription(description);
            appointment.setLocation(location);
            appointment.setContact(contact);
            appointment.setUrl("none"); 
            appointment.setStart(start);
            appointment.setEnd(end);
            appointment.setCreateDate(String.valueOf(LocalDateTime.now()));
            appointment.setCreatedBy(user.getUsername()); 
            appointment.setLastUpdate(String.valueOf(LocalDateTime.now()));
            appointment.setLastUpdateBy(user.getUsername());
            appointmentList.add(appointment);
            conn.close();
            System.out.println("close");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    /**
     * boolean to see if an appointment trying to be made overlaps an appointment
     * already scheduled
     * @return 
     */
    @FXML
    private boolean checkAppointmentOverlap(){
        LocalDate startDate = startDatePicker.getValue();
        String startHour = startHourCombo.getValue();
        String startMin = startMinCombo.getValue();
        LocalDate endDate = endDatePicker.getValue();
        String endHour = endHourCombo.getValue();
        String endMin = endMinCombo.getValue();
        LocalDateTime ldtStart = LocalDateTime.of(startDate.getYear(),startDate.getMonth(),startDate.getDayOfMonth(),Integer.valueOf(startHour), Integer.valueOf(startMin));
        LocalDateTime ldtEnd = LocalDateTime.of(endDate.getYear(),endDate.getMonth(),endDate.getDayOfMonth(),Integer.valueOf(endHour), Integer.valueOf(endMin));  
        int overlap = 0;
        for(Appointment app:appointmentList){
            if(ldtStart.isAfter(app.getStart())&&ldtStart.isBefore(app.getEnd()) || ldtEnd.isAfter(app.getStart())&&ldtEnd.isBefore(app.getEnd())){
                overlap++;
            }
        }
        if(overlap>0){
            return true;
        }
        else{
            return false;
        }
    }

    /**
     * method to return to the home page within the same scene
     * @throws IOException 
     */
    @FXML
    void close() throws IOException {
        URL location = AddAppointmentController.class.getResource("Home.fxml");
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(location);
        fxmlLoader.setBuilderFactory(new JavaFXBuilderFactory());
        Parent root = (Parent) (Node) fxmlLoader.load(location.openStream());
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
        HomeController controller = (HomeController) fxmlLoader.getController();
        controller.setUser(user);
    }
    
    /**
     * 
     * @param event
     * @throws SQLException
     * @throws IOException 
     */
    @FXML
    public void handleAddAppointmentButton(ActionEvent event) throws SQLException, IOException {
        if(isInputValid()){
            LocalTime open = LocalTime.of(9, 0);
            LocalTime close = LocalTime.of(17, 0);
            LocalDate startDate = startDatePicker.getValue();
            String startHour = startHourCombo.getValue();
            String startMin = startMinCombo.getValue();
            LocalDate endDate = endDatePicker.getValue();
            String endHour = endHourCombo.getValue();
            String endMin = endMinCombo.getValue();
            LocalDateTime ldtStart = LocalDateTime.of(startDate.getYear(),startDate.getMonth(),startDate.getDayOfMonth(),Integer.valueOf(startHour), Integer.valueOf(startMin));
            LocalDateTime ldtEnd = LocalDateTime.of(endDate.getYear(),endDate.getMonth(),endDate.getDayOfMonth(),Integer.valueOf(endHour), Integer.valueOf(endMin));
            if(LocalTime.now().isBefore(open) || LocalTime.now().isAfter(close)){
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Information Dialog");
                alert.setHeaderText("Notice!");
                alert.setContentText("You cannot schedule an appointment \n outside of business hours!");
                alert.showAndWait();
            }
            if(checkAppointmentOverlap()){
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Information Dialog");
                alert.setHeaderText("Notice!");
                alert.setContentText("You cannot overlap appointments!");
                alert.showAndWait();
            }
            else{
                addAppointment(titleTextField.getText(), descriptionTextField.getText(),
                locationTextField.getText(), contactTextField.getText(), ldtStart, ldtEnd);
                close();
            }
        }
    }
    
    /**
     * Sets the customer from the home table view to the main screen.
     *
     * @param customer
     */
    public void setCustomer(Customer customer) {
        this.customer = customer;
        customerTextField.setText(customer.getCustomerName());
        
    }
    
    public void setUser(User user){
        this.user = user;
    }

    // Validates user input in text fields.
    private boolean isInputValid() {
        String errorMessage = "";
        if (titleTextField.getText() == null || titleTextField.getText().length() == 0) {
            errorMessage += "No valid title!\n"; 
        }
        if (descriptionTextField.getText() == null || descriptionTextField.getText().length() == 0){
            errorMessage += "No valid description!\n";
        }
        if (locationTextField.getText() == null || locationTextField.getText().length() == 0){
            errorMessage += "No valid location!\n";
        }
        if (contactTextField.getText() == null || contactTextField.getText().length() == 0) {
            errorMessage += "No valid contact!\n"; 
        }
        if (startDatePicker.getValue() == null) {
            errorMessage += "No valid start date!\n"; 
        }
        if (startHourCombo.getSelectionModel().getSelectedItem()== null || startMinCombo.getSelectionModel().getSelectedItem() == null) {
            errorMessage += "No valid start time!\n"; 
        }
        if (endDatePicker.getValue() == null) {
            errorMessage += "No valid end date!\n"; 
        }
        if (endHourCombo.getSelectionModel().getSelectedItem()== null || endMinCombo.getSelectionModel().getSelectedItem() == null) {
            errorMessage += "No valid end time!\n"; 
        }
        if (errorMessage.length() == 0) {
            return true;
        } else {
            // Show the error message.
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Information Dialog");
            alert.setHeaderText("Error!");
            alert.setContentText(errorMessage);
            alert.showAndWait();
            return false;
        }
    }
    
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        hours.addAll("09", "10", "11","12", "13", "14", "15", "16", "17");
        minutes.addAll("00","15","30","45");
        startHourCombo.setItems(hours);
        startHourCombo.setValue("00");
        startMinCombo.setItems(minutes);
        startMinCombo.setValue("00");
        endHourCombo.setItems(hours);
        endHourCombo.setValue("00");
        endMinCombo.setItems(minutes);
        endMinCombo.setValue("00");
    }
}
