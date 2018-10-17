/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package scheduling.model;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 *
 * @author root
 */
public class Customer {
    private final IntegerProperty customerID;
    private final StringProperty customerName;
    private final IntegerProperty customerAddressID;
    private final StringProperty address;
    private final StringProperty address2;
    private final StringProperty postalCode;
    private final IntegerProperty cityId;
    private final StringProperty city;
    private final StringProperty phone;
    private final IntegerProperty countryId;
    private final StringProperty country;
    private final IntegerProperty active;
    private final IntegerProperty count;

    /**
     * Constructor
     */

    public Customer() {
        customerID = new SimpleIntegerProperty();
        customerName = new SimpleStringProperty();
        customerAddressID = new SimpleIntegerProperty();
        active = new SimpleIntegerProperty();
        address = new SimpleStringProperty();
        address2 = new SimpleStringProperty();
        postalCode = new SimpleStringProperty();
        cityId = new SimpleIntegerProperty();
        city = new SimpleStringProperty();
        phone = new SimpleStringProperty();
        countryId = new SimpleIntegerProperty();
        country = new SimpleStringProperty();
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
    
    // customerID
    public int getCustomerID() {
        return this.customerID.get();
    }
    public void setCustomerID(int customerID) {
        this.customerID.set(customerID);
    }
    public IntegerProperty customerIDProperty(){
        return customerID;
    }
    
    // customerName
    public String getCustomerName() {
        return this.customerName.get();
    }
    public void setCustomerName(String customerName) {
        this.customerName.set(customerName);
    }
    public StringProperty customerNameProperty(){
        return customerName;
    }

    // customerAddressID
    public int getCustomerAddressID() {
        return this.customerAddressID.get();
    }
    public void setCustomerAddressID(int customerAddressID) {
        this.customerAddressID.set(customerAddressID);
    }   
    public IntegerProperty customerAddressIDProperty(){
        return customerAddressID;
    }
    
    // active
    public int getActive(){
        return this.active.get();
    }
    public void setActive(int active){
        this.active.set(active);
    }
    public IntegerProperty customerActiveProperty(){
        return active;
    }
    
    // postal code
    public String getPostalCode() {
        return this.postalCode.get();
    }
    public void setPostalCode(String postalCode) {
        this.postalCode.set(postalCode);
    }
    public StringProperty postalCodeProperty(){
        return postalCode;
    }
    
    // address
    public String getAddress() {
        return this.address.get();
    }
    public void setAddress(String address) {
        this.address.set(address);
    }
    public StringProperty addressProperty(){
        return address;
    }
    
    // address2
    public String getAddress2() {
        return this.address2.get();
    }
    public void setAddress2(String address2) {
        this.address2.set(address2);
    }
    public StringProperty address2Property(){
        return address2;
    }
    
    // city
    public String getCity() {
        return this.city.get();
    }
    public void setCity(String city) {
        this.city.set(city);
    }
    public StringProperty cityProperty(){
        return city;
    }
    
    // city Id
    public int getCityId() {
        return this.cityId.get();
    }
    public void setCityId(int cityId) {
        this.cityId.set(cityId);
    }   
    public IntegerProperty cityIdProperty(){
        return cityId;
    }
 
    
    // country Id
    public int getCountryId() {
        return this.countryId.get();
    }
    public void setCountryId(int countryId) {
        this.countryId.set(countryId);
    }   
    public IntegerProperty countryIdProperty(){
        return countryId;
    }
    
    //country 
    public String getCountry() {
        return this.country.get();
    }
    public void setCountry(String country) {
        this.country.set(country);
    }
    public StringProperty countryProperty(){
        return country;
    }
    
    // phone
    public String getPhone() {
        return this.phone.get();
    }
    public void setPhone(String phone) {
        this.phone.set(phone);
    }
    public StringProperty phoneProperty(){
        return phone;
    }
}

