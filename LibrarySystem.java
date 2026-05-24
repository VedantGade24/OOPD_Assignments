// Assignment 2: Library System
// - Maintains record of 10 books
// - Class (static) variable tracks total issued books count
// - Students can issue or return books
// - Prints percentage of issued books at any time

import java.util.Scanner;

// ─────────────────────────────────────────────
//  Book Class
// ─────────────────────────────────────────────
class Book {
    // Instance variables
    String title;
    String author;
    double price;
    int    edition;
    boolean isIssued;
    String  issuedTo;   // name of student who has it

    // Class (static) variable — shared across ALL Book objects
    static int totalBooks  = 0;
    static int issuedCount = 0;

    // Constructor
    Book(String title, String author, double price, int edition) {
        this.title    = title;
        this.author   = author;
        this.price    = price;
        this.edition  = edition;
        this.isIssued = false;
        this.issuedTo = null;
        totalBooks++;           // increment class variable on every new book
    }

    // Issue book to a student
    boolean issueBook(String studentName) {
        if (isIssued) {
            System.out.printf("  ❌ \"%s\" is already issued to %s.%n", title, issuedTo);
            return false;
        }
        isIssued  = true;
        issuedTo  = studentName;
        issuedCount++;          // update class variable
        System.out.printf("  ✅ \"%s\" issued to %s.%n", title, studentName);
        return true;
    }

    // Return book from a student
    boolean returnBook(String studentName) {
        if (!isIssued) {
            System.out.printf("  ❌ \"%s\" was not issued.%n", title);
            return false;
        }
        if (!issuedTo.equalsIgnoreCase(studentName)) {
            System.out.printf("  ❌ \"%s\" was issued to %s, not %s.%n", title, issuedTo, studentName);
            return false;
        }
        isIssued  = false;
        issuedTo  = null;
        issuedCount--;          // update class variable
        System.out.printf("  ✅ \"%s\" returned by %s.%n", title, studentName);
        return true;
    }

    // Display book details
    void displayBook(int index) {
        System.out.printf("  %-3d | %-35s | %-18s | Ed.%-2d | Rs.%-7.2f | %s%n",
                index,
                title,
                author,
                edition,
                price,
                isIssued ? "Issued to: " + issuedTo : "Available");
    }

    // Class (static) method — percentage of issued books
    static double issuedPercentage() {
        if (totalBooks == 0) return 0;
        return ((double) issuedCount / totalBooks) * 100;
    }
}

// ─────────────────────────────────────────────
//  Student Class
// ─────────────────────────────────────────────
class Student {
    String name;
    int    studentId;

    Student(int id, String name) {
        this.studentId = id;
        this.name      = name;
    }

    // Student issues a book from library
    void issueBook(Book book) {
        System.out.printf("%n[Student: %s] Requesting to issue \"%s\"%n", name, book.title);
        book.issueBook(name);
    }

    // Student returns a book to library
    void returnBook(Book book) {
        System.out.printf("%n[Student: %s] Returning \"%s\"%n", name, book.title);
        book.returnBook(name);
    }
}

// ─────────────────────────────────────────────
//  Main Class
// ─────────────────────────────────────────────
public class LibrarySystem {

    // Print all books
    static void displayAllBooks(Book[] books) {
        System.out.println("\n╔══════════════════════════════════════════════════════════════════════════════════╗");
        System.out.println("║                              LIBRARY CATALOG                                    ║");
        System.out.println("╠═══╦═════════════════════════════════════╦════════════════════╦══════╦═══════════╣");
        System.out.println("║ # ║ Title                               ║ Author             ║ Ed.  ║ Status    ║");
        System.out.println("╠═══╬═════════════════════════════════════╬════════════════════╬══════╬═══════════╣");
        for (int i = 0; i < books.length; i++) {
            books[i].displayBook(i + 1);
        }
        System.out.println("╚══════════════════════════════════════════════════════════════════════════════════╝");
    }

    // Print issued percentage
    static void printStats() {
        System.out.println("\n📊 Library Stats:");
        System.out.printf("   Total Books   : %d%n", Book.totalBooks);
        System.out.printf("   Issued Books  : %d%n", Book.issuedCount);
        System.out.printf("   Available     : %d%n", Book.totalBooks - Book.issuedCount);
        System.out.printf("   Issued %%      : %.2f%%%n", Book.issuedPercentage());
    }

    // Find book by title (simple search)
    static Book findBook(Book[] books, String title) {
        for (Book b : books) {
            if (b.title.equalsIgnoreCase(title)) return b;
        }
        return null;
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        // ── Initialize 10 books ────────────────────────────────────────────────
        Book[] books = {
            new Book("The Pragmatic Programmer",       "David Thomas",       899.00, 2),
            new Book("Clean Code",                     "Robert C. Martin",   799.00, 1),
            new Book("Introduction to Algorithms",     "Cormen et al.",      1299.00, 4),
            new Book("Design Patterns",                "Gang of Four",       950.00, 1),
            new Book("Head First Java",                "Kathy Sierra",       699.00, 3),
            new Book("Operating System Concepts",      "Silberschatz",       1099.00, 10),
            new Book("Computer Networks",              "Andrew Tanenbaum",   999.00, 6),
            new Book("Database System Concepts",       "Korth & Sudarshan",  1150.00, 7),
            new Book("Artificial Intelligence: A MA",  "Stuart Russell",     1350.00, 4),
            new Book("Structure & Interpretation",     "Abelson & Sussman",  750.00, 2)
        };

        System.out.println("╔══════════════════════════════════╗");
        System.out.println("║    WELCOME TO JAVA LIBRARY       ║");
        System.out.println("╚══════════════════════════════════╝");

        boolean running = true;

        while (running) {
            System.out.println("\n┌──────────────────────────────┐");
            System.out.println("│           MAIN MENU          │");
            System.out.println("├──────────────────────────────┤");
            System.out.println("│  1. View All Books           │");
            System.out.println("│  2. Issue a Book             │");
            System.out.println("│  3. Return a Book            │");
            System.out.println("│  4. Library Stats            │");
            System.out.println("│  0. Exit                     │");
            System.out.println("└──────────────────────────────┘");
            System.out.print("Enter choice: ");

            int choice = sc.nextInt();
            sc.nextLine();

            switch (choice) {

                case 1:
                    displayAllBooks(books);
                    break;

                case 2:
                    System.out.print("\nEnter your name: ");
                    String issueName = sc.nextLine();
                    displayAllBooks(books);
                    System.out.print("Enter the exact title of the book to issue: ");
                    String issueTitle = sc.nextLine();
                    Book toIssue = findBook(books, issueTitle);
                    if (toIssue == null) {
                        System.out.println("  ❌ Book not found in library.");
                    } else {
                        Student s1 = new Student(1, issueName);
                        s1.issueBook(toIssue);
                    }
                    printStats();
                    break;

                case 3:
                    System.out.print("\nEnter your name: ");
                    String returnName = sc.nextLine();
                    System.out.print("Enter the exact title of the book to return: ");
                    String returnTitle = sc.nextLine();
                    Book toReturn = findBook(books, returnTitle);
                    if (toReturn == null) {
                        System.out.println("  ❌ Book not found in library.");
                    } else {
                        Student s2 = new Student(2, returnName);
                        s2.returnBook(toReturn);
                    }
                    printStats();
                    break;

                case 4:
                    printStats();
                    break;

                case 0:
                    System.out.println("\nThank you for using Java Library System. Goodbye!");
                    running = false;
                    break;

                default:
                    System.out.println("  ❌ Invalid choice.");
            }
        }

        sc.close();
    }
}