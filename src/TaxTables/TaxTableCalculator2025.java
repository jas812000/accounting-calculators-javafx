package TaxTables;

import java.text.DecimalFormat;
import model.FilingStatus;

/**
 * The {@code TaxTableCalculator} class calculates the federal income tax for individuals,
 * heads of household, married couples (filing jointly or separately), and estates/trusts
 * based on their filing status and gross income for the 2025 tax year.
 * <p>
 * This calculator uses inflation-adjusted tax brackets and rates as published in the IRS
 * Revenue Procedure for taxable years beginning in 2025. The class supports the following
 * filing statuses:
 * <ul>
 *     <li>Unmarried Individuals</li>
 *     <li>Heads of Household</li>
 *     <li>Married Individuals Filing Separately</li>
 *     <li>Married Individuals Filing Jointly or Surviving Spouses</li>
 *     <li>Estates and Trusts</li>
 * </ul>
 * <p>
 * The class encapsulates logic to determine the applicable tax rate and calculate the
 * federal income tax using the correct thresholds and rates according to the taxpayer's
 * status and income level.
 *
 * <p>
 * Note: This class reflects the tax brackets and rules effective as of October 22, 2024,
 * and applicable to the 2025 tax year per the IRS Revenue Procedure. If the tax code is
 * amended after that date, results may not reflect subsequent changes.
 *
 * @author James Stevens
 * @version 2025.1
 */
public class TaxTableCalculator2025 {

    // Constants representing different tax rates
    private static final double PERCENTAGE_10 = 0.10;
    private static final double PERCENTAGE_12 = 0.12;
    private static final double PERCENTAGE_22 = 0.22;
    private static final double PERCENTAGE_24 = 0.24;
    private static final double PERCENTAGE_32 = 0.32;
    private static final double PERCENTAGE_35 = 0.35;
    private static final double PERCENTAGE_37 = 0.37;

    // The taxpayer's filing status (represented by an integer 1–5)
    private final FilingStatus filingStatus;

    // The taxpayer's gross salary (before deductions)
    private final double grossSalary;

    // The calculated tax based on filing status and income
    double taxAmount;

    // A string to store the name of the filing status for display purposes
    String filingStatusName;

    /**
     * Constructs a TaxTableCalculator instance with the given filing status and gross salary.
     *
     * @param filingStatus an integer representing the taxpayer's filing status
     * @param grossSalary the gross salary of the taxpayer
     */
    public TaxTableCalculator2025(FilingStatus filingStatus, double grossSalary) {
        this.filingStatus = filingStatus;
        this.grossSalary = grossSalary;
    } // End TaxTableCalculator constructor

    /**
     * This method determines the tax rate based on the filing status of an individual or entity
     * and their gross income. The method uses a switch statement to select the appropriate
     * tax calculation method based on the filing status:
     * <p>
     * - Case 1: Calls the calculateSingle() method for unmarried individuals.
     * - Case 2: Calls the calculateHeadOfHousehold() method for head of household filings.
     * - Case 3: Calls the calculateMarriedFilingSeparate() method for married individuals filing separately.
     * - Case 4: Calls the calculateMarriedFilingJointlySurvivingSpouse() method for married individuals filing jointly or surviving spouses.
     * - Case 5: Calls the calculateEstatesTrusts() method for estates and trusts.
     * <p>
     * After calculating the tax, returns the tax amount.
     *
     */
    public double calculateTax() {
        switch (filingStatus) {
            case SINGLE -> {
                calculateSingle();
                filingStatusName = "being an Unmarried Individual";
            }
            case HEAD_OF_HOUSEHOLD -> {
                calculateHeadOfHousehold();
                filingStatusName = "being the Head of Household";
            }
            case MARRIED_FILING_SEPARATELY -> {
                calculateMarriedFilingSeparate();
                filingStatusName = "being Married Individuals Filing Separate Returns";
            }
            case MARRIED_FILING_JOINTLY, QUALIFYING_SURVIVING_SPOUSE -> {
                calculateMarriedFilingJointlySurvivingSpouse();
                filingStatusName = "being Married Individuals Filing Joint Returns or Surviving Spouses";
            }
            case ESTATES_AND_TRUSTS -> {
                calculateEstatesTrusts();
                filingStatusName = "the Estates and Trusts";
            }
        }
        return taxAmount;
    }


