package scheduling.view;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.Year;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.fxml.JavaFXBuilderFactory;
import javafx.geometry.Insets;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.input.ContextMenuEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import scheduling.model.Appointment;
import static scheduling.model.AppointmentCustomerList.getAppointmentCustomerList;
import scheduling.model.AppointmentList;
import static scheduling.model.AppointmentList.getAppointmentList;
import static scheduling.model.AppointmentTypeList.getAppointmentTypeList;
import static scheduling.model.ConsultantAppointmentList.getConsultantAppointmentList;
import scheduling.model.Customer;
import static scheduling.model.CustomerList.getCustomerList;
import scheduling.model.User;
/**
 * FXML Controller class
 *
 * @author root
 */
public class HomeController implements Initializable {
    @FXML
    public GridPane calendarGrid;
    @FXML
    private TableView<Customer> customerTable;
    @FXML
    private TableColumn<Customer, String> nameColumn;
    @FXML
    private ComboBox<String> yearCombo;
    @FXML
    private ComboBox<String> monthCombo;
    @FXML
    private Button addCustomerButton;
    @FXML
    private Button forwardButton;
    @FXML
    private Button backwardButton;
    private static final String dburl = "jdbc:mysql://52.206.157.109/U04tA4";
    private static final String dbuser = "U04tA4";
    private static final String dbpass = "53688341124";
    private User user;
    private Customer customer;
    public ObservableList<Customer> appointmentCustomerList = getAppointmentCustomerList();
    public ObservableList<Appointment> consultantScheduleAppointments = getConsultantAppointmentList();
    public ObservableList<Appointment> appointmentTypeList = getAppointmentTypeList();
    ObservableList<Customer> customerList = getCustomerList();
    ObservableList<Appointment> appointmentList = getAppointmentList();
    public int lblCount = 1;
    
    // YearCombo
    int currentYear = Year.now().getValue();
    ObservableList<String> years = FXCollections.observableArrayList();
    // creates year list for year List View
    public ObservableList<String> yearList(){
        for(int x = Year.now().getValue(); x>=1980; x--){
            years.add(String.valueOf(x));
        }
        return(years);
    }
    
    // MonthCombo
    String currentMonth = getCurrentMonth();
        ObservableList<String> months = FXCollections.observableArrayList("January",
            "February","March","April","May","June","July","August","September",
            "October", "November","December");
    // returns the current month 
    public String getCurrentMonth(){
        String[] monthName = { "January", "February", "March", "April", "May", "June", "July",
        "August", "September", "October", "November", "December" };
        Calendar cal = Calendar.getInstance();
        String month = monthName[cal.get(Calendar.MONTH)];
        return month;
    }
    
    /**
     *  Selects customer name from customer database table and creates customer list.
     */
    void createCustomerList(){
        try(Connection conn = DriverManager.getConnection(dburl, dbuser, dbpass)){
            System.out.println("Open");
            ArrayList<Integer> customerIds = new ArrayList<>();
            //ObservableList<Customer> customerList = getCustomerList();
            customerList.clear();
            PreparedStatement ps = conn.prepareStatement("select customerId from customer");
            ResultSet rs = ps.executeQuery();
            while(rs.next()){
                customerIds.add(rs.getInt("customerId"));
            }
            for(int customerId : customerIds){
                Customer customer = new Customer();
                ps = conn.prepareStatement("SELECT customerName,addressId,active FROM customer WHERE customerId=" + customerId);
                rs = ps.executeQuery();
                rs.next();
                String customerName = rs.getString("customerName");
                int addressId = rs.getInt("addressId");
                int active = rs.getInt("active");
                customer.setCustomerID(customerId);
                customer.setCustomerName(customerName);
                customer.setCustomerAddressID(addressId);
                customer.setActive(active);
                
                ps = conn.prepareStatement("SELECT address,address2,cityId,postalCode,phone from address where addressId = " + addressId);
                rs = ps.executeQuery();
                rs.next();
                String address = rs.getString("address");
                String address2 = rs.getString("address2");
                int cityId = rs.getInt("cityId");
                String postalCode = rs.getString("postalCode");
                String phone = rs.getString("phone");
                customer.setAddress(address);
                customer.setAddress2(address2);
                customer.setCityId(cityId);
                customer.setPostalCode(postalCode);
                customer.setPhone(phone);
                
                ps = conn.prepareStatement("select city,countryId from city where cityId=" + cityId);
                rs = ps.executeQuery();
                rs.next();
                String city = rs.getString("city");
                int countryId = rs.getInt("countryId");
                customer.setCity(city);
                customer.setCountryId(countryId);
                
                ps = conn.prepareStatement("select country from country where countryId=" + countryId);
                rs = ps.executeQuery();
                rs.next();
                String country = rs.getString("country");
                customer.setCountry(country);
                
                customerList.add(customer);
            }
            conn.close();
            System.out.println("close");
        } catch (SQLException e){
            e.printStackTrace();
        }
    }
    
