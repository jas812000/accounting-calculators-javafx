package repository;

import model.PayrollWithholdingData;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PayrollWithholdingRepositoryTest {

    private final PayrollWithholdingRepository repository =
            new PayrollWithholdingRepository();

    @Test
    void returnsSupportedYearsFromIndex() {
        assertEquals(
                List.of(2026),
                repository.getSupportedYears()
        );
    }

    @Test
    void loads2026Data() {
        PayrollWithholdingData data = repository.load(2026);

        assertEquals(2026, data.taxYear());
        assertEquals(52, data.payPeriodsPerYear().get("WEEKLY"));
        assertEquals(26, data.payPeriodsPerYear().get("BI_WEEKLY"));
        assertEquals(12, data.payPeriodsPerYear().get("MONTHLY"));
    }

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
