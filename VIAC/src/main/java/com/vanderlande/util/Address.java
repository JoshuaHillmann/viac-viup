package com.vanderlande.util;

import java.io.Serializable;

import javax.persistence.Embeddable;

/**
 * The Class Address.
 */
@Embeddable
public class Address implements Serializable {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = -4431351722078090077L;

	/** The city. */
	private String city;

	/** The postal code. */
	private String postalCode;

	/** The street. */
	private String street;

	/**
	 * Gets the city.
	 *
	 * @return the city
	 */
	public String getCity() {
		return city;
	}

	/**
	 * Sets the city.
	 *
	 * @param city
	 *            the new city
	 */
	public void setCity(String city) {
		this.city = city;
	}

	/**
	 * Gets the postal code.
	 *
	 * @return the postal code
	 */
	public String getPostalCode() {
		return postalCode;
	}

	/**
	 * Sets the postal code.
	 *
	 * @param postalCode
	 *            the new postal code
	 */
	public void setPostalCode(String postalCode) {
		this.postalCode = postalCode;
	}

	/**
	 * Gets the street.
	 *
	 * @return the street
	 */
	public String getStreet() {
		return street;
	}

	/**
	 * Sets the street.
	 *
	 * @param street
	 *            the new street
	 */
	public void setStreet(String street) {
		this.street = street;
	}
}
