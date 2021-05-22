package library;

public class Mahasiswa {
	private String npm;
    private String nama;
    private int angkatan;
    private String email;
    private String telp;

    public Mahasiswa(String npm, String nama, int angkatan, String email, String telp){
        this.npm = npm;
    	this.nama = nama;
        this.angkatan = angkatan;
        this.email = email;
        this.telp = telp;
    }

    public String getNpm() {
    	return npm;
    }

    public String getNama() {
        return nama;
    }

    public int getAngkatan() {
        return angkatan;
    }

    public String getEmail() {
        return email;
    }

    public String getTelp() {
        return telp;
    }
}
