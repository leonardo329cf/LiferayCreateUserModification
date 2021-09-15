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

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

/**
 * @author leonardo.ferreira
 */
@RunWith(MockitoJUnitRunner.class)
public class NewScreenNameValidatorTest {

	@Test
	public void validate_ReturnsFalse_When_StrDoesNotEndsWithCorrectEmail() {
		Mockito.when(
			_newScreenNameValidatorMock.getCorrectEmail()
		).thenReturn(
			"@correct.com"
		);
		Mockito.when(
			_newScreenNameValidatorMock.validate(
				Mockito.anyLong(), Mockito.anyString())
		).thenCallRealMethod();

		boolean actual = _newScreenNameValidatorMock.validate(
			0L, "joao@email.com");

		Assert.assertFalse(actual);
	}

	@Test
	public void validate_ReturnsTrue_When_StrEndsWithCorrectEmail() {
		Mockito.when(
			_newScreenNameValidatorMock.getCorrectEmail()
		).thenReturn(
			"@correct.com"
		);
		Mockito.when(
			_newScreenNameValidatorMock.validate(
				Mockito.anyLong(), Mockito.anyString())
		).thenCallRealMethod();

		boolean actual = _newScreenNameValidatorMock.validate(
			0L, "joao@correct.com");

		Assert.assertTrue(actual);
	}

	@Mock
	private NewScreenNameValidator _newScreenNameValidatorMock;

}