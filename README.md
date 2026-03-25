# MotorPH Payroll System

MotorPH is a Java Swing desktop system for payroll, employee management, attendance, leave processing, account administration, and employee self-service. The project was finalized for the MO-IT110 Terminal Assessment using object-oriented programming and layered architecture.

This README is written for project checking and classroom evaluation. It explains the system, how to run it, how to use each dashboard, and the exact working credentials used for checking.

## 1. System Overview

The application supports five user roles:

- `EMPLOYEE`
- `HR`
- `FINANCE`
- `IT`
- `ADMIN`

Main capabilities:

- role-based login
- employee CRUD
- attendance viewing and time logging
- leave request submission and approval
- payroll computation for semi-monthly and monthly periods
- GUI payslip viewing
- user account management
- password reset flows
- CSV path and audit monitoring tools

## 2. Technology Stack

- Java 21
- Java Swing
- Maven project structure
- CSV-based persistence

Important compatibility note:

- Use `JDK 21` when building or running the project.
- The project `pom.xml` is already pinned to Java 21.

## 3. Project Structure

The codebase follows the required layered architecture:

- `src/main/java/com/mycompany/motorph/model`
  - business objects, user hierarchy, payroll and deduction models
- `src/main/java/com/mycompany/motorph/dao`
  - CSV file reading and writing
- `src/main/java/com/mycompany/motorph/service`
  - business rules, validation, authentication, payroll orchestration
- `src/main/java/com/mycompany/motorph/ui`
  - Swing dashboards, dialogs, and reusable panels

Key runtime data files:

- [CSVs/employees.csv](CSVs/employees.csv)
- [CSVs/users.csv](CSVs/users.csv)
- [CSVs/attendance.csv](CSVs/attendance.csv)
- [CSVs/leave_requests.csv](CSVs/leave_requests.csv)
- [CSVs/payroll_records.csv](CSVs/payroll_records.csv)
- `notifications.csv` and `password_audit.csv` are runtime-generated files created in the project root when the system is used

Entry point:

- Main class: `com.mycompany.motorph.main.MotorPH`
- Source file: [MotorPH.java](src/main/java/com/mycompany/motorph/main/MotorPH.java)

## 4. OOP And Architecture Notes

The project demonstrates the final OOP requirements for the Terminal Assessment:

- Encapsulation
  - entities use private fields with getters and setters
- Abstraction
  - interfaces and abstract/shared base classes are used across the model and DAO layers
- Inheritance
  - user roles extend a shared user hierarchy
- Polymorphism
  - overriding and overloading are both used in the current implementation

Examples:

- user hierarchy: `AdminUser`, `HRUser`, `FinanceUser`, `ItUser`, `EmployeeUser`
- abstract/shared models: `SystemUser`, `AbstractDeduction`, `PayrollComponent`
- service orchestration: authentication, leave, attendance, payroll, and notification workflows are separated from UI code

## 5. Build And Run

### NetBeans

1. Open the project in NetBeans.
2. Make sure the project is using `JDK 21`.
3. Click `Clean and Build Project`.
4. Click `Run Project`.

### Terminal With Maven

If Maven is installed:

```bash
mvn clean package
mvn exec:java
```

### Terminal Without Maven

If Maven is not available, this direct compile check works:

```bash
javac --release 21 -d /tmp/motorph-classes $(find src/main/java -name '*.java')
```

## 6. Login Accounts For Checking

Important:

- Passwords are stored as hashes in [users.csv](CSVs/users.csv), so the CSV no longer shows readable passwords.
- The exact checker credentials are documented here for project evaluation.
- Employee accounts still follow the default password pattern unless changed through the system.

### Privileged Accounts

Use these exact credentials for role checking:

| Username | Password      | Role | Notes |
|---|---------------|---|---|
| `hr1` | `hr1123`      | `HR` | Opens the HR dashboard |
| `payroll1` | `payroll1123` | `FINANCE` | Opens the Finance dashboard |
| `it1` | `it1123`      | `IT` | Opens the IT System Tools dashboard |
| `admin` | `admin123`    | `ADMIN` | Opens the Admin dashboard |

These passwords were normalized for project checking and can still be changed later through the IT account-management panel.

Privileged account note:

- `HR`, `FINANCE`, `IT`, and `ADMIN` checker accounts are not employee-linked in the current seed data, so their `employeeNumber` value in `users.csv` is `0`.

### Employee Accounts

Default employee login pattern:

