package it.uniupo.msvm.server.persistence.dao;

import it.uniupo.msvm.common.model.User;
import it.uniupo.msvm.server.persistence.DatabaseManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Optional;

public class UserDAO {
    private final DatabaseManager dbManager;

    public UserDAO() {
        this.dbManager = DatabaseManager.getInstance();
    }
    /**
     * Cerca un utente per mail
     * Grazie al indice 'idx_user_email', questa operazione e O(logN)
     * */
    public Optional<User> findByEmail(String email)
    {
        String sql="SELECT id,username,email,password FROM users WHERE email=?";
        try(Connection conn=dbManager.getConnection();
            PreparedStatement pstmt=conn.prepareStatement(sql)){
            pstmt.setString(1,email);
            try(ResultSet rs=pstmt.executeQuery()){
                if(rs.next())
                {
                    User user=new User(
                            rs.getLong("id"),
                            rs.getString("username"),
                            rs.getString("password"),
                            rs.getString("email")
                    );
                    return Optional.of(user);
                }
            }
            catch (Exception e) {
                System.err.println("[DAO] Error finding userby email:" + e.getMessage());
            }
        }catch (Exception e) {
            System.err.println("[DAO] Error finding userby email:" + e.getMessage());
        }
        return Optional.empty();
    }
}
