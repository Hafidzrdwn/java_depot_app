package projects.main;

import java.sql.ResultSet;
import java.sql.Statement;
import projects.sql.Koneksi;

public class cLogin {
    private cUser loggedin;
    private static Statement stmt;
    private static ResultSet rs;
    static Koneksi k = new Koneksi();

    public String getLoggedin_username() {
        return loggedin.getName();
    }
    
    public String getLoggedin_role() {
        return loggedin.getRole();
    }
    
    public cUser getUserLoggedin() {
        return loggedin;
    }
    
    public boolean login(String username, String password, String role) {
        k.connect();
        if(role.equals("pembeli")) {
            this.loggedin = new cUser(username, role);
            return true;
        }
        
        String cekUsername = null, cekPassword = null, userRole = null;
        int idUser = 0, memberIdUser = 0;
        try {
            stmt = k.getcon().createStatement();
            String cond = (role.equals("member")) ? "u.member_id" : "u.name";
            String format_cond = (role.equals("member")) ? "%d" : "'%s'";
            String query = "SELECT u.id, u.name, u.password, u.member_id, r.name AS role FROM `user` AS u "
                    + "LEFT JOIN role r ON u.role_id = r.id WHERE "+ cond +" = "+ format_cond +" AND password = '%s'";
            query = String.format(query, (role.equals("member")) ? Integer.parseInt(username) : username, password);
            
            rs = stmt.executeQuery(query);
            
            while(rs.next()){
                idUser = rs.getInt("id");
                cekUsername = rs.getString("name");
                cekPassword = rs.getString("password");
                memberIdUser = rs.getInt("member_id");
                userRole = rs.getString("role");
            }
            
            rs.close();
            stmt.close();
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        if(cekUsername == null && cekPassword == null) return false;

        if(role.equals("member")) {
            this.loggedin = new cUser(idUser, cekUsername, memberIdUser, userRole);    
        } else {
            this.loggedin = new cUser(idUser, cekUsername, userRole);
        }
        return true;
    }
}
