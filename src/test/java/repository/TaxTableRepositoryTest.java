package repository;

import model.TaxYearData;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link TaxTableRepository}.
 *
 * Verifies supported-year discovery, loading of versioned tax data,
 * required filing-status schedules, filing-status mappings, and
 * rejection of unsupported tax years.
 */
class TaxTableRepositoryTest {

    private final TaxTableRepository repository = new TaxTableRepository();

    /**
     * Verifies that the repository exposes every tax year listed in the
     * bundled tax-data index.
     */
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

    /**
     * Verifies that every supported tax year can be loaded and contains
     * at least one tax schedule.
     */
    @Test
    void loadsEverySupportedTaxYear() {
        for (int year = 2020; year <= 2026; year++) {
            TaxYearData data = repository.load(year);

            assertNotNull(data);
            assertEquals(year, data.taxYear());
            assertFalse(data.schedules().isEmpty());
        }
    }

    /**
     * Verifies that the 2026 data contains all supported tax schedules.
     */
    @Test
    void loadsExpectedTaxSchedules() {
        TaxYearData data = repository.load(2026);

        assertTrue(data.schedules().containsKey("SINGLE"));
        assertTrue(data.schedules().containsKey("MARRIED_FILING_JOINTLY"));
        assertTrue(data.schedules().containsKey("MARRIED_FILING_SEPARATELY"));
        assertTrue(data.schedules().containsKey("HEAD_OF_HOUSEHOLD"));
        assertTrue(data.schedules().containsKey("ESTATES_AND_TRUSTS"));
    }

    /**
     * Verifies that qualifying surviving spouse status maps to the
     * married-filing-jointly tax schedule.
     */
    @Test
    void loadsQualifyingSurvivingSpouseMapping() {
        TaxYearData data = repository.load(2026);

        assertEquals(
                "MARRIED_FILING_JOINTLY",
                data.filingStatusMappings().get("QUALIFYING_SURVIVING_SPOUSE")
        );
    }

    /**
     * Verifies that a tax year not present in the bundled data is rejected.
     */
    @Test
    void rejectsUnsupportedTaxYear() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> repository.load(2019)
        );

        assertEquals("Unsupported tax year: 2019", exception.getMessage());
    }
}
