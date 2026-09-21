# Accounting Calculators

A JavaFX desktop application that brings payroll, federal income tax, and expense calculations into a single interface.

The project demonstrates Java application development with separated business logic, versioned JSON tax data, repository abstractions, input validation, automated testing, Maven builds, and continuous integration.

---

## Features

### Payroll Calculator

Calculates gross pay, payroll taxes, deductions, and net pay for hourly and salaried employees.

Supported inputs include:

- Hourly or annual compensation
- Multiple pay schedules
- Pay date
- Federal filing status
- Form W-4 Step 2 multiple-jobs selection
- Step 3 credits
- Step 4(a) other income
- Step 4(b) deductions
- Step 4(c) additional withholding
- Federal withholding exemption
- Other payroll deductions
- Year-to-date wages

Payroll calculations include:

- Federal income tax withholding
- Social Security tax
- Medicare tax
- Additional Medicare withholding when applicable
- Other user-entered deductions
- Gross and net pay

The pay date determines the applicable bundled payroll rules.

### Federal Income Tax Calculator

Calculates annual federal income tax from taxable income using progressive tax brackets.

The application includes federal tax schedules for **2020 through 2026** and supports:

- Single
- Married Filing Jointly
- Married Filing Separately
- Head of Household
- Qualifying Surviving Spouse

Tax rules are stored as versioned JSON resources rather than being hard-coded into the user interface.

### Expenses Calculator

Provides an in-memory expense tracker with:

- Expense categories
- Optional descriptions
- Positive monetary amount validation
- Optional due dates
- Add and remove operations
- Automatic running totals
- Currency-formatted output

---

## Application Interface

The JavaFX interface provides a central menu for navigating between the three calculators.

A shared CSS stylesheet provides consistent styling for:

- Typography
- Form controls
- Buttons
- Cards
- Tables
- Calculation results
- Navigation

Financial actions use a distinct visual treatment while navigation and secondary actions remain visually separate.

---

## Architecture

The project separates presentation, business logic, reference data, and data-loading responsibilities.

```text
src/
├── main/
│   ├── java/
│   │   ├── gui/
│   │   │   ├── WelcomeStage.java
│   │   │   ├── PayrollCalculatorView.java
│   │   │   ├── TaxCalculatorView.java
│   │   │   ├── ExpensesView.java
│   │   │   └── ExitView.java
│   │   │
│   │   ├── model/
│   │   │   ├── Payroll and withholding models
│   │   │   ├── Tax calculation models
│   │   │   └── Expense models
│   │   │
│   │   └── repository/
│   │       ├── TaxTableRepository.java
│   │       ├── PayrollWithholdingRepository.java
│   │       └── PayrollTaxRepository.java
│   │
│   └── resources/
│       ├── tax-data/
│       │   ├── 2020.json
│       │   ├── ...
│       │   ├── 2026.json
│       │   └── index.json
│       ├── payroll-withholding/
│       │   ├── 2026.json
│       │   └── index.json
│       ├── payroll-taxes/
│       │   ├── 2026.json
│       │   └── index.json
│       └── styles/
│           └── application.css
│
└── test/
    └── java/
        ├── model/
        └── repository/
```

### GUI Layer

The `gui` package contains the JavaFX views and application navigation.

Its responsibilities include:

- Collecting user input
- Input formatting and validation
- Displaying calculation results
- Navigation between calculators
- Applying shared application styling

Calculation rules are kept outside the UI classes.

### Model Layer

The `model` package contains calculation logic, input and output models, enums, and domain objects.

Major calculation components include:

- `TaxCalculator`
- `FederalWithholdingCalculator`
- `FicaCalculator`
- `PayrollCalculator`

Separating these components from JavaFX allows the calculation logic to be tested independently from the interface.

### Repository Layer

The `repository` package loads the versioned JSON reference data used by the calculators.

Repositories include:

