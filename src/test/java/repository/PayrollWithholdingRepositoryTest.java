package repository;

import model.PayrollWithholdingData;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link PayrollWithholdingRepository}.
 *
 * Verifies supported withholding years, pay-period configuration,
 * filing-status schedules, and rejection of unsupported future years.
 */
class PayrollWithholdingRepositoryTest {

    private final PayrollWithholdingRepository repository =
            new PayrollWithholdingRepository();

    /**
     * Verifies that the withholding index currently exposes 2026.
     */
    @Test
    void returnsSupportedYearsFromIndex() {
        assertEquals(
                List.of(2026),
                repository.getSupportedYears()
        );
    }

    /**
     * Verifies that the 2026 withholding data loads with the expected
     * number of pay periods for each supported schedule.
     */
    @Test
    void loads2026Data() {
        PayrollWithholdingData data = repository.load(2026);

        assertEquals(2026, data.taxYear());
        assertEquals(52, data.payPeriodsPerYear().get("WEEKLY"));
        assertEquals(26, data.payPeriodsPerYear().get("BI_WEEKLY"));
        assertEquals(12, data.payPeriodsPerYear().get("MONTHLY"));
    }

    /**
     * Verifies that the 2026 standard and Step 2 withholding data contain
     * the expected filing-status schedules.
     */
    @Test
    void containsExpected2026Schedules() {
        PayrollWithholdingData data = repository.load(2026);

        assertTrue(data.standardSchedules().containsKey("SINGLE"));
        assertTrue(data.standardSchedules()
                .containsKey("MARRIED_FILING_JOINTLY"));
        assertTrue(data.standardSchedules()
                .containsKey("MARRIED_FILING_SEPARATELY"));
        assertTrue(data.standardSchedules()
                .containsKey("HEAD_OF_HOUSEHOLD"));

        assertEquals(4, data.standardSchedules().size());
        assertEquals(4, data.step2Schedules().size());
    }

    /**
     * Verifies that a future withholding year is rejected rather than
     * silently falling back to the most recent bundled year.
     */
    @Test
    void rejectsUnsupportedFutureYearRatherThanFallingBack() {
        IllegalArgumentException exception =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> repository.load(2027)
                );

        assertTrue(
                exception.getMessage().contains("2027")
        );
    }
}
