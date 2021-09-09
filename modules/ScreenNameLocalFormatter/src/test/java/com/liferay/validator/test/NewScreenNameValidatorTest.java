package com.liferay.validator.test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.liferay.validator.NewScreenNameValidator;

import org.junit.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.Mock;
import org.mockito.Mockito;

public class NewScreenNameValidatorTest {	
	@ParameterizedTest
	@CsvSource({ 
			"'joao@email'",
			"'joaoemail.com'",
			"''",
			"'joão@sd@email.com'"})
	public void validate_ReturnsFalse_When_StrIsNotEmail(String input) {
		NewScreenNameValidator newScreenNameValidator = new NewScreenNameValidator();
		boolean actual = newScreenNameValidator.validate(0L, input);

		assertFalse(actual);
	}
	
	@Test
	public void validate_ReturnsFalse_When_StrDoesNotEndsWithCorrectEmail() {
		
		NewScreenNameValidator newScreenNameValidator = Mockito.mock(NewScreenNameValidator.class);
		Mockito.when(newScreenNameValidator.getCorrectEmail()).thenReturn("@correct.com");
		Mockito.when(newScreenNameValidator.validate(Mockito.anyLong(), Mockito.anyString())).thenCallRealMethod();
		
		boolean actual = newScreenNameValidator.validate(0L, "joao@email.com");

		assertFalse(actual);
	}
	
	@Test
	public void validate_ReturnsTrue_When_StrEndsWithCorrectEmail() {
		
		NewScreenNameValidator newScreenNameValidator = Mockito.mock(NewScreenNameValidator.class);
		Mockito.when(newScreenNameValidator.getCorrectEmail()).thenReturn("@correct.com");
		Mockito.when(newScreenNameValidator.validate(Mockito.anyLong(), Mockito.anyString())).thenCallRealMethod();
		
		boolean actual = newScreenNameValidator.validate(0L, "joao@correct.com");

		assertTrue(actual);
	}
}
