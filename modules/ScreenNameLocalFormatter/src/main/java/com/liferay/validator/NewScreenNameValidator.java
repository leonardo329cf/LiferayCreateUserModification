package com.liferay.validator;


import com.liferay.formatter.keys.NewFormatterKeys;
import com.liferay.petra.string.CharPool;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.security.auth.ScreenNameValidator;
import com.liferay.portal.kernel.util.HtmlUtil;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.PropsUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;

import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.osgi.service.component.annotations.Component;

/**
 * @author leonardo.ferreira
 */
@Component(
		immediate = true, property = "service.ranking:Integer=100",
		service = ScreenNameValidator.class
	)
public class NewScreenNameValidator implements ScreenNameValidator {

	public static final String POSTFIX = "postfix";

	@Override
	public String getAUIValidatorJS() {
		return "function(val) {var pattern = new RegExp('[^A-Za-z0-9" +
			getJSEscapedSpecialChars() +
				"]');if (val.match(pattern)) {return false;}return true;}";
	}

	@Override
	public String getDescription(Locale locale) {
		return LanguageUtil.format(
			locale,
			"the-screen-name-needs-to-have-correct-email-suffix-if-is-email-or-cannot-contain-reserved-word",
			new String[] {getCorrectEmail(), POSTFIX, getSpecialChars()}, false);
	}

	@Override
	public boolean validate(long companyId, String screenName) {
		
		if (!Validator.isEmailAddress(screenName) || 
			StringUtil.equalsIgnoreCase(screenName, POSTFIX) ||
			hasInvalidChars(screenName) ||
			!screenName.endsWith(getCorrectEmail())
			) {
			return false;
		}
		return true;
	}
	
	public String getCorrectEmail() {
		return PropsUtil.get(NewFormatterKeys.USERS_SCREEN_NAME_COMPANY_EMAIL);
	}

	protected String getJSEscapedSpecialChars() {
		if (_jsEscapedSpecialChars == null) {
			_jsEscapedSpecialChars = HtmlUtil.escapeJS(getSpecialCharsRegex());
		}

		return _jsEscapedSpecialChars;
	}

	protected String getSpecialChars() {
		if (_specialChars == null) {
			String specialChars = PropsUtil.get(
				PropsKeys.USERS_SCREEN_NAME_SPECIAL_CHARACTERS);

			_specialChars = StringUtil.removeChar(specialChars, CharPool.SLASH);
		}

		return _specialChars;
	}

	protected String getSpecialCharsRegex() {
		if (_specialCharsRegex == null) {
			Matcher matcher = _escapeRegexPattern.matcher(getSpecialChars());

			_specialCharsRegex = matcher.replaceAll("\\\\$0");
		}

		return _specialCharsRegex;
	}

	protected boolean hasInvalidChars(String screenName) {
		return !screenName.matches(
			"[A-Za-z0-9" + getSpecialCharsRegex() + "]+");
	}

	private static final Pattern _escapeRegexPattern = Pattern.compile(
		"[-+\\\\\\[\\]]");

	private String _jsEscapedSpecialChars;
	private String _specialChars;
	private String _specialCharsRegex;
}