/*Assignment 7: Multithreading
•
Create a multithreaded ticket booking system
•
There are limited number of tickets e.g. 100
•
A user can book only one ticket.
•
Many users e.g. 250 try to book tickets and few of them e.g. 15 cancel already booked ticket at the same time.
*/ 

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

// ─────────────────────────────────────────────
//  Shared Ticket Pool (Critical Section)
// ─────────────────────────────────────────────
class TicketPool {

    private static final int TOTAL_TICKETS = 100;
    private int availableTickets = TOTAL_TICKETS;

    // Tracks which userIds have booked a ticket
    private final Set<Integer> bookedUsers = Collections.synchronizedSet(new HashSet<>());

    // Counters for summary
    private final AtomicInteger successfulBookings = new AtomicInteger(0);
    private final AtomicInteger failedBookings     = new AtomicInteger(0);
    private final AtomicInteger successfulCancels  = new AtomicInteger(0);
    private final AtomicInteger failedCancels      = new AtomicInteger(0);

    // ── Book a ticket ──────────────────────────────────────────────────────────
    public synchronized boolean bookTicket(int userId) {
        if (bookedUsers.contains(userId)) {
            System.out.printf("  [BOOK]   User %-4d → Already has a ticket. Booking denied.%n", userId);
            failedBookings.incrementAndGet();
            return false;
        }
        if (availableTickets <= 0) {
            System.out.printf("  [BOOK]   User %-4d → No tickets left. Booking FAILED.%n", userId);
            failedBookings.incrementAndGet();
            return false;
        }
        availableTickets--;
        bookedUsers.add(userId);
        successfulBookings.incrementAndGet();
        System.out.printf("  [BOOK]   User %-4d → Ticket BOOKED.  Remaining: %d%n",
                userId, availableTickets);
        return true;
    }

    // ── Cancel a ticket ────────────────────────────────────────────────────────
    public synchronized boolean cancelTicket(int userId) {
        if (!bookedUsers.contains(userId)) {
            System.out.printf("  [CANCEL] User %-4d → No booking found. Cancel FAILED.%n", userId);
            failedCancels.incrementAndGet();
            return false;
        }
        availableTickets++;
        bookedUsers.remove(userId);
        successfulCancels.incrementAndGet();
        System.out.printf("  [CANCEL] User %-4d → Ticket CANCELLED. Remaining: %d%n",
                userId, availableTickets);
        return true;
    }

    // ── Getters for summary ────────────────────────────────────────────────────
    public int getAvailableTickets()   { return availableTickets; }
    public int getSuccessfulBookings() { return successfulBookings.get(); }
    public int getFailedBookings()     { return failedBookings.get(); }
    public int getSuccessfulCancels()  { return successfulCancels.get(); }
    public int getFailedCancels()      { return failedCancels.get(); }
    public int getBookedUsersCount()   { return bookedUsers.size(); }
}

// ─────────────────────────────────────────────
//  Booking Thread
// ─────────────────────────────────────────────
class BookingThread extends Thread {

    private final TicketPool pool;
    private final int userId;

    public BookingThread(TicketPool pool, int userId) {
        this.pool   = pool;
        this.userId = userId;
        setName("BookingThread-" + userId);
    }

    @Override
    public void run() {
        // Small random delay to simulate real concurrency
        try { Thread.sleep((long)(Math.random() * 100)); }
        catch (InterruptedException e) { Thread.currentThread().interrupt(); }

        pool.bookTicket(userId);
    }
}

// ─────────────────────────────────────────────
//  Cancellation Thread
// ─────────────────────────────────────────────
class CancellationThread extends Thread {

    private final TicketPool pool;
    private final int userId;

    public CancellationThread(TicketPool pool, int userId) {
        this.pool   = pool;
        this.userId = userId;
        setName("CancelThread-" + userId);
    }

    @Override
    public void run() {
        try { Thread.sleep((long)(Math.random() * 100)); }
        catch (InterruptedException e) { Thread.currentThread().interrupt(); }

        pool.cancelTicket(userId);
    }
}

// ─────────────────────────────────────────────
//  Main Class
// ─────────────────────────────────────────────
public class TicketBookingSystem {

    private static final int TOTAL_TICKETS      = 100;
    private static final int TOTAL_BOOKING_USERS = 250;
    private static final int TOTAL_CANCEL_USERS  = 15;   // first 15 users will try to cancel

    public static void main(String[] args) throws InterruptedException {

        TicketPool pool = new TicketPool();

        System.out.println("╔══════════════════════════════════════════════════════╗");
        System.out.println("║       MULTITHREADED TICKET BOOKING SYSTEM            ║");
        System.out.println("╠══════════════════════════════════════════════════════╣");
        System.out.printf ("║  Total Tickets  : %-33d║%n", TOTAL_TICKETS);
        System.out.printf ("║  Booking Users  : %-33d║%n", TOTAL_BOOKING_USERS);
        System.out.printf ("║  Cancelling Users: %-32d║%n", TOTAL_CANCEL_USERS);
        System.out.println("╚══════════════════════════════════════════════════════╝");
        System.out.println();

        // ── Phase 1: 250 users try to book simultaneously ──────────────────────
        System.out.println("════════ PHASE 1: Booking Phase ════════");
        List<Thread> bookingThreads = new ArrayList<>();
        for (int i = 1; i <= TOTAL_BOOKING_USERS; i++) {
            Thread t = new BookingThread(pool, i);
            bookingThreads.add(t);
        }

        // Start all booking threads (simulates simultaneous access)
        for (Thread t : bookingThreads) t.start();

        // Wait for all bookings to complete
        for (Thread t : bookingThreads) t.join();

        System.out.println();
        System.out.println("════════ PHASE 2: Cancellation Phase ════════");

        // ── Phase 2: 15 users cancel their tickets simultaneously ──────────────
        // Users 1–15 attempt cancellation (they are among the first, likely booked)
        List<Thread> cancelThreads = new ArrayList<>();
        for (int i = 1; i <= TOTAL_CANCEL_USERS; i++) {
            Thread t = new CancellationThread(pool, i);
            cancelThreads.add(t);
        }

        for (Thread t : cancelThreads) t.start();
        for (Thread t : cancelThreads) t.join();

        // ── Summary ────────────────────────────────────────────────────────────
        System.out.println();
        System.out.println("╔══════════════════════════════════════════════════════╗");
        System.out.println("║                   FINAL SUMMARY                     ║");
        System.out.println("╠══════════════════════════════════════════════════════╣");
        System.out.printf ("║  Successful Bookings  : %-28d║%n", pool.getSuccessfulBookings());
        System.out.printf ("║  Failed Bookings      : %-28d║%n", pool.getFailedBookings());
        System.out.printf ("║  Successful Cancels   : %-28d║%n", pool.getSuccessfulCancels());
        System.out.printf ("║  Failed Cancels       : %-28d║%n", pool.getFailedCancels());
        System.out.printf ("║  Current Ticket Holders: %-27d║%n", pool.getBookedUsersCount());
        System.out.printf ("║  Remaining Tickets    : %-28d║%n", pool.getAvailableTickets());
        System.out.println("╚══════════════════════════════════════════════════════╝");
    }
}