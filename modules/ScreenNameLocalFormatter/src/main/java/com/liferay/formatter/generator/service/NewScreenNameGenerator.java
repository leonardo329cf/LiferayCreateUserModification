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

package com.liferay.formatter.generator.service;

import com.liferay.formatter.keys.NewFormatterKeys;
import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.security.auth.ScreenNameGenerator;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.util.PrefsProps;
import com.liferay.portal.kernel.util.Props;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author leonardo.ferreira
 */
@Component(
	immediate = true, property = "service.ranking:Integer=100",
	service = ScreenNameGenerator.class
)
public class NewScreenNameGenerator implements ScreenNameGenerator {

	@Override
	public String generate(long companyId, long userId, String emailAddress)
		throws Exception {

		String screenName = null;

		screenName = StringUtil.extractFirst(emailAddress, CharPool.AT);

		screenName = StringUtil.lowerCase(screenName);

		screenName = _removeUnwantedCaracters(screenName);

		screenName = _concatWithEmailSuffix(screenName);

		if (_screeNameIsReservedScreenName(companyId, screenName) ||
			_screeNameIsAlreadyInUse(companyId, screenName) ||
			_screenNameIsSimilarToFriendlyURLGroup(companyId, screenName)) {

			screenName = _getUnusedScreenName(companyId, screenName);
		}

		return screenName;
	}

	private String _concatWithEmailSuffix(String screenName) {
		screenName = screenName.concat(
			_props.get(NewFormatterKeys.USERS_SCREEN_NAME_COMPANY_EMAIL));

		return screenName;
	}

	private String[] _getAdminReservedScreenNames() {
		return StringUtil.splitLines(
			_props.get(PropsKeys.ADMIN_RESERVED_SCREEN_NAMES));
	}

	private String _getUnusedScreenName(long companyId, String screenName) {
		for (int i = 1;; i++) {
			String tempScreenName =
				StringUtil.extractFirst(screenName, CharPool.AT) + i;

			tempScreenName = _concatWithEmailSuffix(tempScreenName);

			if (_screeNameIsReservedScreenName(companyId, tempScreenName) ||
				_screeNameIsAlreadyInUse(companyId, tempScreenName) ||
				_screenNameIsSimilarToFriendlyURLGroup(
					companyId, tempScreenName)) {

				continue;
			}

			return tempScreenName;
		}
	}

	private String _removeUnwantedCaracters(String screenName) {
		for (char c : screenName.toCharArray()) {
			if (!Validator.isChar(c) && !Validator.isDigit(c) &&
				(c != CharPool.DASH) && (c != CharPool.PERIOD)) {

				screenName = StringUtil.replace(screenName, c, CharPool.PERIOD);
			}
		}

		return screenName;
	}

	private boolean _screeNameIsAlreadyInUse(
		long companyId, String screenName) {

		User user = _userLocalService.fetchUserByScreenName(
			companyId, screenName);

		if (user != null) {
			return true;
		}

		return false;
	}

	private boolean _screeNameIsReservedScreenName(
		long companyId, String screenName) {

		String[] reservedScreenNames = _prefsProps.getStringArray(
			companyId, PropsKeys.ADMIN_RESERVED_SCREEN_NAMES,
			StringPool.NEW_LINE, _getAdminReservedScreenNames());

		for (String reservedScreenName : reservedScreenNames) {
			if (StringUtil.equalsIgnoreCase(screenName, reservedScreenName)) {
				return true;
			}
		}

		return false;
	}

	private boolean _screenNameIsSimilarToFriendlyURLGroup(
		long companyId, String screenName) {

		Group friendlyURLGroup = _groupLocalService.fetchFriendlyURLGroup(
			companyId, StringPool.SLASH + screenName);

		if (friendlyURLGroup != null) {
			return true;
		}

		return false;
	}

	@Reference
	private GroupLocalService _groupLocalService;

	@Reference
	private PrefsProps _prefsProps;

	@Reference
	private Props _props;

	@Reference
	private UserLocalService _userLocalService;

}