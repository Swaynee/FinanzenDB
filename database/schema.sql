CREATE DATABASE IF NOT EXISTS finanzen CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE finanzen;

-- KONTO
-- =========================================================
CREATE TABLE Konto 
(
    konto_id INT UNSIGNED NOT NULL AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    konto_art VARCHAR(30) NOT NULL,
    aktiv BOOLEAN NOT NULL DEFAULT TRUE,
    iban VARCHAR(50),
    bic VARCHAR(50),
    bank VARCHAR(50),
    PRIMARY KEY (konto_id)
);

-- BUDGET
-- =========================================================
CREATE TABLE Budget 
(
    budget_id INT UNSIGNED NOT NULL AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    aktiv BOOLEAN NOT NULL DEFAULT TRUE,
    PRIMARY KEY (budget_id)
);

-- TRANSAKTIONSART
-- =========================================================
CREATE TABLE Transaktionsart 
(
    transaktionsart_id INT UNSIGNED NOT NULL AUTO_INCREMENT,
    name VARCHAR(50) NOT NULL,
    PRIMARY KEY (transaktionsart_id),
    UNIQUE (name)
);

-- TRANSAKTION
-- =========================================================
CREATE TABLE Transaktion 
(
    transaktion_id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    transaktionsart INT UNSIGNED NOT NULL,
    datum DATE NOT NULL,
    planungsmonat DATE NOT NULL,
    beschreibung VARCHAR(255) NOT NULL,
    PRIMARY KEY (transaktion_id),
    CONSTRAINT fk_transaktionsart FOREIGN KEY (transaktionsart) REFERENCES Transaktionsart (transaktionsart_id)
);

-- BUCHUNG
-- Tatsächliche Geldbewegung auf einem Konto
-- =========================================================
CREATE TABLE Buchung 
(
    buchung_id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    transaktion_id BIGINT UNSIGNED NOT NULL,
    konto_id INT UNSIGNED NOT NULL,
    betrag DECIMAL(12, 2) NOT NULL,
    PRIMARY KEY (buchung_id),
    CONSTRAINT fk_buchung_transaktion FOREIGN KEY (transaktion_id) REFERENCES Transaktion (transaktion_id) ON DELETE CASCADE,
    CONSTRAINT fk_buchung_konto FOREIGN KEY (konto_id) REFERENCES Konto (konto_id)
);

-- BUDGET_BUCHUNG
-- Bewegung innerhalb eines Budgets
-- =========================================================
CREATE TABLE Budget_Buchung 
(
    budget_buchung_id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    transaktion_id BIGINT UNSIGNED NOT NULL,
    budget_id INT UNSIGNED NOT NULL,
    betrag DECIMAL(12, 2) NOT NULL,
    PRIMARY KEY (budget_buchung_id),
    CONSTRAINT fk_budget_buchung_transaktion FOREIGN KEY (transaktion_id) REFERENCES Transaktion (transaktion_id) ON DELETE CASCADE,
    CONSTRAINT fk_budget_buchung_budget FOREIGN KEY (budget_id) REFERENCES Budget (budget_id)
);

-- NOTIZ
-- =========================================================
CREATE TABLE Notiz 
(
    notiz_id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    text TEXT NOT NULL,
    betrag DECIMAL(12, 2),
    faellig VARCHAR(50),
    geloescht BOOLEAN NOT NULL DEFAULT TRUE,
    PRIMARY KEY (notiz_id)
);

-- WIEDERKEHRENDE TRANSAKTION
-- Vorlage für die automatische Erzeugung von Transaktionen
-- =========================================================
CREATE TABLE Transaktion_Wiederkehrend 
(
    transaktion_wiederkehrend_id INT UNSIGNED NOT NULL AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    beschreibung VARCHAR(255) NULL,
    betrag DECIMAL(12, 2) NOT NULL,
    zahlungstag TINYINT UNSIGNED NOT NULL,
    transaktionsart_id INT UNSIGNED NOT NULL,
    gueltig_ab DATE NOT NULL,
    gueltig_bis DATE NULL,
    aktiv BOOLEAN NOT NULL DEFAULT TRUE,
    PRIMARY KEY (transaktion_wiederkehrend_id),
    CONSTRAINT fk_wiederkehrend_art FOREIGN KEY (transaktionsart_id) REFERENCES Transaktionsart (transaktionsart_id),
    CONSTRAINT chk_zahlungstag CHECK (zahlungstag BETWEEN 1 AND 31)
);