package com.liferay.formatter;

import com.liferay.mail.kernel.service.MailService;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.CompanyMaxUsersException;
import com.liferay.portal.kernel.exception.ContactBirthdayException;
import com.liferay.portal.kernel.exception.ContactNameException;
import com.liferay.portal.kernel.exception.DuplicateGoogleUserIdException;
import com.liferay.portal.kernel.exception.DuplicateOpenIdException;
import com.liferay.portal.kernel.exception.NoSuchOrganizationException;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.exception.UserEmailAddressException;
import com.liferay.portal.kernel.exception.UserPasswordException;
import com.liferay.portal.kernel.exception.UserReminderQueryException;
import com.liferay.portal.kernel.exception.UserScreenNameException;
import com.liferay.portal.kernel.exception.UserSmsException;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.messaging.DestinationNames;
import com.liferay.portal.kernel.messaging.Message;
import com.liferay.portal.kernel.messaging.MessageBusUtil;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.Contact;
import com.liferay.portal.kernel.model.ContactConstants;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.GroupConstants;
import com.liferay.portal.kernel.model.Organization;
import com.liferay.portal.kernel.model.PasswordPolicy;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.Team;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.UserGroupRole;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.search.Indexer;
import com.liferay.portal.kernel.search.IndexerRegistryUtil;
import com.liferay.portal.kernel.search.SearchException;
import com.liferay.portal.kernel.security.auth.EmailAddressGenerator;
import com.liferay.portal.kernel.security.auth.EmailAddressValidator;
import com.liferay.portal.kernel.security.auth.FullNameDefinition;
import com.liferay.portal.kernel.security.auth.FullNameDefinitionFactory;
import com.liferay.portal.kernel.security.auth.FullNameGenerator;
import com.liferay.portal.kernel.security.auth.FullNameGeneratorFactory;
import com.liferay.portal.kernel.security.auth.FullNameValidator;
import com.liferay.portal.kernel.security.auth.PasswordModificationThreadLocal;
import com.liferay.portal.kernel.security.auth.ScreenNameGenerator;
import com.liferay.portal.kernel.security.auth.ScreenNameValidator;
import com.liferay.portal.kernel.security.pwd.PasswordEncryptorUtil;
import com.liferay.portal.kernel.service.BaseServiceImpl;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceWrapper;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.service.UserLocalServiceWrapper;
import com.liferay.portal.kernel.service.persistence.CompanyPersistence;
import com.liferay.portal.kernel.service.persistence.ContactPersistence;
import com.liferay.portal.kernel.service.persistence.OrganizationPersistence;
import com.liferay.portal.kernel.service.persistence.RolePersistence;
import com.liferay.portal.kernel.service.persistence.TeamPersistence;
import com.liferay.portal.kernel.service.persistence.UserGroupRolePersistence;
import com.liferay.portal.kernel.service.persistence.UserPersistence;
import com.liferay.portal.kernel.transaction.TransactionCommitCallbackUtil;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.FriendlyURLNormalizerUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.PrefsPropsUtil;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.PwdGenerator;
import com.liferay.portal.kernel.util.ServiceProxyFactory;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.UnicodeProperties;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.kernel.workflow.WorkflowHandlerRegistryUtil;
import com.liferay.portal.kernel.workflow.WorkflowThreadLocal;
import com.liferay.portal.model.impl.LayoutImpl;
import com.liferay.portal.security.pwd.PwdToolkitUtil;
import com.liferay.portal.util.PropsValues;
import com.liferay.users.admin.kernel.file.uploads.UserFileUploadsSettings;
import com.liferay.users.admin.kernel.util.UsersAdminUtil;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferencePolicy;
import org.osgi.service.component.annotations.ReferencePolicyOption;

/**
 * @author leonardo.ferreira
 */
@Component(immediate = true, property = {}, service = ServiceWrapper.class)
public class ScreenNameLocalFormatter extends UserLocalServiceWrapper {

