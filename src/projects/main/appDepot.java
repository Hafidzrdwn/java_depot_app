package projects.main;
import com.formdev.flatlaf.themes.FlatMacLightLaf;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;
import javax.swing.JOptionPane;
import javax.swing.UIManager;
import projects.sql.Koneksi;

public class appDepot {
    static Scanner sc = new Scanner(System.in);
    
    // LIST MENU
    static cMenu m1 = new cMenu(1, "Nasi Goreng", 22000, "Makanan");
    static cMenu m2 = new cMenu(2, "Mie Goreng", 24000, "Makanan");
    static cMenu m3 = new cMenu(3, "Cap Jay", 24000, "Makanan");
    static cMenu m4 = new cMenu(4, "Es Teh", 5000, "Minuman");
    static cMenu m5 = new cMenu(5, "Es Jeruk", 7000, "Minuman");
    
    static cLogin loggedin = new cLogin();
    static cUser userLogin;
    static int pilih = 0, pilih2 = 0, pilih3 = 0;
    
    static myQueue<cTransaksi> daftarTransaksi = new myQueue<cTransaksi>();
    static cDaftarTransaksi dt_transaksi_depot = new cDaftarTransaksi();
    static myQueue<cTransaksi> dataDepot = dt_transaksi_depot.getDaftarTransaksi();
        
    public static void main(String[] args) {       
        try {
            FlatMacLightLaf.setup();
            UIManager.put("Button.arc", 10);
            UIManager.put("TextComponent.arc", 10);
        } catch (Exception e) {
            e.printStackTrace();
        }
        new FormLogin(loggedin).setVisible(true);
    }
    
    public static String toRupiah(double text) {
        double a = text;
        Locale localeID = new Locale("in", "ID");
        NumberFormat formatRupiah = NumberFormat.getCurrencyInstance(localeID);
        return formatRupiah.format(a).replace(",00", "").replace("Rp", "Rp ").trim();
    }
    
    public static String makeKodeTransaksi() {
        LocalDate current = LocalDate.now();
        DateTimeFormatter format = DateTimeFormatter.ofPattern("MMdd");
        
        int countQueue = findLastQueue();
        String kode = current.format(format) + ((countQueue < 10) ? ("0"+(countQueue+1)) : (countQueue+1));
        
        return kode;
    }
    
    public static int findLastQueue() {
        if(dataDepot.size() == 0) return 0;
        
        int count = 0;
        
        for(myQueue.Node<cTransaksi> t = dataDepot.front; t != null; t = t.next) {
            if(t == dataDepot.front) {
                count++;
                continue;
            }
            if(!t.data.getKode().equals(t.prev.data.getKode())) {
                count++;
            }
        }
        
        return count;
    }
    
    private static boolean checkDoubleData(cTransaksi tr) {
        if(daftarTransaksi.size() == 0) return false;
        
        boolean state = false;
        
        for(myQueue.Node<cTransaksi> t = daftarTransaksi.front; t != null; t = t.next) {
            if(t.data.getKode().equals(tr.getKode()) && t.data.getMenu().getNama().equals(tr.getMenu().getNama())) {
                int jml = t.data.getJumlah() + tr.getJumlah();
                t.data.setJumlah(jml);
                
                state = true;
                break;
            }
        }
        
        return state;
    }
    
