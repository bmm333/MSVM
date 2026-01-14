package it.uniupo.msvm.server.services;

import it.uniupo.msvm.common.dto.UserDTO;
import it.uniupo.msvm.common.model.User;
import it.uniupo.msvm.server.persistence.dao.UserDAO;

import java.util.Optional;

/**
 * Service Layer per authentication con bcrypt per garantire sicurezza dei dati
 * */
public class AuthService {
    private final UserDAO userDAO;

    //12 rounds
    private static final int WORK_FACTORs = 12;
    public AuthService()
    {
        this.userDAO=new UserDAO();
    }
    /**
     * Gestisce lautenticazione
     * */
    public UserDTO authenticate(String email,String rawPassword)
    {
        //validazione dati
        if(email==null||rawPassword==null||email.isBlank())
        {
            throw new IllegalArgumentException("Invalid email or password");
        }
        //retrival del utente(cerchiamo hash nel db)
        Optional<User> userOpt=userDAO.findByEmail(email);
        //se lutente non esiste , lanciamo comunque credenziali non valide
        //per evitare di rilevare se l'email e presente nel sistema
        if(userOpt.isEmpty())
        {
            throw new SecurityException("Credenziali non valide");
        }
        User user=userOpt.get();
        String storedHash=user.getPassword();
        //verifica password
        boolean passwordMatch=BCrypt.checkpw(rawPassword,storedHash);
        if (!passwordMatch) {
            System.out.println("[AuthService] Login failed for: " + email + " (Bad Password)");
            throw new SecurityException("Credenziali non valide.");
        }
        //dto senza pw
        System.out.println("[AuthService] Login success: " + email);
        return new UserDTO(user.getId(), user.getUsername(), user.getEmail());
    }
    /**
     * Gestisce la registrazione
     * Hasha la password con un nuovo Salt prima di salvarla
     * */
    public UserDTO registerUser(String username,String email,String rawPassword)
    {
        //validazione dati
        if(rawPassword==null||rawPassword.length()<8)
        {
            throw new IllegalArgumentException("Password troppo corta");
        }
        //hashing
        String hashedPassword=BCrypt.hashpw(rawPassword,BCrypt.gensalt(WORK_FACTORs));
        User newUser=new User(null,username,hashedPassword,email);
        boolean success=userDAO.save(newUser);
        if(!success)
        {
            throw new RuntimeException("Errore durante la registrazione");
        }
        System.out.println("[AuthService] Registration success for: " + email);
        return new UserDTO(null,username,email);
    }
}
