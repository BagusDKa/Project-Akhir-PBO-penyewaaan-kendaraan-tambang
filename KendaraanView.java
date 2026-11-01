package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionListener;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import model.KendaraanTambang;

/**
 * VIEW: hanya mengurus tampilan (Java Swing).
 * Tidak ada logika bisnis / akses database di sini; semua aksi
 * diteruskan ke Controller lewat listener.
 */
public class KendaraanView extends JFrame {

    public static final int JENIS_DUMP_TRUCK = 0;
    public static final int JENIS_EXCAVATOR = 1;

    // ===== Sumber data =====
    private final JComboBox<String> cbSumberData =
            new JComboBox<>(new String[]{"Hibernate ORM", "JDBC (PreparedStatement)"});

    // ===== Form data kendaraan =====
    private final JComboBox<String> cbJenis = new JComboBox<>(new String[]{"Dump Truck", "Excavator"});
    private final JTextField txtId = new JTextField(16);
    private final JTextField txtNama = new JTextField(16);
    private final JTextField txtHarga = new JTextField(16);
    private final JTextField txtKapasitas = new JTextField(16);
    private final JLabel lblKapasitas = new JLabel("Kapasitas (ton)");

    private final JButton btnTambah = new JButton("Tambah");
    private final JButton btnUpdate = new JButton("Update");
    private final JButton btnHapus = new JButton("Hapus");
    private final JButton btnBersihkan = new JButton("Bersihkan");

