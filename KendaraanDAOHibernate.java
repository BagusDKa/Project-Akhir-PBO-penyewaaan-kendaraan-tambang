package dao;

import java.util.List;
import java.util.function.Function;
import model.KendaraanTambang;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import util.HibernateUtil;

/**
 * DAO versi ORM (Hibernate). Tidak ada SQL manual:
 * objek langsung disimpan/diambil, query memakai HQL berbasis nama class & atribut.
 */
public class KendaraanDAOHibernate implements KendaraanDAO {

    private final SessionFactory sessionFactory;

    public KendaraanDAOHibernate() {
        this.sessionFactory = HibernateUtil.getSessionFactory();
    }

    @Override
    public void insert(KendaraanTambang kendaraan) {
        dalamTransaksi(session -> {
            session.persist(kendaraan);
            return null;
        });
    }

    @Override
    public List<KendaraanTambang> getAll() {
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery(
                    "FROM KendaraanTambang k ORDER BY k.id", KendaraanTambang.class)
                    .getResultList();
        }
    }

    @Override
    public KendaraanTambang getById(String id) {
        try (Session session = sessionFactory.openSession()) {
            return session.find(KendaraanTambang.class, id);
        }
    }

    @Override
    public List<KendaraanTambang> getByRentangHarga(double min, double max) {
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery(
                    "FROM KendaraanTambang k WHERE k.hargaSewaPerHari BETWEEN :min AND :max "
                    + "ORDER BY k.hargaSewaPerHari", KendaraanTambang.class)
                    .setParameter("min", min)
                    .setParameter("max", max)
                    .getResultList();
        }
    }

    @Override
    public boolean update(KendaraanTambang kendaraan) {
        return dalamTransaksi(session -> {
            if (session.find(KendaraanTambang.class, kendaraan.getId()) == null) {
                return false;
            }
            session.merge(kendaraan);
            return true;
        });
    }

    @Override
    public boolean delete(String id) {
        return dalamTransaksi(session -> {
            KendaraanTambang k = session.find(KendaraanTambang.class, id);
            if (k == null) {
                return false;
            }
            session.remove(k);
            return true;
        });
    }

    @Override
    public String getNamaSumber() {
        return "Hibernate ORM";
    }

    /** Membuka session + transaksi, commit bila sukses, rollback bila gagal. */
    private <T> T dalamTransaksi(Function<Session, T> aksi) {
        try (Session session = sessionFactory.openSession()) {
            Transaction tx = session.beginTransaction();
            try {
                T hasil = aksi.apply(session);
                tx.commit();
                return hasil;
            } catch (RuntimeException e) {
                if (tx.isActive()) {
                    tx.rollback();
                }
                throw e;
            }
        }
    }
}
