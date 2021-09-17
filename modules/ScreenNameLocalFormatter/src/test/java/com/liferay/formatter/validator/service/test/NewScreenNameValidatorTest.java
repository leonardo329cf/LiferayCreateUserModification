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

import com.liferay.formatter.validator.service.NewScreenNameValidator;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.mockito.ArgumentMatchers;

import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

/**
 * @author leonardo.ferreira
 */
@PrepareForTest(NewScreenNameValidator.class)
@RunWith(PowerMockRunner.class)
public class NewScreenNameValidatorTest {

	@After
	public void after() {
	}

	@Before
	public void before() throws Exception {
		_newScreenNameValidator = PowerMockito.spy(
			new NewScreenNameValidator());

		PowerMockito.doReturn(
			_correctEmail
		).when(
			_newScreenNameValidator, "_getCorrectEmail"
		);

		PowerMockito.doReturn(
			false
		).when(
			_newScreenNameValidator, "hasInvalidChars",
			ArgumentMatchers.anyString()
		);
	}

	@Test
	public void validate_ReturnsFalse_When_StrDoesNotEndsWithCorrectEmail()
		throws Exception {

		PowerMockito.doReturn(
			false
		).when(
			_newScreenNameValidator, "hasInvalidChars",
			ArgumentMatchers.anyString()
		);

		String screenName = "joao@email.com";

		boolean actual = _newScreenNameValidator.validate(0L, screenName);

		Assert.assertFalse(actual);
	}

	@Test
	public void validate_ReturnsFalse_When_StrEndsWithNoEmail()
		throws Exception {

		PowerMockito.doReturn(
			false
		).when(
			_newScreenNameValidator, "hasInvalidChars",
			ArgumentMatchers.anyString()
		);

		String screenName = "joao";

		boolean actual = _newScreenNameValidator.validate(0L, screenName);

		Assert.assertFalse(actual);
	}

	@Test
	public void validate_ReturnsFalse_When_StrHasInvalidCaracter()
		throws Exception {

		String screenName = "joao/" + _correctEmail;

		PowerMockito.doReturn(
			true
		).when(
			_newScreenNameValidator, "hasInvalidChars",
			ArgumentMatchers.anyString()
		);

		boolean actual = _newScreenNameValidator.validate(0L, screenName);

		Assert.assertFalse(actual);
	}

	@Test
	public void validate_ReturnsTrue_When_StrEndsWithCorrectEmail()
		throws Exception {

		String screenName = "joao" + _correctEmail;

		PowerMockito.doReturn(
			false
		).when(
			_newScreenNameValidator, "hasInvalidChars",
			ArgumentMatchers.anyString()
		);

		boolean actual = _newScreenNameValidator.validate(0L, screenName);

		Assert.assertTrue(actual);
	}

	private String _correctEmail = "@company.com";
	private NewScreenNameValidator _newScreenNameValidator;

}