    /**
     * This method calculates the tax rate for single individuals based on their gross income.
     * The tax is calculated using a progressive tax structure with different portions of the income
     * taxed at different rates:
     * - 10% for income up to $11,925
     * - 12% for income between $11,925 and $48,475
     * - 22% for income between $48,475 and $103,350
     * - 24% for income between $103,350 and $197,300
     * - 32% for income between $197,300 and $250,525
     * - 35% for income between $250,525 and $626,350
     * - 37% for income above $626,350
     * <p>
     * The method calculates the total tax by applying the applicable tax rate to the income
     * in each tax bracket. For each applicable bracket, it calculates the base tax for
     * the lower brackets and then adds the tax for the income in the current tax bracket.
     *
     */
    public void calculateSingle(){
        if (grossSalary <= 11925){
            taxAmount = (grossSalary * PERCENTAGE_10);
        } else if (grossSalary > 11925 && grossSalary <= 48475) {
            double baseTax = (11925 * PERCENTAGE_10);
            taxAmount =  baseTax + ((grossSalary - 11925) * PERCENTAGE_12);
        } else if (grossSalary > 48475 && grossSalary <= 103350){
            double baseTax = (11925 * PERCENTAGE_10) + ((48475 - 11925) * PERCENTAGE_12);
            taxAmount = baseTax + ((grossSalary - 48475) * PERCENTAGE_22);
        } else if (grossSalary > 103350 && grossSalary <= 197300) {
            double baseTax = (11925 * PERCENTAGE_10) + ((48475 - 11925) * PERCENTAGE_12)
                    + ((103350 - 48475) * PERCENTAGE_22);
            taxAmount = baseTax + ((grossSalary - 103350) * PERCENTAGE_24);
        } else if (grossSalary > 197300 && grossSalary <= 250525){
            double baseTax = (11925 * PERCENTAGE_10) + ((48475 - 11925) * PERCENTAGE_12)
                    + ((103350 - 48475) * PERCENTAGE_22) + ((197300 - 103350) * PERCENTAGE_24);
            taxAmount = baseTax + ((grossSalary - 197300) * PERCENTAGE_32);
        } else if (grossSalary > 250525 && grossSalary <= 626350){
            double baseTax = (11925 * PERCENTAGE_10) + ((48475 - 11925) * PERCENTAGE_12)
                    + ((103350 - 48475) * PERCENTAGE_22) + ((197300 - 103350) * PERCENTAGE_24)
                    + ((250525 - 197300) * PERCENTAGE_32);
            taxAmount = baseTax + ((grossSalary - 250525) * PERCENTAGE_35);
        } else if (grossSalary > 626350){
            double baseTax = (11925 * PERCENTAGE_10) + ((48475 - 11925) * PERCENTAGE_12)
                    + ((103350 - 48475) * PERCENTAGE_22) + ((197300 - 103350) * PERCENTAGE_24)
                    + ((250525 - 197300) * PERCENTAGE_32) + ((626350 - 250525) * PERCENTAGE_35);
            taxAmount = baseTax + ((grossSalary - 626350) * PERCENTAGE_37);
        } // End if-else statements
    } // End calculateSingle method

    /**
     * This method calculates the tax rate for individuals filing as head of household based
     * on their gross income. The tax is calculated using a progressive tax structure with
     * different portions of the income taxed at different rates:
     * - 10% for income up to $17,000
     * - 12% for income between $17,000 and $64,850
     * - 22% for income between $64,850 and $103,350
     * - 24% for income between $103,350 and $197,300
     * - 32% for income between $197,300 and $250,500
     * - 35% for income between $250,500 and $626,350
     * - 37% for income above $626,350
     * <p>
     * The method calculates the total tax by applying the applicable tax rate to the income
     * in each tax bracket. For each applicable range, it first calculates the base tax for
     * the lower tax brackets and then adds the tax for the income in the current tax bracket.
     *
     */
    public void calculateHeadOfHousehold(){
        if (grossSalary <= 17000){
            taxAmount = (grossSalary * PERCENTAGE_10);
        } else if (grossSalary > 17000 && grossSalary <= 64850){
            double baseTax = (17000 * PERCENTAGE_10);
            taxAmount = baseTax + ((grossSalary - 17000) * PERCENTAGE_12);
        } else if (grossSalary > 64850 && grossSalary <= 103350){
            double baseTax = (17000 * PERCENTAGE_10) + ((64850 - 17000) * PERCENTAGE_12);
            taxAmount = baseTax + ((grossSalary - 64850) * PERCENTAGE_22);
        } else if (grossSalary > 103350 && grossSalary <= 197300){
            double baseTax = (17000 * PERCENTAGE_10) + ((64850 - 17000) * PERCENTAGE_12)
                    + ((103350 - 64850) * PERCENTAGE_22);
            taxAmount = baseTax + ((grossSalary - 103350) * PERCENTAGE_24);
        } else if (grossSalary > 197300 && grossSalary <= 250500){
            double baseTax = (17000 * PERCENTAGE_10) + ((64850 - 17000) * PERCENTAGE_12)
                    + ((103350 - 64850) * PERCENTAGE_22) + ((197300 - 103350) * PERCENTAGE_24);
            taxAmount = baseTax + ((grossSalary - 197300) * PERCENTAGE_32);
        } else if (grossSalary > 250500 && grossSalary <= 626350){
            double baseTax = (17000 * PERCENTAGE_10) + ((64850 - 17000) * PERCENTAGE_12)
                    + ((103350 - 64850) * PERCENTAGE_22) + ((197300 - 103350) * PERCENTAGE_24)
                    + ((250500 - 197300) * PERCENTAGE_32);
            taxAmount = baseTax + ((grossSalary - 250500) * PERCENTAGE_35);
        } else if (grossSalary > 626350){
            double baseTax = (17000 * PERCENTAGE_10) + ((64850 - 17000) * PERCENTAGE_12)
                    + ((103350 - 64850) * PERCENTAGE_22) + ((197300 - 103350) * PERCENTAGE_24)
                    + ((250500 - 197300) * PERCENTAGE_32) + ((626350 - 250500) * PERCENTAGE_35);
            taxAmount = baseTax + ((grossSalary - 626350) * PERCENTAGE_37);
        } // End if-else statements
    } // End calculateHeadHousehold method

