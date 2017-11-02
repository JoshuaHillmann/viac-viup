package com.vanderlande.viac.roles;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Check;
import org.apache.wicket.markup.html.form.CheckGroup;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.PropertyModel;

import com.vanderlande.util.DatabaseProvider;
import com.vanderlande.viac.VIACBasePage;
import com.vanderlande.viac.model.VIACAuthorization;
import com.vanderlande.viac.model.VIACRole;

/**
 * The Class RoleEditingPage.
 * 
 * @author desczu, denmuj
 * 
 *         You can edit a specific role on this page (change name+authorizations).
 */
public class RoleEditingPage extends VIACBasePage implements Serializable
{

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = -2570745307703309543L;

    /** The role. */
    final VIACRole role;

    /**
     * Instantiates a new role editing page.
     *
     * @param role
     *            the role
     */
    public RoleEditingPage(VIACRole role)
    {
        this.role = role;
        init();
    }

    /**
     * Inits the {@link RoleEditingPage}
     */
    private void init()
    {
        List<VIACAuthorization> access = DatabaseProvider.getInstance().getAll(VIACAuthorization.class);
        List<VIACAuthorization> accessRightsVita = new ArrayList<>();
        List<VIACAuthorization> accessRightsViac = new ArrayList<>();
        List<VIACAuthorization> accessRightsOther = new ArrayList<>();
        for (VIACAuthorization a : access)
        {
            if (a.getAuthCheckName().contains("vita"))
            {
                accessRightsVita.add(a);
            }
            else if (a.getAuthCheckName().contains("viac"))
            {
                accessRightsViac.add(a);
            }
            else
            {
                accessRightsOther.add(a);
            }
        }
        Form<?> form = new Form("form");
        final TextField<String> roleName = new TextField<String>("roleName", new PropertyModel<String>(role, "name"));
        roleName.setRequired(true);

        final List<VIACAuthorization> selectedAccessRights = new ArrayList<>();
        final List<VIACAuthorization> selectedAccessRights2 = new ArrayList<>();
        final List<VIACAuthorization> selectedAccessRights3 = new ArrayList<>();
        final List<VIACAuthorization> selectedAccessRightsOther = new ArrayList<>();
        if (role.getAuthorizations() != null)
        {
            selectedAccessRights.addAll(role.getAuthorizations());
        }
        for (VIACAuthorization a : selectedAccessRights)
        {
            if (a.getAuthCheckName().contains("vita"))
            {
                selectedAccessRights3.add(a);
            }
            else if (a.getAuthCheckName().contains("viac"))
            {
                selectedAccessRights2.add(a);
            }
            else
            {
                selectedAccessRightsOther.add(a);
            }
        }

        CheckGroup<VIACAuthorization> group = new CheckGroup<VIACAuthorization>("group", selectedAccessRights2);
        form.add(group);
        ListView<VIACAuthorization> authorizations = new ListView<VIACAuthorization>("authorizations", accessRightsViac)
        {
            private static final long serialVersionUID = 1L;

            protected void populateItem(ListItem<VIACAuthorization> item)
            {
                item.add(new Check<VIACAuthorization>("checkbox", item.getModel()));
                item.add(new Label("label", item.getModel().getObject().getName()));
            }
        };
        authorizations.setReuseItems(true);
        group.add(authorizations);
        CheckGroup<VIACAuthorization> group2 = new CheckGroup<VIACAuthorization>("group2", selectedAccessRights3);
        form.add(group2);
        ListView<VIACAuthorization> authorizations2 =
            new ListView<VIACAuthorization>("authorizations2", accessRightsVita)
            {
                private static final long serialVersionUID = 1L;

                protected void populateItem(ListItem<VIACAuthorization> item)
                {
                    item.add(new Check<VIACAuthorization>("checkbox2", item.getModel()));
                    item.add(new Label("label2", item.getModel().getObject().getName()));
                }
            };
        authorizations2.setReuseItems(true);
        group2.add(authorizations2);

        CheckGroup<VIACAuthorization> group3 = new CheckGroup<VIACAuthorization>("group3", selectedAccessRightsOther);
        form.add(group3);
        ListView<VIACAuthorization> authorizations3 =
            new ListView<VIACAuthorization>("authorizations3", accessRightsOther)
            {
                private static final long serialVersionUID = 1L;

                protected void populateItem(ListItem<VIACAuthorization> item)
                {
                    item.add(new Check<VIACAuthorization>("checkbox3", item.getModel()));
                    item.add(new Label("label3", item.getModel().getObject().getName()));
                }
            };
        authorizations3.setReuseItems(true);
        group3.add(authorizations3);

        TextArea<String> roleDescriptionInput =
            new TextArea<String>("roleDescriptionInput", new PropertyModel<String>(role, "description"));
        form.add(roleDescriptionInput);

        Button button = new Button("submit")
        {
            @Override
            public void onSubmit()
            {
                super.onSubmit();
                Set<VIACAuthorization> selectedAccessRightsSet = new HashSet<>();
                selectedAccessRightsSet.addAll(selectedAccessRights2);
                selectedAccessRightsSet.addAll(selectedAccessRights3);
                selectedAccessRightsSet.addAll(selectedAccessRightsOther);
                role.setAuthorizations(selectedAccessRightsSet);

                DatabaseProvider.getInstance().merge(role);
                setResponsePage(RoleAdministrationPage.class);
            }
        };
        Link<String> backButton = new Link<String>("back")
        {
            private static final long serialVersionUID = 1L;

            @Override
            public void onClick()
            {
                setResponsePage(new RoleAdministrationPage());
            }
        };
        add(backButton);

        form.add(button);
        form.add(roleName);
        add(form);
    }
}
