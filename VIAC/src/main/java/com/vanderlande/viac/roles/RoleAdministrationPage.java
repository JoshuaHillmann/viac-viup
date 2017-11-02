package com.vanderlande.viac.roles;

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
import org.apache.wicket.extensions.markup.html.repeater.data.table.filter.FilterToolbar;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;

import com.vanderlande.util.DatabaseProvider;
import com.vanderlande.viac.VIACBasePage;
import com.vanderlande.viac.command.Command;
import com.vanderlande.viac.controls.VIACActionPanel;
import com.vanderlande.viac.controls.VIACButton;
import com.vanderlande.viac.dialog.ConfirmDeleteDialog;
import com.vanderlande.viac.filter.FilterOption;
import com.vanderlande.viac.filter.VIACFilterPanel;
import com.vanderlande.viac.model.VIACRole;
import com.vanderlande.viac.model.VIACUser;
import com.vanderlande.viac.roles.provider.SortableRoleDataProvider;
import com.vanderlande.viac.session.VIACSession;

/**
 * The Class RoleAdministrationPage.
 * 
 * @author dermic, desczu
 * 
 *         Displays all available roles. Click on a role to edit/delete it. You can also create a new role.
 */
public class RoleAdministrationPage extends VIACBasePage implements Serializable
{

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = -1605204959720209033L;

    /** The confirm delete dialog. */
    private ConfirmDeleteDialog confirmDeleteDialog;

    /** The authorizations. */
    List<String> authorizations;

    /** The action panel. */
    VIACActionPanel actionPanel;

    /** The buttons. */
    List<VIACButton> buttons;

    /** The add role panel. */
    private WebMarkupContainer addRolePanel;

    /**
     * Instantiates a new role administration page.
     */
    public RoleAdministrationPage()
    {
        init();
    }

    /**
     * Inits the.
     */
    private void init()
    {
        addRolePanel = new WebMarkupContainer("addRolePanel");

        buttons = new ArrayList<>();

        this.setOutputMarkupId(true);

        List<FilterOption> filterProperties = new ArrayList<>();
        filterProperties.add(new FilterOption("Name", "name"));
        filterProperties.add(new FilterOption("Beschreibung", "description"));

        final SortableRoleDataProvider dataProvider = new SortableRoleDataProvider(filterProperties);
        List<IColumn<VIACRole, String>> columns = new ArrayList<>();

        confirmDeleteDialog = new ConfirmDeleteDialog("confirmDeleteDialog");
        add(confirmDeleteDialog);

        columns.add(new PropertyColumn<VIACRole, String>(new Model<>("Name"), "name", "name"));
        columns.add(new PropertyColumn<VIACRole, String>(new Model<>("Beschreibung"), "description", "description"));
        columns.add(new AbstractColumn<VIACRole, String>(new Model<>("Aktionen"))
        {
            @Override
            public void populateItem(Item<ICellPopulator<VIACRole>> cellItem, String componentId,
                                     final IModel<VIACRole> rowModel)
            {
                VIACButton editButton = new VIACButton(VIACButton.Type.EDIT)
                {
                    @Override
                    public void onClick(final AjaxRequestTarget target)
                    {
                        setResponsePage(new RoleEditingPage(rowModel.getObject()));
                    }
                };
                VIACButton deleteButton = new VIACButton(VIACButton.Type.DELETE)
                {
                    @Override
                    public void onClick(final AjaxRequestTarget target)
                    {
                        String name = rowModel.getObject().getName();
                        confirmDeleteDialog
                            .showMessage(name, onConfirm(rowModel.getObject(), dataProvider), onCancel(), target);

                    }
                };
                actionPanel = new VIACActionPanel(componentId, editButton, deleteButton);
                cellItem.add(actionPanel);
                buttons = actionPanel.getPanelButtons();
                verifyPage();
                if (rowModel.getObject().getName().equalsIgnoreCase("VIAC ADMIN"))
                {
                    editButton.setVisible(false);
                    deleteButton.setVisible(false);
                }
            }

        });

        DataTable<VIACRole, String> tableWithFilterForm =
            new DataTable<VIACRole, String>("tableWithFilterForm", columns, dataProvider, 5);

        tableWithFilterForm.setOutputMarkupId(true);

        FilterForm<VIACRole> roleFilter = new FilterForm<VIACRole>("filterForm", dataProvider);
        add(roleFilter);
        FilterToolbar filterToolbar = new FilterToolbar(tableWithFilterForm, roleFilter);
        tableWithFilterForm.addTopToolbar(filterToolbar);
        tableWithFilterForm.addTopToolbar(new HeadersToolbar<>(tableWithFilterForm, dataProvider));
        tableWithFilterForm.addBottomToolbar(new NavigationToolbar(tableWithFilterForm));
        roleFilter.add(tableWithFilterForm);

        VIACFilterPanel<VIACRole> filterPanel = new VIACFilterPanel<>("filterPanel", dataProvider, tableWithFilterForm);
        roleFilter.add(filterPanel);

        // Form
        final VIACRole role = new VIACRole();
        Form<String> addRoleForm = new Form<String>("addRoleForm");
        final TextField<String> roleNameInput =
            new TextField<String>("roleNameInput", new PropertyModel<String>(role, "name"));
        roleNameInput.setRequired(true);
        TextArea<String> roleDescriptionInput =
            new TextArea<String>("roleDescriptionInput", new PropertyModel<String>(role, "description"));
        Button submitButton = new Button("submit")
        {
            @Override
            public void onSubmit()
            {
                super.onSubmit();

                VIACUser createdBy = VIACSession.getInstance().getUserModel();
                Date createdOn = new Date();

                role.setCreatedBy(createdBy);
                role.setChangedBy(createdBy);
                role.setCreatedOn(createdOn);
                role.setChangedOn(createdOn);
                dataProvider.addRole(role);
                setResponsePage(new RoleEditingPage(role));
            }
        };
        addRoleForm.add(roleNameInput);
        addRoleForm.add(roleDescriptionInput);
        addRoleForm.add(submitButton);
        addRolePanel.add(addRoleForm);
        add(addRolePanel);
    }

    /**
     * On confirm.
     *
     * @param rowModel
     *            the row model
     * @param dataProvider
     *            the data provider
     * @return the command
     */
    public Command onConfirm(final VIACRole rowModel, final SortableRoleDataProvider dataProvider)
    {
        return new Command()
        {
            @Override
            public void execute()
            {
                AjaxRequestTarget ajaxTarget = getRequestCycle().find(AjaxRequestTarget.class);
                DatabaseProvider.getInstance().deleteRole(rowModel);
                dataProvider.reload();

                confirmDeleteDialog.close(ajaxTarget);
                ajaxTarget.add(RoleAdministrationPage.this);
            }
        };
    }

    /**
     * On cancel.
     *
     * @return the command
     */
    public Command onCancel()
    {
        return new Command()
        {
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

        if (!authorizations.contains("viac_new_role"))
        {
            addRolePanel.setVisible(false);
        }

        for (VIACButton b : buttons)
        {

            if (b.getButtonType().toString().equals("DELETE"))
            {
                if (!authorizations.contains("viac_delete_role"))
                {
                    b.setVisible(false);
                }
            }

            if (b.getButtonType().toString().equals("EDIT"))
            {
                if (!authorizations.contains("viac_edit_role"))
                {
                    b.setVisible(false);
                }
            }
        }

    }

    /**
     * Check name.
     *
     * @param name
     *            the name
     * @return the boolean
     */
    private Boolean checkName(String name)
    {
        return name.length() > 1;
    }
}
