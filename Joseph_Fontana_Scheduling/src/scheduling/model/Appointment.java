package scheduling.model;

import java.time.LocalDateTime;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 *
 * @author root
 */
public class Appointment {
    private final IntegerProperty appointmentID;
    private final IntegerProperty customerID;
    private final IntegerProperty userID;
    private final StringProperty title;
    private final StringProperty description;
    private final StringProperty location;
    private final StringProperty contact;
    private final StringProperty url;
    private final ObjectProperty<LocalDateTime> start;
    private final ObjectProperty<LocalDateTime> end;
    private final StringProperty createDate;
    private final StringProperty createdBy;
    private final StringProperty lastUpdate;
    private final StringProperty lastUpdateBy;
    private final IntegerProperty count;
    
    public Appointment() {
        appointmentID = new SimpleIntegerProperty();
        customerID = new SimpleIntegerProperty();
        userID = new SimpleIntegerProperty();
        title = new SimpleStringProperty();
        description = new SimpleStringProperty();
        location = new SimpleStringProperty();
        contact = new SimpleStringProperty();
        url = new SimpleStringProperty();
        createDate = new SimpleStringProperty();
        createdBy = new SimpleStringProperty();
        lastUpdate = new SimpleStringProperty();
        lastUpdateBy = new SimpleStringProperty();
        start = new SimpleObjectProperty();
        end = new SimpleObjectProperty();
        count = new SimpleIntegerProperty();
    }
    
    /**
     * Getters and setters
     * @return 
     */
    
    public int getCount() {
        return this.count.get();
    }
    public void setCount(int count) {
        this.count.set(count);
    }
    public IntegerProperty countProperty(){
        return count;
    }

    public int getAppointmentID() {
        return this.appointmentID.get();
    }
    public void setAppointmentID(int appointmentID) {
        this.appointmentID.set(appointmentID);
    }
    public IntegerProperty appointmentIdProperty(){
        return appointmentID;
    }
    
    public int getCustomerID() {
        return this.customerID.get();
    }
    public void setCustomerID(int customerID) {
        this.customerID.set(customerID);
    }
    public IntegerProperty customerIdProperty(){
        return customerID;
    }
    
    public int getUserID() {
        return this.userID.get();
    }
    public void setUserID(int userID) {
        this.userID.set(userID);
    }
    public IntegerProperty userIdProperty(){
        return userID;
    }
    
    
    public String getTitle() {
        return this.title.get();
    }
    public void setTitle(String title) {
        this.title.set(title);
    }
    public StringProperty titleProperty(){
        return title;
    }
    
    
    public String getDescription(){
        return this.description.get();
    }
    public void setDescription(String description){
        this.description.set(description);
    }
    public StringProperty descriptionProperty(){
        return description;
    }
    
    
    public String getLocation(){
        return this.location.get();
    }
    public void setLocation(String location){
        this.location.set(location);
    }
    public StringProperty locationProperty(){
        return location;
    }
    
    public String getContact(){
        return this.contact.get();
    }
    public void setContact(String contact){
        this.contact.set(contact);
    }
    public StringProperty contactProperty(){
        return contact;
    }
    
    public String getUrl(){
        return this.url.get();
    }
    public void setUrl(String url){
        this.url.set(url);
    }
    public StringProperty urlProperty(){
        return url;
    }
    
    public LocalDateTime getStart(){
        return this.start.get();
    }
    public void setStart(LocalDateTime start){
        this.start.set(start);
    }
    public ObjectProperty startProperty(){
        return start;
    }
    

    
    public LocalDateTime getEnd(){
        return this.end.get();
    }
    public void setEnd(LocalDateTime end){
        this.end.set(end);
    }
    public ObjectProperty endProperty(){
        return end;
    }
    

    public String getCreateDate(){
        return this.createDate.get();
    }
    public void setCreateDate(String createDate){
        this.createDate.set(createDate);
    }
    public StringProperty createDateProperty(){
        return createDate;
    }
    
    public String getCreatedBy(){
        return this.createdBy.get();
    }
    public void setCreatedBy(String createdBy){
        this.createdBy.set(createdBy);
    }
    public StringProperty createdByProperty(){
        return createdBy;
    }
    
    
    public String getLastUpdate(){
        return this.lastUpdate.get();
    }
    public void setLastUpdate(String lastUpdate){
        this.lastUpdate.set(lastUpdate);
    }
    public StringProperty lastUpdateProperty(){
        return lastUpdate;
    }
    
    
    public String getLastUpdateBy(){
        return this.lastUpdateBy.get();
    }
    public void setLastUpdateBy(String lastUpdateBy){
        this.lastUpdateBy.set(lastUpdateBy);
    }
    public StringProperty lastUpdateByProperty(){
        return lastUpdateBy;
    }
    
}