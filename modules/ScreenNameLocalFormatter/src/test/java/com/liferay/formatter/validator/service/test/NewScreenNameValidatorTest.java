/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */

package com.liferay.formatter.validator.service.test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import static org.mockito.ArgumentMatchers.anyString;

import static org.powermock.api.mockito.PowerMockito.doReturn;
import static org.powermock.api.mockito.PowerMockito.spy;

import com.liferay.formatter.validator.service.NewScreenNameValidator;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

/**
 * @author leonardo.ferreira
 */
@PrepareForTest(NewScreenNameValidator.class)
@RunWith(PowerMockRunner.class)
public class NewScreenNameValidatorTest {

	@Before
	public void setup() throws Exception {
		_newScreenNameValidator = spy(new NewScreenNameValidator());

		doReturn(
			_correctEmail
		).when(
			_newScreenNameValidator, "_getCorrectEmail"
		);
	}

	@Test
	public void validate_ReturnsFalse_When_StrDoesNotEndsWithCorrectEmail()
		throws Exception {

		doReturn(
			false
		).when(
			_newScreenNameValidator, "hasInvalidChars", anyString()
		);

		String screenName = "joao@email.com";

		boolean actual = _newScreenNameValidator.validate(0L, screenName);

		assertFalse(actual);
	}

	@Test
	public void validate_ReturnsFalse_When_StrEndsWithNoEmail()
		throws Exception {

		doReturn(
			false
		).when(
			_newScreenNameValidator, "hasInvalidChars", anyString()
		);

		String screenName = "joao";

		boolean actual = _newScreenNameValidator.validate(0L, screenName);

		assertFalse(actual);
	}

	@Test
	public void validate_ReturnsFalse_When_StrHasInvalidCaracter()
		throws Exception {

		String screenName = "joao/" + _correctEmail;

		doReturn(
			true
		).when(
			_newScreenNameValidator, "hasInvalidChars", anyString()
		);

		boolean actual = _newScreenNameValidator.validate(0L, screenName);

		assertFalse(actual);
	}

	@Test
	public void validate_ReturnsTrue_When_StrEndsWithCorrectEmail()
		throws Exception {

		String screenName = "joao" + _correctEmail;

		doReturn(
			false
		).when(
			_newScreenNameValidator, "hasInvalidChars", anyString()
		);

		boolean actual = _newScreenNameValidator.validate(0L, screenName);

		assertTrue(actual);
	}

	private String _correctEmail = "@company.com";
	private NewScreenNameValidator _newScreenNameValidator;

}