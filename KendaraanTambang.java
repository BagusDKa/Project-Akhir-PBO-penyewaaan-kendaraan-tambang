package model;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorColumn;
import jakarta.persistence.DiscriminatorType;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.Table;

/**
 * ABSTRACTION  : class abstract + method abstract.
 * ENCAPSULATION: atribut private, akses lewat getter/setter yang memvalidasi.
 * ORM          : dipetakan Hibernate ke tabel "kendaraan" (Single Table Inheritance),
 *                kolom "jenis" menjadi pembeda (discriminator) DumpTruck / Excavator.
 */
@Entity
@Table(name = "kendaraan")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "jenis", discriminatorType = DiscriminatorType.STRING, length = 20)
public abstract class KendaraanTambang {

    @Id
    @Column(name = "id", length = 20)
    private String id;

    @Column(name = "nama_kendaraan", nullable = false, length = 100)
    private String namaKendaraan;

    @Column(name = "harga_sewa_per_hari", nullable = false)
    private double hargaSewaPerHari;

    /** Constructor kosong wajib ada untuk Hibernate. */
    protected KendaraanTambang() {
    }

    public KendaraanTambang(String id, String namaKendaraan, double hargaSewaPerHari) {
        setId(id);
        setNamaKendaraan(namaKendaraan);
        setHargaSewaPerHari(hargaSewaPerHari);
    }

    public String getId() {
        return id;
    }

    public String getNamaKendaraan() {
        return namaKendaraan;
    }

    public double getHargaSewaPerHari() {
        return hargaSewaPerHari;
    }

    public void setId(String id) {
        if (id == null || id.trim().isEmpty()) {
            throw new IllegalArgumentException("ID tidak boleh kosong.");
        }
        this.id = id.trim();
    }

    public void setNamaKendaraan(String namaKendaraan) {
        if (namaKendaraan == null || namaKendaraan.trim().isEmpty()) {
            throw new IllegalArgumentException("Nama kendaraan tidak boleh kosong.");
        }
        this.namaKendaraan = namaKendaraan.trim();
    }

    public void setHargaSewaPerHari(double hargaSewaPerHari) {
        if (hargaSewaPerHari <= 0) {
            throw new IllegalArgumentException("Harga sewa harus lebih dari 0.");
        }
        this.hargaSewaPerHari = hargaSewaPerHari;
    }

    // ===== Method abstract (wajib di-override subclass) =====
    public abstract String getJenis();

    public abstract String getDetail();

    public abstract double getKapasitas();

    public abstract void setKapasitas(double kapasitas);

    public abstract double hitungBiayaSewa(int hari);

    // ===== Overloading =====
    public double hitungBiayaSewa(int hari, double diskonPersen) {
        if (diskonPersen < 0 || diskonPersen > 100) {
            throw new IllegalArgumentException("Diskon harus antara 0 sampai 100.");
        }
        double total = hitungBiayaSewa(hari); // dynamic dispatch (polymorphism)
        return total - (total * diskonPersen / 100);
    }

    protected void validasiHari(int hari) {
        if (hari <= 0) {
            throw new IllegalArgumentException("Jumlah hari harus lebih dari 0.");
        }
    }

    @Override
    public String toString() {
        String harga = String.format("Rp %,.2f / hari", hargaSewaPerHari);
        return String.format("%-8s | %-12s | %-20s | %-24s | %s",
                id, getJenis(), namaKendaraan, harga, getDetail());
    }
}
