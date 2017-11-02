package com.vanderlande.vita.applicant;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.extensions.markup.html.form.DateTextField;
import org.apache.wicket.extensions.markup.html.repeater.data.table.DataTable;
import org.apache.wicket.extensions.markup.html.repeater.data.table.HeadersToolbar;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.NavigationToolbar;
import org.apache.wicket.extensions.markup.html.repeater.data.table.PropertyColumn;
import org.apache.wicket.extensions.yui.calendar.DatePicker;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.form.upload.FileUpload;
import org.apache.wicket.markup.html.link.DownloadLink;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.panel.Fragment;
import org.apache.wicket.markup.repeater.RepeatingView;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.validation.validator.EmailAddressValidator;
import org.apache.wicket.validation.validator.StringValidator;

import com.vanderlande.util.DatabaseProvider;
import com.vanderlande.util.MultiFileUploadForm;
import com.vanderlande.viac.session.VIACSession;
import com.vanderlande.vita.VITABasePage;
import com.vanderlande.vita.applicant.dialog.ConfirmCreateDialogContent;
import com.vanderlande.vita.applicant.provider.SortableStatusHistoryDataProvider;
import com.vanderlande.vita.model.VITAApplicant;
import com.vanderlande.vita.model.VITAApplicantStatus;
import com.vanderlande.vita.model.VITACareer;
import com.vanderlande.vita.model.VITADocument;
import com.vanderlande.vita.model.VITAHistoricStatus;

/**
 * The Class ApplicantDetailPage.
 * 
 * @author denmuj
 * 
 *         Displays all applicant-information. You can also edit the applicant
 *         and download his documents.
 */
public class ApplicantDetailPage extends VITABasePage implements Serializable {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 8485465225014267470L;

	/** The authorizations. */
	public List<String> authorizations;

	/** The comment area. */
	private TextArea<String> commentArea;

	/** The applicant model. */
	private VITAApplicant applicantModel;

	/** The is new applicant. */
	private boolean isNewApplicant;

	/** The download link. */
	private DownloadLink downloadLink;

	/** The save button. */
	private AjaxButton saveButton;

	/** The edit button. */
	private Button editButton;

	/** The career choice. */
	private DropDownChoice<VITACareer> careerChoice;

	/** The status choice. */
	private DropDownChoice<VITAApplicantStatus> statusChoice;

	/** The postal code field. */
	private TextField postalCodeField;

	/** The city field. */
	private TextField<String> cityField;

	/** The street field. */
	private TextField<String> streetField;

	/** The phone field. */
	private TextField<String> phoneField;

	/** The birthday field. */
	private DateTextField birthdayField;

	/** The applied on field. */
	private DateTextField appliedOnField;

	/** The gender choice. */
	private DropDownChoice<String> genderChoice;

	/** The mail field. */
	private TextField<String> mailField;

	/** The lastname field. */
	private TextField<String> lastnameField;

	/** The firstname field. */
	private TextField<String> firstnameField;

	/** The eligo password field. */
	private TextField<String> eligoPasswordField;

	/** The eligo username field. */
	private TextField<String> eligoUsernameField;

	/** The eligo firstname field. */
	private TextField<String> eligoFirstnameField;

	/** The eligo lastname field. */
	private TextField<String> eligoLastnameField;

	/** The file upload form. */
	private MultiFileUploadForm fileUploadForm;

	/** The navigation container. */
	private RepeatingView navigationContainer;

	/** The back button. */
	private Link<String> backButton;

	/** The reject button. */
	private AjaxButton rejectButton;

	/** The accept button. */
	private AjaxButton acceptButton;

	/** The status 0. */
	private VITAApplicantStatus status0;

	/** The status 1. */
	private VITAApplicantStatus status1;

	/** The status 2. */
	private VITAApplicantStatus status2;

	/** The status 3. */
	private VITAApplicantStatus status3;

	/** The status history panel. */
	private WebMarkupContainer statusHistoryPanel;

	/** The documents panel. */
	private WebMarkupContainer documentsPanel;

	/** The table with filter form. */
	private DataTable<VITAHistoricStatus, String> tableWithFilterForm;

	/** The temp status. */
	private VITAApplicantStatus tempStatus;

	/** The last page. */
	private WebPage lastPage;

	/**
	 * Instantiates a new ApplicantDetailPage. This constructor is used when you
	 * need to create a new applicant
	 */
	public ApplicantDetailPage(WebPage lastPage) {
		applicantModel = new VITAApplicant();
		isNewApplicant = true;
		tempStatus = applicantModel.getStatus();
		this.lastPage = lastPage;
		init();
	}

