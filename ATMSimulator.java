// Assignment 1: ATM Machine Simulator
// Operations and their bitwise values:
//   Check Balance  = 1  (0001)
//   Deposit        = 2  (0010)
//   Withdraw       = 4  (0100)
//   Transfer Funds = 8  (1000)
//
// User Types and their permission values:
//   Basic    = 1  (can only check balance)
//   Silver   = 3  (check balance + deposit)
//   Gold     = 7  (check balance + deposit + withdraw)
//   Premium  = 15 (all operations)

import java.util.Scanner;

public class ATMSimulator {

    // ── Operation permission bits ──────────────────────────────────────────────
    static final int OP_CHECK_BALANCE  = 1;   // 0001
    static final int OP_DEPOSIT        = 2;   // 0010
    static final int OP_WITHDRAW       = 4;   // 0100
    static final int OP_TRANSFER       = 8;   // 1000

    // ── User type permission masks ─────────────────────────────────────────────
    static final int USER_BASIC   = 1;    // 0001 → check balance only
    static final int USER_SILVER  = 3;    // 0011 → check balance + deposit
    static final int USER_GOLD    = 7;    // 0111 → check balance + deposit + withdraw
    static final int USER_PREMIUM = 15;   // 1111 → all operations

    // ── Check if a user type has permission for an operation ───────────────────
    // Uses bitwise AND: if result != 0, permission exists
    static boolean hasPermission(int userType, int operation) {
        return (userType & operation) != 0;
    }

    // ── Display menu ───────────────────────────────────────────────────────────
    static void showMenu(int userType) {
        System.out.println("\n╔══════════════════════════╗");
        System.out.println("║       ATM MENU           ║");
        System.out.println("╠══════════════════════════╣");
        if (hasPermission(userType, OP_CHECK_BALANCE))
            System.out.println("║  1. Check Balance        ║");
        if (hasPermission(userType, OP_DEPOSIT))
            System.out.println("║  2. Deposit              ║");
        if (hasPermission(userType, OP_WITHDRAW))
            System.out.println("║  4. Withdraw             ║");
        if (hasPermission(userType, OP_TRANSFER))
            System.out.println("║  8. Transfer Funds       ║");
        System.out.println("║  0. Exit                 ║");
        System.out.println("╚══════════════════════════╝");
        System.out.print("Enter your choice: ");
    }

    // ── Get user type value from string ───────────────────────────────────────
    static int getUserTypeValue(String type) {
        switch (type.toUpperCase()) {
            case "BASIC":   return USER_BASIC;
            case "SILVER":  return USER_SILVER;
            case "GOLD":    return USER_GOLD;
            case "PREMIUM": return USER_PREMIUM;
            default:        return -1;
        }
    }

    // ── Get user type name from value ──────────────────────────────────────────
    static String getUserTypeName(int value) {
        switch (value) {
            case USER_BASIC:   return "BASIC";
            case USER_SILVER:  return "SILVER";
            case USER_GOLD:    return "GOLD";
            case USER_PREMIUM: return "PREMIUM";
            default:           return "UNKNOWN";
        }
    }

    // ── Main ───────────────────────────────────────────────────────────────────
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        System.out.println("╔══════════════════════════════════════╗");
        System.out.println("║     WELCOME TO JAVA ATM SIMULATOR    ║");
        System.out.println("╠══════════════════════════════════════╣");
        System.out.println("║  User Types:                         ║");
        System.out.println("║    BASIC   → Check Balance only      ║");
        System.out.println("║    SILVER  → Balance + Deposit       ║");
        System.out.println("║    GOLD    → Balance+Deposit+Withdraw║");
        System.out.println("║    PREMIUM → All Operations          ║");
        System.out.println("╚══════════════════════════════════════╝");

        // ── Get initial input ──────────────────────────────────────────────────
        System.out.print("\nEnter your name: ");
        String name = sc.nextLine();

        System.out.print("Enter your account balance: Rs. ");
        double balance = sc.nextDouble();
        sc.nextLine(); // consume newline

