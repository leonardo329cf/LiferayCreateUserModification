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

package com.liferay.formatter.service.test;

import com.liferay.formatter.keys.NewFormatterKeys;
import com.liferay.formatter.service.NewUserLocalServiceWrapper;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.util.Props;

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
public class NewUserLocalServiceWrapperTest {

	@Test
	public void addDefaultAdminUser_Calls_userLocalService_addDefaultAdminUser_With_addEmailIfIsNotEmail_Result()
		throws PortalException {

		// Arrange

		String screenName = "user";

		Mockito.when(
			_props.get(NewFormatterKeys.USERS_SCREEN_NAME_COMPANY_EMAIL)
		).thenReturn(
			_correctEmailSufix
		);

		Mockito.when(
			_userLocalService.addDefaultAdminUser(
				Mockito.anyLong(), Mockito.anyString(), Mockito.anyString(),
				Mockito.any(), Mockito.anyString(), Mockito.anyString(),
				Mockito.anyString())
		).thenReturn(
			_user
		);

		String expected = _newUserLocalServiceWrapper.addEmailIfIsNotEmail(
			screenName);

		// Act

		_newUserLocalServiceWrapper.addDefaultAdminUser(
			0L, screenName, "user@email.com", null, "user", "user", "user");

		// Assert

		Mockito.verify(
			_userLocalService
		).addDefaultAdminUser(
			Mockito.anyLong(), Mockito.eq(expected), Mockito.anyString(),
			Mockito.any(), Mockito.anyString(), Mockito.anyString(),
			Mockito.anyString()
		);
	}

	@Test
	public void addEmailIfIsNotEmail_Returns_ScreenName_When_ScreenName_IsEmail() {

		// Arrange

		String screenName = "user@email.com";
		String expected = "user@email.com";

		// Act

		String actual = _newUserLocalServiceWrapper.addEmailIfIsNotEmail(
			screenName);

		//Assert
		Assert.assertEquals(expected, actual);
	}

	@Test
	public void addEmailIfIsNotEmail_Returns_ScreenName_When_ScreenName_IsEmpty() {

		// Arrange

		String screenName = "";

		String expected = "";

		// Act

		String actual = _newUserLocalServiceWrapper.addEmailIfIsNotEmail(
			screenName);

		//Assert
		Assert.assertEquals(expected, actual);
	}

	@Test
	public void addEmailIfIsNotEmail_Returns_ScreenName_When_ScreenName_IsNull() {

		// Arrange

		String screenName = null;
		String expected = null;

		// Act

		String actual = _newUserLocalServiceWrapper.addEmailIfIsNotEmail(
			screenName);

		//Assert
		Assert.assertEquals(expected, actual);
	}

	@Test
	public void addEmailIfIsNotEmail_Returns_ScreenNameConcatWithCompanyEmail_When_ScreenName_IsNotEmail_Or_IsNotNull_Or_IsNotEmpty() {

		// Arrange

		String screenName = "user";
		String expected = "user@company.com";
		Mockito.when(
			_props.get(NewFormatterKeys.USERS_SCREEN_NAME_COMPANY_EMAIL)
		).thenReturn(
			_correctEmailSufix
		);

		// Act

		String actual = _newUserLocalServiceWrapper.addEmailIfIsNotEmail(
			screenName);

		//Assert
		Assert.assertEquals(expected, actual);
	}

