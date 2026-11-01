package model;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

/** INHERITANCE: Excavator mewarisi KendaraanTambang. */
@Entity
@DiscriminatorValue("Excavator")
public class Excavator extends KendaraanTambang {

    @Column(name = "kapasitas")
    private double kapasitasBucket;

    protected Excavator() { // untuk Hibernate
    }

    public Excavator(String id, String namaKendaraan, double hargaSewaPerHari, double kapasitasBucket) {
        super(id, namaKendaraan, hargaSewaPerHari);
        setKapasitasBucket(kapasitasBucket);
    }

    public double getKapasitasBucket() {
        return kapasitasBucket;
    }

    public void setKapasitasBucket(double kapasitasBucket) {
        if (kapasitasBucket <= 0) {
            throw new IllegalArgumentException("Kapasitas bucket (m3) harus lebih dari 0.");
        }
        this.kapasitasBucket = kapasitasBucket;
    }

    @Override
    public double getKapasitas() {
        return kapasitasBucket;
    }

    @Override
    public void setKapasitas(double kapasitas) {
        setKapasitasBucket(kapasitas);
    }

    @Override
    public String getJenis() {
        return "Excavator";
    }

    @Override
    public String getDetail() {
        return String.format("Bucket %.1f m3", kapasitasBucket);
    }

    /**
     * Overriding: aturan berbeda dari DumpTruck.
     * >= 30 hari diskon 15%, >= 7 hari diskon 5%.
     * (Sesuaikan dengan aturan Excavator di kode lama kamu bila berbeda.)
     */
    @Override
    public double hitungBiayaSewa(int hari) {
        validasiHari(hari);
        double total = getHargaSewaPerHari() * hari;
        if (hari >= 30) {
            total = total * 0.85;
        } else if (hari >= 7) {
            total = total * 0.95;
        }
        return total;
    }
}
