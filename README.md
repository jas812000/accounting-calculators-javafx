# Accounting Calculators (JavaFX)

A desktop accounting application built with Java and JavaFX that provides payroll, tax, and expense calculation tools.  
The project emphasizes separation of concerns, validated user input, and modular business logic suitable for extension and testing.

---

## Overview

This application consolidates common personal accounting workflows into a single desktop interface:

- **Payroll Calculator** – Computes gross and net pay across different pay schedules
- **Tax Calculator** – Calculates federal income tax and FICA using 2025 tax brackets by filing status
- **Expenses Calculator** – Captures categorized expenses and calculates totals

The system is structured to keep UI code (JavaFX views) separate from computation logic (model calculators and tax tables).

---

## Features

- JavaFX desktop UI with a simple welcome → menu → feature navigation flow
- Modular calculation layer for payroll, tax, and expenses
- Filing-status-driven 2025 federal tax table computation
- Input validation and consistent currency formatting
- Expense tracking with categories, description, and optional due date

---

## Architecture

### GUI Layer (`gui`)
- JavaFX screens for **Welcome**, **Menu**, **Payroll**, **Tax**, **Expenses**, and **Exit**
- Handles layout, event handlers, and display formatting
- Applies input validation via `TextFormatter` and button enable/disable bindings

### Domain Model (`model`)
- Immutable records/enums representing inputs and outputs (`PayrollInputs`, `PayrollResult`, `TaxInputs`, `TaxResult`, etc.)
- Pure calculation classes (`PayrollCalculator`, `TaxCalculator`)
- Expense model and category enumeration for the Expenses table

### Tax Tables (`TaxTables`)
- Dedicated tax table calculator encapsulating the 2025 federal tax brackets
- Progressive bracket-based computation by filing status

---

## Notes on Calculations

- **FICA** is modeled as a simplified flat employee rate (SS + Medicare) and does not include wage-base caps or additional Medicare thresholds.
- **Federal tax** uses 2025 bracket logic based on filing status. (This is a table-style computation, not payroll withholding tables.)

---

## Technologies Used

- **Java**
- **JavaFX**
- Java Records and Enums
- Standard Java formatting utilities (`NumberFormat`)
- Input validation via JavaFX bindings and `TextFormatter`

---

## Running the Application

1. Clone the repository
2. Open the project in an IDE configured for JavaFX
3. Run `gui.WelcomeStage` to launch the application

---

## Future Enhancements

- Persistence for expenses (save/load)
- More complete tax modeling (deductions, wage base caps, withholding tables)
- Automated unit and integration tests for calculators
- Export/reporting (CSV/PDF)
- Improved UI consistency and styling system

---

## Author

**James Stevens**
