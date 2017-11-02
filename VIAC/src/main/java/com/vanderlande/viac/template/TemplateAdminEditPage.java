package com.vanderlande.viac.template;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.form.upload.FileUpload;
import org.apache.wicket.markup.html.link.DownloadLink;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.panel.Fragment;
import org.apache.wicket.markup.repeater.RepeatingView;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.PropertyModel;

import com.vanderlande.util.DatabaseProvider;
import com.vanderlande.util.MultiFileUploadForm;
import com.vanderlande.viac.VIACBasePage;
import com.vanderlande.viac.model.MailDocument;
import com.vanderlande.viac.model.MailTemplate;
import com.vanderlande.viac.template.model.TemplateHelper;
import com.vanderlande.viac.template.model.TemplateVariable;

/**
 * The class TemplateAdminEditPage
 * 
 * @author dedhor, dekscha
 * 
 * Admin template page to edit a template (text, cc, attachments)
 */
public class TemplateAdminEditPage extends VIACBasePage {
	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 3924035333075661072L;
	
	/** The template. */
	private final MailTemplate template;

	/**
	 * Constructor for the template edit page.
	 *
	 * @param template the template
	 */
	public TemplateAdminEditPage(MailTemplate template){
		super();
		this.template = template;
		init();
	}
	
	/**
	 * Init function creates the required wicket objects and binds them to the html file.
	 */
	private void init(){
		Form<?> form = new Form<>("form");
		
		
		Label idLabel = new Label("idLabel", "Template ID:");
		TextField<String> idTextField = new TextField<>("id", new PropertyModel<String>(this.template, "key"));
		
		Label ccLabel = new Label("ccLabel", "CC (Mail-Adressen mit \";\" separieren):");
		TextField<String> ccTextField = new TextField<>("ccInput", new PropertyModel<String>(this.template, "carbonCopy"));
		
		Label textLabel = new Label("textLabel", "Template:");
		TextArea<String> textTextArea = new TextArea<String>("text",new PropertyModel<String>(this.template, "mailText"));
		
		RepeatingView navigationContainer = new RepeatingView("navContainer");
		form.add(navigationContainer);
		navigationContainer.setOutputMarkupId(true);
		
		final Set<MailDocument> documents = template.getDocuments();

		//list template attachments
		for (final MailDocument mailDoc : documents) {
			DownloadLink downloadLink = new DownloadLink("download", new LoadableDetachableModel<File>() {
				private static final long serialVersionUID = 1L;

				@Override
				protected File load() {
					final File file = new File(mailDoc.getFileName());
					try {
						FileUtils.writeByteArrayToFile(new File(mailDoc.getFileName()), mailDoc.getData());
						return file;
					} catch (IOException e) {
						return null;
					}
				}
			});

			final Fragment fragment = new Fragment(navigationContainer.newChildId(), "fragment", this);
			fragment.setOutputMarkupId(true);
			final Label fileName = new Label("navItemLabel", mailDoc.getFileName());

			fragment.add(downloadLink);
			downloadLink.add(fileName);
			navigationContainer.add(fragment);

			//implement delete for existing attachments
			AjaxLink deleteLink = new AjaxLink("deleteLink") {
				private static final long serialVersionUID = 1L;

				@Override
				public void onClick(AjaxRequestTarget target) {
					template.getDocuments().remove(mailDoc);
					DatabaseProvider.getInstance().delete(mailDoc);
					documents.remove(mailDoc);
					setResponsePage(new TemplateAdminEditPage(template));
				}
			};

			fragment.add(deleteLink);
		}
		
		//implement upload field for attachments
		MultiFileUploadForm fileUploadForm = new MultiFileUploadForm("fileUploadForm") {
			private static final long serialVersionUID = 1L;

			@Override
			protected void onSubmit() {
				for (FileUpload upload : this.getUploads()) {
					// Create a new file
					File newFile = new File(upload.getClientFileName());

					// Check new file, delete if it already existed
					this.checkFileExists(newFile);
					try {
						upload.writeTo(newFile);

						Path path = Paths.get(newFile.getPath());
						byte[] data = java.nio.file.Files.readAllBytes(path);

						MailDocument document = new MailDocument();
						document.setTemplate(template);
						document.setData(data);
						document.setFileName(upload.getClientFileName());
						document.setFileType(upload.getContentType());
						DatabaseProvider.getInstance().create(document);
						template.getDocuments().add(document);

					} catch (Exception e) {
						e.printStackTrace();
						throw new IllegalStateException("Unable to write file");
					}
				}
				setResponsePage(new TemplateAdminEditPage(template));
			}
		};

		Button uploadButton = new Button("uploadButton");

		fileUploadForm.add(uploadButton);
		
		//Print available template markers
		String templateVariables = "Folgende Variablen stehen zur Verfügung:";
		templateVariables += "<table class=\"table table-striped\">";
		templateVariables += "<tr><th>Schlüssel</th><th>Beschreibung</th></tr>";
		for (TemplateVariable t: TemplateHelper.getInstance().getTemplateValues()){
			templateVariables += "<tr><td>" + t.getKey() + "</td><td>" + t.getDescription() + "</td></td>";
		}
		templateVariables += "</table>";
		Label infoSpan = (Label) new Label("infoSpan", templateVariables).setEscapeModelStrings(false);
		
		form.add(idLabel);
		form.add(idTextField);
		form.add(ccLabel);
		form.add(ccTextField);
		form.add(textLabel);
		form.add(textTextArea);
		form.add(infoSpan);
		form.add(fileUploadForm);
		
		//implement back button
		Link<String> backButton = new Link<String>("back") {
			private static final long serialVersionUID = 1L;

			@Override
			public void onClick() {
				setResponsePage(TemplateAdminPage.class);
			}
		};
		form.add(backButton);

		//implement save button
		Button submitButton = new Button("submit") {
			private static final long serialVersionUID = -6945656790341817258L;

			@Override
			public void onSubmit() {
				saveTemplate();
			}
		};
		form.add(submitButton);

		add(form);
	}
	
	/**
	 * Save the changed template in the database.
	 */
	private void saveTemplate(){
		DatabaseProvider.getInstance().merge(template);
	}
}
