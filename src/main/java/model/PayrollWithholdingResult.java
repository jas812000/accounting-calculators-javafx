package model;

/**
 * Result of a federal income tax withholding calculation.
 *
 * @param taxYear withholding year selected from the pay date
 * @param adjustedAnnualWages adjusted annual wage amount from Worksheet 1A
 * @param tentativeAnnualWithholding tentative annual withholding before credits
 * @param federalWithholding final federal income tax withheld for the pay period
 */
public record PayrollWithholdingResult(
        int taxYear,
        double adjustedAnnualWages,
        double tentativeAnnualWithholding,
        double federalWithholding
) {}
