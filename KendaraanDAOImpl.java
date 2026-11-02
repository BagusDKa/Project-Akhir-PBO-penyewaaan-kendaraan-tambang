package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import model.DumpTruck;
import model.Excavator;
import model.KendaraanTambang;
import util.KoneksiDB;

public class KendaraanDAOImpl implements KendaraanDAO {

    private static final String SELECT_DASAR =
            "SELECT id, jenis, nama_kendaraan, harga_sewa_per_hari, kapasitas FROM kendaraan";

    @Override
    public void insert(KendaraanTambang k) throws SQLException {
        String sql = "INSERT INTO kendaraan (id, jenis, nama_kendaraan, harga_sewa_per_hari, kapasitas) "
                   + "VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = KoneksiDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, k.getId());
            ps.setString(2, k.getJenis());
            ps.setString(3, k.getNamaKendaraan());
            ps.setDouble(4, k.getHargaSewaPerHari());
            ps.setDouble(5, k.getKapasitas()); // polymorphism, tanpa instanceof
            ps.executeUpdate();
        }
    }

    @Override
    public List<KendaraanTambang> getAll() throws SQLException {
        String sql = SELECT_DASAR + " ORDER BY id";
        List<KendaraanTambang> hasil = new ArrayList<>();
        try (Connection conn = KoneksiDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                hasil.add(mapRow(rs));
            }
        }
        return hasil;
    }

    @Override
    public KendaraanTambang getById(String id) throws SQLException {
        String sql = SELECT_DASAR + " WHERE id = ?";
        try (Connection conn = KoneksiDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }
        }
        return null;
    }

    @Override
    public List<KendaraanTambang> getByRentangHarga(double min, double max) throws SQLException {
        String sql = SELECT_DASAR + " WHERE harga_sewa_per_hari BETWEEN ? AND ? ORDER BY harga_sewa_per_hari";
        List<KendaraanTambang> hasil = new ArrayList<>();
        try (Connection conn = KoneksiDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDouble(1, min);
            ps.setDouble(2, max);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    hasil.add(mapRow(rs));
                }
            }
        }
        return hasil;
    }

    @Override
    public boolean update(KendaraanTambang k) throws SQLException {
        String sql = "UPDATE kendaraan SET nama_kendaraan = ?, harga_sewa_per_hari = ?, kapasitas = ? WHERE id = ?";
        try (Connection conn = KoneksiDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, k.getNamaKendaraan());
            ps.setDouble(2, k.getHargaSewaPerHari());
            ps.setDouble(3, k.getKapasitas());
            ps.setString(4, k.getId());
            return ps.executeUpdate() > 0;
        }
    }

    @Override
    public boolean delete(String id) throws SQLException {
        String sql = "DELETE FROM kendaraan WHERE id = ?";
        try (Connection conn = KoneksiDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, id);
            return ps.executeUpdate() > 0;
        }
    }

    @Override
    public String getNamaSumber() {
        return "JDBC (PreparedStatement)";
    }

    private KendaraanTambang mapRow(ResultSet rs) throws SQLException {
        String id = rs.getString("id");
        String jenis = rs.getString("jenis");
        String nama = rs.getString("nama_kendaraan");
        double harga = rs.getDouble("harga_sewa_per_hari");
        double kapasitas = rs.getDouble("kapasitas");

        if ("Dump Truck".equals(jenis)) {
            return new DumpTruck(id, nama, harga, kapasitas);
        }
        return new Excavator(id, nama, harga, kapasitas);
    }
}
