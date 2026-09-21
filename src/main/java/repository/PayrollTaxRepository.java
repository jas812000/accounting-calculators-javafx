package repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import model.PayrollTaxData;
import model.PayrollTaxDataIndex;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * Loads bundled year-specific payroll tax data.
 */
public final class PayrollTaxRepository {

    private static final String ROOT = "/payroll-taxes/";
    private static final ObjectMapper MAPPER = new ObjectMapper();

    private final PayrollTaxDataIndex index;

    public PayrollTaxRepository() {
        this.index = read(
                ROOT + "index.json",
                PayrollTaxDataIndex.class
        );
    }

    public List<Integer> getSupportedYears() {
        return index.supportedYears();
    }

    public PayrollTaxData load(int year) {
        if (!index.supportedYears().contains(year)) {
            throw new IllegalArgumentException(
                    "Payroll tax data is not available for tax year "
                            + year
            );
        }

        PayrollTaxData data = read(
                ROOT + year + ".json",
                PayrollTaxData.class
        );

        if (data.taxYear() != year) {
            throw new IllegalStateException(
                    "Payroll tax resource year does not match "
                            + "requested year " + year
            );
        }

        return data;
    }

    private static <T> T read(
            String resource,
            Class<T> type
    ) {
        try (InputStream input =
                     PayrollTaxRepository.class
                             .getResourceAsStream(resource)) {

            if (input == null) {
                throw new IllegalStateException(
                        "Missing payroll tax resource: " + resource
                );
            }

            return MAPPER.readValue(input, type);

        } catch (IOException exception) {
            throw new IllegalStateException(
                    "Unable to read payroll tax resource: " + resource,
                    exception
            );
        }
    }
}
