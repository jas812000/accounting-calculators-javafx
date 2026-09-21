package repository;

import model.PayrollTaxData;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PayrollTaxRepositoryTest {

    private final PayrollTaxRepository repository =
            new PayrollTaxRepository();

    @Test
    void supports2026() {
        assertEquals(
                List.of(2026),
                repository.getSupportedYears()
        );
    }

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

    @Test
    void rejectsUnsupportedFutureYear() {
        assertThrows(
                IllegalArgumentException.class,
                () -> repository.load(2027)
        );
    }
}
