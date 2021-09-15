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

package com.liferay.formatter.service;

import com.liferay.formatter.keys.NewFormatterKeys;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceWrapper;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.service.UserLocalServiceWrapper;
import com.liferay.portal.kernel.util.Props;
import com.liferay.portal.kernel.util.Validator;

import java.util.Locale;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author leonardo.ferreira
 */
@Component(immediate = true, property = {}, service = ServiceWrapper.class)
public class NewUserLocalServiceWrapper extends UserLocalServiceWrapper {

	public NewUserLocalServiceWrapper() {
		super(null);
	}

	/**
	 * Adds a default admin user for the company.
	 *
	 * @param companyId    the primary key of the user's company
	 * @param screenName   the user's screen name
	 * @param emailAddress the user's email address
	 * @param locale       the user's locale
	 * @param firstName    the user's first name
	 * @param middleName   the user's middle name
	 * @param lastName     the user's last name
	 * @return the new default admin user
	 */
	@Override
	public User addDefaultAdminUser(
			long companyId, String screenName, String emailAddress,
			Locale locale, String firstName, String middleName, String lastName)
		throws PortalException {

		String screenNamewithEmail = addEmailIfIsNotEmail(screenName);

		return getWrappedService().addDefaultAdminUser(
			companyId, screenNamewithEmail, emailAddress, locale, firstName,
			middleName, lastName);
	}

	public String addEmailIfIsNotEmail(String screenName) {
		if ((screenName != null) && !screenName.equals("") &&
			!Validator.isEmailAddress(screenName)) {

			screenName = screenName.concat(
				_props.get(NewFormatterKeys.USERS_SCREEN_NAME_COMPANY_EMAIL));
		}

		return screenName;
	}

	/**
	 * Adds a user.
	 *
	 * <p>
	 * This method handles the creation and bookkeeping of the user including its
	 * resources, metadata, and internal data structures. It is not necessary to
	 * make subsequent calls to any methods to setup default groups, resources, etc.
	 * </p>
	 *
	 * @param creatorUserId   the primary key of the creator
	 * @param companyId       the primary key of the user's company
	 * @param autoPassword    whether a password should be automatically generated
	 *                        for the user
	 * @param password1       the user's password
	 * @param password2       the user's password confirmation
	 * @param autoScreenName  whether a screen name should be automatically
	 *                        generated for the user
	 * @param screenName      the user's screen name
	 * @param emailAddress    the user's email address
	 * @param locale          the user's locale
	 * @param firstName       the user's first name
	 * @param middleName      the user's middle name
	 * @param lastName        the user's last name
	 * @param prefixId        the user's name prefix ID
	 * @param suffixId        the user's name suffix ID
	 * @param male            whether the user is male
	 * @param birthdayMonth   the user's birthday month (0-based, meaning 0 for
	 *                        January)
	 * @param birthdayDay     the user's birthday day
	 * @param birthdayYear    the user's birthday year
	 * @param jobTitle        the user's job title
	 * @param groupIds        the primary keys of the user's groups
	 * @param organizationIds the primary keys of the user's organizations
	 * @param roleIds         the primary keys of the roles this user possesses
	 * @param userGroupIds    the primary keys of the user's user groups
	 * @param sendEmail       whether to send the user an email notification about
	 *                        their new account
	 * @param serviceContext  the service context to be applied (optionally
	 *                        <code>null</code>). Can set the UUID (with the
	 *                        <code>uuid</code> attribute), asset category IDs,
	 *                        asset tag names, and expando bridge attributes for the
	 *                        user.
	 * @return the new user
	 */
	@Override
	public User addUser(
			long creatorUserId, long companyId, boolean autoPassword,
			String password1, String password2, boolean autoScreenName,
			String screenName, String emailAddress, Locale locale,
			String firstName, String middleName, String lastName, long prefixId,
			long suffixId, boolean male, int birthdayMonth, int birthdayDay,
			int birthdayYear, String jobTitle, long[] groupIds,
			long[] organizationIds, long[] roleIds, long[] userGroupIds,
			boolean sendEmail, ServiceContext serviceContext)
		throws PortalException {

		String screenNamewithEmail = addEmailIfIsNotEmail(screenName);

		return getWrappedService().addUser(
			creatorUserId, companyId, autoPassword, password1, password2,
			autoScreenName, screenNamewithEmail, emailAddress, locale,
			firstName, middleName, lastName, prefixId, suffixId, male,
			birthdayMonth, birthdayDay, birthdayYear, jobTitle, groupIds,
			organizationIds, roleIds, userGroupIds, sendEmail, serviceContext);
	}

