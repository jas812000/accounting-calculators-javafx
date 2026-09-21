package model;

/**
 * Employee FICA withholding for one paycheck.
 *
 * @param socialSecurity employee Social Security withholding
 * @param medicare employee regular Medicare withholding
 * @param additionalMedicare employee Additional Medicare withholding
 */
public record FicaResult(
        double socialSecurity,
        double medicare,
        double additionalMedicare
) {
    public double total() {
        return socialSecurity + medicare + additionalMedicare;
    }
}
