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

import com.liferay.formatter.keys.NewFormatterKeys;
import com.liferay.formatter.validator.service.NewScreenNameValidator;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.util.Html;
import com.liferay.portal.kernel.util.Props;
import com.liferay.portal.kernel.util.PropsKeys;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.mockito.InjectMocks;
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
		String screenName = "joao@email.com";

		Mockito.when(
			_props.get(NewFormatterKeys.USERS_SCREEN_NAME_COMPANY_EMAIL)
		).thenReturn(
			_correctEmail
		);

		Mockito.when(
			_props.get(PropsKeys.USERS_SCREEN_NAME_SPECIAL_CHARACTERS)
		).thenReturn(
			_screenNameSpecialCaracters
		);

		boolean actual = _newScreenNameValidator.validate(0L, screenName);

		Assert.assertFalse(actual);
	}

	@Test
	public void validate_ReturnsFalse_When_StrEndsWithNoEmail() {
		String screenName = "joao";

		boolean actual = _newScreenNameValidator.validate(0L, screenName);

		Assert.assertFalse(actual);
	}

	@Test
	public void validate_ReturnsFalse_When_StrHasInvalidCaracter() {
		String screenName = "joao/" + _correctEmail;

		Mockito.when(
			_props.get(PropsKeys.USERS_SCREEN_NAME_SPECIAL_CHARACTERS)
		).thenReturn(
			_screenNameSpecialCaracters
		);

		boolean actual = _newScreenNameValidator.validate(0L, screenName);

		Assert.assertFalse(actual);
	}

	@Test
	public void validate_ReturnsTrue_When_StrEndsWithCorrectEmail() {
		String screenName = "joao" + _correctEmail;

		Mockito.when(
			_props.get(NewFormatterKeys.USERS_SCREEN_NAME_COMPANY_EMAIL)
		).thenReturn(
			_correctEmail
		);

		Mockito.when(
			_props.get(PropsKeys.USERS_SCREEN_NAME_SPECIAL_CHARACTERS)
		).thenReturn(
			_screenNameSpecialCaracters
		);

		boolean actual = _newScreenNameValidator.validate(0L, screenName);

		Assert.assertTrue(actual);
	}

	private String _correctEmail = "@company.com";

	@Mock
	private Html _html;

	@Mock
	private Language _language;

	@InjectMocks
	private NewScreenNameValidator _newScreenNameValidator;

	@Mock
	private Props _props;

	private String _screenNameSpecialCaracters = "@._";

}