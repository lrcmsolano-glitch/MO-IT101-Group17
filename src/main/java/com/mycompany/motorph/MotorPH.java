/*
 * MotorPH Payroll System
 *
 * A console-based Java payroll application for MotorPH employees.
 * Handles login, attendance tracking, salary computation, and
 * government-mandated deductions (SSS, PhilHealth, Pag-IBIG, Withholding Tax).
 *
 * Data is read from two CSV files located in the resources/ folder:
 *   - Employee Details  : employee info including name, birthday, and hourly rate
 *   - Attendance Record : daily login/logout records per employee
 *
 * Roles supported:
 *   - employee      : view own basic details
 *   - payroll_staff : generate payroll reports for one or all employees
 *
 * Authors : Franz Anthony Navarro, Christine Majella Solano,
 *           Samantha Kobe Basbas, Javis Bayle, Vanessa Gomez
 */
package com.mycompany.motorph;

import java.io.BufferedReader;
import java.io.FileReader;
import java.time.Duration;
import java.time.LocalTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class MotorPH {

    // =========================================================================
    // Constants — CSV file paths relative to the project root
    // =========================================================================

    /** Path to the employee details CSV (name, birthday, salary info). */
    private static final String EMP_FILE =
        "resources/MotorPH_Employee Data - Employee Details.csv";

    /** Path to the attendance record CSV (daily login and logout times). */
    private static final String ATT_FILE =
        "resources/MotorPH_Employee Data - Attendance Record.csv";

    // =========================================================================
    // In-memory employee data — four parallel maps, all keyed by employee ID.
    //
    // Using parallel maps instead of a class keeps the design purely procedural.
    // All maps are populated once by loadEmployees() at startup, allowing
    // O(1) field lookups anywhere in the program without re-reading the file.
    // =========================================================================

    /** Maps employee ID → last name. */
    private static final Map<String, String> lastNames   = new HashMap<>();

    /** Maps employee ID → first name. */
    private static final Map<String, String> firstNames  = new HashMap<>();

    /** Maps employee ID → birthday string (MM/DD/YYYY). */
    private static final Map<String, String> birthdays   = new HashMap<>();

    /** Maps employee ID → hourly rate in Philippine Peso. */
    private static final Map<String, Double> hourlyRates = new HashMap<>();

    // =========================================================================
    // Entry Point
    // =========================================================================

    /**
     * Application entry point.
     *
     * Workflow:
     * 
     *   Prompt for username and password.
     *   Validate credentials — only "employee" and "payroll_staff"
     *       with password "1234" are accepted.
     *   Load all employee records into memory via {@link #loadEmployees()}.
     *   Route to the appropriate menu based on the authenticated role.
     * 
     */
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        System.out.println("Login");
        System.out.print("Enter Username: ");
        String username = sc.nextLine();
        System.out.print("Enter Password: ");
        String password = sc.nextLine();

        // Reject any username that is not a recognised role, or a wrong password
        if ((!username.equals("employee") && !username.equals("payroll_staff"))
                || !password.equals("1234")) {
            System.out.println("\nIncorrect username and/or password");
            return;
        }

        // Populate the in-memory maps from the employee CSV
        loadEmployees();

        // Guard against an empty or unreadable employee file
        if (lastNames.isEmpty()) {
            System.out.println("Error: could not load employee data.");
            return;
        }

        // Hand off to the correct role menu
        displayMenu(username, sc);
    }

    // =========================================================================
    // CSV Parsing
    // =========================================================================

    /**
     * Reads every row of the employee details CSV and populates the four
     * parallel maps.
     */
    static void loadEmployees() {
        try (BufferedReader br = new BufferedReader(new FileReader(EMP_FILE))) {

            br.readLine(); // skip the header row

            String line;
            while ((line = br.readLine()) != null) {

                if (line.trim().isEmpty()) continue; // ignore blank lines

                // Use a regex-aware split to handle quoted commas (e.g. "45,000")
                String[] data = line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)");

                String employeeId = data[0].trim();

                // Store each field in its corresponding map under the employee ID
                lastNames  .put(employeeId, data[1].trim());
                firstNames .put(employeeId, data[2].trim());
                birthdays  .put(employeeId, data[3].trim());

                // Strip surrounding quotes before parsing the hourly rate to double
                hourlyRates.put(employeeId,
                        Double.parseDouble(data[18].replace("\"", "").trim()));
            }

        } catch (Exception e) {
            System.out.println("Error reading employee file: " + e.getMessage());
        }
    }

    // =========================================================================
    // Menu Routing
    // =========================================================================

    /**
     * Routes the authenticated user to the correct role-specific menu.
     */
    static void displayMenu(String username, Scanner sc) {
        if (username.equals("employee")) {
            displayEmployeeMenu(sc);
        } else {
            displayPayrollMenu(sc);
        }
    }

    /**
     * Displays and handles the employee self-service menu.
     */
    static void displayEmployeeMenu(Scanner sc) {
        boolean done = false;

        while (!done) {
            System.out.println("\n--- Welcome, Employee. ---");
            System.out.println("Menu Option:");
            System.out.println(" [1] Enter your Employee Number.");
            System.out.println(" [2] Exit the Program.");
            System.out.print("Enter a number from the options: ");

            switch (sc.nextLine().trim()) {
                case "1":
                    // Prompt until a valid employee ID is entered, then display details
                    String employeeId = promptForEmployee(sc);
                    System.out.println("\n===================================");
                    System.out.println("======== Employee Details =========");
                    System.out.println("===================================");
                    printEmployeeDetails(employeeId);
                    System.out.println("===================================");
                    done = true; // task complete — exit the menu loop
                    break;
                case "2":
                    System.out.println("Exiting Program...");
                    System.exit(0);
                    break;
                default:
                    // Re-prompt on any unrecognised input
                    System.out.println("Invalid option. Please choose 1 or 2.");
            }
        }
    }

    /**
     * Displays and handles the payroll staff menu.
     */
    static void displayPayrollMenu(Scanner sc) {
        boolean done = false;

        while (!done) {
            System.out.println("\n--- Welcome, Payroll Staff. ---");
            System.out.println("Menu Option:");
            System.out.println(" [1] Process payroll");
            System.out.println(" [2] Exit the Program.");
            System.out.print("Enter a number from the options: ");

            switch (sc.nextLine().trim()) {
                case "1":
                    // Show the payroll sub-menu
                    System.out.println("\nProcess Payroll for:");
                    System.out.println(" [1] One employee");
                    System.out.println(" [2] All employees");
                    System.out.println(" [3] Exit the Program.");
                    System.out.print("Enter a number from the options: ");

                    switch (sc.nextLine().trim()) {
                        case "1":
                            // Single-employee path: prompt for ID, then run payroll
                            String employeeId = promptForEmployee(sc);
                            printEmployeeHeader(employeeId);
                            processPayroll(employeeId);
                            done = true;
                            break;

                        case "2":
                            // All-employees path: iterate every loaded ID
                            // The header is printed once per employee BEFORE
                            // entering the month loop, so it never repeats.
                            for (String id : lastNames.keySet()) {
                                printEmployeeHeader(id);
                                processPayroll(id);
                            }
                            done = true;
                            break;

                        case "3":
                            System.out.println("Exiting Program...");
                            System.exit(0);
                            break;

                        default:
                            System.out.println(
                                "Invalid option. Please choose between 1 - 3.");
                    }
                    break;

                case "2":
                    System.out.println("Exiting Program...");
                    System.exit(0);
                    break;

                default:
                    System.out.println("Invalid option. Please choose 1 or 2.");
            }
        }
    }

    // =========================================================================
    // Payroll Processing
    // =========================================================================

    /**
     * Reads the attendance file for the given employee and prints a full
     * payroll breakdown for each month worked (June–December 2024).
     */
    static void processPayroll(String employeeId) {

        // Arrays sized 13 so month numbers (1–12) map directly to their index.
        // Index 0 is unused.
        double[] firstCutoff  = new double[13]; // total hours for days 1–15
        double[] secondCutoff = new double[13]; // total hours for days 16–end

        // The attendance CSV stores times without leading zeros (e.g. "8:05"),
        // so "H:mm" is used instead of the stricter "HH:mm"
        DateTimeFormatter timeFormat = DateTimeFormatter.ofPattern("H:mm");

        // --- Pass 1: scan the attendance file and accumulate hours per cutoff ---
        try (BufferedReader br = new BufferedReader(new FileReader(ATT_FILE))) {

            br.readLine(); // skip the header row

            String line;
            while ((line = br.readLine()) != null) {

                if (line.trim().isEmpty()) continue; // skip blank lines

                String[] data = line.split(",");
                if (data.length < 6)                      continue; // skip malformed rows
                if (!data[0].trim().equals(employeeId))   continue; // skip other employees

                // Parse the date field (MM/DD/YYYY)
                String[] parts = data[3].trim().split("/");
                int month = Integer.parseInt(parts[0]);
                int day   = Integer.parseInt(parts[1]);
                int year  = Integer.parseInt(parts[2]);

                // Only process 2024 attendance for months June through December
                if (year != 2024 || month < 6 || month > 12) continue;

                LocalTime login  = LocalTime.parse(data[4].trim(), timeFormat);
                LocalTime logout = LocalTime.parse(data[5].trim(), timeFormat);

                // Apply grace-period and lunch-break rules to get credited hours
                double hours = computeHours(login, logout);

                // Accumulate hours into the correct cutoff bucket
                if (day <= 15) firstCutoff[month]  += hours;
                else           secondCutoff[month] += hours;
            }

        } catch (Exception e) {
            System.out.println("Error reading attendance file: " + e.getMessage());
            return; // cannot continue without attendance data
        }

        // Retrieve the employee's hourly rate once, outside the month loop
        double rate = hourlyRates.get(employeeId);

        // --- Pass 2: compute and print the payroll report for each active month ---
        for (int month = 6; month <= 12; month++) {

            // Skip months where the employee has no attendance records
            if (firstCutoff[month] == 0 && secondCutoff[month] == 0) continue;

            String monthName   = getMonthName(month);
            int    daysInMonth = YearMonth.of(2024, month).lengthOfMonth();

            // --- Gross salary per cutoff ---
            double grossFirst   = firstCutoff[month]  * rate;
            double grossSecond  = secondCutoff[month] * rate;

            // Combined monthly gross is used for tax bracket calculation only;
            // deductions are otherwise applied to the 2nd-cutoff gross
            double monthlyGross = grossFirst + grossSecond;

            // --- Government-mandated deductions (applied on 2nd cutoff) ---
            double sss             = computeSSS(grossSecond);
            double philHealth      = computePhilHealth(grossSecond);
            double pagIbig         = computePagIbig(grossSecond);
            double tax             = computeTax(monthlyGross); // uses full monthly gross
            double totalDeductions = sss + philHealth + pagIbig + tax;

            // Net pay for the 2nd cutoff after all deductions
            double netSecond = grossSecond - totalDeductions;

            // --- Print the monthly payroll summary ---
            System.out.println("\n===================================");
            System.out.println("       Month of " + monthName);
            System.out.println("===================================");

            // 1st cutoff: no deductions — net equals gross
            System.out.println("\nCutoff Date: " + monthName + " 1 to 15");
            System.out.printf("Total Hours Worked : %.2f%n", firstCutoff[month]);
            System.out.printf("Gross Salary       : %.2f%n", grossFirst);
            System.out.printf("Net Salary         : %.2f%n", grossFirst);

            // 2nd cutoff: deductions are subtracted from gross
            System.out.println("\nCutoff Date: " + monthName + " 16 to " + daysInMonth);
            System.out.printf("Total Hours Worked : %.2f%n", secondCutoff[month]);
            System.out.printf("Gross Salary       : %.2f%n", grossSecond);
            System.out.printf("Deductions         : %.2f%n", totalDeductions);
            System.out.printf("    SSS            : %.2f%n", sss);
            System.out.printf("    PhilHealth     : %.2f%n", philHealth);
            System.out.printf("    Pag-IBIG       : %.2f%n", pagIbig);
            System.out.printf("    Tax (monthly)  : %.2f%n", tax);
            System.out.printf("Net Salary         : %.2f%n", netSecond);
        }
    }

    // =========================================================================
    // Input Prompt Helpers
    // =========================================================================

    /**
     * Repeatedly prompts the user for an employee ID until a match is found
     * in the loaded employee data.
     */
    static String promptForEmployee(Scanner sc) {
        while (true) {
            System.out.print("\nPlease enter your Employee Number: ");
            String employeeId = sc.nextLine().trim();
            System.out.println("Searching records for Employee #" + employeeId + "...");

            if (lastNames.containsKey(employeeId)) {
                return employeeId; // valid ID found — return to caller
            }

            // ID not found; prompt the user to try again
            System.out.println("Employee number does not exist. Please try again.");
        }
    }

    // =========================================================================
    // Print Helpers
    // =========================================================================

    /**
     * Prints the three core employee fields (ID, full name, birthday) to
     * standard output. Used both in the employee self-service view and as
     * part of the payroll staff header.
     */
    static void printEmployeeDetails(String employeeId) {
        System.out.println("Employee #     : " + employeeId);
        // Name is displayed as "Last, First" to match standard HR formatting
        System.out.println("Employee Name  : "
                + lastNames.get(employeeId) + ", " + firstNames.get(employeeId));
        System.out.println("Birthday       : " + birthdays.get(employeeId));
    }

    /**
     * Prints a decorative section divider followed by the employee's details.
     * Called once per employee before entering the payroll month loop, ensuring
     * the header never appears more than once regardless of how many months
     * the employee worked.
     */
    static void printEmployeeHeader(String employeeId) {
        System.out.println("\n=====================================================");
        printEmployeeDetails(employeeId);
        System.out.println("=====================================================");
    }

    // =========================================================================
    // Utility — Month Name
    // =========================================================================

    /**
     * Converts a numeric month (1–12) to its full English name.
     */
    static String getMonthName(int month) {
        return switch (month) {
            case 1  -> "January";
            case 2  -> "February";
            case 3  -> "March";
            case 4  -> "April";
            case 5  -> "May";
            case 6  -> "June";
            case 7  -> "July";
            case 8  -> "August";
            case 9  -> "September";
            case 10 -> "October";
            case 11 -> "November";
            case 12 -> "December";
            default -> "Month " + month; // fallback for unexpected values
        };
    }

    // =========================================================================
    // Hours Computation
    // =========================================================================

    /**
     * Calculates the number of billable hours worked for a single attendance
     * record, applying the following company rules:
     */
    static double computeHours(LocalTime login, LocalTime logout) {
        LocalTime graceTime  = LocalTime.of(8, 10); // last on-time login
        LocalTime cutoffTime = LocalTime.of(17, 0); // no pay beyond 5:00 PM

        // Cap the logout at 17:00 — overtime is not compensated
        if (logout.isAfter(cutoffTime)) logout = cutoffTime;

        // Calculate raw minutes between (capped) login and logout
        long minutesWorked = Duration.between(login, logout).toMinutes();

        // Deduct the mandatory 1-hour unpaid lunch break
        if (minutesWorked > 60) minutesWorked -= 60;
        else minutesWorked = 0; // worked less than an hour — no credit

        double hours = minutesWorked / 60.0;

        // Grace-period rule: on-time employees always receive a full 8-hour day
        if (!login.isAfter(graceTime)) return 8.0;

        // Late employees receive actual hours, capped at the 8-hour maximum
        return Math.min(hours, 8.0);
    }

    // =========================================================================
    // Government-Mandated Deduction Computations
    // =========================================================================

    /**
     * Computes the employee's SSS (Social Security System) contribution using
     * the tiered monthly salary credit table.
     */
    static double computeSSS(double salary) {
        if (salary < 3250)  return 135.00;
        if (salary < 3750)  return 157.50;
        if (salary < 4250)  return 180.00;
        if (salary < 4750)  return 202.50;
        if (salary < 5250)  return 225.00;
        if (salary < 5750)  return 247.50;
        if (salary < 6250)  return 270.00;
        if (salary < 6750)  return 292.50;
        if (salary < 7250)  return 315.00;
        if (salary < 7750)  return 337.50;
        if (salary < 8250)  return 360.00;
        if (salary < 8750)  return 382.50;
        if (salary < 9250)  return 405.00;
        if (salary < 9750)  return 427.50;
        if (salary < 10250) return 450.00;
        if (salary < 10750) return 472.50;
        if (salary < 11250) return 495.00;
        if (salary < 11750) return 517.50;
        if (salary < 12250) return 540.00;
        if (salary < 12750) return 562.50;
        if (salary < 13250) return 585.00;
        if (salary < 13750) return 607.50;
        if (salary < 14250) return 630.00;
        if (salary < 14750) return 652.50;
        if (salary < 15250) return 675.00;
        if (salary < 15750) return 697.50;
        if (salary < 16250) return 720.00;
        if (salary < 16750) return 742.50;
        if (salary < 17250) return 765.00;
        if (salary < 17750) return 787.50;
        if (salary < 18250) return 810.00;
        if (salary < 18750) return 832.50;
        if (salary < 19250) return 855.00;
        if (salary < 19750) return 877.50;
        if (salary < 20250) return 900.00;
        if (salary < 20750) return 922.50;
        if (salary < 21250) return 945.00;
        if (salary < 21750) return 967.50;
        if (salary < 22250) return 990.00;
        if (salary < 22750) return 1012.50;
        if (salary < 23250) return 1035.00;
        if (salary < 23750) return 1057.50;
        if (salary < 24250) return 1080.00;
        if (salary < 24750) return 1102.50;
        return 1125.00; // maximum contribution for salary >= ₱24,750
    }

    /**
     * Computes the employee's share of the PhilHealth (Philippine Health
     * Insurance Corporation) monthly premium.
     */
    static double computePhilHealth(double salary) {
        // Total premium: 3% of salary, floored at ₱300 and capped at ₱1,800
        double premium = Math.min(Math.max(salary * 0.03, 300), 1800);
        return premium / 2; // employee pays exactly half the total premium
    }

    /**
     * Computes the employee's Pag-IBIG (HDMF) contribution.
     */
    static double computePagIbig(double salary) {
        if (salary >= 1000 && salary <= 1500) return salary * 0.01; // 1% rate
        if (salary > 1500)                    return salary * 0.02; // 2% rate
        return 0; // below the minimum contributory salary
    }

    /**
     * Computes the monthly withholding tax based on the BIR (Bureau of Internal
     * Revenue) graduated tax table for compensation income.
     */
    static double computeTax(double monthlyGross) {
        if (monthlyGross <= 20832)  return 0;                                               // tax-exempt
        if (monthlyGross < 33333)   return (monthlyGross - 20833)  * 0.20;                 // 20% bracket
        if (monthlyGross < 66667)   return 2500   + (monthlyGross - 33333)  * 0.25;        // 25% bracket
        if (monthlyGross < 166667)  return 10833  + (monthlyGross - 66667)  * 0.30;        // 30% bracket
        if (monthlyGross < 666667)  return 40833.33 + (monthlyGross - 166667) * 0.32;      // 32% bracket
        return                             200833.33 + (monthlyGross - 666667) * 0.35;     // 35% bracket
    }
}
