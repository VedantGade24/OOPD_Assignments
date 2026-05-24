import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.time.LocalDate;

// ─────────────────────────────────────────────────────────────────────────────
// INTERFACE — Item
// ─────────────────────────────────────────────────────────────────────────────
interface Item {
    String getName();
    double getPrice();
    String getCategory();
}

// ─────────────────────────────────────────────────────────────────────────────
// ITEM TYPES
// ─────────────────────────────────────────────────────────────────────────────
class Electronics implements Item {
    private String name;
    private double price;
    private String brand;
    private int warrantyMonths;

    public Electronics(String name, double price, String brand, int warrantyMonths) {
        this.name           = name;
        this.price          = price;
        this.brand          = brand;
        this.warrantyMonths = warrantyMonths;
    }

    @Override public String getName()      { return name; }
    @Override public double getPrice()     { return price; }
    @Override public String getCategory()  { return "Electronics"; }
    public String getBrand()               { return brand; }
    public int getWarrantyMonths()         { return warrantyMonths; }

    @Override
    public String toString() {
        return String.format("Electronics{name='%s', price=%.2f, brand='%s', warranty=%d months}",
                name, price, brand, warrantyMonths);
    }
}

class Grocery implements Item {
    private String name;
    private double price;
    private LocalDate expiryDate;
    private double weightKg;

    public Grocery(String name, double price, LocalDate expiryDate, double weightKg) {
        this.name       = name;
        this.price      = price;
        this.expiryDate = expiryDate;
        this.weightKg   = weightKg;
    }

    @Override public String getName()      { return name; }
    @Override public double getPrice()     { return price; }
    @Override public String getCategory()  { return "Grocery"; }
    public LocalDate getExpiryDate()       { return expiryDate; }
    public double getWeightKg()            { return weightKg; }

    public boolean isExpired() {
        return LocalDate.now().isAfter(expiryDate);
    }

    @Override
    public String toString() {
        return String.format("Grocery{name='%s', price=%.2f, expiry=%s, weight=%.2fkg}",
                name, price, expiryDate, weightKg);
    }
}

class Clothing implements Item {
    private String name;
    private double price;
    private String size;
    private String material;

    public Clothing(String name, double price, String size, String material) {
        this.name     = name;
        this.price    = price;
        this.size     = size;
        this.material = material;
    }

    @Override public String getName()      { return name; }
    @Override public double getPrice()     { return price; }
    @Override public String getCategory()  { return "Clothing"; }
    public String getSize()                { return size; }
    public String getMaterial()            { return material; }

