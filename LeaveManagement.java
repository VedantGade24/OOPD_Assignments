import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// ─────────────────────────────────────────────────────────────────────────────
// ENUMS
// ─────────────────────────────────────────────────────────────────────────────
enum LeaveType {
    CASUAL, EARNED
}

enum LeaveStatus {
    PENDING, APPROVED, REJECTED
}

// ─────────────────────────────────────────────────────────────────────────────
// CUSTOM EXCEPTIONS
// ─────────────────────────────────────────────────────────────────────────────

// Thrown when employee doesn't have enough leave balance
class InsufficientLeaveException extends Exception {
    private String leaveType;
    private int requested;
    private int available;

    public InsufficientLeaveException(String leaveType, int requested, int available) {
        super(String.format("Insufficient %s leave. Requested: %d, Available: %d",
                leaveType, requested, available));
        this.leaveType = leaveType;
        this.requested = requested;
        this.available = available;
    }

    public String getLeaveType() { return leaveType; }
    public int getRequested()    { return requested; }
    public int getAvailable()    { return available; }
}

// Thrown when a leave application ID is not found
class LeaveNotFoundException extends Exception {
    public LeaveNotFoundException(int applicationId) {
        super("Leave application not found with ID: " + applicationId);
    }
}

// Thrown when an invalid leave type string is provided
class InvalidLeaveTypeException extends Exception {
    public InvalidLeaveTypeException(String type) {
        super("Invalid leave type: '" + type + "'. Allowed types: CASUAL, EARNED");
    }
}

