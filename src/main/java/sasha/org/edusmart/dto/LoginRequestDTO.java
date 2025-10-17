package sasha.org.edusmart.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class LoginRequestDTO implements Serializable{
    private static final long serialVersionUID = 1L;

    private String email;
    private String password;
}
