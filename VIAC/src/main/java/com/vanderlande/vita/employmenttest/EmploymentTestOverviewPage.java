package com.vanderlande.vita.employmenttest;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.extensions.markup.html.repeater.data.grid.ICellPopulator;
import org.apache.wicket.extensions.markup.html.repeater.data.table.AbstractColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.DataTable;
import org.apache.wicket.extensions.markup.html.repeater.data.table.HeadersToolbar;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.NavigationToolbar;
import org.apache.wicket.extensions.markup.html.repeater.data.table.PropertyColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.filter.FilterForm;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;

import com.vanderlande.util.DatabaseProvider;
import com.vanderlande.viac.command.Command;
import com.vanderlande.viac.controls.VIACActionPanel;
import com.vanderlande.viac.controls.VIACButton;
import com.vanderlande.viac.dialog.ConfirmDeleteDialog;
import com.vanderlande.viac.filter.FilterOption;
import com.vanderlande.viac.filter.VIACFilterPanel;
import com.vanderlande.viac.model.VIACUser;
import com.vanderlande.viac.session.VIACSession;
import com.vanderlande.vita.VITABasePage;
import com.vanderlande.vita.employmenttest.provider.EmploymentTestGroupDataProvider;
import com.vanderlande.vita.model.VITAApplicant;
import com.vanderlande.vita.model.VITAApplicantStatus;
import com.vanderlande.vita.model.VITAEligoTest;
import com.vanderlande.vita.model.VITATestStatus;

/**
 * The Class EmploymentTestOverviewPage.
 * 
 * @author dermic, desczu
 * 
 *         This class displays every test-group. You can create a new test, delete a test (only if no applicant was
 *         already invited for this test) and, if the test is finished, open a test-summary page.
 */
public class EmploymentTestOverviewPage extends VITABasePage implements Serializable
{
    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = -8852909178298357640L;

    /** The default room. */
    final String DEFAULT_ROOM = "OHS 18 Schulungsraum";

    /** The confirm delete dialog. */
    private ConfirmDeleteDialog confirmDeleteDialog;

    /** The authorizations. */
    public List<String> authorizations;

    /** The buttons. */
    public List<VIACButton> buttons;

    /** The action panel. */
    public VIACActionPanel actionPanel;

    /** The add test panel. */
    private WebMarkupContainer addTestPanel;

    /**
     * Instantiates a new employment test group page.
     */
    public EmploymentTestOverviewPage()
    {
        init();
    }

