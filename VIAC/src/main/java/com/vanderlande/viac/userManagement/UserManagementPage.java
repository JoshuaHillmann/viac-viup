package com.vanderlande.viac.userManagement;

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
import com.vanderlande.viac.VIACBasePage;
import com.vanderlande.viac.command.Command;
import com.vanderlande.viac.controls.VIACActionPanel;
import com.vanderlande.viac.controls.VIACButton;
import com.vanderlande.viac.dialog.ConfirmDeleteDialog;
import com.vanderlande.viac.filter.FilterOption;
import com.vanderlande.viac.filter.VIACFilterPanel;
import com.vanderlande.viac.model.VIACUser;
import com.vanderlande.viac.session.VIACSession;
import com.vanderlande.viac.userManagement.data.model.UserTableModel;
import com.vanderlande.viac.userManagement.data.provider.SortableUserDataProvider;

/**
 * The Class UserManagementPage.
 * 
 * @author dekscha
 * 
 *         Displays all users. You can edit/delete an existing user or create a new one.
 */
public class UserManagementPage extends VIACBasePage
{

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 4675018248487681499L;

    /** The confirm delete dialog. */
    private ConfirmDeleteDialog confirmDeleteDialog;

    /** The data provider. */
    private SortableUserDataProvider dataProvider;

    /** The authorizations. */
    List<String> authorizations;

    /** The action panel. */
    VIACActionPanel actionPanel;

    /** The buttons. */
    List<VIACButton> buttons;

    /** The create new user button. */
    Link<String> createNewUserButton;

    /**
     * Instantiates a new user management page.
     */
    public UserManagementPage()
    {
        super();
    }

    /**
     * Initializes the components.
     */
    @Override
    protected void initialize()
    {
        super.initialize();
        confirmDeleteDialog = new ConfirmDeleteDialog("confirmDeleteDialog");
        add(confirmDeleteDialog);

        buttons = new ArrayList<>();

        List<FilterOption> filters = new ArrayList<>();
        filters.add(new FilterOption("Benutzername", "name"));
        filters.add(new FilterOption("Vorname", "firstname"));
        filters.add(new FilterOption("Nachname", "lastname"));
        filters.add(new FilterOption("Rolle", "roles"));

        dataProvider = new SortableUserDataProvider(filters);

        DataTable<UserTableModel, String> tableWithFilterForm = this.initDataTable(filters);

        FilterForm<UserTableModel> userFilterForm = new FilterForm<UserTableModel>("filterForm", dataProvider);

        FilterToolbar filterToolbar = new FilterToolbar(tableWithFilterForm, userFilterForm);
        tableWithFilterForm.addTopToolbar(filterToolbar);
        tableWithFilterForm.addTopToolbar(new HeadersToolbar<>(tableWithFilterForm, dataProvider));
        tableWithFilterForm.addBottomToolbar(new NavigationToolbar(tableWithFilterForm));
        userFilterForm.add(tableWithFilterForm);

        VIACFilterPanel<UserTableModel> filterPanel =
            new VIACFilterPanel<>("filterPanel", dataProvider, tableWithFilterForm);
        userFilterForm.add(filterPanel);

        add(userFilterForm);

        createNewUserButton = new Link<String>("createNewUserButton")
        {
            private static final long serialVersionUID = -4037454663308029262L;

            @Override
            public void onClick()
            {
                setResponsePage(new EditUserPage());
            }
        };
        add(createNewUserButton);

    }