    /**
     *  Draws grid for the calendar view.
     */
    @FXML
    private void drawCalendarGrid(){
        // Go through each calendar grid location, or each "day" (7x5)
        int rows = 6;
        int cols = 7;
        for (int i = 0; i < rows; i++){
            for (int j = 0; j < cols; j++){
                // Add VBox and style it
                VBox vPane = new VBox();
                vPane.getStyleClass().add("calendar_pane");
                vPane.setMinWidth(50);
                vPane.setSpacing(5);
                vPane.setPadding(new Insets(1, 0, 0, 1));
                GridPane.setVgrow(vPane, Priority.ALWAYS);
                // Add it to the grid
                calendarGrid.add(vPane, j, i);  
            }
        }       
    }
    
    /**
     * draws weekly grid and calls loadSelectedWeek to load appointments
     */
    @FXML
    private void drawWeeklyGrid(){
        forwardButton.setVisible(true);
        backwardButton.setVisible(true);
        calendarGrid.getChildren().clear();
        // Go through each calendar grid location, or each "day" (7x5)
        
        int rows = 1;
        int cols = 7;
        for (int i = 0; i < rows; i++){
         for (int j = 0; j < cols; j++){   
                // Add VBox and style it
                
                VBox vPane = new VBox();
                vPane.getStyleClass().add("calendar_pane");
                vPane.setMinWidth(50);
                vPane.setMaxHeight(125);
                vPane.setSpacing(5);
                vPane.setPadding(new Insets(1, 0, 0, 1));
                GridPane.setVgrow(vPane, Priority.ALWAYS);
                GridPane.setValignment(vPane, VPos.TOP);
                calendarGrid.add(vPane, j, i);  
                
            }   
        }
        loadSelectedWeek();
        initializeAppointments();
    }
    
    // clears calendar grid, draws and loads monthly view with appointments
    @FXML
    private void handleMonthlyButton(){
        calendarGrid.getChildren().clear();
        drawCalendarGrid();
        loadSelectedMonth();
        initializeAppointments();
        lblCount = 1;
        forwardButton.setVisible(false);
        backwardButton.setVisible(false);
    }
    
    // passes appointment list to showDate to display appointments
    @FXML 
    public void initializeAppointments(){
        try{
            for(Appointment app : AppointmentList.getAppointmentList()){
                String year = String.valueOf(app.getStart().getYear());
                String month = String.valueOf(app.getStart().getMonth()).toLowerCase();
                String desc = app.getDescription();
                String yearSelection = yearCombo.getSelectionModel().getSelectedItem();
                String monthSelection = monthCombo.getSelectionModel().getSelectedItem().toLowerCase();
                if(year.equals(yearSelection) && month.equals(monthSelection)){
                    int day = app.getStart().getDayOfMonth();
                    showDate(day, app);
                }
            }
        }catch(NullPointerException e){
            e.printStackTrace();
        }
    }
    
