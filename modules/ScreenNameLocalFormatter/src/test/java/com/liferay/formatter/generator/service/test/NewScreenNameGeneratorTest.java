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
import com.liferay.portal.kernel.service.GroupLocalServiceUtil;
import com.liferay.portal.kernel.service.UserLocalServiceUtil;
import com.liferay.portal.kernel.util.PrefsPropsUtil;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.PropsUtil;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.mockito.AdditionalMatchers;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

/**
 * @author leonardo.ferreira
 */
@RunWith(MockitoJUnitRunner.class)
public class NewScreenNameGeneratorTest {

	@After
	public void close() {
		_propsUtil.close();
		_prefsPropsUtil.close();
		_userLocalServiceUtil.close();
		_groupLocalServiceUtil.close();
	}

	@Before
	public void init() {
		_propsUtil = Mockito.mockStatic(PropsUtil.class);
		_prefsPropsUtil = Mockito.mockStatic(PrefsPropsUtil.class);
		_userLocalServiceUtil = Mockito.mockStatic(UserLocalServiceUtil.class);
		_groupLocalServiceUtil = Mockito.mockStatic(
			GroupLocalServiceUtil.class);
	}

	@Test
	public void testGenerate_AddsEmailSufix_When_ScreenNameIsUnusedByOtherUserOrGroup_And_ScreenNameDoesNotContainsReservedWords()
		throws Exception {

		// Arrange

		String input = "user@liferay.com";
		String expected = "user" + _correctEmail;
		_propsUtil.when(
			() -> PropsUtil.get(
				NewFormatterKeys.USERS_SCREEN_NAME_COMPANY_EMAIL)
		).thenReturn(
			_correctEmail
		);
		_propsUtil.when(
			() -> PropsUtil.get(PropsKeys.ADMIN_RESERVED_SCREEN_NAMES)
		).thenReturn(
			_adminReservedScreenNames
		);

		_prefsPropsUtil.when(
			() -> PrefsPropsUtil.getStringArray(
				Mockito.anyLong(), Mockito.anyString(), Mockito.anyString(),
				Mockito.any())
		).thenReturn(
			_adminReservedScreenNamesArray
		);

		_userLocalServiceUtil.when(
			() -> UserLocalServiceUtil.fetchUserByScreenName(
				Mockito.anyLong(), Mockito.anyString())
		).thenReturn(
			null
		);

		_groupLocalServiceUtil.when(
			() -> GroupLocalServiceUtil.fetchFriendlyURLGroup(
				Mockito.anyLong(), Mockito.anyString())
		).thenReturn(
			null
		);

		NewScreenNameGenerator newScreenNameGenerator =
			new NewScreenNameGenerator();

		// Act

		String result = newScreenNameGenerator.generate(0L, 0L, input);

		// Assert

		Assert.assertEquals(expected, result);
	}

	@Test
	public void testGenerate_CreatesNewScreenName_When_ScreenNameIsAlreadyUseByGroup()
		throws Exception {

		// Arrange

		String input = "user@liferay.com";
		String prefix = "user";
		String expected = "user1" + _correctEmail;

		_propsUtil.when(
			() -> PropsUtil.get(
				NewFormatterKeys.USERS_SCREEN_NAME_COMPANY_EMAIL)
		).thenReturn(
			_correctEmail
		);
		_propsUtil.when(
			() -> PropsUtil.get(PropsKeys.ADMIN_RESERVED_SCREEN_NAMES)
		).thenReturn(
			_adminReservedScreenNames
		);

		_prefsPropsUtil.when(
			() -> PrefsPropsUtil.getStringArray(
				Mockito.anyLong(), Mockito.anyString(), Mockito.anyString(),
				Mockito.any())
		).thenReturn(
			_adminReservedScreenNamesArray
		);

		_userLocalServiceUtil.when(
			() -> UserLocalServiceUtil.fetchUserByScreenName(
				Mockito.anyLong(), Mockito.anyString())
		).thenReturn(
			null
		);

		_groupLocalServiceUtil.when(
			() -> GroupLocalServiceUtil.fetchFriendlyURLGroup(
				Mockito.anyLong(),
				Mockito.eq(StringPool.SLASH + prefix + _correctEmail))
		).thenReturn(
			_groupWrapperMock
		);
		_groupLocalServiceUtil.when(
			() -> GroupLocalServiceUtil.fetchFriendlyURLGroup(
				Mockito.anyLong(), Mockito.eq(expected))
		).thenReturn(
			null
		);

		NewScreenNameGenerator newScreenNameGenerator =
			new NewScreenNameGenerator();

		// Act

		String result = newScreenNameGenerator.generate(0L, 0L, input);

		// Assert

		Assert.assertEquals(expected, result);
	}

	@Test
	public void testGenerate_CreatesNewScreenName_When_ScreenNameIsAlreadyUseByUser()
		throws Exception {

		// Arrange

		String input = "user@liferay.com";

		String prefix = "user";

		String expected = prefix + "1" + _correctEmail;

		_propsUtil.when(
			() -> PropsUtil.get(
				NewFormatterKeys.USERS_SCREEN_NAME_COMPANY_EMAIL)
		).thenReturn(
			_correctEmail
		);
		_propsUtil.when(
			() -> PropsUtil.get(PropsKeys.ADMIN_RESERVED_SCREEN_NAMES)
		).thenReturn(
			_adminReservedScreenNames
		);

		_prefsPropsUtil.when(
			() -> PrefsPropsUtil.getStringArray(
				Mockito.anyLong(), Mockito.anyString(), Mockito.anyString(),
				Mockito.any())
		).thenReturn(
			_adminReservedScreenNamesArray
		);

		_userLocalServiceUtil.when(
			() -> UserLocalServiceUtil.fetchUserByScreenName(
				Mockito.anyLong(), Mockito.eq(prefix + _correctEmail))
		).thenReturn(
			_userWrapperMock
		);

		_userLocalServiceUtil.when(
			() -> UserLocalServiceUtil.fetchUserByScreenName(
				Mockito.anyLong(),
				AdditionalMatchers.not(Mockito.eq(prefix + _correctEmail)))
		).thenReturn(
			null
		);

		_groupLocalServiceUtil.when(
			() -> GroupLocalServiceUtil.fetchFriendlyURLGroup(
				Mockito.anyLong(), Mockito.anyString())
		).thenReturn(
			null
		);

		NewScreenNameGenerator newScreenNameGenerator =
			new NewScreenNameGenerator();

		// Act

		String result = newScreenNameGenerator.generate(0L, 0L, input);

		// Assert

		Assert.assertEquals(expected, result);
	}

	private String _adminReservedScreenNames = "ADMIN\nAdmin\nadmin";
	private String[] _adminReservedScreenNamesArray = {
		"ADMIN", "Admin", "admin"
	};
	private String _correctEmail = "@company.com";
	private MockedStatic<GroupLocalServiceUtil> _groupLocalServiceUtil;

	@Mock
	private GroupWrapper _groupWrapperMock;

	private MockedStatic<PrefsPropsUtil> _prefsPropsUtil;
	private MockedStatic<PropsUtil> _propsUtil;
	private MockedStatic<UserLocalServiceUtil> _userLocalServiceUtil;

	@Mock
	private UserWrapper _userWrapperMock;

}