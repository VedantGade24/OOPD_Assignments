// Assignment 3: Constructors, Constructor Overloading and Method Overloading
// - Employee has id, name, salary, bonus, rating
// - Every employee must have id and name
// - Default: salary = 10000.00, bonus = 0, rating = 1
// - Employee can have any other salary and/or bonus
// - Final bonus = bonus * rating
// - Array of 10 employees
// - Employer can update bonus or rating or both
// - Print before and after salary details

import java.util.Scanner;

// ─────────────────────────────────────────────
//  Employee Class
// ─────────────────────────────────────────────
class Employee {

    // Instance variables
    int    id;
    String name;
    double salary;
    double bonus;
    int    rating;

    // ── Constructor 1: Only id and name (all defaults) ─────────────────────────
    Employee(int id, String name) {
        this.id     = id;
        this.name   = name;
        this.salary = 10000.00;   // default salary
        this.bonus  = 0;          // default bonus
        this.rating = 1;          // default rating
    }

    // ── Constructor 2: id, name, salary ───────────────────────────────────────
    Employee(int id, String name, double salary) {
        this(id, name);           // call Constructor 1
        this.salary = salary;
    }

    // ── Constructor 3: id, name, salary, bonus ────────────────────────────────
    Employee(int id, String name, double salary, double bonus) {
        this(id, name, salary);   // call Constructor 2
        this.bonus = bonus;
    }

    // ── Constructor 4: id, name, salary, bonus, rating ────────────────────────
    Employee(int id, String name, double salary, double bonus, int rating) {
        this(id, name, salary, bonus);  // call Constructor 3
        this.rating = rating;
    }

    // ── Calculate effective bonus (bonus * rating) ─────────────────────────────
    double getEffectiveBonus() {
        return bonus * rating;
    }

    // ── Calculate total salary (salary + effective bonus) ─────────────────────
    double getTotalSalary() {
        return salary + getEffectiveBonus();
    }

    // ── Method Overloading: update() ──────────────────────────────────────────

    // Update only bonus
    void update(double newBonus) {
        this.bonus = newBonus;
    }

    // Update only rating
    void update(int newRating) {
        this.rating = newRating;
    }

    // Update both bonus and rating
    void update(double newBonus, int newRating) {
        this.bonus  = newBonus;
        this.rating = newRating;
    }

    // ── Display employee details ───────────────────────────────────────────────
    void display() {
        System.out.printf("  %-4d | %-18s | Base: Rs.%-9.2f | Bonus: Rs.%-7.2f | Rating: %-2d | Eff.Bonus: Rs.%-8.2f | Total: Rs.%.2f%n",
                id, name, salary, bonus, rating, getEffectiveBonus(), getTotalSalary());
    }

    // Short display for before/after comparison
    void displayShort(String label) {
        System.out.printf("    %-8s → Salary: Rs.%-9.2f | Bonus: Rs.%-7.2f | Rating: %-2d | Eff.Bonus: Rs.%-8.2f | Total: Rs.%.2f%n",
                label, salary, bonus, rating, getEffectiveBonus(), getTotalSalary());
    }
}

// ─────────────────────────────────────────────
//  Main Class
// ─────────────────────────────────────────────
public class EmployeeSystem {

    // Print all employees
    static void displayAll(Employee[] emp) {
        System.out.println("\n╔══════════════════════════════════════════════════════════════════════════════════════════╗");
        System.out.println("║                              EMPLOYEE RECORDS                                           ║");
        System.out.println("╠══════════════════════════════════════════════════════════════════════════════════════════╣");
        for (Employee e : emp) {
            e.display();
        }
        System.out.println("╚══════════════════════════════════════════════════════════════════════════════════════════╝");
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        // ── Create 10 employees using different constructors ───────────────────
        // Demonstrates constructor overloading
        Employee[] employees = {
            new Employee(101, "Amit Sharma"),                              // Constructor 1: all defaults
            new Employee(102, "Priya Mehta",   25000.00),                  // Constructor 2: custom salary
            new Employee(103, "Ravi Kumar",    30000.00, 2000.00),         // Constructor 3: salary + bonus
            new Employee(104, "Sneha Patil",   35000.00, 3000.00, 3),      // Constructor 4: all fields
            new Employee(105, "Rohit Verma",   40000.00, 4000.00, 4),
            new Employee(106, "Neha Joshi",    22000.00, 1500.00, 2),
            new Employee(107, "Sanjay Gupta",  28000.00),
            new Employee(108, "Anita Rao",     32000.00, 2500.00, 3),
            new Employee(109, "Vikram Singh",  45000.00, 5000.00, 5),
            new Employee(110, "Pooja Desai",   18000.00, 1000.00, 2)
        };

        System.out.println("╔══════════════════════════════════════╗");
        System.out.println("║     EMPLOYEE MANAGEMENT SYSTEM       ║");
        System.out.println("╚══════════════════════════════════════╝");

        boolean running = true;

        while (running) {
            System.out.println("\n┌──────────────────────────────────┐");
            System.out.println("│            EMPLOYER MENU         │");
            System.out.println("├──────────────────────────────────┤");
            System.out.println("│  1. View All Employees           │");
            System.out.println("│  2. Update Employee Bonus        │");
            System.out.println("│  3. Update Employee Rating       │");
            System.out.println("│  4. Update Bonus AND Rating      │");
            System.out.println("│  0. Exit                         │");
            System.out.println("└──────────────────────────────────┘");
            System.out.print("Enter choice: ");

            int choice = sc.nextInt();
            sc.nextLine();

            if (choice == 0) {
                System.out.println("\nExiting Employee Management System. Goodbye!");
                running = false;
                continue;
            }

            if (choice < 1 || choice > 4) {
                System.out.println("  ❌ Invalid choice.");
                continue;
            }

            // Show employees so employer can pick one
            displayAll(employees);
            System.out.print("\nEnter Employee ID to update (101–110): ");
            int targetId = sc.nextInt();
            sc.nextLine();

            // Find the employee
            Employee target = null;
            for (Employee e : employees) {
                if (e.id == targetId) { target = e; break; }
            }

            if (target == null) {
                System.out.println("  ❌ Employee ID not found.");
                continue;
            }

            System.out.printf("%n──── Updating: %s (ID: %d) ────%n", target.name, target.id);

            switch (choice) {

                case 1:
                    // Just view — already shown above
                    break;

                case 2:
                    // Update bonus only → calls update(double)
                    target.displayShort("BEFORE");
                    System.out.print("Enter new Bonus amount: Rs. ");
                    double newBonus = sc.nextDouble();
                    sc.nextLine();
                    target.update(newBonus);          // overloaded: update(double)
                    target.displayShort("AFTER ");
                    break;

                case 3:
                    // Update rating only → calls update(int)
                    target.displayShort("BEFORE");
                    System.out.print("Enter new Rating (1–5): ");
                    int newRating = sc.nextInt();
                    sc.nextLine();
                    target.update(newRating);         // overloaded: update(int)
                    target.displayShort("AFTER ");
                    break;

                case 4:
                    // Update both → calls update(double, int)
                    target.displayShort("BEFORE");
                    System.out.print("Enter new Bonus amount: Rs. ");
                    double nb = sc.nextDouble();
                    sc.nextLine();
                    System.out.print("Enter new Rating (1–5): ");
                    int nr = sc.nextInt();
                    sc.nextLine();
                    target.update(nb, nr);            // overloaded: update(double, int)
                    target.displayShort("AFTER ");
                    break;
            }
        }

        sc.close();
    }
}