    // ===== Tabel =====
    private final DefaultTableModel tableModel = new DefaultTableModel(
            new Object[]{"ID", "Jenis", "Nama Kendaraan", "Harga Sewa / Hari", "Detail"}, 0) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };
    private final JTable tabel = new JTable(tableModel);

    // ===== Hitung biaya sewa =====
    private final JTextField txtHari = new JTextField(8);
    private final JTextField txtDiskon = new JTextField("0", 8);
    private final JButton btnHitung = new JButton("Hitung Biaya");
    private final JLabel lblHasilBiaya = new JLabel("Total: -");

    // ===== Cari berdasarkan harga =====
    private final JTextField txtHargaMin = new JTextField(10);
    private final JTextField txtHargaMax = new JTextField(10);
    private final JButton btnCari = new JButton("Cari");
    private final JButton btnTampilSemua = new JButton("Tampilkan Semua");

    private final JLabel lblStatus = new JLabel(" ");

    public KendaraanView() {
        super("Sistem Penyewaan Kendaraan Tambang");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel root = new JPanel(new BorderLayout(10, 10));
        root.setBorder(new EmptyBorder(10, 10, 10, 10));
        root.add(buatPanelAtas(), BorderLayout.NORTH);
        root.add(buatPanelForm(), BorderLayout.WEST);
        root.add(buatPanelTabel(), BorderLayout.CENTER);
        root.add(buatPanelBawah(), BorderLayout.SOUTH);
        setContentPane(root);

        // Perilaku murni tampilan: label kapasitas menyesuaikan jenis
        cbJenis.addActionListener(e -> lblKapasitas.setText(
                cbJenis.getSelectedIndex() == JENIS_DUMP_TRUCK
                        ? "Kapasitas (ton)" : "Kapasitas bucket (m3)"));

        pack();
        setMinimumSize(new Dimension(1050, 620));
        setLocationRelativeTo(null);
    }

    // ================== Pembuatan panel ==================

    private JPanel buatPanelAtas() {
        JPanel panel = new JPanel(new BorderLayout());
        JLabel judul = new JLabel("SISTEM PENYEWAAN KENDARAAN TAMBANG");
        judul.setFont(judul.getFont().deriveFont(Font.BOLD, 20f));
        panel.add(judul, BorderLayout.WEST);

        JPanel kanan = new JPanel();
        kanan.add(new JLabel("Sumber Data:"));
        kanan.add(cbSumberData);
        panel.add(kanan, BorderLayout.EAST);
        return panel;
    }

    private JPanel buatPanelForm() {
        JPanel form = new JPanel(new GridBagLayout());
        form.setBorder(BorderFactory.createTitledBorder("Data Kendaraan"));

        int baris = 0;
        tambahBaris(form, baris++, new JLabel("Jenis"), cbJenis);
        tambahBaris(form, baris++, new JLabel("ID Kendaraan"), txtId);
        tambahBaris(form, baris++, new JLabel("Nama Kendaraan"), txtNama);
        tambahBaris(form, baris++, new JLabel("Harga Sewa / Hari (Rp)"), txtHarga);
        tambahBaris(form, baris++, lblKapasitas, txtKapasitas);

        JPanel tombol = new JPanel(new GridLayout(2, 2, 6, 6));
        tombol.add(btnTambah);
        tombol.add(btnUpdate);
        tombol.add(btnHapus);
        tombol.add(btnBersihkan);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = baris++;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(12, 4, 4, 4);
        form.add(tombol, gbc);

        JLabel petunjuk = new JLabel("<html><i>Klik baris tabel untuk memilih data<br>"
                + "(Update / Hapus / Hitung Biaya).</i></html>");
        gbc.gridy = baris;
        gbc.insets = new Insets(8, 4, 4, 4);
        form.add(petunjuk, gbc);

        // dorong isi ke atas
        GridBagConstraints pengisi = new GridBagConstraints();
        pengisi.gridy = baris + 1;
        pengisi.weighty = 1;
        form.add(new JPanel(), pengisi);
        return form;
    }

    private JComponent buatPanelTabel() {
        tabel.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabel.setRowHeight(24);
        tabel.getTableHeader().setReorderingAllowed(false);
        tabel.getColumnModel().getColumn(0).setPreferredWidth(70);
        tabel.getColumnModel().getColumn(1).setPreferredWidth(90);
        tabel.getColumnModel().getColumn(2).setPreferredWidth(170);
        tabel.getColumnModel().getColumn(3).setPreferredWidth(150);
        tabel.getColumnModel().getColumn(4).setPreferredWidth(140);

        JScrollPane scroll = new JScrollPane(tabel);
        scroll.setBorder(BorderFactory.createTitledBorder("Daftar Kendaraan"));
        return scroll;
    }

    private JPanel buatPanelBawah() {
        JPanel hitung = new JPanel(new GridBagLayout());
        hitung.setBorder(BorderFactory.createTitledBorder("Hitung Biaya Sewa (kendaraan terpilih)"));
        tambahBaris(hitung, 0, new JLabel("Lama sewa (1-365 hari)"), txtHari);
        tambahBaris(hitung, 1, new JLabel("Diskon % (0-100)"), txtDiskon);
        lblHasilBiaya.setFont(lblHasilBiaya.getFont().deriveFont(Font.BOLD, 14f));
        tambahBaris(hitung, 2, btnHitung, lblHasilBiaya);

        JPanel cari = new JPanel(new GridBagLayout());
        cari.setBorder(BorderFactory.createTitledBorder("Cari Kendaraan (Rentang Harga)"));
        tambahBaris(cari, 0, new JLabel("Harga minimum (Rp)"), txtHargaMin);
        tambahBaris(cari, 1, new JLabel("Harga maksimum (Rp)"), txtHargaMax);
        tambahBaris(cari, 2, btnCari, btnTampilSemua);

        JPanel tengah = new JPanel(new GridLayout(1, 2, 10, 0));
        tengah.add(hitung);
        tengah.add(cari);

        lblStatus.setForeground(new Color(70, 70, 70));
        lblStatus.setBorder(new EmptyBorder(6, 2, 0, 2));

        JPanel panel = new JPanel(new BorderLayout());
        panel.add(tengah, BorderLayout.CENTER);
        panel.add(lblStatus, BorderLayout.SOUTH);
        return panel;
    }

    private void tambahBaris(JPanel panel, int baris, JComponent kiri, JComponent kanan) {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 4, 4, 4);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.gridy = baris;

        gbc.gridx = 0;
        gbc.fill = GridBagConstraints.NONE;
        panel.add(kiri, gbc);

        gbc.gridx = 1;
        gbc.weightx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(kanan, gbc);
    }

    // ================== Menampilkan data ==================

    public void tampilkanDaftar(List<KendaraanTambang> daftar) {
        tableModel.setRowCount(0);
        for (KendaraanTambang k : daftar) {          // perulangan
            tableModel.addRow(new Object[]{
                k.getId(),
                k.getJenis(),                         // polymorphism
                k.getNamaKendaraan(),
                String.format("Rp %,.2f", k.getHargaSewaPerHari()),
                k.getDetail()                         // polymorphism
            });
        }
        setStatus("Menampilkan " + daftar.size() + " data kendaraan.");
    }

    public void isiForm(KendaraanTambang k) {
        cbJenis.setSelectedIndex("Dump Truck".equals(k.getJenis()) ? JENIS_DUMP_TRUCK : JENIS_EXCAVATOR);
        txtId.setText(k.getId());
        txtNama.setText(k.getNamaKendaraan());
        txtHarga.setText(String.format(java.util.Locale.US, "%.0f", k.getHargaSewaPerHari()));
        txtKapasitas.setText(String.valueOf(k.getKapasitas()));
        // ID dan jenis tidak boleh diubah saat update
        txtId.setEditable(false);
        cbJenis.setEnabled(false);
    }

    public void bersihkanForm() {
        cbJenis.setSelectedIndex(JENIS_DUMP_TRUCK);
        cbJenis.setEnabled(true);
        txtId.setEditable(true);
        txtId.setText("");
        txtNama.setText("");
        txtHarga.setText("");
        txtKapasitas.setText("");
        tabel.clearSelection();
        lblHasilBiaya.setText("Total: -");
        txtId.requestFocus();
    }

    public void tampilkanHasilBiaya(double total) {
        lblHasilBiaya.setText(String.format("Total: Rp %,.2f", total));
    }

    public void setStatus(String pesan) {
        lblStatus.setText(pesan);
    }

    public void tampilkanInfo(String pesan) {
        JOptionPane.showMessageDialog(this, pesan, "Informasi", JOptionPane.INFORMATION_MESSAGE);
    }

    public void tampilkanPeringatan(String pesan) {
        JOptionPane.showMessageDialog(this, pesan, "Input Tidak Valid", JOptionPane.WARNING_MESSAGE);
    }

    public void tampilkanError(String pesan) {
        JOptionPane.showMessageDialog(this, pesan, "Kesalahan", JOptionPane.ERROR_MESSAGE);
    }

    public boolean konfirmasi(String pesan) {
        int jawab = JOptionPane.showConfirmDialog(this, pesan, "Konfirmasi",
                JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        return jawab == JOptionPane.YES_OPTION;
    }

    // ================== Getter input (dibaca Controller) ==================

    public int getJenisTerpilih() {
        return cbJenis.getSelectedIndex();
    }

    public int getSumberDataTerpilih() {
        return cbSumberData.getSelectedIndex();
    }

    public String getInputId() {
        return txtId.getText();
    }

    public String getInputNama() {
        return txtNama.getText();
    }

    public String getInputHarga() {
        return txtHarga.getText();
    }

    public String getInputKapasitas() {
        return txtKapasitas.getText();
    }

    public String getInputHari() {
        return txtHari.getText();
    }

    public String getInputDiskon() {
        return txtDiskon.getText();
    }

    public String getInputHargaMin() {
        return txtHargaMin.getText();
    }

    public String getInputHargaMax() {
        return txtHargaMax.getText();
    }

    /** ID pada baris tabel yang dipilih, atau null jika belum ada yang dipilih. */
    public String getIdTerpilih() {
        int baris = tabel.getSelectedRow();
        if (baris < 0) {
            return null;
        }
        return (String) tableModel.getValueAt(baris, 0);
    }

    // ================== Pendaftaran listener (dipasang Controller) ==================

    public void addTambahListener(ActionListener l) {
        btnTambah.addActionListener(l);
    }

    public void addUpdateListener(ActionListener l) {
        btnUpdate.addActionListener(l);
    }

    public void addHapusListener(ActionListener l) {
        btnHapus.addActionListener(l);
    }

    public void addBersihkanListener(ActionListener l) {
        btnBersihkan.addActionListener(l);
    }

    public void addHitungListener(ActionListener l) {
        btnHitung.addActionListener(l);
    }

    public void addCariListener(ActionListener l) {
        btnCari.addActionListener(l);
    }

    public void addTampilSemuaListener(ActionListener l) {
        btnTampilSemua.addActionListener(l);
    }

    public void addSumberDataListener(ActionListener l) {
        cbSumberData.addActionListener(l);
    }

    public void addTabelSelectionListener(ListSelectionListener l) {
        tabel.getSelectionModel().addListSelectionListener(l);
    }
}
