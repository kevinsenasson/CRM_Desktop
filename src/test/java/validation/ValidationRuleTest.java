package validation;

import org.junit.jupiter.api.Test;
import validation.rule.*;

import static org.junit.jupiter.api.Assertions.*;

class ValidationRulesTest {

    // =========================
    // AmountRule
    // =========================
    @Test
    void amountRule_should_return_true_for_positive_numbers() {
        assertTrue(AmountRule.isPositiveAmount("123", "\\d+(\\.\\d+)?"));
        assertTrue(AmountRule.isPositiveAmount("123.45", "\\d+(\\.\\d+)?"));
        assertTrue(AmountRule.isPositiveAmount("123,45", "\\d+(,\\d+)?")); // , support
    }

    @Test
    void amountRule_should_return_false_for_zero_or_negative_or_invalid() {
        assertFalse(AmountRule.isPositiveAmount("0", "\\d+(\\.\\d+)?"));
        assertFalse(AmountRule.isPositiveAmount("-5", "-?\\d+(\\.\\d+)?"));
        assertFalse(AmountRule.isPositiveAmount("abc", "\\d+(\\.\\d+)?"));
        assertFalse(AmountRule.isPositiveAmount("", "\\d+(\\.\\d+)?"));
        assertFalse(AmountRule.isPositiveAmount(null, "\\d+(\\.\\d+)?"));
    }

    // =========================
    // LengthRule
    // =========================
    @Test
    void lengthRule_should_return_null_for_valid_length() {
        ValidationRule rule = LengthRule.length(2, 5);
        assertNull(rule.validate("abc"));
    }

    @Test
    void lengthRule_should_return_error_messages() {
        ValidationRule rule = LengthRule.length(2, 5);
        assertEquals("Doit contenir au moins 2 caractères", rule.validate("a"));
        assertEquals("Nombre de caractères maximal : 5", rule.validate("abcdef"));
    }

    @Test
    void lengthRule_named_should_return_error_messages() {
        ValidationRule rule = LengthRule.length(2, 5, "Nom");
        assertEquals("Nom doit contenir au moins 2 caractères", rule.validate("a"));
        assertEquals("Nom ne doit pas dépasser 5 caractères", rule.validate("abcdef"));
    }

    @Test
    void lengthRule_isNotBlankMin_and_isMaxLength() {
        assertTrue(LengthRule.isNotBlankMin("abc", 2));
        assertFalse(LengthRule.isNotBlankMin("a", 2));
        assertTrue(LengthRule.isMaxLength("abc", 5));
        assertFalse(LengthRule.isMaxLength("abcdef", 5));
    }

    // =========================
    // LuhnValidator
    // =========================
    @Test
    void luhnValidator_should_validate_correct_siret() {
        // exemple valide : 73282932000074
        assertTrue(LuhnValidator.isSiretValidForLuhn("73282932000074"));
    }

    @Test
    void luhnValidator_should_invalidate_wrong_siret() {
        assertFalse(LuhnValidator.isSiretValidForLuhn("73282932000000"));
    }

    // =========================
    // SiretRule
    // =========================
    @Test
    void siretRule_should_return_null_for_valid_siret() {
        ValidationRule rule = SiretRule.siret();
        assertNull(rule.validate("73282932000074"));
    }

    @Test
    void siretRule_should_return_error_messages() {
        ValidationRule rule = SiretRule.siret();
        assertEquals("Le SIRET doit contenir 14 chiffres", rule.validate("123"));
        assertEquals("Le SIRET est invalide", rule.validate("73282932000000"));
        assertNull(rule.validate(null)); // accepte null
        assertNull(rule.validate(""));   // accepte vide
    }

    // =========================
    // MandatoryRule
    // =========================
    @Test
    void mandatoryRule_should_return_message_when_blank_or_null() {
        ValidationRule rule = MandatoryRule.mandatory("Champ obligatoire");
        assertEquals("Champ obligatoire", rule.validate(null));
        assertEquals("Champ obligatoire", rule.validate(""));
        assertEquals("Champ obligatoire", rule.validate("   "));
        assertNull(rule.validate("valeur"));
    }

    // =========================
    // RegexRule
    // =========================
    @Test
    void regexRule_should_return_null_for_matching_value() {
        ValidationRule rule = RegexRule.regex("\\d+", "Doit être un nombre");
        assertNull(rule.validate("123"));
        assertNull(rule.validate(null));
        assertNull(rule.validate(""));
        assertNull(rule.validate("   "));
    }

    @Test
    void regexRule_should_return_message_for_non_matching_value() {
        ValidationRule rule = RegexRule.regex("\\d+", "Doit être un nombre");
        assertEquals("Doit être un nombre", rule.validate("abc"));
    }

    @Test
    void regexRule_matches_static_method() {
        assertTrue(RegexRule.matches(null, "\\d+"));
        assertTrue(RegexRule.matches("", "\\d+"));
        assertTrue(RegexRule.matches("   ", "\\d+"));
        assertTrue(RegexRule.matches("123", "\\d+"));
        assertFalse(RegexRule.matches("abc", "\\d+"));
    }
}