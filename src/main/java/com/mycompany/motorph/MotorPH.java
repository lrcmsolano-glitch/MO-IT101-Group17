/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */
package com.mycompany.motorph;

import java.io.BufferedReader;
import java.io.FileReader;
import java.time.Duration;
import java.time.LocalTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;

/**
 *
 * @author tinniewinnie
 */
public class MotorPH {

    public static void main(String[] args) {
        String empFile = "resources/MotorPH_Employee Data - Employee Details.csv";
        String attFile = "resources/MotorPH_Employee Data - Attendance Record.csv";

        Scanner sc = new Scanner(System.in);

//        Login
        System.out.println("Login");

//        Username (employee / payroll_staff)
        System.out.print("Enter Username: ");
        String username = sc.nextLine();
//        Password (1234)
        System.out.print("Enter Password: ");
        String password = sc.nextLine();

//        Login Logic
        if (!username.equals("employee") && !username.equals("payroll_staff") || !password.equals("1234")) {
//            Error if username and password is incorrect
            System.out.println("\nIncorrect username and/or password");
        } else {
//            Employee 
            if (username.equals("employee")) {
                boolean optionFound = false;

                do {
                    System.out.println("\n--- Welcome, Employee. ---");
                    System.out.println("Menu Option:");
                    System.out.println(" [1] Enter your Employee Number.");
                    System.out.println(" [2] Exit the Program.");
                    System.out.print("Enter a number from the options: ");

                    String employeeOption = sc.nextLine();

                    switch (employeeOption) {
                        case "1":
                            String EmployeeNumber = "";
                            String LastName = "";
                            String FirstName = "";
                            String Birthday = "";
                            boolean found = false;
                            // Inner Loop: Keep asking for ID until found
                            do {
                                System.out.print("\nPlease enter your Employee Number: ");
                                String empNum = sc.nextLine();
                                System.out.println("Searching records for Employee #" + empNum + "...");
                                
                                try (BufferedReader br = new BufferedReader(new FileReader(empFile))) {
                                    br.readLine(); // Skip Header
                                    String line;
                                    
                                    while ((line = br.readLine()) != null) {
                                        if (line.trim().isEmpty()) {
                                            continue;
                                        }
                                        
                                        String[] data = line.split(",", -1);
                                        
                                        if (data[0].equals(empNum)) {
                                            EmployeeNumber = data[0];
                                            LastName = data[1];
                                            FirstName = data[2];
                                            Birthday = data[3];
                                            found = true;
                                            break;
                                        }
                                    }
                                } catch (Exception e) {
                                    System.out.println("Error reading employee file: " + e.getMessage());
                                    break; // Break the inner loop if file is missing
                                }
                                
                                if (!found) {
                                    System.out.println("Employee number does not exist. Please try again.");
                                }
                                
                            } while (!found);
                            // Display Results
                            System.out.println("\n===================================");
                            System.out.println("======== Employee Details =========");
                            System.out.println("===================================");
                            System.out.println("Employee #     : " + EmployeeNumber);
                            System.out.println("Employee Name  : " + LastName + ", " + FirstName);
                            System.out.println("Birthday       : " + Birthday);
                            System.out.println("===================================");
                            optionFound = true;
                            break;
                        case "2":
                            System.out.println("Exiting Program...");
                            System.exit(0);
                        default:
                            System.out.println("Invalid option. Please choose 1 or 2.");
                            break;
                    }

                } while (!optionFound);

            } else if (username.equals("payroll_staff")) {
                boolean optionFound = false;

                do {
                    System.out.println("\n--- Welcome, Payroll Staff. ---");
                    System.out.println("Menu Option:");
                    System.out.println(" [1] Process payroll");
                    System.out.println(" [2] Exit the Program.");
                    System.out.print("Enter a number from the options: ");

                    String payrollOption = sc.nextLine();

                    switch (payrollOption) {
                        case "1":
                            System.out.println("\nProcess Payroll for: ");
                            System.out.println("Menu Option:");
                            System.out.println(" [1] One employee");
                            System.out.println(" [2] All employees");
                            System.out.println(" [3] Exit the Program.");
                            System.out.print("Enter a number from the options: ");
                            String subOption = sc.nextLine();

                            switch (subOption) {
                                case "1": // One Employee

                                    String EmployeeNumber = "";
                                    String LastName = "";
                                    String FirstName = "";
                                    String Birthday = "";
                                    double hrRate = 0.0;
                                    boolean found = false;
                                    // Inner Loop: Keep asking for ID until found
                                    do {
                                        System.out.print("\nPlease enter your Employee Number: ");
                                        String empNum = sc.nextLine();
                                        System.out.println("Searching records for Employee #" + empNum + "...");

                                        try (BufferedReader br = new BufferedReader(new FileReader(empFile))) {
                                            br.readLine(); // Skip Header
                                            String line;

                                            while ((line = br.readLine()) != null) {
                                                if (line.trim().isEmpty()) {
                                                    continue;
                                                }

                                                //  String[] data = line.split(",", -1);
                                                // Use smart regex split
                                                String[] data = line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)");

                                                System.out.println(data[17]);

                                                if (data[0].equals(empNum)) {
                                                    EmployeeNumber = data[0];
                                                    LastName = data[1];
                                                    FirstName = data[2];
                                                    Birthday = data[3];

//                                                  // Clean the string (remove quotes) before parsing to double
                                                    String rawRate = data[18].replace("\"", "").trim();
                                                    hrRate = Double.parseDouble(rawRate);

                                                    found = true;
                                                    break;
                                                }
                                            }
                                        } catch (Exception e) {
                                            System.out.println("Error reading employee file: " + e.getMessage());
                                            break; // Break the inner loop if file is missing
                                        }

                                        if (!found) {
                                            System.out.println("Employee number does not exist. Please try again.");
                                        }

                                    } while (!found);

                                    // Display Results
                                    System.out.println("\n===================================");
                                    System.out.println("======== Employee Details =========");
                                    System.out.println("===================================");
                                    System.out.println("Employee #     : " + EmployeeNumber);
                                    System.out.println("Employee Name  : " + LastName + ", " + FirstName);
                                    System.out.println("Birthday       : " + Birthday);
                                    System.out.println("===================================");

                                    double[] firstCutoff = new double[13];
                                    double[] secondCutoff = new double[13];

                                    DateTimeFormatter timeFormat = DateTimeFormatter.ofPattern("H:mm");

                                    try (BufferedReader br = new BufferedReader(new FileReader(attFile))) {

                                        br.readLine(); // skip header
                                        String line;

                                        while ((line = br.readLine()) != null) {

                                            if (line.trim().isEmpty()) {
                                                continue;
                                            }

                                            String[] data = line.split(",");

                                            if (data.length < 6) {
                                                continue;
                                            }

                                            // only selected employee
                                            if (!data[0].equals(EmployeeNumber)) {
                                                continue;
                                            }

                                            String date = data[3].trim();
                                            String[] parts = date.split("/");

                                            int month = Integer.parseInt(parts[0]);
                                            int day = Integer.parseInt(parts[1]);
                                            int year = Integer.parseInt(parts[2]);

                                            if (year != 2024) {
                                                continue;  // optional filter
                                            }
                                            LocalTime login = LocalTime.parse(data[4].trim(), timeFormat);
                                            LocalTime logout = LocalTime.parse(data[5].trim(), timeFormat);

                                            double hours = computeHours(login, logout);

                                            if (day <= 15) {
                                                firstCutoff[month] += hours;
                                            } else {
                                                secondCutoff[month] += hours;
                                            }
                                        }

                                    } catch (Exception e) {
                                        System.out.println("Error reading attendance file.");
                                    }

                                    for (int month = 1; month <= 12; month++) {

                                        if (firstCutoff[month] == 0 && secondCutoff[month] == 0) {
                                            continue;
                                        }

                                        int daysInMonth = YearMonth.of(2024, month).lengthOfMonth();

                                        String monthName = switch (month) {
                                            case 6 ->
                                                "June";
                                            case 7 ->
                                                "July";
                                            case 8 ->
                                                "August";
                                            case 9 ->
                                                "September";
                                            case 10 ->
                                                "October";
                                            case 11 ->
                                                "November";
                                            case 12 ->
                                                "December";
                                            default ->
                                                "Month " + month;
                                        };

                                        System.out.println("\n===================================");
                                        System.out.println("       Month of " + monthName + "    ");
                                        System.out.println("===================================");

                                        System.out.println("\nCutoff Date: " + monthName + " 1 to 15");
                                        System.out.println("Total Hours Worked : " + firstCutoff[month]);
                                        
                                         double grossOne = firstCutoff[month] * hrRate;
                                        System.out.println("Gross Salary: " + grossOne);
                                        System.out.println("Net Salary: "+ grossOne);

                                        System.out.println("\nCutoff Date: " + monthName + " 16 to " + daysInMonth);
                                        System.out.println("Total Hours Worked : " + secondCutoff[month]);

                                        double grossTwo = secondCutoff[month] * hrRate;
                                        System.out.println("Gross Salary: " + grossTwo);

                                        double deductionsTwo = computeSSS(grossTwo) + computePhilHealth(grossTwo) + computePagIbig(grossTwo) + computeTax(grossTwo);
                                        System.out.println("Deductions: " + deductionsTwo);
                                        System.out.println("    SSS: " + computeSSS(grossTwo));
                                        System.out.println("    PhilHealth: " + computePhilHealth(grossTwo));
                                        System.out.println("    Pag-IBIG: " + computePagIbig(grossTwo));
                                        System.out.println("    Tax: " + computeTax(grossTwo));
                                        System.out.println("Net Salary: " + (grossTwo - deductionsTwo));
                                    }

                                    optionFound = true;
                                    break;

                                case "2": // All Employees

                                    try (BufferedReader empReader = new BufferedReader(new FileReader(empFile))) {

                                        empReader.readLine(); // skip header
                                        String empLine;

                                        while ((empLine = empReader.readLine()) != null) {

                                            if (empLine.trim().isEmpty()) {
                                                continue;
                                            }

//                                            String[] empData = empLine.split(",", -1);
                                            String[] empData = empLine.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)");

                                            String EmployeeNumberAll = empData[0];
                                            String LastNameAll = empData[1];
                                            String FirstNameAll = empData[2];
                                            String BirthdayAll = empData[3];

                                            String rawRateAll = empData[18].replace("\"", "").trim();
                                            double hrRateAll = Double.parseDouble(rawRateAll);

                                            // Display Employee Info
                                            System.out.println("\n=====================================================");
                                            System.out.println("Employee #     : " + EmployeeNumberAll);
                                            System.out.println("Employee Name  : " + LastNameAll + ", " + FirstNameAll);
                                            System.out.println("Birthday       : " + BirthdayAll);
                                            System.out.println("=====================================================");

                                            double[] firstCutoffAll = new double[13];
                                            double[] secondCutoffAll = new double[13];

                                            DateTimeFormatter timeFormatAll = DateTimeFormatter.ofPattern("H:mm");

                                            // Read attendance file for this employee
                                            try (BufferedReader attReader = new BufferedReader(new FileReader(attFile))) {

                                                attReader.readLine(); // skip header
                                                String attLine;

                                                while ((attLine = attReader.readLine()) != null) {

                                                    if (attLine.trim().isEmpty()) {
                                                        continue;
                                                    }

                                                    String[] data = attLine.split(",");

                                                    if (data.length < 6) {
                                                        continue;
                                                    }

                                                    if (!data[0].equals(EmployeeNumberAll)) {
                                                        continue;
                                                    }

                                                    String date = data[3].trim();
                                                    String[] parts = date.split("/");

                                                    int month = Integer.parseInt(parts[0]);
                                                    int day = Integer.parseInt(parts[1]);
                                                    int year = Integer.parseInt(parts[2]);

                                                    if (year != 2024) {
                                                        continue;
                                                    }

                                                    if (month < 6 || month > 12) {
                                                        continue; // June–Dec only
                                                    }
                                                    LocalTime login = LocalTime.parse(data[4].trim(), timeFormatAll);
                                                    LocalTime logout = LocalTime.parse(data[5].trim(), timeFormatAll);

                                                    double hours = computeHours(login, logout);

                                                    if (day <= 15) {
                                                        firstCutoffAll[month] += hours;
                                                    } else {
                                                        secondCutoffAll[month] += hours;
                                                    }
                                                }

                                            } catch (Exception e) {
                                                System.out.println("Error reading attendance file.");
                                            }

                                            // Display Payroll (June–December only)
                                            for (int month = 6; month <= 12; month++) {

                                                if (firstCutoffAll[month] == 0 && secondCutoffAll[month] == 0) {
                                                    continue;
                                                }

                                                int daysInMonth = YearMonth.of(2024, month).lengthOfMonth();

                                                String monthName = switch (month) {
                                                    case 6 ->
                                                        "June";
                                                    case 7 ->
                                                        "July";
                                                    case 8 ->
                                                        "August";
                                                    case 9 ->
                                                        "September";
                                                    case 10 ->
                                                        "October";
                                                    case 11 ->
                                                        "November";
                                                    case 12 ->
                                                        "December";
                                                    default ->
                                                        "Month " + month;
                                                };

                                                System.out.println("\n===================================");
                                                System.out.println("       Month of " + monthName + "    ");
                                                System.out.println("===================================");

                                                System.out.println("\nCutoff Date: " + monthName + " 1 to 15");
                                                System.out.println("Total Hours Worked : " + firstCutoffAll[month]);
                                                double grossOneAll = firstCutoffAll[month] * hrRateAll;
                                                System.out.println("Gross Salary: " + grossOneAll);
                                                System.out.println("Net Salary: " + grossOneAll);

                                                System.out.println("\nCutoff Date: " + monthName + " 16 to " + daysInMonth);
                                                System.out.println("Total Hours Worked : " + secondCutoffAll[month]);

                                                double grossTwoAll = secondCutoffAll[month] * hrRateAll;
                                                System.out.println("Gross Salary: " + grossTwoAll);

                                                double deductionsTwoAll = computeSSS(grossTwoAll) + computePhilHealth(grossTwoAll) + computePagIbig(grossTwoAll) + computeTax(grossTwoAll);
                                                System.out.println("Deductions: " + deductionsTwoAll);
                                                System.out.println("    SSS: " + computeSSS(grossTwoAll));
                                                System.out.println("    PhilHealth: " + computePhilHealth(grossTwoAll));
                                                System.out.println("    Pag-IBIG: " + computePagIbig(grossTwoAll));
                                                System.out.println("    Tax: " + computeTax(grossTwoAll));
                                                System.out.println("Net Salary: " + (grossTwoAll - deductionsTwoAll));
                                            }

                                        }

                                    } catch (Exception e) {
                                        System.out.println("Error reading employee file.");
                                    }

                                    optionFound = true;
                                    break;
                                case "3":
                                    System.out.println("Exiting Program...");
                                    System.exit(0);
                                default:
                                    System.out.println("Invalid option. Please choose between 1 - 3.");
                                    break;
                            }

                            break;

                        case "2":
                            System.out.println("Exiting Program...");
                            System.exit(0);
                        default:
                            System.out.println("Invalid option. Please choose 1 or 2.");
                            break;
                    }

                } while (!optionFound);

            }
        }