    public static void main_menu_list() {
        String mnu_list = null;
        String role = userLogin.getRole().toLowerCase();

        switch (role) {
            case "pembeli":
            case "member":
                mnu_list = "1. TAMBAH TRANSAKSI\n2. HAPUS TRANSAKSI\n3. LIHAT ORDER PEMBELIAN\n";
                if(role.equals("member")) {
                    mnu_list += "4. UBAH PASSWORD\n5. ";
                } else {
                    mnu_list += "4. ";
                }
                
                mnu_list += "SELESAI";
                break;
            case "administrator":
                mnu_list = "1. PROSES TRANSAKSI\n2. LOGOUT";
                break;
            case "pemilik":
                mnu_list = "1. TOTAL NILAI ORDER (SUDAH DIPROSES)\n2. TOTAL NILAI ORDER (BELUM DIPROSES)\n"
                        + "3. EDIT MENU\n4. TOTAL NILAI PENJUALAN\n5. TOTAL BELANJA MEMBER\n6. GRAFIK PENJUALAN"
                        + "\n7. LOGOUT";
                break;
            default:
                throw new AssertionError();
        }
        
        System.out.println(mnu_list);
    }
    
    public static void menu() {
        int back = -1;
        dataDepot = dt_transaksi_depot.getDaftarTransaksi();
        userLogin = loggedin.getUserLoggedin();
        String kode = makeKodeTransaksi();
        do {
            dataDepot = dt_transaksi_depot.getDaftarTransaksi();
            
            // update harga menu by DB
            m1.setHarga(m1.getRealHarga(m1));
            m2.setHarga(m2.getRealHarga(m2));
            m3.setHarga(m3.getRealHarga(m3));
            m4.setHarga(m4.getRealHarga(m4));
            m5.setHarga(m5.getRealHarga(m5));
            
            String userRole = userLogin.getRole().toLowerCase();
            String user = (loggedin.getLoggedin_username() != null) ? loggedin.getLoggedin_username() : "tester";
            
            System.out.println("=====================================================");
            System.out.printf("%-18s %-32s %s\n", "|", "PROGRAM KASIR", "|");
            System.out.printf("%-13s %-37s %s\n", "|", "DEPOT MIE AGUNG SURABAYA", "|");
            System.out.println("=====================================================");
            System.out.printf("%-12s %-38s %s\n","|","HALO, SELAMAT DATANG "+ user.toUpperCase() +"!", "|");
            System.out.println("=====================================================");
            System.out.println("MENU "+userLogin.getRole().toUpperCase());
            System.out.println("=====================================================");

            // MENU ADMIN
            if(userRole.equals("administrator") && countOrderByStatus(0) > 0) {
                tampil_order_admin();
                showSingleOrder();
            } else if(userRole.equals("administrator")) {
                tampil_order_admin();
            }
            
            // SHOW SUBMENU
            main_menu_list();
            System.out.println("=====================================================");
            
            
            System.out.print("Ketik disini ");
            switch (userRole) {
                case "pembeli" ->  System.out.print("(1-4): ");
                case "member" ->  System.out.print("(1-5): ");
                case "administrator" ->  System.out.print("(1-2): ");
                case "pemilik" ->  System.out.print("(1-7): ");
                default -> throw new AssertionError();
            }
            
            pilih = sc.nextInt();
            
            switch (userRole) {
                case "pembeli":
                case "member":
                    back = (userRole.equals("member")) ? 5 : 4;
                    
                    switch (pilih) {
                        // TAMBAH TRANSAKSI
                        case 1:
                            submenu_tambah_transaksi(kode);
                            break;
                        // HAPUS TRANSAKSI
                        case 2:
                            submenu_hapus_transaksi();
                            break;
                        // LIHAT ORDER
                        case 3:
                            submenu_lihat_order();
                            break;
                        // SELESAI / UBAH PASSWORD
                        case 4:
                            if(!userRole.equals("member")) {
                                dt_transaksi_depot.connectData(daftarTransaksi, userLogin);
                                myQueue.Node<cTransaksi> t = daftarTransaksi.front;
        
                                for(; t != null; t = t.next) {
                                    if(t.data.getPembeli().getName().equals(userLogin.getName())) {
                                       daftarTransaksi.hapusTransaksi(t.data);
                                    }
                                } 
                                daftarTransaksi = new myQueue<cTransaksi>();
                                JOptionPane.showMessageDialog(null, "Terima Kasih, Anda Telah Logout!");
                                main(null);
                            } else {
                                submenu_ubah_password();
                            }
                            break;
                        // SELESAI FOR MEMBER
                        case 5:
                            if(userRole.equals("member")) {
                                dt_transaksi_depot.connectData(daftarTransaksi, userLogin);
                                myQueue.Node<cTransaksi> t = daftarTransaksi.front;
        
                                for(; t != null; t = t.next) {
                                    if(t.data.getPembeli().getName().equals(userLogin.getName())) {
                                       daftarTransaksi.hapusTransaksi(t.data);
                                    }
                                }
                                daftarTransaksi = new myQueue<cTransaksi>();
                                JOptionPane.showMessageDialog(null, "Terima Kasih, Anda Telah Logout!");
                                main(null);
                            } else {
                                JOptionPane.showMessageDialog(null, "Pilihan tidak valid!",
                            "ERROR", JOptionPane.ERROR_MESSAGE);
                            }
                            break;
                        default:
                            JOptionPane.showMessageDialog(null, "Pilihan tidak valid!",
                            "ERROR", JOptionPane.ERROR_MESSAGE);
                    }      
                    
                    break;
                case "administrator":
                    back = 2;
                    switch (pilih) {
                        case 1:
                            proses_transaksi();
                            break;
                        case 2:
                            JOptionPane.showMessageDialog(null, "Terima Kasih, Anda Telah Logout!");
                            main(null);
                            break;
                        default:
                            JOptionPane.showMessageDialog(null, "Pilihan tidak valid!",
                            "ERROR", JOptionPane.ERROR_MESSAGE);
                    }
                    break;
                case "pemilik":
                    back = 7;
                    switch (pilih) {
                        case 1:
                            // lihat sudah proses
                            total_nilai_order(1);
                            break;
                        case 2:
                            // lihat belum proses
                            total_nilai_order(0);
                            break;
                        case 3:
                            // edit menu
                            submenu_edit_menu();
                            break;
                        case 4:
                            // total nilai penjualan
                            total_nilai_penjualan();
                            break;
                        case 5:
                            // biaya belanja member
                            total_belanja_member();
                            break;
                        case 6:
                            // grafik penjualan
                            show_grafik_penjualan();
                            break;
                        case 7:
                            JOptionPane.showMessageDialog(null, "Terima Kasih, Anda Telah Logout!");
                            main(null);
                            break;
                        default:
                            JOptionPane.showMessageDialog(null, "Pilihan tidak valid!",
                            "ERROR", JOptionPane.ERROR_MESSAGE);
                    }
                    break;
                default:
                    throw new AssertionError();
            }
            
        } while(pilih != back);
    }
    
