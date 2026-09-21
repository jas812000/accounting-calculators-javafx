package repository;

import model.TaxYearData;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TaxTableRepositoryTest {

    private final TaxTableRepository repository = new TaxTableRepository();

    @Test
    void returnsSupportedTaxYearsFromIndex() {
        assertEquals(
                java.util.List.of(
                        2020,
                        2021,
                        2022,
                        2023,
                        2024,
                        2025,
                        2026
                ),
                repository.getSupportedYears()
        );
    }

    @Test
    void loadsEverySupportedTaxYear() {
        for (int year = 2020; year <= 2026; year++) {
            TaxYearData data = repository.load(year);

            assertNotNull(data);
            assertEquals(year, data.taxYear());
            assertFalse(data.schedules().isEmpty());
        }
    }

    @Test
    void loadsExpectedTaxSchedules() {
        TaxYearData data = repository.load(2026);

        assertTrue(data.schedules().containsKey("SINGLE"));
        assertTrue(data.schedules().containsKey("MARRIED_FILING_JOINTLY"));
        assertTrue(data.schedules().containsKey("MARRIED_FILING_SEPARATELY"));
        assertTrue(data.schedules().containsKey("HEAD_OF_HOUSEHOLD"));
        assertTrue(data.schedules().containsKey("ESTATES_AND_TRUSTS"));
    }

    @Test
    void loadsQualifyingSurvivingSpouseMapping() {
        TaxYearData data = repository.load(2026);

        assertEquals(
                "MARRIED_FILING_JOINTLY",
                data.filingStatusMappings().get("QUALIFYING_SURVIVING_SPOUSE")
        );
    }

    @Test
    void rejectsUnsupportedTaxYear() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> repository.load(2019)
        );

        assertEquals("Unsupported tax year: 2019", exception.getMessage());
    }
}
