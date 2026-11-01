package dao;

import java.sql.SQLException;
import java.util.List;
import model.KendaraanTambang;

/** Kontrak akses data. Diimplementasikan oleh JDBC dan Hibernate (ORM). */
public interface KendaraanDAO {

    void insert(KendaraanTambang kendaraan) throws SQLException;

    List<KendaraanTambang> getAll() throws SQLException;

    KendaraanTambang getById(String id) throws SQLException;

    List<KendaraanTambang> getByRentangHarga(double min, double max) throws SQLException;

    boolean update(KendaraanTambang kendaraan) throws SQLException;

    boolean delete(String id) throws SQLException;

    String getNamaSumber();
}
