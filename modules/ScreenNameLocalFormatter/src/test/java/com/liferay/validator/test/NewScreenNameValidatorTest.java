package com.liferay.validator.test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.liferay.validator.NewScreenNameValidator;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class NewScreenNameValidatorTest {	
	@Mock
	NewScreenNameValidator newScreenNameValidatorMock;
	
	@Test
	public void validate_ReturnsFalse_When_StrDoesNotEndsWithCorrectEmail() {
		Mockito.when(newScreenNameValidatorMock.getCorrectEmail()).thenReturn("@correct.com");
		Mockito.when(newScreenNameValidatorMock.validate(Mockito.anyLong(), Mockito.anyString())).thenCallRealMethod();
		
		boolean actual = newScreenNameValidatorMock.validate(0L, "joao@email.com");

		assertFalse(actual);
	}
	
	@Test
	public void validate_ReturnsTrue_When_StrEndsWithCorrectEmail() {
		
		NewScreenNameValidator newScreenNameValidator = Mockito.mock(NewScreenNameValidator.class);
		Mockito.when(newScreenNameValidatorMock.getCorrectEmail()).thenReturn("@correct.com");
		Mockito.when(newScreenNameValidatorMock.validate(Mockito.anyLong(), Mockito.anyString())).thenCallRealMethod();
		
		boolean actual = newScreenNameValidatorMock.validate(0L, "joao@correct.com");

		assertTrue(actual);
	}
}
