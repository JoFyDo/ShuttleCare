package com.rocketshipcheckingtool.test;

import org.junit.rules.ExternalResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * JUnit rule that sets up a test database for integration tests.
 * Creates all necessary tables matching the production schema and populates them with test data.
 */
public class TestDatabaseSetup extends ExternalResource {
    private static final Logger logger = LoggerFactory.getLogger(TestDatabaseSetup.class);
    private final String dbPath;
    protected Connection connection;

    /**
     * Constructs a new TestDatabaseSetup with the specified database path.
     *
     * @param dbPath The file path for the test database.
     */
    public TestDatabaseSetup(String dbPath) {
        this.dbPath = dbPath;
        logger.info("TestDatabaseSetup initialized with database path: {}", dbPath);
    }

    @Override
    protected void before() throws Exception {
        // Delete existing database file if it exists
        File dbFile = new File(dbPath);
        if (dbFile.exists()) {
            if (dbFile.delete()) {
                logger.info("Deleted existing test database file: {}", dbPath);
            } else {
                logger.warn("Failed to delete existing test database file: {}", dbPath);
            }
        }

        // Create new database
        String jdbcUrl = "jdbc:sqlite:" + dbPath;
        connection = DriverManager.getConnection(jdbcUrl);
        // Enable WAL mode for better concurrency
        try (Statement s = connection.createStatement()) {
            s.execute("PRAGMA journal_mode=WAL;");
        }
        logger.info("Connected to test database at: {}", jdbcUrl);

        // Create schema and load test data
        createDatabase();
        loadTestData();
    }