	/**
	 * Adds a user.
	 *
	 * <p>
	 * This method handles the creation and bookkeeping of the user including its
	 * resources, metadata, and internal data structures. It is not necessary to
	 * make subsequent calls to any methods to setup default groups, resources, etc.
	 * </p>
	 *
	 * @param creatorUserId   the primary key of the creator
	 * @param companyId       the primary key of the user's company
	 * @param autoPassword    whether a password should be automatically generated
	 *                        for the user
	 * @param password1       the user's password
	 * @param password2       the user's password confirmation
	 * @param autoScreenName  whether a screen name should be automatically
	 *                        generated for the user
	 * @param screenName      the user's screen name
	 * @param emailAddress    the user's email address
	 * @param facebookId      the user's facebook ID
	 * @param openId          the user's OpenID
	 * @param locale          the user's locale
	 * @param firstName       the user's first name
	 * @param middleName      the user's middle name
	 * @param lastName        the user's last name
	 * @param prefixId        the user's name prefix ID
	 * @param suffixId        the user's name suffix ID
	 * @param male            whether the user is male
	 * @param birthdayMonth   the user's birthday month (0-based, meaning 0 for
	 *                        January)
	 * @param birthdayDay     the user's birthday day
	 * @param birthdayYear    the user's birthday year
	 * @param jobTitle        the user's job title
	 * @param groupIds        the primary keys of the user's groups
	 * @param organizationIds the primary keys of the user's organizations
	 * @param roleIds         the primary keys of the roles this user possesses
	 * @param userGroupIds    the primary keys of the user's user groups
	 * @param sendEmail       whether to send the user an email notification about
	 *                        their new account
	 * @param serviceContext  the service context to be applied (optionally
	 *                        <code>null</code>). Can set the UUID (with the
	 *                        <code>uuid</code> attribute), asset category IDs,
	 *                        asset tag names, and expando bridge attributes for the
	 *                        user.
	 * @return the new user
	 * @deprecated As of Athanasius (7.3.x), replaced by
	 *             {@link #addUser(long, long, boolean, String, String, boolean, String, String, Locale, String, String, String, long, long, boolean, int, int, int, String, long[], long[], long[], long[], boolean, ServiceContext)}
	 */
	@Deprecated
	@Override
	public User addUser(
			long creatorUserId, long companyId, boolean autoPassword,
			String password1, String password2, boolean autoScreenName,
			String screenName, String emailAddress, long facebookId,
			String openId, Locale locale, String firstName, String middleName,
			String lastName, long prefixId, long suffixId, boolean male,
			int birthdayMonth, int birthdayDay, int birthdayYear,
			String jobTitle, long[] groupIds, long[] organizationIds,
			long[] roleIds, long[] userGroupIds, boolean sendEmail,
			ServiceContext serviceContext)
		throws PortalException {

		String screenNamewithEmail = addEmailIfIsNotEmail(screenName);

		return getWrappedService().addUser(
			creatorUserId, companyId, autoPassword, password1, password2,
			autoScreenName, screenNamewithEmail, emailAddress, facebookId,
			openId, locale, firstName, middleName, lastName, prefixId, suffixId,
			male, birthdayMonth, birthdayDay, birthdayYear, jobTitle, groupIds,
			organizationIds, roleIds, userGroupIds, sendEmail, serviceContext);
	}

	/**
	 * Adds a user with workflow.
	 *
	 * <p>
	 * This method handles the creation and bookkeeping of the user including its
	 * resources, metadata, and internal data structures. It is not necessary to
	 * make subsequent calls to any methods to setup default groups, resources, etc.
	 * </p>
	 *
	 * @param creatorUserId   the primary key of the creator
	 * @param companyId       the primary key of the user's company
	 * @param autoPassword    whether a password should be automatically generated
	 *                        for the user
	 * @param password1       the user's password
	 * @param password2       the user's password confirmation
	 * @param autoScreenName  whether a screen name should be automatically
	 *                        generated for the user
	 * @param screenName      the user's screen name
	 * @param emailAddress    the user's email address
	 * @param locale          the user's locale
	 * @param firstName       the user's first name
	 * @param middleName      the user's middle name
	 * @param lastName        the user's last name
	 * @param prefixId        the user's name prefix ID
	 * @param suffixId        the user's name suffix ID
	 * @param male            whether the user is male
	 * @param birthdayMonth   the user's birthday month (0-based, meaning 0 for
	 *                        January)
	 * @param birthdayDay     the user's birthday day
	 * @param birthdayYear    the user's birthday year
	 * @param jobTitle        the user's job title
	 * @param groupIds        the primary keys of the user's groups
	 * @param organizationIds the primary keys of the user's organizations
	 * @param roleIds         the primary keys of the roles this user possesses
	 * @param userGroupIds    the primary keys of the user's user groups
	 * @param sendEmail       whether to send the user an email notification about
	 *                        their new account
	 * @param serviceContext  the service context to be applied (optionally
	 *                        <code>null</code>). Can set the UUID (with the
	 *                        <code>uuid</code> attribute), asset category IDs,
	 *                        asset tag names, and expando bridge attributes for the
	 *                        user.
	 * @return the new user
	 */
	@Override
	public User addUserWithWorkflow(
			long creatorUserId, long companyId, boolean autoPassword,
			String password1, String password2, boolean autoScreenName,
			String screenName, String emailAddress, Locale locale,
			String firstName, String middleName, String lastName, long prefixId,
			long suffixId, boolean male, int birthdayMonth, int birthdayDay,
			int birthdayYear, String jobTitle, long[] groupIds,
			long[] organizationIds, long[] roleIds, long[] userGroupIds,
			boolean sendEmail, ServiceContext serviceContext)
		throws PortalException {

		String screenNamewithEmail = addEmailIfIsNotEmail(screenName);

		return getWrappedService().addUserWithWorkflow(
			creatorUserId, companyId, autoPassword, password1, password2,
			autoScreenName, screenNamewithEmail, emailAddress, locale,
			firstName, middleName, lastName, prefixId, suffixId, male,
			birthdayMonth, birthdayDay, birthdayYear, jobTitle, groupIds,
			organizationIds, roleIds, userGroupIds, sendEmail, serviceContext);
	}