    public static void submenu_tambah_transaksi(String kode) {
        cTransaksi tr = null;
        int jumlah = 0;
        System.out.println("=====================================================");
        System.out.printf("%-18s %s\n","", "TAMBAH TRANSAKSI");
        System.out.println("=====================================================\n");

        do {
            System.out.printf("%-20s %s\n","", "DAFTAR MENU");
            System.out.println("=====================================================");
            cMenu.show_list();
            System.out.println("Ketik 6 untuk [Kembali]");

            System.out.print("Pilih Menu (1-5) = ");
            pilih2 = sc.nextInt();

            if(pilih2 > 0 && pilih2 < 6) {
                System.out.print("Jumlah = ");
                jumlah = sc.nextInt();
            } 
            
            switch (pilih2) {
                case 1:
                    tr = new cTransaksi(kode, userLogin, m1, jumlah, 0);
                    break;
                case 2:
                    tr = new cTransaksi(kode, userLogin, m2, jumlah, 0);
                    break;
                case 3:
                    tr = new cTransaksi(kode, userLogin, m3, jumlah, 0);
                    break;
                case 4:
                    tr = new cTransaksi(kode, userLogin, m4, jumlah, 0);
                    break;
                case 5:
                    tr = new cTransaksi(kode, userLogin, m5, jumlah, 0);
                    break;
                case 6:
                    menu();
                    break;
                default:
                    JOptionPane.showMessageDialog(null, "Pilihan tidak valid!",
                                "ERROR", JOptionPane.ERROR_MESSAGE);
            }
            
            if(userLogin.getRole().toLowerCase().equals("member")) {
                // diskon
                double discount = tr.getMenu().getHarga() * 0.05;
                double newPrice = tr.getMenu().getHarga() - discount;
                tr.getMenu().setHarga((int)newPrice);
            }
            
            if(!checkDoubleData(tr)) {
                daftarTransaksi.enqueue(tr);
            }
            
        } while(pilih2 != 6);
    }
    
