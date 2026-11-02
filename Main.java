package main;

import controller.KendaraanController;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import model.PengelolaKendaraan;
import view.KendaraanView;

public class Main {

    public static void main(String[] args) {
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

        SwingUtilities.invokeLater(() -> {
            PengelolaKendaraan model = new PengelolaKendaraan();
            KendaraanView view = new KendaraanView();
            KendaraanController controller = new KendaraanController(model, view);
            controller.jalankan();
        });
    }
}
