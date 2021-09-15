package com.liferay.formatter.test;

import static org.junit.Assert.assertEquals;

import com.liferay.formatter.NewUserLocalServiceWrapper;
import com.liferay.formatter.keys.NewFormatterKeys;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.util.Props;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class NewUserLocalServiceWrapperTest {

	@Mock
	private Props props;

	@Mock
	private UserLocalService userLocalService;
	
	@Mock
	private User user;

	@InjectMocks
	private NewUserLocalServiceWrapper newUserLocalServiceWrapper;

	private final String correctEmailSufix = "@company.com";

	@Test
	public void addDefaultAdminUser_Calls_userLocalService_addDefaultAdminUser_With_addEmailIfIsNotEmail_Result()
			throws PortalException {

		// Arrange
		String screenName = "user";

		Mockito.when(props.get(NewFormatterKeys.USERS_SCREEN_NAME_COMPANY_EMAIL)).thenReturn(correctEmailSufix);

		Mockito.when(userLocalService.addDefaultAdminUser(Mockito.anyLong(), Mockito.anyString(), Mockito.anyString(),
				Mockito.any(), Mockito.anyString(), Mockito.anyString(), Mockito.anyString())).thenReturn(user);

		String expected = newUserLocalServiceWrapper.addEmailIfIsNotEmail(screenName);

		// Act
		newUserLocalServiceWrapper.addDefaultAdminUser(0L, screenName, "user@email.com", null, "user",
				"user", "user");

		// Assert
		Mockito.verify(userLocalService).addDefaultAdminUser(Mockito.anyLong(), Mockito.eq(expected),
				Mockito.anyString(), Mockito.any(), Mockito.anyString(), Mockito.anyString(), Mockito.anyString());
	}

	@Test
	public void addUser_Calls_userLocalService_addUser_With_addEmailIfIsNotEmail_Result() throws PortalException {
		
		// Arrange
		String screenName = "user";
		
		Mockito.when(props.get(NewFormatterKeys.USERS_SCREEN_NAME_COMPANY_EMAIL)).thenReturn(correctEmailSufix);
		
		Mockito.when(userLocalService.addUser(
				Mockito.anyLong(),
				Mockito.anyLong(),
				Mockito.anyBoolean(), 
				Mockito.anyString(), 
				Mockito.anyString(), 
				Mockito.anyBoolean(), 
				Mockito.anyString(), 
				Mockito.anyString(), 
				Mockito.any(), 
				Mockito.anyString(), 
				Mockito.anyString(), 
				Mockito.anyString(), 
				Mockito.anyLong(),
				Mockito.anyLong(), 
				Mockito.anyBoolean(), 
				Mockito.anyInt(), 
				Mockito.anyInt(), 
				Mockito.anyInt(), 
				Mockito.anyString(), 
				Mockito.any(), 
				Mockito.any(), 
				Mockito.any(), 
				Mockito.any(), 
				Mockito.anyBoolean(), 
				Mockito.any()))
				.thenReturn(user);
		
		String expected = newUserLocalServiceWrapper.addEmailIfIsNotEmail(screenName);
		
		// Act
		newUserLocalServiceWrapper.addUser(
				0,
				0, 
				false, 
				"", 
				"", 
				false, 
				screenName, 
				"", 
				null, 
				"", 
				"", 
				"", 
				0, 
				0, 
				false, 
				0, 
				0, 
				0, 
				"", 
				null,
				null,
				null,
				null, 
				false, 
				null);
		
		// Assert		
		Mockito.verify(userLocalService).addUser(
				Mockito.anyLong(),
				Mockito.anyLong(),
				Mockito.anyBoolean(), 
				Mockito.anyString(), 
				Mockito.anyString(), 
				Mockito.anyBoolean(), 
				Mockito.eq(expected), 
				Mockito.anyString(), 
				Mockito.any(), 
				Mockito.anyString(), 
				Mockito.anyString(), 
				Mockito.anyString(), 
				Mockito.anyLong(),
				Mockito.anyLong(), 
				Mockito.anyBoolean(), 
				Mockito.anyInt(), 
				Mockito.anyInt(), 
				Mockito.anyInt(), 
				Mockito.anyString(), 
				Mockito.any(), 
				Mockito.any(), 
				Mockito.any(), 
				Mockito.any(), 
				Mockito.anyBoolean(), 
				Mockito.any());
	}
	
	@Test
	public void addUserDeprecated_Calls_userLocalService_addUserDeprecated_With_addEmailIfIsNotEmail_Result() throws PortalException {
		
		// Arrange
		String screenName = "user";
		
		Mockito.when(props.get(NewFormatterKeys.USERS_SCREEN_NAME_COMPANY_EMAIL)).thenReturn(correctEmailSufix);
		
		Mockito.when(userLocalService.addUser(
				Mockito.anyLong(),
				Mockito.anyLong(),
				Mockito.anyBoolean(), 
				Mockito.anyString(), 
				Mockito.anyString(), 
				Mockito.anyBoolean(), 
				Mockito.anyString(), 
				Mockito.anyString(), 
				Mockito.anyLong(), 
				Mockito.anyString(),
				Mockito.any(),
				Mockito.anyString(), 
				Mockito.anyString(), 
				Mockito.anyString(), 
				Mockito.anyLong(),
				Mockito.anyLong(), 
				Mockito.anyBoolean(), 
				Mockito.anyInt(), 
				Mockito.anyInt(), 
				Mockito.anyInt(), 
				Mockito.anyString(), 
				Mockito.any(), 
				Mockito.any(), 
				Mockito.any(), 
				Mockito.any(), 
				Mockito.anyBoolean(), 
				Mockito.any()))
				.thenReturn(user);
		
		String expected = newUserLocalServiceWrapper.addEmailIfIsNotEmail(screenName);
		
		// Act
		newUserLocalServiceWrapper.addUser(
				0, 
				0, 
				false, 
				"", 
				"", 
				false, 
				screenName, 
				"", 
				0, 
				"", 
				null, 
				"", 
				"", 
				"", 
				0, 
				0, 
				false, 
				0, 
				0, 
				0, 
				"", 
				null, 
				null, 
				null, 
				null, 
				false, 
				null);
		
		// Assert		
		Mockito.verify(userLocalService).addUser(
				Mockito.anyLong(),
				Mockito.anyLong(),
				Mockito.anyBoolean(), 
				Mockito.anyString(), 
				Mockito.anyString(), 
				Mockito.anyBoolean(), 
				Mockito.eq(expected), 
				Mockito.anyString(), 
				Mockito.anyLong(), 
				Mockito.anyString(),
				Mockito.any(),
				Mockito.anyString(), 
				Mockito.anyString(), 
				Mockito.anyString(), 
				Mockito.anyLong(),
				Mockito.anyLong(), 
				Mockito.anyBoolean(), 
				Mockito.anyInt(), 
				Mockito.anyInt(), 
				Mockito.anyInt(), 
				Mockito.anyString(), 
				Mockito.any(), 
				Mockito.any(), 
				Mockito.any(), 
				Mockito.any(), 
				Mockito.anyBoolean(), 
				Mockito.any());
	}
	
	@Test
	public void addUserWithWorkflow_Calls_userLocalService_addUserWithWorkflow_With_addEmailIfIsNotEmail_Result() throws PortalException {
		
		// Arrange
		String screenName = "user";
		
		Mockito.when(props.get(NewFormatterKeys.USERS_SCREEN_NAME_COMPANY_EMAIL)).thenReturn(correctEmailSufix);
		
		Mockito.when(userLocalService.addUserWithWorkflow(
				Mockito.anyLong(),
				Mockito.anyLong(),
				Mockito.anyBoolean(), 
				Mockito.anyString(), 
				Mockito.anyString(), 
				Mockito.anyBoolean(), 
				Mockito.anyString(), 
				Mockito.anyString(), 
				Mockito.any(), 
				Mockito.anyString(), 
				Mockito.anyString(), 
				Mockito.anyString(), 
				Mockito.anyLong(),
				Mockito.anyLong(), 
				Mockito.anyBoolean(), 
				Mockito.anyInt(), 
				Mockito.anyInt(), 
				Mockito.anyInt(), 
				Mockito.anyString(), 
				Mockito.any(), 
				Mockito.any(), 
				Mockito.any(), 
				Mockito.any(), 
				Mockito.anyBoolean(), 
				Mockito.any()))
				.thenReturn(user);
		
		String expected = newUserLocalServiceWrapper.addEmailIfIsNotEmail(screenName);
		
		// Act
		newUserLocalServiceWrapper.addUserWithWorkflow(
				0,
				0, 
				false, 
				"", 
				"", 
				false, 
				screenName, 
				"", 
				null, 
				"", 
				"", 
				"", 
				0, 
				0, 
				false, 
				0, 
				0, 
				0, 
				"", 
				null,
				null,
				null,
				null, 
				false, 
				null);
		
		// Assert		
		Mockito.verify(userLocalService).addUserWithWorkflow(
				Mockito.anyLong(),
				Mockito.anyLong(),
				Mockito.anyBoolean(), 
				Mockito.anyString(), 
				Mockito.anyString(), 
				Mockito.anyBoolean(), 
				Mockito.eq(expected), 
				Mockito.anyString(), 
				Mockito.any(), 
				Mockito.anyString(), 
				Mockito.anyString(), 
				Mockito.anyString(), 
				Mockito.anyLong(),
				Mockito.anyLong(), 
				Mockito.anyBoolean(), 
				Mockito.anyInt(), 
				Mockito.anyInt(), 
				Mockito.anyInt(), 
				Mockito.anyString(), 
				Mockito.any(), 
				Mockito.any(), 
				Mockito.any(), 
				Mockito.any(), 
				Mockito.anyBoolean(), 
				Mockito.any());
	}
	
	
	
	@Test
	public void addUserWithWorkflowDeprecated_Calls_userLocalService_addUserWithWorkflowDeprecated_With_addEmailIfIsNotEmail_Result() throws PortalException {
		
		// Arrange
		String screenName = "user";
		
		Mockito.when(props.get(NewFormatterKeys.USERS_SCREEN_NAME_COMPANY_EMAIL)).thenReturn(correctEmailSufix);
		
		Mockito.when(userLocalService.addUserWithWorkflow(
				Mockito.anyLong(),
				Mockito.anyLong(),
				Mockito.anyBoolean(), 
				Mockito.anyString(), 
				Mockito.anyString(), 
				Mockito.anyBoolean(), 
				Mockito.anyString(), 
				Mockito.anyString(), 
				Mockito.anyLong(), 
				Mockito.anyString(),
				Mockito.any(),
				Mockito.anyString(), 
				Mockito.anyString(), 
				Mockito.anyString(), 
				Mockito.anyLong(),
				Mockito.anyLong(), 
				Mockito.anyBoolean(), 
				Mockito.anyInt(), 
				Mockito.anyInt(), 
				Mockito.anyInt(), 
				Mockito.anyString(), 
				Mockito.any(), 
				Mockito.any(), 
				Mockito.any(), 
				Mockito.any(), 
				Mockito.anyBoolean(), 
				Mockito.any()))
				.thenReturn(user);
		
		String expected = newUserLocalServiceWrapper.addEmailIfIsNotEmail(screenName);
		
		// Act
		newUserLocalServiceWrapper.addUserWithWorkflow(
				0, 
				0, 
				false, 
				"", 
				"", 
				false, 
				screenName, 
				"", 
				0, 
				"", 
				null, 
				"", 
				"", 
				"", 
				0, 
				0, 
				false, 
				0, 
				0, 
				0, 
				"", 
				null, 
				null, 
				null, 
				null, 
				false, 
				null);
		
		// Assert		
		Mockito.verify(userLocalService).addUserWithWorkflow(
				Mockito.anyLong(),
				Mockito.anyLong(),
				Mockito.anyBoolean(), 
				Mockito.anyString(), 
				Mockito.anyString(), 
				Mockito.anyBoolean(), 
				Mockito.eq(expected), 
				Mockito.anyString(), 
				Mockito.anyLong(), 
				Mockito.anyString(),
				Mockito.any(),
				Mockito.anyString(), 
				Mockito.anyString(), 
				Mockito.anyString(), 
				Mockito.anyLong(),
				Mockito.anyLong(), 
				Mockito.anyBoolean(), 
				Mockito.anyInt(), 
				Mockito.anyInt(), 
				Mockito.anyInt(), 
				Mockito.anyString(), 
				Mockito.any(), 
				Mockito.any(), 
				Mockito.any(), 
				Mockito.any(), 
				Mockito.anyBoolean(), 
				Mockito.any());
	}

	@Test
	public void addEmailIfIsNotEmail_Returns_ScreenNameConcatWithCompanyEmail_When_ScreenName_IsNotEmail_Or_IsNotNull_Or_IsNotEmpty() {
		// Arrange
		String screenName = "user";
		String expected = "user@company.com";
		Mockito.when(props.get(NewFormatterKeys.USERS_SCREEN_NAME_COMPANY_EMAIL)).thenReturn(correctEmailSufix);
				
		// Act
		String actual = newUserLocalServiceWrapper.addEmailIfIsNotEmail(screenName);
		
		//Assert
		assertEquals(expected, actual);
	}
	
	@Test
	public void addEmailIfIsNotEmail_Returns_ScreenName_When_ScreenName_IsEmail() {
		// Arrange
		String screenName = "user@email.com";
		String expected = "user@email.com";
				
		// Act
		String actual = newUserLocalServiceWrapper.addEmailIfIsNotEmail(screenName);
		
		//Assert
		assertEquals(expected, actual);
	}
	
	@Test
	public void addEmailIfIsNotEmail_Returns_ScreenName_When_ScreenName_IsNull() {
		// Arrange
		String screenName = null;
		String expected = null;
				
		// Act
		String actual = newUserLocalServiceWrapper.addEmailIfIsNotEmail(screenName);
		
		//Assert
		assertEquals(expected, actual);
	}
	
	@Test
	public void addEmailIfIsNotEmail_Returns_ScreenName_When_ScreenName_IsEmpty() {
		// Arrange
		String screenName = "";
		String expected = "";
				
		// Act
		String actual = newUserLocalServiceWrapper.addEmailIfIsNotEmail(screenName);
		
		//Assert
		assertEquals(expected, actual);
	}
}