    /**
     * This method calculates the tax rate for individuals filing separately as married couples
     * based on their gross income. The tax is calculated using a progressive tax structure with
     * different portions of the income taxed at different rates:
     * - 10% for income up to $11,925
     * - 12% for income between $11,925 and $48,475
     * - 22% for income between $48,475 and $103,350
     * - 24% for income between $103,350 and $197,300
     * - 32% for income between $197,300 and $250,525
     * - 35% for income between $250,525 and $375,800
     * - 37% for income above $375,800
     * <p>
     * The method computes the tax by applying the applicable tax rate to portions of the income
     * based on the above brackets. For each range, it calculates the base tax for the income
     * in the lower bracket and then adds the tax for the income in the current bracket.
     *
     */
    public void calculateMarriedFilingSeparate(){
        if (grossSalary <= 11925){
            taxAmount = (grossSalary * PERCENTAGE_10);
        } else if (grossSalary > 11925 && grossSalary <= 48475) {
            double baseTax = (11925 * PERCENTAGE_10);
            taxAmount = baseTax + ((grossSalary - 11925) * PERCENTAGE_12);
        } else if (grossSalary > 48475 && grossSalary <= 103350){
            double baseTax = (11925 * PERCENTAGE_10) + ((48475 - 11925) * PERCENTAGE_12);
            taxAmount = baseTax + ((grossSalary - 48475) * PERCENTAGE_22);
        } else if (grossSalary > 103350 && grossSalary <= 197300) {
            double baseTax = (11925 * PERCENTAGE_10) + ((48475 - 11925) * PERCENTAGE_12)
                    + ((103350 - 48475) * PERCENTAGE_22);
            taxAmount = baseTax + ((grossSalary - 103350) * PERCENTAGE_24);
        } else if (grossSalary > 197300 && grossSalary <= 250525){
            double baseTax = (11925 * PERCENTAGE_10) + ((48475 - 11925) * PERCENTAGE_12)
                    + ((103350 - 48475) * PERCENTAGE_22) + ((197300 - 103350) * PERCENTAGE_24);
            taxAmount = baseTax + ((grossSalary - 197300) * PERCENTAGE_32);
        } else if (grossSalary > 250525 && grossSalary <= 375800){
            double baseTax = (11925 * PERCENTAGE_10) + ((48475 - 11925) * PERCENTAGE_12)
                    + ((103350 - 48475) * PERCENTAGE_22) + ((197300 - 103350) * PERCENTAGE_24)
                    + ((250525 - 197300) * PERCENTAGE_32);
            taxAmount = baseTax + ((grossSalary - 250525) * PERCENTAGE_35);
        } else if (grossSalary > 375800){
            double baseTax = (11925 * PERCENTAGE_10) + ((48475 - 11925) * PERCENTAGE_12)
                    + ((103350 - 48475) * PERCENTAGE_22) + ((197300 - 103350) * PERCENTAGE_24)
                    + ((250525 - 197300) * PERCENTAGE_32) + ((375800 - 250525) * PERCENTAGE_35);
            taxAmount = baseTax + ((grossSalary - 375800) * PERCENTAGE_37);
        } // End if-else statements
    } // End calculateMarriedFilingSeparate method

