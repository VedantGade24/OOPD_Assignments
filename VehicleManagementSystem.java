// Assignment 4: Vehicle Management System
// Hierarchy:
//   Vehicle (base)
//     ├── ManualVehicle  (fuel = None)
//     └── AutoVehicle    (has mileage)
//           ├── Bike     (wheels = 2)
//           └── Car      (wheels = 4)
//
// Demonstrates: Inheritance, Method Overriding, Polymorphism

// ─────────────────────────────────────────────
//  Base Class: Vehicle
// ─────────────────────────────────────────────
class Vehicle {

    protected String brand;
    protected String fuel;
    protected int    maxSpeed;
    protected int    wheels;

    // Constructor
    Vehicle(String brand, String fuel, int maxSpeed, int wheels) {
        this.brand    = brand;
        this.fuel     = fuel;
        this.maxSpeed = maxSpeed;
        this.wheels   = wheels;
    }

    // Methods (to be overridden)
    void displayInfo() {
        System.out.println("  Brand     : " + brand);
        System.out.println("  Fuel      : " + fuel);
        System.out.println("  Max Speed : " + maxSpeed + " km/h");
        System.out.println("  Wheels    : " + wheels);
    }

    void start() {
        System.out.println("  [Vehicle] " + brand + " is starting...");
    }

    void stop() {
        System.out.println("  [Vehicle] " + brand + " has stopped.");
    }
}

// ─────────────────────────────────────────────
//  ManualVehicle: fuel = None (human powered)
// ─────────────────────────────────────────────
class ManualVehicle extends Vehicle {

    private int gears;

    ManualVehicle(String brand, int maxSpeed, int wheels, int gears) {
        super(brand, "None", maxSpeed, wheels);  // fuel = None
        this.gears = gears;
    }

    @Override
    void displayInfo() {
        System.out.println("  ── Manual Vehicle ──");
        super.displayInfo();
        System.out.println("  Gears     : " + gears);
    }

    @Override
    void start() {
        System.out.println("  [ManualVehicle] " + brand + " — pedaling to start (no engine).");
    }

    @Override
    void stop() {
        System.out.println("  [ManualVehicle] " + brand + " — applying hand brakes to stop.");
    }

    void changeGear(int gear) {
        if (gear < 1 || gear > gears)
            System.out.println("  ❌ Invalid gear! Available gears: 1 to " + gears);
        else
            System.out.println("  [ManualVehicle] " + brand + " — gear shifted to " + gear);
    }
}

// ─────────────────────────────────────────────
//  AutoVehicle: has mileage, engine-powered
// ─────────────────────────────────────────────
class AutoVehicle extends Vehicle {

    protected double mileage;  // km per litre

    AutoVehicle(String brand, String fuel, int maxSpeed, int wheels, double mileage) {
        super(brand, fuel, maxSpeed, wheels);
        this.mileage = mileage;
    }

    @Override
    void displayInfo() {
        System.out.println("  ── Auto Vehicle ──");
        super.displayInfo();
        System.out.printf("  Mileage   : %.1f km/l%n", mileage);
    }

    @Override
    void start() {
        System.out.println("  [AutoVehicle] " + brand + " engine started. Vroom!");
    }

    @Override
    void stop() {
        System.out.println("  [AutoVehicle] " + brand + " engine off. Stopped.");
    }

    // Calculate range for given fuel amount
    void calculateRange(double fuelLitres) {
        System.out.printf("  [AutoVehicle] With %.1f litres → Range: %.1f km%n",
                fuelLitres, fuelLitres * mileage);
    }
}

// ─────────────────────────────────────────────
//  Bike: inherits AutoVehicle, wheels = 2
// ─────────────────────────────────────────────
class Bike extends AutoVehicle {

    private String bikeType;   // Sport / Cruiser / Commuter
    private boolean hasABS;

    Bike(String brand, String fuel, int maxSpeed, double mileage,
         String bikeType, boolean hasABS) {
        super(brand, fuel, maxSpeed, 2, mileage);  // wheels = 2
        this.bikeType = bikeType;
        this.hasABS   = hasABS;
    }

    @Override
    void displayInfo() {
        System.out.println("  ══ BIKE ══════════════════════════");
        super.displayInfo();
        System.out.println("  Type      : " + bikeType);
        System.out.println("  ABS       : " + (hasABS ? "Yes" : "No"));
        System.out.println("  ══════════════════════════════════");
    }

    @Override
    void start() {
        System.out.println("  [Bike] " + brand + " — kick-starting the bike. Brap brap!");
    }

    @Override
    void stop() {
        System.out.println("  [Bike] " + brand + " — pulling front & rear brakes to stop.");
    }

    // Bike-specific method
    void ride() {
        System.out.println("  [Bike] " + brand + " (" + bikeType + ") — riding at full throttle!");
    }

    void wheelie() {
        if (bikeType.equalsIgnoreCase("Sport"))
            System.out.println("  [Bike] " + brand + " — pulling a wheelie! 🏍️");
        else
            System.out.println("  [Bike] " + brand + " — not a sport bike, wheelie not recommended.");
    }
}

// ─────────────────────────────────────────────
//  Car: inherits AutoVehicle, wheels = 4
// ─────────────────────────────────────────────
class Car extends AutoVehicle {

    private int    seats;
    private String transmission;  // Manual / Automatic
    private boolean hasAirbags;

    Car(String brand, String fuel, int maxSpeed, double mileage,
        int seats, String transmission, boolean hasAirbags) {
        super(brand, fuel, maxSpeed, 4, mileage);  // wheels = 4
        this.seats        = seats;
        this.transmission = transmission;
        this.hasAirbags   = hasAirbags;
    }

