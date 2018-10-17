package scheduling.view;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import scheduling.model.Appointment;
import static scheduling.model.AppointmentList.getAppointmentList;
import scheduling.model.User;

public class LoginController {
    private static final String dburl = "jdbc:mysql://52.206.157.109/U04tA4";
    private static final String dbuser = "U04tA4";
    private static final String dbpass = "53688341124";
    private static ZoneId zoneId;
    Alert alert = new Alert(Alert.AlertType.ERROR);
    @FXML
    private TextField unTextField;
    @FXML
    private TextField pwTextField;
    @FXML
    private Button loginButton;
    @FXML
    private Label unLbl;
    @FXML
    private Label pwLbl;
    static ObservableList<Appointment> appointmentList = getAppointmentList();

    
    // gets users' locale and applies appropriate language(english or arabic)
    @FXML
    private void setLocale(){
        ResourceBundle rb = ResourceBundle.getBundle("scheduling.view.Login", Locale.getDefault());
        loginButton.setText(rb.getString("loginButton"));
        unLbl.setText(rb.getString("unLbl"));
        pwLbl.setText(rb.getString("pwLbl"));   
    }
    
    
    /**
     * Initializes the controller class.
     */
    @FXML
    public void initialize() {
        setLocale();
        // Action event lambda to open the home page when user clicks login
        loginButton.setOnAction(e -> {
            try {
                handleLoginButton();
            } catch (SQLException ex) {
                Logger.getLogger(LoginController.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(LoginController.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
    }
    
    /**
     * Action when user clicks login, checks for empty
     * fields, calls validateLogin method to check credentials
     * @param event 
     */
    @FXML
    void handleLoginButton() throws SQLException, IOException{
        String un = unTextField.getText();
        String pw = pwTextField.getText();
        String userHomeFolder = System.getProperty("user.home");
        File textFile = new File(userHomeFolder, "login_log.txt");
        textFile.setReadable(true);
        textFile.setWritable(true);
        ResourceBundle rb = ResourceBundle.getBundle("scheduling.view.Login", Locale.getDefault());
        boolean correctLogin = validateUsernamePassword(un,pw);
        if(correctLogin){
            createAppointmentList();
            try(FileWriter fw = new FileWriter(textFile, true);
                BufferedWriter bw = new BufferedWriter(fw);
                PrintWriter out = new PrintWriter(bw)){
                    out.println(un + "\t" + LocalDateTime.now());
            } catch (IOException e) {
                    e.printStackTrace();
            }
        FXMLLoader loader = new FXMLLoader(getClass().getResource("Home.fxml"));
        Parent root = (Parent) loader.load();
        Stage stage = (Stage) unTextField.getScene().getWindow();     
        stage.setTitle("Home");
        stage.setScene(new Scene(root));
        stage.show();
        HomeController controller = loader.getController();
        controller.setUser(user);
                
        }else{
            alert.setTitle(rb.getString("errorLbl"));
            alert.setHeaderText(rb.getString("errorLbl"));
            alert.setContentText(rb.getString("wrongUnPwLbl"));
            alert.showAndWait();
        }
    }

    /**
     * validates username/password against database
     * @param un
     * @param pw
     * @return 
     */
    static User user = new User();
    public boolean validateUsernamePassword(String un, String pw) throws SQLException{
        try(Connection conn = DriverManager.getConnection(dburl, dbuser, dbpass)){
            System.out.println("Open");
            PreparedStatement ps = conn.prepareStatement("SELECT * FROM user WHERE userName = ? AND password = ?");
            ps.setString(1, un);
            ps.setString(2, pw);
            ResultSet rs = ps.executeQuery();
            if(rs.next()){
                user.setUsername(un);
                user.setUserID(rs.getInt("userId"));
                conn.close();
                System.out.println("Close");
                return true;
            }
            else{
                conn.close();
                System.out.println("Close");
                return false;
            }
        }
    }
    

    /**
     *  Selects appointment table from database and creates appointment list.
     */
    private void createAppointmentList(){
        try(Connection conn = DriverManager.getConnection(dburl, dbuser, dbpass)){
            System.out.println("Open");
            appointmentList.clear();
            int alertCount = 0;
            PreparedStatement ps = conn.prepareStatement("select * from appointment where userId =" + user.getUserID()); 
            ResultSet rs = ps.executeQuery();
            while(rs.next()){
                Appointment appointment = new Appointment();
                int appointmentId = rs.getInt("appointmentId");
                int customerId = rs.getInt("customerId");
                int userId = rs.getInt("userId");
                String title = rs.getString("title");
                String description = rs.getString("description");
                String location = rs.getString("location");
                String contact = rs.getString("contact");
                String url = rs.getString("url");
                Timestamp start = rs.getTimestamp("start");
                Timestamp end = rs.getTimestamp("end");
                String createDate = rs.getString("createDate");
                String createdBy = rs.getString("createdBy");
                String lastUpdate = rs.getString("lastUpdate");
                String lastUpdateBy = rs.getString("lastUpdateBy");
                appointment.setAppointmentID(appointmentId);
                appointment.setCustomerID(customerId);
                appointment.setUserID(userId);
                appointment.setTitle(title);
                appointment.setDescription(description);
                appointment.setLocation(location);
                appointment.setContact(contact);
                appointment.setUrl(url);
                
                LocalDateTime ldtStart = start.toLocalDateTime();
                ZoneId zStart = ZoneId.systemDefault();
                ZonedDateTime zdtStart = ldtStart.atZone(zStart);
                Instant instantStart = zdtStart.toInstant();
                LocalDateTime ldtStart2 = LocalDateTime.ofInstant(instantStart, ZoneOffset.UTC);
                appointment.setStart(ldtStart2);
                LocalDateTime ldtEnd = end.toLocalDateTime();
                ZoneId zEnd = ZoneId.systemDefault();
                ZonedDateTime zdtEnd = ldtEnd.atZone(zEnd);
                Instant instantEnd = zdtEnd.toInstant();
                LocalDateTime ldtEnd2 = LocalDateTime.ofInstant(instantEnd, ZoneOffset.UTC);
                appointment.setEnd(ldtEnd2);
                appointment.setCreateDate(createDate);
                appointment.setCreatedBy(createdBy);
                appointment.setLastUpdate(lastUpdate);
                appointment.setLastUpdateBy(lastUpdateBy);
                appointmentList.add(appointment);
            }
            conn.close();
            System.out.println("close");
        } catch (SQLException e){
            e.printStackTrace();
        }
    }
}
