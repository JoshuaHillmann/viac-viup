package com.vanderlande.vita.employmenttest;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.time.DateUtils;
import org.apache.wicket.Page;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.OnChangeAjaxBehavior;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow.PageCreator;
import org.apache.wicket.extensions.markup.html.repeater.data.grid.ICellPopulator;
import org.apache.wicket.extensions.markup.html.repeater.data.table.AbstractColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.DataTable;
import org.apache.wicket.extensions.markup.html.repeater.data.table.HeadersToolbar;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.NavigationToolbar;
import org.apache.wicket.extensions.markup.html.repeater.data.table.PropertyColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.filter.FilterForm;
import org.apache.wicket.extensions.yui.calendar.DateTimeField;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.Radio;
import org.apache.wicket.markup.html.form.RadioGroup;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.link.DownloadLink;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.vanderlande.exceptions.MailNotSentException;
import com.vanderlande.exceptions.NotAllFieldsFilledException;
import com.vanderlande.util.DatabaseProvider;
import com.vanderlande.util.MailHandlerSMTP;
import com.vanderlande.viac.command.Command;
import com.vanderlande.viac.controls.VIACActionPanel;
import com.vanderlande.viac.controls.VIACButton;
import com.vanderlande.viac.dialog.ConfirmDialog;
import com.vanderlande.viac.dialog.NotificationDialog;
import com.vanderlande.viac.filter.FilterOption;
import com.vanderlande.viac.filter.VIACFilterPanel;
import com.vanderlande.viac.model.MailTemplate;
import com.vanderlande.viac.model.VIACUser;
import com.vanderlande.viac.session.VIACSession;
import com.vanderlande.viac.template.model.TemplateHelper;
import com.vanderlande.viac.template.model.TemplateVariable;
import com.vanderlande.viac.userManagement.password.PasswordHandler;
import com.vanderlande.vita.VITABasePage;
import com.vanderlande.vita.applicant.ApplicantDetailPage;
import com.vanderlande.vita.employmenttest.provider.OpenApplicantsProvider;
import com.vanderlande.vita.employmenttest.provider.TestApplicantsDataProvider;
import com.vanderlande.vita.model.VITAApplicant;
import com.vanderlande.vita.model.VITAApplicantStatus;
import com.vanderlande.vita.model.VITADocument;
import com.vanderlande.vita.model.VITAEligoTest;
import com.vanderlande.vita.model.VITATestStatus;

/**
 * The Class EmploymentTestDetailsPage.
 * 
 * @author desczu
 * 
 *         On this page the whole testmanagement is done. You can add/remove applicants from the test, delegate the
 *         testplanning-process to another person, manage a checklist for the test, automatically send test-invitations
 *         to the applicants and finish/cancel the test. The test is automatically saved when you move applicants but
 *         has to be saved explicitly when something else has changed. If the test date changed you can send a
 *         "date-change-email" to all invited applicants.
 * 
 */
public class EmploymentTestDetailsPage extends VITABasePage
{
    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 3914790343598288403L;

    /** The test group. */
    private VITAEligoTest testGroup;

    /** The tables. */
    private DataTable<VITAApplicant, String> allOpenApplicantsTable, testApplicantsTable;

    /** The delegates. */
    private RadioGroup<VIACUser> delegates;

    /** The delegate. */
    private VIACUser delegate;

    /** The buttons. */
    private AjaxButton sendEmailButton, eligoManagementButton, finishTestButton, delegateTest;

    /**
     * The test applicants data provider. Used to provide all applicants which are currently in the test
     */
    private TestApplicantsDataProvider testApplicantsDataProvider;

    /**
     * The open applicants data provider. Used to provide all possible applicants for the test (status =
     * BEWERBUNG_EINGEGANGEN)
     */
    private OpenApplicantsProvider openApplicantsDataProvider;

    /** The authorizations. */
    List<String> authorizations;

    /** The buttons. */
    List<VIACButton> buttonsNextStep;

    List<VIACButton> buttonsTest;

    List<VIACButton> buttonsPossible;

    /** The panels. */
    VIACActionPanel actionPanelNextStep;

    VIACActionPanel actionPanelTest;

    VIACActionPanel actionPanelPossible;

    /** The form. */
    private Form<VITAEligoTest> form;

    /** The detail dialog. Shows the applicant-details */
    private ModalWindow detailDialog;

    /**
     * The notification dialog. Shows some warnings/errors/notifications that do not require confirmation (e.g. "Mail
     * could not be send")
     */
    private NotificationDialog notificationDialog;

    /** The certificate check. */
    private CheckBox certificateCheck;

    /** The login information check. */
    private CheckBox loginInformationCheck;

    /** The email check. */
    private CheckBox emailCheck;

    /** The catering check. */
    private CheckBox cateringCheck;

    /** The comment area. */
    private TextArea<String> commentArea;

    /** The test date. */
    private DateTimeField testDate;

    /** The test room. */
    private TextField<String> testRoom;

    /** The test name. */
    private TextField<String> testName;

    /** The possible delegates. */
    private ListView<VIACUser> possibleDelegates;

    /** The save test. */
    private AjaxButton saveTestButton;

    /** Test Participation Document download. */
    private DownloadLink loadTestParticipationButton;

    /** The cancel test. */
    private AjaxButton cancelTestButton;

    /** The authorizations. */
    private List<String> roles;

    /** The delegates panel. */
    private WebMarkupContainer delegatesPanel;

    /** The test is saved. */
    private boolean testIsSaved = true;

    /** The confirm dialog. */
    private ConfirmDialog confirmDialog;

    /** The application received. */
    private VITAApplicantStatus APPLICATION_RECEIVED;

    /** The invited to test. */
    private VITAApplicantStatus INVITED_TO_TEST;

    /** The test done. */
    private VITAApplicantStatus TEST_DONE;

    /** The not appeared. */
    private VITAApplicantStatus NOT_APPEARED;

    /** The test canceled. */
    private VITATestStatus TEST_CANCELED;

    /** The test finished. */
    private VITATestStatus TEST_FINISHED;

    /**
     * The Constructor.
     *
     * @param testGroup
     *            the test group
     */
    public EmploymentTestDetailsPage(VITAEligoTest testGroup)
    {
        this.testGroup = testGroup;
        List<VITAApplicantStatus> allApplicantStatus = DatabaseProvider.getInstance().getAll(VITAApplicantStatus.class);
        APPLICATION_RECEIVED = allApplicantStatus.get(0);
        INVITED_TO_TEST = allApplicantStatus.get(2);
        TEST_DONE = allApplicantStatus.get(3);
        NOT_APPEARED = allApplicantStatus.get(4);
        List<VITATestStatus> allTestStatus = DatabaseProvider.getInstance().getAll(VITATestStatus.class);
        TEST_CANCELED = allTestStatus.get(2);
        TEST_FINISHED = allTestStatus.get(3);
        init();
    }

