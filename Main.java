package main;

import controller.KendaraanController;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import model.PengelolaKendaraan;
import view.KendaraanView;

public class Main {

    public static void main(String[] args) {
        // Perulangan + percabangan: cari tema Nimbus agar tampilan lebih rapi
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception e) {
            // jika gagal, pakai tema bawaan
        }

        // Semua komponen Swing dibuat di Event Dispatch Thread
        SwingUtilities.invokeLater(() -> {
            PengelolaKendaraan model = new PengelolaKendaraan();
            KendaraanView view = new KendaraanView();
            KendaraanController controller = new KendaraanController(model, view);
            controller.jalankan();
        });
    }
}