	public ScreenNameLocalFormatter() {
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
	public User addDefaultAdminUser(long companyId, String screenName, String emailAddress, Locale locale,
			String firstName, String middleName, String lastName) throws PortalException {

		long creatorUserId = 0;
		boolean autoPassword = false;

		String password1 = PropsValues.DEFAULT_ADMIN_PASSWORD;

		String password2 = password1;

		boolean autoScreenName = false;

		screenName = getLogin(screenName);

		for (int i = 1;; i++) {
			User screenNameUser = userPersistence.fetchByC_SN(companyId, screenName);

			if (screenNameUser == null) {
				break;
			}

			screenName = screenName + i;
		}

		long facebookId = 0;
		String openId = StringPool.BLANK;
		long prefixId = 0;
		long suffixId = 0;
		boolean male = true;
		int birthdayMonth = Calendar.JANUARY;
		int birthdayDay = 1;
		int birthdayYear = 1970;
		String jobTitle = StringPool.BLANK;

		Group guestGroup = groupLocalService.getGroup(companyId, GroupConstants.GUEST);

		long[] groupIds = { guestGroup.getGroupId() };

		long[] organizationIds = null;

		Role adminRole = roleLocalService.getRole(companyId, RoleConstants.ADMINISTRATOR);

		Role powerUserRole = roleLocalService.getRole(companyId, RoleConstants.POWER_USER);

		long[] roleIds = { adminRole.getRoleId(), powerUserRole.getRoleId() };

		long[] userGroupIds = null;
		boolean sendEmail = false;
		ServiceContext serviceContext = new ServiceContext();

		Company company = companyLocalService.getCompany(companyId);

		serviceContext.setPathMain(PortalUtil.getPathMain());
		serviceContext.setPortalURL(company.getPortalURL(0));

		User defaultAdminUser = addUser(creatorUserId, companyId, autoPassword, password1, password2, autoScreenName,
				screenName, emailAddress, facebookId, openId, locale, firstName, middleName, lastName, prefixId,
				suffixId, male, birthdayMonth, birthdayDay, birthdayYear, jobTitle, groupIds, organizationIds, roleIds,
				userGroupIds, sendEmail, serviceContext);

		updateEmailAddressVerified(defaultAdminUser.getUserId(), true);

		updateLastLogin(defaultAdminUser.getUserId(), defaultAdminUser.getLoginIP());

		updatePasswordReset(defaultAdminUser.getUserId(), false);

		return defaultAdminUser;
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
	public User addUser(long creatorUserId, long companyId, boolean autoPassword, String password1, String password2,
			boolean autoScreenName, String screenName, String emailAddress, Locale locale, String firstName,
			String middleName, String lastName, long prefixId, long suffixId, boolean male, int birthdayMonth,
			int birthdayDay, int birthdayYear, String jobTitle, long[] groupIds, long[] organizationIds, long[] roleIds,
			long[] userGroupIds, boolean sendEmail, ServiceContext serviceContext) throws PortalException {

		
		boolean workflowEnabled = WorkflowThreadLocal.isEnabled();

		try {
			WorkflowThreadLocal.setEnabled(false);

			if (serviceContext == null) {
				serviceContext = new ServiceContext();
			}

			if (serviceContext.getWorkflowAction() != WorkflowConstants.ACTION_PUBLISH) {

				serviceContext.setWorkflowAction(WorkflowConstants.ACTION_PUBLISH);
			}

			return addUserWithWorkflow(creatorUserId, companyId, autoPassword, password1, password2, autoScreenName,
					screenName, emailAddress, locale, firstName, middleName, lastName, prefixId, suffixId, male,
					birthdayMonth, birthdayDay, birthdayYear, jobTitle, groupIds, organizationIds, roleIds,
					userGroupIds, sendEmail, serviceContext);
		} finally {
			WorkflowThreadLocal.setEnabled(workflowEnabled);
		}
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
	public User addUser(long creatorUserId, long companyId, boolean autoPassword, String password1, String password2,
			boolean autoScreenName, String screenName, String emailAddress, long facebookId, String openId,
			Locale locale, String firstName, String middleName, String lastName, long prefixId, long suffixId,
			boolean male, int birthdayMonth, int birthdayDay, int birthdayYear, String jobTitle, long[] groupIds,
			long[] organizationIds, long[] roleIds, long[] userGroupIds, boolean sendEmail,
			ServiceContext serviceContext) throws PortalException {

		boolean workflowEnabled = WorkflowThreadLocal.isEnabled();

		try {
			WorkflowThreadLocal.setEnabled(false);

			if (serviceContext == null) {
				serviceContext = new ServiceContext();
			}

			if (serviceContext.getWorkflowAction() != WorkflowConstants.ACTION_PUBLISH) {

				serviceContext.setWorkflowAction(WorkflowConstants.ACTION_PUBLISH);
			}

			return addUserWithWorkflow(creatorUserId, companyId, autoPassword, password1, password2, autoScreenName,
					screenName, emailAddress, facebookId, openId, locale, firstName, middleName, lastName, prefixId,
					suffixId, male, birthdayMonth, birthdayDay, birthdayYear, jobTitle, groupIds, organizationIds,
					roleIds, userGroupIds, sendEmail, serviceContext);
		} finally {
			WorkflowThreadLocal.setEnabled(workflowEnabled);
		}
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
	public User addUserWithWorkflow(long creatorUserId, long companyId, boolean autoPassword, String password1,
			String password2, boolean autoScreenName, String screenName, String emailAddress, Locale locale,
			String firstName, String middleName, String lastName, long prefixId, long suffixId, boolean male,
			int birthdayMonth, int birthdayDay, int birthdayYear, String jobTitle, long[] groupIds,
			long[] organizationIds, long[] roleIds, long[] userGroupIds, boolean sendEmail,
			ServiceContext serviceContext) throws PortalException {

		// User

		Company company = companyPersistence.findByPrimaryKey(companyId);
		screenName = getLogin(screenName);

		if (PrefsPropsUtil.getBoolean(companyId, PropsKeys.USERS_SCREEN_NAME_ALWAYS_AUTOGENERATE)) {

			autoScreenName = true;
		}

		// PLACEHOLDER 01

		long userId = counterLocalService.increment();

		if ((emailAddress == null) || this.emailAddressGenerator.isGenerated(emailAddress)) {

			emailAddress = StringPool.BLANK;
		} else {
			emailAddress = StringUtil.toLowerCase(emailAddress.trim());
		}

		if (!PrefsPropsUtil.getBoolean(companyId, PropsKeys.USERS_EMAIL_ADDRESS_REQUIRED)
				&& Validator.isNull(emailAddress)) {

			emailAddress = this.emailAddressGenerator.generate(companyId, userId);
		}

		validate(companyId, userId, autoPassword, password1, password2, autoScreenName, screenName, emailAddress, null,
				firstName, middleName, lastName, organizationIds, locale);

		if (!autoPassword && (Validator.isNull(password1) || Validator.isNull(password2))) {

			throw new UserPasswordException.MustNotBeNull(userId);
		}

		if (autoScreenName) {
			
			try {
				screenName = screenNameGenerator.generate(companyId, userId, emailAddress);
			} catch (Exception exception) {
				throw new SystemException(exception);
			}
		}

		User defaultUser = getDefaultUser(companyId);

		FullNameGenerator fullNameGenerator = FullNameGeneratorFactory.getInstance();

		String fullName = fullNameGenerator.getFullName(firstName, middleName, lastName);

		String greeting = LanguageUtil.format(locale, "welcome-x", fullName, false);

		User user = userPersistence.create(userId);

		if (serviceContext != null) {
			String uuid = serviceContext.getUuid();

			if (Validator.isNotNull(uuid)) {
				user.setUuid(uuid);
			}
		}

		user.setCompanyId(companyId);
		user.setDefaultUser(false);
		user.setContactId(counterLocalService.increment());

		if (Validator.isNotNull(password1)) {
			PasswordModificationThreadLocal.setPasswordModified(true);
			PasswordModificationThreadLocal.setPasswordUnencrypted(password1);

			user.setPassword(PasswordEncryptorUtil.encrypt(password1));
			user.setPasswordUnencrypted(password1);
		}

		user.setPasswordEncrypted(true);

		PasswordPolicy passwordPolicy = defaultUser.getPasswordPolicy();

		if ((passwordPolicy != null) && passwordPolicy.isChangeable() && passwordPolicy.isChangeRequired()) {

			user.setPasswordReset(true);
		} else {
			user.setPasswordReset(false);
		}

		user.setScreenName(screenName);
		user.setEmailAddress(emailAddress);

		user.setDigest(user.getDigest(password1));

		Long ldapServerId = null;

		if (serviceContext != null) {
			ldapServerId = (Long) serviceContext.getAttribute("ldapServerId");
		}

		if (ldapServerId != null) {
			user.setLdapServerId(ldapServerId);
		} else {
			user.setLdapServerId(-1);
		}

		user.setLanguageId(LocaleUtil.toLanguageId(locale));
		user.setTimeZoneId(defaultUser.getTimeZoneId());
		user.setGreeting(greeting);
		user.setFirstName(firstName);
		user.setMiddleName(middleName);
		user.setLastName(lastName);
		user.setJobTitle(jobTitle);
		user.setStatus(WorkflowConstants.STATUS_DRAFT);
		user.setExpandoBridgeAttributes(serviceContext);

		user = userPersistence.update(user, serviceContext);

		// Contact

		String creatorUserName = StringPool.BLANK;

		if (creatorUserId <= 0) {
			creatorUserId = user.getUserId();

			// Don't grab the full name from the User object because it doesn't
			// have a corresponding Contact object yet

			// creatorUserName = user.getFullName();
		} else {
			User creatorUser = userPersistence.findByPrimaryKey(creatorUserId);

			creatorUserName = creatorUser.getFullName();
		}

		Date birthday = getBirthday(birthdayMonth, birthdayDay, birthdayYear);

		Contact contact = contactPersistence.create(user.getContactId());

		contact.setCompanyId(user.getCompanyId());
		contact.setUserId(creatorUserId);
		contact.setUserName(creatorUserName);
		contact.setClassName(User.class.getName());
		contact.setClassPK(user.getUserId());
		contact.setAccountId(company.getAccountId());
		contact.setParentContactId(ContactConstants.DEFAULT_PARENT_CONTACT_ID);
		contact.setEmailAddress(user.getEmailAddress());
		contact.setFirstName(firstName);
		contact.setMiddleName(middleName);
		contact.setLastName(lastName);
		contact.setPrefixId(prefixId);
		contact.setSuffixId(suffixId);
		contact.setMale(male);
		contact.setBirthday(birthday);
		contact.setJobTitle(jobTitle);

		contactPersistence.update(contact, serviceContext);

		// Group

		groupLocalService.addGroup(user.getUserId(), GroupConstants.DEFAULT_PARENT_GROUP_ID, User.class.getName(),
				user.getUserId(), GroupConstants.DEFAULT_LIVE_GROUP_ID, (Map<Locale, String>) null, null, 0, true,
				GroupConstants.DEFAULT_MEMBERSHIP_RESTRICTION, StringPool.SLASH + screenName, false, true, null);

		// Groups

		if (!ArrayUtil.isEmpty(groupIds)) {
			List<Group> groups = new ArrayList<>();

			for (long groupId : groupIds) {
				Group group = groupLocalService.fetchGroup(groupId);

				if (group != null) {
					groups.add(group);
				} else {
					if (_log.isWarnEnabled()) {
						_log.warn("Group " + groupId + " does not exist");
					}
				}
			}

			groupLocalService.addUserGroups(userId, groups);
		}

		addDefaultGroups(userId);

		// Organizations

		updateOrganizations(userId, organizationIds, false);

		// Roles

		if (roleIds != null) {
			roleIds = UsersAdminUtil.addRequiredRoles(user, roleIds);

			userPersistence.setRoles(userId, roleIds);
		}

		addDefaultRoles(userId);

		// User groups

		if (userGroupIds != null) {
			userPersistence.setUserGroups(userId, userGroupIds);
		}

		addDefaultUserGroups(userId);

		// Resources

		resourceLocalService.addResources(companyId, 0, creatorUserId, User.class.getName(), user.getUserId(), false,
				false, false);

		// Asset

		if (serviceContext != null) {
			updateAsset(creatorUserId, user, serviceContext.getAssetCategoryIds(), serviceContext.getAssetTagNames());
		}

		// Indexer

		if ((serviceContext == null) || serviceContext.isIndexingEnabled()) {
			reindex(user);
		}

		// Workflow

		long workflowUserId = creatorUserId;

		if (workflowUserId == userId) {
			workflowUserId = defaultUser.getUserId();
		}

		ServiceContext workflowServiceContext = new ServiceContext();

		if (serviceContext != null) {
			workflowServiceContext = (ServiceContext) serviceContext.clone();
		}

		Map<String, Serializable> workflowContext = (Map<String, Serializable>) workflowServiceContext
				.removeAttribute("workflowContext");

		if (workflowContext == null) {
			workflowContext = Collections.emptyMap();
		}

		workflowServiceContext.setAttributes(new HashMap<String, Serializable>());

		workflowServiceContext.setAttribute("autoPassword", autoPassword);
		workflowServiceContext.setAttribute("sendEmail", sendEmail);

		user = WorkflowHandlerRegistryUtil.startWorkflowInstance(companyId, WorkflowConstants.DEFAULT_GROUP_ID,
				workflowUserId, User.class.getName(), userId, user, workflowServiceContext, workflowContext);

		if (serviceContext != null) {
			String passwordUnencrypted = (String) serviceContext.getAttribute("passwordUnencrypted");

			if (Validator.isNotNull(passwordUnencrypted)) {
				user.setPasswordUnencrypted(passwordUnencrypted);
			}
		}

		return user;
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
	public User addUserWithWorkflow(long creatorUserId, long companyId, boolean autoPassword, String password1,
			String password2, boolean autoScreenName, String screenName, String emailAddress, long facebookId,
			String openId, Locale locale, String firstName, String middleName, String lastName, long prefixId,
			long suffixId, boolean male, int birthdayMonth, int birthdayDay, int birthdayYear, String jobTitle,
			long[] groupIds, long[] organizationIds, long[] roleIds, long[] userGroupIds, boolean sendEmail,
			ServiceContext serviceContext) throws PortalException {

		User user = getWrappedService().addUserWithWorkflow(creatorUserId, companyId, autoPassword, password1,
				password2, autoScreenName, screenName, emailAddress, locale, firstName, middleName, lastName, prefixId,
				suffixId, male, birthdayMonth, birthdayDay, birthdayYear, jobTitle, groupIds, organizationIds, roleIds,
				userGroupIds, sendEmail, serviceContext);

		openId = StringUtil.trim(openId);

		validateOpenId(companyId, user.getUserId(), openId);

		user.setFacebookId(facebookId);
		user.setOpenId(openId);

		return getWrappedService().updateUser(user);
	}

	/**
	 * Updates a user account that was automatically created when a guest user
	 * participated in an action (e.g. posting a comment) and only provided his name
	 * and email address.
	 *
	 * @param creatorUserId         the primary key of the creator
	 * @param companyId             the primary key of the user's company
	 * @param autoPassword          whether a password should be automatically
	 *                              generated for the user
	 * @param password1             the user's password
	 * @param password2             the user's password confirmation
	 * @param autoScreenName        whether a screen name should be automatically
	 *                              generated for the user
	 * @param screenName            the user's screen name
	 * @param emailAddress          the user's email address
	 * @param locale                the user's locale
	 * @param firstName             the user's first name
	 * @param middleName            the user's middle name
	 * @param lastName              the user's last name
	 * @param prefixId              the user's name prefix ID
	 * @param suffixId              the user's name suffix ID
	 * @param male                  whether the user is male
	 * @param birthdayMonth         the user's birthday month (0-based, meaning 0
	 *                              for January)
	 * @param birthdayDay           the user's birthday day
	 * @param birthdayYear          the user's birthday year
	 * @param jobTitle              the user's job title
	 * @param updateUserInformation whether to update the user's information
	 * @param sendEmail             whether to send the user an email notification
	 *                              about their new account
	 * @param serviceContext        the service context to be applied (optionally
	 *                              <code>null</code>). Can set expando bridge
	 *                              attributes for the user.
	 * @return the user
	 */
	@Override
	public User updateIncompleteUser(long creatorUserId, long companyId, boolean autoPassword, String password1,
			String password2, boolean autoScreenName, String screenName, String emailAddress, Locale locale,
			String firstName, String middleName, String lastName, long prefixId, long suffixId, boolean male,
			int birthdayMonth, int birthdayDay, int birthdayYear, String jobTitle, boolean updateUserInformation,
			boolean sendEmail, ServiceContext serviceContext) throws PortalException {

		User user = getUserByEmailAddress(companyId, emailAddress);

		if (user.getStatus() != WorkflowConstants.STATUS_INCOMPLETE) {
			throw new PortalException("Invalid user status");
		}

		User defaultUser = getDefaultUser(companyId);

		if (updateUserInformation) {
			autoScreenName = false;

			if (PrefsPropsUtil.getBoolean(companyId, PropsKeys.USERS_SCREEN_NAME_ALWAYS_AUTOGENERATE)) {

				autoScreenName = true;
			}

			validate(companyId, user.getUserId(), autoPassword, password1, password2, autoScreenName, screenName,
					emailAddress, null, firstName, middleName, lastName, null, locale);

			if (!autoPassword && (Validator.isNull(password1) || Validator.isNull(password2))) {

				throw new UserPasswordException.MustNotBeNull(user.getUserId());
			}

			if (autoScreenName) {

				try {
					screenName = screenNameGenerator.generate(companyId, user.getUserId(), emailAddress);
				} catch (Exception exception) {
					throw new SystemException(exception);
				}
			}

			FullNameGenerator fullNameGenerator = FullNameGeneratorFactory.getInstance();

			String fullName = fullNameGenerator.getFullName(firstName, middleName, lastName);

			String greeting = LanguageUtil.format(locale, "welcome-x", fullName, false);

			if (Validator.isNotNull(password1)) {
				user.setPassword(PasswordEncryptorUtil.encrypt(password1));
				user.setPasswordUnencrypted(password1);
			}

			user.setPasswordEncrypted(true);

			PasswordPolicy passwordPolicy = defaultUser.getPasswordPolicy();

			if ((passwordPolicy != null) && passwordPolicy.isChangeable() && passwordPolicy.isChangeRequired()) {

				user.setPasswordReset(true);
			} else {
				user.setPasswordReset(false);
			}

			user.setScreenName(screenName);
			user.setLanguageId(locale.toString());
			user.setTimeZoneId(defaultUser.getTimeZoneId());
			user.setGreeting(greeting);
			user.setFirstName(firstName);
			user.setMiddleName(middleName);
			user.setLastName(lastName);
			user.setJobTitle(jobTitle);
			user.setExpandoBridgeAttributes(serviceContext);

			Date birthday = getBirthday(birthdayMonth, birthdayDay, birthdayYear);

			Contact contact = user.getContact();

			contact.setFirstName(firstName);
			contact.setMiddleName(middleName);
			contact.setLastName(lastName);
			contact.setPrefixId(prefixId);
			contact.setSuffixId(suffixId);
			contact.setMale(male);
			contact.setBirthday(birthday);
			contact.setJobTitle(jobTitle);

			contactPersistence.update(contact, serviceContext);

			// Indexer

			Indexer<User> indexer = IndexerRegistryUtil.nullSafeGetIndexer(User.class);

			indexer.reindex(user);
		}

		user.setStatus(WorkflowConstants.STATUS_DRAFT);

		user = userPersistence.update(user, serviceContext);

		// Workflow

		long workflowUserId = creatorUserId;

		if (workflowUserId == user.getUserId()) {
			workflowUserId = defaultUser.getUserId();
		}

		ServiceContext workflowServiceContext = serviceContext;

		if (workflowServiceContext == null) {
			workflowServiceContext = new ServiceContext();
		}

		workflowServiceContext.setAttribute("autoPassword", autoPassword);
		workflowServiceContext.setAttribute("passwordUnencrypted", password1);
		workflowServiceContext.setAttribute("sendEmail", sendEmail);

		WorkflowHandlerRegistryUtil.startWorkflowInstance(companyId, workflowUserId, User.class.getName(),
				user.getUserId(), user, workflowServiceContext);

		return getUserByEmailAddress(companyId, emailAddress);
	}

	/**
	 * Updates a user account that was automatically created when a guest user
	 * participated in an action (e.g. posting a comment) and only provided his name
	 * and email address.
	 *
	 * @param creatorUserId         the primary key of the creator
	 * @param companyId             the primary key of the user's company
	 * @param autoPassword          whether a password should be automatically
	 *                              generated for the user
	 * @param password1             the user's password
	 * @param password2             the user's password confirmation
	 * @param autoScreenName        whether a screen name should be automatically
	 *                              generated for the user
	 * @param screenName            the user's screen name
	 * @param emailAddress          the user's email address
	 * @param facebookId            the user's facebook ID
	 * @param openId                the user's OpenID
	 * @param locale                the user's locale
	 * @param firstName             the user's first name
	 * @param middleName            the user's middle name
	 * @param lastName              the user's last name
	 * @param prefixId              the user's name prefix ID
	 * @param suffixId              the user's name suffix ID
	 * @param male                  whether the user is male
	 * @param birthdayMonth         the user's birthday month (0-based, meaning 0
	 *                              for January)
	 * @param birthdayDay           the user's birthday day
	 * @param birthdayYear          the user's birthday year
	 * @param jobTitle              the user's job title
	 * @param updateUserInformation whether to update the user's information
	 * @param sendEmail             whether to send the user an email notification
	 *                              about their new account
	 * @param serviceContext        the service context to be applied (optionally
	 *                              <code>null</code>). Can set expando bridge
	 *                              attributes for the user.
	 * @return the user
	 * @deprecated As of Athanasius (7.3.x), replaced by
	 *             {@link #updateIncompleteUser(long, long, boolean, String, String, boolean, String, String, Locale, String, String, String, long, long, boolean, int, int, int, String, boolean, boolean, ServiceContext)}
	 */
	@Deprecated
	@Override
	public User updateIncompleteUser(long creatorUserId, long companyId, boolean autoPassword, String password1,
			String password2, boolean autoScreenName, String screenName, String emailAddress, long facebookId,
			String openId, Locale locale, String firstName, String middleName, String lastName, long prefixId,
			long suffixId, boolean male, int birthdayMonth, int birthdayDay, int birthdayYear, String jobTitle,
			boolean updateUserInformation, boolean sendEmail, ServiceContext serviceContext) throws PortalException {

		User user = getUserByEmailAddress(companyId, emailAddress);

		if (facebookId > 0) {
			autoPassword = false;

			if ((password1 == null) || (password2 == null)) {
				password1 = PwdGenerator.getPassword();

				password2 = password1;
			}

			sendEmail = false;
		}

		if (updateUserInformation) {
			validateOpenId(companyId, user.getUserId(), openId);
		}

		user = getWrappedService().updateIncompleteUser(creatorUserId, companyId, autoPassword, password1, password2,
				autoScreenName, screenName, emailAddress, locale, firstName, middleName, lastName, prefixId, suffixId,
				male, birthdayMonth, birthdayDay, birthdayYear, jobTitle, updateUserInformation, sendEmail,
				serviceContext);

		user.setFacebookId(facebookId);
		user.setOpenId(openId);

		return getWrappedService().updateUser(user);
	}

	/**
	 * Updates the user.
	 *
	 * @param userId                the primary key of the user
	 * @param oldPassword           the user's old password
	 * @param newPassword1          the user's new password (optionally
	 *                              <code>null</code>)
	 * @param newPassword2          the user's new password confirmation (optionally
	 *                              <code>null</code>)
	 * @param passwordReset         whether the user should be asked to reset their
	 *                              password the next time they login
	 * @param reminderQueryQuestion the user's new password reset question
	 * @param reminderQueryAnswer   the user's new password reset answer
	 * @param screenName            the user's new screen name
	 * @param emailAddress          the user's new email address
	 * @param hasPortrait           if the user has a custom portrait image
	 * @param portraitBytes         the new portrait image data
	 * @param languageId            the user's new language ID
	 * @param timeZoneId            the user's new time zone ID
	 * @param greeting              the user's new greeting
	 * @param comments              the user's new comments
	 * @param firstName             the user's new first name
	 * @param middleName            the user's new middle name
	 * @param lastName              the user's new last name
	 * @param prefixId              the user's new name prefix ID
	 * @param suffixId              the user's new name suffix ID
	 * @param male                  whether user is male
	 * @param birthdayMonth         the user's new birthday month (0-based, meaning
	 *                              0 for January)
	 * @param birthdayDay           the user's new birthday day
	 * @param birthdayYear          the user's birthday year
	 * @param smsSn                 the user's new SMS screen name
	 * @param facebookSn            the user's new Facebook screen name
	 * @param jabberSn              the user's new Jabber screen name
	 * @param skypeSn               the user's new Skype screen name
	 * @param twitterSn             the user's new Twitter screen name
	 * @param jobTitle              the user's new job title
	 * @param groupIds              the primary keys of the user's groups
	 * @param organizationIds       the primary keys of the user's organizations
	 * @param roleIds               the primary keys of the user's roles
	 * @param userGroupRoles        the user user's group roles
	 * @param userGroupIds          the primary keys of the user's user groups
	 * @param serviceContext        the service context to be applied (optionally
	 *                              <code>null</code>). Can set the UUID (with the
	 *                              <code>uuid</code> attribute), asset category
	 *                              IDs, asset tag names, and expando bridge
	 *                              attributes for the user.
	 * @return the user
	 */
	@Override
	public User updateUser(long userId, String oldPassword, String newPassword1, String newPassword2,
			boolean passwordReset, String reminderQueryQuestion, String reminderQueryAnswer, String screenName,
			String emailAddress, boolean hasPortrait, byte[] portraitBytes, String languageId, String timeZoneId,
			String greeting, String comments, String firstName, String middleName, String lastName, long prefixId,
			long suffixId, boolean male, int birthdayMonth, int birthdayDay, int birthdayYear, String smsSn,
			String facebookSn, String jabberSn, String skypeSn, String twitterSn, String jobTitle, long[] groupIds,
			long[] organizationIds, long[] roleIds, List<UserGroupRole> userGroupRoles, long[] userGroupIds,
			ServiceContext serviceContext) throws PortalException {

		// User

		String password = oldPassword;
		screenName = getLogin(screenName);
		emailAddress = StringUtil.toLowerCase(StringUtil.trim(emailAddress));
		facebookSn = StringUtil.toLowerCase(StringUtil.trim(facebookSn));
		jabberSn = StringUtil.toLowerCase(StringUtil.trim(jabberSn));
		skypeSn = StringUtil.toLowerCase(StringUtil.trim(skypeSn));
		twitterSn = StringUtil.toLowerCase(StringUtil.trim(twitterSn));

		if (this.emailAddressGenerator.isGenerated(emailAddress)) {
			emailAddress = StringPool.BLANK;
		}

		Locale locale = LocaleUtil.fromLanguageId(languageId);

		validate(userId, screenName, emailAddress, null, firstName, middleName, lastName, smsSn, locale);

		User user = userPersistence.findByPrimaryKey(userId);

		Company company = companyPersistence.findByPrimaryKey(user.getCompanyId());

		if (!PropsValues.USERS_EMAIL_ADDRESS_REQUIRED && Validator.isNull(emailAddress)) {

			emailAddress = this.emailAddressGenerator.generate(user.getCompanyId(), userId);
		}

		if (Validator.isNotNull(newPassword1) || Validator.isNotNull(newPassword2)) {

			user = updatePassword(userId, newPassword1, newPassword2, passwordReset);

			password = newPassword1;

			user.setDigest(user.getDigest(password));
		}

		if (user.getContactId() <= 0) {
			user.setContactId(counterLocalService.increment());
		}

		user.setPasswordReset(passwordReset);

		if (Validator.isNotNull(reminderQueryQuestion) && Validator.isNotNull(reminderQueryAnswer)) {

			user.setReminderQueryQuestion(reminderQueryQuestion);
			user.setReminderQueryAnswer(reminderQueryAnswer);
		}

		boolean screenNameModified = !StringUtil.equalsIgnoreCase(user.getScreenName(), screenName);

		if (screenNameModified) {
			user.setScreenName(screenName);

			user.setDigest(StringPool.BLANK);
		}

		boolean sendEmailAddressVerification = false;

		if (company.isStrangersVerify() && !StringUtil.equalsIgnoreCase(emailAddress, user.getEmailAddress())) {

			sendEmailAddressVerification = true;
		} else {
			setEmailAddress(user, password, firstName, middleName, lastName, emailAddress);
		}

		if (serviceContext != null) {
			String uuid = serviceContext.getUuid();

			if (Validator.isNotNull(uuid)) {
				user.setUuid(uuid);
			}
		}

		Long ldapServerId = null;

		if (serviceContext != null) {
			ldapServerId = (Long) serviceContext.getAttribute("ldapServerId");
		}

		if (ldapServerId != null) {
			user.setLdapServerId(ldapServerId);
		}

		PortalUtil.updateImageId(user, hasPortrait, portraitBytes, "portraitId",
				_userFileUploadsSettings.getImageMaxSize(), _userFileUploadsSettings.getImageMaxHeight(),
				_userFileUploadsSettings.getImageMaxWidth());

		user.setLanguageId(languageId);
		user.setTimeZoneId(timeZoneId);
		user.setGreeting(greeting);
		user.setComments(comments);
		user.setFirstName(firstName);
		user.setMiddleName(middleName);
		user.setLastName(lastName);
		user.setJobTitle(jobTitle);
		user.setExpandoBridgeAttributes(serviceContext);

		user = userPersistence.update(user, serviceContext);

		// Contact

		Date birthday = getBirthday(birthdayMonth, birthdayDay, birthdayYear);

		long contactId = user.getContactId();

		Contact contact = contactPersistence.fetchByPrimaryKey(contactId);

		if (contact == null) {
			contact = contactPersistence.create(contactId);

			contact.setCompanyId(user.getCompanyId());
			contact.setUserName(StringPool.BLANK);
			contact.setClassName(User.class.getName());
			contact.setClassPK(user.getUserId());
			contact.setAccountId(company.getAccountId());
			contact.setParentContactId(ContactConstants.DEFAULT_PARENT_CONTACT_ID);
		}

		contact.setEmailAddress(user.getEmailAddress());
		contact.setFirstName(firstName);
		contact.setMiddleName(middleName);
		contact.setLastName(lastName);
		contact.setPrefixId(prefixId);
		contact.setSuffixId(suffixId);
		contact.setMale(male);
		contact.setBirthday(birthday);
		contact.setSmsSn(smsSn);
		contact.setFacebookSn(facebookSn);
		contact.setJabberSn(jabberSn);
		contact.setSkypeSn(skypeSn);
		contact.setTwitterSn(twitterSn);
		contact.setJobTitle(jobTitle);

		contactPersistence.update(contact, serviceContext);

		// Group

		if (screenNameModified) {
			Group group = groupLocalService.getUserGroup(user.getCompanyId(), userId);

			groupLocalService.updateFriendlyURL(group.getGroupId(), StringPool.SLASH + screenName);
		}

		// Groups and organizations

		// See LPS-33205. Cache the user's list of user group roles because
		// adding or removing groups may add or remove user group roles
		// depending on the site default user associations.

		List<UserGroupRole> previousUserGroupRoles = userGroupRolePersistence.findByUserId(userId);

		updateGroups(userId, groupIds, false);
		updateOrganizations(userId, organizationIds, false);

		// Roles

		if (roleIds != null) {
			roleIds = UsersAdminUtil.addRequiredRoles(user, roleIds);

			userPersistence.setRoles(userId, roleIds);
		}

		// User group roles

		updateUserGroupRoles(user, groupIds, organizationIds, userGroupRoles, previousUserGroupRoles);

		// User groups

		if (userGroupIds != null) {
			userPersistence.setUserGroups(userId, userGroupIds);
		}

		// Announcements

		announcementsDeliveryLocalService.getUserDeliveries(user.getUserId());

		// Asset

		if (serviceContext != null) {
			updateAsset(userId, user, serviceContext.getAssetCategoryIds(), serviceContext.getAssetTagNames());
		}

		// Indexer

		if ((serviceContext == null) || serviceContext.isIndexingEnabled()) {
			Indexer<User> indexer = IndexerRegistryUtil.nullSafeGetIndexer(User.class);

			indexer.reindex(user);
		}

		// Email address verification

		if ((serviceContext != null) && sendEmailAddressVerification) {
			sendEmailAddressVerification(user, emailAddress, serviceContext);
		}

		return user;
	}

	/**
	 * Updates the user.
	 *
	 * @param userId                the primary key of the user
	 * @param oldPassword           the user's old password
	 * @param newPassword1          the user's new password (optionally
	 *                              <code>null</code>)
	 * @param newPassword2          the user's new password confirmation (optionally
	 *                              <code>null</code>)
	 * @param passwordReset         whether the user should be asked to reset their
	 *                              password the next time they login
	 * @param reminderQueryQuestion the user's new password reset question
	 * @param reminderQueryAnswer   the user's new password reset answer
	 * @param screenName            the user's new screen name
	 * @param emailAddress          the user's new email address
	 * @param facebookId            the user's new Facebook ID
	 * @param openId                the user's new OpenID
	 * @param hasPortrait           if the user has a custom portrait image
	 * @param portraitBytes         the new portrait image data
	 * @param languageId            the user's new language ID
	 * @param timeZoneId            the user's new time zone ID
	 * @param greeting              the user's new greeting
	 * @param comments              the user's new comments
	 * @param firstName             the user's new first name
	 * @param middleName            the user's new middle name
	 * @param lastName              the user's new last name
	 * @param prefixId              the user's new name prefix ID
	 * @param suffixId              the user's new name suffix ID
	 * @param male                  whether user is male
	 * @param birthdayMonth         the user's new birthday month (0-based, meaning
	 *                              0 for January)
	 * @param birthdayDay           the user's new birthday day
	 * @param birthdayYear          the user's birthday year
	 * @param smsSn                 the user's new SMS screen name
	 * @param facebookSn            the user's new Facebook screen name
	 * @param jabberSn              the user's new Jabber screen name
	 * @param skypeSn               the user's new Skype screen name
	 * @param twitterSn             the user's new Twitter screen name
	 * @param jobTitle              the user's new job title
	 * @param groupIds              the primary keys of the user's groups
	 * @param organizationIds       the primary keys of the user's organizations
	 * @param roleIds               the primary keys of the user's roles
	 * @param userGroupRoles        the user user's group roles
	 * @param userGroupIds          the primary keys of the user's user groups
	 * @param serviceContext        the service context to be applied (optionally
	 *                              <code>null</code>). Can set the UUID (with the
	 *                              <code>uuid</code> attribute), asset category
	 *                              IDs, asset tag names, and expando bridge
	 *                              attributes for the user.
	 * @return the user
	 * @deprecated As of Athanasius (7.3.x), replaced by
	 *             {@link #updateUser(long, String, String, String, boolean, String, String, String, String, boolean, byte[], String, String, String, String, String, String, String, long, long, boolean, int, int, int, String, String, String, String, String, String, long[], long[], long[], List, long[], ServiceContext) }
	 */
	@Deprecated
	@Override
	public User updateUser(long userId, String oldPassword, String newPassword1, String newPassword2,
			boolean passwordReset, String reminderQueryQuestion, String reminderQueryAnswer, String screenName,
			String emailAddress, long facebookId, String openId, boolean hasPortrait, byte[] portraitBytes,
			String languageId, String timeZoneId, String greeting, String comments, String firstName, String middleName,
			String lastName, long prefixId, long suffixId, boolean male, int birthdayMonth, int birthdayDay,
			int birthdayYear, String smsSn, String facebookSn, String jabberSn, String skypeSn, String twitterSn,
			String jobTitle, long[] groupIds, long[] organizationIds, long[] roleIds,
			List<UserGroupRole> userGroupRoles, long[] userGroupIds, ServiceContext serviceContext)
			throws PortalException {

		User user = getWrappedService().updateUser(userId, oldPassword, newPassword1, newPassword2, passwordReset,
				reminderQueryQuestion, reminderQueryAnswer, screenName, emailAddress, hasPortrait, portraitBytes,
				languageId, timeZoneId, greeting, comments, firstName, middleName, lastName, prefixId, suffixId, male,
				birthdayMonth, birthdayDay, birthdayYear, smsSn, facebookSn, jabberSn, skypeSn, twitterSn, jobTitle,
				groupIds, organizationIds, roleIds, userGroupRoles, userGroupIds, serviceContext);

		openId = StringUtil.trim(openId);

		validateOpenId(user.getCompanyId(), userId, openId);

		user.setFacebookId(facebookId);
		user.setOpenId(openId);

		return getWrappedService().updateUser(user);
	}

//	
//	
//	
//	
//	
//	Useful methods:
//	
//	
//	
//	
//	
	
	protected String getLogin(String login) {
		return StringUtil.lowerCase(StringUtil.trim(login));
	}

	protected Date getBirthday(int birthdayMonth, int birthdayDay, int birthdayYear) throws PortalException {

		Date birthday = PortalUtil.getDate(birthdayMonth, birthdayDay, birthdayYear, ContactBirthdayException.class);

		if (birthday.after(new Date())) {
			throw new ContactBirthdayException();
		}

		return birthday;
	}

	protected void setEmailAddress(User user, String password, String firstName, String middleName, String lastName,
			String emailAddress) throws PortalException {

		if (StringUtil.equalsIgnoreCase(emailAddress, user.getEmailAddress())) {
			return;
		}

		long userId = user.getUserId();

		if (!user.hasCompanyMx() && user.hasCompanyMx(emailAddress) && Validator.isNotNull(password)) {

			// test@test.com -> test@liferay.com

			mailService.addUser(user.getCompanyId(), userId, password, firstName, middleName, lastName, emailAddress);
		} else if (user.hasCompanyMx() && user.hasCompanyMx(emailAddress)) {

			// test@liferay.com -> bob@liferay.com

			mailService.updateEmailAddress(user.getCompanyId(), userId, emailAddress);
		} else if (user.hasCompanyMx() && !user.hasCompanyMx(emailAddress)) {

			// test@liferay.com -> test@test.com

			mailService.deleteEmailAddress(user.getCompanyId(), userId);
		}

		user.setDigest(StringPool.BLANK);
		user.setEmailAddress(emailAddress);
	}

//	
//	
//	
//	
//	
//	Secundary methods
//	
//	
//	
//	
//	
	
	protected void updateOrganizations(long userId, long[] newOrganizationIds, boolean indexingEnabled)
			throws PortalException {

		if (newOrganizationIds == null) {
			return;
		}

		List<Long> oldOrganizationIds = ListUtil.fromArray(getOrganizationPrimaryKeys(userId));

		for (long newOrganizationId : newOrganizationIds) {
			oldOrganizationIds.remove(newOrganizationId);
		}

		if (!oldOrganizationIds.isEmpty()) {
			unsetUserOrganizations(userId, ArrayUtil.toLongArray(oldOrganizationIds));
		}

		userPersistence.setOrganizations(userId, newOrganizationIds);

		if (indexingEnabled) {
			reindex(userId);
		}
	}

	protected void updateUserGroupRoles(User user, long[] groupIds, long[] organizationIds,
			List<UserGroupRole> userGroupRoles, List<UserGroupRole> previousUserGroupRoles) throws PortalException {

		if (userGroupRoles == null) {
			return;
		}

		userGroupRoles = new ArrayList<>(userGroupRoles);

		for (UserGroupRole userGroupRole : previousUserGroupRoles) {
			if (userGroupRoles.contains(userGroupRole)) {
				userGroupRoles.remove(userGroupRole);
			} else {
				userGroupRoleLocalService.deleteUserGroupRole(userGroupRole);
			}
		}

		if (ListUtil.isEmpty(userGroupRoles)) {
			return;
		}

		long[] validGroupIds = null;

		if (groupIds != null) {
			validGroupIds = ArrayUtil.clone(groupIds);
		} else {
			List<Group> userGroups = groupLocalService.getUserGroups(user.getUserId(), true);

			int size = userGroups.size();

			validGroupIds = new long[size];

			for (int i = 0; i < size; i++) {
				Group userGroup = userGroups.get(i);

				validGroupIds[i] = userGroup.getGroupId();
			}
		}

		if (organizationIds == null) {
			organizationIds = user.getOrganizationIds();
		}

		for (long organizationId : organizationIds) {
			Organization organization = organizationPersistence.findByPrimaryKey(organizationId);

			if (!ArrayUtil.contains(validGroupIds, organization.getGroupId())) {
				validGroupIds = ArrayUtil.append(validGroupIds, organization.getGroupId());
			}
		}

		Arrays.sort(validGroupIds);

		for (UserGroupRole userGroupRole : userGroupRoles) {
			int count = Arrays.binarySearch(validGroupIds, userGroupRole.getGroupId());

			if (count >= 0) {
				userGroupRoleLocalService.addUserGroupRole(userGroupRole);
			}
		}
	}
	
	protected void updateGroups(
			long userId, long[] newGroupIds, boolean indexingEnabled)
		throws PortalException {

		if (newGroupIds == null) {
			return;
		}

		List<Long> oldGroupIds = ListUtil.fromArray(
			getGroupPrimaryKeys(userId));

		for (long newGroupId : newGroupIds) {
			oldGroupIds.remove(newGroupId);
		}

		if (!oldGroupIds.isEmpty()) {
			unsetUserGroups(userId, ArrayUtil.toLongArray(oldGroupIds));
		}

		userPersistence.setGroups(userId, newGroupIds);

		for (long newGroupId : newGroupIds) {
			addDefaultRolesAndTeams(newGroupId, new long[] {userId});
		}

		if (indexingEnabled) {
			reindex(userId);
		}
	}
	
	protected void addDefaultRolesAndTeams(long groupId, long[] userIds)
			throws PortalException {

			List<Role> defaultSiteRoles = new ArrayList<>();

			Group group = groupLocalService.getGroup(groupId);

			UnicodeProperties typeSettingsUnicodeProperties =
				group.getTypeSettingsProperties();

			long[] defaultSiteRoleIds = StringUtil.split(
				typeSettingsUnicodeProperties.getProperty("defaultSiteRoleIds"),
				0L);

			for (long defaultSiteRoleId : defaultSiteRoleIds) {
				Role defaultSiteRole = rolePersistence.fetchByPrimaryKey(
					defaultSiteRoleId);

				if (defaultSiteRole == null) {
					if (_log.isWarnEnabled()) {
						_log.warn("Unable to find role " + defaultSiteRoleId);
					}

					continue;
				}

				defaultSiteRoles.add(defaultSiteRole);
			}

			List<Team> defaultTeams = new ArrayList<>();

			long[] defaultTeamIds = StringUtil.split(
				typeSettingsUnicodeProperties.getProperty("defaultTeamIds"), 0L);

			for (long defaultTeamId : defaultTeamIds) {
				Team defaultTeam = teamPersistence.findByPrimaryKey(defaultTeamId);

				if (defaultTeam == null) {
					if (_log.isWarnEnabled()) {
						_log.warn("Unable to find team " + defaultTeamId);
					}

					continue;
				}

				defaultTeams.add(defaultTeam);
			}

			for (long userId : userIds) {
				Set<Long> userRoleIdsSet = new HashSet<>();

				for (Role role : defaultSiteRoles) {
					userRoleIdsSet.add(role.getRoleId());
				}

				long[] userRoleIds = ArrayUtil.toArray(
					userRoleIdsSet.toArray(new Long[0]));

				userGroupRoleLocalService.addUserGroupRoles(
					userId, groupId, userRoleIds);

				Set<Long> userTeamIdsSet = new HashSet<>();

				for (Team team : defaultTeams) {
					userTeamIdsSet.add(team.getTeamId());
				}

				long[] userTeamIds = ArrayUtil.toArray(
					userTeamIdsSet.toArray(new Long[0]));

				userPersistence.addTeams(userId, userTeamIds);
			}
		}
	

//	
//	
//	
//	
//	
//	Unset User methods
//	
//	
//	
//	
//	

	protected void unsetUserOrganizations(long userId, long[] organizationIds) throws PortalException {

		long[] groupIds = new long[organizationIds.length];

		for (int i = 0; i < organizationIds.length; i++) {
			Organization organization = organizationPersistence.findByPrimaryKey(organizationIds[i]);

			groupIds[i] = organization.getGroupId();
		}

		userGroupRoleLocalService.deleteUserGroupRoles(userId, groupIds);

		organizationLocalService.deleteUserOrganizations(userId, organizationIds);

		reindex(userId);

		TransactionCommitCallbackUtil.registerCallback(() -> {
			Message message = new Message();

			message.put("groupIds", groupIds);
			message.put("userId", userId);

			MessageBusUtil.sendMessage(DestinationNames.SUBSCRIPTION_CLEAN_UP, message);

			return null;
		});
	}
	
	protected void unsetUserGroups(long userId, long[] groupIds)
			throws PortalException {

			List<UserGroupRole> userGroupRoles =
				userGroupRolePersistence.findByUserId(userId);

			for (UserGroupRole userGroupRole : userGroupRoles) {
				if (ArrayUtil.contains(groupIds, userGroupRole.getGroupId())) {
					Role role = rolePersistence.findByPrimaryKey(
						userGroupRole.getRoleId());

					if ((role.getType() == RoleConstants.TYPE_DEPOT) ||
						(role.getType() == RoleConstants.TYPE_SITE)) {

						userGroupRolePersistence.remove(userGroupRole);
					}
				}
			}

			List<Team> oldTeams = userPersistence.getTeams(userId);

			List<Team> removedFromTeams = new ArrayList<>();

			for (Team team : oldTeams) {
				if (ArrayUtil.contains(groupIds, team.getGroupId())) {
					removedFromTeams.add(team);
				}
			}

			if (!removedFromTeams.isEmpty()) {
				userPersistence.removeTeams(userId, removedFromTeams);
			}

			userPersistence.removeGroups(userId, groupIds);

			TransactionCommitCallbackUtil.registerCallback(
				() -> {
					Message message = new Message();

					message.put("groupIds", groupIds);
					message.put("userId", userId);

					MessageBusUtil.sendMessage(
						DestinationNames.SUBSCRIPTION_CLEAN_UP, message);

					return null;
				});
		}

//	
//	
//	
//	
//	
//	Reindex methods
//	
//	
//	
//	
//	
	protected void reindex(List<User> users) throws SearchException {
		Indexer<User> indexer = IndexerRegistryUtil.nullSafeGetIndexer(User.class);

		try {
			indexer.reindex(users);
		} catch (SearchException searchException) {
			throw new SystemException(searchException);
		}
	}

	protected void reindex(long userId) throws SearchException {
		Indexer<User> indexer = IndexerRegistryUtil.nullSafeGetIndexer(User.class);

		User user = getWrappedService().fetchUser(userId);

		indexer.reindex(user);
	}

	protected void reindex(long[] userIds) throws SearchException {
		Indexer<User> indexer = IndexerRegistryUtil.nullSafeGetIndexer(User.class);

		List<User> users = new ArrayList<>(userIds.length);

		for (Long userId : userIds) {
			User user = getWrappedService().fetchUser(userId);

			users.add(user);
		}

		indexer.reindex(users);
	}

	protected void reindex(User user) throws SearchException {
		Indexer<User> indexer = IndexerRegistryUtil.nullSafeGetIndexer(User.class);

		indexer.reindex(user);
	}

	// sda
	private static final Log _log = LogFactoryUtil.getLog(ScreenNameLocalFormatter.class);

	private static volatile UserFileUploadsSettings _userFileUploadsSettings = ServiceProxyFactory
			.newServiceTrackedInstance(UserFileUploadsSettings.class, ScreenNameLocalFormatter.class,
					"_userFileUploadsSettings", false);

//	
//	
//	
//	
//	
//	validate stuff
//	
//	
//
//	
//
	protected void validate(long companyId, long userId, boolean autoPassword, String password1, String password2,
			boolean autoScreenName, String screenName, String emailAddress, String openId, String firstName,
			String middleName, String lastName, long[] organizationIds, Locale locale) throws PortalException {

		validateCompanyMaxUsers(companyId);

		if (!autoScreenName) {
			validateScreenName(companyId, userId, screenName);
		}

		if (!autoPassword) {
			PasswordPolicy passwordPolicy = passwordPolicyLocalService.getDefaultPasswordPolicy(companyId);

			PwdToolkitUtil.validate(companyId, 0, password1, password2, passwordPolicy);
		}

		validateEmailAddress(companyId, emailAddress);

		if (Validator.isNotNull(emailAddress)) {
			User user = userPersistence.fetchByC_EA(companyId, emailAddress);

			if ((user != null) && (user.getUserId() != userId)) {
				throw new UserEmailAddressException.MustNotBeDuplicate(companyId, emailAddress);
			}
		}

		validateOpenId(companyId, userId, openId);

		validateFullName(companyId, firstName, middleName, lastName, locale);

		if (organizationIds != null) {
			for (long organizationId : organizationIds) {
				Organization organization = organizationPersistence.fetchByPrimaryKey(organizationId);

				if (organization == null) {
					throw new NoSuchOrganizationException("{organizationId=" + organizationId + "}");
				}
			}
		}
	}

	protected void validate(long userId, String screenName, String emailAddress, String openId, String firstName,
			String middleName, String lastName, String smsSn, Locale locale) throws PortalException {

		User user = userPersistence.findByPrimaryKey(userId);

		if (!StringUtil.equalsIgnoreCase(user.getScreenName(), screenName)) {
			validateScreenName(user.getCompanyId(), userId, screenName);
		}

		validateEmailAddress(user.getCompanyId(), emailAddress);

		validateOpenId(user.getCompanyId(), userId, openId);

		if (!user.isDefaultUser()) {
			if (Validator.isNotNull(emailAddress)
					&& !StringUtil.equalsIgnoreCase(user.getEmailAddress(), emailAddress)) {

				User userWithSameEmailAddress = userPersistence.fetchByC_EA(user.getCompanyId(), emailAddress);

				if (userWithSameEmailAddress != null) {
					throw new UserEmailAddressException.MustNotBeDuplicate(user.getCompanyId(), userId, emailAddress);
				}
			}

			validateFullName(user.getCompanyId(), firstName, middleName, lastName, locale);
		}

		if (Validator.isNotNull(smsSn) && !Validator.isEmailAddress(smsSn)) {
			throw new UserSmsException.MustBeEmailAddress(smsSn);
		}
	}

	protected void validateCompanyMaxUsers(long companyId) throws PortalException {

		Company company = companyPersistence.findByPrimaryKey(companyId);

		if (company.isSystem() || (company.getMaxUsers() == 0)) {
			return;
		}

		int userCount = searchCount(companyId, null, WorkflowConstants.STATUS_APPROVED, null);

		if (userCount >= company.getMaxUsers()) {
			throw new CompanyMaxUsersException();
		}
	}

	protected void validateEmailAddress(long companyId, String emailAddress) throws PortalException {

		if (Validator.isNull(emailAddress) && !PropsValues.USERS_EMAIL_ADDRESS_REQUIRED) {

			return;
		}

		if (!this.emailAddressValidator.validate(companyId, emailAddress)) {
			throw new UserEmailAddressException.MustValidate(emailAddress, this.emailAddressValidator);
		}

		String pop3User = PrefsPropsUtil.getString(PropsKeys.MAIL_SESSION_MAIL_POP3_USER,
				PropsValues.MAIL_SESSION_MAIL_POP3_USER);

		if (StringUtil.equalsIgnoreCase(emailAddress, pop3User)) {
			throw new UserEmailAddressException.MustNotBePOP3User(emailAddress);
		}

		String[] reservedEmailAddresses = PrefsPropsUtil.getStringArray(companyId,
				PropsKeys.ADMIN_RESERVED_EMAIL_ADDRESSES, StringPool.NEW_LINE,
				PropsValues.ADMIN_RESERVED_EMAIL_ADDRESSES);

		for (String reservedEmailAddress : reservedEmailAddresses) {
			if (StringUtil.equalsIgnoreCase(emailAddress, reservedEmailAddress)) {

				throw new UserEmailAddressException.MustNotBeReserved(emailAddress, reservedEmailAddresses);
			}
		}
	}

	protected void validateEmailAddress(User user, String emailAddress1, String emailAddress2) throws PortalException {

		if (!emailAddress1.equals(emailAddress2)) {
			throw new UserEmailAddressException.MustBeEqual(user, emailAddress1, emailAddress2);
		}

		validateEmailAddress(user.getCompanyId(), emailAddress1);
		validateEmailAddress(user.getCompanyId(), emailAddress2);

		if (!StringUtil.equalsIgnoreCase(emailAddress1, user.getEmailAddress())) {

			User userWithSameEmailAddress = userPersistence.fetchByC_EA(user.getCompanyId(), emailAddress1);

			if (userWithSameEmailAddress != null) {
				throw new UserEmailAddressException.MustNotBeDuplicate(user.getCompanyId(), user.getUserId(),
						emailAddress1);
			}
		}
	}

	protected void validateFullName(long companyId, String firstName, String middleName, String lastName, Locale locale)
			throws PortalException {

		FullNameDefinition fullNameDefinition = FullNameDefinitionFactory.getInstance(locale);

		if (Validator.isNull(firstName)) {
			throw new ContactNameException.MustHaveFirstName();
		} else if (Validator.isNull(middleName) && fullNameDefinition.isFieldRequired("middle-name")) {

			throw new ContactNameException.MustHaveMiddleName();
		} else if (Validator.isNull(lastName) && fullNameDefinition.isFieldRequired("last-name")) {

			throw new ContactNameException.MustHaveLastName();
		}

		if (!this.fullNameValidator.validate(companyId, firstName, middleName, lastName)) {

			throw new ContactNameException.MustHaveValidFullName(fullNameValidator);
		}
	}

	protected void validateGoogleUserId(long companyId, long userId, String googleUserId) throws PortalException {

		if (Validator.isNull(googleUserId)) {
			return;
		}

		User user = userPersistence.fetchByC_GUID(companyId, googleUserId);

		if ((user != null) && (user.getUserId() != userId)) {
			throw new DuplicateGoogleUserIdException(
					StringBundler.concat("New user ", userId, " conflicts with existing user ", userId,
							" who is already associated with Google user ID ", googleUserId));
		}
	}

	protected void validateOpenId(long companyId, long userId, String openId) throws PortalException {

		if (Validator.isNull(openId)) {
			return;
		}

		User user = userPersistence.fetchByC_O(companyId, openId);

		if ((user != null) && (user.getUserId() != userId)) {
			throw new DuplicateOpenIdException("{userId=" + userId + "}");
		}
	}

	protected void validatePassword(long companyId, long userId, String password1, String password2)
			throws PortalException {

		if (Validator.isNull(password1) || Validator.isNull(password2)) {
			throw new UserPasswordException.MustNotBeNull(userId);
		}

		if (!password1.equals(password2)) {
			throw new UserPasswordException.MustMatch(userId);
		}

		PasswordPolicy passwordPolicy = passwordPolicyLocalService.getPasswordPolicyByUserId(userId);

		PwdToolkitUtil.validate(companyId, userId, password1, password2, passwordPolicy);
	}

	protected void validateReminderQuery(String question, String answer) throws PortalException {

		if (!PropsValues.USERS_REMINDER_QUERIES_ENABLED) {
			return;
		}

		if (Validator.isNull(question)) {
			throw new UserReminderQueryException("Question is null");
		}

		if (Validator.isNull(answer)) {
			throw new UserReminderQueryException("Answer is null");
		}
	}

	protected void validateScreenName(long companyId, long userId, String screenName) throws PortalException {
		
		if (Validator.isNull(screenName)) {
			throw new UserScreenNameException.MustNotBeNull(userId);
		}

		if (!this.screenNameValidator.validate(companyId, screenName)) {
			throw new UserScreenNameException.MustValidate(userId, screenName, screenNameValidator);
		}

		if (Validator.isNumber(screenName) && !PropsValues.USERS_SCREEN_NAME_ALLOW_NUMERIC) {

			throw new UserScreenNameException.MustNotBeNumeric(userId, screenName);
		}

		String[] anonymousNames = BaseServiceImpl.ANONYMOUS_NAMES;

		for (String anonymousName : anonymousNames) {
			if (StringUtil.equalsIgnoreCase(screenName, anonymousName)) {
				throw new UserScreenNameException.MustNotBeReservedForAnonymous(userId, screenName, anonymousNames);
			}
		}

		User user = userPersistence.fetchByC_SN(companyId, screenName);

		if ((user != null) && (user.getUserId() != userId)) {
			throw new UserScreenNameException.MustNotBeDuplicate(user.getUserId(), screenName);
		}

		String friendlyURL = FriendlyURLNormalizerUtil.normalize(StringPool.SLASH + screenName);

		int exceptionType = LayoutImpl.validateFriendlyURL(friendlyURL);

		if (exceptionType != -1) {
			throw new UserScreenNameException.MustProduceValidFriendlyURL(userId, screenName, exceptionType);
		}

		String[] reservedScreenNames = PrefsPropsUtil.getStringArray(companyId, PropsKeys.ADMIN_RESERVED_SCREEN_NAMES,
				StringPool.NEW_LINE, PropsValues.ADMIN_RESERVED_SCREEN_NAMES);

		for (String reservedScreenName : reservedScreenNames) {
			if (StringUtil.equalsIgnoreCase(screenName, reservedScreenName)) {
				throw new UserScreenNameException.MustNotBeReserved(userId, screenName, reservedScreenNames);
			}
		}
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

	@Reference
	protected UserPersistence userPersistence;

	@Reference
	protected com.liferay.portal.kernel.service.ResourceLocalService resourceLocalService;

	@Reference
	protected CompanyPersistence companyPersistence;

	@Reference
	protected com.liferay.counter.kernel.service.CounterLocalService counterLocalService;

	@Reference
	protected ContactPersistence contactPersistence;

	@Reference
	protected com.liferay.portal.kernel.service.GroupLocalService groupLocalService;

	@Reference
	protected com.liferay.portal.kernel.service.PasswordPolicyLocalService passwordPolicyLocalService;

	@Reference
	protected OrganizationPersistence organizationPersistence;

	@Reference
	protected com.liferay.portal.kernel.service.UserGroupRoleLocalService userGroupRoleLocalService;

	@Reference
	protected com.liferay.portal.kernel.service.OrganizationLocalService organizationLocalService;

	@Reference
	protected com.liferay.portal.kernel.service.RoleLocalService roleLocalService;

	@Reference
	protected com.liferay.portal.kernel.service.CompanyLocalService companyLocalService;

	@Reference
	protected MailService mailService;

	@Reference
	protected UserGroupRolePersistence userGroupRolePersistence;

	@Reference
	protected com.liferay.announcements.kernel.service.AnnouncementsDeliveryLocalService announcementsDeliveryLocalService;

	@Reference
	protected RolePersistence rolePersistence;
	
	@Reference
	protected TeamPersistence teamPersistence;
	
	@Reference(
			policy = ReferencePolicy.DYNAMIC,
			policyOption = ReferencePolicyOption.GREEDY
		)
	protected volatile EmailAddressGenerator emailAddressGenerator;
	
	@Reference(
			policy = ReferencePolicy.DYNAMIC,
			policyOption = ReferencePolicyOption.GREEDY
		)
	protected volatile ScreenNameGenerator screenNameGenerator;
	
	@Reference(
			policy = ReferencePolicy.DYNAMIC,
			policyOption = ReferencePolicyOption.GREEDY
		)
	protected volatile ScreenNameValidator screenNameValidator;
	
	@Reference(
			policy = ReferencePolicy.DYNAMIC,
			policyOption = ReferencePolicyOption.GREEDY
		)
	protected volatile FullNameValidator fullNameValidator;
	
	@Reference(
			policy = ReferencePolicy.DYNAMIC,
			policyOption = ReferencePolicyOption.GREEDY
		)
	protected volatile EmailAddressValidator emailAddressValidator;
	
	@Reference(unbind = "-")
	private void serviceSetter(UserLocalService userLocalService) {
		setWrappedService(userLocalService);
	}
	
//	
//	
//	
//	
//	
//	Get and set for the beans references
//	
//	
//	
//	
//	
	
	public UserPersistence getUserPersistence() {
		return userPersistence;
	}

	public void setUserPersistence(UserPersistence userPersistence) {
		this.userPersistence = userPersistence;
	}

	public com.liferay.portal.kernel.service.ResourceLocalService getResourceLocalService() {
		return resourceLocalService;
	}

	public void setResourceLocalService(com.liferay.portal.kernel.service.ResourceLocalService resourceLocalService) {
		this.resourceLocalService = resourceLocalService;
	}

	public CompanyPersistence getCompanyPersistence() {
		return companyPersistence;
	}

	public void setCompanyPersistence(CompanyPersistence companyPersistence) {
		this.companyPersistence = companyPersistence;
	}

	public com.liferay.counter.kernel.service.CounterLocalService getCounterLocalService() {
		return counterLocalService;
	}

	public void setCounterLocalService(com.liferay.counter.kernel.service.CounterLocalService counterLocalService) {
		this.counterLocalService = counterLocalService;
	}

	public ContactPersistence getContactPersistence() {
		return contactPersistence;
	}

	public void setContactPersistence(ContactPersistence contactPersistence) {
		this.contactPersistence = contactPersistence;
	}

	public com.liferay.portal.kernel.service.GroupLocalService getGroupLocalService() {
		return groupLocalService;
	}

	public void setGroupLocalService(com.liferay.portal.kernel.service.GroupLocalService groupLocalService) {
		this.groupLocalService = groupLocalService;
	}

	public com.liferay.portal.kernel.service.PasswordPolicyLocalService getPasswordPolicyLocalService() {
		return passwordPolicyLocalService;
	}

	public void setPasswordPolicyLocalService(
			com.liferay.portal.kernel.service.PasswordPolicyLocalService passwordPolicyLocalService) {
		this.passwordPolicyLocalService = passwordPolicyLocalService;
	}

	public OrganizationPersistence getOrganizationPersistence() {
		return organizationPersistence;
	}

	public void setOrganizationPersistence(OrganizationPersistence organizationPersistence) {
		this.organizationPersistence = organizationPersistence;
	}

	public com.liferay.portal.kernel.service.UserGroupRoleLocalService getUserGroupRoleLocalService() {
		return userGroupRoleLocalService;
	}

	public void setUserGroupRoleLocalService(
			com.liferay.portal.kernel.service.UserGroupRoleLocalService userGroupRoleLocalService) {
		this.userGroupRoleLocalService = userGroupRoleLocalService;
	}

	public com.liferay.portal.kernel.service.OrganizationLocalService getOrganizationLocalService() {
		return organizationLocalService;
	}

	public void setOrganizationLocalService(
			com.liferay.portal.kernel.service.OrganizationLocalService organizationLocalService) {
		this.organizationLocalService = organizationLocalService;
	}

	public com.liferay.portal.kernel.service.RoleLocalService getRoleLocalService() {
		return roleLocalService;
	}

	public void setRoleLocalService(com.liferay.portal.kernel.service.RoleLocalService roleLocalService) {
		this.roleLocalService = roleLocalService;
	}

	public com.liferay.portal.kernel.service.CompanyLocalService getCompanyLocalService() {
		return companyLocalService;
	}

	public void setCompanyLocalService(com.liferay.portal.kernel.service.CompanyLocalService companyLocalService) {
		this.companyLocalService = companyLocalService;
	}

	public MailService getMailService() {
		return mailService;
	}

	public void setMailService(MailService mailService) {
		this.mailService = mailService;
	}

	public UserGroupRolePersistence getUserGroupRolePersistence() {
		return userGroupRolePersistence;
	}

	public void setUserGroupRolePersistence(UserGroupRolePersistence userGroupRolePersistence) {
		this.userGroupRolePersistence = userGroupRolePersistence;
	}

	public com.liferay.announcements.kernel.service.AnnouncementsDeliveryLocalService getAnnouncementsDeliveryLocalService() {
		return announcementsDeliveryLocalService;
	}

	public void setAnnouncementsDeliveryLocalService(
			com.liferay.announcements.kernel.service.AnnouncementsDeliveryLocalService announcementsDeliveryLocalService) {
		this.announcementsDeliveryLocalService = announcementsDeliveryLocalService;
	}

	public RolePersistence getRolePersistence() {
		return rolePersistence;
	}

	public void setRolePersistence(RolePersistence rolePersistence) {
		this.rolePersistence = rolePersistence;
	}

	public TeamPersistence getTeamPersistence() {
		return teamPersistence;
	}

	public void setTeamPersistence(TeamPersistence teamPersistence) {
		this.teamPersistence = teamPersistence;
	}

	public EmailAddressGenerator getEmailAddressGenerator() {
		return emailAddressGenerator;
	}

	public void setEmailAddressGenerator(EmailAddressGenerator emailAddressGenerator) {
		this.emailAddressGenerator = emailAddressGenerator;
	}

	public ScreenNameGenerator getScreenNameGenerator() {
		return screenNameGenerator;
	}

	public void setScreenNameGenerator(ScreenNameGenerator screenNameGenerator) {
		this.screenNameGenerator = screenNameGenerator;
	}

	public ScreenNameValidator getScreenNameValidator() {
		return screenNameValidator;
	}

	public void setScreenNameValidator(ScreenNameValidator screenNameValidator) {
		this.screenNameValidator = screenNameValidator;
	}

	public FullNameValidator getFullNameValidator() {
		return fullNameValidator;
	}

	public void setFullNameValidator(FullNameValidator fullNameValidator) {
		this.fullNameValidator = fullNameValidator;
	}

	public EmailAddressValidator getEmailAddressValidator() {
		return emailAddressValidator;
	}

	public void setEmailAddressValidator(EmailAddressValidator emailAddressValidator) {
		this.emailAddressValidator = emailAddressValidator;
	}
}