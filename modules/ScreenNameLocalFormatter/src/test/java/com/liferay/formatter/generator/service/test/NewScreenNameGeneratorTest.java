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

package com.liferay.formatter.generator.service.test;

import com.liferay.formatter.generator.service.NewScreenNameGenerator;
import com.liferay.formatter.keys.NewFormatterKeys;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.model.GroupWrapper;
import com.liferay.portal.kernel.model.UserWrapper;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.util.PrefsProps;
import com.liferay.portal.kernel.util.Props;
import com.liferay.portal.kernel.util.PropsKeys;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.mockito.AdditionalMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

/**
 * @author leonardo.ferreira
 */
@RunWith(MockitoJUnitRunner.class)
public class NewScreenNameGeneratorTest {

	@Before
	public void setup() {
		Mockito.when(
				_props.get(NewFormatterKeys.USERS_SCREEN_NAME_COMPANY_EMAIL)
			).thenReturn(
				_correctEmail
			);
			Mockito.when(
				_props.get(PropsKeys.ADMIN_RESERVED_SCREEN_NAMES)
			).thenReturn(
				_adminReservedScreenNames
			);

			Mockito.when(
				_prefsProps.getStringArray(
					Mockito.anyLong(), Mockito.anyString(), Mockito.anyString(),
					Mockito.any())
			).thenReturn(
				_adminReservedScreenNamesArray
			);
	}
	
	
	@Test
	public void testGenerate_AddsEmailSufix_When_ScreenNameIsUnusedByOtherUserOrGroup_And_ScreenNameDoesNotContainsReservedWords()
		throws Exception {

		String input = "user@liferay.com";
		String expected = "user" + _correctEmail;

		Mockito.when(
			_userLocalService.fetchUserByScreenName(
				Mockito.anyLong(), Mockito.anyString())
		).thenReturn(
			null
		);

		Mockito.when(
			_groupLocalService.fetchFriendlyURLGroup(
				Mockito.anyLong(), Mockito.anyString())
		).thenReturn(
			null
		);

		String result = _newScreenNameGenerator.generate(0L, 0L, input);

		Assert.assertEquals(expected, result);
	}

	@Test
	public void testGenerate_CreatesNewScreenName_When_ScreenNameIsAlreadyInUseByGroup()
		throws Exception {

		String input = "user@liferay.com";
		String prefix = "user";
		String expected = "user1" + _correctEmail;

		Mockito.when(
			_userLocalService.fetchUserByScreenName(
				Mockito.anyLong(), Mockito.anyString())
		).thenReturn(
			null
		);

		Mockito.when(
			_groupLocalService.fetchFriendlyURLGroup(
				Mockito.anyLong(),
				Mockito.eq(StringPool.SLASH + prefix + _correctEmail))
		).thenReturn(
			_groupWrapperMock
		);

		String result = _newScreenNameGenerator.generate(0L, 0L, input);

		Assert.assertEquals(expected, result);
	}

	@Test
	public void testGenerate_CreatesNewScreenName_When_ScreenNameIsAlreadyUseByUser()
		throws Exception {

		String input = "user@liferay.com";

		String prefix = "user";

		String expected = prefix + "1" + _correctEmail;

		Mockito.when(
			_userLocalService.fetchUserByScreenName(
				Mockito.anyLong(), Mockito.eq(prefix + _correctEmail))
		).thenReturn(
			_userWrapperMock
		);

		Mockito.when(
			_userLocalService.fetchUserByScreenName(
				Mockito.anyLong(),
				AdditionalMatchers.not(Mockito.eq(prefix + _correctEmail)))
		).thenReturn(
			null
		);

		Mockito.when(
			_groupLocalService.fetchFriendlyURLGroup(
				Mockito.anyLong(), Mockito.anyString())
		).thenReturn(
			null
		);

		String result = _newScreenNameGenerator.generate(0L, 0L, input);

		Assert.assertEquals(expected, result);
	}

	private final String _adminReservedScreenNames = "ADMIN\nAdmin\nadmin";
	private final String[] _adminReservedScreenNamesArray = {
		"ADMIN", "Admin", "admin"
	};
	private final String _correctEmail = "@company.com";

	@Mock
	private GroupLocalService _groupLocalService;

	@Mock
	private GroupWrapper _groupWrapperMock;

	@InjectMocks
	private NewScreenNameGenerator _newScreenNameGenerator;

	@Mock
	private PrefsProps _prefsProps;

	@Mock
	private Props _props;

	@Mock
	private UserLocalService _userLocalService;

	@Mock
	private UserWrapper _userWrapperMock;

}