    /**
     * Inits the EmploymentTestOverviewPage.
     */
    private void init()
    {
        addTestPanel = new WebMarkupContainer("addTestPanel");
        this.setOutputMarkupId(true);
        confirmDeleteDialog = new ConfirmDeleteDialog("confirmDeleteDialog");
        add(confirmDeleteDialog);

        // filter options
        List<FilterOption> filterProperties = new ArrayList<>();
        filterProperties.add(new FilterOption("Name", "name"));
        filterProperties.add(new FilterOption("Status", "status"));

        // columns of test table
        List<IColumn<VITAEligoTest, String>> columns = new ArrayList<>();
        final EmploymentTestGroupDataProvider dataProvider = new EmploymentTestGroupDataProvider(filterProperties);
        columns.add(new PropertyColumn<VITAEligoTest, String>(new Model<>("ID"), "id", "id"));
        columns.add(new PropertyColumn<VITAEligoTest, String>(new Model<>("Name"), "name", "name"));
        columns.add(new PropertyColumn<VITAEligoTest, String>(new Model<>("Datum"), "date", "date"));
        columns.add(new PropertyColumn<VITAEligoTest, String>(new Model<>("Status"), "status", "status"));
        columns
            .add(new PropertyColumn<VITAEligoTest, String>(new Model<>("Erstellt von"), "createdBy", "createdBy.name"));
        columns.add(
            new PropertyColumn<VITAEligoTest, String>(
                new Model<>("Zuletzt geändert von"),
                "changedBy",
                "changedBy.name"));
        columns.add(new PropertyColumn<VITAEligoTest, String>(new Model<>("Geändert am"), "changedOn", "changedOn"));
        columns.add(new AbstractColumn<VITAEligoTest, String>(new Model<>("Aktionen"))
        {
            /** The Constant serialVersionUID. */
            private static final long serialVersionUID = 8664433472015345595L;

            @Override
            public void populateItem(Item<ICellPopulator<VITAEligoTest>> cellItem, String componentId,
                                     final IModel<VITAEligoTest> rowModel)
            {
                // edit button
                VIACButton editButton = new VIACButton(VIACButton.Type.EDIT)
                {
                    private static final long serialVersionUID = -1045760471934970249L;

                    @Override
                    public void onClick(AjaxRequestTarget target)
                    {
                        setResponsePage(new EmploymentTestDetailsPage(rowModel.getObject()));
                    }
                };

                // delete button
                VIACButton deleteButton = new VIACButton(VIACButton.Type.DELETE)
                {
                    private static final long serialVersionUID = 449721839196109195L;

                    @Override
                    public void onClick(final AjaxRequestTarget target)
                    {
                        String name = rowModel.getObject().getName();
                        confirmDeleteDialog
                            .showMessage(name, onConfirm(rowModel.getObject(), dataProvider), onCancel(), target);
                    }
                };

                // summary button
                VIACButton summaryButton = new VIACButton(VIACButton.Type.DETAILS)
                {
                    private static final long serialVersionUID = 8842648163086760741L;

                    @Override
                    public void onClick(AjaxRequestTarget target)
                    {
                        setResponsePage(new EmploymentTestSummaryPage(rowModel.getObject()));
                    }
                };
                // if the status is "test geplant" you can edit and delete the
                // button
                if (rowModel
                    .getObject()
                    .getStatus()
                    .equals(DatabaseProvider.getInstance().getAll(VITATestStatus.class).get(0)))
                {
                    editButton.setEnabled(true);
                    deleteButton.setEnabled(true);
                    summaryButton.setEnabled(false);
                    summaryButton.setVisible(false);
                }
                // otherwise you can only see the summary-page
                else
                {
                    editButton.setEnabled(false);
                    editButton.setVisible(false);
                    deleteButton.setEnabled(false);
                    deleteButton.setVisible(false);
                }

                // deleting a test is only allowed when no invitations are sent
                boolean invitationsSent = false;
                for (VITAApplicant applicant : rowModel.getObject().getTestApplicants())
                {
                    if (!invitationsSent)
                    {
                        if (!applicant.getStatus().equals(
                            DatabaseProvider.getInstance().getAll(VITAApplicantStatus.class).get(0)))
                        {
                            invitationsSent = true;
                        }
                    }
                }
                if (invitationsSent)
                {
                    deleteButton.setEnabled(false);
                    deleteButton.setVisible(false);
                }
                actionPanel = new VIACActionPanel(componentId, editButton, summaryButton, deleteButton);
                cellItem.add(actionPanel);
                buttons = actionPanel.getPanelButtons();

                // check authorizations for this page
                verifyPage();
            }
        });

        // table with filter form
        DataTable<VITAEligoTest, String> tableWithFilterForm =
            new DataTable<VITAEligoTest, String>("tableWithFilterForm", columns, dataProvider, 5);

        tableWithFilterForm.setOutputMarkupId(true);

        FilterForm<VITAEligoTest> filterForm = new FilterForm<VITAEligoTest>("filterForm", dataProvider);
        VIACFilterPanel<VITAEligoTest> filterPanel =
            new VIACFilterPanel<VITAEligoTest>("filterPanel", dataProvider, tableWithFilterForm);
        filterForm.add(filterPanel);
        filterForm.add(tableWithFilterForm);

        tableWithFilterForm.addTopToolbar(new HeadersToolbar<>(tableWithFilterForm, dataProvider));
        tableWithFilterForm.addBottomToolbar(new NavigationToolbar(tableWithFilterForm));
        filterForm.add(tableWithFilterForm);

        // add test form
        final VITAEligoTest eligoTest = new VITAEligoTest();
        final Form<String> addTestForm = new Form<String>("addTestForm");
        TextField<String> testNameInput =
            new TextField<String>("testNameInput", new PropertyModel<String>(eligoTest, "name"));

        // submit button
        Button submitButton = new Button("submit")
        {
            /** The Constant serialVersionUID. */
            private static final long serialVersionUID = 5829835985538770036L;

            @Override
            public void onSubmit()
            {
                super.onSubmit();
                createTest(dataProvider, eligoTest);
            }
        };
        testNameInput.setRequired(true);

        // add components to form
        addTestForm.add(testNameInput);
        addTestForm.add(submitButton);
        addTestPanel.add(addTestForm);
        add(filterForm);
        add(addTestPanel);
    }

