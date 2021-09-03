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

package com.liferay.portal.model.impl;

import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.LayoutFriendlyURLException;
import com.liferay.portal.kernel.model.LayoutConstants;
import com.liferay.portal.kernel.model.LayoutSet;
import com.liferay.portal.kernel.util.Validator;

/**
 * Represents a portal layout, providing access to the layout's URLs, parent
 * layouts, child layouts, theme settings, type settings, and more.
 *
 * <p>
 * The UI name for a layout is "page." Thus, a layout represents a page in the
 * portal. A single page is either part of the public or private layout set of a
 * group (site). Layouts can be organized hierarchically and are summarized in a
 * {@link LayoutSet}.
 * </p>
 *
 * @author Brian Wing Shun Chan
 */
public class LayoutImpl {

	public static int validateFriendlyURL(String friendlyURL) {
		return validateFriendlyURL(friendlyURL, true);
	}
	
	public static int validateFriendlyURL(
			String friendlyURL, boolean checkMaxLength) {

			if (friendlyURL.length() < 2) {
				return LayoutFriendlyURLException.TOO_SHORT;
			}

			if (checkMaxLength &&
				(friendlyURL.length() > LayoutConstants.FRIENDLY_URL_MAX_LENGTH)) {

				return LayoutFriendlyURLException.TOO_LONG;
			}

			if (!friendlyURL.startsWith(StringPool.SLASH)) {
				return LayoutFriendlyURLException.DOES_NOT_START_WITH_SLASH;
			}

			if (friendlyURL.endsWith(StringPool.SLASH)) {
				return LayoutFriendlyURLException.ENDS_WITH_SLASH;
			}

			if (friendlyURL.contains(StringPool.DOUBLE_SLASH)) {
				return LayoutFriendlyURLException.ADJACENT_SLASHES;
			}

			for (char c : friendlyURL.toCharArray()) {
				if (!Validator.isChar(c) && !Validator.isDigit(c) &&
					(c != CharPool.DASH) && (c != CharPool.PERCENT) &&
					(c != CharPool.PERIOD) && (c != CharPool.PLUS) &&
					(c != CharPool.SLASH) && (c != CharPool.STAR) &&
					(c != CharPool.UNDERLINE)) {

					return LayoutFriendlyURLException.INVALID_CHARACTERS;
				}
			}

			return -1;
		}

}