    /**
     * loads the next week in weekly calendar view, 
     * unless it is the last week of the month
     */
    @FXML
    private void loadNextWeek(){
        backwardButton.setDisable(false);
        int year = Integer.parseInt(yearCombo.getSelectionModel().getSelectedItem());
        int month = monthCombo.getSelectionModel().getSelectedIndex();
        // Note: Java's Gregorian Calendar class gives us the right
        // "first day of the month" for a given calendar & month
        // This accounts for Leap Year
        GregorianCalendar gc = new GregorianCalendar(year, month, 1);
        //int firstDay = gc.get(Calendar.DAY_OF_WEEK);
        int daysInMonth = gc.getActualMaximum(Calendar.DAY_OF_MONTH);
        System.out.println(daysInMonth);
        // We are "offsetting" our start depending on what the
        // first day of the month is.
        // For example: Sunday start, Monday start, Wednesday start.. etc
        //int offset = firstDay;
        int gridCount = 1;
        
        // Go through calendar grid
        for(Node node : calendarGrid.getChildren()){
            VBox day = (VBox) node;
            day.getChildren().clear();
            // Make a new day label   
            day.setStyle("-fx-backgroud-color: white");
            day.setStyle("-fx-font: 14px \"System\" ");
            // Start placing labels on the first day for the month
            Label dayNum = new Label(String.valueOf(lblCount));
            dayNum.setPadding(new Insets(5));
            dayNum.setStyle("-fx-text-fill:darkslategray");
            day.getChildren().add(dayNum);
            lblCount++;    
            // Don't place a label if we've reached maximum label for the month
            if (lblCount > daysInMonth+1) {
                // Instead, darken day color
                day.getChildren().clear();
                day.setStyle("-fx-background-color: #E9F2F5"); 
                forwardButton.setDisable(true);
            }  
        }
        initializeAppointments();
    }
    
    /**
     * loads the previous week in weekly calendar view, 
     * unless it is the first week of the month already
     */
    @FXML
    private void loadPreviousWeek(){
        forwardButton.setDisable(false);
        int year = Integer.parseInt(yearCombo.getSelectionModel().getSelectedItem());
        int month = monthCombo.getSelectionModel().getSelectedIndex();
        // Note: Java's Gregorian Calendar class gives us the right
        // "first day of the month" for a given calendar & month
        // This accounts for Leap Year
        GregorianCalendar gc = new GregorianCalendar(year, month, 1);
        int firstDay = gc.get(Calendar.DAY_OF_WEEK);
        int daysInMonth = gc.getActualMaximum(Calendar.DAY_OF_MONTH);
        System.out.println(daysInMonth);
        // We are "offsetting" our start depending on what the
        // first day of the month is.
        // For example: Sunday start, Monday start, Wednesday start.. etc
        int offset = firstDay;
        int gridCount = 1;
        lblCount = lblCount - 14;
        // Go through calendar grid
        for(Node node : calendarGrid.getChildren()){
            VBox day = (VBox) node;
            day.getChildren().clear();
            // Make a new day label   
            day.setStyle("-fx-backgroud-color: white");
            day.setStyle("-fx-font: 14px \"System\" ");
            // Start placing labels on the first day for the month
            Label dayNum = new Label(String.valueOf(lblCount));
            dayNum.setPadding(new Insets(5));
            dayNum.setStyle("-fx-text-fill:darkslategray");
            day.getChildren().add(dayNum);
            lblCount++;    
            // Don't place a label if we've reached maximum label for the month
            if (lblCount <= 1) {
                // Instead, darken day color
                day.getChildren().clear();
                day.setStyle("-fx-background-color: #E9F2F5");
                backwardButton.setDisable(true);
            }  
        }
        initializeAppointments();
    }
    
