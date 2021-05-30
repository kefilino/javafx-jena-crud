package library;

public class Enrollment {
	private String id;
    private String npm;
    private String kdmatkul;
    private int tahun;

    public Enrollment(String id, String npm, String kdmatkul, int tahun) {
        this.id = id;
    	this.npm = npm;
    	this.kdmatkul = kdmatkul;
    	this.tahun = tahun;
    }

    public String getId() {
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
