package controller;

import dao.KendaraanDAOHibernate;
import dao.KendaraanDAOImpl;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.List;
import model.DumpTruck;
import model.Excavator;
import model.KendaraanTambang;
import model.PengelolaKendaraan;
import util.HibernateUtil;
import util.InputValidator;
import view.KendaraanView;

/**
 * CONTROLLER: menerima event dari View, memvalidasi input (Scanner),
 * memanggil Model, lalu memperbarui View.
 */
public class KendaraanController {

    private static final int SUMBER_HIBERNATE = 0;
    private static final int SUMBER_JDBC = 1;

    private final PengelolaKendaraan model;
    private final KendaraanView view;

    public KendaraanController(PengelolaKendaraan model, KendaraanView view) {
        this.model = model;
        this.view = view;
        daftarkanListener();
    }

    public void jalankan() {
        view.setVisible(true);
        gantiSumberData(); // pakai pilihan awal combo (Hibernate ORM) lalu muat data
    }

    private void daftarkanListener() {
        view.addTambahListener(e -> tambahData());
        view.addUpdateListener(e -> updateData());
        view.addHapusListener(e -> hapusData());
        view.addBersihkanListener(e -> view.bersihkanForm());
        view.addHitungListener(e -> hitungBiayaSewa());
        view.addCariListener(e -> cariByHarga());
        view.addTampilSemuaListener(e -> muatSemuaData());
        view.addSumberDataListener(e -> gantiSumberData());
        view.addTabelSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                pilihBarisTabel();
            }
        });
        view.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                HibernateUtil.shutdown();
            }
        });
    }

    /** Penanganan error terpusat: input salah -> peringatan, error lain -> pesan kesalahan. */
    private void jalankanAksi(Runnable aksi) {
        try {
            aksi.run();
        } catch (IllegalArgumentException e) {
            view.tampilkanPeringatan(e.getMessage());
        } catch (RuntimeException e) {
            view.tampilkanError("Terjadi kesalahan:\n" + e.getMessage());
        }
    }

    private void gantiSumberData() {
        jalankanAksi(() -> {
            switch (view.getSumberDataTerpilih()) {
                case SUMBER_HIBERNATE:
                    model.setDao(new KendaraanDAOHibernate());
                    break;
                case SUMBER_JDBC:
                    model.setDao(new KendaraanDAOImpl());
                    break;
                default:
                    throw new IllegalStateException("Sumber data tidak dikenal.");
            }
            view.tampilkanDaftar(model.getSemuaKendaraan());
        });
        // menampilkan sumber yang benar-benar aktif (tetap JDBC bila Hibernate gagal disiapkan)
        view.setStatus("Sumber data aktif: " + model.getNamaSumberData());
    }

    private void muatSemuaData() {
        jalankanAksi(() -> view.tampilkanDaftar(model.getSemuaKendaraan()));
    }

    private void pilihBarisTabel() {
        String id = view.getIdTerpilih();
        if (id == null) {
            return;
        }
        jalankanAksi(() -> {
            KendaraanTambang k = model.cari(id);
            if (k != null) {
                view.isiForm(k);
            }
        });
    }

    private void tambahData() {
        jalankanAksi(() -> {
            int jenis = view.getJenisTerpilih();
            String id = InputValidator.bacaStringTidakKosong(view.getInputId(), "ID Kendaraan");
            if (model.cari(id) != null) {
                view.tampilkanPeringatan("Gagal: ID " + id + " sudah dipakai.");
                return;
            }
            String nama = InputValidator.bacaStringTidakKosong(view.getInputNama(), "Nama Kendaraan");
            double harga = InputValidator.bacaDoublePositif(view.getInputHarga(), "Harga sewa/hari");

            KendaraanTambang kendaraan; // tipe induk, objek anak (polymorphism)
            if (jenis == KendaraanView.JENIS_DUMP_TRUCK) {
                double kapasitas = InputValidator.bacaDoublePositif(view.getInputKapasitas(), "Kapasitas (ton)");
                kendaraan = new DumpTruck(id, nama, harga, kapasitas);
            } else {
                double kapasitas = InputValidator.bacaDoublePositif(view.getInputKapasitas(), "Kapasitas bucket (m3)");
                kendaraan = new Excavator(id, nama, harga, kapasitas);
            }

            if (model.tambahKendaraan(kendaraan)) {
                view.tampilkanInfo("Data berhasil ditambahkan.");
                view.bersihkanForm();
                view.tampilkanDaftar(model.getSemuaKendaraan());
            } else {
                view.tampilkanPeringatan("Gagal: ID " + id + " sudah dipakai.");
            }
        });
    }

    private void updateData() {
        jalankanAksi(() -> {
            if (model.getJumlahData() == 0) {
                view.tampilkanPeringatan("Belum ada data kendaraan.");
                return;
            }
            String id = view.getIdTerpilih();
            if (id == null) {
                view.tampilkanPeringatan("Pilih data pada tabel yang akan diupdate.");
                return;
            }
            String namaBaru = InputValidator.bacaStringTidakKosong(view.getInputNama(), "Nama Kendaraan");
            double hargaBaru = InputValidator.bacaDoublePositif(view.getInputHarga(), "Harga sewa/hari");
            double kapasitasBaru = InputValidator.bacaDoublePositif(view.getInputKapasitas(), "Kapasitas");

            if (model.updateKendaraan(id, namaBaru, hargaBaru, kapasitasBaru)) {
                view.tampilkanInfo("Data " + id + " berhasil diupdate.");
                view.bersihkanForm();
                view.tampilkanDaftar(model.getSemuaKendaraan());
            } else {
                view.tampilkanPeringatan("Data dengan ID " + id + " tidak ditemukan.");
            }
        });
    }

    private void hapusData() {
        jalankanAksi(() -> {
            String id = view.getIdTerpilih();
            if (id == null) {
                view.tampilkanPeringatan("Pilih data pada tabel yang akan dihapus.");
                return;
            }
            if (!view.konfirmasi("Yakin ingin menghapus kendaraan dengan ID " + id + "?")) {
                return;
            }
            if (model.hapusKendaraan(id)) {
                view.tampilkanInfo("Data berhasil dihapus.");
                view.bersihkanForm();
                view.tampilkanDaftar(model.getSemuaKendaraan());
            } else {
                view.tampilkanPeringatan("Data dengan ID " + id + " tidak ditemukan.");
            }
        });
    }

    private void hitungBiayaSewa() {
        jalankanAksi(() -> {
            String id = view.getIdTerpilih();
            if (id == null) {
                view.tampilkanPeringatan("Pilih kendaraan pada tabel terlebih dahulu.");
                return;
            }
            KendaraanTambang k = model.cari(id); // overloading cari(String)
            if (k == null) {
                view.tampilkanPeringatan("Data dengan ID tersebut tidak ditemukan.");
                return;
            }
            int hari = InputValidator.bacaIntRentang(view.getInputHari(), "Lama sewa", 1, 365);
            int diskon = InputValidator.bacaIntRentang(view.getInputDiskon(), "Diskon", 0, 100);

            double total;
            if (diskon == 0) {
                total = k.hitungBiayaSewa(hari);          // overriding per jenis
            } else {
                total = k.hitungBiayaSewa(hari, diskon);  // overloading
            }
            view.tampilkanHasilBiaya(total);
            view.setStatus(String.format("%s (%s) disewa %d hari, diskon %d%% -> Rp %,.2f",
                    k.getNamaKendaraan(), k.getJenis(), hari, diskon, total));
        });
    }

    private void cariByHarga() {
        jalankanAksi(() -> {
            double min = InputValidator.bacaDoublePositif(view.getInputHargaMin(), "Harga minimum");
            double max = InputValidator.bacaDoublePositif(view.getInputHargaMax(), "Harga maksimum");
            if (min > max) {
                view.tampilkanPeringatan("Harga minimum tidak boleh lebih besar dari maksimum.");
                return;
            }
            List<KendaraanTambang> hasil = model.cari(min, max); // overloading cari(double, double)
            view.tampilkanDaftar(hasil);
            if (hasil.isEmpty()) {
                view.tampilkanInfo("Tidak ada kendaraan pada rentang harga tersebut.");
            }
        });
    }
}
