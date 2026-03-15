# MotorPH Payroll System

A console-based Java payroll application for MotorPH employees that handles attendance tracking, salary computation, and government-mandated deductions.

---

## How It Works

### 1. Login
The system prompts for a **username** and **password**. Two roles are supported:

| Role | Username | Password |
|------|----------|----------|
| Employee | `employee` | `1234` |
| Payroll Staff | `payroll_staff` | `1234` |

---

### 2. Employee Role
Employees can look up their own basic details by entering their **Employee Number**. The system searches the employee CSV file and displays:
- Employee Number
- Full Name
- Birthday

---

### 3. Payroll Staff Role
Payroll staff can process payroll for:

- **One Employee** – Enter an employee number to view a full payroll breakdown per month (June–December 2024), split into two cutoff periods.
- **All Employees** – Automatically loops through every employee and generates payroll reports for all of them.

---

## Payroll Computation

Attendance is read from the attendance CSV file. Hours worked are calculated per cutoff period:

- **1st Cutoff:** Days 1–15 of the month
- **2nd Cutoff:** Days 16–end of month

### Hours Worked Rules
- Login after **8:10 AM** → late; actual hours are counted
- Login at or before **8:10 AM** → credited a full **8 hours**
- Logout capped at **5:00 PM**
- A **1-hour lunch break** is deducted if total time exceeds 1 hour
- Maximum of **8 hours** credited per day

### Gross Salary
```
Gross = Total Hours Worked × Hourly Rate
```

### Deductions (applied on 2nd cutoff gross)
| Deduction | Basis |
|-----------|-------|
| SSS | Tiered table based on salary bracket |
| PhilHealth | 3% of salary (employee share = 50%), min ₱300 / max ₱1,800 |
| Pag-IBIG | 1% (salary ≤ ₱1,500) or 2% (salary > ₱1,500) |
| Withholding Tax | Tiered tax table based on taxable income |

### Net Salary
```
Net = Gross − (SSS + PhilHealth + Pag-IBIG + Withholding Tax)
```

> **Note:** Deductions are currently only applied to the **2nd cutoff** salary.

---

## Data Files

The system reads from two CSV files located in the `resources/` folder:

| File | Description |
|------|-------------|
| `MotorPH_Employee Data - Employee Details.csv` | Employee info including name, birthday, and hourly rate (column index 18) |
| `MotorPH_Employee Data - Attendance Record.csv` | Daily login/logout records with date and employee number |

---

## Project Structure

```
MotorPH/
├── src/
│   └── com/mycompany/motorph/
│       └── MotorPH.java
└── resources/
    ├── MotorPH_Employee Data - Employee Details.csv
    └── MotorPH_Employee Data - Attendance Record.csv
```

---

## Requirements

- Java 17 or higher (uses switch expressions and modern `java.time` API)
- CSV data files placed in the `resources/` directory