    /**
     *  Makes sure the month starts on the correct day of week and
     *  adds number label for each day
     */
    @FXML
    private void loadSelectedMonth(){
        int year = Integer.parseInt(yearCombo.getSelectionModel().getSelectedItem());
        int month = monthCombo.getSelectionModel().getSelectedIndex();
        GregorianCalendar gc = new GregorianCalendar(year, month, 1);
        int firstDay = gc.get(Calendar.DAY_OF_WEEK);
        int daysInMonth = gc.getActualMaximum(Calendar.DAY_OF_MONTH);
        int offset = firstDay;
        int gridCount = 1;
        int lblCount = 1;
       // Go through calendar grid
       for(Node node : calendarGrid.getChildren()){
            VBox day = (VBox) node;
            day.getChildren().clear();
            day.setStyle("-fx-backgroud-color: white");
            day.setStyle("-fx-font: 14px \"System\" ");
            // Start placing labels on the first day for the month
            if (gridCount < offset) {
                gridCount++;
                // Darken color of the offset days
                day.setStyle("-fx-background-color: #E9F2F5"); 
            } else {
                // Don't place a label if we've reached maximum label for the month
                if (lblCount > daysInMonth) {
                // Instead, darken day color
                day.setStyle("-fx-background-color: #E9F2F5"); 
                } else {
                    // Make a new day label   
                    Label dayNumber = new Label(Integer.toString(lblCount));
                    dayNumber.setPadding(new Insets(5));
                    dayNumber.setStyle("-fx-text-fill:darkslategray");
                    day.getChildren().add(dayNumber);
                }
                lblCount++;           
            }
        }
    }

    /**
     * Add appointments to weekly calendar view
     */
    @FXML
    private void loadSelectedWeek(){
        int year = Integer.parseInt(yearCombo.getSelectionModel().getSelectedItem());
        int month = monthCombo.getSelectionModel().getSelectedIndex();
        // Note: Java's Gregorian Calendar class gives us the right
        // "first day of the month" for a given calendar & month
        // This accounts for Leap Year
        GregorianCalendar gc = new GregorianCalendar(year, month, 1);
        int firstDay = gc.get(Calendar.DAY_OF_WEEK);
        int daysInMonth = gc.getActualMaximum(Calendar.DAY_OF_MONTH);
        // We are "offsetting" our start depending on what the
        // first day of the month is.
        // For example: Sunday start, Monday start, Wednesday start.. etc
        int offset = firstDay;
        int gridCount = 1;
        forwardButton.setDisable(false);
        backwardButton.setDisable(true);
        
       // Go through calendar grid
       for(Node node : calendarGrid.getChildren()){
            VBox day = (VBox) node;
            day.getChildren().clear();
            // Make a new day label   
            day.setStyle("-fx-backgroud-color: white");
            day.setStyle("-fx-font: 14px \"System\" ");
            // Start placing labels on the first day for the month
            if (gridCount < offset) {
                gridCount++;
                // Darken color of the offset days
                day.setStyle("-fx-background-color: #E9F2F5"); 
            } else {
                Label dayNum = new Label(String.valueOf(lblCount));
                dayNum.setPadding(new Insets(5));
                dayNum.setStyle("-fx-text-fill:darkslategray");
                day.getChildren().add(dayNum);
                lblCount++;    
                // Don't place a label if we've reached maximum label for the month
                if (lblCount > daysInMonth) {
                // Instead, darken day color
                day.setStyle("-fx-background-color: #E9F2F5"); 
                }  
            }
        }
    }
    
    // setUser used in LoginController
    public void setUser(User user){
        this.user = user;
    }
    
