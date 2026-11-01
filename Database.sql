-- =====================================================
-- Database Sistem Penyewaan Kendaraan Tambang
-- Jalankan file ini di phpMyAdmin / MySQL Workbench / CLI
-- =====================================================
CREATE DATABASE IF NOT EXISTS db_kendaraan_tambang;
USE db_kendaraan_tambang;

CREATE TABLE IF NOT EXISTS kendaraan (
    id                  VARCHAR(20)  NOT NULL PRIMARY KEY,
    jenis               VARCHAR(20)  NOT NULL,   -- 'Dump Truck' / 'Excavator' (discriminator ORM)
    nama_kendaraan      VARCHAR(100) NOT NULL,
    harga_sewa_per_hari DOUBLE       NOT NULL,
    kapasitas           DOUBLE       NOT NULL    -- ton (Dump Truck) / m3 bucket (Excavator)
);

-- Data dummy awal
INSERT IGNORE INTO kendaraan (id, jenis, nama_kendaraan, harga_sewa_per_hari, kapasitas) VALUES
('DT001', 'Dump Truck', 'Hino FM 260 JD',    2500000, 24),
('DT002', 'Dump Truck', 'Scania P360',       3200000, 30),
('EX001', 'Excavator',  'Komatsu PC200',     4000000, 0.9),
('EX002', 'Excavator',  'Caterpillar 320D',  4500000, 1.2);
