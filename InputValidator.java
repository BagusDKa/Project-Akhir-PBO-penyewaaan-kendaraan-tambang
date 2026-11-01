package util;

import java.util.Locale;
import java.util.Scanner;

/**
 * Validasi input dari komponen GUI menggunakan Scanner.
 * Teks dari JTextField dibaca dengan Scanner lalu dicek
 * hasNextInt()/hasNextDouble() sebelum dikonversi.
 * Jika tidak valid -> IllegalArgumentException berisi pesan untuk pengguna.
 */
public final class InputValidator {

    private InputValidator() {
    }

    public static String bacaStringTidakKosong(String teks, String namaField) {
        if (teks == null || teks.trim().isEmpty()) {
            throw new IllegalArgumentException(namaField + " tidak boleh kosong.");
        }
        return teks.trim();
    }

    public static double bacaDoublePositif(String teks, String namaField) {
        String isi = bacaStringTidakKosong(teks, namaField);
        try (Scanner scanner = new Scanner(isi)) {
            scanner.useLocale(Locale.US); // titik sebagai pemisah desimal, contoh 0.9
            if (!scanner.hasNextDouble()) {
                throw new IllegalArgumentException(namaField + " harus berupa angka.");
            }
            double nilai = scanner.nextDouble();
            if (scanner.hasNext()) {
                throw new IllegalArgumentException(namaField + " hanya boleh berisi satu angka.");
            }
            if (nilai <= 0) {
                throw new IllegalArgumentException(namaField + " harus lebih dari 0.");
            }
            return nilai;
        }
    }

    public static int bacaIntRentang(String teks, String namaField, int min, int max) {
        String isi = bacaStringTidakKosong(teks, namaField);
        try (Scanner scanner = new Scanner(isi)) {
            if (!scanner.hasNextInt()) {
                throw new IllegalArgumentException(namaField + " harus berupa angka bulat.");
            }
            int nilai = scanner.nextInt();
            if (scanner.hasNext()) {
                throw new IllegalArgumentException(namaField + " hanya boleh berisi satu angka.");
            }
            if (nilai < min || nilai > max) {
                throw new IllegalArgumentException(
                        namaField + " harus antara " + min + " sampai " + max + ".");
            }
            return nilai;
        }
    }
}
