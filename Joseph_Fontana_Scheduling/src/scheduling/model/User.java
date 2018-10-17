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
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 *
 * @author root
 */
public class User {
    private final IntegerProperty userID;
    private final StringProperty username;
    private final StringProperty password;

    
    public User() {
        userID = new SimpleIntegerProperty();
        username = new SimpleStringProperty();
        password = new SimpleStringProperty();
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

    public String getUsername() {
        return this.username.get();
    }

    public void setUsername(String username) {
        this.username.set(username);
    }
    public StringProperty userNameProperty(){
        return username;
    }

    public String getPassword() {
        return this.password.get();
    }

    public void setPassword(String password) {
        this.password.set(password);
    }   
    public StringProperty passwordProperty(){
        return password;
    }
    
}
