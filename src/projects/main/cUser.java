package projects.main;

import java.sql.Statement;
import projects.sql.Koneksi;

public class cUser {
    private int id, member_id;
    private String name, role;
    Koneksi k = new Koneksi();
    
    // sql
    private static Statement stmt;
            
    cUser(String name, String role) {
        this.name = name;
        this.role = role;
    }
    
    cUser(int id, String name, String role) {
        this.id = id;
        this.name = name;
        this.role = role;
    }
    
    cUser(int id, String name, int member_id, String role) {
        this.id = id;
        this.name = name;
        this.member_id = member_id;
        this.role = role;
    }

    public String getName() {
        return name;
    }

    public int getId() {
        return id;
    }
    
    public int getMemberId() {
        return member_id;
    }
    
    public String getRole() {
        return role;
    }

    public void setName(String name) {
        this.name = name;
    }
    
    public void setRole(String role) {
        this.role = role;
    }
    
    public void ubahPassword(cUser user, String pass) {
        k.connect();
        try {
            stmt = k.getcon().createStatement();
            String query = "UPDATE user SET password='%s' WHERE member_id=%d";
            query = String.format(query, pass, user.getMemberId());
            
            stmt.execute(query);
            
            stmt.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