    private static boolean tampilkan_order() {
        myQueue.Node<cTransaksi> t = daftarTransaksi.front;
        boolean state = false;
        
        if(t == null || daftarTransaksi.size() == 0) {
            System.out.printf("\n%-25s %s","", "ANDA BELUM PERNAH ORDER.");
            System.out.println("\n");
            menu();
        } else {
            System.out.println("\nKODE TRANSAKSI : " + t.data.getKode());
            System.out.println("=========================================================================");
            System.out.printf("%-5s %-16s %-10s %-10s %-14s %s\n","NO","NAMA MENU", "TIPE", "JUMLAH", "HARGA"
            ,"SUBTOTAL");
            System.out.println("=========================================================================");
            int i = 1;
            int totalBiaya = 0;
            for(; t != null; t = t.next) {
                if(t.data.getPembeli().getName().equals(userLogin.getName())) {
                    int subTotal = t.data.getJumlah() * t.data.getMenu().getHarga();
                    System.out.printf("%-5s %-16s %-10s %-10s %-14s %s\n",(i)+".",t.data.getMenu().getNama()
                            , t.data.getMenu().getTipe(), t.data.getJumlah(), toRupiah(t.data.getMenu().getHarga())
                    , (toRupiah(subTotal)));
                    
                    totalBiaya += subTotal;
                    i++;
                }
            }
            System.out.println("=========================================================================");
            System.out.println("TOTAL BIAYA BELANJA : " + toRupiah(totalBiaya) + "\n");
            state = true;
        }
        
        return state;
    }
    
    private static int hitungJumlahOrder() {
        myQueue.Node<cTransaksi> t = daftarTransaksi.front;
        
        if(t == null) return 0;
        
        int count = 0;
        for(; t != null; t = t.next) {
            if(t.data.getPembeli().getName().equals(userLogin.getName())) {
                count++;
            }
        }
        
        return count;
    }
    
    public static cTransaksi findTransaksi(int nomor) {
        myQueue.Node<cTransaksi> t = daftarTransaksi.front;
        
        if(t == null) return null;
        
        cTransaksi dt = null;
        
        int count = 0;
        for(; t != null; t = t.next) {
            if(t.data.getPembeli().getName().equals(userLogin.getName())) {
                count++;
            }
            
            if(count == nomor)  {
                dt = t.data;
                break;
            }
         }
        return dt;
    }
    
    public static void submenu_lihat_order() {
        System.out.println("=========================================================================");
        System.out.printf("%-25s %s\n","", "DAFTAR ORDER PEMBELIAN");
        System.out.println("=========================================================================");
        
        tampilkan_order();
    }
    
