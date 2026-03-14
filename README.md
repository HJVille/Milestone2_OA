# MotorPH Payroll System

MotorPH is a Java Swing payroll and employee management system organized using the required layered architecture:

- `model` for business objects and OOP abstractions
- `dao` for CSV persistence
- `service` for business rules and validation
- `ui` for JFrame and panel-based interaction

## Active Role Flows

- `EMPLOYEE`
  - View profile information
  - Time in / time out
  - Submit leave requests
  - View government IDs
  - View payslip summary
  - View payslip history
  - Change password
  - View notifications
- `HR`
  - Manage employees
  - View attendance records
  - Review leave requests
  - View government ID information
- `FINANCE`
  - Process payroll
  - View payroll records
  - Open GUI payslip view
- `IT`
  - Manage user accounts
  - Review CSV paths
  - Review password audit logs
  - Reset employee passwords
- `ADMIN`
  - Full access to HR, Finance, and IT modules through a single container dashboard

## Entry Point

- [MotorPH.java](/Users/heartvillegas/NetBeansProjects/MotorPH/src/main/java/com/mycompany/motorph/main/MotorPH.java)

## Repository

- `git@github.com:HJVille/ooa-oop-motorph-payroll.git`

## Submission Documents

- [milestone-2-implementation-package-filled.md](/Users/heartvillegas/NetBeansProjects/MotorPH/docs/milestone-2-implementation-package-filled.md)
- [refactoring-plan.md](/Users/heartvillegas/NetBeansProjects/MotorPH/docs/refactoring-plan.md)
- [smoke-test-checklist.md](/Users/heartvillegas/NetBeansProjects/MotorPH/docs/smoke-test-checklist.md)
- [known-issues.md](/Users/heartvillegas/NetBeansProjects/MotorPH/docs/known-issues.md)
- [team-contributions.md](/Users/heartvillegas/NetBeansProjects/MotorPH/docs/team-contributions.md)
