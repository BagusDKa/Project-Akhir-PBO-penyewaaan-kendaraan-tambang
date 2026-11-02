package model;

import dao.KendaraanDAO;
import dao.KendaraanDAOImpl;
import java.sql.SQLException;
import java.util.List;

public class PengelolaKendaraan {

    private KendaraanDAO dao;

    public PengelolaKendaraan() {
        this(new KendaraanDAOImpl());
    }

    public PengelolaKendaraan(KendaraanDAO dao) {
        setDao(dao);
    }

    public void setDao(KendaraanDAO dao) {
        if (dao == null) {
            throw new IllegalArgumentException("DAO tidak boleh null.");
        }
        this.dao = dao;
    }

    public String getNamaSumberData() {
        return dao.getNamaSumber();
    }

    public boolean tambahKendaraan(KendaraanTambang kendaraan) {
        try {
            if (dao.getById(kendaraan.getId()) != null) {
                return false;
            }
            dao.insert(kendaraan);
            return true;
        } catch (SQLException e) {
            throw new RuntimeException("Kesalahan database: " + e.getMessage(), e);
        }
    }

    public List<KendaraanTambang> getSemuaKendaraan() {
        try {
            return dao.getAll();
        } catch (SQLException e) {
            throw new RuntimeException("Kesalahan database: " + e.getMessage(), e);
        }
    }

    public KendaraanTambang cari(String id) {
        try {
            return dao.getById(id);
        } catch (SQLException e) {
            throw new RuntimeException("Kesalahan database: " + e.getMessage(), e);
        }
    }

    public List<KendaraanTambang> cari(double hargaMin, double hargaMax) {
        try {
            return dao.getByRentangHarga(hargaMin, hargaMax);
        } catch (SQLException e) {
            throw new RuntimeException("Kesalahan database: " + e.getMessage(), e);
        }
    }

    public boolean updateKendaraan(String id, String namaBaru, double hargaBaru, double kapasitasBaru) {
        KendaraanTambang k = cari(id);
        if (k == null) {
            return false;
        }
        k.setNamaKendaraan(namaBaru);
        k.setHargaSewaPerHari(hargaBaru);
        k.setKapasitas(kapasitasBaru);
        try {
            return dao.update(k);
        } catch (SQLException e) {
            throw new RuntimeException("Kesalahan database: " + e.getMessage(), e);
        }
    }

    public boolean hapusKendaraan(String id) {
        try {
            return dao.delete(id);
        } catch (SQLException e) {
            throw new RuntimeException("Kesalahan database: " + e.getMessage(), e);
        }
    }

    public int getJumlahData() {
        return getSemuaKendaraan().size();
    }
}
