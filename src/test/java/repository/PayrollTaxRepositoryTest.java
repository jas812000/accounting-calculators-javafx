package repository;

import model.PayrollTaxData;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link PayrollTaxRepository}.
 *
 * Verifies supported payroll-tax years, loading of FICA rates and limits,
 * and rejection of unsupported future years.
 */
class PayrollTaxRepositoryTest {

    private final PayrollTaxRepository repository =
            new PayrollTaxRepository();

    /**
     * Verifies that the payroll-tax index currently exposes 2026.
     */
    @Test
    void supports2026() {
        assertEquals(
                List.of(2026),
                repository.getSupportedYears()
        );
    }

    /**
     * Verifies that the bundled 2026 payroll-tax data contains the expected
     * Social Security, Medicare, and Additional Medicare values.
     */
    @Test
    void loads2026PayrollTaxData() {
        PayrollTaxData data = repository.load(2026);

        assertEquals(2026, data.taxYear());
        assertEquals(0.062, data.socialSecurityRate(), 0.000001);
        assertEquals(
                184_500,
                data.socialSecurityWageBase(),
                0.001
        );
        assertEquals(0.0145, data.medicareRate(), 0.000001);
        assertEquals(
                0.009,
                data.additionalMedicareRate(),
                0.000001
        );
        assertEquals(
                200_000,
                data.additionalMedicareThreshold(),
                0.001
        );
    }

    /**
     * Verifies that payroll-tax data is not fabricated or reused for an
     * unsupported future year.
     */
    @Test
    void rejectsUnsupportedFutureYear() {
        assertThrows(
                IllegalArgumentException.class,
                () -> repository.load(2027)
        );
    }
}
