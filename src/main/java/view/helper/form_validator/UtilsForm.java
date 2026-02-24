package view.helper.form_validator;

import javafx.beans.value.ObservableValue;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.scene.control.*;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import validation.rule.ValidationRule;

/**
 * Classe utilitaire pour la validation des formulaires JavaFX.
 * <p>
 * Fournit des méthodes pour binder des champs (TextField, DatePicker, ComboBox) à des règles de validation
 * et pour générer des bindings booléens représentant l'état global du formulaire.
 * </p>
 */
public class UtilsForm {

    /**
     * Lie un ObservableValue à une règle de validation.
     * <p>
     * Lorsque la valeur change, toutes les règles sont appliquées successivement.
     * Si une règle échoue, le message d'erreur est affiché et le style du champ est mis à jour.
     * </p>
     *
     * @param observable Observable à surveiller
     * @param errorLabel Label où afficher le message d'erreur
     * @param getValue Fonction pour convertir la valeur observée en String
     * @param setStyle Fonction pour appliquer le style CSS au champ
     * @param rules Liste des règles de validation à appliquer
     */
    private static void bindObservable(ObservableValue<?> observable,
                                       Label errorLabel,
                                       Function<Object,String> getValue,
                                       Consumer<String> setStyle,
                                       ValidationRule... rules) {
        observable.addListener((obs, oldVal, newVal) -> {
            String value = getValue.apply(newVal);
            for (ValidationRule rule : rules) {
                String msg = rule.validate(value);
                if (msg != null) {
                    errorLabel.setText(msg);
                    setStyle.accept("-fx-border-color: #e74c3c");
                    return;
                }
            }
            errorLabel.setText("");
            setStyle.accept("-fx-border-color: #3498db");
        });
    }

    /**
     * Lie un TextField à des règles de validation.
     *
     * @param field TextField à valider
     * @param label Label pour afficher les erreurs
     * @param rules Règles de validation
     */
    public static void bindField(TextField field, Label label, ValidationRule... rules) {
        bindObservable(field.textProperty(), label, val -> val == null ? "" : val.toString(),
                field::setStyle, rules);
    }

    /**
     * Lie un DatePicker à des règles de validation.
     *
     * @param dp DatePicker à valider
     * @param label Label pour afficher les erreurs
     * @param rules Règles de validation
     */
    public static void bindDatePicker(DatePicker dp, Label label, ValidationRule... rules) {
        bindObservable(dp.valueProperty(), label, val -> val == null ? "" : val.toString(),
                dp::setStyle, rules);
    }

    /**
     * Lie un ComboBox à des règles de validation.
     *
     * @param cb ComboBox à valider
     * @param label Label pour afficher les erreurs
     * @param rules Règles de validation
     * @param <T> Type des éléments du ComboBox
     */
    public static <T> void bindComboBox(ComboBox<T> cb, Label label, ValidationRule... rules) {
        bindObservable(cb.getSelectionModel().selectedItemProperty(), label, val -> val == null ? "" : val.toString(),
                cb::setStyle, rules);
    }

    /**
     * Crée un BooleanBinding représentant l'état global du formulaire.
     * <p>
     * Le binding est recalculé à chaque changement d'une des observables fournies.
     * </p>
     *
     * @param validator Fonction qui retourne true si le formulaire est valide, false sinon
     * @param observables Observables à surveiller pour recalculer le binding
     * @return BooleanBinding reflétant l'état du formulaire
     */
    public static BooleanBinding createFormBinding(
            Supplier<Boolean> validator,
            ObservableValue<?>... observables
    ) {
        return Bindings.createBooleanBinding(
                validator::get,
                observables
        );
    }

}