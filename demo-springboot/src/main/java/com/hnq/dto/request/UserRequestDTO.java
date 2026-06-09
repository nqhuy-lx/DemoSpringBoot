package com.hnq.dto.request;

import java.io.Serializable;

public class UserRequestDTO implements Serializable {
    private String firstName;
    private String lastName;
    private String email;
    private String phone;

    public UserRequestDTO(String phone, String email, String firstName, String lastName) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phone = phone;
    }

    public String getLastName() {
        return lastName;
    }

    public String getEmail() {
        return email;
    }

    public String getPhone() {
        return phone;
    }

    public String getFirstName() {
        return firstName;
    }
}
