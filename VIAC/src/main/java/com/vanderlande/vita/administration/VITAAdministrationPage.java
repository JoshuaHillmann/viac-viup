package com.vanderlande.vita.administration;

import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.extensions.markup.html.repeater.data.grid.ICellPopulator;
import org.apache.wicket.extensions.markup.html.repeater.data.table.AbstractColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.DataTable;
import org.apache.wicket.extensions.markup.html.repeater.data.table.HeadersToolbar;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.NavigationToolbar;
import org.apache.wicket.extensions.markup.html.repeater.data.table.PropertyColumn;
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
import com.vanderlande.viac.session.VIACSession;
import com.vanderlande.vita.VITABasePage;
import com.vanderlande.vita.administration.provider.SortableCareerDataProvider;
import com.vanderlande.vita.model.VITACareer;

/**
 * The Class VITAAdministrationPage.
 * 
 * @author dermic, denmuj
 * 
 *         On this page the whole vita administration is done. In this page every career is displayed. You can create a
 *         new career or edit a career(change the name of the selected one or set it to not active).
 */
public class VITAAdministrationPage extends VITABasePage
{
    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 4182786939755356936L;

    /** The table with filterForm . */
    private DataTable<VITACareer, String> tableWithFilterForm;

    /** The delete career button. */
    private AjaxButton deleteCareerButton;

    /** The edit career button. */
    private Button editCareerButton;

    /** The edit career. */
    private VITACareer editCareer;

    /** The confirm delete dialog. */
    private ConfirmDeleteDialog confirmDeleteDialog;

    /** The edit career name input. */
    private TextField<String> editCareerNameInput;

    /** The add career panel. */
    private WebMarkupContainer addCareerPanel;

    /** The edit career panel. */
    private WebMarkupContainer editCareerPanel;

    /** The authorizations. */
    public List<String> authorizations;

    /** The buttons. */
    public List<VIACButton> buttons;

    /** The action panel. */
    public VIACActionPanel actionPanel;

    /** The Constructor. */
    public VITAAdministrationPage()
    {
        init();
    }

    /**
     * Instantiates a new table with filterForm.
     * 
     * @return table with filterForm
     */
    public DataTable<VITACareer, String> getTable()
    {
        return tableWithFilterForm;
    }

