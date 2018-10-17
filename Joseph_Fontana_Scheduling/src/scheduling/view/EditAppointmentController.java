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
public class EditAppointmentController implements Initializable {
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
    private Button saveButton;
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
    private Appointment app;
    private Customer customer;
    public User user;
    private static final String dburl = "jdbc:mysql://52.206.157.109/U04tA4";
    private static final String dbuser = "U04tA4";
    private static final String dbpass = "53688341124";
    ObservableList<String> hours = FXCollections.observableArrayList();
    ObservableList<String> minutes = FXCollections.observableArrayList();
    
    // method home controller uses to set the user
    public void setUser(User user){
        this.user = user;
    }

    /**
     * // Method home controller uses to set the appointment to be modified
     * @param app
     * @throws SQLException 
     */
    public void setAppointment (Appointment app) throws SQLException {
        this.app = app;
        customerTextField.setText(retrieveCustomerName(app.getCustomerID()));
        titleTextField.setText(app.getTitle());
        descriptionTextField.setText(app.getDescription());
        locationTextField.setText(app.getLocation());
        contactTextField.setText(app.getContact());
        startDatePicker.setValue(app.getStart().toLocalDate());
        endDatePicker.setValue(app.getEnd().toLocalDate());
        hours.addAll("09", "10", "11","12", "13", "14", "15", "16", "17");
        minutes.addAll("00","15","30","45");
        startHourCombo.setItems(hours);
        startHourCombo.setValue(String.valueOf(app.getStart().getHour()));
        startMinCombo.setItems(minutes);
        startMinCombo.setValue(String.valueOf(app.getStart().getMinute()));
        endHourCombo.setItems(hours);
        endHourCombo.setValue(String.valueOf(app.getEnd().getHour()));
        endMinCombo.setItems(minutes);
        endMinCombo.setValue(String.valueOf(app.getEnd().getMinute()));
    }
    
    /**
     * method to retrieve customer name by customer id
     * @param customerId
     * @return
     * @throws SQLException 
     */
    public String retrieveCustomerName(int customerId) throws SQLException {
        try(Connection conn = DriverManager.getConnection(dburl, dbuser, dbpass)){
            System.out.println("Open");
            PreparedStatement ps = conn.prepareStatement("select customerName from customer where customerId= " + customerId); 
            ResultSet rs = ps.executeQuery();
            rs.next();
            String name = rs.getString("customerName");
            conn.close();
            System.out.println("Close");
            return name;
        }
        catch(Exception e){
            e.printStackTrace();
            return "error";
        }
    }
    
    /**
     * returns to home window within same scene
     * @throws IOException 
     */
    @FXML
    void close() throws IOException {
        URL location = EditAppointmentController.class.getResource("Home.fxml");
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
     * calls method to validate user input, edits appointment,and closes the
     * edit appointment window
     * @param event
     * @throws SQLException
     * @throws IOException 
     */
    @FXML
    public void handleEditAppointmentButton(ActionEvent event) throws SQLException, IOException {
        if(isInputValid()){
            LocalDate startDate = startDatePicker.getValue();
            String startHour = startHourCombo.getValue();
            String startMin = startMinCombo.getValue();
            LocalDate endDate = endDatePicker.getValue();
            String endHour = endHourCombo.getValue();
            String endMin = endMinCombo.getValue();
            LocalDateTime ldtStart = LocalDateTime.of(startDate.getYear(),startDate.getMonth(),startDate.getDayOfMonth(),Integer.valueOf(startHour), Integer.valueOf(startMin));
            LocalDateTime ldtEnd = LocalDateTime.of(endDate.getYear(),endDate.getMonth(),endDate.getDayOfMonth(),Integer.valueOf(endHour), Integer.valueOf(endMin));      
            editAppointment(titleTextField.getText(), descriptionTextField.getText(),
                locationTextField.getText(), contactTextField.getText(), ldtStart, ldtEnd);
            close();
        }
    }
    
    /**
     * updates appointment in database and appointment object
     * @param title
     * @param description
     * @param location
     * @param contact
     * @param start
     * @param end
     * @throws SQLException 
     */
    public void editAppointment( String title, String description, String location,
            String contact, LocalDateTime start, LocalDateTime end) throws SQLException {
        try (Connection conn = DriverManager.getConnection(dburl, dbuser, dbpass)) {
            System.out.println("Open");
            ObservableList<Appointment> appointmentList = getAppointmentList();
            PreparedStatement ps = conn.prepareStatement("update appointment set title =?,description=?,location=?,"
                + "contact=?,start=?,end=?,lastUpdate=?,lastUpdateBy=? where appointmentId="+app.getAppointmentID());
            ps.setString(1, title);
            ps.setString(2, description);
            ps.setString(3, location);
            ps.setString(4, contact);
            ps.setString(5, String.valueOf(start));
            ps.setString(6, String.valueOf(end));
            ps.setString(7, String.valueOf(LocalDateTime.now()));
            ps.setString(8, user.getUsername());

            ps.execute();

            app.setTitle(title);
            app.setDescription(description);
            app.setLocation(location);
            app.setContact(contact);
            app.setStart(start);
            app.setEnd(end);

            conn.close();
            System.out.println("close");
        } catch (Exception e) {
            e.printStackTrace();
        }
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
        // TODO
    }    
    
}
