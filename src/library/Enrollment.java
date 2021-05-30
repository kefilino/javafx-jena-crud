package library;

public class Enrollment {
	private String id;
    private String npm;
    private String kdmatkul;
    private String tahun;

    public MataKuliah (String id, String npm, String kdmatkul, String tahun) {
        this.id = id;
    	this.npm = npm;
    	this.kdmatkul = kdmatkul;
    	this.tahun = tahun;
    }

    public String getID() {
    	return id;
    }

    public String getNpm() {
        return npm;
    }

    public String getKdmatkul() {
        return kdmatkul;
    }

    public int getTahun() {
        return tahun;
    }
}