    /**
     * On confirm.
     * 
     * This method is called when the delete-confirmation dialog is confirmed. The selected testGroup is deleted.
     *
     * @param testGroup
     *            the row model
     * @param dataProvider
     *            the data provider
     * @return the command
     */
    public Command onConfirm(final VITAEligoTest testGroup, final EmploymentTestGroupDataProvider dataProvider)
    {
        return new Command()
        {
            /** The Constant serialVersionUID. */
            private static final long serialVersionUID = -1387759911339163020L;

            @Override
            public void execute()
            {
                AjaxRequestTarget ajaxTarget = getRequestCycle().find(AjaxRequestTarget.class);
                DatabaseProvider.getInstance().deleteTest(testGroup);
                dataProvider.reload();

                confirmDeleteDialog.close(ajaxTarget);
                ajaxTarget.add(EmploymentTestOverviewPage.this);
            }
        };
    }

    /**
     * On cancel.
     * 
     * This method is called when the delete-confirmation dialog is cancelled. Just closes the dialog.
     *
     * @return the command
     */
    public Command onCancel()
    {
        return new Command()
        {
            /** The Constant serialVersionUID. */
            private static final long serialVersionUID = 6102534455870604110L;

            @Override
            public void execute()
            {
                AjaxRequestTarget ajaxTarget = getRequestCycle().find(AjaxRequestTarget.class);
                confirmDeleteDialog.close(ajaxTarget);
            }
        };
    }

    /**
     * Creates the test.
     * 
     * Redirects to the EmploymentTestDetailsPage
     *
     * @param dataProvider
     *            the data provider
     * @param eligoTest
     *            the eligo test
     */
    private void createTest(final EmploymentTestGroupDataProvider dataProvider, final VITAEligoTest eligoTest)
    {
        VIACUser createdBy = VIACSession.getInstance().getUserModel();
        VITATestStatus status = DatabaseProvider.getInstance().getAll(VITATestStatus.class).get(0);
        Date createdOn = new Date();

        eligoTest.setCreatedBy(createdBy);
        eligoTest.setCreatedOn(createdOn);
        eligoTest.setRoom(DEFAULT_ROOM);
        eligoTest.setStatus(status);
        dataProvider.addTest(eligoTest);
        setResponsePage(new EmploymentTestDetailsPage(eligoTest));
    }

    /**
     * Disables/hides all components that should not be visible if user does not have the required authorizations.
     */
    private void verifyPage()
    {
        authorizations = new ArrayList<>();
        authorizations = VIACSession.getInstance().getUserAuthorizations();

        // authorized to create new test?
        if (!authorizations.contains("vita_new_test"))
        {
            addTestPanel.setVisible(false);
        }

        for (VIACButton b : buttons)
        {
            // authorized to edit test?
            if (b.getButtonType().toString() == "EDIT")
            {
                if (!authorizations.contains("vita_edit_test"))
                {
                    b.setVisible(false);
                }
            }
            // authorized to delete test
            if (b.getButtonType().toString() == "DELETE")
            {
                if (!authorizations.contains("vita_delete_test"))
                {
                    b.setVisible(false);
                }
            }
        }
    }
}