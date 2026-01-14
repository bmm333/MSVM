package it.uniupo.msvm.server.services;

import it.uniupo.msvm.common.dto.UserDTO;

public class AuthService {
    public UserDTO authenticate(String email,String password)
    {
        UserDTO user=new UserDTO();
        return user;
    }
    public UserDTO registerUser(String email,String username,String password)
    {
        UserDTO user=new UserDTO();
        return user;
    }

}
