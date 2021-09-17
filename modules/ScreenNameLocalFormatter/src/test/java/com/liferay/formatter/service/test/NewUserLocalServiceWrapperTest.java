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

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.liferay.formatter.keys.NewFormatterKeys;
import com.liferay.formatter.service.NewUserLocalServiceWrapper;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.util.Props;

import org.junit.Before;
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

	// addDefaultAdminUser tests

	@Test
	public void addDefaultAdminUser_Calls_BaseMethodImplementation_With_ScreenNameWithEmailSuffixAdded_When_ScreenNameIsNotEmail_And_IsNotNull_And_IsNotEmpty()
		throws PortalException {

		String screenName = "user";

		when(
			_userLocalService.addDefaultAdminUser(
				Mockito.anyLong(), Mockito.any(), Mockito.anyString(),
				Mockito.any(), Mockito.anyString(), Mockito.anyString(),
				Mockito.anyString())
		).thenReturn(
			_user
		);

		String expected = screenName + _correctEmailSuffix;

		_newUserLocalServiceWrapper.addDefaultAdminUser(
			0L, screenName, "user@email.com", null, "user", "user", "user");

		verify(
			_userLocalService
		).addDefaultAdminUser(
			Mockito.anyLong(), Mockito.eq(expected), Mockito.anyString(),
			Mockito.any(), Mockito.anyString(), Mockito.anyString(),
			Mockito.anyString()
		);
	}

	@Test
	public void addDefaultAdminUser_Calls_BaseMethodImplementation_With_UnmodifiedScreenName_When_ScreenNameIsAnEmailAddress()
		throws PortalException {

		String screenName = "user" + _anyEmailSuffix;

		when(
			_userLocalService.addDefaultAdminUser(
				Mockito.anyLong(), Mockito.any(), Mockito.anyString(),
				Mockito.any(), Mockito.anyString(), Mockito.anyString(),
				Mockito.anyString())
		).thenReturn(
			_user
		);

		String expected = screenName;

		_newUserLocalServiceWrapper.addDefaultAdminUser(
			0L, screenName, "user@email.com", null, "user", "user", "user");

		verify(
			_userLocalService
		).addDefaultAdminUser(
			Mockito.anyLong(), Mockito.eq(expected), Mockito.anyString(),
			Mockito.any(), Mockito.anyString(), Mockito.anyString(),
			Mockito.anyString()
		);
	}

	@Test
	public void addDefaultAdminUser_Calls_BaseMethodImplementationOfAddDefaultAdminUser_With_EmptyScreenName_When_ScreenNameIsEmpty()
		throws PortalException {

		String screenName = "";

		when(
			_userLocalService.addDefaultAdminUser(
				Mockito.anyLong(), Mockito.any(), Mockito.anyString(),
				Mockito.any(), Mockito.anyString(), Mockito.anyString(),
				Mockito.anyString())
		).thenReturn(
			_user
		);

		String expected = screenName;

		_newUserLocalServiceWrapper.addDefaultAdminUser(
			0L, screenName, "user@email.com", null, "user", "user", "user");

		verify(
			_userLocalService
		).addDefaultAdminUser(
			Mockito.anyLong(), Mockito.eq(expected), Mockito.anyString(),
			Mockito.any(), Mockito.anyString(), Mockito.anyString(),
			Mockito.anyString()
		);
	}

	@Test
	public void addDefaultAdminUser_Calls_BaseMethodImplementationOfAddDefaultAdminUser_With_NullScreenName_When_ScreenNameIsNull()
		throws PortalException {

		String screenName = null;

		when(
			_userLocalService.addDefaultAdminUser(
				Mockito.anyLong(), Mockito.any(), Mockito.anyString(),
				Mockito.any(), Mockito.anyString(), Mockito.anyString(),
				Mockito.anyString())
		).thenReturn(
			_user
		);

		String expected = screenName;

		_newUserLocalServiceWrapper.addDefaultAdminUser(
			0L, screenName, "user@email.com", null, "user", "user", "user");

		verify(
			_userLocalService
		).addDefaultAdminUser(
			Mockito.anyLong(), Mockito.eq(expected), Mockito.anyString(),
			Mockito.any(), Mockito.anyString(), Mockito.anyString(),
			Mockito.anyString()
		);
	}

	@Test
	public void addUser_Calls_BaseMethodImplementation_With_ScreenNameWithEmailSuffixAdded_When_ScreenNameIsNotEmail_And_IsNotNull_And_IsNotEmpty()
		throws PortalException {

		String screenName = "user";

		when(
			_userLocalService.addUser(
				Mockito.anyLong(), Mockito.anyLong(), Mockito.anyBoolean(),
				Mockito.anyString(), Mockito.anyString(), Mockito.anyBoolean(),
				Mockito.any(), Mockito.anyString(), Mockito.any(),
				Mockito.anyString(), Mockito.anyString(), Mockito.anyString(),
				Mockito.anyLong(), Mockito.anyLong(), Mockito.anyBoolean(),
				Mockito.anyInt(), Mockito.anyInt(), Mockito.anyInt(),
				Mockito.anyString(), Mockito.any(), Mockito.any(),
				Mockito.any(), Mockito.any(), Mockito.anyBoolean(),
				Mockito.any())
		).thenReturn(
			_user
		);

		String expected = screenName + _correctEmailSuffix;

		_newUserLocalServiceWrapper.addUser(
			0, 0, false, "", "", false, screenName, "", null, "", "", "", 0, 0,
			false, 0, 0, 0, "", null, null, null, null, false, null);

		verify(
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

	// addUser tests

	@Test
	public void addUser_Calls_BaseMethodImplementation_With_UnmodifiedScreenName_When_ScreenNameIsAnEmailAddress()
		throws PortalException {

		String screenName = "user" + _anyEmailSuffix;

		when(
			_userLocalService.addUser(
				Mockito.anyLong(), Mockito.anyLong(), Mockito.anyBoolean(),
				Mockito.anyString(), Mockito.anyString(), Mockito.anyBoolean(),
				Mockito.any(), Mockito.anyString(), Mockito.any(),
				Mockito.anyString(), Mockito.anyString(), Mockito.anyString(),
				Mockito.anyLong(), Mockito.anyLong(), Mockito.anyBoolean(),
				Mockito.anyInt(), Mockito.anyInt(), Mockito.anyInt(),
				Mockito.anyString(), Mockito.any(), Mockito.any(),
				Mockito.any(), Mockito.any(), Mockito.anyBoolean(),
				Mockito.any())
		).thenReturn(
			_user
		);

		String expected = screenName;

		_newUserLocalServiceWrapper.addUser(
			0, 0, false, "", "", false, screenName, "", null, "", "", "", 0, 0,
			false, 0, 0, 0, "", null, null, null, null, false, null);

		verify(
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
	public void addUser_Calls_BaseMethodImplementationOfAddDefaultAdminUser_With_EmptyScreenName_When_ScreenNameIsEmpty()
		throws PortalException {

		String screenName = null;

		when(
			_userLocalService.addUser(
				Mockito.anyLong(), Mockito.anyLong(), Mockito.anyBoolean(),
				Mockito.anyString(), Mockito.anyString(), Mockito.anyBoolean(),
				Mockito.any(), Mockito.anyString(), Mockito.any(),
				Mockito.anyString(), Mockito.anyString(), Mockito.anyString(),
				Mockito.anyLong(), Mockito.anyLong(), Mockito.anyBoolean(),
				Mockito.anyInt(), Mockito.anyInt(), Mockito.anyInt(),
				Mockito.anyString(), Mockito.any(), Mockito.any(),
				Mockito.any(), Mockito.any(), Mockito.anyBoolean(),
				Mockito.any())
		).thenReturn(
			_user
		);

		String expected = screenName;

		_newUserLocalServiceWrapper.addUser(
			0, 0, false, "", "", false, screenName, "", null, "", "", "", 0, 0,
			false, 0, 0, 0, "", null, null, null, null, false, null);

		verify(
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
	public void addUser_Calls_BaseMethodImplementationOfAddDefaultAdminUser_With_NullScreenName_When_ScreenNameIsNull()
		throws PortalException {

		String screenName = null;

		when(
			_userLocalService.addUser(
				Mockito.anyLong(), Mockito.anyLong(), Mockito.anyBoolean(),
				Mockito.anyString(), Mockito.anyString(), Mockito.anyBoolean(),
				Mockito.any(), Mockito.anyString(), Mockito.any(),
				Mockito.anyString(), Mockito.anyString(), Mockito.anyString(),
				Mockito.anyLong(), Mockito.anyLong(), Mockito.anyBoolean(),
				Mockito.anyInt(), Mockito.anyInt(), Mockito.anyInt(),
				Mockito.anyString(), Mockito.any(), Mockito.any(),
				Mockito.any(), Mockito.any(), Mockito.anyBoolean(),
				Mockito.any())
		).thenReturn(
			_user
		);

		String expected = screenName;

		_newUserLocalServiceWrapper.addUser(
			0, 0, false, "", "", false, screenName, "", null, "", "", "", 0, 0,
			false, 0, 0, 0, "", null, null, null, null, false, null);

		verify(
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
	public void addUserDeprecated_Calls_BaseMethodImplementation_With_ScreenNameWithEmailSuffixAdded_When_ScreenNameIsNotEmail_And_IsNotNull_And_IsNotEmpty()
		throws PortalException {

		String screenName = "user";

		when(
			_userLocalService.addUser(
				Mockito.anyLong(), Mockito.anyLong(), Mockito.anyBoolean(),
				Mockito.anyString(), Mockito.anyString(), Mockito.anyBoolean(),
				Mockito.any(), Mockito.anyString(), Mockito.anyLong(),
				Mockito.anyString(), Mockito.any(), Mockito.anyString(),
				Mockito.anyString(), Mockito.anyString(), Mockito.anyLong(),
				Mockito.anyLong(), Mockito.anyBoolean(), Mockito.anyInt(),
				Mockito.anyInt(), Mockito.anyInt(), Mockito.anyString(),
				Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(),
				Mockito.anyBoolean(), Mockito.any())
		).thenReturn(
			_user
		);

		String expected = screenName + _correctEmailSuffix;

		_newUserLocalServiceWrapper.addUser(
			0, 0, false, "", "", false, screenName, "", 0, "", null, "", "", "",
			0, 0, false, 0, 0, 0, "", null, null, null, null, false, null);

		verify(
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

	// addUserDeprecated tests

	@Test
	public void addUserDeprecated_Calls_BaseMethodImplementation_With_UnmodifiedScreenName_When_ScreenNameIsAnEmailAddress()
		throws PortalException {

		String screenName = "user" + _anyEmailSuffix;

		when(
			_userLocalService.addUser(
				Mockito.anyLong(), Mockito.anyLong(), Mockito.anyBoolean(),
				Mockito.anyString(), Mockito.anyString(), Mockito.anyBoolean(),
				Mockito.any(), Mockito.anyString(), Mockito.anyLong(),
				Mockito.anyString(), Mockito.any(), Mockito.anyString(),
				Mockito.anyString(), Mockito.anyString(), Mockito.anyLong(),
				Mockito.anyLong(), Mockito.anyBoolean(), Mockito.anyInt(),
				Mockito.anyInt(), Mockito.anyInt(), Mockito.anyString(),
				Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(),
				Mockito.anyBoolean(), Mockito.any())
		).thenReturn(
			_user
		);

		String expected = screenName;

		_newUserLocalServiceWrapper.addUser(
			0, 0, false, "", "", false, screenName, "", 0, "", null, "", "", "",
			0, 0, false, 0, 0, 0, "", null, null, null, null, false, null);

		verify(
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
	public void addUserDeprecated_Calls_BaseMethodImplementationOfAddDefaultAdminUser_With_EmptyScreenName_When_ScreenNameIsEmpty()
		throws PortalException {

		String screenName = "";

		when(
			_userLocalService.addUser(
				Mockito.anyLong(), Mockito.anyLong(), Mockito.anyBoolean(),
				Mockito.anyString(), Mockito.anyString(), Mockito.anyBoolean(),
				Mockito.any(), Mockito.anyString(), Mockito.anyLong(),
				Mockito.anyString(), Mockito.any(), Mockito.anyString(),
				Mockito.anyString(), Mockito.anyString(), Mockito.anyLong(),
				Mockito.anyLong(), Mockito.anyBoolean(), Mockito.anyInt(),
				Mockito.anyInt(), Mockito.anyInt(), Mockito.anyString(),
				Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(),
				Mockito.anyBoolean(), Mockito.any())
		).thenReturn(
			_user
		);

		String expected = screenName;

		_newUserLocalServiceWrapper.addUser(
			0, 0, false, "", "", false, screenName, "", 0, "", null, "", "", "",
			0, 0, false, 0, 0, 0, "", null, null, null, null, false, null);

		verify(
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
	public void addUserDeprecated_Calls_BaseMethodImplementationOfAddDefaultAdminUser_With_NullScreenName_When_ScreenNameIsNull()
		throws PortalException {

		String screenName = null;

		when(
			_userLocalService.addUser(
				Mockito.anyLong(), Mockito.anyLong(), Mockito.anyBoolean(),
				Mockito.anyString(), Mockito.anyString(), Mockito.anyBoolean(),
				Mockito.any(), Mockito.anyString(), Mockito.anyLong(),
				Mockito.anyString(), Mockito.any(), Mockito.anyString(),
				Mockito.anyString(), Mockito.anyString(), Mockito.anyLong(),
				Mockito.anyLong(), Mockito.anyBoolean(), Mockito.anyInt(),
				Mockito.anyInt(), Mockito.anyInt(), Mockito.anyString(),
				Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(),
				Mockito.anyBoolean(), Mockito.any())
		).thenReturn(
			_user
		);

		String expected = screenName;

		_newUserLocalServiceWrapper.addUser(
			0, 0, false, "", "", false, screenName, "", 0, "", null, "", "", "",
			0, 0, false, 0, 0, 0, "", null, null, null, null, false, null);

		verify(
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
	public void addUserWithWorkflow_Calls_BaseMethodImplementation_With_ScreenNameWithEmailSuffixAdded_When_ScreenNameIsNotEmail_And_IsNotNull_And_IsNotEmpty()
		throws PortalException {

		String screenName = "user";

		when(
			_userLocalService.addUserWithWorkflow(
				Mockito.anyLong(), Mockito.anyLong(), Mockito.anyBoolean(),
				Mockito.anyString(), Mockito.anyString(), Mockito.anyBoolean(),
				Mockito.any(), Mockito.anyString(), Mockito.any(),
				Mockito.anyString(), Mockito.anyString(), Mockito.anyString(),
				Mockito.anyLong(), Mockito.anyLong(), Mockito.anyBoolean(),
				Mockito.anyInt(), Mockito.anyInt(), Mockito.anyInt(),
				Mockito.anyString(), Mockito.any(), Mockito.any(),
				Mockito.any(), Mockito.any(), Mockito.anyBoolean(),
				Mockito.any())
		).thenReturn(
			_user
		);

		String expected = screenName + _correctEmailSuffix;

		_newUserLocalServiceWrapper.addUserWithWorkflow(
			0, 0, false, "", "", false, screenName, "", null, "", "", "", 0, 0,
			false, 0, 0, 0, "", null, null, null, null, false, null);

		verify(
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

	// addUserWithWorkflow tests

	@Test
	public void addUserWithWorkflow_Calls_BaseMethodImplementation_With_UnmodifiedScreenName_When_ScreenNameIsAnEmailAddress()
		throws PortalException {

		String screenName = "user" + _correctEmailSuffix;

		when(
			_userLocalService.addUserWithWorkflow(
				Mockito.anyLong(), Mockito.anyLong(), Mockito.anyBoolean(),
				Mockito.anyString(), Mockito.anyString(), Mockito.anyBoolean(),
				Mockito.any(), Mockito.anyString(), Mockito.any(),
				Mockito.anyString(), Mockito.anyString(), Mockito.anyString(),
				Mockito.anyLong(), Mockito.anyLong(), Mockito.anyBoolean(),
				Mockito.anyInt(), Mockito.anyInt(), Mockito.anyInt(),
				Mockito.anyString(), Mockito.any(), Mockito.any(),
				Mockito.any(), Mockito.any(), Mockito.anyBoolean(),
				Mockito.any())
		).thenReturn(
			_user
		);

		String expected = screenName;

		_newUserLocalServiceWrapper.addUserWithWorkflow(
			0, 0, false, "", "", false, screenName, "", null, "", "", "", 0, 0,
			false, 0, 0, 0, "", null, null, null, null, false, null);

		verify(
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
	public void addUserWithWorkflow_Calls_BaseMethodImplementationOfAddDefaultAdminUser_With_EmptyScreenName_When_ScreenNameIsEmpty()
		throws PortalException {

		String screenName = "";

		when(
			_userLocalService.addUserWithWorkflow(
				Mockito.anyLong(), Mockito.anyLong(), Mockito.anyBoolean(),
				Mockito.anyString(), Mockito.anyString(), Mockito.anyBoolean(),
				Mockito.any(), Mockito.anyString(), Mockito.any(),
				Mockito.any(), Mockito.anyString(), Mockito.anyString(),
				Mockito.anyLong(), Mockito.anyLong(), Mockito.anyBoolean(),
				Mockito.anyInt(), Mockito.anyInt(), Mockito.anyInt(),
				Mockito.anyString(), Mockito.any(), Mockito.any(),
				Mockito.any(), Mockito.any(), Mockito.anyBoolean(),
				Mockito.any())
		).thenReturn(
			_user
		);

		String expected = screenName;

		_newUserLocalServiceWrapper.addUserWithWorkflow(
			0, 0, false, "", "", false, screenName, "", null, "", "", "", 0, 0,
			false, 0, 0, 0, "", null, null, null, null, false, null);

		verify(
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
	public void addUserWithWorkflow_Calls_BaseMethodImplementationOfAddDefaultAdminUser_With_NullScreenName_When_ScreenNameIsNull()
		throws PortalException {

		String screenName = null;

		when(
			_userLocalService.addUserWithWorkflow(
				Mockito.anyLong(), Mockito.anyLong(), Mockito.anyBoolean(),
				Mockito.anyString(), Mockito.anyString(), Mockito.anyBoolean(),
				Mockito.any(), Mockito.anyString(), Mockito.any(),
				Mockito.any(), Mockito.anyString(), Mockito.anyString(),
				Mockito.anyLong(), Mockito.anyLong(), Mockito.anyBoolean(),
				Mockito.anyInt(), Mockito.anyInt(), Mockito.anyInt(),
				Mockito.anyString(), Mockito.any(), Mockito.any(),
				Mockito.any(), Mockito.any(), Mockito.anyBoolean(),
				Mockito.any())
		).thenReturn(
			_user
		);

		String expected = screenName;

		_newUserLocalServiceWrapper.addUserWithWorkflow(
			0, 0, false, "", "", false, screenName, "", null, "", "", "", 0, 0,
			false, 0, 0, 0, "", null, null, null, null, false, null);

		verify(
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
	public void addUserWithWorkflowDeprecated_Calls_BaseMethodImplementation_With_ScreenNameWithEmailSuffixAdded_When_ScreenNameIsNotEmail_And_IsNotNull_And_IsNotEmpty()
		throws PortalException {

		String screenName = "user";

		when(
			_userLocalService.addUserWithWorkflow(
				Mockito.anyLong(), Mockito.anyLong(), Mockito.anyBoolean(),
				Mockito.anyString(), Mockito.anyString(), Mockito.anyBoolean(),
				Mockito.any(), Mockito.anyString(), Mockito.anyLong(),
				Mockito.anyString(), Mockito.any(), Mockito.anyString(),
				Mockito.anyString(), Mockito.anyString(), Mockito.anyLong(),
				Mockito.anyLong(), Mockito.anyBoolean(), Mockito.anyInt(),
				Mockito.anyInt(), Mockito.anyInt(), Mockito.anyString(),
				Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(),
				Mockito.anyBoolean(), Mockito.any())
		).thenReturn(
			_user
		);

		String expected = screenName + _correctEmailSuffix;

		_newUserLocalServiceWrapper.addUserWithWorkflow(
			0, 0, false, "", "", false, screenName, "", 0, "", null, "", "", "",
			0, 0, false, 0, 0, 0, "", null, null, null, null, false, null);

		verify(
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

	// addUserWithWorkflowDeprecated

	@Test
	public void addUserWithWorkflowDeprecated_Calls_BaseMethodImplementation_With_UnmodifiedScreenName_When_ScreenNameIsAnEmailAddress()
		throws PortalException {

		String screenName = "user" + _correctEmailSuffix;

		when(
			_userLocalService.addUserWithWorkflow(
				Mockito.anyLong(), Mockito.anyLong(), Mockito.anyBoolean(),
				Mockito.anyString(), Mockito.anyString(), Mockito.anyBoolean(),
				Mockito.any(), Mockito.anyString(), Mockito.anyLong(),
				Mockito.anyString(), Mockito.any(), Mockito.anyString(),
				Mockito.anyString(), Mockito.anyString(), Mockito.anyLong(),
				Mockito.anyLong(), Mockito.anyBoolean(), Mockito.anyInt(),
				Mockito.anyInt(), Mockito.anyInt(), Mockito.anyString(),
				Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(),
				Mockito.anyBoolean(), Mockito.any())
		).thenReturn(
			_user
		);

		String expected = screenName;

		_newUserLocalServiceWrapper.addUserWithWorkflow(
			0, 0, false, "", "", false, screenName, "", 0, "", null, "", "", "",
			0, 0, false, 0, 0, 0, "", null, null, null, null, false, null);

		verify(
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

	@Test
	public void addUserWithWorkflowDeprecated_Calls_BaseMethodImplementationOfAddDefaultAdminUser_With_EmptyScreenName_When_ScreenNameIsEmpty()
		throws PortalException {

		String screenName = "";

		when(
			_userLocalService.addUserWithWorkflow(
				Mockito.anyLong(), Mockito.anyLong(), Mockito.anyBoolean(),
				Mockito.anyString(), Mockito.anyString(), Mockito.anyBoolean(),
				Mockito.any(), Mockito.anyString(), Mockito.anyLong(),
				Mockito.anyString(), Mockito.any(), Mockito.anyString(),
				Mockito.anyString(), Mockito.anyString(), Mockito.anyLong(),
				Mockito.anyLong(), Mockito.anyBoolean(), Mockito.anyInt(),
				Mockito.anyInt(), Mockito.anyInt(), Mockito.anyString(),
				Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(),
				Mockito.anyBoolean(), Mockito.any())
		).thenReturn(
			_user
		);

		String expected = screenName;

		_newUserLocalServiceWrapper.addUserWithWorkflow(
			0, 0, false, "", "", false, screenName, "", 0, "", null, "", "", "",
			0, 0, false, 0, 0, 0, "", null, null, null, null, false, null);

		verify(
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

	@Test
	public void addUserWithWorkflowDeprecated_Calls_BaseMethodImplementationOfAddDefaultAdminUser_With_NullScreenName_When_ScreenNameIsNull()
		throws PortalException {

		String screenName = null;

		when(
			_userLocalService.addUserWithWorkflow(
				Mockito.anyLong(), Mockito.anyLong(), Mockito.anyBoolean(),
				Mockito.anyString(), Mockito.anyString(), Mockito.anyBoolean(),
				Mockito.any(), Mockito.anyString(), Mockito.anyLong(),
				Mockito.anyString(), Mockito.any(), Mockito.anyString(),
				Mockito.anyString(), Mockito.anyString(), Mockito.anyLong(),
				Mockito.anyLong(), Mockito.anyBoolean(), Mockito.anyInt(),
				Mockito.anyInt(), Mockito.anyInt(), Mockito.anyString(),
				Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(),
				Mockito.anyBoolean(), Mockito.any())
		).thenReturn(
			_user
		);

		String expected = screenName;

		_newUserLocalServiceWrapper.addUserWithWorkflow(
			0, 0, false, "", "", false, screenName, "", 0, "", null, "", "", "",
			0, 0, false, 0, 0, 0, "", null, null, null, null, false, null);

		verify(
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

	@Before
	public void setup() {
		when(
			_props.get(NewFormatterKeys.USERS_SCREEN_NAME_COMPANY_EMAIL)
		).thenReturn(
			_correctEmailSuffix
		);
	}

	private final String _anyEmailSuffix = "@email.com";
	private final String _correctEmailSuffix = "@company.com";

	@InjectMocks
	private NewUserLocalServiceWrapper _newUserLocalServiceWrapper;

	@Mock
	private Props _props;

	@Mock
	private User _user;

	@Mock
	private UserLocalService _userLocalService;

}