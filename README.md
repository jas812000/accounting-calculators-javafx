# Accounting Calculators (JavaFX)

A desktop accounting application built with Java and JavaFX that provides payroll, tax, and expense calculation tools.  
The project emphasizes clean separation of concerns, validated user input, and modular business logic suitable for extension and testing.

---

## Overview

This application consolidates common personal accounting workflows into a single desktop interface:

- **Payroll Calculator** – Computes gross and net pay across different pay schedules
- **Tax Calculator** – Calculates federal income tax and FICA using 2025 IRS tax brackets
- **Expenses Calculator** – Tracks categorized expenses and calculates totals

The system is structured to clearly separate UI components from core calculation logic, making the application easier to maintain, test, and extend.

---

## Features

- JavaFX-based desktop UI
- Modular calculation engines for payroll and tax logic
- IRS 2025 federal tax brackets by filing status
- Expense tracking with categories, descriptions, and optional due dates
- Input validation and formatted currency output
- Simple navigation flow with welcome, menu, and exit screens

---

## Architecture

The application follows a layered structure:

- **GUI Layer (`gui`)**
  - JavaFX views for payroll, tax, expenses, navigation, and exit flows
  - Handles layout, user input, and interaction logic

- **Domain Model (`model`)**
  - Immutable records and enums representing expenses, filing status, pay schedules, and results
  - Pure calculation classes for payroll and tax logic

- **Tax Tables (`TaxTables`)**
  - Dedicated calculator encapsulating 2025 federal tax brackets
  - Progressive tax calculation by filing status

This separation keeps business rules independent of UI concerns and supports future expansion (additional tax years, deductions, persistence, or testing).

---

## Key Calculations

### Payroll
- Supports hourly and annual pay inputs
- Weekly, bi-weekly, and monthly schedules
- FICA deductions (Social Security + Medicare) applied consistently
- Net pay computed after deductions

### Tax
- Filing statuses supported:
  - Single
  - Married Filing Jointly
  - Married Filing Separately
  - Head of Household
  - Qualifying Surviving Spouse
  - Estates and Trusts
- Uses inflation-adjusted IRS 2025 tax brackets
- Progressive bracket-based calculation

### Expenses
- Categorized expense entries
- Optional descriptions and due dates
- Running total calculation
- Input validation for monetary values

---

## Technologies Used

- **Java**
- **JavaFX**
- Java Records and Enums
- Immutable domain models
- Functional-style calculations
- Standard Java formatting utilities

---

## Running the Application

1. Clone the repository
2. Open the project in a Java IDE with JavaFX support
3. Run the `WelcomeStage` class to launch the application

---

## Future Enhancements

- Persistent storage for expenses
- Additional tax years and withholding logic
- Automated unit and integration tests
- Expanded deduction and benefits modeling
- Improved reporting and export features

---

## Author

**James Stevens**
