package com.liferay.validator.util;

import com.liferay.portal.kernel.language.UTF8Control;

import java.util.Enumeration;
import java.util.ResourceBundle;

import org.osgi.service.component.annotations.Component;

@Component(
	    property = { "language.id=" }, 
	    service = ResourceBundle.class
	)
public class NewScreenNameValidatorResourceBundle extends ResourceBundle {

	@Override
	protected Object handleGetObject(String key) {
		return _resourceBundle.getObject(key);
	}

	@Override
	public Enumeration<String> getKeys() {
		return _resourceBundle.getKeys();
	}
	
	private final ResourceBundle _resourceBundle = ResourceBundle.getBundle(
	        "content.Language", UTF8Control.INSTANCE);
}
