package com.vanderlande.viac.template.model;

import java.io.Serializable;

/**
 * Template variable for template helper.
 */
public class TemplateVariable implements Serializable{
    private static final long serialVersionUID = -2516572303084666739L;
    private String key, description, value;

    /**
     * Template variable with key and description.
     * @param key
     * @param description
     * @return 
     */
	public TemplateVariable(String key, String description) {
		super();
		this.key = key;
		this.description = description;
	}
	
	/**
	 * Template variable with key, description and value.
	 * @param key
	 * @param description
	 * @param value
	 * @return 
	 */
	public TemplateVariable(String key, String description, String value) {
		super();
		this.key = key;
		this.description = description;
		this.value = value;
	}

	/**
	 * Get template value
	 * @return value
	 */
	public String getValue() {
		return value;
	}

	/**
	 * Set template value
	 * @param value
	 */
	public void setValue(String value) {
		this.value = value;
	}

	/**
	 * Get template key
	 * @return key
	 */
	public String getKey() {
		return key;
	}

	/**
	 * Set template key
	 * @param key
	 */
	public void setKey(String key) {
		this.key = key;
	}

	/**
	 * Get template description
	 * @return description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * Set template description
	 * @param description
	 */
	public void setDescription(String description) {
		this.description = description;
	}
	
}