    public static void submenu_hapus_transaksi() {
        System.out.println("=========================================================================");
        System.out.printf("%-26s %s\n","", "HAPUS TRANSAKSI ANDA");
        System.out.println("=========================================================================");
        do {
            boolean next = tampilkan_order();
            if(next) {
                System.out.println("Ketik 6 untuk [Kembali]");
                System.out.print("Masukkan Nomor Menu = "); pilih3 = sc.nextInt();

                if(pilih3 == 6) {
                    menu();
                } else if(pilih3 <= 0 || pilih3 > hitungJumlahOrder()) {
                    JOptionPane.showMessageDialog(null, "Order tidak ditemukan!",
                                        "ERROR", JOptionPane.ERROR_MESSAGE);
                } else {
                    // HAPUS TRANSAKSI
                    cTransaksi singleTr = findTransaksi(pilih3);
                    boolean stateHapus = daftarTransaksi.hapusTransaksi(singleTr);

                    if(stateHapus) {
                        JOptionPane.showMessageDialog(null, "Berhasil Menghapus Order!",
                                        "HAPUS ORDER", JOptionPane.INFORMATION_MESSAGE);
                    } else {
                        JOptionPane.showMessageDialog(null, "Order Gagal Dihapus!",
                                        "ERROR", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        } while(pilih3 != 6);
    }
    
    public static void submenu_ubah_password() {
        System.out.println("=====================================================");
        System.out.printf("%-18s %s\n","", "UBAH PASSWORD");
        System.out.println("=====================================================");
        
        String newPass, confirm = null;
        System.out.println("Enter \"-\" untuk [Kembali]");
        System.out.print("Masukkan Password Baru = "); newPass = sc.next();
        if(!newPass.trim().equals("-")) {
            do {
                System.out.print("Konfirmasi Password Baru = "); confirm = sc.next(); 

                if(!confirm.equals(newPass)) JOptionPane.showMessageDialog(null, "Konfirmasi Password tidak sesuai!",
                                            "ERROR", JOptionPane.ERROR_MESSAGE);
            } while(!confirm.equals(newPass));

            userLogin.ubahPassword(userLogin, newPass);
            JOptionPane.showMessageDialog(null, "Password Berhasil Diubah!",
                                            "BERHASIL", JOptionPane.INFORMATION_MESSAGE);
        }
    }
    
    public static int countOrderByStatus(int status) {
        myQueue.Node<cTransaksi> t = dataDepot.front;
        
        if(t == null) return 0;
        
        int count = 0;
        for(; t != null; t = t.next) {
            if(t.data.getStatus() == status) {
                count++;
            }
        }
        
        return count;
    }
    
    public static void tampil_order_admin() {
        myQueue.Node<cTransaksi> t = dataDepot.front;
        
        if(t == null || countOrderByStatus(0) == 0) {
            System.out.printf("\n%-17s %s","", "BELUM ADA ORDER.");
            System.out.println("\n");
        } else {
            System.out.println("JUMLAH TRANSAKSI BELUM DIPROSES : " + countOrderByStatus(0));
            System.out.println("=====================================================================");
            System.out.printf("%-4s %-16s %-10s %-16s %-8s %s\n","NO","KODE TRANSAKSI","PEMBELI", "NAMA MENU", "JUMLAH", "STATUS");
            System.out.println("=====================================================================");
            int i = 1;
            for(; t != null; t = t.next) {
                if(t.data.getStatus() == 0) {
                    System.out.printf("%-4s %-16s %-10s %-16s %-8s %s\n",(i)+".", t.data.getKode()
                            , (t.data.getPembeli().getRole().toLowerCase().equals("member")) ? t.data.getPembeli().getMemberId() : t.data.getPembeli().getName()
                            , t.data.getMenu().getNama(), t.data.getJumlah(), t.data.getStatus());
                    i++;
                }
            }
            System.out.println("=====================================================================");
        }
    }
    
    private static cTransaksi getSingleOrder() {
        myQueue.Node<cTransaksi> t = dataDepot.front;
        cTransaksi order = null;
        
        for(; t != null; t = t.next) {
            if(t.data.getStatus() == 0) {
               order = t.data;
               break;
            }
        }
        
        return order;
    }
    
    private static void showSingleOrder() {
        cTransaksi order = getSingleOrder();
        
        System.out.println("TRANSAKSI YANG AKAN DIPROSES");
        System.out.println("=====================================================================");
        System.out.println("Kode Transaksi : " + order.getKode());
        System.out.println("Pembeli : " + ((order.getPembeli().getRole().toLowerCase().equals("member")) ?
                order.getPembeli().getMemberId() : order.getPembeli().getName()));
        System.out.println("Nama Menu : " + order.getMenu().getNama() + " [" + order.getMenu().getTipe() + "]");
        System.out.println("Jumlah : " + order.getJumlah());
        System.out.println("=====================================================");
    }
    
    // sql
    static Koneksi k = new Koneksi();
    private static Statement stmt;
    private static ResultSet rs;
    
    public static void proses_transaksi() {
        k.connect();
        cTransaksi order = getSingleOrder();
        try {
            stmt = k.getcon().createStatement();
            String query = "UPDATE `detail_transaksi` SET status=1 WHERE kode_transaksi=%d AND menu_id=%d";
            query = String.format(query, Integer.parseInt(order.getKode()), order.getMenu().getId());
            
            stmt.execute(query);
            
            stmt.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        dt_transaksi_depot.getDataFromDB();
        System.out.println("Transaksi Berhasil Diproses...");
    }
    
    public static void total_nilai_order(int status) {
        myQueue.Node<cTransaksi> t = dataDepot.front;
        System.out.println("==========================================================================================");
        System.out.println("TOTAL NILAI ORDER ("+ ((status == 0) ? "BELUM" : "SUDAH") +" DIPROSES) [" + countOrderByStatus(status) +"]");
        System.out.println("==========================================================================================");
        
        System.out.printf("%-4s %-16s %-10s %-16s %-10s %-13s %s\n","NO","KODE TRANSAKSI","PEMBELI", "NAMA MENU", "JUMLAH", "HARGA", "SUBTOTAL");
        System.out.println("==========================================================================================");
        int i = 1;
        int biayaTotal = 0;
        if(countOrderByStatus(status) == 0) {
            System.out.printf("\n%-38s %s","", "ORDER KOSONG.");
            System.out.println("\n");
        } else {
            for(; t != null; t = t.next) {
                if(t.data.getStatus() == status) {
                    int subTotal = t.data.getJumlah() * t.data.getMenu().getHarga();
                    System.out.printf("%-4s %-16s %-10s %-16s %-10s %-13s %s\n",(i)+".", t.data.getKode()
                            , (t.data.getPembeli().getRole().toLowerCase().equals("member")) ? t.data.getPembeli().getMemberId() : t.data.getPembeli().getName()
                            , t.data.getMenu().getNama(), t.data.getJumlah(), toRupiah(t.data.getMenu().getHarga()), toRupiah(subTotal));
                    biayaTotal += subTotal;
                    i++;
                }
            }
        }
        System.out.println("==========================================================================================");
        System.out.println("TOTAL : " + toRupiah(biayaTotal));
    }
    
    public static void submenu_edit_menu() {
        System.out.printf("\n%-20s %s\n","", "DAFTAR MENU");
        System.out.println("=====================================================");
        cMenu.show_list();
        int pilihMenu = 0;
        do {
            System.out.println("Ketik 6 untuk [Kembali]");
            System.out.print("Pilih Menu yang akan diubah (1-5)= "); pilihMenu = sc.nextInt();

            switch (pilihMenu) {
                case 1:
                    System.out.println("Harga Lama : "+ toRupiah(m1.getHarga()));
                    break;
                case 2:
                    System.out.println("Harga Lama : "+ toRupiah(m2.getHarga()));
                    break;
                case 3:
                    System.out.println("Harga Lama : "+ toRupiah(m3.getHarga()));
                    break;
                case 4:
                    System.out.println("Harga Lama : "+ toRupiah(m4.getHarga()));
                    break;
                case 5:
                    System.out.println("Harga Lama : "+ toRupiah(m5.getHarga()));
                    break;
                case 6: 
                    menu();
                    break;
                default:
                    JOptionPane.showMessageDialog(null, "Pilihan tidak valid!",
                                "ERROR", JOptionPane.ERROR_MESSAGE);
            }
            
            if(pilihMenu >= 1 && pilihMenu <= 5) {
                int newPriceMenu = 0;
                System.out.print("Masukkan harga baru = "); newPriceMenu = sc.nextInt();
                new cMenu().editMenu(pilihMenu, newPriceMenu);
                JOptionPane.showMessageDialog(null, "Berhasil Mengedit Harga Menu!",
                                "BERHASIL", JOptionPane.INFORMATION_MESSAGE);
            }
            
        } while(pilihMenu != 6);
    }
    
    public static void total_nilai_penjualan() {
        k.connect();
        System.out.println("=================================");
        System.out.println("TOTAL NILAI PENJUALAN");
        System.out.println("=================================");
        int totalPendapatan = 0;
        String list = "";
        try {
            stmt = k.getcon().createStatement();
            String query = "SELECT m.id, m.nama, SUM(dt.subtotal) AS pendapatan FROM `detail_transaksi` AS dt "
                    + "RIGHT JOIN menu AS m ON dt.menu_id = m.id GROUP BY m.id, m.nama";
            
            rs = stmt.executeQuery(query);
            int i = 0;
            while(rs.next()){
                totalPendapatan += rs.getInt("pendapatan");
                list += (i+1)+". "+rs.getString("nama") + " : " + toRupiah(rs.getInt("pendapatan")) + "\n";
                i++;
            }
            
            rs.close();
            stmt.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        System.out.println("Total Pendapatan : " + toRupiah(totalPendapatan));
        System.out.println(list);
    }
    
    public static void total_belanja_member() {
        k.connect();
        System.out.println("=================================");
        System.out.println("TOTAL BELANJA MEMBER");
        System.out.println("=================================");
        try {
            stmt = k.getcon().createStatement();
            String query = "SELECT u.id, u.name, u.member_id, SUM(dt.subtotal) AS total_belanja FROM `user` AS u "
                    + "LEFT JOIN transaksi AS t ON u.id = t.user_id "
                    + "JOIN detail_transaksi AS dt ON t.kode = dt.kode_transaksi "
                    + "WHERE u.member_id IS NOT NULL "
                    + "GROUP BY u.id, u.name";
            
            rs = stmt.executeQuery(query);
            int i = 0;
            while(rs.next()){
                System.out.printf("%-2s %-13s %-2s %s\n", (i+1)+".", (rs.getString("name") + " ["+rs.getInt("member_id")+"]"), ":", toRupiah(rs.getInt("total_belanja")));
                i++;
            }
            
            rs.close();
            stmt.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private static String makeGrafik(int total) {
        int numX = total / 10000;
        StringBuilder graph = new StringBuilder();
        for (int i = 0; i < numX; i++) {
            graph.append("X");
        }
        
        return (graph.length() > 0) ? graph.toString() + " " : "";
    }
    
    public static void show_grafik_penjualan() {
        k.connect();
        System.out.println("=============================================");
        System.out.println("GRAFIK PENJUALAN");
        System.out.println("=============================================");
        try {
            stmt = k.getcon().createStatement();
            String query = "SELECT m.nama, SUM(dt.subtotal) AS pendapatan " +
                       "FROM detail_transaksi AS dt " +
                       "RIGHT JOIN menu AS m ON dt.menu_id = m.id " +
                       "GROUP BY dt.menu_id, m.nama";
            
            rs = stmt.executeQuery(query);
            while(rs.next()){
                System.out.printf("%-12s %-2s %s\n", rs.getString("nama"), ":", makeGrafik(rs.getInt("pendapatan")) + toRupiah(rs.getInt("pendapatan")));
            }
            
            rs.close();
            stmt.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}