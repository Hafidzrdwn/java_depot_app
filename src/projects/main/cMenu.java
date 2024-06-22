package projects.main;

import static projects.main.appDepot.toRupiah;
import java.sql.ResultSet;
import java.sql.Statement;
import projects.sql.Koneksi;

public class cMenu {
    private String nama, tipe;
    private int id, harga;
    
    // sql
    private static Statement stmt;
    private static ResultSet rs;
    static Koneksi k = new Koneksi();
    
    cMenu(){}
    
    cMenu (int id, String nama, int harga, String tipe) {
        this.id = id; this.nama = nama; this.harga = harga; this.tipe = tipe;
    }
    
    public String getTipe(){
        return tipe;
    }    
    
    public String getNama(){
        return nama;
    }   
    
    public int getHarga(){
        return harga;
    }
    
    public void setHarga(int harga) {
        this.harga = harga;
    }
    
    public int getId (){
        return id;
    }
    
    public int getRealHarga(cMenu m) {
        k.connect();
        int real = 0;
        try {
            stmt = k.getcon().createStatement();
            String query = "SELECT harga FROM `menu` WHERE id=%d";
            query = String.format(query, m.getId());
            rs = stmt.executeQuery(query);
            while(rs.next()){
                real = rs.getInt("harga");
            }
            
            rs.close();
            stmt.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
            
        return real;
    }
    
    public static void show_list(){
        k.connect();
        System.out.printf("%-6s %-18s %s \n","NO","NAMA MENU","HARGA");
        System.out.println("=====================================================");
        
        int id = 0, harga = 0;
        String nama, tipe = null;
        try {
            stmt = k.getcon().createStatement();
            String query = "SELECT * FROM `menu`";
            
            rs = stmt.executeQuery(query);
            int i = 1;
            while(rs.next()){
                System.out.printf("%-6s %-18s %s\n",
                        (i)+".", rs.getString("nama"), toRupiah(rs.getInt("harga")));
                
                i++;
            }
            
            rs.close();
            stmt.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        System.out.println("=====================================================");
    }
    
    public static void editMenu(int id, int price) {
        k.connect();
        try {
            stmt = k.getcon().createStatement();
            String query = "UPDATE menu SET harga=%d WHERE id=%d";
            query = String.format(query, price, id);
            
            stmt.execute(query);
            
            stmt.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}