    // displays appointments, right clickable to edit or delete
    public void showDate(int dayNumber, Appointment app){
        //Image img = new Image(getClass().getClassLoader().getResourceAsStream("academiccalendar/ui/icons/icon2.png"));
        //ImageView imgView = new ImageView();
        //imgView.setImage(img);
        for (Node node: calendarGrid.getChildren()) {
            // Get the current day    
            VBox day = (VBox) node;
            // Don't look at any empty days (they at least must have a day label!)
            if (!day.getChildren().isEmpty()) {
                // Get the day label for that day
                Label lbl = (Label) day.getChildren().get(0);
                // Get the number
                int currentNumber = Integer.parseInt(lbl.getText());
                // Did we find a match?
                if (currentNumber == dayNumber) {
                    // Add an event label with the given description
                    Label descLbl = new Label(app.getDescription());    //(desc + time); 
                    //eventLbl.setGraphic(imgView);
                    descLbl.setText(app.getDescription());
                    // Add label to calendar
                    day.getChildren().add(descLbl);
                    ContextMenu contextMenu = new ContextMenu();
                    MenuItem edit = new MenuItem("edit");
                    edit.setOnAction(new EventHandler<ActionEvent>() {
                        @Override
                        public void handle(ActionEvent event) {
                            handleEditAppointmentButton(app); 
                        }
                    });
                    MenuItem delete = new MenuItem("delete");
                    delete.setOnAction(new EventHandler<ActionEvent>() {
                        @Override
                        public void handle(ActionEvent event) {
                            getAppointmentList().remove(app);
                            try(Connection conn = DriverManager.getConnection(dburl, dbuser, dbpass)){
                                System.out.println("Open");
                                PreparedStatement ps = conn.prepareStatement("delete from appointment where appointmentId = " + app.getAppointmentID()); 
                                ps.executeUpdate();
                                updateView();
                                conn.close();
                                System.out.println("Close");
                            } catch (SQLException ex) {
                                Logger.getLogger(HomeController.class.getName()).log(Level.SEVERE, null, ex);
                            }
                        }
                    });
                    // Add MenuItem to ContextMenu
                    contextMenu.getItems().addAll(edit, delete);
                    descLbl.setOnContextMenuRequested(new EventHandler<ContextMenuEvent>() {
                        @Override
                        public void handle(ContextMenuEvent event) {
                            contextMenu.show(descLbl,event.getScreenX() ,event.getScreenY());
                        }
                    });
                }
            }
        }
    }
    
    /**
     * alert if user has a meeting within 15 minutes of login
     */
    public void upcomingAppointmentAlert(){
        for(Appointment app: AppointmentList.getAppointmentList()){
            if(app.getStart().isAfter(LocalDateTime.now()) && app.getStart().isBefore(LocalDateTime.now().plusMinutes(15)) ){
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Information Dialog");
                alert.setHeaderText("Notice!");
                alert.setContentText("You have an appointment scheduled at "+ app.getStart().toLocalTime());
                alert.showAndWait();
            }   
        }
    }

    // Update monthly calendar view
    @FXML
    public void updateView() {
        loadSelectedMonth();
        initializeAppointments();
    }

    /**
     *  Opens the add appointment page.
     */
    @FXML
    void handleAddAppointmentButton(){
        try{
            if(customerTable.getSelectionModel().getSelectedItem() != null){
                URL location = AddAppointmentController.class.getResource("AddAppointment.fxml");
                FXMLLoader fxmlLoader = new FXMLLoader();
                fxmlLoader.setLocation(location);
                fxmlLoader.setBuilderFactory(new JavaFXBuilderFactory());
                Parent root = (Parent) (Node) fxmlLoader.load(location.openStream());
                AddAppointmentController controller = (AddAppointmentController) fxmlLoader.getController();
                customer = customerTable.getSelectionModel().getSelectedItem();
                controller.setCustomer(customer);
                controller.setUser(user);
                Stage stage = (Stage) addCustomerButton.getScene().getWindow();
                Scene scene = new Scene(root);
                stage.setScene(scene);
                stage.show();
            } else if (customerTable.getSelectionModel().getSelectedItem() == null){
                Alert alert = new Alert(AlertType.INFORMATION);
                alert.setTitle("Information Dialog");
                alert.setHeaderText("Error!");
                alert.setContentText("No Customer Selected");
                alert.showAndWait();
            }

        } catch(Exception e){
            e.printStackTrace();
        }
    } 

    /**
     *  Opens the add customer page.
     * @param event 
     */
    @FXML
    void handleAddCustomerButton(ActionEvent event){
        try{
            FXMLLoader loader = new FXMLLoader(getClass().getResource("AddCustomer.fxml"));
            Parent root = (Parent) loader.load();
            Stage stage = new Stage();    
            stage.setTitle("Add Customer");
            stage.setScene(new Scene(root));
            stage.show();
            AddCustomerController controller = loader.getController();
            controller.setUser(user);
        } catch(Exception e){
            e.printStackTrace();
        }
    } 
    