// Thrown when manager tries to act on an already-processed application
class LeaveAlreadyProcessedException extends Exception {
    public LeaveAlreadyProcessedException(int applicationId, String status) {
        super("Leave application #" + applicationId + " is already " + status);
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// LEAVE APPLICATION
// ─────────────────────────────────────────────────────────────────────────────
class LeaveApplication {
    private static int counter = 1;

    private int applicationId;
    private int employeeId;
    private LeaveType leaveType;
    private int days;
    private String reason;
    private LeaveStatus status;
    private String managerRemark;

    public LeaveApplication(int employeeId, LeaveType leaveType, int days, String reason) {
        this.applicationId = counter++;
        this.employeeId    = employeeId;
        this.leaveType     = leaveType;
        this.days          = days;
        this.reason        = reason;
        this.status        = LeaveStatus.PENDING;
        this.managerRemark = "";
    }

    public int getApplicationId()    { return applicationId; }
    public int getEmployeeId()       { return employeeId; }
    public LeaveType getLeaveType()  { return leaveType; }
    public int getDays()             { return days; }
    public String getReason()        { return reason; }
    public LeaveStatus getStatus()   { return status; }
    public String getManagerRemark() { return managerRemark; }

    public void setStatus(LeaveStatus status)   { this.status = status; }
    public void setManagerRemark(String remark) { this.managerRemark = remark; }

    @Override
    public String toString() {
        return String.format(
            "Application #%-3d | EmpID: %-4d | Type: %-7s | Days: %-3d | Status: %-8s | Reason: %s",
            applicationId, employeeId, leaveType, days, status, reason);
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// EMPLOYEE
// ─────────────────────────────────────────────────────────────────────────────
class Employee {
    private int id;
    private String name;
    private int totalLeaves;
    private int casualLeaves;
    private int earnedLeaves;

    private List<LeaveApplication> applications;

    public Employee(int id, String name, int casualLeaves, int earnedLeaves) {
        // assert — basic sanity checks (run with: java -ea LeaveManagement)
        assert id > 0                              : "Employee ID must be positive";
        assert name != null && !name.trim().isEmpty() : "Employee name cannot be empty";
        assert casualLeaves >= 0                   : "Casual leaves cannot be negative";
        assert earnedLeaves >= 0                   : "Earned leaves cannot be negative";

        this.id           = id;
        this.name         = name;
        this.casualLeaves = casualLeaves;
        this.earnedLeaves = earnedLeaves;
        this.totalLeaves  = casualLeaves + earnedLeaves;
        this.applications = new ArrayList<>();
    }

    public int getId()              { return id; }
    public String getName()         { return name; }
    public int getTotalLeaves()     { return totalLeaves; }
    public int getCasualLeaves()    { return casualLeaves; }
    public int getEarnedLeaves()    { return earnedLeaves; }
    public List<LeaveApplication> getApplications() { return applications; }

    // Employee applies for leave
    // throws — declares checked exceptions to the caller
    public LeaveApplication applyLeave(String leaveTypeStr, int days, String reason)
            throws InsufficientLeaveException, InvalidLeaveTypeException {

        // Validate leave type — throws InvalidLeaveTypeException
        LeaveType leaveType;
        try {
            leaveType = LeaveType.valueOf(leaveTypeStr.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new InvalidLeaveTypeException(leaveTypeStr);
        }

        // Check balance — throws InsufficientLeaveException
        int balance = (leaveType == LeaveType.CASUAL) ? casualLeaves : earnedLeaves;
        if (days > balance) {
            throw new InsufficientLeaveException(leaveType.name(), days, balance);
        }

        LeaveApplication app = new LeaveApplication(id, leaveType, days, reason);
        applications.add(app);

        System.out.println("  [APPLIED]  " + name + " applied for " + days
                + " " + leaveType + " leave(s). Application ID: #" + app.getApplicationId());
        return app;
    }

    // Called by Manager on approval — deducts from balance
    public void deductLeave(LeaveType type, int days) {
        assert days > 0 : "Days to deduct must be positive";

        if (type == LeaveType.CASUAL) {
            assert days <= casualLeaves : "Deduction exceeds casual leave balance";
            casualLeaves -= days;
        } else {
            assert days <= earnedLeaves : "Deduction exceeds earned leave balance";
            earnedLeaves -= days;
        }
        totalLeaves -= days;
    }

    public void printLeaveBalance() {
        System.out.printf("  [%d] %-15s | Casual: %2d | Earned: %2d | Total: %2d%n",
                id, name, casualLeaves, earnedLeaves, totalLeaves);
    }

    @Override
    public String toString() {
        return String.format("Employee{id=%d, name='%s', casual=%d, earned=%d}",
                id, name, casualLeaves, earnedLeaves);
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// MANAGER
// ─────────────────────────────────────────────────────────────────────────────
class Manager {
    private int id;
    private String name;

    private Map<Integer, LeaveApplication> allApplications; // appId -> app
    private Map<Integer, Employee> employees;               // empId -> employee

    public Manager(int id, String name) {
        assert id > 0 : "Manager ID must be positive";
        this.id              = id;
        this.name            = name;
        this.allApplications = new HashMap<>();
        this.employees       = new HashMap<>();
    }

    public void addEmployee(Employee emp) {
        employees.put(emp.getId(), emp);
        System.out.println("  [REGISTERED] " + emp.getName() + " under Manager " + name);
    }

    public void receiveApplication(LeaveApplication app) {
        allApplications.put(app.getApplicationId(), app);
    }

    // Approve leave — throws checked exceptions
    public void approveLeave(int applicationId, String remark)
            throws LeaveNotFoundException, LeaveAlreadyProcessedException {

        LeaveApplication app = getApplication(applicationId); // throws LeaveNotFoundException

        if (app.getStatus() != LeaveStatus.PENDING) {
            throw new LeaveAlreadyProcessedException(applicationId, app.getStatus().name());
        }

        app.setStatus(LeaveStatus.APPROVED);
        app.setManagerRemark(remark);

        // Deduct leave from employee balance
        Employee emp = employees.get(app.getEmployeeId());
        emp.deductLeave(app.getLeaveType(), app.getDays());

        System.out.println("  [APPROVED]  Application #" + applicationId
                + " approved by " + name + ". Remark: " + remark);
    }

    // Reject leave — throws checked exceptions
    public void rejectLeave(int applicationId, String remark)
            throws LeaveNotFoundException, LeaveAlreadyProcessedException {

        LeaveApplication app = getApplication(applicationId);

        if (app.getStatus() != LeaveStatus.PENDING) {
            throw new LeaveAlreadyProcessedException(applicationId, app.getStatus().name());
        }

        app.setStatus(LeaveStatus.REJECTED);
        app.setManagerRemark(remark);

        System.out.println("  [REJECTED]  Application #" + applicationId
                + " rejected by " + name + ". Remark: " + remark);
    }

    public void viewPendingApplications() {
        System.out.println("\n  ---- Pending Applications ----");
        boolean found = false;
        for (LeaveApplication app : allApplications.values()) {
            if (app.getStatus() == LeaveStatus.PENDING) {
                System.out.println("  " + app);
                found = true;
            }
        }
        if (!found) System.out.println("  No pending applications.");
    }

    public void viewAllApplications() {
        System.out.println("\n===== All Leave Applications (Manager: " + name + ") =====");
        if (allApplications.isEmpty()) {
            System.out.println("  None.");
        } else {
            for (LeaveApplication app : allApplications.values()) {
                System.out.println("  " + app);
                if (!app.getManagerRemark().isEmpty())
                    System.out.println("             Remark : " + app.getManagerRemark());
            }
        }
    }

    // Private helper — throws LeaveNotFoundException
    private LeaveApplication getApplication(int appId) throws LeaveNotFoundException {
        LeaveApplication app = allApplications.get(appId);
        if (app == null) throw new LeaveNotFoundException(appId);
        return app;
    }

    public String getName() { return name; }
}

// ─────────────────────────────────────────────────────────────────────────────
// MAIN CLASS
// ─────────────────────────────────────────────────────────────────────────────
public class LeaveManagement {

    public static void main(String[] args) {

        System.out.println("========== Employee Leave Management System ==========\n");

        // ── Setup ──────────────────────────────────────────────────────────────
        Manager manager = new Manager(1, "Mr. Sharma");

        Employee emp1 = new Employee(101, "Aisha Khan",  6, 10);
        Employee emp2 = new Employee(102, "Ravi Desai",  4,  5);
        Employee emp3 = new Employee(103, "Priya Mehta", 6,  8);

        System.out.println(">> Registering employees...");
        manager.addEmployee(emp1);
        manager.addEmployee(emp2);
        manager.addEmployee(emp3);

        System.out.println("\n--- Initial Leave Balances ---");
        emp1.printLeaveBalance();
        emp2.printLeaveBalance();
        emp3.printLeaveBalance();


        // ── Scenario 1: Normal application → approval and rejection ───────────
        System.out.println("\n========== Scenario 1: Normal Apply + Approve/Reject ==========");
        try {
            LeaveApplication app1 = emp1.applyLeave("CASUAL", 2, "Personal work");
            manager.receiveApplication(app1);

            LeaveApplication app2 = emp2.applyLeave("EARNED", 3, "Family vacation");
            manager.receiveApplication(app2);

            manager.approveLeave(app1.getApplicationId(), "Approved. Enjoy your day off.");
            manager.rejectLeave(app2.getApplicationId(), "Peak project season, please reschedule.");

        } catch (InsufficientLeaveException | InvalidLeaveTypeException e) {
            System.out.println("  Leave error   : " + e.getMessage());
        } catch (LeaveNotFoundException | LeaveAlreadyProcessedException e) {
            System.out.println("  Manager error : " + e.getMessage());
        } finally {
            // finally always executes — used for cleanup/logging
            System.out.println("  [finally] Scenario 1 processing complete.");
        }


        // ── Scenario 2: Insufficient leave balance ─────────────────────────────
        System.out.println("\n========== Scenario 2: Insufficient Leave Balance ==========");
        try {
            // emp2 only has 4 casual leaves, requesting 10
            LeaveApplication app = emp2.applyLeave("CASUAL", 10, "Extended trip");
            manager.receiveApplication(app);
        } catch (InsufficientLeaveException e) {
            System.out.println("  Caught InsufficientLeaveException : " + e.getMessage());
            System.out.println("  Details → Type: " + e.getLeaveType()
                    + ", Requested: " + e.getRequested()
                    + ", Available: " + e.getAvailable());
        } catch (InvalidLeaveTypeException e) {
            System.out.println("  Caught InvalidLeaveTypeException  : " + e.getMessage());
        } finally {
            System.out.println("  [finally] Scenario 2 processing complete.");
        }


        // ── Scenario 3: Invalid leave type ────────────────────────────────────
        System.out.println("\n========== Scenario 3: Invalid Leave Type ==========");
        try {
            LeaveApplication app = emp3.applyLeave("SICK", 2, "Fever");
            manager.receiveApplication(app);
        } catch (InvalidLeaveTypeException e) {
            System.out.println("  Caught InvalidLeaveTypeException  : " + e.getMessage());
        } catch (InsufficientLeaveException e) {
            System.out.println("  Caught InsufficientLeaveException : " + e.getMessage());
        } finally {
            System.out.println("  [finally] Scenario 3 processing complete.");
        }


        // ── Scenario 4: Application not found ────────────────────────────────
        System.out.println("\n========== Scenario 4: Application Not Found ==========");
        try {
            manager.approveLeave(9999, "Approving non-existent application");
        } catch (LeaveNotFoundException e) {
            System.out.println("  Caught LeaveNotFoundException          : " + e.getMessage());
        } catch (LeaveAlreadyProcessedException e) {
            System.out.println("  Caught LeaveAlreadyProcessedException  : " + e.getMessage());
        } finally {
            System.out.println("  [finally] Scenario 4 processing complete.");
        }


        // ── Scenario 5: Approving an already-processed application ────────────
        System.out.println("\n========== Scenario 5: Double Processing Same Application ==========");
        try {
            LeaveApplication app = emp3.applyLeave("EARNED", 2, "Rest and recovery");
            manager.receiveApplication(app);

            manager.approveLeave(app.getApplicationId(), "Approved.");
            // Try to approve again — should throw LeaveAlreadyProcessedException
            manager.approveLeave(app.getApplicationId(), "Approving again...");

        } catch (LeaveAlreadyProcessedException e) {
            System.out.println("  Caught LeaveAlreadyProcessedException  : " + e.getMessage());
        } catch (LeaveNotFoundException | InsufficientLeaveException | InvalidLeaveTypeException e) {
            System.out.println("  Other error : " + e.getMessage());
        } finally {
            System.out.println("  [finally] Scenario 5 processing complete.");
        }


        // ── Scenario 6: Multiple leaves by same employee ──────────────────────
        System.out.println("\n========== Scenario 6: Multiple Leave Applications ==========");
        try {
            LeaveApplication app1 = emp1.applyLeave("EARNED", 3, "Annual vacation");
            manager.receiveApplication(app1);

            LeaveApplication app2 = emp1.applyLeave("CASUAL", 1, "Quick errand");
            manager.receiveApplication(app2);

            manager.viewPendingApplications();

            manager.approveLeave(app1.getApplicationId(), "Approved. Have a good vacation.");
            manager.approveLeave(app2.getApplicationId(), "Approved.");

        } catch (InsufficientLeaveException | InvalidLeaveTypeException e) {
            System.out.println("  Leave error   : " + e.getMessage());
        } catch (LeaveNotFoundException | LeaveAlreadyProcessedException e) {
            System.out.println("  Manager error : " + e.getMessage());
        } finally {
            System.out.println("  [finally] Scenario 6 processing complete.");
        }


        // ── Final State ───────────────────────────────────────────────────────
        manager.viewAllApplications();

        System.out.println("\n--- Final Leave Balances ---");
        emp1.printLeaveBalance();
        emp2.printLeaveBalance();
        emp3.printLeaveBalance();
    }
}