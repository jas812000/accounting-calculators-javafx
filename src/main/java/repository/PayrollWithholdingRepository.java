package repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import model.PayrollWithholdingData;
import model.PayrollWithholdingDataIndex;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class PayrollWithholdingRepository {

    private static final String RESOURCE_DIRECTORY =
            "/payroll-withholding/";
    private static final String INDEX_RESOURCE =
            RESOURCE_DIRECTORY + "index.json";

    private final ObjectMapper objectMapper;

    public PayrollWithholdingRepository() {
        objectMapper = new ObjectMapper();
    }

    public List<Integer> getSupportedYears() {
        try (InputStream inputStream =
                     PayrollWithholdingRepository.class
                             .getResourceAsStream(INDEX_RESOURCE)) {

            if (inputStream == null) {
                throw new IllegalStateException(
                        "Payroll withholding data index is missing"
                );
            }

            PayrollWithholdingDataIndex index =
                    objectMapper.readValue(
                            inputStream,
                            PayrollWithholdingDataIndex.class
                    );

            return index.supportedYears();

        } catch (IOException exception) {
            throw new IllegalStateException(
                    "Unable to load payroll withholding data index",
                    exception
            );
        }
    }

    public PayrollWithholdingData load(int year) {
        String resourcePath =
                RESOURCE_DIRECTORY + year + ".json";

        try (InputStream inputStream =
                     PayrollWithholdingRepository.class
                             .getResourceAsStream(resourcePath)) {

            if (inputStream == null) {
                throw new IllegalArgumentException(
                        "Federal withholding data is not available "
                                + "for tax year " + year
                );
            }

            PayrollWithholdingData data =
                    objectMapper.readValue(
                            inputStream,
                            PayrollWithholdingData.class
                    );

            if (data.taxYear() != year) {
                throw new IllegalArgumentException(
                        "Payroll withholding data year does not "
                                + "match requested year: " + year
                );
            }

            return data;

        } catch (IOException exception) {
            throw new IllegalArgumentException(
                    "Unable to load payroll withholding data "
                            + "for year: " + year,
                    exception
            );
        }
    }
}
