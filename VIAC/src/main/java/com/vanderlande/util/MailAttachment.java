package com.vanderlande.util;

/**
 * @author dedhor
 * Class for mail attachment. Used for unified attachments for both Exchange and JavaMail.
 */
public class MailAttachment{
	private String name;
	private String mimeType;
	private byte[] content;
	private boolean isInline;
	
	/**
	 * Constructor for a mail attachment, name and content is required.
	 * Automatically set mime type to binary if not given.
	 * @param name
	 * @param content
	 */
	public MailAttachment(String name, byte[] content) {
		this.name = name;
		this.content = content;
		this.isInline = false;
		this.mimeType = "application/binary";
	}
	
	/**
	 * Get the mime type.
	 * @return mime type
	 */
	public String getMimeType() {
		return mimeType;
	}

	/**
	 * Set the mime type.
	 * @param mime type
	 */
	public void setMimeType(String mimeType) {
		this.mimeType = mimeType;
	}

	/**
	 * Get wether the attachment will be usable in the text of the mail.
	 * @return is inline
	 */
	public boolean isInline() {
		return isInline;
	}

	/**
	 * Set wether the attachment will be usable in the text of the mail.
	 * @param is inline
	 */
	public void setInline(boolean isInline) {
		this.isInline = isInline;
	}

	/**
	 * Get the filename of the attachment.
	 * @return filename
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * Set the filename for the attachment.
	 * @param filename
	 */
	public void setName(String name) {
		this.name = name;
	}
	
	/**
	 * Get the byte content of the attachment.
	 * @return content
	 */
	public byte[] getContent() {
		return content;
	}
	
	/**
	 * Set the byte content for the attachment.
	 * @param content
	 */
	public void setContent(byte[] content) {
		this.content = content;
	}
}