    /**
     *  Opens the edit appointment window.
     * @param event 
     */
    @FXML
    void handleEditAppointmentButton(Appointment app){
        try{
            URL location = EditAppointmentController.class.getResource("EditAppointment.fxml");
            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(location);
            fxmlLoader.setBuilderFactory(new JavaFXBuilderFactory());
            Parent root = (Parent) (Node) fxmlLoader.load(location.openStream());
            EditAppointmentController controller = (EditAppointmentController) fxmlLoader.getController();
            controller.setUser(user);
            controller.setAppointment(app);
            Stage stage = (Stage) addCustomerButton.getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        } catch(Exception e){
            e.printStackTrace();
        }
    } 
    
    /**
     * opens edit customer window 
     * @param event 
     */
    @FXML
    void handleEditCustomerButton(ActionEvent event){
        try{
            FXMLLoader loader = new FXMLLoader(getClass().getResource("EditCustomer.fxml"));
            Parent root = (Parent) loader.load();
            Stage stage = new Stage();    
            stage.setTitle("Edit Customer");
            stage.setScene(new Scene(root));
            stage.show();
            EditCustomerController controller = loader.getController();
            Customer customer = customerTable.getSelectionModel().getSelectedItem();
            controller.setCustomer(customer);
            controller.setUser(user);

        } catch(Exception e){
            e.printStackTrace();
        }
    } 
    
    /**
     * deletes customer from database and from customer list
     * @param event
     * @throws SQLException 
     */
    @FXML
    void handleDeleteCustomerButton(ActionEvent event) throws SQLException{
        Alert alertConfirm = new Alert(AlertType.CONFIRMATION);
        alertConfirm.setTitle("Confirmation Dialog");
        alertConfirm.setHeaderText("- Are you sure you want to delete this customer?" +
                                   "\n- All events with this customer will also be deleted!");
        Optional<ButtonType> result = alertConfirm.showAndWait();
        if (result.get() == ButtonType.OK){
            if(customerTable.getSelectionModel().getSelectedItem() != null){
                Customer customer = customerTable.getSelectionModel().getSelectedItem();
                customerList.remove(customer);
                try(Connection conn = DriverManager.getConnection(dburl, dbuser, dbpass)){
                    System.out.println("Open");
                    PreparedStatement ps = conn.prepareStatement("delete from customer where customerId = " + customer.getCustomerID()); 
                    ps.executeUpdate();
                }
            }else if(customerTable.getSelectionModel().getSelectedItem() == null){
                Alert alert = new Alert(AlertType.INFORMATION);
                alert.setTitle("Information Dialog");
                alert.setHeaderText("Error");
                alert.setContentText("No customer Selected");
                alert.showAndWait();
            }
        }
    }

    // Report for appointment type per month
    @FXML
    public void createAppointmentTypeReport() throws SQLException, IOException, InterruptedException{
        appointmentTypeList.clear();
        try(Connection conn = DriverManager.getConnection(dburl, dbuser, dbpass)){
            System.out.println("Open");
            PreparedStatement ps = conn.prepareStatement("select distinct description from appointment where userId = " + user.getUserID() + " order by description");
            ResultSet rs = ps.executeQuery();
            
            while(rs.next()){
                Appointment app = new Appointment();
                String description = rs.getString("description");
                PreparedStatement ps1 = conn.prepareStatement("select description, count(*) as 'count' from appointment where userId ="+user.getUserID() +" and description ="+"'"+description+"'");
                ResultSet rs1 = ps1.executeQuery();
                rs1.next();
                String desc2 = rs1.getString("description");
                int count = rs1.getInt("count");
                app.setDescription(description);
                app.setCount(count);
                appointmentTypeList.add(app);
            }
            conn.close();
            System.out.println("Close");
        }
        FXMLLoader loader = new FXMLLoader(getClass().getResource("AppointmentTypeReport.fxml"));
        Parent root = (Parent) loader.load();
        Stage stage = new Stage();    
        stage.setTitle("Appointment Customer Report");
        stage.setScene(new Scene(root));
        stage.show();
    }

