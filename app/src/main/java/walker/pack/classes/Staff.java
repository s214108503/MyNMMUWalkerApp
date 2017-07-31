package walker.pack.classes;

import java.io.Serializable;

/**
 * Created by Olebogeng Malope on 7/17/2017.
 */

public class Staff implements Serializable {
    private String Staff_ID; //pk
    private String Door_ID, Floor_Number, Building_Number; //fk
    private String Name, Surname, Position, Department, Campus, Phone, Email, Image_URL;

    public Staff(String staff_ID, String door_ID, String floor_Number, String building_Number, String name, String surname, String position, String department, String campus, String phone, String email, String image_URL) {
        Staff_ID = staff_ID;
        Door_ID = door_ID;
        Floor_Number = floor_Number;
        Building_Number = building_Number;
        Name = name;
        Surname = surname;
        Position = position;
        Department = department;
        Campus = campus;
        Phone = phone;
        Email = email;
        Image_URL = image_URL;
    }

    public String getStaff_ID() {
        return Staff_ID;
    }

    public void setStaff_ID(String staff_ID) {
        Staff_ID = staff_ID;
    }

    public String getDoor_ID() {
        return Door_ID;
    }

    public void setDoor_ID(String door_ID) {
        Door_ID = door_ID;
    }

    public String getFloor_Number() {
        return Floor_Number;
    }

    public void setFloor_Number(String floor_Number) {
        Floor_Number = floor_Number;
    }

    public String getBuilding_Number() {
        return Building_Number;
    }

    public void setBuilding_Number(String building_Number) {
        Building_Number = building_Number;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getSurname() {
        return Surname;
    }

    public void setSurname(String surname) {
        Surname = surname;
    }

    public String getPosition() {
        return Position;
    }

    public void setPosition(String position) {
        Position = position;
    }

    public String getDepartment() {
        return Department;
    }

    public void setDepartment(String department) {
        Department = department;
    }

    public String getCampus() {
        return Campus;
    }

    public void setCampus(String campus) {
        Campus = campus;
    }

    public String getPhone() {
        return Phone;
    }

    public void setPhone(String phone) {
        Phone = phone;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public String getImage_URL() {
        return Image_URL;
    }

    public void setImage_URL(String image_URL) {
        Image_URL = image_URL;
    }
}