//        
    }

    static double computeHours(LocalTime login, LocalTime logout) {

        LocalTime graceTime = LocalTime.of(8, 10);
        LocalTime cutoffTime = LocalTime.of(17, 0);

        if (logout.isAfter(cutoffTime)) {
            logout = cutoffTime;
        }

        long minutesWorked = Duration.between(login, logout).toMinutes();

        // Deduct lunch (if total worked is more than 1 hour)
        if (minutesWorked > 60) {
            minutesWorked -= 60;
        } else {
            minutesWorked = 0;
        }

        double hours = minutesWorked / 60.0;

        // Grace period rule
        if (!login.isAfter(graceTime)) {
            return 8.0;
        }

        // Return hours worked, capped at 8
        return Math.min(hours, 8.0);
    }

    static double computeSSS(double salary) {

        if (salary < 3250) {
            return 135.00;
        } else if (salary < 3750) {
            return 157.50;
        } else if (salary < 4250) {
            return 180.00;
        } else if (salary < 4750) {
            return 202.50;
        } else if (salary < 5250) {
            return 225.00;
        } else if (salary < 5750) {
            return 247.50;
        } else if (salary < 6250) {
            return 270.00;
        } else if (salary < 6750) {
            return 292.50;
        } else if (salary < 7250) {
            return 315.00;
        } else if (salary < 7750) {
            return 337.50;
        } else if (salary < 8250) {
            return 360.00;
        } else if (salary < 8750) {
            return 382.50;
        } else if (salary < 9250) {
            return 405.00;
        } else if (salary < 9750) {
            return 427.50;
        } else if (salary < 10250) {
            return 450.00;
        } else if (salary < 10750) {
            return 472.50;
        } else if (salary < 11250) {
            return 495.00;
        } else if (salary < 11750) {
            return 517.50;
        } else if (salary < 12250) {
            return 540.00;
        } else if (salary < 12750) {
            return 562.50;
        } else if (salary < 13250) {
            return 585.00;
        } else if (salary < 13750) {
            return 607.50;
        } else if (salary < 14250) {
            return 630.00;
        } else if (salary < 14750) {
            return 652.50;
        } else if (salary < 15250) {
            return 675.00;
        } else if (salary < 15750) {
            return 697.50;
        } else if (salary < 16250) {
            return 720.00;
        } else if (salary < 16750) {
            return 742.50;
        } else if (salary < 17250) {
            return 765.00;
        } else if (salary < 17750) {
            return 787.50;
        } else if (salary < 18250) {
            return 810.00;
        } else if (salary < 18750) {
            return 832.50;
        } else if (salary < 19250) {
            return 855.00;
        } else if (salary < 19750) {
            return 877.50;
        } else if (salary < 20250) {
            return 900.00;
        } else if (salary < 20750) {
            return 922.50;
        } else if (salary < 21250) {
            return 945.00;
        } else if (salary < 21750) {
            return 967.50;
        } else if (salary < 22250) {
            return 990.00;
        } else if (salary < 22750) {
            return 1012.50;
        } else if (salary < 23250) {
            return 1035.00;
        } else if (salary < 23750) {
            return 1057.50;
        } else if (salary < 24250) {
            return 1080.00;
        } else if (salary < 24750) {
            return 1102.50;
        } else {
            return 1125.00;
        }
    }

    static double computePhilHealth(double salary) {

        double premium = salary * 0.03;

        if (premium < 300) {
            premium = 300;
        }
        if (premium > 1800) {
            premium = 1800;
        }

        return premium / 2; // employee share
    }

    static double computePagIbig(double salary) {

        if (salary >= 1000 && salary <= 1500) {
            return salary * 0.01;
        } else if (salary > 1500) {
            return salary * 0.02;
        } else {
            return 0;
        }
    }

    static double computeTax(double taxableIncome) {

        if (taxableIncome <= 20832) {
            return 0;
        } else if (taxableIncome < 33333) {
            return (taxableIncome - 20833) * 0.20;
        } else if (taxableIncome < 66667) {
            return 2500 + (taxableIncome - 33333) * 0.25;
        } else if (taxableIncome < 166667) {
            return 10833 + (taxableIncome - 66667) * 0.30;
        } else if (taxableIncome < 666667) {
            return 40833.33 + (taxableIncome - 166667) * 0.32;
        } else {
            return 200833.33 + (taxableIncome - 666667) * 0.35;
        }
    }
}
