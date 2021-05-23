package library;

public class MataKuliah {
	private String kdmatkul;
    private String nama;
    private String pengajar;
    private int sks;

    public MataKuliah (String kdmatkul, String nama, String pengajar, int sks) {
        this.kdmatkul = kdmatkul;
    	this.nama = nama;
    	this.pengajar = pengajar;
    	this.sks = sks;
    }

    public String getKdmatkul() {
    	return kdmatkul;
    }

    public String getNama() {
        return nama;
    }

    public String getPengajar() {
        return pengajar;
    }

    public int getSks() {
        return sks;
    }
}
