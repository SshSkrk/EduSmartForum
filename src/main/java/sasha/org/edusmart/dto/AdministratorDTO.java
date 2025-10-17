package sasha.org.edusmart.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class AdministratorDTO implements Serializable{
    private static final long serialVersionUID = 1L;

    private Integer id;
    private String email;
    private boolean verified;
    private String password;
    private String role;
    private String firstName;
    private String lastName;
}
