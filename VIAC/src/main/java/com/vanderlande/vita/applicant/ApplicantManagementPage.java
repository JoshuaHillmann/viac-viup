package com.vanderlande.vita.applicant;

import java.io.Serializable;
import java.util.ArrayList;
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
import org.apache.wicket.extensions.markup.html.repeater.data.table.filter.FilterToolbar;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

import com.vanderlande.util.DatabaseProvider;
import com.vanderlande.viac.command.Command;
import com.vanderlande.viac.controls.VIACActionPanel;
import com.vanderlande.viac.controls.VIACButton;
import com.vanderlande.viac.dialog.ConfirmDeleteDialog;
import com.vanderlande.viac.filter.FilterOption;
import com.vanderlande.viac.filter.VIACFilterPanel;
import com.vanderlande.viac.session.VIACSession;
import com.vanderlande.vita.VITABasePage;
import com.vanderlande.vita.applicant.provider.SortableApplicantDataProvider;
import com.vanderlande.vita.model.VITAApplicant;

/**
 * The Class ApplicantManagementPage.
 * 
 * @author denmuj
 * 
 *         Displays all applicants. You can edit/delete an existing applicant or create a new one.
 */
public class ApplicantManagementPage extends VITABasePage implements Serializable
{
    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 8726945366130152254L;

    /** The confirm delete dialog. */
    private ConfirmDeleteDialog confirmDeleteDialog;

    /** The authorizations. */
    public List<String> authorizations;

    /** The buttons. */
    public List<VIACButton> buttons;

    /** The action panel. */
    public VIACActionPanel actionPanel;

    /** The create new applicant button. */
    public Link<String> createNewApplicantButton;

    /**
     * Instantiates a new applicant management page.
     */
    public ApplicantManagementPage()
    {
        init();
    }

    /**
     * Inits the ApplicantManagementPage.
     */
    private void init()
    {
        this.setOutputMarkupId(true);
        buttons = new ArrayList<>();

        // set filter options
        List<FilterOption> filterOptions = new ArrayList<>();
        filterOptions.add(new FilterOption("Vorname", "firstname"));
        filterOptions.add(new FilterOption("Nachname", "lastname"));
        filterOptions.add(new FilterOption("Karriere", "career"));
        filterOptions.add(new FilterOption("Status", "status"));
        filterOptions.add(new FilterOption("ELIGO-Name", "eligoUsername"));

        final SortableApplicantDataProvider dataProvider = new SortableApplicantDataProvider(filterOptions);
        List<IColumn<VITAApplicant, String>> columns = new ArrayList<>();

        confirmDeleteDialog = new ConfirmDeleteDialog("confirmDeleteDialog");
        add(confirmDeleteDialog);

        // colums of the applicant table
        columns.add(new PropertyColumn<VITAApplicant, String>(new Model<>("Vorname"), "firstname", "firstname"));
        columns.add(new PropertyColumn<VITAApplicant, String>(new Model<>("Nachname"), "lastname", "lastname"));
        columns.add(new PropertyColumn<VITAApplicant, String>(new Model<>("Karriere"), "career", "career"));
        columns.add(new PropertyColumn<VITAApplicant, String>(new Model<>("Status"), "status", "status"));
        columns.add(new PropertyColumn<VITAApplicant, String>(new Model<>("Beworben am"), "appliedOn", "appliedOn"));
        columns.add(new AbstractColumn<VITAApplicant, String>(new Model<>("Aktionen"))
        {
            @Override
            public void populateItem(Item<ICellPopulator<VITAApplicant>> cellItem, String componentId,
                                     final IModel<VITAApplicant> rowModel)
            {
                VIACButton editButton = new VIACButton(VIACButton.Type.DETAILS)
                {
                    @Override
                    public void onClick(final AjaxRequestTarget target)
                    {
                        setResponsePage(new ApplicantDetailPage(rowModel.getObject(), ApplicantManagementPage.this));
                    }
                };
                VIACButton deleteButton = new VIACButton(VIACButton.Type.DELETE)
                {
                    @Override
                    public void onClick(final AjaxRequestTarget target)
                    {
                        String name = rowModel.getObject().getFirstname() + " " + rowModel.getObject().getLastname();
                        confirmDeleteDialog
                            .showMessage(name, onConfirm(rowModel.getObject(), dataProvider), onCancel(), target);
                    }
                };
                actionPanel = new VIACActionPanel(componentId, editButton, deleteButton);
                cellItem.add(actionPanel);
                buttons = actionPanel.getPanelButtons();

                // checks authorizations for this page
                verifyPage();
            }
        });

        // applicant table options
        DataTable<VITAApplicant, String> tableWithFilterForm =
            new DataTable<VITAApplicant, String>("tableWithFilterForm", columns, dataProvider, 5);

        tableWithFilterForm.setOutputMarkupId(true);

        FilterForm<VITAApplicant> applicantFilter = new FilterForm<VITAApplicant>("filterForm", dataProvider);
        add(applicantFilter);
        FilterToolbar filterToolbar = new FilterToolbar(tableWithFilterForm, applicantFilter);
        tableWithFilterForm.addTopToolbar(filterToolbar);
        tableWithFilterForm.addTopToolbar(new HeadersToolbar<>(tableWithFilterForm, dataProvider));
        tableWithFilterForm.addBottomToolbar(new NavigationToolbar(tableWithFilterForm));
        applicantFilter.add(tableWithFilterForm);

        VIACFilterPanel<VITAApplicant> filterPanel =
            new VIACFilterPanel<>("filterPanel", dataProvider, tableWithFilterForm);
        applicantFilter.add(filterPanel);

        createNewApplicantButton = new Link<String>("createNewApplicantButton")
        {
            /** The Constant serialVersionUID. */
            private static final long serialVersionUID = -4037454663308029262L;

            @Override
            public void onClick()
            {
                setResponsePage(new ApplicantDetailPage(ApplicantManagementPage.this));
            }
        };
        add(createNewApplicantButton);
    }