        System.out.print("Enter user type (BASIC / SILVER / GOLD / PREMIUM): ");
        String typeInput = sc.nextLine().trim();
        int userType = getUserTypeValue(typeInput);

        while (userType == -1) {
            System.out.print("Invalid type! Enter again (BASIC / SILVER / GOLD / PREMIUM): ");
            typeInput = sc.nextLine().trim();
            userType = getUserTypeValue(typeInput);
        }

        System.out.printf("%nWelcome, %s! [%s account]%n", name, getUserTypeName(userType));
        System.out.printf("User Permission Bits: %d (%s)%n", userType,
                String.format("%4s", Integer.toBinaryString(userType)).replace(' ', '0'));

        boolean repeat = true;

        while (repeat) {
            showMenu(userType);
            int choice = sc.nextInt();
            sc.nextLine();

            // Check permission using bitwise AND before performing operation
            if (choice != 0 && !hasPermission(userType, choice)) {
                System.out.println("\n⛔ ACCESS DENIED! Your account type does not have permission for this operation.");
                System.out.printf("   Your permission bits : %s (%d)%n",
                        String.format("%4s", Integer.toBinaryString(userType)).replace(' ', '0'), userType);
                System.out.printf("   Required permission  : %s (%d)%n",
                        String.format("%4s", Integer.toBinaryString(choice)).replace(' ', '0'), choice);
            } else {
                switch (choice) {
                    case 0:
                        System.out.println("\nThank you for using Java ATM. Goodbye!");
                        repeat = false;
                        break;

                    case OP_CHECK_BALANCE:
                        System.out.printf("%n💰 Current Balance: Rs. %.2f%n", balance);
                        break;

                    case OP_DEPOSIT:
                        System.out.print("Enter deposit amount: Rs. ");
                        double deposit = sc.nextDouble();
                        sc.nextLine();
                        if (deposit <= 0) {
                            System.out.println("❌ Invalid deposit amount.");
                        } else {
                            balance += deposit;
                            System.out.printf("✅ Rs. %.2f deposited. New Balance: Rs. %.2f%n", deposit, balance);
                        }
                        break;

                    case OP_WITHDRAW:
                        System.out.print("Enter withdrawal amount: Rs. ");
                        double withdraw = sc.nextDouble();
                        sc.nextLine();
                        if (withdraw <= 0) {
                            System.out.println("❌ Invalid withdrawal amount.");
                        } else if (withdraw > balance) {
                            System.out.printf("❌ Insufficient balance! Available: Rs. %.2f%n", balance);
                        } else {
                            balance -= withdraw;
                            System.out.printf("✅ Rs. %.2f withdrawn. New Balance: Rs. %.2f%n", withdraw, balance);
                        }
                        break;

                    case OP_TRANSFER:
                        System.out.print("Enter recipient account number: ");
                        String recipient = sc.nextLine();
                        System.out.print("Enter transfer amount: Rs. ");
                        double transfer = sc.nextDouble();
                        sc.nextLine();
                        if (transfer <= 0) {
                            System.out.println("❌ Invalid transfer amount.");
                        } else if (transfer > balance) {
                            System.out.printf("❌ Insufficient balance! Available: Rs. %.2f%n", balance);
                        } else {
                            balance -= transfer;
                            System.out.printf("✅ Rs. %.2f transferred to account [%s]. New Balance: Rs. %.2f%n",
                                    transfer, recipient, balance);
                        }
                        break;

                    default:
                        System.out.println("❌ Invalid option. Please choose from the menu.");
                }
            }

            // Ask to repeat only if not already exiting
            if (repeat) {
                System.out.print("\nDo you want to perform another operation? (yes/no): ");
                String again = sc.nextLine().trim();
                // Using assignment operator in condition as required
                repeat = (again.equalsIgnoreCase("yes") || again.equalsIgnoreCase("y"));
            }
        }

        sc.close();
    }
}