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
import com.liferay.portal.kernel.service.GroupLocalServiceUtil;
import com.liferay.portal.kernel.service.UserLocalServiceUtil;
import com.liferay.portal.kernel.util.PrefsPropsUtil;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.PropsUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;

import org.osgi.service.component.annotations.Component;

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

		screenName = StringUtil.toLowerCase(screenName);

		for (char c : screenName.toCharArray()) {
			if (!Validator.isChar(c) && !Validator.isDigit(c) &&
				(c != CharPool.DASH) && (c != CharPool.PERIOD)) {

				screenName = StringUtil.replace(screenName, c, CharPool.PERIOD);
			}
		}

		screenName = screenName.concat(
			PropsUtil.get(NewFormatterKeys.USERS_SCREEN_NAME_COMPANY_EMAIL));

		String[] reservedScreenNames = PrefsPropsUtil.getStringArray(
			companyId, PropsKeys.ADMIN_RESERVED_SCREEN_NAMES,
			StringPool.NEW_LINE, _getAdminReservedScreenNames());

		for (String reservedScreenName : reservedScreenNames) {
			if (StringUtil.equalsIgnoreCase(screenName, reservedScreenName)) {
				return _getUnusedScreenName(companyId, screenName);
			}
		}

		User user = UserLocalServiceUtil.fetchUserByScreenName(
			companyId, screenName);

		if (user != null) {
			return _getUnusedScreenName(companyId, screenName);
		}

		Group friendlyURLGroup = GroupLocalServiceUtil.fetchFriendlyURLGroup(
			companyId, StringPool.SLASH + screenName);

		if (friendlyURLGroup == null) {
			return screenName;
		}

		return _getUnusedScreenName(companyId, screenName);
	}

	private String[] _getAdminReservedScreenNames() {
		return StringUtil.splitLines(
			PropsUtil.get(PropsKeys.ADMIN_RESERVED_SCREEN_NAMES));
	}

	private String _getUnusedScreenName(long companyId, String screenName) {
		for (int i = 1;; i++) {
			String tempScreenName =
				StringUtil.extractFirst(screenName, CharPool.AT) + i;

			tempScreenName = tempScreenName.concat(
				PropsUtil.get(
					NewFormatterKeys.USERS_SCREEN_NAME_COMPANY_EMAIL));

			User user = UserLocalServiceUtil.fetchUserByScreenName(
				companyId, tempScreenName);

			if (user != null) {
				continue;
			}

			Group friendlyURLGroup =
				GroupLocalServiceUtil.fetchFriendlyURLGroup(
					companyId, StringPool.SLASH + tempScreenName);

			if (friendlyURLGroup == null) {
				return tempScreenName;
			}
		}
	}

}