    /**
     * Inits the data table.
     *
     * @param filters
     *            the filters
     * @return the data table
     */
    private DataTable<UserTableModel, String> initDataTable(List<FilterOption> filters)
    {

        final SortableUserDataProvider fDataProvider = dataProvider;
        List<IColumn<UserTableModel, String>> columns = new ArrayList<>();

        columns.add(new PropertyColumn<UserTableModel, String>(new Model<>("Benutzername"), "name", "name"));
        columns.add(new PropertyColumn<UserTableModel, String>(new Model<>("Vorname"), "firstname", "firstname"));
        columns.add(new PropertyColumn<UserTableModel, String>(new Model<>("Nachname"), "lastname", "lastname"));
        columns.add(new PropertyColumn<UserTableModel, String>(new Model<>("Rollen"), "roles", "roles"));
        columns.add(new AbstractColumn<UserTableModel, String>(new Model<>("Aktionen"))
        {
            private static final long serialVersionUID = -229895030474387747L;

            @Override
            public void populateItem(Item<ICellPopulator<UserTableModel>> cellItem, String componentId,
                                     IModel<UserTableModel> rowModel)
            {
                final VIACUser user = rowModel.getObject().getUser();

                VIACButton editButton = new VIACButton(VIACButton.Type.EDIT)
                {
                    private static final long serialVersionUID = 1L;

                    @Override
                    public void onClick(AjaxRequestTarget target)
                    {
                        setResponsePage(new EditUserPage(user));
                    }
                };
                final String name = rowModel.getObject().getName();
                VIACButton deleteButton = new VIACButton(VIACButton.Type.DELETE)
                {
                    private static final long serialVersionUID = 1L;

                    @Override
                    public void onClick(AjaxRequestTarget target)
                    {
                        confirmDeleteDialog.showMessage(name, onConfirm(user, fDataProvider), onCancel(), target);
                    }

                };

                String text = "";
                if (user.isBanned())
                    text = "Entsperren";
                else
                    text = "Sperren";

                VIACButton banButton = new VIACButton(text)
                {

                    private static final long serialVersionUID = 1L;

                    @Override
                    public void onClick(AjaxRequestTarget target)
                    {
                        banUser(user);
                        target.add(UserManagementPage.this);
                    }
                };
                actionPanel = new VIACActionPanel(componentId, editButton, deleteButton, banButton);
                cellItem.add(actionPanel);
                buttons = actionPanel.getPanelButtons();
                verifyPage();
                if (user.equals(VIACSession.getInstance().getUserModel()))
                {
                    deleteButton.setVisible(false);
                    banButton.setVisible(false);
                }
            }
        });

        DataTable<UserTableModel, String> dataTable =
            new DataTable<UserTableModel, String>("tableWithFilterForm", columns, dataProvider, 5);
        dataTable.setOutputMarkupId(true);

        return dataTable;
    }

    /**
     * On confirm.
     *
     * @param user
     *            the user
     * @param dataProvider
     *            the data provider
     * @return the command
     */
    private Command onConfirm(final VIACUser user, final SortableUserDataProvider dataProvider)
    {
        return new Command()
        {
            private static final long serialVersionUID = 1L;

            @Override
            public void execute()
            {
                AjaxRequestTarget ajaxTarget = getRequestCycle().find(AjaxRequestTarget.class);
                if (user.getId() != VIACSession.getInstance().getUserModel().getId())
                    DatabaseProvider.getInstance().deleteUser(user);
                dataProvider.reload();
                ajaxTarget.add(UserManagementPage.this);
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
            private static final long serialVersionUID = 1L;

            @Override
            public void execute()
            {
                AjaxRequestTarget ajaxTarget = getRequestCycle().find(AjaxRequestTarget.class);
                confirmDeleteDialog.close(ajaxTarget);
            }
        };
    }

    /**
     * Ban user.
     *
     * @param user
     *            the user
     */
    private void banUser(VIACUser user)
    {
        //TODO Meldung
        if (user.getId() != VIACSession.getInstance().getUserModel().getId())
        {
            if (user.isBanned())
            {
                user.setBanned(false);
            }
            else
            {
                user.setBanned(true);
            }
            DatabaseProvider.getInstance().mergeUser(user);
            dataProvider.reload();
        }
    }

    /**
     * Disables/hides all components that should not be visible if user does not have the required authorizations.
     */
    private void verifyPage()
    {
        authorizations = new ArrayList<>();
        authorizations = VIACSession.getInstance().getUserAuthorizations();

        if (!authorizations.contains("viac_new_user"))
        {
            createNewUserButton.setVisible(false);
        }

        for (VIACButton b : buttons)
        {

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
            else if (b.getButtonType().toString() == "DELETE")
            {
                if (!authorizations.contains("viac_delete_user"))
                {
                    b.setVisible(false);
                }
            }
            else if (b.getButtonType().toString() == "EDIT")
            {
                if (!authorizations.contains("viac_edit_user"))
                {
                    b.setVisible(false);
                }
            }
        }
    }
}