- `TaxTableRepository`
- `PayrollWithholdingRepository`
- `PayrollTaxRepository`

This keeps year-specific tax and payroll rules outside the presentation layer.

---

## Tax and Payroll Data

### Annual Federal Income Tax

Bundled annual federal income tax schedules are available for:

**2020–2026**

The annual tax calculator applies progressive federal tax brackets to **taxable income**.

It is a bracket-based calculator and does not attempt to reproduce every rule, worksheet, credit, deduction, or special case found on a complete federal income tax return.

### Payroll Withholding

Federal payroll withholding data currently supports:

**2026**

Payroll withholding is modeled separately from annual federal income tax liability and uses Form W-4 information entered through the Payroll Calculator.

### FICA

Payroll tax data currently supports:

**2026**

The payroll calculator accounts for employee:

- Social Security tax
- Social Security wage-base limits
- Medicare tax
- Additional Medicare withholding thresholds

Year-to-date wages are used when applying wage-based payroll tax limits.

---

## Technologies

- **Java 21**
- **JavaFX 21**
- **Maven**
- **JUnit 5**
- **Jackson**
- **JSON**
- **CSS**
- **GitHub Actions**

---

## Automated Testing

The project contains automated tests for the calculation and repository layers.

The current suite contains **61 tests** covering:

- Annual federal income tax calculations
- Tax-table loading
- Federal payroll withholding
- Payroll-withholding data loading
- FICA calculations
- Payroll-tax data loading
- Hourly and salaried payroll calculations
- Expense validation and behavior

Run the complete test suite with:

```bash
mvn clean test
```

---

## Continuous Integration

GitHub Actions automatically builds and tests the project on:

- Pull requests targeting `main`
- Pushes to `main`

The workflow uses Java 21 and executes:

```bash
mvn --batch-mode clean test
```

---

## Requirements

To build and run the application locally:

- Java Development Kit (JDK) 21
- Apache Maven

JavaFX and other application dependencies are managed through Maven.

---

## Running the Application

Clone the repository:

```bash
git clone <repository-url>
cd accounting-calculators-javafx
```

Launch the application:

```bash
mvn javafx:run
```

The JavaFX Maven plugin launches `gui.WelcomeStage` as the application entry point.

---

## Building and Testing

Compile the project and run all automated tests:

```bash
mvn clean test
```

Create the Maven package:

```bash
mvn clean package
```

Generated Maven build output is written to `target/`.

---

## Design Decisions

Several implementation choices keep the application maintainable and testable:

- Calculation logic is separated from JavaFX views.
- Annual tax, payroll withholding, and payroll tax rules are stored in versioned JSON resources.
- Unsupported payroll years fail explicitly rather than silently falling back to another year's rules.
- Annual federal income tax and payroll withholding are modeled as separate calculations.
- Expense descriptions and due dates are optional.
- Expense totals update automatically when records are added or removed.
- A shared stylesheet provides consistent application-wide presentation.

---

## Limitations

This project is an educational and portfolio application, not production tax or payroll software.

Current limitations include:

- Annual tax calculations apply progressive federal tax schedules to taxable income rather than reproducing a complete federal income tax return.
- Payroll withholding currently supports the bundled 2026 withholding rules.
- Payroll tax data currently supports 2026.
- The payroll model uses a single year-to-date wage value when applying payroll-tax calculations; production payroll systems may need to track different taxable wage bases separately.
- State and local income taxes are not calculated.
- Expense records are stored only for the current application session and are not persisted.

Calculations should not be used as a substitute for professional tax, payroll, accounting, or financial advice.

---

## Future Development

Potential future improvements include:

- Additional payroll withholding years
- Additional payroll-tax years
- Separate year-to-date taxable wage bases where payroll treatment differs
- State and local tax support
- Expense persistence
- CSV or PDF export
- Additional reporting and historical summaries

---

## License

This project is licensed under the MIT License. See [LICENSE](LICENSE) for details.
