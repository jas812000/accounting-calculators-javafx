package repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import model.TaxDataIndex;
import model.TaxYearData;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * Loads federal income tax data from bundled JSON resources.
 */
public class TaxTableRepository {

    private static final String RESOURCE_DIRECTORY = "/tax-data/";
    private static final String INDEX_RESOURCE =
            RESOURCE_DIRECTORY + "index.json";

    private final ObjectMapper objectMapper;

    public TaxTableRepository() {
        this.objectMapper = new ObjectMapper();
    }

    /**
     * Returns the tax years supported by the bundled tax data.
     *
     * @return immutable list of supported tax years
     * @throws IllegalStateException if the tax-data index cannot be loaded
     */
    public List<Integer> getSupportedYears() {
        try (InputStream inputStream =
                     TaxTableRepository.class.getResourceAsStream(
                             INDEX_RESOURCE
                     )) {

            if (inputStream == null) {
                throw new IllegalStateException(
                        "Tax data index is missing"
                );
            }

            TaxDataIndex index =
                    objectMapper.readValue(inputStream, TaxDataIndex.class);

            return index.supportedYears();

        } catch (IOException exception) {
            throw new IllegalStateException(
                    "Unable to load tax data index",
                    exception
            );
        }
    }

    /**
     * Loads tax data for the requested year.
     *
     * @param year tax year to load
     * @return tax data for the requested year
     * @throws IllegalArgumentException if the tax year is unsupported
     *                                  or its data cannot be loaded
     */
    public TaxYearData load(int year) {
        String resourcePath = RESOURCE_DIRECTORY + year + ".json";

        try (InputStream inputStream =
                     TaxTableRepository.class.getResourceAsStream(
                             resourcePath
                     )) {

            if (inputStream == null) {
                throw new IllegalArgumentException(
                        "Unsupported tax year: " + year
                );
            }

            TaxYearData taxYearData =
                    objectMapper.readValue(inputStream, TaxYearData.class);

            if (taxYearData.taxYear() != year) {
                throw new IllegalArgumentException(
                        "Tax data year does not match requested year: " + year
                );
            }

            return taxYearData;

        } catch (IOException exception) {
            throw new IllegalArgumentException(
                    "Unable to load tax data for year: " + year,
                    exception
            );
        }
    }
}
