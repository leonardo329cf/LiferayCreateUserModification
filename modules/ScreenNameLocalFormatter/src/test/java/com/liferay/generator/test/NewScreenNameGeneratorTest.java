package com.liferay.generator.test;

import com.liferay.formatter.keys.NewFormatterKeys;
import com.liferay.generator.NewScreenNameGenerator;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.service.GroupLocalServiceUtil;
import com.liferay.portal.kernel.service.UserLocalServiceUtil;
import com.liferay.portal.kernel.util.PrefsPropsUtil;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.PropsUtil;
import com.liferay.portal.kernel.model.GroupWrapper;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.UserWrapper;

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

@RunWith(MockitoJUnitRunner.class)
public class NewScreenNameGeneratorTest {

	private String correctEmail = "@company.com";
	private String adminReservedScreenNames = "ADMIN\nAdmin\nadmin";
	private String[] adminReservedScreenNamesArray = { "ADMIN", "Admin", "admin" };

	@Mock
	UserWrapper userWrapperMock;

	@Mock
	GroupWrapper groupWrapperMock;

	MockedStatic<PropsUtil> propsUtil;

	MockedStatic<PrefsPropsUtil> prefsPropsUtil;

	MockedStatic<UserLocalServiceUtil> userLocalServiceUtil;

	MockedStatic<GroupLocalServiceUtil> groupLocalServiceUtil;

	@Before
	public void init() {
		propsUtil = Mockito.mockStatic(PropsUtil.class);
		prefsPropsUtil = Mockito.mockStatic(PrefsPropsUtil.class);
		userLocalServiceUtil = Mockito.mockStatic(UserLocalServiceUtil.class);
		groupLocalServiceUtil = Mockito.mockStatic(GroupLocalServiceUtil.class);
	}

	@After
	public void close() {
		propsUtil.close();
		prefsPropsUtil.close();
		userLocalServiceUtil.close();
		groupLocalServiceUtil.close();
	}

	@Test
	public void testGenerate_AddsEmailSufix_When_ScreenNameIsUnusedByOtherUserOrGroup_And_ScreenNameDoesNotContainsReservedWords()
			throws Exception {
		// Arrange
		String input = "user@liferay.com";
		String expected = "user" + correctEmail;
		propsUtil.when(() -> PropsUtil.get(NewFormatterKeys.USERS_SCREEN_NAME_COMPANY_EMAIL)).thenReturn(correctEmail);
		propsUtil.when(() -> PropsUtil.get(PropsKeys.ADMIN_RESERVED_SCREEN_NAMES)).thenReturn(adminReservedScreenNames);

		prefsPropsUtil.when(() -> PrefsPropsUtil.getStringArray(Mockito.anyLong(), Mockito.anyString(),
				Mockito.anyString(), Mockito.any())).thenReturn(adminReservedScreenNamesArray);

		userLocalServiceUtil
				.when(() -> UserLocalServiceUtil.fetchUserByScreenName(Mockito.anyLong(), Mockito.anyString()))
				.thenReturn(null);

		groupLocalServiceUtil
				.when(() -> GroupLocalServiceUtil.fetchFriendlyURLGroup(Mockito.anyLong(), Mockito.anyString()))
				.thenReturn(null);

		NewScreenNameGenerator newScreenNameGenerator = new NewScreenNameGenerator();

		// Act
		String result = newScreenNameGenerator.generate(0L, 0L, input);

		// Assert
		Assert.assertEquals(expected, result);

	}

	@Test
	public void testGenerate_CreatesNewScreenName_When_ScreenNameIsAlreadyUseByUser() throws Exception {
		// Arrange
		String input = "user@liferay.com";
		String prefix = "user";
		String expected = prefix + "1" + correctEmail;

		propsUtil.when(() -> PropsUtil.get(NewFormatterKeys.USERS_SCREEN_NAME_COMPANY_EMAIL)).thenReturn(correctEmail);
		propsUtil.when(() -> PropsUtil.get(PropsKeys.ADMIN_RESERVED_SCREEN_NAMES)).thenReturn(adminReservedScreenNames);

		prefsPropsUtil.when(() -> PrefsPropsUtil.getStringArray(Mockito.anyLong(), Mockito.anyString(),
				Mockito.anyString(), Mockito.any())).thenReturn(adminReservedScreenNamesArray);

		userLocalServiceUtil.when(
				() -> UserLocalServiceUtil.fetchUserByScreenName(Mockito.anyLong(), Mockito.eq(prefix + correctEmail)))
				.thenReturn(userWrapperMock);

		userLocalServiceUtil.when(
				() -> UserLocalServiceUtil.fetchUserByScreenName(Mockito.anyLong(), AdditionalMatchers.not(Mockito.eq(prefix + correctEmail))))
				.thenReturn(null);

		groupLocalServiceUtil
				.when(() -> GroupLocalServiceUtil.fetchFriendlyURLGroup(Mockito.anyLong(), Mockito.anyString()))
				.thenReturn(null);

		NewScreenNameGenerator newScreenNameGenerator = new NewScreenNameGenerator();

		// Act
		String result = newScreenNameGenerator.generate(0L, 0L, input);

		// Assert
		Assert.assertEquals(expected, result);

	}

	@Test
	public void testGenerate_CreatesNewScreenName_When_ScreenNameIsAlreadyUseByGroup() throws Exception {
		// Arrange
		String input = "user@liferay.com";
		String prefix = "user";
		String expected = "user1" + correctEmail;

		propsUtil.when(() -> PropsUtil.get(NewFormatterKeys.USERS_SCREEN_NAME_COMPANY_EMAIL)).thenReturn(correctEmail);
		propsUtil.when(() -> PropsUtil.get(PropsKeys.ADMIN_RESERVED_SCREEN_NAMES)).thenReturn(adminReservedScreenNames);

		prefsPropsUtil.when(() -> PrefsPropsUtil.getStringArray(Mockito.anyLong(), Mockito.anyString(),
				Mockito.anyString(), Mockito.any())).thenReturn(adminReservedScreenNamesArray);

		userLocalServiceUtil
				.when(() -> UserLocalServiceUtil.fetchUserByScreenName(Mockito.anyLong(), Mockito.anyString()))
				.thenReturn(null);

		groupLocalServiceUtil
				.when(() -> GroupLocalServiceUtil.fetchFriendlyURLGroup(
						Mockito.anyLong(),
						Mockito.eq(StringPool.SLASH + prefix + correctEmail)))
				.thenReturn(groupWrapperMock);
		groupLocalServiceUtil
				.when(() -> GroupLocalServiceUtil.fetchFriendlyURLGroup(
						Mockito.anyLong(),
						Mockito.eq(expected)))
				.thenReturn(null);

		NewScreenNameGenerator newScreenNameGenerator = new NewScreenNameGenerator();

		// Act
		String result = newScreenNameGenerator.generate(0L, 0L, input);

		// Assert
		Assert.assertEquals(expected, result);

	}
}
