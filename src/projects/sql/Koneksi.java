package projects.sql;

import java.sql.DriverManager;
import java.sql.Connection;

public class Koneksi {
    private String url = "jdbc:mysql://localhost:3306/kasir_bp2";
    private String username = "root";
    private String password = "";
    private Connection con;
    
    public Connection getcon(){
          return con;
    }
    
    public void connect() {
        try{
              con = DriverManager.getConnection(url, username, password);
          }catch(Exception e){
              System.out.println(e);
          }
    }
}
