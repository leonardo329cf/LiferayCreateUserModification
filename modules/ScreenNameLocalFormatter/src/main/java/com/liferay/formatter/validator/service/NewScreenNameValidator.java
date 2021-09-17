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

package com.liferay.formatter.validator.service;

import com.liferay.formatter.keys.NewFormatterKeys;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.security.auth.DefaultScreenNameValidator;
import com.liferay.portal.kernel.security.auth.ScreenNameValidator;
import com.liferay.portal.kernel.util.Props;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;

import java.util.Locale;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author leonardo.ferreira
 */
@Component(
	immediate = true, property = "service.ranking:Integer=100",
	service = ScreenNameValidator.class
)
public class NewScreenNameValidator
	extends DefaultScreenNameValidator implements ScreenNameValidator {

	@Override
	public String getDescription(Locale locale) {
		return _language.format(
			locale,
			"the-screen-name-needs-to-have-correct-email-suffix-if-is-email-" +
				"or-cannot-contain-reserved-word",
			new String[] {_getCorrectEmail(), POSTFIX, getSpecialChars()},
			false);
	}

	@Override
	public boolean validate(long companyId, String screenName) {
		if (!Validator.isEmailAddress(screenName) ||
			StringUtil.equalsIgnoreCase(screenName, POSTFIX) ||
			hasInvalidChars(screenName) ||
			!screenName.endsWith(_getCorrectEmail())) {

			return false;
		}

		return true;
	}

	private String _getCorrectEmail() {
		return _props.get(NewFormatterKeys.USERS_SCREEN_NAME_COMPANY_EMAIL);
	}

	@Reference
	private Language _language;

	@Reference
	private Props _props;

}