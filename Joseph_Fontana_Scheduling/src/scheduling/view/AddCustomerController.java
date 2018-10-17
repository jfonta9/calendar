package scheduling.view;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import scheduling.model.Customer;
import scheduling.model.CustomerList;
import static scheduling.model.CustomerList.getCustomerList;
import scheduling.model.User;

/**
 * FXML Controller class
 *
 * @author root
 */
public class AddCustomerController implements Initializable {
    @FXML
    private TextField nameTextField;
    @FXML
    private TextField addressTextField;
    @FXML
    private TextField address2TextField;
    @FXML
    private TextField cityTextField;
    @FXML
    private TextField phoneTextField;
    @FXML
    private TextField postalTextField;
    @FXML
    private TextField countryTextField;
    @FXML
    private Button cancelCustomerButton;
    private static final String dburl = "jdbc:mysql://52.206.157.109/U04tA4";
    private static final String dbuser = "U04tA4";
    private static final String dbpass = "53688341124";
    private User user;
    ObservableList<Customer> customerList = getCustomerList();
    Customer customer = new Customer();

    
    
    // setUser used in HomeController
    public void setUser(User user){
        this.user = user;
    }

    /**
     * calls addCustomer method with text field as parameters
     * @param event 
     */
    @FXML
    void handleAddCustomerButton(ActionEvent event){
        if(isInputValid()){
            try {
                addCustomer(nameTextField.getText(), addressTextField.getText(), address2TextField.getText(),
                cityTextField.getText(), countryTextField.getText(), postalTextField.getText(), phoneTextField.getText());
            } catch (SQLException ex) {
                Logger.getLogger(AddCustomerController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    /**
     * closes out of the add customer window
     */
    @FXML 
    void cancel(){
        Stage stage = (Stage) cancelCustomerButton.getScene().getWindow();
        stage.close();
    }

    /**
     *  Inserts customer info into customer table, Creates customer to add to 
     *  customer List 
     * @param name
     * @param address
     * @param address2
     * @param city
     * @param country
     * @param postalCode
     * @param phone
     * @throws SQLException 
     */
    public void addCustomer(String name, String address, String address2,
                                   String city, String country, String postalCode, String phone) throws SQLException{
        int countryId = getCountryId(country);
        int cityId = getCityId(city,countryId);
        int addressId = getAddressId(address,address2,postalCode,phone,cityId);
        try {
            if(customerExist(name,addressId)){
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Information Dialog");
                alert.setHeaderText("Error!");
                alert.setContentText("Customer already exists!");
                alert.showAndWait();
            }else{
                addNewCustomer(name,addressId); 
                cancel();
                customerList.add(customer);
            }
        }catch(SQLException e){
            e.printStackTrace();
        }
    }

    /**
     * adds a new customer into the db and creates customer object
     * @param name
     * @param addressId
     * @throws SQLException 
     */
    public void addNewCustomer(String name, int addressId) throws SQLException{
        try(Connection conn = DriverManager.getConnection(dburl,dbuser,dbpass)){
            System.out.println("Open");
            ObservableList<Customer> customerList = CustomerList.getCustomerList();
            
            PreparedStatement psMaxCustomerId = conn.prepareStatement("SELECT MAX(customerId) FROM customer");
            ResultSet rsMaxCustomerId = psMaxCustomerId.executeQuery();
            rsMaxCustomerId.next();
            int nextCustomerId = rsMaxCustomerId.getInt(1) + 1;
            rsMaxCustomerId.close();
            
            PreparedStatement psNewCustomer = conn.prepareStatement("insert into customer(customerId, customerName,addressId,"
                                            + "active,createDate,createdBy,lastUpdate,lastUpdateBy)values(?,?,?,?,?,?,?,?)");
            psNewCustomer.setInt(1, nextCustomerId);
            psNewCustomer.setString(2, name);
            psNewCustomer.setInt(3, addressId); 
            psNewCustomer.setInt(4, 1); 
            psNewCustomer.setString(5, String.valueOf(LocalDateTime.now()));
            psNewCustomer.setString(6, user.getUsername()); 
            psNewCustomer.setString(7, String.valueOf(LocalDateTime.now()));
            psNewCustomer.setString(8, user.getUsername()); 
            psNewCustomer.execute();
            psNewCustomer.close();
            
            customer.setCustomerID(nextCustomerId);
            customer.setCustomerName(name);
            customer.setCustomerAddressID(addressId);
            customer.setActive(1); // fix
            
            conn.close();
            System.out.println("close");
  
        } catch(Exception e){
            e.printStackTrace();
        }
    
    }

    /**
     * checks the db to see if customer already exists
     * @param name
     * @param addressId
     * @return
     * @throws SQLException 
     */
    public static boolean customerExist(String name, int addressId) throws SQLException{
        try(Connection conn = DriverManager.getConnection(dburl,dbuser,dbpass)){
            System.out.println("Open");
            PreparedStatement psCustomerExist = conn.prepareStatement("SELECT customerId FROM customer WHERE customerName = '" + name + "' " +
                    "AND addressId = " + addressId);
            ResultSet rsCustomerExist = psCustomerExist.executeQuery();
            if(rsCustomerExist.next()){
                rsCustomerExist.close();
                conn.close();
                System.out.println("Close");
                return true;
            }else{
                rsCustomerExist.close();
                conn.close();
                System.out.println("Close");
                return false;
            }
        }
    }
    
    /**
     * returns address id if address exists, creates new address if does not exist
     * @param address
     * @param address2
     * @param postalCode
     * @param phone
     * @param cityId
     * @return
     * @throws SQLException 
     */
    public int getAddressId(String address, String address2, String postalCode, String phone, int cityId) throws SQLException{
        try(Connection conn = DriverManager.getConnection(dburl,dbuser,dbpass)){
            System.out.println("Open");
            ObservableList<Customer> customerList = CustomerList.getCustomerList();
            
            PreparedStatement psAddressId = conn.prepareStatement("SELECT addressId FROM address WHERE address = '" + address + "' AND " +
                    "address2 = '" + address2 + "' AND postalCode = '" + postalCode + "' AND phone = '" + phone + "' AND cityId = " + cityId);
            ResultSet rsAddressId = psAddressId.executeQuery();
            if(rsAddressId.next()){
                int addressId = rsAddressId.getInt("addressId");
                rsAddressId.close();
                customer.setAddress(address);
                customer.setAddress2(address2);
                customer.setPhone(phone);
                customer.setPostalCode(postalCode);
                conn.close();
                System.out.println("Close");
                
                return addressId;
            }
            else{
                PreparedStatement psMaxAddressId = conn.prepareStatement("SELECT MAX(addressId) FROM address");
                ResultSet rsMaxAddressId = psMaxAddressId.executeQuery();
                rsMaxAddressId.next();
                int nextAddressId = rsMaxAddressId.getInt(1) + 1; 
                rsMaxAddressId.close();
                
                PreparedStatement psNewCity = conn.prepareStatement("insert into address(addressId,address,address2,cityId, "
                    + "postalCode, phone, createDate, createdBy, lastUpdate, lastUpdateBy)"
                    + "values(?,?,?,?,?,?,?,?,?,?)");
                psNewCity.setInt(1, nextAddressId);
                psNewCity.setString(2, address);
                psNewCity.setString(3, address2);
                psNewCity.setInt(4, cityId);
                psNewCity.setString(5, postalCode);
                psNewCity.setString(6, phone);
                psNewCity.setString(7, String.valueOf(LocalDateTime.now()));
                psNewCity.setString(8, user.getUsername());
                psNewCity.setString(9, String.valueOf(LocalDateTime.now())); 
                psNewCity.setString(10, user.getUsername()); 
                psNewCity.execute();
                
                customer.setAddress(address);
                customer.setAddress2(address2);
                customer.setPhone(phone);
                customer.setPostalCode(postalCode);
                
                conn.close();
                System.out.println("Close");
                
                return nextAddressId;
            }
        }
    }
    
    /**
     * returns city id if city exists, creates new city if does not exist
     * @param city
     * @param countryId
     * @return
     * @throws SQLException 
     */
    public int getCityId(String city, int countryId) throws SQLException{
        try(Connection conn = DriverManager.getConnection(dburl,dbuser,dbpass)){
            System.out.println("Open");
            ObservableList<Customer> customerList = CustomerList.getCustomerList();
            
            PreparedStatement psCityId = conn.prepareStatement("SELECT cityId FROM city WHERE city = '" + city + "' AND countryId =" + countryId);
            ResultSet rsCityId = psCityId.executeQuery();
            if(rsCityId.next()){
                int cityId = rsCityId.getInt("cityId");
                rsCityId.close();
                customer.setCityId(cityId);
                customer.setCity(city);
                customer.setCountryId(countryId);
                conn.close();
                System.out.println("Close");
                return cityId;
                
            }
            else{
                PreparedStatement psMaxCityId = conn.prepareStatement("SELECT MAX(cityId) FROM city");
                ResultSet rsMaxCityId = psMaxCityId.executeQuery();
                rsMaxCityId.next();
                int nextCityId = rsMaxCityId.getInt(1) + 1; 
                rsMaxCityId.close();
                
                PreparedStatement psNewCity = conn.prepareStatement("insert into city(cityId,city,countryId,createDate,"
                        + "createdBy,lastUpdate,lastUpdateBy)values(?,?,?,?,?,?,?)");
                
                psNewCity.setInt(1,nextCityId); 
                psNewCity.setString(2, city);
                psNewCity.setInt(3,countryId); 
                psNewCity.setString(4, String.valueOf(LocalDateTime.now()));
                psNewCity.setString(5, user.getUsername());
                psNewCity.setString(6, String.valueOf(LocalDateTime.now())); 
                psNewCity.setString(7, user.getUsername());
                psNewCity.execute();
                
                customer.setCityId(nextCityId);
                customer.setCity(city);
                customer.setCountryId(countryId);
                
                conn.close();
                System.out.println("Close");
                
                return nextCityId;
            }
        }
    }
    
    /**
     * returns country id if country exists, creates new country if does not exist
     * @param country
     * @return
     * @throws SQLException 
     */
    public int getCountryId(String country) throws SQLException{
        try(Connection conn = DriverManager.getConnection(dburl,dbuser,dbpass)){
            System.out.println("Open");
            ObservableList<Customer> customerList = CustomerList.getCustomerList();
            
            PreparedStatement psCountryId = conn.prepareStatement("SELECT countryId FROM country WHERE country = '" + country + "'");
            ResultSet rsCountryId = psCountryId.executeQuery();
            if(rsCountryId.next()){
                int countryId = rsCountryId.getInt("countryId");
                rsCountryId.close();
                
                customer.setCountryId(countryId);
                customer.setCountry(country);
                
                conn.close();
                System.out.println("Close");
                
                return countryId;
            }
            else{
                PreparedStatement psMaxCountryId = conn.prepareStatement("SELECT MAX(countryId) FROM country");
                ResultSet rsMaxCountryId = psMaxCountryId.executeQuery();
                rsMaxCountryId.next();
                int nextCountryId = rsMaxCountryId.getInt(1) + 1; 
                rsMaxCountryId.close();
                
                PreparedStatement psNewCountry = conn.prepareStatement("insert into country(countryId, country,"
                    + "createDate,createdBy,lastUpdate,lastUpdateBy) values(?,?,?,?,?,?)");
                psNewCountry.setInt(1, nextCountryId ); 
                psNewCountry.setString(2, country);
                psNewCountry.setString(3, String.valueOf(LocalDateTime.now()));
                psNewCountry.setString(4, user.getUsername());
                psNewCountry.setString(5, String.valueOf(LocalDateTime.now())); 
                psNewCountry.setString(6, user.getUsername()); 
                psNewCountry.execute();
                
                customer.setCountryId(nextCountryId);
                customer.setCountry(country);
                
                conn.close();
                System.out.println("Close");
                
                return nextCountryId;
            }
        }
    }
    
    // Validates user input in text fields.
    private boolean isInputValid() {
        String errorMessage = "";
        if (nameTextField.getText() == null || nameTextField.getText().length() == 0) {
            errorMessage += "No valid customer name!\n"; 
        }
        if (addressTextField.getText() == null || addressTextField.getText().length() == 0){
            errorMessage += "No valid customer address!\n";
        }
        if (address2TextField.getText() == null || address2TextField.getText().length() == 0){
            errorMessage += "No valid address 2!\n";
        }
        if (postalTextField.getText() == null || postalTextField.getText().length() == 0) {
            errorMessage += "No valid postal code!\n"; 
        }
        if (cityTextField.getText() == null || cityTextField.getText().length() == 0) {
            errorMessage += "No valid city!\n"; 
        }
        if (countryTextField.getText() == null || countryTextField.getText().length() == 0) {
            errorMessage += "No valid country!\n"; 
        }
        if (phoneTextField.getText() == null || phoneTextField.getText().length() == 0) {
            errorMessage += "No valid phone number!\n"; 
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
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
       address2TextField.setText("none");
    }  
}