    @Override
    public String toString() {
        return String.format("Clothing{name='%s', price=%.2f, size='%s', material='%s'}",
                name, price, size, material);
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// GENERIC CLASS — InventoryItem<T>
// Wraps any Item type along with its stock quantity
// ─────────────────────────────────────────────────────────────────────────────
class InventoryItem<T extends Item> {
    private T item;
    private int quantity;

    public InventoryItem(T item, int quantity) {
        if (quantity < 0) throw new IllegalArgumentException("Quantity cannot be negative.");
        this.item     = item;
        this.quantity = quantity;
    }

    public T getItem()       { return item; }
    public int getQuantity() { return quantity; }

    public void addStock(int amount) {
        if (amount <= 0) throw new IllegalArgumentException("Amount to add must be positive.");
        quantity += amount;
    }

    public void removeStock(int amount) {
        if (amount <= 0)       throw new IllegalArgumentException("Amount to remove must be positive.");
        if (amount > quantity) throw new IllegalArgumentException("Insufficient stock for: " + item.getName());
        quantity -= amount;
    }

    public boolean isOutOfStock() { return quantity == 0; }

    @Override
    public String toString() {
        return String.format("[%-12s] qty=%-4d | %s", item.getCategory(), quantity, item);
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// GENERIC CLASS — Inventory<T extends Item>
// Core inventory — holds items of ONE specific type
// ─────────────────────────────────────────────────────────────────────────────
class Inventory<T extends Item> {
    private String inventoryName;
    private List<InventoryItem<T>> items;

    public Inventory(String inventoryName) {
        this.inventoryName = inventoryName;
        this.items         = new ArrayList<>();
    }

    // Add item — if already exists (by name), update stock
    public void addItem(T item, int quantity) {
        Optional<InventoryItem<T>> existing = findByName(item.getName());
        if (existing.isPresent()) {
            existing.get().addStock(quantity);
            System.out.println("  Stock updated : " + item.getName()
                    + " | New qty: " + existing.get().getQuantity());
        } else {
            items.add(new InventoryItem<>(item, quantity));
            System.out.println("  Item added    : " + item.getName());
        }
    }

    // Sell / reduce stock
    public void sellItem(String itemName, int quantity) {
        InventoryItem<T> inv = findByName(itemName)
                .orElseThrow(() -> new IllegalArgumentException("Item not found: " + itemName));
        inv.removeStock(quantity);
        System.out.println("  Sold " + quantity + "x " + itemName
                + " | Remaining: " + inv.getQuantity());
    }

    // Total monetary value of all stock
    public double getTotalValue() {
        double total = 0;
        for (InventoryItem<T> inv : items) {
            total += inv.getItem().getPrice() * inv.getQuantity();
        }
        return total;
    }

    // List all out-of-stock items
    public List<T> getOutOfStockItems() {
        List<T> list = new ArrayList<>();
        for (InventoryItem<T> inv : items) {
            if (inv.isOutOfStock()) list.add(inv.getItem());
        }
        return list;
    }

    // Display all items in this inventory
    public void displayAll() {
        System.out.println("\n====== " + inventoryName + " ======");
        if (items.isEmpty()) {
            System.out.println("  (empty)");
        } else {
            for (InventoryItem<T> inv : items) {
                System.out.println("  " + inv);
            }
        }
        System.out.printf("  Total Inventory Value : Rs. %.2f%n", getTotalValue());
    }

    // Expose item list for utility methods
    public List<InventoryItem<T>> getItems() { return items; }
    public String getInventoryName()         { return inventoryName; }

    // Private helper
    private Optional<InventoryItem<T>> findByName(String name) {
        for (InventoryItem<T> inv : items) {
            if (inv.getItem().getName().equalsIgnoreCase(name)) return Optional.of(inv);
        }
        return Optional.empty();
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// UTILITY CLASS — demonstrates wildcards and generic methods
// ─────────────────────────────────────────────────────────────────────────────
class InventoryUtils {

    // Upper-bounded wildcard — works for Inventory of ANY Item subtype (read-only)
    public static void printSummary(Inventory<? extends Item> inventory) {
        System.out.println("\n--- Summary: " + inventory.getInventoryName() + " ---");
        System.out.printf("    Total Value   : Rs. %.2f%n", inventory.getTotalValue());
        List<? extends Item> outOfStock = inventory.getOutOfStockItems();
        if (outOfStock.isEmpty()) {
            System.out.println("    All items are in stock.");
        } else {
            System.out.println("    Out of stock  :");
            for (Item item : outOfStock) System.out.println("      - " + item.getName());
        }
    }

    // Generic method — finds the most expensive item in any typed inventory list
    public static <T extends Item> T getMostExpensive(List<InventoryItem<T>> items) {
        if (items == null || items.isEmpty()) return null;
        T best = items.get(0).getItem();
        for (InventoryItem<T> inv : items) {
            if (inv.getItem().getPrice() > best.getPrice()) best = inv.getItem();
        }
        return best;
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// MAIN CLASS
// ─────────────────────────────────────────────────────────────────────────────
public class InventoryManagement {

    public static void main(String[] args) {

        System.out.println("========== Generic Inventory Management System ==========");

        // ── Electronics Inventory ─────────────────────────────────────────────
        System.out.println("\n>> Adding Electronics...");
        Inventory<Electronics> electronics = new Inventory<>("Electronics Store");
        electronics.addItem(new Electronics("Samsung TV",  45000, "Samsung", 24), 10);
        electronics.addItem(new Electronics("iPhone 15",   80000, "Apple",   12),  5);
        electronics.addItem(new Electronics("Dell Laptop", 55000, "Dell",    12),  7);
        electronics.displayAll();

        System.out.println("\n>> Selling 3x Samsung TV...");
        electronics.sellItem("Samsung TV", 3);
        electronics.displayAll();

        // ── Grocery Inventory ─────────────────────────────────────────────────
        System.out.println("\n>> Adding Groceries...");
        Inventory<Grocery> grocery = new Inventory<>("Grocery Store");
        grocery.addItem(new Grocery("Basmati Rice", 120, LocalDate.now().plusMonths(12), 5.0), 50);
        grocery.addItem(new Grocery("Amul Butter",   55, LocalDate.now().plusDays(30),   0.5), 30);
        grocery.addItem(new Grocery("Tata Salt",     20, LocalDate.now().plusYears(2),   1.0), 100);
        grocery.displayAll();

        System.out.println("\n>> Selling all 50 bags of Basmati Rice (out of stock test)...");
        grocery.sellItem("Basmati Rice", 50);
        List<Grocery> outOfStock = grocery.getOutOfStockItems();
        System.out.println("  Out of stock items: " + outOfStock.size()
                + " (" + (outOfStock.isEmpty() ? "none" : outOfStock.get(0).getName()) + ")");

        // ── Clothing Inventory ────────────────────────────────────────────────
        System.out.println("\n>> Adding Clothing...");
        Inventory<Clothing> clothing = new Inventory<>("Clothing Store");
        clothing.addItem(new Clothing("Levi's Jeans",   2500, "32", "Denim"),  20);
        clothing.addItem(new Clothing("Cotton Kurta",    800, "M",  "Cotton"), 15);
        clothing.addItem(new Clothing("Woollen Jacket", 3500, "L",  "Wool"),    8);
        clothing.displayAll();

        System.out.println("\n>> Restocking 5 more Woollen Jackets...");
        clothing.addItem(new Clothing("Woollen Jacket", 3500, "L", "Wool"), 5);
        clothing.displayAll();

        // ── Wildcard utility — works across all inventory types ───────────────
        System.out.println("\n========== Inventory Summaries (Wildcard Demo) ==========");
        InventoryUtils.printSummary(electronics);
        InventoryUtils.printSummary(grocery);
        InventoryUtils.printSummary(clothing);

        // ── Generic method — most expensive item ──────────────────────────────
        System.out.println("\n========== Most Expensive Items (Generic Method Demo) ==========");
        Electronics costlyElec = InventoryUtils.getMostExpensive(electronics.getItems());
        Grocery     costlyGroc = InventoryUtils.getMostExpensive(grocery.getItems());
        Clothing    costlyClot = InventoryUtils.getMostExpensive(clothing.getItems());

        System.out.println("  Electronics : " + (costlyElec != null ? costlyElec : "N/A"));
        System.out.println("  Grocery     : " + (costlyGroc != null ? costlyGroc : "N/A"));
        System.out.println("  Clothing    : " + (costlyClot != null ? costlyClot : "N/A"));

        // ── Exception demo — insufficient stock ───────────────────────────────
        System.out.println("\n========== Exception Demos ==========");
        System.out.println(">> Over-selling iPhone 15 (only 5 in stock)...");
        try {
            electronics.sellItem("iPhone 15", 999);
        } catch (IllegalArgumentException e) {
            System.out.println("  Caught: " + e.getMessage());
        }

        System.out.println("\n>> Selling an item that doesn't exist...");
        try {
            clothing.sellItem("Invisible Shirt", 1);
        } catch (IllegalArgumentException e) {
            System.out.println("  Caught: " + e.getMessage());
        }

        System.out.println("\n>> Adding item with negative quantity...");
        try {
            electronics.addItem(new Electronics("Broken TV", 100, "NoName", 0), -5);
        } catch (IllegalArgumentException e) {
            System.out.println("  Caught: " + e.getMessage());
        }
    }
}