```text
Username  = employee number
Password  = emp<employeeNumber>
```

Examples:

```text
10003 / emp10003
10010 / emp10010
10022 / emp10022
10034 / emp10034
```

Employee seed accounts cover employee numbers `10001` to `10034`.

Default-password behavior:

- Employee accounts using the default `emp<employeeNumber>` password pattern will trigger the default-password update screen before opening the employee dashboard.

## 7. Dashboard Guide

### Employee Dashboard

Purpose:

- employee self-service portal

Main areas:

- `Dashboard`
  - employee summary and quick information
- `My Profile`
  - employee details, government IDs, password update
- `Payslips`
  - payslip summary and payslip history
- `Leave Requests`
  - file leave requests and view request history
- `Logout`

Typical employee flow:

1. Log in as an employee.
2. Review profile details.
3. Check payslip history and payroll summary.
4. Submit a leave request.
5. Update password if needed.

### HR Dashboard

Purpose:

- employee administration and leave management

Main areas:

- employee management
- attendance records
- leave request queue

Typical HR flow:

1. Log in as `hr1`.
2. Open employee records.
3. Add, edit, or delete employee entries.
4. Review attendance records.
5. Approve or reject leave requests.

### Finance Dashboard

Purpose:

- payroll processing and payroll records

Main areas:

- payroll preview
- payroll processing
- payroll records
- payslip viewing

Typical finance flow:

1. Log in as `payroll1`.
2. Select a payroll period.
3. Preview payroll totals.
4. Process and save payroll records.
5. Review payroll history and payslips.

### IT System Tools

Purpose:

- support and account administration

Main areas:

- user accounts
- CSV path monitor
- password audit log

Typical IT flow:

1. Log in as `it1`.
2. Review user accounts.
3. Reset employee-linked accounts to default password if needed.
4. Manually set passwords when allowed.
5. Review audit history and file-path status.

### Admin Dashboard

Purpose:

- consolidated access to HR, Finance, and IT functions

Main areas:

- employee management
- attendance
- leave requests
- payroll
- user accounts
- CSV status
- audit log

Typical admin flow:

1. Log in as `admin`.
2. Switch between HR, Finance, and IT modules from one dashboard.
3. Review payroll, leave, account, and support data from a single container.

## 8. Password And Recovery Rules

Current password behavior:

- passwords are stored hashed in [users.csv](CSVs/users.csv)
- privileged account checker passwords are listed in this README
- employee checking can use the default `emp<employeeNumber>` password pattern
- old plaintext rows are supported by migration-safe logic
- password changes and resets now save hashes instead of plaintext

Forgot password behavior:

- `EMPLOYEE`
  - self-service recovery using government IDs
- `HR`
  - contact Administrator
- `ADMIN`
  - contact IT
- `IT`
  - contact Administrator

Default password behavior:

- employee accounts using `emp<employeeNumber>` are treated as default-password accounts
- those users are required to update the password before continuing into the employee workspace
- IT/Admin reset to default returns employee-linked accounts to `emp<employeeNumber>`

## 9. Functional Scope By Requirement

The project currently demonstrates:

- login system using CSV-backed user records
- role-based access control
- employee CRUD
- payroll computation
- payslip generation in GUI form
- attendance persistence and retrieval
- leave request submission and approval
- notification and audit logging support

## 10. Validation And Security Notes

Validation covered in the system includes:

- required employee form fields
- numeric-only payroll and ID fields
- birthday/date validation
- duplicate employee number checks
- government ID format checks
- inline UI validation in major forms

Security-related notes:

- `users.csv` no longer stores readable passwords
- password audit events are written to the runtime-generated `password_audit.csv` file when password actions occur
- privileged roles do not use self-service password reset

## 11. Testing And Verification

Build verification used for the project:

```bash
javac --release 21 -d /tmp/motorph-classes $(find src/main/java -name '*.java')
```

Automated test files were used during development and are not included in the final submission branch.

## 12. Notes For Project Checking

- Use `JDK 21`.
- Build the project before running.
- If checking employee accounts, use the default pattern `employeeNumber / emp<employeeNumber>`.
- If checking HR, Finance, IT, or Admin accounts, use the credentials listed in Section 6.
- The CSV now stores password hashes, so readable passwords are no longer shown in [users.csv](CSVs/users.csv).

## 13. Repository

- Repository: `https://github.com/HJVille/Milestone2_OA/tree/terminal-assessment-submission`