    /**
     * On confirm.
     * 
     * This method is executed when the user confirms the notification-dialog that shows up when he wants to delete the
     * applicant. This method just deletes the applicant.
     *
     * @param rowModel
     *            the row model
     * @param dataProvider
     *            the data provider
     * @return the command
     */
    public Command onConfirm(final VITAApplicant rowModel, final SortableApplicantDataProvider dataProvider)
    {
        return new Command()
        {
            public void execute()
            {
                AjaxRequestTarget ajaxTarget = getRequestCycle().find(AjaxRequestTarget.class);
                DatabaseProvider.getInstance().deleteApplicant(rowModel);
                dataProvider.reload();
                ajaxTarget.add(ApplicantManagementPage.this);
            }
        };
    }

    /**
     * On cancel.
     * 
     * This method is executed when the user cancels the notification-dialog that shows up when he wants to delete the
     * applicant. This method just closes the notification-dialog.
     *
     * @return the command
     */
    public Command onCancel()
    {
        return new Command()
        {
            public void execute()
            {
                AjaxRequestTarget ajaxTarget = getRequestCycle().find(AjaxRequestTarget.class);
                confirmDeleteDialog.close(ajaxTarget);
            }
        };
    }

    /**
     * Disables/hides all components that should not be visible if user does not have the required authorizations.
     */
    private void verifyPage()
    {
        authorizations = new ArrayList<>();
        authorizations = VIACSession.getInstance().getUserAuthorizations();

        // authorized to create new applicant?
        if (!authorizations.contains("vita_new_applicant"))
        {
            createNewApplicantButton.setVisible(false);
        }

        for (VIACButton b : buttons)
        {
            // authorized to block applicant?
            if (b.getButtonType() == null)
            {
                if (b.getText().toLowerCase().equals("sperren"))
                {
                    if (!authorizations.contains("viac_block_user"))
                    {
                        b.setVisible(false);
                    }
                }
            }
            // authorized to delete applicant?
            else if (b.getButtonType().toString() == "DELETE")
            {
                if (!authorizations.contains("vita_delete_applicant"))
                {
                    b.setVisible(false);
                }
                // authorized to see details of applicant?
            }
            else if (b.getButtonType().toString() == "DETAILS")
            {
                if (!authorizations.contains("vita_detail_applicant"))
                {
                    b.setVisible(false);
                }
            }
        }
    }
}