	/**
	 * Instantiates a ApplicantDetailPage. This constructor is used when you
	 * need to edit an existing applicant
	 *
	 * @param applicant
	 *            the applicant
	 */
	public ApplicantDetailPage(VITAApplicant applicant, WebPage lastPage) {
		applicantModel = applicant;
		isNewApplicant = false;
		tempStatus = applicantModel.getStatus();
		this.lastPage = lastPage;
		init();
	}

	/**
	 * Initializes the components.
	 */
	private void init() {
		// gets status
		status1 = DatabaseProvider.getInstance().getAll(VITAApplicantStatus.class).get(0);
		status2 = DatabaseProvider.getInstance().getAll(VITAApplicantStatus.class).get(3);
		status3 = DatabaseProvider.getInstance().getAll(VITAApplicantStatus.class).get(7);
		status0 = DatabaseProvider.getInstance().getAll(VITAApplicantStatus.class).get(10);

		// create panel
		statusHistoryPanel = new WebMarkupContainer("statusHistoryPanel");
		documentsPanel = new WebMarkupContainer("documentsPanel");

		Form<?> form = new Form<>("form");
		form.add(statusHistoryPanel);
		form.add(documentsPanel);

		// confirm delete dialog
		final ModalWindow confirmDialog;
		add(confirmDialog = new ModalWindow("confirmDeleteDialog"));
		confirmDialog.showUnloadConfirmation(false);
		confirmDialog.setInitialWidth(ConfirmCreateDialogContent.DEFAULT_WIDTH);
		confirmDialog.setInitialHeight(ConfirmCreateDialogContent.DEFAULT_HEIGHT);

		confirmDialog.setResizable(false);
		confirmDialog.setCloseButtonCallback(new ModalWindow.CloseButtonCallback() {
			/** The Constant serialVersionUID. */
			private static final long serialVersionUID = 1L;

			@Override
			public boolean onCloseButtonClicked(AjaxRequestTarget target) {
				return true;
			}
		});

		// initialize all fields

		// comment arre
		Label commentLabel = new Label("commentLabel", new Model<String>("Bemerkungen:"));
		commentArea = new TextArea<String>("commentArea", new PropertyModel<String>(applicantModel, "comment"));
		commentArea.add(StringValidator.maximumLength(155));
		form.add(commentArea);

		// firstname
		Label firstnameLabel = new Label("firstnameLabel", new Model<String>("Vorname:"));
		firstnameField = new TextField<>("firstname", new PropertyModel<String>(applicantModel, "firstname"));

		// lastname
		Label lastnameLabel = new Label("lastnameLabel", new Model<String>("Nachname:"));
		lastnameField = new TextField<>("lastname", new PropertyModel<String>(applicantModel, "lastname"));

		// get all applicant status
		List<VITAApplicantStatus> status;
		status = new ArrayList<VITAApplicantStatus>();
		status = DatabaseProvider.getInstance().getAll(VITAApplicantStatus.class);

		// applicant status
		Label statusLabel = new Label("statusLabel", new Model<String>("Status:"));
		statusChoice = new DropDownChoice<VITAApplicantStatus>("status",
				new PropertyModel<VITAApplicantStatus>(applicantModel, "status"), status);

		// get all careers
		List<VITACareer> career;
		career = new ArrayList<VITACareer>();
		career = DatabaseProvider.getInstance().getAllActiveCareers();
		if (!career.contains(applicantModel.getCareer()) && applicantModel.getCareer() != null) {
			career.add(applicantModel.getCareer());
		}

		// career
		Label careerLabel = new Label("careerLabel", new Model<String>("Karriere:"));
		careerChoice = new DropDownChoice<VITACareer>("career", new PropertyModel<VITACareer>(applicantModel, "career"),
				career);

		// mail
		Label mailLabel = new Label("mailLabel", new Model<String>("Mail:"));
		mailField = new TextField<>("mail", new PropertyModel<String>(applicantModel, "mail"));
		mailField.add(EmailAddressValidator.getInstance());

		// birthday
		Label birthdayLabel = new Label("birthdayLabel", new Model<String>("Geburtstag:"));
		birthdayField = new DateTextField("birthday", new PropertyModel<Date>(applicantModel, "birthday"));
		birthdayField.add(new DatePicker().setShowOnFieldClick(true));

		// applied on
		Label appliedOnLabel = new Label("appliedOnLabel", new Model<String>("Beworben am:"));
		appliedOnField = new DateTextField("appliedOn", new PropertyModel<Date>(applicantModel, "appliedOn"));
		appliedOnField.add(new DatePicker().setShowOnFieldClick(true));

		// city
		Label cityLabel = new Label("cityLabel", new Model<String>("Stadt:"));
		cityField = new TextField<>("city", new PropertyModel<String>(applicantModel, "address.city"));

		// postal code
		Label postalCodeLabel = new Label("postalCodeLabel", new Model<String>("PLZ:"));
		postalCodeField = new TextField("postalCode", new PropertyModel<String>(applicantModel, "address.postalCode"),
				Integer.class);

		// street
		Label streetLabel = new Label("streetLabel", new Model<String>("Straße:"));
		streetField = new TextField<>("street", new PropertyModel<String>(applicantModel, "address.street"));

		// phone
		Label phoneLabel = new Label("phoneLabel", new Model<String>("Telefon:"));
		phoneField = new TextField<>("phone", new PropertyModel<String>(applicantModel, "phone"));

		// gender label
		Label genderLabel = new Label("genderLabel", new Model<String>("Geschlecht:"));

		// set gender
		List<String> gender;
		gender = new ArrayList<String>();
		gender.add("Weiblich");
		gender.add("Männlich");

		// gender choice
		genderChoice = new DropDownChoice<String>("gender", new PropertyModel<String>(applicantModel, "gender"),
				gender);

		// eligo username
		Label eligoUsernameLabel = new Label("eligoUsernameLabel", new Model<String>("Eligo Username:"));
		eligoUsernameField = new TextField<>("eligoUsername",
				new PropertyModel<String>(applicantModel, "eligoUsername"));

		// eligo password
		Label eligoPasswordLabel = new Label("eligoPasswordLabel", new Model<String>("Eligo Passwort:"));
		eligoPasswordField = new TextField<>("eligoPassword",
				new PropertyModel<String>(applicantModel, "eligoPassword"));

		// eligo lastname
		Label eligoLastnameLabel = new Label("eligoLastnameLabel", new Model<String>("Eligo Nachname:"));
		eligoLastnameField = new TextField<>("eligoLastname",
				new PropertyModel<String>(applicantModel, "eligoLastname"));

		// eligo firstname
		Label eligoFirstnameLabel = new Label("eligoFirstnameLabel", new Model<String>("Eligo Vorname:"));
		eligoFirstnameField = new TextField<>("eligoFirstname",
				new PropertyModel<String>(applicantModel, "eligoFirstname"));

		// navigations container - documents
		navigationContainer = new RepeatingView("navContainer");
		documentsPanel.add(navigationContainer);
		navigationContainer.setOutputMarkupId(true);

		final Set<VITADocument> documents = applicantModel.getDocuments();

		for (final VITADocument vitaDocument : documents) {
			downloadLink = new DownloadLink("download", new LoadableDetachableModel<File>() {
				/** The Constant serialVersionUID. */
				private static final long serialVersionUID = 1L;

				@Override
				protected File load() {
					final File file = new File(vitaDocument.getFileName());
					try {
						FileUtils.writeByteArrayToFile(new File(vitaDocument.getFileName()), vitaDocument.getData());
						return file;
					} catch (IOException e) {
						return null;
					}
				}
			});

			final Fragment fragment = new Fragment(navigationContainer.newChildId(), "fragment", this);
			fragment.setOutputMarkupId(true);
			final Label fileName = new Label("navItemLabel", vitaDocument.getFileName());

			fragment.add(downloadLink);
			downloadLink.add(fileName);
			navigationContainer.add(fragment);

			AjaxLink<Object> deleteLink = new AjaxLink<Object>("deleteLink") {
				/** The Constant serialVersionUID. */
				private static final long serialVersionUID = 1L;

				@Override
				public void onClick(AjaxRequestTarget target) {
					applicantModel.getDocuments().remove(vitaDocument);
					DatabaseProvider.getInstance().deleteDocument(vitaDocument);
					documents.remove(vitaDocument);
					setResponsePage(new ApplicantDetailPage(applicantModel, lastPage));
				}
			};
			fragment.add(deleteLink);
		}

		// file upload
		fileUploadForm = new MultiFileUploadForm("fileUploadForm") {
			/** The Constant serialVersionUID. */
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

						VITADocument document = new VITADocument();
						document.setApplicant(applicantModel);
						document.setCreatedBy(VIACSession.getInstance().getUserModel());
						document.setData(data);
						document.setFileName(upload.getClientFileName());
						document.setFileType(upload.getContentType());
						DatabaseProvider.getInstance().create(document);
						applicantModel.getDocuments().add(document);

						System.out.println("Datei \"" + upload.getClientFileName() + "\" gespeichert!");
					} catch (Exception e) {
						e.printStackTrace();
						throw new IllegalStateException("Unable to write file");
					}
				}
				setResponsePage(new ApplicantDetailPage(applicantModel, lastPage));
			}
		};

		Button uploadButton = new Button("uploadButton");

		// add all components to form
		fileUploadForm.add(uploadButton);
		documentsPanel.add(fileUploadForm);
		form.add(documentsPanel);

		form.add(commentLabel);

		form.add(firstnameLabel);
		form.add(firstnameField.setRequired(true));

		form.add(lastnameLabel);
		form.add(lastnameField.setRequired(true));

		form.add(genderLabel);
		form.add(genderChoice.setRequired(true));

		form.add(careerLabel);
		form.add(careerChoice.setRequired(true));

		form.add(statusLabel);
		form.add(statusChoice.setRequired(true));

		form.add(appliedOnLabel);
		form.add(appliedOnField.setRequired(true));

		form.add(birthdayLabel);
		form.add(birthdayField.setRequired(true));

		form.add(mailLabel);
		form.add(mailField.setRequired(true));

		form.add(phoneLabel);
		form.add(phoneField);

		form.add(streetLabel);
		form.add(streetField);

		form.add(cityLabel);
		form.add(cityField);

		form.add(postalCodeLabel);
		form.add(postalCodeField);

		form.add(eligoUsernameLabel);
		form.add(eligoUsernameField);

		form.add(eligoFirstnameLabel);
		form.add(eligoFirstnameField);

		form.add(eligoLastnameLabel);
		form.add(eligoLastnameField);

		form.add(eligoPasswordLabel);
		form.add(eligoPasswordField);

		// historic status
		List<IColumn<VITAHistoricStatus, String>> columns = new ArrayList<>();
		final SortableStatusHistoryDataProvider dataProviderStatus = new SortableStatusHistoryDataProvider(
				new Model<>(applicantModel));

		columns.add(new PropertyColumn<VITAHistoricStatus, String>(new Model<>("Status"), "name", "name"));
		columns.add(
				new PropertyColumn<VITAHistoricStatus, String>(new Model<>("Geändert am"), "changedOn", "changedOn"));

		tableWithFilterForm = new DataTable<VITAHistoricStatus, String>("tableWithFilterForm", columns,
				dataProviderStatus, 5);
		tableWithFilterForm.addTopToolbar(new HeadersToolbar<>(tableWithFilterForm, dataProviderStatus));
		tableWithFilterForm.addBottomToolbar(new NavigationToolbar(tableWithFilterForm));
		tableWithFilterForm.setOutputMarkupId(true);

		statusHistoryPanel.add(tableWithFilterForm);

		// back button
		backButton = new Link<String>("back") {
			/** The Constant serialVersionUID. */
			private static final long serialVersionUID = 1L;

			@Override
			public void onClick() {
				setResponsePage(lastPage);
			}
		};
		form.add(backButton);

		// edit button
		editButton = new Button("edit") {
			/** The Constant serialVersionUID. */
			private static final long serialVersionUID = -5538586370704721868L;

			@Override
			public void onSubmit() {
				careerChoice.setEnabled(true);
				commentArea.setEnabled(true);
				statusChoice.setEnabled(true);
				postalCodeField.setEnabled(true);
				cityField.setEnabled(true);
				streetField.setEnabled(true);
				phoneField.setEnabled(true);
				birthdayField.setEnabled(true);
				appliedOnField.setEnabled(true);
				genderChoice.setEnabled(true);
				mailField.setEnabled(true);
				lastnameField.setEnabled(true);
				firstnameField.setEnabled(true);
				fileUploadForm.setVisible(true);
				navigationContainer.setEnabled(true);

				saveButton.setEnabled(true);
				editButton.setEnabled(false);
			}
		};
		form.add(editButton);

		// save button
		saveButton = new AjaxButton("save") {
			/** The Constant serialVersionUID. */
			private static final long serialVersionUID = 1911995023405992639L;

			@Override
			protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
				super.onSubmit(target, form);
				careerChoice.setEnabled(false);
				commentArea.setEnabled(false);
				statusChoice.setEnabled(false);
				postalCodeField.setEnabled(false);
				cityField.setEnabled(false);
				streetField.setEnabled(false);
				phoneField.setEnabled(false);
				birthdayField.setEnabled(false);
				appliedOnField.setEnabled(false);
				genderChoice.setEnabled(false);
				mailField.setEnabled(false);
				lastnameField.setEnabled(false);
				firstnameField.setEnabled(false);
				fileUploadForm.setVisible(false);
				navigationContainer.setEnabled(false);

				// check if applicant is already existing
				if (isNewApplicant) {
					List<VITAApplicant> applicants = DatabaseProvider.getInstance().getAll(VITAApplicant.class);
					List<VITAApplicant> similarApplicants = new ArrayList<>();
					for (VITAApplicant applicant : applicants) {
						if (applicantModel.getFirstname().equals(applicant.getFirstname())
								&& applicantModel.getLastname().equals(applicant.getLastname())
								&& applicantModel.getBirthday().compareTo(applicant.getBirthday()) == 0) {
							similarApplicants.add(applicant);
						}
					}

					if (similarApplicants.isEmpty()) {
						createApplicant();
						VITAApplicantStatus newStatus = applicantModel.getStatus();
						if (tempStatus != newStatus) {
							createHistoricStatus(newStatus, applicantModel.getId());
						}
						applicantModel = DatabaseProvider.getInstance().get(VITAApplicant.class,
								applicantModel.getId());
						setResponsePage(new ApplicantDetailPage(applicantModel, lastPage));
					} else {
						confirmDialog.setContent(new ConfirmCreateDialogContent(confirmDialog.getContentId(),
								similarApplicants, ApplicantDetailPage.this) {
							/** The Constant serialVersionUID. */
							private static final long serialVersionUID = 1L;

							@Override
							public void onConfirm() {
								createApplicant();
								VITAApplicantStatus newStatus = applicantModel.getStatus();
								if (tempStatus != newStatus) {
									createHistoricStatus(newStatus, applicantModel.getId());
								}

								setResponsePage(ApplicantManagementPage.class);
							}

							@Override
							public void onCancel() {
								AjaxRequestTarget ajaxTarget = getRequestCycle().find(AjaxRequestTarget.class);
								confirmDialog.close(ajaxTarget);
							}
						});
						confirmDialog.show(target);
					}
				} else {
					createApplicant();
					VITAApplicantStatus newStatus = applicantModel.getStatus();
					if (tempStatus != newStatus) {
						createHistoricStatus(newStatus, applicantModel.getId());
					}
				}
				applicantModel = DatabaseProvider.getInstance().get(VITAApplicant.class, applicantModel.getId());
				target.add(tableWithFilterForm);
			}
		};
		form.add(saveButton);

		// reject button
		rejectButton = new AjaxButton("reject") {
			/** The Constant serialVersionUID. */
			private static final long serialVersionUID = 1911995023405992639L;

			@Override
			protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
				if (applicantModel.getStatus().getId() == status1.getId()) {
					createStatus(1);
				} else if (applicantModel.getStatus().getId() == status2.getId()) {
					createStatus(5);
				} else if (applicantModel.getStatus().getId() == status3.getId()) {
					createStatus(8);
				}
				VITAApplicantStatus newStatus = applicantModel.getStatus();
				if (tempStatus != newStatus) {
					createHistoricStatus(newStatus, applicantModel.getId());
				}
				applicantModel = DatabaseProvider.getInstance().get(VITAApplicant.class, applicantModel.getId());
				setResponsePage(new ApplicantDetailPage(applicantModel, lastPage));
			}
		};
		form.add(rejectButton);

		// accept button
		acceptButton = new AjaxButton("accept") {
			/** The Constant serialVersionUID. */
			private static final long serialVersionUID = 1911995023405992639L;

			@Override
			protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
				if (applicantModel.getStatus().getId() != status0.getId()) {
					createStatus(10);
				}
				VITAApplicantStatus newStatus = applicantModel.getStatus();
				if (tempStatus != newStatus) {
					createHistoricStatus(newStatus, applicantModel.getId());
				}
				applicantModel = DatabaseProvider.getInstance().get(VITAApplicant.class, applicantModel.getId());
				setResponsePage(new ApplicantDetailPage(applicantModel, lastPage));
			}
		};
		form.add(acceptButton);

		// is new applicant?
		if (!isNewApplicant) {
			careerChoice.setEnabled(false);
			commentArea.setEnabled(false);
			statusChoice.setEnabled(false);
			postalCodeField.setEnabled(false);
			cityField.setEnabled(false);
			streetField.setEnabled(false);
			phoneField.setEnabled(false);
			birthdayField.setEnabled(false);
			appliedOnField.setEnabled(false);
			genderChoice.setEnabled(false);
			mailField.setEnabled(false);
			lastnameField.setEnabled(false);
			firstnameField.setEnabled(false);
			eligoUsernameField.setEnabled(false);
			eligoPasswordField.setEnabled(false);
			fileUploadForm.setVisible(false);
			saveButton.setEnabled(false);
			eligoFirstnameField.setEnabled(false);
			eligoLastnameField.setEnabled(false);
			rejectButton.setEnabled(false);
			acceptButton.setEnabled(false);

			if (applicantModel.getStatus().getId() == status1.getId()
					|| applicantModel.getStatus().getId() == status2.getId()
					|| applicantModel.getStatus().getId() == status3.getId()) {
				rejectButton.setEnabled(true);
			}

			if (applicantModel.getStatus().getId() != status0.getId()) {
				acceptButton.setEnabled(true);
			}
		} else {
			editButton.setVisible(false);
			eligoUsernameField.setEnabled(false);
			eligoLastnameField.setEnabled(false);
			eligoFirstnameField.setEnabled(false);
			eligoPasswordField.setEnabled(false);
			acceptButton.setVisible(false);
			rejectButton.setVisible(false);
			statusHistoryPanel.setVisible(false);
			documentsPanel.setVisible(false);
		}
		add(form);

		// check authorizations for this page
		verifyPage();
	}

	/**
	 * Creates the applicant.
	 */
	private void createApplicant() {
		if (isNewApplicant) {
			applicantModel.setCreatedOn(new Date());
			applicantModel.setCreatedBy(VIACSession.getInstance().getUserModel());
			DatabaseProvider.getInstance().create(applicantModel);
		} else {
			DatabaseProvider.getInstance().merge(applicantModel);
		}
	}

	/**
	 * Creates the status.
	 *
	 * @param id
	 *            the id
	 */
	private void createStatus(int id) {
		// set changed values
		if (isNewApplicant) {
			applicantModel.setCreatedOn(new Date());
			applicantModel.setCreatedBy(VIACSession.getInstance().getUserModel());
		}

		applicantModel.setChangedOn(new Date());
		applicantModel.setChangedBy(VIACSession.getInstance().getUserModel());

		applicantModel.setStatus(DatabaseProvider.getInstance().getAll(VITAApplicantStatus.class).get(id));
		DatabaseProvider.getInstance().merge(applicantModel);
		setResponsePage(new ApplicantDetailPage(applicantModel, lastPage));
	}

	/**
	 * Sets the button visibility.
	 *
	 * @param shouldBeVisible
	 *            the new button visibility
	 */
	public void setButtonVisibility(Boolean shouldBeVisible) {
		saveButton.setVisible(shouldBeVisible);
		editButton.setVisible(shouldBeVisible);
		backButton.setVisible(shouldBeVisible);
		rejectButton.setVisible(shouldBeVisible);
		acceptButton.setVisible(shouldBeVisible);
		backButton.setVisible(shouldBeVisible);
	}

	/**
	 * Creates the historic status.
	 *
	 * @param statusName
	 *            the status name
	 * @param id
	 *            the id
	 */
	public void createHistoricStatus(VITAApplicantStatus statusName, long id) {
		VITAApplicantStatus newStatus = applicantModel.getStatus();
		if (tempStatus != newStatus) {
		}
		DatabaseProvider.getInstance().createHistoricStatus(statusName, id);
		setResponsePage(new ApplicantDetailPage(applicantModel, lastPage));
	}

	/**
	 * Disables/hides all components that should not be visible if user does not
	 * have the required authorizations.
	 */
	private void verifyPage() {
		authorizations = new ArrayList<>();
		authorizations = VIACSession.getInstance().getUserAuthorizations();

		// authorized to edit applicant?
		if (!authorizations.contains("vita_edit_applicant")) {
			editButton.setVisible(false);
			saveButton.setVisible(false);
		}
		// authorized to accept applicant?
		if (!authorizations.contains("vita_accept_applicant")) {
			acceptButton.setVisible(false);
		}
		// authorized to reject applicant?
		if (!authorizations.contains("vita_reject_applicant")) {
			rejectButton.setVisible(false);
		}
	}
}
