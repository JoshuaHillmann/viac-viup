package com.vanderlande.util;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;

import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.upload.FileUpload;
import org.apache.wicket.markup.html.form.upload.MultiFileUploadField;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.util.file.Files;
import org.apache.wicket.util.lang.Bytes;

/**
 * The Class MultiFileUploadForm.
 * 
 * @author dekscha
 * 
 * Used to upload multiple files in a form.
 */
public class MultiFileUploadForm extends Form<Void> {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = -2622652680596237629L;

	/**
	 * The uploads. collection that will hold uploaded FileUpload objects
	 */
	private final Collection<FileUpload> uploads = new ArrayList<>();

	/**
	 * Gets the uploads.
	 *
	 * @return the uploads
	 */
	public Collection<FileUpload> getUploads() {
		return uploads;
	}

	/**
	 * Construct.
	 * 
	 * @param name
	 *            Component name
	 */
	public MultiFileUploadForm(String name) {
		super(name);

		// set this form to multipart mode (always needed for uploads!)
		setMultiPart(true);

		// Add one multi-file upload field
		add(new MultiFileUploadField("fileInput", new PropertyModel<Collection<FileUpload>>(this, "uploads")));

		setMaxSize(Bytes.megabytes(500));

		setFileMaxSize(Bytes.megabytes(5));
	}

	/**
	 * Check whether the file already exists, and if so, try to delete it.
	 * 
	 * @param newFile
	 *            the file to check
	 */
	public void checkFileExists(File newFile) {
		if (newFile.exists()) {
			// Try to delete the file
			if (!Files.remove(newFile)) {
				throw new IllegalStateException("Unable to overwrite " + newFile.getAbsolutePath());
			}
		}
	}
}
