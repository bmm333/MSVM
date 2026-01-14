package it.uniupo.msvm.common.service;

import it.uniupo.msvm.common.dto.FullProfileDTO;
import it.uniupo.msvm.common.dto.UserDTO;
import it.uniupo.msvm.common.dto.UserLoginDTO;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface MsvmClientService extends Remote {

    UserDTO login(UserLoginDTO loginDto)throws RemoteException;
    UserDTO register(UserLoginDTO registerDto)throws RemoteException;

    FullProfileDTO getProfile(Long userId) throws RemoteException;
    boolean updateProfile(FullProfileDTO profileDto) throws RemoteException;

}
