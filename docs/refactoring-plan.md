# Refactoring Plan

## Goal

Refactor the earlier CP2-style implementation into an OOP-based, layered system that follows the Milestone 2 structure.

## Current Layer Mapping

- `model`
  - `Employee`, `Attendance`, `LeaveRequest`, `Payslip`, `PayrollPeriodOption`, `EmployeePayrollSummary`, role-specific user classes, deduction classes
- `dao`
  - `EmployeeDAO`, `UserDAO`, `AttendanceDAO`, `LeaveDAO`, `PayrollDAO`, `NotificationDAO`, `CsvFilePaths`
- `service`
  - `AuthService`, `AccessControlService`, `AttendanceService`, `EmployeePortalService`, `EmployeeValidationService`, `LeaveService`, `NotificationService`
- `ui`
  - `LoginForm`, `EmployeeDashboard`, `HRDashboard`, `PayrollDashboard`, `AdminDashboardFrame`, `SystemToolsFrame`, and reusable panels

## OOP Refactoring Notes

- Encapsulation
  - Core entities keep fields private and expose behavior through getters, setters, and validation.
- Abstraction
  - Interfaces such as `EmployeeInterface`, `UserInterface`, `LeaveInterface`, `PayrollInterface`, and `AttendanceInterface` define layer contracts.
  - Abstract classes such as `SystemUser`, `AbstractDeduction`, and `PayrollComponent` hold shared behavior.
- Inheritance
  - `AdminUser`, `HRUser`, `FinanceUser`, `ItUser`, and `EmployeeUser` extend the shared user hierarchy.
  - Deduction classes extend `AbstractDeduction`.
- Polymorphism
  - Overriding is used in role classes and deduction classes.
  - Overloading is used in service methods such as `AuthService.login(...)`.

## Active Non-UI Classes and Methods

- `EmployeeDAO`
  - `loadEmployees`, `addEmployee`, `updateEmployee`, `deleteEmployee`, `saveEmployees`, `toCSV`
- `UserDAO`
  - `loadUsers`, `saveUsers`, `findUser`
- `AttendanceDAO`
  - `loadAttendance`, `loadAttendanceRows`, `saveAttendanceRows`
- `LeaveDAO`
  - `loadLeaves`, `saveLeave`, `saveAllLeaves`
- `PayrollDAO`
  - `savePayroll`, `savePayrolls`, `getPayrollHistory`
- `NotificationDAO`
  - `loadNotifications`, `saveNotifications`
- `AuthService`
  - `login`, `changePassword`, `resetPasswordWithGovernmentIds`, `isDefaultPassword`
- `AccessControlService`
  - `canAccess`, `getAllowedFeatures`, `hasRole`
- `AttendanceService`
  - `getAttendanceHistory`, `getRecentAttendance`, `timeIn`, `timeOut`, `getTodayStatus`
- `EmployeePortalService`
  - `getEmployeeByNumber`, `getAvailablePayrollPeriods`, `getPayrollSummary`, `getPayrollHistory`, `getPayrollSummariesForPeriod`, `buildPayslipsForPeriod`
- `EmployeeValidationService`
  - `createEmployee`, `updateEmployee`
- `LeaveService`
  - `submitLeave`, `getRequests`, `getRequestsForEmployee`, `respondToLeave`
- `NotificationService`
  - `record`, `getRecentNotifications`, `markAsRead`

## Refactoring Outcome

- Role-specific dashboards now isolate employee, HR, finance, IT, and admin responsibilities.
- DAO classes isolate CSV reading and writing from UI code.
- Validation and business rules are handled in service and model layers instead of in forms alone.
- Dead legacy forms and unused classes were removed to match the current running system.