    /**
     * Instantiates a new employment test details page.
     */
    private void init()
    {
        buttonsNextStep = new ArrayList<>();
        buttonsTest = new ArrayList<>();
        buttonsPossible = new ArrayList<>();

        // filterable columns for the open applicants table
        List<FilterOption> filterProperties = new ArrayList<>();
        filterProperties.add(new FilterOption("Vorname", "firstname"));
        filterProperties.add(new FilterOption("Nachname", "lastname"));
        filterProperties.add(new FilterOption("Karriere", "career"));

        // Dialogs
        detailDialog = new ModalWindow("modalDetailDialog");
        detailDialog.setInitialHeight(550);
        detailDialog.setInitialWidth(800);
        add(detailDialog);

        notificationDialog = new NotificationDialog("notificationDialog");
        add(notificationDialog);

        confirmDialog = new ConfirmDialog("confirmDialog");
        add(confirmDialog);

        // Dataprovider
        testApplicantsDataProvider = new TestApplicantsDataProvider(testGroup);
        openApplicantsDataProvider = new OpenApplicantsProvider(testGroup, filterProperties);

        // planning-panel
        testName = new TextField<String>("testName", new PropertyModel<String>(testGroup, "name"));
        testDate = new DateTimeField("testDate", new PropertyModel<Date>(testGroup, "date"));
        testDate.add(new OnChangeAjaxBehavior()
        {
            /** The Constant serialVersionUID. */
            private static final long serialVersionUID = -2990601505793237263L;

            @Override
            protected void onUpdate(AjaxRequestTarget target)
            {
                toggleButtons(false, target);
                testIsSaved = false;
            }
        });
        testRoom = new TextField<String>("testRoom", new PropertyModel<String>(testGroup, "room"));
        testRoom.add(new OnChangeAjaxBehavior()
        {
            /** The Constant serialVersionUID. */
            private static final long serialVersionUID = -2990601505793237263L;

            @Override
            protected void onUpdate(AjaxRequestTarget target)
            {
                toggleButtons(false, target);
                testIsSaved = false;
            }
        });

        // checklist-panel
        certificateCheck =
            new CheckBox("certificateCheck", new PropertyModel<Boolean>(testGroup, "isCertificatePrinted"));
        loginInformationCheck =
            new CheckBox("loginInformationCheck", new PropertyModel<Boolean>(testGroup, "isLoginInformationPrinted"));
        emailCheck = new CheckBox("emailCheck", new PropertyModel<Boolean>(testGroup, "isEmailSent"));
        cateringCheck = new CheckBox("cateringCheck", new PropertyModel<Boolean>(testGroup, "isCateringOrganized"));
        commentArea = new TextArea<String>("commentArea", new PropertyModel<String>(testGroup, "comment"));

        // delegate-panel
        delegates = new RadioGroup<VIACUser>("group", Model.of(delegate));
        possibleDelegates =
            new ListView<VIACUser>("possibleDelegates", DatabaseProvider.getInstance().getAllPossibleTestDelegates())
            {
                /** The Constant serialVersionUID. */
                private static final long serialVersionUID = 8302133829188691631L;

                protected void populateItem(ListItem<VIACUser> item)
                {
                    String delegateName =
                        item.getModel().getObject().getFirstname() + " " + item.getModel().getObject().getLastname();
                    item.add(new Radio<VIACUser>("radio", item.getModel()));
                    item.add(new Label("delegateName", delegateName));
                }
            };

        possibleDelegates.setReuseItems(true); // wicket wants it, don't ask
        delegates.add(possibleDelegates);

        // delegate test button
        delegateTest = new AjaxButton("delegateTest")
        {
            /** The Constant serialVersionUID. */
            private static final long serialVersionUID = 1293150690768889749L;

            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form)
            {
                // get the planning-assistant from the radiogroup in
                // "planning-panel" and - if not null - send
                // an email to this person
                VIACUser delegate = delegates.getModelObject();
                if (delegate == null)
                {
                    notificationDialog.showMessage("Kein Planungsassistent ausgewählt!", target);
                }
                else
                {
                    try
                    {
                        sendDelegateEmail(delegate);
                        notificationDialog.showMessage("Email wurde erfolgreich versendet.", target);
                    }
                    catch (NotAllFieldsFilledException e)
                    {
                        notificationDialog.showMessage(e.getMessage(), target);
                    }
                    catch (MailNotSentException e)
                    {
                        notificationDialog.showMessage(e.getMessage(), target);
                    }
                }
            }
        };
        delegateTest.setOutputMarkupId(true);
        if (DatabaseProvider.getInstance().getAllPossibleTestDelegates().size() == 0)
        {
            delegateTest.setEnabled(false);
        }
        delegatesPanel = new WebMarkupContainer("delegatesPanel");
        delegatesPanel.add(delegateTest);
        delegatesPanel.add(delegates);

        // Action-panel