    /**
     * Instantiates a new vita administration page.
     */
    public void init()
    {
        // the panels
        addCareerPanel = new WebMarkupContainer("addCareerPanel");
        editCareerPanel = new WebMarkupContainer("editCareerPanel");
        buttons = new ArrayList<>();
        confirmDeleteDialog = new ConfirmDeleteDialog("confirmDeleteDialog");
        add(confirmDeleteDialog);

        // the editCareer InputField
        editCareerNameInput = new TextField<>("editCareerNameInput");
        editCareerNameInput.setRequired(true);
        editCareerNameInput.setEnabled(false);
        editCareerNameInput.setOutputMarkupId(true);

        // the table filled with all careers
        final List<IColumn<VITACareer, String>> columns = new ArrayList<>();
        final SortableCareerDataProvider dataProvider = new SortableCareerDataProvider();

        // the different columns
        columns.add(new PropertyColumn<VITACareer, String>(new Model<>("ID"), "id", "id"));
        columns.add(new PropertyColumn<VITACareer, String>(new Model<>("Name"), "name", "name"));
        columns.add(new PropertyColumn<VITACareer, String>(new Model<>("Aktuell"), "isActive", "isActive"));
        columns.add(new AbstractColumn<VITACareer, String>(new Model<>("Aktionen"))
        {
            @Override
            public void populateItem(Item<ICellPopulator<VITACareer>> cellItem, String componentId,
                                     final IModel<VITACareer> rowModel)
            {
                VIACButton editButton = new VIACButton(VIACButton.Type.EDIT)
                {
                    @Override
                    public void onClick(final AjaxRequestTarget target)
                    {
                        // enable buttons an set changes
                        editCareer = rowModel.getObject();
                        deleteCareerButton.setEnabled(true);
                        editCareerButton.setEnabled(true);
                        editCareerNameInput.setModel(new PropertyModel<String>(editCareer, "name"));
                        editCareerNameInput.setRequired(true);
                        editCareerNameInput.setOutputMarkupId(true);
                        editCareerNameInput.setEnabled(true);
                        // refresh
                        target.add(deleteCareerButton);
                        target.add(editCareerButton);
                        target.add(editCareerNameInput);
                    }
                };

                // if career is not active disable edit button
                if (!rowModel.getObject().isActive())
                {
                    editButton.setVisible(false);
                }
                actionPanel = new VIACActionPanel(componentId, editButton);
                cellItem.add(actionPanel);
                buttons = actionPanel.getPanelButtons();

                // check authorizations for this page
                verifyPage();
            }
        });

        // settings for career table
        tableWithFilterForm = new DataTable<VITACareer, String>("tableWithFilterForm", columns, dataProvider, 5);
        tableWithFilterForm.setOutputMarkupId(true);
        tableWithFilterForm.addTopToolbar(new HeadersToolbar<>(tableWithFilterForm, dataProvider));
        tableWithFilterForm.addBottomToolbar(new NavigationToolbar(tableWithFilterForm));
        add(tableWithFilterForm);

        // new career
        final VITACareer career = new VITACareer();
        Form<String> addCareerForm = new Form<String>("addCareerForm");
        final TextField<String> newCarrerNameInput =
            new TextField<>("newCareerNameInput", new PropertyModel<String>(career, "name"));
        newCarrerNameInput.setRequired(true);
        Button submitButton = new Button("submit")
        {
            /** The Constant serialVersionUID. */
            private static final long serialVersionUID = 4834618813399455121L;

            @Override
            public void onSubmit()
            {
                super.onSubmit();
                dataProvider.addCareer(career);
                setResponsePage(new VITAAdministrationPage());
            }
        };
        addCareerForm.add(newCarrerNameInput);
        addCareerForm.add(submitButton);
        addCareerPanel.add(addCareerForm);

        // edit career
        Form<String> editCareerForm = new Form<String>("editCareerForm");
        editCareerForm.add(editCareerNameInput);
        editCareerButton = new Button("edit")
        {
            @Override
            public void onSubmit()
            {
                super.onSubmit();
                editCareer.setName(editCareerNameInput.getInput());
                DatabaseProvider.getInstance().merge(editCareer);
                setResponsePage(VITAAdministrationPage.class);
            }
        };

        // delete career
        deleteCareerButton = new AjaxButton("delete")
        {
            @Override
            public void onSubmit(AjaxRequestTarget target, Form<?> form)
            {
                confirmDeleteDialog.showMessage(editCareer.getName(), onConfirm(), onCancel(), target);
            }
        };

        if (editCareer == null)
        {
            editCareerButton.setEnabled(false);
            deleteCareerButton.setEnabled(false);
        }
        editCareerForm.add(editCareerButton);
        editCareerForm.add(deleteCareerButton);
        editCareerPanel.add(editCareerForm);
        add(addCareerPanel);
        add(editCareerPanel);

    }

    /**
     * This method is executed when the user confirms the notification-dialog that shows up when he wants to archive the
     * career. This method just sets the career changes.
     *
     * @return the command
     */
    protected Command onConfirm()
    {
        return new Command()
        {
            /** The Constant serialVersionUID. */
            private static final long serialVersionUID = 1366784755341148663L;

            @Override
            public void execute()
            {
                editCareer.setActive(false);
                DatabaseProvider.getInstance().merge(editCareer);
                setResponsePage(VITAAdministrationPage.class);
            }
        };
    }

    /**
     * This method is executed when the user cancels the notification-dialog that shows up when he wants to archive the
     * career. This method just closes the notification-dialog.
     *
     * @return the command
     */
    protected Command onCancel()
    {
        return new Command()
        {
            /** The Constant serialVersionUID. */
            private static final long serialVersionUID = -3863853140589898962L;

            @Override
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

        // authorized to add new career?
        if (!authorizations.contains("vita_new_career"))
        {
            addCareerPanel.setVisible(false);
        }

        // authorized to edit careers?
        for (VIACButton b : buttons)
        {
            if (b.getButtonType().toString() == "EDIT")
            {
                if (!authorizations.contains("vita_edit_career"))
                {
                    b.setVisible(false);
                    editCareerPanel.setVisible(false);
                }
            }
        }
    }
}
