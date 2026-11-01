package model;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

/** INHERITANCE: DumpTruck mewarisi KendaraanTambang. */
@Entity
@DiscriminatorValue("Dump Truck")
public class DumpTruck extends KendaraanTambang {

    @Column(name = "kapasitas")
    private double kapasitasTon;

    protected DumpTruck() { // untuk Hibernate
    }

    public DumpTruck(String id, String namaKendaraan, double hargaSewaPerHari, double kapasitasTon) {
        super(id, namaKendaraan, hargaSewaPerHari);
        setKapasitasTon(kapasitasTon);
    }

    public double getKapasitasTon() {
        return kapasitasTon;
    }

    public void setKapasitasTon(double kapasitasTon) {
        if (kapasitasTon <= 0) {
            throw new IllegalArgumentException("Kapasitas (ton) harus lebih dari 0.");
        }
        this.kapasitasTon = kapasitasTon;
    }

    @Override
    public double getKapasitas() {
        return kapasitasTon;
    }

    @Override
    public void setKapasitas(double kapasitas) {
        setKapasitasTon(kapasitas);
    }

    @Override
    public String getJenis() {
        return "Dump Truck";
    }

    @Override
    public String getDetail() {
        return String.format("Kapasitas %.1f ton", kapasitasTon);
    }

    /** Overriding: sewa >= 7 hari diskon 10%. */
    @Override
    public double hitungBiayaSewa(int hari) {
        validasiHari(hari);
        double total = getHargaSewaPerHari() * hari;
        if (hari >= 7) {
            total = total * 0.90;
        }
        return total;
    }
}