    @Override
    protected void after() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                logger.info("Test database connection closed");
            }
        } catch (Exception e) {
            logger.error("Error closing test database connection", e);
        }
    }

    /**
     * Create the database schema for testing.
     * Creates all tables with the same structure as the production database.
     *
     * @throws Exception if a database error occurs
     */
    private void createDatabase() throws Exception {
        try (Statement statement = connection.createStatement()) {
            // Create Shuttle table
            statement.execute("""
                CREATE TABLE IF NOT EXISTS Shuttles (
                               ID                   INTEGER not null
                   primary key,
               Name                 TEXT     default '',
               Status               TEXT     default '',
               Landing              DATETIME default 0,
               Mechanic             INTEGER
                   constraint Mechanic
                       references Mechanics,
               PredictedReleaseTime DATETIME
                );
            """);

            // Create Mechanic table
            statement.execute("""
                CREATE TABLE IF NOT EXISTS Mechanics (
                            ID   integer not null
            constraint Mechanic_pk
                primary key autoincrement,
                            Name TEXT
                );
            """);

            // Create Task table
            statement.execute("""
                CREATE TABLE IF NOT EXISTS Tasks (
                               Task       TEXT    not null,
                               Status     TEXT,
                               ShuttleID  INTEGER not null,
                               Mechanic   INTEGER
                                   constraint Mechanic
                                       references Mechanics,
                               Active     TEXT,
                               ID         INTEGER not null
                                   primary key autoincrement,
                               TimeNeeded integer
                               );
            """);

            // Create Part table
            statement.execute("""
                CREATE TABLE IF NOT EXISTS Parts (
ID       integer
        constraint Parts_pk
            primary key autoincrement,
    Name     TEXT,
    Price    INTEGER,
    Quantity integer
                );
            """);

            // Create Notification table
            statement.execute("""
                CREATE TABLE IF NOT EXISTS Notifications (
ID           integer not null
        constraint ID
            primary key autoincrement,
    Notification TEXT,
    Active       TEXT,
    ShuttleID    integer
        constraint ShuttleID
            references Shuttles,
    Sender       TEXT,
    Comment      TEXT
                );
            """);

            // Create GeneralTask table
            statement.execute("""
                CREATE TABLE IF NOT EXISTS GeneralTasks (
    ID         INTEGER not null
        primary key autoincrement,
    Task       TEXT,
    Status     TEXT,
    ShuttleID  INTEGER not null
        constraint ShuttleID
            references Shuttles,
    TimeNeeded integer
                );
            """);

            // Create QuestionnaireRating table
            statement.execute("""
                CREATE TABLE IF NOT EXISTS QuestionnaireRatings (
ID        integer not null
        constraint QuestionnaireRating_pk
            primary key autoincrement,
    Topic     TEXT,
    Active    TEXT,
    Rating    integer,
    ShuttleID integer
        constraint ShuttleID
            references Shuttles
                );
            """);

            // Create Comment table
            statement.execute("""
                CREATE TABLE IF NOT EXISTS Comments (
    ID        integer not null
        constraint ID
            primary key autoincrement,
    Comment   TEXT,
    ShuttleID integer
        constraint ShuttleID
            references Shuttles,
    Active    TEXT
                );
            """);

            logger.info("Created database schema for testing");
        }
    }

    /**
     * Loads test data into the database.
     *
     * @throws Exception if a database error occurs
     */
    private void loadTestData() throws Exception {
        try (Statement statement = connection.createStatement()) {
            // Insert test mechanics first (since Shuttles/Tasks reference them)
            statement.executeUpdate("INSERT INTO Mechanics (ID, Name) VALUES (0, 'Andi');");
            statement.executeUpdate("INSERT INTO Mechanics (ID, Name) VALUES (1, 'Günther');");
            statement.executeUpdate("INSERT INTO Mechanics (ID, Name) VALUES (2, 'Lisa');");
            statement.executeUpdate("INSERT INTO Mechanics (ID, Name) VALUES (3, 'Lothar');");
            statement.executeUpdate("INSERT INTO Mechanics (ID, Name) VALUES (4, 'Sandra');");
            statement.executeUpdate("INSERT INTO Mechanics (ID, Name) VALUES (5, 'Hans');");
            statement.executeUpdate("INSERT INTO Mechanics (ID, Name) VALUES (6, 'Alois');");
            statement.executeUpdate("INSERT INTO Mechanics (ID, Name) VALUES (7, 'Boris');");
            statement.executeUpdate("INSERT INTO Mechanics (ID, Name) VALUES (8, 'Christian');");
            statement.executeUpdate("INSERT INTO Mechanics (ID, Name) VALUES (9, 'Deniz');");

            // Insert test shuttles
            statement.executeUpdate("INSERT INTO Shuttles (ID, Name, Status, Landing, Mechanic, PredictedReleaseTime) VALUES (0, 'Orion Drift-Shuttle', 'Flug', '2025-04-21 13:13:13', 0, null);");
            statement.executeUpdate("INSERT INTO Shuttles (ID, Name, Status, Landing, Mechanic, PredictedReleaseTime) VALUES (1, 'Skyframe Delta-Shuttle', 'Gelandet', '2025-05-07 10:10:10', 1, '2025-05-08 02:10:10.0');");
            statement.executeUpdate("INSERT INTO Shuttles (ID, Name, Status, Landing, Mechanic, PredictedReleaseTime) VALUES (2, 'Dreamline-Shuttle', 'Inspektion 1', '2025-04-21 13:13:13', 2, '2025-04-22 05:13:13.0');");
            statement.executeUpdate("INSERT INTO Shuttles (ID, Name, Status, Landing, Mechanic, PredictedReleaseTime) VALUES (3, 'Stellar Voyager-Shuttle', 'In Wartung', '2025-04-21 13:13:13', 3, '2025-04-22 05:13:13.0');");
            statement.executeUpdate("INSERT INTO Shuttles (ID, Name, Status, Landing, Mechanic, PredictedReleaseTime) VALUES (4, 'Pioneer Nova', 'Freigegeben', '0', 6, null);");

            // Insert test tasks
            statement.executeUpdate("INSERT INTO Tasks (Task, Status, ShuttleID, Mechanic, Active, ID, TimeNeeded) VALUES ('Bildschirme ersetzen', 'false', 1, 0, 'true', 54, 3);");
            statement.executeUpdate("INSERT INTO Tasks (Task, Status, ShuttleID, Mechanic, Active, ID, TimeNeeded) VALUES ('Sauerstofftanks ersetzen', 'true', 2, 0, 'true', 55, 6);");
            statement.executeUpdate("INSERT INTO Tasks (Task, Status, ShuttleID, Mechanic, Active, ID, TimeNeeded) VALUES ('Sensoren kalibrieren', 'true', 2, 5, 'true', 56, 4);");
            statement.executeUpdate("INSERT INTO Tasks (Task, Status, ShuttleID, Mechanic, Active, ID, TimeNeeded) VALUES ('Nicht nachvollziehbare Fehlermeldungen in Diagnosesystemen: Updates einspielen', 'true', 3, 9, 'true', 57, 1);");
            statement.executeUpdate("INSERT INTO Tasks (Task, Status, ShuttleID, Mechanic, Active, ID, TimeNeeded) VALUES ('Rauchmelder ersetzten', 'true', 3, 1, 'true', 58, 5);");
            statement.executeUpdate("INSERT INTO Tasks (Task, Status, ShuttleID, Mechanic, Active, ID, TimeNeeded) VALUES ('Störung der Temperaturregullierung: Innentemperatur war leicht zu hoch', 'true', 2, 1, 'true', 59, 1);");

            // Insert test parts (use batch for performance and avoid statement length issues)
            String[] partsInserts = {
                "INSERT INTO Parts (ID, Name, Price, Quantity) VALUES (1, 'Hochleistungs-Batterie (Li-ion, 500Wh)', 4500, 11);",
                "INSERT INTO Parts (ID, Name, Price, Quantity) VALUES (2, 'Raumanzug (space-grade, belüftet)', 25000, 6);",
                "INSERT INTO Parts (ID, Name, Price, Quantity) VALUES (3, 'Sauerstoffgenerator (120L/h)', 12500, 3);",
                "INSERT INTO Parts (ID, Name, Price, Quantity) VALUES (4, 'Navigationsgerät (3D-Raum, GPS, Laser)', 9800, 7);",
                "INSERT INTO Parts (ID, Name, Price, Quantity) VALUES (5, 'Hydraulisches Andocksystem (Standard, 10T)', 72000, 4);",
                "INSERT INTO Parts (ID, Name, Price, Quantity) VALUES (6, 'Frachtcontainer (raumgeprüft, 1.5x2m)', 25000, 8);",
                "INSERT INTO Parts (ID, Name, Price, Quantity) VALUES (7, 'Kommunikationsmodul (satellitenfähig, 1GB)', 3800, 15);",
                "INSERT INTO Parts (ID, Name, Price, Quantity) VALUES (8, 'Mikrogravitationstestmodul (10kg)', 6800, 12);",
                "INSERT INTO Parts (ID, Name, Price, Quantity) VALUES (9, 'Thermal-Wärmeabschirmung (Raumklasse)', 5200, 4);",
                "INSERT INTO Parts (ID, Name, Price, Quantity) VALUES (10, 'Wasserfilter (2.5L/min, Wiederverwendbar)', 150, 30);",
                "INSERT INTO Parts (ID, Name, Price, Quantity) VALUES (11, 'Plasmabeschichtungsanlage (Miniatur, tragbar)', 12000, 2);",
                "INSERT INTO Parts (ID, Name, Price, Quantity) VALUES (12, 'Quantenkompass (selbstkalibrierend)', 4500, 8);",
                "INSERT INTO Parts (ID, Name, Price, Quantity) VALUES (13, 'Mikroskopmodul (4-fach, hochauflösend)', 6500, 3);",
                "INSERT INTO Parts (ID, Name, Price, Quantity) VALUES (14, 'Luftdichtungs-Set (Komplett, für Kabine)', 2000, 5);",
                "INSERT INTO Parts (ID, Name, Price, Quantity) VALUES (15, 'Strukturelle Verstärkungseinheit (Titanium)', 15500, 6);",
                "INSERT INTO Parts (ID, Name, Price, Quantity) VALUES (16, 'Kühlmodul (Raumtemperaturregler)', 2200, 25);",
                "INSERT INTO Parts (ID, Name, Price, Quantity) VALUES (17, 'UV-Strahlenschutzfolie (Fenster, 1m²)', 170, 50);",
                "INSERT INTO Parts (ID, Name, Price, Quantity) VALUES (18, 'Verbindungsdüse (schnell, Schnellverschluss)', 1800, 20);",
                "INSERT INTO Parts (ID, Name, Price, Quantity) VALUES (19, 'Sensorpaket (Luftfeuchtigkeit, Temperatur, Druck)', 2400, 12);",
                "INSERT INTO Parts (ID, Name, Price, Quantity) VALUES (20, 'Automatisiertes Steuerungssystem (für Navigation)', 37000, 7);",
                "INSERT INTO Parts (ID, Name, Price, Quantity) VALUES (21, 'Rettungsboje (Wasserfest, 20kg)', 1500, 3);",
                "INSERT INTO Parts (ID, Name, Price, Quantity) VALUES (22, 'Bordcomputer (Redundant, mit künstlicher Intelligenz)', 120000, 2);",
                "INSERT INTO Parts (ID, Name, Price, Quantity) VALUES (23, 'Notfall-Sauerstoffkanister (10L)', 250, 45);",
                "INSERT INTO Parts (ID, Name, Price, Quantity) VALUES (24, 'Deckenventilator (geräuscharm, 50W)', 150, 100);",
                "INSERT INTO Parts (ID, Name, Price, Quantity) VALUES (25, 'Bord-Toilette (kompakt, Recyclingfähig)', 5500, 8);",
                "INSERT INTO Parts (ID, Name, Price, Quantity) VALUES (26, 'Luftentfeuchter (Miniatur, 100W)', 350, 60);",
                "INSERT INTO Parts (ID, Name, Price, Quantity) VALUES (27, 'Feuerlöscher (Chemisch, 2kg)', 450, 25);",
                "INSERT INTO Parts (ID, Name, Price, Quantity) VALUES (28, 'Kompakte Klimaanlage (Energieeffizient)', 7500, 10);",
                "INSERT INTO Parts (ID, Name, Price, Quantity) VALUES (29, 'Ladegerät (220V für Batterien)', 1000, 15);",
                "INSERT INTO Parts (ID, Name, Price, Quantity) VALUES (30, 'Luftdruckregler (automatisch, bis 50 bar)', 9500, 5);",
                "INSERT INTO Parts (ID, Name, Price, Quantity) VALUES (31, 'LED-Beleuchtungseinheit (dimmbar, weltraumtauglich)', 320, 80);",
                "INSERT INTO Parts (ID, Name, Price, Quantity) VALUES (32, 'Notstrom-Inverter (automatisch schaltend)', 5700, 12);",
                "INSERT INTO Parts (ID, Name, Price, Quantity) VALUES (33, 'Avionik-Hauptrechner (redundant, Raumklasse)', 78000, 6);",
                "INSERT INTO Parts (ID, Name, Price, Quantity) VALUES (34, 'Datenspeicher-Einheit (strahlensicher, SSD 1TB)', 3800, 15);",
                "INSERT INTO Parts (ID, Name, Price, Quantity) VALUES (35, 'Dockingring (automatischer Andockmechanismus)', 65000, 4);",
                "INSERT INTO Parts (ID, Name, Price, Quantity) VALUES (36, 'Sicherheitsgurtset (3-Punkt, mehrdimensional)', 280, 60);",
                "INSERT INTO Parts (ID, Name, Price, Quantity) VALUES (37, 'Strahlenschutz-Vorhang (flexibel, Bleifrei)', 2100, 25);",
                "INSERT INTO Parts (ID, Name, Price, Quantity) VALUES (38, 'Bordküche (kompakt, vakuumgeeignet)', 29000, 3);",
                "INSERT INTO Parts (ID, Name, Price, Quantity) VALUES (39, 'Touchscreen-Steuereinheit (Multi-Input, redundant)', 6900, 10);",
                "INSERT INTO Parts (ID, Name, Price, Quantity) VALUES (40, 'Magnetischer Werkzeughalter (wandmontierbar)', 150, 45);",
                "INSERT INTO Parts (ID, Name, Price, Quantity) VALUES (41, 'Inbusschrauben (Edelstahl, M5x16, Set à 100 Stk)', 40, 12);",
                "INSERT INTO Parts (ID, Name, Price, Quantity) VALUES (42, 'Selbstsichernde Muttern (M5, Set à 100)', 35, 8);",
                "INSERT INTO Parts (ID, Name, Price, Quantity) VALUES (43, 'Hochtemperatur-Dichtung (Silikonring Ø10cm)', 18, 25);",
                "INSERT INTO Parts (ID, Name, Price, Quantity) VALUES (44, 'Keramikkleber (raumfahrtzertifiziert, 50ml)', 95, 40);",
                "INSERT INTO Parts (ID, Name, Price, Quantity) VALUES (45, 'Schmiermittel (raumgleitfähig, 100ml Tube)', 22, 0);",
                "INSERT INTO Parts (ID, Name, Price, Quantity) VALUES (46, 'Mikrocontroller-Platine (STM-basiert, Space-grade)', 1100, 18);",
                "INSERT INTO Parts (ID, Name, Price, Quantity) VALUES (47, 'Kondensatorpackung (elektronisch, 100uF)', 85, 100);",
                "INSERT INTO Parts (ID, Name, Price, Quantity) VALUES (48, 'Wellenfilter (optisch, 5nm Bandbreite)', 145, 30);",
                "INSERT INTO Parts (ID, Name, Price, Quantity) VALUES (49, 'Silizium-Solarpanel (50W, raumgeeignet)', 300, 75);",
                "INSERT INTO Parts (ID, Name, Price, Quantity) VALUES (50, 'Luftgewehrmodul (sicherheitstauglich)', 200, 0);",
                "INSERT INTO Parts (ID, Name, Price, Quantity) VALUES (51, 'Trägerschiene (kompakt, 12cm)', 30, 60);",
                "INSERT INTO Parts (ID, Name, Price, Quantity) VALUES (52, 'Schnellverschluss-Konnektor (für Flüssigkeiten)', 210, 25);",
                "INSERT INTO Parts (ID, Name, Price, Quantity) VALUES (53, 'Frequenzmodulator (hochfrequent, bis 40GHz)', 5500, 10);",
                "INSERT INTO Parts (ID, Name, Price, Quantity) VALUES (54, 'Verkabelungssatz (geschirmt, 50m)', 80, 200);",
                "INSERT INTO Parts (ID, Name, Price, Quantity) VALUES (55, 'Verstärkermodul (RF, 10W)', 2200, 50);",
                "INSERT INTO Parts (ID, Name, Price, Quantity) VALUES (56, 'Lufterhitzer (kompakt, 100W)', 95, 110);",
                "INSERT INTO Parts (ID, Name, Price, Quantity) VALUES (57, 'Adapterkabel (T-Stück, 10cm)', 8, 150);",
                "INSERT INTO Parts (ID, Name, Price, Quantity) VALUES (58, 'Kompressionsdichtung (Vulkanisat, Ø5cm)', 15, 60);",
                "INSERT INTO Parts (ID, Name, Price, Quantity) VALUES (59, 'Schalldämpfungsmodul (10dB, raumklassifiziert)', 180, 45);",
                "INSERT INTO Parts (ID, Name, Price, Quantity) VALUES (60, 'Datenkabel (USB 3.0, 2m)', 18, 250);",
                "INSERT INTO Parts (ID, Name, Price, Quantity) VALUES (61, 'Sicherheitsventil (hydraulisch, 10 bar)', 65, 80);",
                "INSERT INTO Parts (ID, Name, Price, Quantity) VALUES (62, 'Wasserpumpe (kompakt, 10L/min)', 200, 70);",
                "INSERT INTO Parts (ID, Name, Price, Quantity) VALUES (63, 'Hochspannungsversorgung (30V, 10A)', 2500, 8);",
                "INSERT INTO Parts (ID, Name, Price, Quantity) VALUES (64, 'Elektromagnetische Abschirmung (20x20cm)', 300, 10);",
                "INSERT INTO Parts (ID, Name, Price, Quantity) VALUES (65, 'Dämpfungselement (vibrationsabsorbierend, klein)', 16, 90);",
                "INSERT INTO Parts (ID, Name, Price, Quantity) VALUES (66, 'Schnellkupplung (Druckleitung, 1/2 Zoll)', 22, 50);",
                "INSERT INTO Parts (ID, Name, Price, Quantity) VALUES (67, 'Ersatz-Lüftermodul (Luftzirkulationseinheit, Mini)', 180, 0);",
                "INSERT INTO Parts (ID, Name, Price, Quantity) VALUES (68, 'Hochpräzisions-Feuchtigkeitssensor (Raumfahrtzulassung)', 240, 15);",
                "INSERT INTO Parts (ID, Name, Price, Quantity) VALUES (69, 'SMD-Widerstandssortiment (100 Werte, je 10 Stk)', 35, 25);",
                "INSERT INTO Parts (ID, Name, Price, Quantity) VALUES (70, 'Hochleistungs-Thermalpad (Wärmeableitung, 10x10cm)', 65, 60);",
                "INSERT INTO Parts (ID, Name, Price, Quantity) VALUES (71, 'Laserschutzfilter (visuelle Anzeige, Polycarbonat)', 110, 0);",
                "INSERT INTO Parts (ID, Name, Price, Quantity) VALUES (72, 'LED-Anzeigeeinheit (Mehrfarben, steckbar)', 38, 100);",
                "INSERT INTO Parts (ID, Name, Price, Quantity) VALUES (73, 'EMV-Abschirmfolie (Kupfer, selbstklebend, 1m²)', 85, 20);",
                "INSERT INTO Parts (ID, Name, Price, Quantity) VALUES (74, 'Notfall-Nahrungsmodul (1-Person, 24h Ration)', 45, 0);",
                "INSERT INTO Parts (ID, Name, Price, Quantity) VALUES (75, 'Trägerschiene für Module (20 cm, Aluminium)', 19, 70);",
                "INSERT INTO Parts (ID, Name, Price, Quantity) VALUES (76, 'USB-Dateninterface (raumklassifiziert, Typ-C)', 65, 12);",
                "INSERT INTO Parts (ID, Name, Price, Quantity) VALUES (77, 'Diagnosestecker (mehrpolig, magnetisch, Raum-Norm)', 120, 40);",
                "INSERT INTO Parts (ID, Name, Price, Quantity) VALUES (78, 'Touchpanelrahmen (für Display 10\", Montagekit)', 48, 35);",
                "INSERT INTO Parts (ID, Name, Price, Quantity) VALUES (79, 'Kabelführungstunnel (schwenkbar, 50 cm)', 78, 18);",
                "INSERT INTO Parts (ID, Name, Price, Quantity) VALUES (80, 'Dreipunkt-Halterung für Nutzlastadapter', 220, 0);",
                "INSERT INTO Parts (ID, Name, Price, Quantity) VALUES (81, 'Akustikdämmmatte (selbsthaftend, 1m²)', 115, 10);",
                "INSERT INTO Parts (ID, Name, Price, Quantity) VALUES (82, 'Positionsleuchte (grün, blinkend, 12V DC)', 88, 60);",
                "INSERT INTO Parts (ID, Name, Price, Quantity) VALUES (83, 'Bajonettverschluss für Wartungsklappe (verriegelbar)', 42, 50);",
                "INSERT INTO Parts (ID, Name, Price, Quantity) VALUES (84, 'Adaptermodul USB ↔ CAN-Bus (weltraumtauglich)', 180, 8);",
                "INSERT INTO Parts (ID, Name, Price, Quantity) VALUES (85, 'Gaskartusche (Stickstoff, 500ml, kompakt)', 62, 0);",
                "INSERT INTO Parts (ID, Name, Price, Quantity) VALUES (86, 'Mikrofonmodul (für Innenkommunikation, abgeschirmt)', 76, 20);",
                "INSERT INTO Parts (ID, Name, Price, Quantity) VALUES (87, 'Lichtleiterkabel (5m, für interne Beleuchtung)', 55, 30);",
                "INSERT INTO Parts (ID, Name, Price, Quantity) VALUES (88, 'Ersatzlüftungsgitter (Edelstahl, 10x10 cm)', 28, 90);",
                "INSERT INTO Parts (ID, Name, Price, Quantity) VALUES (89, 'Reinigungstuch (staubfrei, vakuumverpackt, Set à 20)', 12, 110);",
                "INSERT INTO Parts (ID, Name, Price, Quantity) VALUES (90, 'Mini-Relais (DC 12V, raumfahrtqualifiziert)', 14, 150);",
                "INSERT INTO Parts (ID, Name, Price, Quantity) VALUES (91, 'Elastomerfeder (kompakt, hochbelastbar)', 32, 0);",
                "INSERT INTO Parts (ID, Name, Price, Quantity) VALUES (92, 'Schutzfolie für Bedienfeld (kratzfest, transparent)', 9, 200);",
                "INSERT INTO Parts (ID, Name, Price, Quantity) VALUES (93, 'Teleskophalter (verstellbar, 20–40cm)', 39, 40);",
                "INSERT INTO Parts (ID, Name, Price, Quantity) VALUES (94, 'Andockkameramodul (Farb-HD, IR-Nachtsicht)', 3200, 5);"
            };
            for (String sql : partsInserts) {
                statement.executeUpdate(sql);
            }

            // Insert test notifications
            statement.executeUpdate("INSERT INTO Notifications (ID, Notification, Active, ShuttleID, Sender, Comment) VALUES (19, 'Störung der Temperaturregullierung', 'false', 2, 'manager', 'Innentemperatur war leicht zu hoch');");
            statement.executeUpdate("INSERT INTO Notifications (ID, Notification, Active, ShuttleID, Sender, Comment) VALUES (20, 'Wiederholte Fehlfunktionen in einem Sensor', 'true', 2, 'manager', 'Diese Sensoren hatten bisher immer für Probleme gesorgt');");
            statement.executeUpdate("INSERT INTO Notifications (ID, Notification, Active, ShuttleID, Sender, Comment) VALUES (21, 'Nicht nachvollziehbare Fehlermeldungen in Diagnosesystemen', 'false', 3, 'manager', '');");
            statement.executeUpdate("INSERT INTO Notifications (ID, Notification, Active, ShuttleID, Sender, Comment) VALUES (22, 'Störungen im Andocksystem', 'false', 3, 'manager', '');");
            statement.executeUpdate("INSERT INTO Notifications (ID, Notification, Active, ShuttleID, Sender, Comment) VALUES (23, 'Fehlfunktion eines Rauchmelders', 'false', 3, 'manager', 'Rauchmelder soll ständig gepiepst haben');");
            statement.executeUpdate("INSERT INTO Notifications (ID, Notification, Active, ShuttleID, Sender, Comment) VALUES (24, 'Starke Vibrationen', 'true', 1, 'manager', '');");

            // Insert test general tasks
            String[] generalTaskInserts = {
                "INSERT INTO GeneralTasks (ID, Task, Status, ShuttleID, TimeNeeded) VALUES (1, 'Triebwerkscheck', 'false', 0, 1);",
                "INSERT INTO GeneralTasks (ID, Task, Status, ShuttleID, TimeNeeded) VALUES (2, 'Hydrauliksystem testen', 'false', 0, 1);",
                "INSERT INTO GeneralTasks (ID, Task, Status, ShuttleID, TimeNeeded) VALUES (3, 'Treibstoffleitung testen', 'false', 0, 1);",
                "INSERT INTO GeneralTasks (ID, Task, Status, ShuttleID, TimeNeeded) VALUES (4, 'Hitzeschild inspizieren', 'false', 0, 1);",
                "INSERT INTO GeneralTasks (ID, Task, Status, ShuttleID, TimeNeeded) VALUES (5, 'Düsen reinigen', 'false', 0, 1);",
                "INSERT INTO GeneralTasks (ID, Task, Status, ShuttleID, TimeNeeded) VALUES (6, 'Lebenserhaltungssysteme checken', 'false', 0, 1);",
                "INSERT INTO GeneralTasks (ID, Task, Status, ShuttleID, TimeNeeded) VALUES (7, 'Eletronik-& Kommunikationssysteme checken', 'false', 0, 1);",
                "INSERT INTO GeneralTasks (ID, Task, Status, ShuttleID, TimeNeeded) VALUES (8, 'Batterien warten', 'false', 0, 1);",
                "INSERT INTO GeneralTasks (ID, Task, Status, ShuttleID, TimeNeeded) VALUES (9, 'Software überprüfen', 'false', 0, 1);",
                "INSERT INTO GeneralTasks (ID, Task, Status, ShuttleID, TimeNeeded) VALUES (10, 'Systemdiagnose durchführen', 'false', 0, 1);",
                "INSERT INTO GeneralTasks (ID, Task, Status, ShuttleID, TimeNeeded) VALUES (11, 'Triebwerksprobelauf', 'false', 0, 1);",
                "INSERT INTO GeneralTasks (ID, Task, Status, ShuttleID, TimeNeeded) VALUES (12, 'Druckkabinekontrolle', 'false', 0, 1);",
                "INSERT INTO GeneralTasks (ID, Task, Status, ShuttleID, TimeNeeded) VALUES (13, 'Batterie prüfen', 'false', 0, 1);",
                "INSERT INTO GeneralTasks (ID, Task, Status, ShuttleID, TimeNeeded) VALUES (14, 'Testlauf der Navigationssysteme', 'false', 0, 1);",
                "INSERT INTO GeneralTasks (ID, Task, Status, ShuttleID, TimeNeeded) VALUES (15, 'Rettungssysteme überprüfen', 'false', 0, 1);",
                "INSERT INTO GeneralTasks (ID, Task, Status, ShuttleID, TimeNeeded) VALUES (16, 'Endkontrolle', 'false', 0, 1);",
                "INSERT INTO GeneralTasks (ID, Task, Status, ShuttleID, TimeNeeded) VALUES (17, 'Triebwerkscheck', 'false', 1, 1);",
                "INSERT INTO GeneralTasks (ID, Task, Status, ShuttleID, TimeNeeded) VALUES (18, 'Hydrauliksystem testen', 'false', 1, 1);",
                "INSERT INTO GeneralTasks (ID, Task, Status, ShuttleID, TimeNeeded) VALUES (19, 'Treibstoffleitung testen', 'false', 1, 1);",
                "INSERT INTO GeneralTasks (ID, Task, Status, ShuttleID, TimeNeeded) VALUES (20, 'Hitzeschild inspizieren', 'false', 1, 1);",
                "INSERT INTO GeneralTasks (ID, Task, Status, ShuttleID, TimeNeeded) VALUES (21, 'Düsen reinigen', 'false', 1, 1);",
                "INSERT INTO GeneralTasks (ID, Task, Status, ShuttleID, TimeNeeded) VALUES (22, 'Lebenserhaltungssysteme checken', 'false', 1, 1);",
                "INSERT INTO GeneralTasks (ID, Task, Status, ShuttleID, TimeNeeded) VALUES (23, 'Eletronik-& Kommunikationssysteme checken', 'false', 1, 1);",
                "INSERT INTO GeneralTasks (ID, Task, Status, ShuttleID, TimeNeeded) VALUES (24, 'Batterien warten', 'false', 1, 1);",
                "INSERT INTO GeneralTasks (ID, Task, Status, ShuttleID, TimeNeeded) VALUES (25, 'Software überprüfen', 'false', 1, 1);",
                "INSERT INTO GeneralTasks (ID, Task, Status, ShuttleID, TimeNeeded) VALUES (26, 'Systemdiagnose durchführen', 'false', 1, 1);",
                "INSERT INTO GeneralTasks (ID, Task, Status, ShuttleID, TimeNeeded) VALUES (27, 'Triebwerksprobelauf', 'false', 1, 1);",
                "INSERT INTO GeneralTasks (ID, Task, Status, ShuttleID, TimeNeeded) VALUES (28, 'Druckkabinekontrolle', 'false', 1, 1);",
                "INSERT INTO GeneralTasks (ID, Task, Status, ShuttleID, TimeNeeded) VALUES (29, 'Batterie prüfen', 'false', 1, 1);",
                "INSERT INTO GeneralTasks (ID, Task, Status, ShuttleID, TimeNeeded) VALUES (30, 'Testlauf der Navigationssysteme', 'false', 1, 1);",
                "INSERT INTO GeneralTasks (ID, Task, Status, ShuttleID, TimeNeeded) VALUES (31, 'Rettungssysteme überprüfen', 'false', 1, 1);",
                "INSERT INTO GeneralTasks (ID, Task, Status, ShuttleID, TimeNeeded) VALUES (32, 'Endkontrolle', 'false', 1, 1);",
                "INSERT INTO GeneralTasks (ID, Task, Status, ShuttleID, TimeNeeded) VALUES (33, 'Triebwerkscheck', 'true', 2, 1);",
                "INSERT INTO GeneralTasks (ID, Task, Status, ShuttleID, TimeNeeded) VALUES (34, 'Hydrauliksystem testen', 'true', 2, 1);",
                "INSERT INTO GeneralTasks (ID, Task, Status, ShuttleID, TimeNeeded) VALUES (35, 'Treibstoffleitung testen', 'true', 2, 1);",
                "INSERT INTO GeneralTasks (ID, Task, Status, ShuttleID, TimeNeeded) VALUES (36, 'Hitzeschild inspizieren', 'true', 2, 1);",
                "INSERT INTO GeneralTasks (ID, Task, Status, ShuttleID, TimeNeeded) VALUES (37, 'Düsen reinigen', 'true', 2, 1);",
                "INSERT INTO GeneralTasks (ID, Task, Status, ShuttleID, TimeNeeded) VALUES (38, 'Lebenserhaltungssysteme checken', 'true', 2, 1);",
                "INSERT INTO GeneralTasks (ID, Task, Status, ShuttleID, TimeNeeded) VALUES (39, 'Eletronik-& Kommunikationssysteme checken', 'true', 2, 1);",
                "INSERT INTO GeneralTasks (ID, Task, Status, ShuttleID, TimeNeeded) VALUES (40, 'Batterien warten', 'true', 2, 1);",
                "INSERT INTO GeneralTasks (ID, Task, Status, ShuttleID, TimeNeeded) VALUES (41, 'Software überprüfen', 'true', 2, 1);",
                "INSERT INTO GeneralTasks (ID, Task, Status, ShuttleID, TimeNeeded) VALUES (42, 'Systemdiagnose durchführen', 'true', 2, 1);",
                "INSERT INTO GeneralTasks (ID, Task, Status, ShuttleID, TimeNeeded) VALUES (43, 'Triebwerksprobelauf', 'true', 2, 1);",
                "INSERT INTO GeneralTasks (ID, Task, Status, ShuttleID, TimeNeeded) VALUES (44, 'Druckkabinekontrolle', 'true', 2, 1);",
                "INSERT INTO GeneralTasks (ID, Task, Status, ShuttleID, TimeNeeded) VALUES (45, 'Batterie prüfen', 'true', 2, 1);",
                "INSERT INTO GeneralTasks (ID, Task, Status, ShuttleID, TimeNeeded) VALUES (46, 'Testlauf der Navigationssysteme', 'true', 2, 1);",
                "INSERT INTO GeneralTasks (ID, Task, Status, ShuttleID, TimeNeeded) VALUES (47, 'Rettungssysteme überprüfen', 'true', 2, 1);",
                "INSERT INTO GeneralTasks (ID, Task, Status, ShuttleID, TimeNeeded) VALUES (48, 'Endkontrolle', 'false', 2, 1);",
                "INSERT INTO GeneralTasks (ID, Task, Status, ShuttleID, TimeNeeded) VALUES (49, 'Triebwerkscheck', 'true', 3, 1);",
                "INSERT INTO GeneralTasks (ID, Task, Status, ShuttleID, TimeNeeded) VALUES (50, 'Hydrauliksystem testen', 'true', 3, 1);",
                "INSERT INTO GeneralTasks (ID, Task, Status, ShuttleID, TimeNeeded) VALUES (51, 'Treibstoffleitung testen', 'true', 3, 1);",
                "INSERT INTO GeneralTasks (ID, Task, Status, ShuttleID, TimeNeeded) VALUES (52, 'Hitzeschild inspizieren', 'true', 3, 1);",
                "INSERT INTO GeneralTasks (ID, Task, Status, ShuttleID, TimeNeeded) VALUES (53, 'Düsen reinigen', 'true', 3, 1);",
                "INSERT INTO GeneralTasks (ID, Task, Status, ShuttleID, TimeNeeded) VALUES (54, 'Lebenserhaltungssysteme checken', 'true', 3, 1);",
                "INSERT INTO GeneralTasks (ID, Task, Status, ShuttleID, TimeNeeded) VALUES (55, 'Eletronik-& Kommunikationssysteme checken', 'true', 3, 1);",
                "INSERT INTO GeneralTasks (ID, Task, Status, ShuttleID, TimeNeeded) VALUES (56, 'Batterien warten', 'true', 3, 1);",
                "INSERT INTO GeneralTasks (ID, Task, Status, ShuttleID, TimeNeeded) VALUES (57, 'Software überprüfen', 'true', 3, 1);",
                "INSERT INTO GeneralTasks (ID, Task, Status, ShuttleID, TimeNeeded) VALUES (58, 'Systemdiagnose durchführen', 'true', 3, 1);",
                "INSERT INTO GeneralTasks (ID, Task, Status, ShuttleID, TimeNeeded) VALUES (59, 'Triebwerksprobelauf', 'true', 3, 1);",
                "INSERT INTO GeneralTasks (ID, Task, Status, ShuttleID, TimeNeeded) VALUES (60, 'Druckkabinekontrolle', 'true', 3, 1);",
                "INSERT INTO GeneralTasks (ID, Task, Status, ShuttleID, TimeNeeded) VALUES (61, 'Batterie prüfen', 'true', 3, 1);",
                "INSERT INTO GeneralTasks (ID, Task, Status, ShuttleID, TimeNeeded) VALUES (62, 'Testlauf der Navigationssysteme', 'true', 3, 1);",
                "INSERT INTO GeneralTasks (ID, Task, Status, ShuttleID, TimeNeeded) VALUES (63, 'Rettungssysteme überprüfen', 'true', 3, 1);",
                "INSERT INTO GeneralTasks (ID, Task, Status, ShuttleID, TimeNeeded) VALUES (64, 'Endkontrolle', 'true', 3, 1);",
                "INSERT INTO GeneralTasks (ID, Task, Status, ShuttleID, TimeNeeded) VALUES (65, 'Endkontrolle', 'false', 4, 1);",
                "INSERT INTO GeneralTasks (ID, Task, Status, ShuttleID, TimeNeeded) VALUES (66, 'Rettungssysteme überprüfen', 'false', 4, 1);",
                "INSERT INTO GeneralTasks (ID, Task, Status, ShuttleID, TimeNeeded) VALUES (67, 'Testlauf der Navigationssysteme', 'false', 4, 1);",
                "INSERT INTO GeneralTasks (ID, Task, Status, ShuttleID, TimeNeeded) VALUES (68, 'Batterie prüfen', 'false', 4, 1);",
                "INSERT INTO GeneralTasks (ID, Task, Status, ShuttleID, TimeNeeded) VALUES (69, 'Druckkabinekontrolle', 'false', 4, 1);",
                "INSERT INTO GeneralTasks (ID, Task, Status, ShuttleID, TimeNeeded) VALUES (70, 'Software überprüfen', 'false', 4, 1);"
            };
            for (String sql : generalTaskInserts) {
                statement.executeUpdate(sql);
            }

            // Insert test questionnaire ratings
            statement.executeUpdate("INSERT INTO QuestionnaireRatings (ID, Topic, Active, Rating, ShuttleID) VALUES (1, 'Sicherheit', 'true', 6, 0);");
            statement.executeUpdate("INSERT INTO QuestionnaireRatings (ID, Topic, Active, Rating, ShuttleID) VALUES (2, 'Zuverlässigkeit', 'true', 4, 0);");
            statement.executeUpdate("INSERT INTO QuestionnaireRatings (ID, Topic, Active, Rating, ShuttleID) VALUES (3, 'Effizienz', 'true', 4, 0);");
            statement.executeUpdate("INSERT INTO QuestionnaireRatings (ID, Topic, Active, Rating, ShuttleID) VALUES (4, 'Sicherheit', 'true', 7, 1);");
            statement.executeUpdate("INSERT INTO QuestionnaireRatings (ID, Topic, Active, Rating, ShuttleID) VALUES (5, 'Zuverlässigkeit', 'true', 8, 1);");
            statement.executeUpdate("INSERT INTO QuestionnaireRatings (ID, Topic, Active, Rating, ShuttleID) VALUES (6, 'Effizienz', 'true', 2, 1);");
            statement.executeUpdate("INSERT INTO QuestionnaireRatings (ID, Topic, Active, Rating, ShuttleID) VALUES (7, 'Sicherheit', 'true', 8, 2);");
            statement.executeUpdate("INSERT INTO QuestionnaireRatings (ID, Topic, Active, Rating, ShuttleID) VALUES (8, 'Zuverlässigkeit', 'true', 8, 2);");
            statement.executeUpdate("INSERT INTO QuestionnaireRatings (ID, Topic, Active, Rating, ShuttleID) VALUES (9, 'Effizienz', 'true', 6, 2);");
            statement.executeUpdate("INSERT INTO QuestionnaireRatings (ID, Topic, Active, Rating, ShuttleID) VALUES (10, 'Sicherheit', 'true', 3, 3);");
            statement.executeUpdate("INSERT INTO QuestionnaireRatings (ID, Topic, Active, Rating, ShuttleID) VALUES (11, 'Zuverlässigkeit', 'true', 8, 3);");
            statement.executeUpdate("INSERT INTO QuestionnaireRatings (ID, Topic, Active, Rating, ShuttleID) VALUES (12, 'Effizienz', 'true', 6, 3);");

            // Insert test comments
            statement.executeUpdate("INSERT INTO Comments (ID, Comment, ShuttleID, Active) VALUES (1, 'Starke Vibrationen', 1, 'false');");
            statement.executeUpdate("INSERT INTO Comments (ID, Comment, ShuttleID, Active) VALUES (2, 'Störung des Betriebsablaufs durch Passagiere', 1, 'true');");
            statement.executeUpdate("INSERT INTO Comments (ID, Comment, ShuttleID, Active) VALUES (3, 'Störung der Temperaturregullierung', 2, 'false');");
            statement.executeUpdate("INSERT INTO Comments (ID, Comment, ShuttleID, Active) VALUES (4, 'Erneute Verzögerung beim Boarding', 1, 'true');");
            statement.executeUpdate("INSERT INTO Comments (ID, Comment, ShuttleID, Active) VALUES (5, 'Wiederholte Fehlfunktionen in einem Sensor', 2, 'false');");
            statement.executeUpdate("INSERT INTO Comments (ID, Comment, ShuttleID, Active) VALUES (6, 'Nicht nachvollziehbare Fehlermeldungen in Diagnosesystemen', 3, 'false');");
            statement.executeUpdate("INSERT INTO Comments (ID, Comment, ShuttleID, Active) VALUES (8, 'Mangelhafte Aufgabenkoordination', 3, 'true');");
            statement.executeUpdate("INSERT INTO Comments (ID, Comment, ShuttleID, Active) VALUES (9, 'Fehlfunktion eines Rauchmelders', 3, 'false');");
            statement.executeUpdate("INSERT INTO Comments (ID, Comment, ShuttleID, Active) VALUES (10, 'Leck in einem Drucksystem', 1, 'true');");

            logger.info("Loaded test data into database");
        }
    }

    /**
     * Gets the database connection.
     *
     * @return The database connection.
     */
    public Connection getConnection() {
        return connection;
    }
}