	@Test
	public void addUser_Calls_userLocalService_addUser_With_addEmailIfIsNotEmail_Result()
		throws PortalException {

		// Arrange

		String screenName = "user";

		Mockito.when(
			_props.get(NewFormatterKeys.USERS_SCREEN_NAME_COMPANY_EMAIL)
		).thenReturn(
			_correctEmailSufix
		);

		Mockito.when(
			_userLocalService.addUser(
				Mockito.anyLong(), Mockito.anyLong(), Mockito.anyBoolean(),
				Mockito.anyString(), Mockito.anyString(), Mockito.anyBoolean(),
				Mockito.anyString(), Mockito.anyString(), Mockito.any(),
				Mockito.anyString(), Mockito.anyString(), Mockito.anyString(),
				Mockito.anyLong(), Mockito.anyLong(), Mockito.anyBoolean(),
				Mockito.anyInt(), Mockito.anyInt(), Mockito.anyInt(),
				Mockito.anyString(), Mockito.any(), Mockito.any(),
				Mockito.any(), Mockito.any(), Mockito.anyBoolean(),
				Mockito.any())
		).thenReturn(
			_user
		);

		String expected = _newUserLocalServiceWrapper.addEmailIfIsNotEmail(
			screenName);

		// Act

		_newUserLocalServiceWrapper.addUser(
			0, 0, false, "", "", false, screenName, "", null, "", "", "", 0, 0,
			false, 0, 0, 0, "", null, null, null, null, false, null);

		// Assert

		Mockito.verify(
			_userLocalService
		).addUser(
			Mockito.anyLong(), Mockito.anyLong(), Mockito.anyBoolean(),
			Mockito.anyString(), Mockito.anyString(), Mockito.anyBoolean(),
			Mockito.eq(expected), Mockito.anyString(), Mockito.any(),
			Mockito.anyString(), Mockito.anyString(), Mockito.anyString(),
			Mockito.anyLong(), Mockito.anyLong(), Mockito.anyBoolean(),
			Mockito.anyInt(), Mockito.anyInt(), Mockito.anyInt(),
			Mockito.anyString(), Mockito.any(), Mockito.any(), Mockito.any(),
			Mockito.any(), Mockito.anyBoolean(), Mockito.any()
		);
	}

	@Test
	public void addUserDeprecated_Calls_userLocalService_addUserDeprecated_With_addEmailIfIsNotEmail_Result()
		throws PortalException {

		// Arrange

		String screenName = "user";

		Mockito.when(
			_props.get(NewFormatterKeys.USERS_SCREEN_NAME_COMPANY_EMAIL)
		).thenReturn(
			_correctEmailSufix
		);

		Mockito.when(
			_userLocalService.addUser(
				Mockito.anyLong(), Mockito.anyLong(), Mockito.anyBoolean(),
				Mockito.anyString(), Mockito.anyString(), Mockito.anyBoolean(),
				Mockito.anyString(), Mockito.anyString(), Mockito.anyLong(),
				Mockito.anyString(), Mockito.any(), Mockito.anyString(),
				Mockito.anyString(), Mockito.anyString(), Mockito.anyLong(),
				Mockito.anyLong(), Mockito.anyBoolean(), Mockito.anyInt(),
				Mockito.anyInt(), Mockito.anyInt(), Mockito.anyString(),
				Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(),
				Mockito.anyBoolean(), Mockito.any())
		).thenReturn(
			_user
		);

		String expected = _newUserLocalServiceWrapper.addEmailIfIsNotEmail(
			screenName);

		// Act

		_newUserLocalServiceWrapper.addUser(
			0, 0, false, "", "", false, screenName, "", 0, "", null, "", "", "",
			0, 0, false, 0, 0, 0, "", null, null, null, null, false, null);

		// Assert

		Mockito.verify(
			_userLocalService
		).addUser(
			Mockito.anyLong(), Mockito.anyLong(), Mockito.anyBoolean(),
			Mockito.anyString(), Mockito.anyString(), Mockito.anyBoolean(),
			Mockito.eq(expected), Mockito.anyString(), Mockito.anyLong(),
			Mockito.anyString(), Mockito.any(), Mockito.anyString(),
			Mockito.anyString(), Mockito.anyString(), Mockito.anyLong(),
			Mockito.anyLong(), Mockito.anyBoolean(), Mockito.anyInt(),
			Mockito.anyInt(), Mockito.anyInt(), Mockito.anyString(),
			Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(),
			Mockito.anyBoolean(), Mockito.any()
		);
	}