    // Report for appointments per customer
    @FXML
    public void createAppointmentCustomerReport() throws SQLException, IOException, InterruptedException{
        appointmentCustomerList.clear();
        try(Connection conn = DriverManager.getConnection(dburl, dbuser, dbpass)){
            System.out.println("Open");
            PreparedStatement ps = conn.prepareStatement("select customerName,customerId from customer order by customerId");
            ResultSet rs = ps.executeQuery();
            while(rs.next()){
                String name = rs.getString("customerName");
                int customerId = rs.getInt("customerId");
                PreparedStatement ps1 = conn.prepareStatement("select count(*) as 'count' from appointment where userId ="+user.getUserID() +" and customerId ="+"'"+customerId+"'");
                ResultSet rs1 = ps1.executeQuery();
                rs1.next();
                int count = rs1.getInt("count");
                Customer cus = new Customer();
                cus.setCustomerName(name);
                cus.setCount(count);
                appointmentCustomerList.add(cus);
            }
            conn.close();
            System.out.println("Close");
        }
        FXMLLoader loader = new FXMLLoader(getClass().getResource("AppointmentCustomerReport.fxml"));
        Parent root = (Parent) loader.load();
        Stage stage = new Stage();    
        stage.setTitle("Appointment Customer Report");
        stage.setScene(new Scene(root));
        stage.show();
    }

    // Report for a selected customers' schedule
    @FXML
    public void createConsultantScheduleReport() throws SQLException, IOException, InterruptedException{
        if(customerTable.getSelectionModel().getSelectedItem() == null){
            Alert alert = new Alert(AlertType.INFORMATION);
            alert.setTitle("Information Dialog");
            alert.setHeaderText("Error!");
            alert.setContentText("No Customer Selected");
            alert.showAndWait();
        }else{
            consultantScheduleAppointments.clear();
            Customer cus = customerTable.getSelectionModel().getSelectedItem();
            try(Connection conn = DriverManager.getConnection(dburl, dbuser, dbpass)){
                System.out.println("Open");
                PreparedStatement ps = conn.prepareStatement("select * from appointment where customerId = " + cus.getCustomerID());
                ResultSet rs = ps.executeQuery();
                while(rs.next()){
                    Appointment consultantApp = new Appointment();
                    consultantApp.setDescription(rs.getString("description"));
                    consultantApp.setLocation(rs.getString("location"));
                    
                    LocalDateTime ldtStart = rs.getTimestamp("start").toLocalDateTime();
                    ZoneId zStart = ZoneId.systemDefault();
                    ZonedDateTime zdtStart = ldtStart.atZone(zStart);
                    Instant instantStart = zdtStart.toInstant();
                    LocalDateTime ldtStart2 = LocalDateTime.ofInstant(instantStart, ZoneOffset.UTC);
                    consultantApp.setStart(ldtStart2);
                    LocalDateTime ldtEnd = rs.getTimestamp("end").toLocalDateTime();
                    ZoneId zEnd = ZoneId.systemDefault();
                    ZonedDateTime zdtEnd = ldtEnd.atZone(zEnd);
                    Instant instantEnd = zdtEnd.toInstant();
                    LocalDateTime ldtEnd2 = LocalDateTime.ofInstant(instantEnd, ZoneOffset.UTC);
                    consultantApp.setEnd(ldtEnd2);
                    consultantScheduleAppointments.add(consultantApp);
                }
                conn.close();
                System.out.println("Close");
            }
            FXMLLoader loader = new FXMLLoader(getClass().getResource("ConsultantScheduleReport.fxml"));
            Parent root = (Parent) loader.load();
            Stage stage = new Stage();    
            stage.setTitle("Consultant Schedule Report");
            stage.setScene(new Scene(root));
            stage.show();
        }
    }

    
    
    /**
     * Initializes the controller class.
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        createCustomerList();
        customerTable.setItems(getCustomerList());
        // lambda to set customer table name column cells more efficiently 
        nameColumn.setCellValueFactory(cellData -> cellData.getValue().customerNameProperty());
        drawCalendarGrid();
        monthCombo.setItems(months);
        monthCombo.setValue(currentMonth);
        yearCombo.setItems(yearList());
        yearCombo.setValue(String.valueOf(currentYear));
        loadSelectedMonth();
        initializeAppointments();
        forwardButton.setVisible(false);
        backwardButton.setVisible(false);
    }
}