	//
	//
	//
	//
	//
	//	Util methods:
	//
	//
	//
	//
	//
	/**
	 * Adds a user with workflow.
	 *
	 * <p>
	 * This method handles the creation and bookkeeping of the user including its
	 * resources, metadata, and internal data structures. It is not necessary to
	 * make subsequent calls to any methods to setup default groups, resources, etc.
	 * </p>
	 *
	 * @param creatorUserId   the primary key of the creator
	 * @param companyId       the primary key of the user's company
	 * @param autoPassword    whether a password should be automatically generated
	 *                        for the user
	 * @param password1       the user's password
	 * @param password2       the user's password confirmation
	 * @param autoScreenName  whether a screen name should be automatically
	 *                        generated for the user
	 * @param screenName      the user's screen name
	 * @param emailAddress    the user's email address
	 * @param facebookId      the user's facebook ID
	 * @param openId          the user's OpenID
	 * @param locale          the user's locale
	 * @param firstName       the user's first name
	 * @param middleName      the user's middle name
	 * @param lastName        the user's last name
	 * @param prefixId        the user's name prefix ID
	 * @param suffixId        the user's name suffix ID
	 * @param male            whether the user is male
	 * @param birthdayMonth   the user's birthday month (0-based, meaning 0 for
	 *                        January)
	 * @param birthdayDay     the user's birthday day
	 * @param birthdayYear    the user's birthday year
	 * @param jobTitle        the user's job title
	 * @param groupIds        the primary keys of the user's groups
	 * @param organizationIds the primary keys of the user's organizations
	 * @param roleIds         the primary keys of the roles this user possesses
	 * @param userGroupIds    the primary keys of the user's user groups
	 * @param sendEmail       whether to send the user an email notification about
	 *                        their new account
	 * @param serviceContext  the service context to be applied (optionally
	 *                        <code>null</code>). Can set the UUID (with the
	 *                        <code>uuid</code> attribute), asset category IDs,
	 *                        asset tag names, and expando bridge attributes for the
	 *                        user.
	 * @return the new user
	 * @deprecated As of Athanasius (7.3.x), replaced by
	 *             {@link #addUserWithWorkflow(long, long, boolean, String, String, boolean, String, String, Locale, String, String, String, long, long, boolean, int, int, int, String, long[], long[], long[], long[], boolean, ServiceContext)}
	 */
	@Deprecated
	@Override
	public User addUserWithWorkflow(
			long creatorUserId, long companyId, boolean autoPassword,
			String password1, String password2, boolean autoScreenName,
			String screenName, String emailAddress, long facebookId,
			String openId, Locale locale, String firstName, String middleName,
			String lastName, long prefixId, long suffixId, boolean male,
			int birthdayMonth, int birthdayDay, int birthdayYear,
			String jobTitle, long[] groupIds, long[] organizationIds,
			long[] roleIds, long[] userGroupIds, boolean sendEmail,
			ServiceContext serviceContext)
		throws PortalException {

		String screenNamewithEmail = addEmailIfIsNotEmail(screenName);

		return getWrappedService().addUserWithWorkflow(
			creatorUserId, companyId, autoPassword, password1, password2,
			autoScreenName, screenNamewithEmail, emailAddress, facebookId,
			openId, locale, firstName, middleName, lastName, prefixId, suffixId,
			male, birthdayMonth, birthdayDay, birthdayYear, jobTitle, groupIds,
			organizationIds, roleIds, userGroupIds, sendEmail, serviceContext);
	}

	//
	//
	//
	//
	//
	//	References
	//
	//
	//
	//
	//
	@Reference(unbind = "-")
	private void _serviceSetter(UserLocalService userLocalService) {
		setWrappedService(userLocalService);
	}

	@Reference
	private Props _props;

}