    @Override
    void displayInfo() {
        System.out.println("  ══ CAR ═══════════════════════════");
        super.displayInfo();
        System.out.println("  Seats        : " + seats);
        System.out.println("  Transmission : " + transmission);
        System.out.println("  Airbags      : " + (hasAirbags ? "Yes" : "No"));
        System.out.println("  ══════════════════════════════════");
    }

    @Override
    void start() {
        System.out.println("  [Car] " + brand + " — push-button start. Engine humming.");
    }

    @Override
    void stop() {
        System.out.println("  [Car] " + brand + " — brake pressed, parking mode engaged.");
    }

    // Car-specific method
    void honk() {
        System.out.println("  [Car] " + brand + " — BEEP BEEP! 🚗");
    }

    void openTrunk() {
        System.out.println("  [Car] " + brand + " — trunk opened.");
    }
}

// ─────────────────────────────────────────────
//  Main Class: Demonstrates Polymorphism
// ─────────────────────────────────────────────
public class VehicleManagementSystem {

    // ── Polymorphic method: works for ANY Vehicle ──────────────────────────────
    static void testDrive(Vehicle v) {
        System.out.println("\n▶ Test Driving: " + v.brand);
        v.start();          // calls overridden start() based on actual type
        v.stop();           // calls overridden stop() based on actual type
    }

    // ── Display info polymorphically ───────────────────────────────────────────
    static void showInfo(Vehicle v) {
        v.displayInfo();    // calls overridden displayInfo() based on actual type
    }

    public static void main(String[] args) {

        // ── Create vehicles ────────────────────────────────────────────────────
        ManualVehicle cycle   = new ManualVehicle("Hero Bicycle", 30, 2, 6);

        Bike sportBike        = new Bike("Yamaha R15",    "Petrol", 150, 45.0, "Sport",   true);
        Bike commuter         = new Bike("Honda Shine",   "Petrol", 100, 65.0, "Commuter",false);

        Car sedan             = new Car("Maruti Swift",  "Petrol", 180, 22.0, 5, "Manual",    true);
        Car suv               = new Car("Tata Nexon EV", "Electric", 120, 0.0, 5, "Automatic", true);

        System.out.println("╔══════════════════════════════════════════════════╗");
        System.out.println("║        VEHICLE MANAGEMENT SYSTEM                 ║");
        System.out.println("╚══════════════════════════════════════════════════╝");

        // ── 1. Display info (polymorphism via overridden displayInfo()) ─────────
        System.out.println("\n════════════════════════════════════");
        System.out.println("  SECTION 1: Vehicle Info Display");
        System.out.println("════════════════════════════════════");

        // Using Vehicle array — polymorphism in action
        Vehicle[] fleet = { cycle, sportBike, commuter, sedan, suv };

        for (Vehicle v : fleet) {
            showInfo(v);   // correct displayInfo() called for each type
            System.out.println();
        }

        // ── 2. Polymorphic start/stop ──────────────────────────────────────────
        System.out.println("════════════════════════════════════");
        System.out.println("  SECTION 2: Polymorphic Test Drive");
        System.out.println("════════════════════════════════════");

        for (Vehicle v : fleet) {
            testDrive(v);
        }

        // ── 3. Type-specific methods ───────────────────────────────────────────
        System.out.println("\n════════════════════════════════════");
        System.out.println("  SECTION 3: Type-Specific Methods");
        System.out.println("════════════════════════════════════");

        System.out.println("\n── Manual Vehicle ──");
        cycle.changeGear(3);
        cycle.changeGear(8);   // invalid gear demo

        System.out.println("\n── Bike Methods ──");
        sportBike.ride();
        sportBike.wheelie();
        commuter.wheelie();    // non-sport bike
        sportBike.calculateRange(5.0);

        System.out.println("\n── Car Methods ──");
        sedan.honk();
        sedan.openTrunk();
        suv.honk();
        sedan.calculateRange(30.0);

        // ── 4. Upcasting & Downcasting demo ───────────────────────────────────
        System.out.println("\n════════════════════════════════════");
        System.out.println("  SECTION 4: Upcasting & Downcasting");
        System.out.println("════════════════════════════════════");

        // Upcast — Bike stored as Vehicle reference
        Vehicle v1 = new Bike("Royal Enfield", "Petrol", 130, 35.0, "Cruiser", false);
        v1.start();           // calls Bike's overridden start()
        v1.displayInfo();     // calls Bike's overridden displayInfo()

        // Downcast — get back specific Bike methods
        if (v1 instanceof Bike) {
            Bike b = (Bike) v1;
            b.ride();
            b.wheelie();
        }

        // ── 5. instanceof check demo ───────────────────────────────────────────
        System.out.println("\n════════════════════════════════════");
        System.out.println("  SECTION 5: instanceof Check");
        System.out.println("════════════════════════════════════");

        for (Vehicle v : fleet) {
            System.out.printf("  %-20s → ", v.brand);
            if      (v instanceof Bike)          System.out.println("Bike (AutoVehicle > Vehicle)");
            else if (v instanceof Car)           System.out.println("Car  (AutoVehicle > Vehicle)");
            else if (v instanceof ManualVehicle) System.out.println("ManualVehicle > Vehicle");
            else if (v instanceof AutoVehicle)   System.out.println("AutoVehicle > Vehicle");
            else                                 System.out.println("Vehicle");
        }
    }
}