        // finisch test button
        finishTestButton = new AjaxButton("finishTest")
        {
            /** The Constant serialVersionUID. */
            private static final long serialVersionUID = -7182811059480921058L;

            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form)
            {
                finishTest(target);
            }
        };

        // save test button
        saveTestButton = new AjaxButton("saveTest")
        {
            /** The Constant serialVersionUID. */
            private static final long serialVersionUID = 8399494162199087520L;

            @Override
            public void onSubmit(AjaxRequestTarget target, Form<?> form)
            {
                saveTest(target);
                target.add(EmploymentTestDetailsPage.this);
            }
        };

        // load test participation button
        loadTestParticipationButton = new DownloadLink("getTestParticipation", new LoadableDetachableModel<File>()
        {
            /** The Constant serialVersionUID. */
            private static final long serialVersionUID = 1L;

            @Override
            protected File load()
            {
                return generateParticipationDocument();

            }
        });
        loadTestParticipationButton.setOutputMarkupId(true);
        if (testGroup.getDate() == null || testGroup.getTestApplicants().size() == 0)
        {
            loadTestParticipationButton.setEnabled(false);
        }

        // send email button
        sendEmailButton = new AjaxButton("sendEmail")
        {
            /** The Constant serialVersionUID. */
            private static final long serialVersionUID = -1271547733621153725L;

            @Override
            public void onSubmit(AjaxRequestTarget target, Form<?> form)
            {
                // send an email to each applicant
                // 1) check if the applicant is already invited
                // (yes): send a date-change-email (only if test-date really
                // changed)
                // (no): send a regular invitation-mail
                // 2) show a notification if the process was successful or not
                try
                {
                    for (VITAApplicant applicant : testGroup.getTestApplicants())
                    {
                        if (applicant.getStatus().equals(APPLICATION_RECEIVED))
                        {
                            sendInvitationEmail(applicant);
                        }
                        else if (applicant.getStatus().equals(INVITED_TO_TEST))
                        {
                            if (!applicant.getInvitationDate().equals(testGroup.getDate()))
                            {
                                sendTestDateChangedEmail(applicant);
                            }
                        }
                        target.add(allOpenApplicantsTable);
                        target.add(testApplicantsTable);
                    }
                    notificationDialog.showMessage("Emails wurden erfolgreich versendet!", target);
                }
                catch (NotAllFieldsFilledException e)
                {
                    notificationDialog.showMessage("Es sind nicht alle erforderlichen Felder ausgefüllt!", target);
                }
                catch (MailNotSentException e)
                {

                    notificationDialog.showMessage(
                        "Email konnte nicht gesendet werden.",
                        EmploymentTestDetailsPage.this.getRequestCycle().find(AjaxRequestTarget.class));
                }
            }
        };

        // eligo management button
        eligoManagementButton = new AjaxButton("eligoLink")
        {
            /** The Constant serialVersionUID. */
            private static final long serialVersionUID = 5284546350535946633L;

            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form)
            {
                setResponsePage(
                    new EmploymentTestEligoManagementPage(
                        testGroup,
                        PasswordHandler.getInstance().getRandomPassword(10)));
            }
        };

        // cancel test button
        cancelTestButton = new AjaxButton("cancelTest")
        {
            /** The Constant serialVersionUID. */
            private static final long serialVersionUID = 4524583698291986424L;

            @Override
            public void onSubmit(AjaxRequestTarget target, Form<?> form)
            {
                cancelTest(target);
            }

        };

        finishTestButton.setOutputMarkupId(true);
        eligoManagementButton.setOutputMarkupId(true);
        sendEmailButton.setOutputMarkupId(true);
        cancelTestButton.setOutputMarkupId(true);

        if (testGroup.getTestApplicants().size() == 0)
        {
            finishTestButton.setEnabled(false);
            eligoManagementButton.setEnabled(false);
            sendEmailButton.setEnabled(false);
            cancelTestButton.setEnabled(false);
        }

        // The table with all the applicants in the test
        List<IColumn<VITAApplicant, String>> testApplicantsColumns = new ArrayList<>();
        testApplicantsColumns
            .add(new PropertyColumn<VITAApplicant, String>(new Model<>("Vorname"), "firstname", "firstname"));
        testApplicantsColumns
            .add(new PropertyColumn<VITAApplicant, String>(new Model<>("Nachname"), "lastname", "lastname"));
        testApplicantsColumns
            .add(new PropertyColumn<VITAApplicant, String>(new Model<>("Beworben am"), "appliedOn", "appliedOn"));
        testApplicantsColumns
            .add(new PropertyColumn<VITAApplicant, String>(new Model<>("Karriere"), "career.name", "career.name"));
        testApplicantsColumns
            .add(new PropertyColumn<VITAApplicant, String>(new Model<>("Status"), "status.name", "status.name"));
        testApplicantsColumns.add(new AbstractColumn<VITAApplicant, String>(new Model<>("Nächster Schritt"))
        {
            /** The Constant serialVersionUID. */
            private static final long serialVersionUID = -5896586296298760442L;

            @Override
            public void populateItem(Item<ICellPopulator<VITAApplicant>> cellItem, String componentId,
                                     final IModel<VITAApplicant> rowModel)
            {
                final VITAApplicant applicant = rowModel.getObject();

                String text = "";

                // status == Bewerbung eingegangen
                if (applicant.getStatus().equals(APPLICATION_RECEIVED))
                {
                    text = "Einladung senden";
                }
                // status == Zum Online-Test eingeladen
                else if (applicant.getStatus().equals(INVITED_TO_TEST))
                {
                    text = "Nicht erschienen";
                }
                // status == Nicht zum Online-Test erschienen
                else if (applicant.getStatus().equals(NOT_APPEARED))
                {
                    text = "Doch erschienen";
                }
                // this button changes it's label and behavior depending on the
                // applicant's status
                final VIACButton nextStepButton = new VIACButton(text)
                {
                    /** The Constant serialVersionUID. */
                    private static final long serialVersionUID = 823568622538687932L;

                    public void onClick(final AjaxRequestTarget target)
                    {
                        VITAApplicantStatus status = null;
                        // status == Bewerbung eingegangen (label = Einladung
                        // senden)
                        // 1) check if the test is saved
                        // 2) try to send invitation-email
                        // 3) show notification
                        if (applicant.getStatus().equals(APPLICATION_RECEIVED))
                        {
                            // 1)
                            if (testIsSaved)
                            {
                                // 2)
                                try
                                {
                                    sendInvitationEmail(applicant);
                                    notificationDialog.showMessage(
                                        "Email wurde erfolgreich versendet",
                                        target,
                                        EmploymentTestDetailsPage.this);
                                }
                                // 3)
                                catch (NotAllFieldsFilledException e)
                                {
                                    notificationDialog
                                        .showMessage("Es sind nicht alle erforderlichen Felder ausgefüllt!", target);
                                }
                                catch (MailNotSentException e)
                                {
                                    notificationDialog.showMessage(
                                        "Email konnte nicht gesendet werden.",
                                        EmploymentTestDetailsPage.this.getRequestCycle().find(AjaxRequestTarget.class));
                                }
                            }
                            else
                            {
                                notificationDialog.showMessage("Der Test muss zuerst gespeichert werden!", target);
                            }
                        }
                        // status == zum Online-Test eingeladen (label = nicht
                        // erschienen)
                        // 1) set applicant status to "Nicht zum Online-Test
                        // erschienen"
                        else if (applicant.getStatus().equals(INVITED_TO_TEST))
                        {
                            status = NOT_APPEARED;
                            applicant.setStatus(status);
                            DatabaseProvider.getInstance().createHistoricStatus(status, applicant.getId());
                            DatabaseProvider.getInstance().merge(applicant);
                            target.add(EmploymentTestDetailsPage.this);

                        }
                        // status == Nicht zum Online-Test erschienen (label =
                        // Doch erschienen)
                        // 1) set applicant status to "Zum Online-Test
                        // eingeladen"
                        else if (applicant.getStatus().equals(NOT_APPEARED))
                        {
                            status = INVITED_TO_TEST;
                            applicant.setStatus(status);
                            DatabaseProvider.getInstance().createHistoricStatus(status, applicant.getId());
                            DatabaseProvider.getInstance().merge(applicant);
                            target.add(EmploymentTestDetailsPage.this);
                        }
                    }
                };

                actionPanelNextStep = new VIACActionPanel(componentId, nextStepButton);
                cellItem.add(actionPanelNextStep);
                buttonsNextStep = actionPanelNextStep.getPanelButtons();

                // check authorizations for this page
                verifyPage();
            }
        });
        testApplicantsColumns.add(new AbstractColumn<VITAApplicant, String>(new Model<>("Aktionen"))
        {
            /** The Constant serialVersionUID. */
            private static final long serialVersionUID = -4494250855730234006L;

            @Override
            public void populateItem(Item<ICellPopulator<VITAApplicant>> cellItem, String componentId,
                                     final IModel<VITAApplicant> rowModel)
            {
                final VITAApplicant applicant = rowModel.getObject();
                VIACButton removeButton = new VIACButton(VIACButton.Type.REMOVE_FROM)
                {
                    /** The Constant serialVersionUID. */
                    private static final long serialVersionUID = 5017375031920912851L;

                    @Override
                    public void onClick(AjaxRequestTarget target)
                    {
                        // check if the applicant is already invited and show a
                        // warning if the
                        // user tries to remove him from the test
                        if (applicant.getStatus().equals(INVITED_TO_TEST) || applicant.getStatus().equals(NOT_APPEARED))
                        {
                            confirmDialog.showMessage(
                                applicant.getFirstname() + " " + applicant.getLastname() +
                                                      " wurde bereits eingeladen. Soll er trotzdem entfernt werden (der Status wird zurück auf BEWERBUNG_EINGEGANGEN gesetzt)",
                                new Command()
                                {
                                    /** The Constant serialVersionUID. */
                                    private static final long serialVersionUID = 6665624321190471221L;

                                    @Override
                                    public void execute()
                                    {
                                        AjaxRequestTarget ajaxTarget = getRequestCycle().find(AjaxRequestTarget.class);
                                        testApplicantsDataProvider.removeApplicant(applicant);
                                        testGroup.getTestApplicants().remove(applicant);
                                        openApplicantsDataProvider.addApplicant(applicant);
                                        ajaxTarget.add(allOpenApplicantsTable);
                                        ajaxTarget.add(testApplicantsTable);
                                        saveTest(ajaxTarget);
                                        confirmDialog.close(ajaxTarget);
                                    };
                                },
                                new Command()
                                {
                                    /** The Constant serialVersionUID. */
                                    private static final long serialVersionUID = 6191986112813096694L;

                                    @Override
                                    public void execute()
                                    {
                                        AjaxRequestTarget ajaxTarget = getRequestCycle().find(AjaxRequestTarget.class);
                                        confirmDialog.close(ajaxTarget);
                                    }
                                },
                                target);

                        }
                        // if the applicant is not invited just remove him from
                        // the test (no warning)
                        else
                        {
                            testApplicantsDataProvider.removeApplicant(applicant);
                            testGroup.getTestApplicants().remove(applicant);
                            openApplicantsDataProvider.addApplicant(applicant);
                            target.add(allOpenApplicantsTable);
                            target.add(testApplicantsTable);
                            saveTest(target);
                        }
                    }
                };

                // detail button
                VIACButton detailButton = new VIACButton(VIACButton.Type.DETAILS)
                {
                    /** The Constant serialVersionUID. */
                    private static final long serialVersionUID = -7111736372802126447L;

                    @Override
                    public void onClick(AjaxRequestTarget target)
                    {
                        // shows the detailpage of the applicant in a modal
                        // window
                        detailDialog.setPageCreator(new PageCreator()
                        {
                            /** The Constant serialVersionUID. */
                            private static final long serialVersionUID = -1398240448852307270L;

                            @Override
                            public Page createPage()
                            {
                                ApplicantDetailPage page =
                                    new ApplicantDetailPage(rowModel.getObject(), EmploymentTestDetailsPage.this);
                                page.setHeaderVisibility(false);
                                page.setButtonVisibility(false);
                                return page;
                            }
                        });
                        detailDialog.show(target);
                    }
                };

                // edit button
                VIACButton editButton = new VIACButton(VIACButton.Type.EDIT)
                {
                    /** The Constant serialVersionUID. */
                    private static final long serialVersionUID = 2769555147047481360L;

                    @Override
                    public void onClick(AjaxRequestTarget target)
                    {
                        // redirect to the ApplicantDetailPage (you can edit the
                        // applicant there)
                        setResponsePage(new ApplicantDetailPage(rowModel.getObject(), EmploymentTestDetailsPage.this));
                    }
                };

                actionPanelTest = new VIACActionPanel(componentId, removeButton, detailButton, editButton);
                cellItem.add(actionPanelTest);
                buttonsTest = actionPanelTest.getPanelButtons();

                // check authorizations for this page
                verifyPage();
            }

        });

        // applicants table
        testApplicantsTable = new DataTable<VITAApplicant, String>(
            "testApplicantsTable",
            testApplicantsColumns,
            testApplicantsDataProvider,
            5);

        testApplicantsTable.setOutputMarkupId(true);
        testApplicantsTable.addTopToolbar(new HeadersToolbar<>(testApplicantsTable, testApplicantsDataProvider));
        testApplicantsTable.addBottomToolbar(new NavigationToolbar(testApplicantsTable));

        // the table with the open applicants (contains every applicant with the
        // status "Bewerbung eingegangen")
        List<IColumn<VITAApplicant, String>> openApplicantsColumns = new ArrayList<>();
        openApplicantsColumns
            .add(new PropertyColumn<VITAApplicant, String>(new Model<>("Vorname"), "firstname", "firstname"));
        openApplicantsColumns
            .add(new PropertyColumn<VITAApplicant, String>(new Model<>("Nachname"), "lastname", "lastname"));
        openApplicantsColumns
            .add(new PropertyColumn<VITAApplicant, String>(new Model<>("Beworben am"), "appliedOn", "appliedOn"));
        openApplicantsColumns
            .add(new PropertyColumn<VITAApplicant, String>(new Model<>("Karriere"), "career.name", "career.name"));
        openApplicantsColumns.add(new AbstractColumn<VITAApplicant, String>(new Model<>("Aktionen"))
        {
            /** The Constant serialVersionUID. */
            private static final long serialVersionUID = -6300780401172889040L;

            @Override
            public void populateItem(Item<ICellPopulator<VITAApplicant>> cellItem, String componentId,
                                     final IModel<VITAApplicant> rowModel)
            {
                VIACButton addButton = new VIACButton(VIACButton.Type.ADD)
                {
                    /** The Constant serialVersionUID. */
                    private static final long serialVersionUID = -5916769880335090893L;

                    @Override
                    public void onClick(AjaxRequestTarget target)
                    {
                        // add the applicant to the test-applicants-table
                        testApplicantsDataProvider.addApplicant(rowModel.getObject());
                        testGroup.getTestApplicants().add(rowModel.getObject());
                        openApplicantsDataProvider.removeApplicant(rowModel.getObject());
                        target.add(testApplicantsTable);
                        target.add(allOpenApplicantsTable);
                        target.add(eligoManagementButton);
                        target.add(sendEmailButton);
                        target.add(finishTestButton);
                        target.add(delegateTest);
                        saveTest(target);
                    }
                };

                // detail button
                VIACButton detailButton = new VIACButton(VIACButton.Type.DETAILS)
                {
                    /** The Constant serialVersionUID. */
                    private static final long serialVersionUID = 5851323089911498321L;

                    @Override
                    public void onClick(AjaxRequestTarget target)
                    {
                        // shows the applicant-details in a modal-window
                        detailDialog.setPageCreator(new PageCreator()
                        {
                            /** The Constant serialVersionUID. */
                            private static final long serialVersionUID = 4595478746614148967L;

                            @Override
                            public Page createPage()
                            {
                                ApplicantDetailPage page =
                                    new ApplicantDetailPage(rowModel.getObject(), EmploymentTestDetailsPage.this);
                                page.setHeaderVisibility(false);
                                page.setButtonVisibility(false);
                                return page;
                            }
                        });
                        detailDialog.show(target);
                    }
                };

                // edit button
                VIACButton editButton = new VIACButton(VIACButton.Type.EDIT)
                {
                    /** The Constant serialVersionUID. */
                    private static final long serialVersionUID = 1967685011886189975L;

                    @Override
                    public void onClick(AjaxRequestTarget target)
                    {
                        setResponsePage(new ApplicantDetailPage(rowModel.getObject(), EmploymentTestDetailsPage.this));
                    }
                };

                actionPanelPossible = new VIACActionPanel(componentId, addButton, detailButton, editButton);
                cellItem.add(actionPanelPossible);
                buttonsTest = actionPanelPossible.getPanelButtons();

                // check authorizations for this page
                verifyPage();
            }
        });

        // open applicants table
        allOpenApplicantsTable = new DataTable<VITAApplicant, String>(
            "allOpenApplicantsTable",
            openApplicantsColumns,
            openApplicantsDataProvider,
            5);
        allOpenApplicantsTable.setOutputMarkupId(true);
        allOpenApplicantsTable.addTopToolbar(new HeadersToolbar<>(allOpenApplicantsTable, openApplicantsDataProvider));
        allOpenApplicantsTable.addBottomToolbar(new NavigationToolbar(allOpenApplicantsTable));

        // the filter for the openApplicantsTable
        FilterForm<VITAApplicant> filterForm = new FilterForm<VITAApplicant>("filterForm", openApplicantsDataProvider);
        VIACFilterPanel<VITAApplicant> filterPanel =
            new VIACFilterPanel<VITAApplicant>("filterPanel", openApplicantsDataProvider, allOpenApplicantsTable);
        filterForm.add(filterPanel);
        filterForm.add(allOpenApplicantsTable);

        testApplicantsTable.setOutputMarkupId(true);

        // add all the components!
        testRoom.setRequired(true);

        form = new Form<VITAEligoTest>("form");
        form.add(certificateCheck);
        form.add(loginInformationCheck);
        form.add(emailCheck);
        form.add(cateringCheck);
        form.add(commentArea);
        form.add(delegatesPanel);
        form.add(delegatesPanel);
        form.add(testDate);
        form.add(testRoom);
        form.add(eligoManagementButton);
        form.add(sendEmailButton);
        form.add(finishTestButton);
        form.add(testName);
        form.add(testApplicantsTable);
        form.add(saveTestButton);
        form.add(loadTestParticipationButton);
        form.add(filterForm);
        form.add(cancelTestButton);

        add(form);
    }

    /**
     * Send test date changed email.
     *
     * @param applicant
     *            the applicant
     * @throws NotAllFieldsFilledException
     *             the not all fields filled exception
     * @throws MailNotSentException
     *             the mail not sent exception
     */
    private void sendTestDateChangedEmail(VITAApplicant applicant)
        throws NotAllFieldsFilledException, MailNotSentException
    {
        if (testGroup.getDate() != null && testGroup.getRoom() != null)
        {
            try
            {

                MailTemplate textTemplate =
                    TemplateHelper.getInstance().getTemplate("mail_template_test_invitation_change");
                String mail_text = textTemplate.getMailText();
                ArrayList<TemplateVariable> al = new ArrayList<>();
                al.add(new TemplateVariable("#test_date", "", testGroup.getDate().toLocaleString()));
                al.add(new TemplateVariable("#test_room", "", testGroup.getRoom()));
                mail_text = TemplateHelper.getInstance().useTemplate(mail_text, applicant, al);
                MailHandlerSMTP.getInstance().sendMail(
                    applicant.getMail(),
                    TemplateHelper.getInstance().getTemplate("mail_template_test_invitation_change_header"),
                    textTemplate,
                    mail_text);
                applicant.setInvitationDate(testGroup.getDate());
                DatabaseProvider.getInstance().merge(applicant);
            }
            catch (Exception e)
            {

                throw new MailNotSentException();
            }
        }
        else
        {
            throw new NotAllFieldsFilledException("Nicht alle Felder ausgefüllt!");
        }
    }

    /**
     * Toggle buttons.
     *
     * @param shouldBeEnabled
     *            the should be enabled
     * @param target
     *            the target
     */
    private void toggleButtons(boolean shouldBeEnabled, AjaxRequestTarget target)
    {
        if (testGroup.getTestApplicants().size() == 0)
        {
            finishTestButton.setEnabled(false);
            eligoManagementButton.setEnabled(false);
            sendEmailButton.setEnabled(false);
            cancelTestButton.setEnabled(false);
        }
        else
        {
            finishTestButton.setEnabled(shouldBeEnabled);
            eligoManagementButton.setEnabled(shouldBeEnabled);
            sendEmailButton.setEnabled(shouldBeEnabled);
            cancelTestButton.setEnabled(shouldBeEnabled);
        }
        if (DatabaseProvider.getInstance().getAllPossibleTestDelegates().size() == 0)
        {
            delegateTest.setEnabled(false);
        }
        else
        {
            delegateTest.setEnabled(shouldBeEnabled);
        }
        if (testGroup.getDate() == null || testGroup.getTestApplicants().size() == 0)
        {
            loadTestParticipationButton.setEnabled(false);
        }
        else
        {
            loadTestParticipationButton.setEnabled(shouldBeEnabled);
        }
        target.add(eligoManagementButton);
        target.add(sendEmailButton);
        target.add(finishTestButton);
        target.add(delegateTest);
        target.add(cancelTestButton);
        target.add(loadTestParticipationButton);
    }

    /**
     * Send email.
     *
     * @param applicant
     *            the applicant
     * @throws NotAllFieldsFilledException
     *             the not all fields filled exception
     * @throws MailNotSentException
     *             the mail not sent exception
     */
    private void sendInvitationEmail(VITAApplicant applicant)
        throws NotAllFieldsFilledException, MailNotSentException
    {
        if (testGroup.getDate() != null && testGroup.getRoom() != null)
        {
            try
            {
                MailTemplate mailTextTemplate =
                    TemplateHelper.getInstance().getTemplate("mail_template_test_invitation");
                String mail_text = mailTextTemplate.getMailText();

                ArrayList<TemplateVariable> al = new ArrayList<>();
                al.add(new TemplateVariable("#test_date", "", testGroup.getDate().toLocaleString()));
                al.add(new TemplateVariable("#test_room", "", testGroup.getRoom()));
                mail_text = TemplateHelper.getInstance().useTemplate(mail_text, applicant, al);

                MailTemplate mailHeaderTemplate =
                    TemplateHelper.getInstance().getTemplate("mail_template_test_invitation_header");

                MailHandlerSMTP
                    .getInstance()
                    .sendMail(applicant.getMail(), mailHeaderTemplate, mailTextTemplate, mail_text);

                VITAApplicantStatus status = INVITED_TO_TEST;
                applicant.setStatus(status);
                DatabaseProvider.getInstance().createHistoricStatus(status, applicant.getId());
                applicant.setInvitationDate(testGroup.getDate());
                DatabaseProvider.getInstance().merge(applicant);
            }
            catch (Exception e)
            {
                e.printStackTrace();
                throw new MailNotSentException();
            }
        }
        else
        {
            throw new NotAllFieldsFilledException("Nicht alle Felder ausgefüllt!");
        }
    }

    /**
     * Send cancel mail.
     *
     * @param applicant
     *            the applicant
     * @throws MailNotSentException
     *             the mail not sent exception
     */
    private void sendCancelMail(VITAApplicant applicant)
        throws MailNotSentException
    {
        try
        {
            MailTemplate textTemplate = TemplateHelper.getInstance().getTemplate("mail_template_cancel_test");
            String mail_text = textTemplate.getMailText();
            ArrayList<TemplateVariable> al = new ArrayList<>();
            al.add(new TemplateVariable("#test_date", "", testGroup.getDate().toLocaleString()));
            al.add(new TemplateVariable("#test_room", "", testGroup.getRoom()));
            mail_text = TemplateHelper.getInstance().useTemplate(mail_text, applicant, al);
            MailHandlerSMTP.getInstance().sendMail(
                applicant.getMail(),
                TemplateHelper.getInstance().getTemplate("mail_template_cancel_test_header"),
                textTemplate,
                mail_text);
            VITAApplicantStatus status = APPLICATION_RECEIVED;
            applicant.setStatus(status);
            DatabaseProvider.getInstance().createHistoricStatus(status, applicant.getId());
            applicant.setInvitationDate(null);
            DatabaseProvider.getInstance().merge(applicant);
        }
        catch (Exception e)
        {
            throw new MailNotSentException();
        }
    }

    /**
     * Send delegate email.
     *
     * @param delegate
     *            the delegate
     * @throws NotAllFieldsFilledException
     *             the not all fields filled exception
     * @throws MailNotSentException
     *             the mail not sent exception
     */
    private void sendDelegateEmail(VIACUser delegate)
        throws NotAllFieldsFilledException, MailNotSentException
    {
        if (testGroup.getName() != null)
        {
            try
            {
                MailTemplate textTemplate = TemplateHelper.getInstance().getTemplate("mail_template_deligate_test");
                String mail_text = textTemplate.getMailText();
                ArrayList<TemplateVariable> al = new ArrayList<>();
                al.add(new TemplateVariable("#test_group", "", testGroup.getName()));
                mail_text = TemplateHelper.getInstance().useTemplate(mail_text, delegate, al);
                MailHandlerSMTP.getInstance().sendMail(
                    delegate.getName(),
                    TemplateHelper.getInstance().getTemplate("mail_template_deligate_test_header"),
                    textTemplate,
                    mail_text);
            }
            catch (Exception e)
            {
                throw new MailNotSentException();
            }
        }
        else
        {
            throw new NotAllFieldsFilledException("Nicht alle Felder ausgefüllt!");
        }
    }

    /**
     * Finishes the test. Checks if the testdate is already over (you cannot finish a future test) and if all
     * eligo-test-results are uploaded. If not, a notification window shows up. Otherwise the test-status is set to
     * "TEST ABGESCHLOSSEN" and every applicant with the status "Zum Online-Test eingeladen" gets the new status
     * "Online-Test gemacht". The user is then redirected to the Test-Overview Page.
     *
     * @param target
     *            the target
     */
    private void finishTest(AjaxRequestTarget target)
    {
        if (testGroup.getDate() == null)
        {
            notificationDialog.showMessage("Kein Testdatum angelegt.", target);
        }
        else if (testGroup.getDate().after(new Date()))
        {
            notificationDialog.showMessage(
                "Das Testdatum liegt in der Zukunft. Test kann erst abgeschlossen werden, wenn das Testdatum abgelaufen ist",
                target);
        }
        else
        {
            boolean allEligoDocumentsUploaded = true;
            for (VITAApplicant applicant : testGroup.getTestApplicants())
            {
                if (applicant.getStatus().equals(INVITED_TO_TEST) || applicant.getStatus().equals(TEST_DONE))
                    if (allEligoDocumentsUploaded)
                    {
                        boolean applicantHasEligoDocument = false;
                        for (VITADocument document : applicant.getDocuments())
                        {
                            if (document.isEligo())
                            {
                                applicantHasEligoDocument = true;
                            }
                        }
                        allEligoDocumentsUploaded = applicantHasEligoDocument;
                    }
            }
            if (!allEligoDocumentsUploaded)
            {
                notificationDialog.showMessage("Es sind noch nicht alle ELIGO-Testergebnisse hochgeladen", target);
            }
            else
            {

                VITATestStatus status = TEST_FINISHED;
                testGroup.setStatus(status);
                VIACUser changedBy = VIACSession.getInstance().getUserModel();
                testGroup.setChangedBy(changedBy);
                DatabaseProvider.getInstance().merge(testGroup);
                for (VITAApplicant applicant : testGroup.getTestApplicants())
                {
                    if (applicant.getStatus().equals(INVITED_TO_TEST))
                    {
                        VITAApplicantStatus applicantStatus = TEST_DONE;
                        applicant.setStatus(applicantStatus);
                        DatabaseProvider.getInstance().createHistoricStatus(applicantStatus, applicant.getId());
                        DatabaseProvider.getInstance().merge(applicant);
                    }
                }
                setResponsePage(EmploymentTestOverviewPage.class);
            }
        }
    }

    /**
     * Saves test. Also deletes all test-dependend informations of the "open applicants" and saves them.
     *
     * @param target
     *            the target
     */
    private void saveTest(AjaxRequestTarget target)
    {
        if (form.hasError())
        {
            System.out.println("error!");
        }
        testGroup.setChangedBy(VIACSession.getInstance().getUserModel());
        testGroup.setChangedOn(new Date());
        if (testGroup.getName() == null)
        {
            testGroup.setName("KEIN TESTNAME GESETZT");
        }
        DatabaseProvider.getInstance().merge(testGroup);
        for (VITAApplicant applicant : testGroup.getTestApplicants())
        {
            applicant.setEligoTest(testGroup);
            DatabaseProvider.getInstance().merge(applicant);
        }
        testApplicantsDataProvider.setApplicants(testGroup.getTestApplicants());
        VIACUser changedBy = VIACSession.getInstance().getUserModel();
        testGroup.setChangedBy(changedBy);
        for (VITAApplicant applicant : openApplicantsDataProvider.getApplicants())
        {
            applicant.setEligoTest(null);
            applicant.setEligoFirstname("");
            applicant.setEligoLastname("");
            applicant.setEligoUsername("");
            applicant.setEligoPassword("");
            applicant.setInvitationDate(null);
            VITAApplicantStatus status = APPLICATION_RECEIVED;
            applicant.setStatus(status);
            DatabaseProvider.getInstance().createHistoricStatus(status, applicant.getId());
            DatabaseProvider.getInstance().merge(applicant);
        }
        testIsSaved = true;
        toggleButtons(true, target);
    }

    /**
     * Cancel test.
     *
     * @param target
     *            the target
     */
    private void cancelTest(final AjaxRequestTarget target)
    {
        confirmDialog.showMessage(
            "Soll der Test wirklich abgesagt werden? Es wird eine Email an alle eingeladenen Teilnehmer gesendet.",
            onConfirmNotificationCancelTest(target),
            onCancelNotificationCancelTest(),
            target);
    }

    /**
     * This method is executed when the user confirms the notification-dialog that shows up when he wants to cancel the
     * test. Every applicants gets a Cancel-Test-Email (if he is already invited) and the test-status changes to "TEST
     * ABGESAGT"
     *
     * @param target
     *            the target
     * @return the command
     */
    private Command onConfirmNotificationCancelTest(final AjaxRequestTarget target)
    {
        return new Command()
        {
            /** The Constant serialVersionUID. */
            private static final long serialVersionUID = -6396826378699248128L;

            @Override
            public void execute()
            {
                try
                {
                    for (VITAApplicant applicant : testGroup.getTestApplicants())
                    {
                        // Only send cancel-mail when applicant has been
                        // invited.
                        if (applicant.getStatus().equals(INVITED_TO_TEST))
                        {
                            sendCancelMail(applicant);
                        }
                    }
                    testGroup.setChangedBy(VIACSession.getInstance().getUserModel());
                    testGroup.setChangedOn(new Date());
                    testGroup.setStatus(TEST_CANCELED);
                    DatabaseProvider.getInstance().merge(testGroup);
                    setResponsePage(EmploymentTestOverviewPage.class);
                }
                catch (MailNotSentException e)
                {
                    notificationDialog.showMessage("Emails konnten nicht gesendet werden!", target);
                }
            };
        };
    }

    /**
     * This method is executed when the user cancels the notification-dialog that shows up when he wants to cancel the
     * test. This method just closes the notification-dialog
     *
     * @return the command
     */
    private Command onCancelNotificationCancelTest()
    {
        return new Command()
        {
            /** The Constant serialVersionUID. */
            private static final long serialVersionUID = -5133389840800619951L;

            @Override
            public void execute()
            {
                AjaxRequestTarget ajaxTarget = getRequestCycle().find(AjaxRequestTarget.class);
                confirmDialog.close(ajaxTarget);
            }
        };
    }

    /**
     * This method creates a participation document, available for download.
     * 
     * @return completed participation document
     */
    private File generateParticipationDocument()
    {
        Document document = new Document(PageSize.A4, 0, 0, 0, 0);
        String testName = "";
        if (testGroup.getName() != null)
        {
            testName = testGroup.getName();
        }
        File pdf = new File(
            "Teilnehmerbestätigung_" + testName + "_" + PasswordHandler.getInstance().getRandomPassword(10) + ".pdf");
        try
        {
            // Variables
            FileOutputStream os = new FileOutputStream(pdf);
            PdfWriter.getInstance(document, os);

            BaseFont bf, bf_b;
            Font font_7, font_7_g, font_8, font_10, font_10_b;
            PdfPTable table;
            PdfPCell cell;
            String build_text;
            Date date = Calendar.getInstance().getTime();
            SimpleDateFormat sdf_date = new SimpleDateFormat("dd.MM.yyyy");
            SimpleDateFormat sdf_time = new SimpleDateFormat("HH:mm");

            // Font Variables
            bf = BaseFont.createFont("c:/windows/fonts/arial.ttf", BaseFont.WINANSI, BaseFont.EMBEDDED);
            bf_b = BaseFont.createFont("c:/windows/fonts/arialbd.ttf", BaseFont.WINANSI, BaseFont.EMBEDDED);
            font_7 = new Font(bf, 7);
            font_7_g = new Font(bf, 7);
            font_7_g.setColor(BaseColor.GRAY);
            font_8 = new Font(bf, 8);
            font_10 = new Font(bf, 10);
            font_10_b = new Font(bf_b, 10);

            document.open();

            for (VITAApplicant applicantModel : testGroup.getTestApplicants())
            {

                document.newPage();
                // Placeholder on top of the page
                table = new PdfPTable(1);
                table.getDefaultCell().setBorder(com.itextpdf.text.Rectangle.NO_BORDER);
                table.getDefaultCell().setBorderWidth(0);

                table.addCell(new Phrase(Chunk.NEWLINE));
                table.addCell(new Phrase(Chunk.NEWLINE));
                table.addCell(new Phrase(Chunk.NEWLINE));
                table.addCell(new Phrase(Chunk.NEWLINE));
                table.addCell(new Phrase(Chunk.NEWLINE));
                table.addCell(new Phrase(Chunk.NEWLINE));
                table.addCell(new Phrase(Chunk.NEWLINE));
                table.addCell(new Phrase(Chunk.NEWLINE));
                table.addCell(new Phrase(Chunk.NEWLINE));
                table.addCell(new Phrase(Chunk.NEWLINE));
                table.addCell(new Phrase(Chunk.NEWLINE));
                table.addCell(new Phrase(Chunk.NEWLINE));
                table.addCell(new Phrase(Chunk.NEWLINE));
                table.addCell(new Phrase(Chunk.NEWLINE));

                document.add(table);

                // Header information table
                table = new PdfPTable(4);
                table.getDefaultCell().setBorder(com.itextpdf.text.Rectangle.NO_BORDER);

                table.addCell(new Phrase("Ihr Zeichen", font_7));
                table.addCell(new Phrase("Unser Zeichen", font_7));
                table.addCell(new Phrase("Ansprechpartner", font_7));
                table.addCell(new Phrase("Datum", font_7));

                table.addCell(new Phrase("", font_8));
                table.addCell("");
                table.addCell(
                    new Phrase(
                        VIACSession.getInstance().getUserModel().getFirstname() + " " +
                               VIACSession.getInstance().getUserModel().getLastname(),
                        font_8));
                table.addCell(new Phrase("Dortmund, den " + sdf_date.format(date), font_8));

                document.add(table);

                // document content
                table = new PdfPTable(1);
                table.getDefaultCell().setBorder(com.itextpdf.text.Rectangle.NO_BORDER);

                table.addCell(new Phrase(Chunk.NEWLINE));
                table.addCell(new Phrase(Chunk.NEWLINE));
                table.addCell(new Phrase("Bescheinigung über die Teilnahme am Eignungstest", font_10_b));
                table.addCell(new Phrase(Chunk.NEWLINE));
                table.addCell(new Phrase(Chunk.NEWLINE));

                build_text = "Hiermit bestätigen wir, dass ";
                if (applicantModel.getGender() == "Weiblich")
                {
                    build_text += "Frau";
                }
                else
                {
                    build_text += "Herr";
                }
                date = DateUtils.addHours(testGroup.getDate(), 2);
                build_text += " " + applicantModel.getLastname() + " am " + sdf_date.format(testGroup.getDate()) +
                              " zwischen " + sdf_time.format(testGroup.getDate()) + " und " + sdf_time.format(date) +
                              " Uhr am Einstellungstest in unserem Hause teilgenommen hat.";
                table.addCell(new Phrase(build_text, font_10));

                table.addCell(new Phrase(Chunk.NEWLINE));
                table.addCell(
                    new Phrase(
                        "Sollten Sie weitere Rückfragen haben, stehe ich Ihnen jederzeit gern zur Verfügung.",
                        font_10));
                table.addCell(new Phrase(Chunk.NEWLINE));
                table.addCell(new Phrase("Mit freundlichen Grüßen", font_10));
                table.addCell(new Phrase(Chunk.NEWLINE));
                table.addCell(new Phrase("Vanderlande Industries GmbH", font_10));

                table.addCell(new Phrase(Chunk.NEWLINE));
                table.addCell(new Phrase(Chunk.NEWLINE));
                table.addCell(new Phrase(Chunk.NEWLINE));

                table.addCell(
                    new Phrase(
                        "i.A. " + VIACSession.getInstance().getUserModel().getFirstname() + " " +
                               VIACSession.getInstance().getUserModel().getLastname(),
                        font_10));

                table.addCell(new Phrase(Chunk.NEWLINE));
                table.addCell(new Phrase(Chunk.NEWLINE));
                table.addCell(new Phrase(Chunk.NEWLINE));
                table.addCell(new Phrase(Chunk.NEWLINE));
                table.addCell(new Phrase(Chunk.NEWLINE));
                table.addCell(new Phrase(Chunk.NEWLINE));
                table.addCell(new Phrase(Chunk.NEWLINE));
                table.addCell(new Phrase(Chunk.NEWLINE));
                table.addCell(new Phrase(Chunk.NEWLINE));
                table.addCell(new Phrase(Chunk.NEWLINE));
                table.addCell(new Phrase(Chunk.NEWLINE));
                table.addCell(new Phrase(Chunk.NEWLINE));
                table.addCell(new Phrase(Chunk.NEWLINE));
                table.addCell(new Phrase(Chunk.NEWLINE));
                table.addCell(new Phrase(Chunk.NEWLINE));
                table.addCell(new Phrase(Chunk.NEWLINE));
                table.addCell(new Phrase(Chunk.NEWLINE));

                document.add(table);

                // Footer on the bottom of the page
                table = new PdfPTable(1);
                table.getDefaultCell().setBorder(com.itextpdf.text.Rectangle.NO_BORDER);
                table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);

                table.addCell(new Phrase("Vanderlande Industries GmbH", font_7_g));
                table.addCell(new Phrase("Joseph-von-Fraunhofer-Str. 25 - 44227 Dortmund", font_7_g));
                table.addCell(new Phrase("Tel.: +49 (0)231 97 94-0 - Fax: +49 (0)231 97 94-1111", font_7_g));
                table.addCell(new Phrase("Commerzbank AG Beckum", font_7_g));
                table.addCell(new Phrase("IBAN DE27 4128 0043 0553 1019 00 – BIC DRESDEFF413", font_7_g));
                table.addCell(new Phrase("Geschäftsführer: Peter Stuer - Amtsgericht Dortmund - HRB 8539", font_7_g));

                document.add(table);
            }

            document.close();
        }
        catch (IOException | DocumentException e)
        {
            e.printStackTrace();
        }

        return pdf;
    }

    /**
     * Disables/hides all components that should not be visible if user does not have the required authorizations.
     */
    private void verifyPage()
    {
        authorizations = new ArrayList<>();
        authorizations = VIACSession.getInstance().getUserAuthorizations();

        // authorized to delegate tests?
        if (!authorizations.contains("vita_delegate_test"))
        {
            delegatesPanel.setVisible(false);
        }
        // authorized to close tests?
        if (!authorizations.contains("vita_close_test"))
        {
            finishTestButton.setVisible(false);
        }
        // authorized to cancel tests?
        if (!authorizations.contains("vita_cancel_test"))
        {
            cancelTestButton.setVisible(false);
        }
        // authorized to see eligo administration?
        if (!authorizations.contains("vita_eligo_administration"))
        {
            eligoManagementButton.setVisible(false);
        }
        // authorized to send mail?
        if (!authorizations.contains("vita_send_mail"))
        {
            sendEmailButton.setVisible(false);
        }

        for (VIACButton b : buttonsTest)
        {
            // authorized to edit applicants?
            if (b.getButtonType().toString() == "EDIT")
            {
                if (!authorizations.contains("vita_edit_applicant"))
                {
                    b.setVisible(false);
                }
            }
            // authorized to see details of applicants?
            if (b.getButtonType().toString() == "DETAILS")
            {
                if (!authorizations.contains("vita_detail_applicant"))
                {
                    b.setVisible(false);
                }
            }
        }

        for (VIACButton b : buttonsNextStep)
        {
            if (b.getButtonType() == null)
            {
                // authorized to send mails to individuals?
                if (b.getText().toLowerCase().equals("einladung senden"))
                {
                    if (!authorizations.contains("vita_send_mail"))
                    {
                        b.setVisible(false);
                    }
                }
            }
        }

        for (VIACButton b : buttonsPossible)
        {
            // authorized to edit applicants?
            if (b.getButtonType().toString() == "EDIT")
            {
                if (!authorizations.contains("vita_edit_applicant"))
                {
                    b.setVisible(false);
                }
            }
            // authorized to see details of applicants?
            if (b.getButtonType().toString() == "DETAILS")
            {
                if (!authorizations.contains("vita_detail_applicant"))
                {
                    b.setVisible(false);
                }
            }
        }
    }
}