	@Test
	public void addUserWithWorkflow_Calls_userLocalService_addUserWithWorkflow_With_addEmailIfIsNotEmail_Result()
		throws PortalException {

		// Arrange

		String screenName = "user";

		Mockito.when(
			_props.get(NewFormatterKeys.USERS_SCREEN_NAME_COMPANY_EMAIL)
		).thenReturn(
			_correctEmailSufix
		);

		Mockito.when(
			_userLocalService.addUserWithWorkflow(
				Mockito.anyLong(), Mockito.anyLong(), Mockito.anyBoolean(),
				Mockito.anyString(), Mockito.anyString(), Mockito.anyBoolean(),
				Mockito.anyString(), Mockito.anyString(), Mockito.any(),
				Mockito.anyString(), Mockito.anyString(), Mockito.anyString(),
				Mockito.anyLong(), Mockito.anyLong(), Mockito.anyBoolean(),
				Mockito.anyInt(), Mockito.anyInt(), Mockito.anyInt(),
				Mockito.anyString(), Mockito.any(), Mockito.any(),
				Mockito.any(), Mockito.any(), Mockito.anyBoolean(),
				Mockito.any())
		).thenReturn(
			_user
		);

		String expected = _newUserLocalServiceWrapper.addEmailIfIsNotEmail(
			screenName);

		// Act

		_newUserLocalServiceWrapper.addUserWithWorkflow(
			0, 0, false, "", "", false, screenName, "", null, "", "", "", 0, 0,
			false, 0, 0, 0, "", null, null, null, null, false, null);

		// Assert

		Mockito.verify(
			_userLocalService
		).addUserWithWorkflow(
			Mockito.anyLong(), Mockito.anyLong(), Mockito.anyBoolean(),
			Mockito.anyString(), Mockito.anyString(), Mockito.anyBoolean(),
			Mockito.eq(expected), Mockito.anyString(), Mockito.any(),
			Mockito.anyString(), Mockito.anyString(), Mockito.anyString(),
			Mockito.anyLong(), Mockito.anyLong(), Mockito.anyBoolean(),
			Mockito.anyInt(), Mockito.anyInt(), Mockito.anyInt(),
			Mockito.anyString(), Mockito.any(), Mockito.any(), Mockito.any(),
			Mockito.any(), Mockito.anyBoolean(), Mockito.any()
		);
	}

	@Test
	public void addUserWithWorkflowDeprecated_Calls_userLocalService_addUserWithWorkflowDeprecated_With_addEmailIfIsNotEmail_Result()
		throws PortalException {

		// Arrange

		String screenName = "user";

		Mockito.when(
			_props.get(NewFormatterKeys.USERS_SCREEN_NAME_COMPANY_EMAIL)
		).thenReturn(
			_correctEmailSufix
		);

		Mockito.when(
			_userLocalService.addUserWithWorkflow(
				Mockito.anyLong(), Mockito.anyLong(), Mockito.anyBoolean(),
				Mockito.anyString(), Mockito.anyString(), Mockito.anyBoolean(),
				Mockito.anyString(), Mockito.anyString(), Mockito.anyLong(),
				Mockito.anyString(), Mockito.any(), Mockito.anyString(),
				Mockito.anyString(), Mockito.anyString(), Mockito.anyLong(),
				Mockito.anyLong(), Mockito.anyBoolean(), Mockito.anyInt(),
				Mockito.anyInt(), Mockito.anyInt(), Mockito.anyString(),
				Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(),
				Mockito.anyBoolean(), Mockito.any())
		).thenReturn(
			_user
		);

		String expected = _newUserLocalServiceWrapper.addEmailIfIsNotEmail(
			screenName);

		// Act

		_newUserLocalServiceWrapper.addUserWithWorkflow(
			0, 0, false, "", "", false, screenName, "", 0, "", null, "", "", "",
			0, 0, false, 0, 0, 0, "", null, null, null, null, false, null);

		// Assert

		Mockito.verify(
			_userLocalService
		).addUserWithWorkflow(
			Mockito.anyLong(), Mockito.anyLong(), Mockito.anyBoolean(),
			Mockito.anyString(), Mockito.anyString(), Mockito.anyBoolean(),
			Mockito.eq(expected), Mockito.anyString(), Mockito.anyLong(),
			Mockito.anyString(), Mockito.any(), Mockito.anyString(),
			Mockito.anyString(), Mockito.anyString(), Mockito.anyLong(),
			Mockito.anyLong(), Mockito.anyBoolean(), Mockito.anyInt(),
			Mockito.anyInt(), Mockito.anyInt(), Mockito.anyString(),
			Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(),
			Mockito.anyBoolean(), Mockito.any()
		);
	}

	private final String _correctEmailSufix = "@company.com";

	@InjectMocks
	private NewUserLocalServiceWrapper _newUserLocalServiceWrapper;

	@Mock
	private Props _props;

	@Mock
	private User _user;

	@Mock
	private UserLocalService _userLocalService;

}