    /**
     * This method calculates the tax rate for individuals filing jointly as a married couple
     * or for surviving spouses based on their gross income. The tax is calculated in progressive
     * tiers, where different portions of the income are taxed at different rates:
     * - 10% for income up to $23,850
     * - 12% for income between $23,850 and $96,950
     * - 22% for income between $96,950 and $206,700
     * - 24% for income between $206,700 and $394,600
     * - 32% for income between $394,600 and $501,500
     * - 35% for income between $501,500 and $751,600
     * - 37% for income above $751,600
     * <p>
     * The final tax rate is the sum of the taxes from each applicable tax bracket based on the
     * gross income provided. The method uses a series of conditional statements to determine
     * which tax brackets apply to the given gross income and calculate the tax accordingly.
     *
     */
    public void calculateMarriedFilingJointlySurvivingSpouse() {
        if (grossSalary <= 23850){
            taxAmount = (grossSalary * PERCENTAGE_10);
        } else if (grossSalary > 23850 && grossSalary <= 96950){
            double baseTax = (23850 * PERCENTAGE_10);
            taxAmount = baseTax + ((grossSalary - 23850) * PERCENTAGE_12);
        } else if (grossSalary > 96950 && grossSalary <= 206700){
            double baseTax = (23850 * PERCENTAGE_10) + ((96950 - 23850) * PERCENTAGE_12);
            taxAmount = baseTax + ((grossSalary - 96950) * PERCENTAGE_22);
        } else if (grossSalary > 206700 && grossSalary <= 394600){
            double baseTax = (23850 * PERCENTAGE_10) + ((96950 - 23850) * PERCENTAGE_12)
                    + ((206700 - 96950) * PERCENTAGE_22);
            taxAmount = baseTax + ((grossSalary - 206700) * PERCENTAGE_24);
        } else if (grossSalary > 394600 && grossSalary <= 501500){
            double baseTax = (23850 * PERCENTAGE_10) + ((96950 - 23850) * PERCENTAGE_12)
                    + ((206700 - 96950) * PERCENTAGE_22) + ((394600 - 206700) * PERCENTAGE_24);
            taxAmount = baseTax + ((grossSalary - 394600) * PERCENTAGE_32);
        } else if (grossSalary > 501500 && grossSalary <=751600){
            double baseTax = (23850 * PERCENTAGE_10) + ((96950 - 23850) * PERCENTAGE_12)
                    + ((206700 - 96950) * PERCENTAGE_22) + ((394600 - 206700) * PERCENTAGE_24)
                    + ((501500 - 394600) * PERCENTAGE_32);
            taxAmount = baseTax + ((grossSalary - 501500) * PERCENTAGE_35);
        } else if (grossSalary > 751600) {
            double baseTax = (23850 * PERCENTAGE_10) + ((96950 - 23850) * PERCENTAGE_12)
                    + ((206700 - 96950) * PERCENTAGE_22) + ((394600 - 206700) * PERCENTAGE_24)
                    + ((501500 - 394600) * PERCENTAGE_32) + ((751600 - 501500) * PERCENTAGE_35);
            taxAmount = baseTax + ((grossSalary - 751600) * PERCENTAGE_37);
        } // End if-else statements
    } // calculateMarriedJointlySurvivingSpouse method

    /**
     * This method calculates the tax rate for estates and trusts based on the gross income.
     * The tax is calculated in tiers, where different portions of the income are taxed at
     * different rates:
     * - 10% for income up to $3150
     * - 24% for income between $3150 and $11,450
     * - 35% for income between $11,450 and $15,650
     * - 35% for income above $15,650
     * <p>
     * The final tax rate is the sum of the taxes from each applicable tier based on the
     * gross income provided.
     *
     */
    public void calculateEstatesTrusts(){
        if (grossSalary <= 3150){
            taxAmount = (grossSalary * PERCENTAGE_10);
        } else if (grossSalary > 3150 && grossSalary <= 11450){
            double baseTax = (3150 * PERCENTAGE_10);
            taxAmount = baseTax + ((grossSalary - 3150) * PERCENTAGE_24);
        } else if (grossSalary > 11450 && grossSalary <= 15650){
            double baseTax = (3150 * PERCENTAGE_10) + ((11450 - 3150) * PERCENTAGE_24);
            taxAmount = baseTax + ((grossSalary - 11450) * PERCENTAGE_35);
        } else if (grossSalary > 15650){
            double baseTax = (3150 * PERCENTAGE_10) + ((11450 - 3150) * PERCENTAGE_24)
                    + ((15650 - 11450) * PERCENTAGE_35);
            taxAmount = baseTax + ((grossSalary - 15650) * PERCENTAGE_35);
        }  // End if-else statements
    } // End EstatesTrusts method

} // End TaxTableCalculator class
