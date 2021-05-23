package library;

public class Dosen {
	private String nim;
    private String nama;

    public Dosen (String nim, String nama) {
        this.nim = nim;
    	this.nama = nama;
    }

    public String getNim() {
    	return nim;
    }

    public String getNama() {
        return nama;
    }
}
