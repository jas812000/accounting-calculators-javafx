package model;

import repository.PayrollWithholdingRepository;

import java.util.List;
import java.util.Map;

/**
 * Calculates federal income tax withholding using IRS Publication 15-T
 * Worksheet 1A and the Percentage Method tables for automated payroll
 * systems.
 *
 * This implementation supports Forms W-4 from 2020 or later.
 */
public class FederalWithholdingCalculator {

    private final PayrollWithholdingRepository repository;

    public FederalWithholdingCalculator() {
        this(new PayrollWithholdingRepository());
    }

    public FederalWithholdingCalculator(
            PayrollWithholdingRepository repository
    ) {
        if (repository == null) {
            throw new IllegalArgumentException(
                    "repository is required"
            );
        }

        this.repository = repository;
    }

    public PayrollWithholdingResult calculate(
            PayrollWithholdingInputs inputs
    ) {
        if (inputs == null) {
            throw new IllegalArgumentException(
                    "inputs are required"
            );
        }

        int taxYear = inputs.payDate().getYear();
        PayrollWithholdingData data = repository.load(taxYear);

        Integer payPeriods =
                data.payPeriodsPerYear().get(
                        inputs.paySchedule().name()
                );

        if (payPeriods == null) {
            throw new IllegalArgumentException(
                    "Unsupported pay schedule: "
                            + inputs.paySchedule()
            );
        }

        double annualizedWages =
                inputs.taxableWages() * payPeriods;

        double adjustedBeforeDeductions =
                annualizedWages + inputs.step4aOtherIncome();

        double standardAdjustment =
                inputs.step2Checked()
                        ? 0
                        : inputs.filingStatus()
                                == FilingStatus.MARRIED_FILING_JOINTLY
                                ? 12_900
                                : 8_600;

        double totalDeductions =
                inputs.step4bDeductions()
                        + standardAdjustment;

        double adjustedAnnualWages =
                Math.max(
                        0,
                        adjustedBeforeDeductions
                                - totalDeductions
                );

        Map<String, List<WithholdingBracket>> schedules =
                inputs.step2Checked()
                        ? data.step2Schedules()
                        : data.standardSchedules();

        FilingStatus scheduleStatus =
                inputs.filingStatus()
                        == FilingStatus.QUALIFYING_SURVIVING_SPOUSE
                        ? FilingStatus.MARRIED_FILING_JOINTLY
                        : inputs.filingStatus();

        List<WithholdingBracket> brackets =
                schedules.get(scheduleStatus.name());

        if (brackets == null) {
            throw new IllegalArgumentException(
                    "Unsupported payroll filing status: "
                            + inputs.filingStatus()
            );
        }

        WithholdingBracket bracket =
                findBracket(adjustedAnnualWages, brackets);

        double tentativeAnnualWithholding =
                bracket.baseWithholding()
                        + (adjustedAnnualWages
                        - bracket.minimum())
                        * bracket.rate();

        double tentativePeriodWithholding =
                tentativeAnnualWithholding / payPeriods;

        double creditsPerPeriod =
                inputs.step3Credits() / payPeriods;

        double afterCredits =
                Math.max(
                        0,
                        tentativePeriodWithholding
                                - creditsPerPeriod
                );

        double federalWithholding =
                afterCredits
                        + inputs.step4cAdditionalWithholding();

        return new PayrollWithholdingResult(
                taxYear,
                adjustedAnnualWages,
                tentativeAnnualWithholding,
                federalWithholding
        );
    }

    private WithholdingBracket findBracket(
            double adjustedAnnualWages,
            List<WithholdingBracket> brackets
    ) {
        for (WithholdingBracket bracket : brackets) {
            boolean atOrAboveMinimum =
                    adjustedAnnualWages >= bracket.minimum();

            boolean belowMaximum =
                    bracket.maximum() == null
                            || adjustedAnnualWages
                            < bracket.maximum();

            if (atOrAboveMinimum && belowMaximum) {
                return bracket;
            }
        }

        throw new IllegalStateException(
                "No withholding bracket found for adjusted "
                        + "annual wages: " + adjustedAnnualWages
        );
    }
}
