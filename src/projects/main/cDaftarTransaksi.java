package projects.main;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.PreparedStatement;
import projects.sql.Koneksi;

public class cDaftarTransaksi {
    private myQueue<cTransaksi> dtDepot = new myQueue<cTransaksi>();
    
    // sql
    private static Statement stmt;
    private static ResultSet rs;
    Koneksi k = new Koneksi();
    
    public myQueue<cTransaksi> getDaftarTransaksi() {
        this.getDataFromDB();
        return this.dtDepot;
    }
    
    public void connectData(myQueue<cTransaksi> dt, cUser user) {
        k.connect();
        myQueue.Node<cTransaksi> t = dt.front;
        myQueue<cTransaksi> clone = new myQueue<>();
        
        for(; t != null; t = t.next) {
            if(t.data.getPembeli().getName().equals(user.getName())) {
               clone.enqueue(t.data);
            }
        }
        
        if(user.getRole().equals("pembeli")) {
            int userId = 0;
            try {
                stmt = k.getcon().createStatement();
                String sql = "INSERT INTO user (name, role_id) VALUE('%s', %d)";
                sql = String.format(sql, user.getName(), 1);

                stmt.execute(sql);
                
                String query = "SELECT id FROM `user` WHERE name = '%s'";
                query = String.format(query, user.getName());
            
                rs = stmt.executeQuery(query);
                while(rs.next()) {
                    userId = rs.getInt("id");
                }
                
                rs.close();
                stmt.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            
            saveData(clone, userId);
        } else {
            saveData(clone, user.getId());
        }
    }
    
    private void saveData(myQueue<cTransaksi> data, int userId) {
        k.connect();
        String insertSql = "INSERT INTO `transaksi` (kode, user_id) VALUES (?, ?)";
        String insertDetailSql = "INSERT INTO `detail_transaksi` (kode_transaksi, menu_id, qty, subtotal) VALUES (?, ?, ?, ?)";
        
        try (Connection conn = k.getcon()){   
            
            conn.setAutoCommit(false);
            
            try (PreparedStatement insertStmt = conn.prepareStatement(insertSql);
                 PreparedStatement insertDetail = conn.prepareStatement(insertDetailSql)) {
                        
                myQueue.Node<cTransaksi> t = data.front;

                if(t != null) {
                    insertStmt.setInt(1, Integer.parseInt(t.data.getKode()));
                    insertStmt.setInt(2, userId);
                    insertStmt.executeUpdate();
                }
                
            
                for(; t != null; t = t.next) {
                    insertDetail.setInt(1, Integer.parseInt(t.data.getKode()));
                    insertDetail.setInt(2, t.data.getMenu().getId());
                    insertDetail.setInt(3, t.data.getJumlah());
                    insertDetail.setInt(4, (t.data.getJumlah() * t.data.getMenu().getHarga()));
                    insertDetail.addBatch();
                }
                
                insertDetail.executeBatch();
                
                conn.commit();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public void getDataFromDB() {
        k.connect();
        String query = "SELECT t.kode, dt.status, dt.qty, m.id AS menu_id, m.nama AS nama_menu, m.harga, m.tipe, u.id AS user_id, "
                + "u.name AS user_name, u.member_id, r.name AS role FROM `transaksi` AS t "
                + "JOIN detail_transaksi AS dt ON t.kode = dt.kode_transaksi "
                + "JOIN user AS u ON t.user_id = u.id "
                + "JOIN menu AS m ON dt.menu_id = m.id "
                + "JOIN role AS r ON u.role_id = r.id "
                + "ORDER BY dt.id";
        try {
            stmt = k.getcon().createStatement();
            rs = stmt.executeQuery(query);

            this.dtDepot = new myQueue<cTransaksi>();
            while (rs.next()) {
                cUser usr = null;
                if (rs.getString("role").toLowerCase().equals("member")) {
                    usr = new cUser(rs.getInt("user_id"), rs.getString("user_name"), rs.getInt("member_id"), rs.getString("role"));
                } else {
                    usr = new cUser(rs.getInt("user_id"), rs.getString("user_name"), rs.getString("role"));
                }
                cMenu mn = new cMenu(rs.getInt("menu_id"), rs.getString("nama_menu"), rs.getInt("harga"), rs.getString("tipe"));
                cTransaksi tr = new cTransaksi(String.valueOf(rs.getInt("kode")), usr, mn, rs.getInt("qty"), rs.getInt("status"));
                
                this.dtDepot.enqueue(tr);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
