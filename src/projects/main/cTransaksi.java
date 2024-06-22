package projects.main;

public class cTransaksi {
    String kode;
    cUser pembeli;
    cMenu menu;
    int jumlahMn;
    int status;
    
    cTransaksi(String kode, cUser pembeli, cMenu menu, int jumlahMn, int status) {
        this.kode = kode;
        this.pembeli = pembeli;
        this.menu = menu;
        this.jumlahMn = jumlahMn;
        this.status = status;
    }
    
    public void setStatus(int s) {
        this.status = s;
    }
    
    public String getKode() {
        return this.kode;
    }
    
    public cUser getPembeli() {
        return this.pembeli;
    }
    
    public cMenu getMenu() {
        return this.menu;
    }
    
    public int getJumlah() {
        return this.jumlahMn;
    }
    
    public void setJumlah(int jumlahMn) {
        this.jumlahMn = jumlahMn;
    }
    
    public int getStatus() {
        return this.status;
    }
}
