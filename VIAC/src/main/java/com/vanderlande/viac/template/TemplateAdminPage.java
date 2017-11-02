package com.vanderlande.viac.template;

import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.extensions.markup.html.repeater.data.grid.ICellPopulator;
import org.apache.wicket.extensions.markup.html.repeater.data.table.AbstractColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.DataTable;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.PropertyColumn;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

import com.vanderlande.viac.VIACBasePage;
import com.vanderlande.viac.model.MailTemplate;
import com.vanderlande.viac.session.VIACSession;
import com.vanderlande.viac.template.model.TemplateHelper;
import com.vanderlande.viac.template.model.TemplateTableModel;
import com.vanderlande.viac.template.provider.TemplateDataProvider;

/**
 * Admin template management page.
 * 
 * @author dedhor
 * 
 *         Displays an overview of all available templates. The user can preview the template and click on an editbutton
 *         for each template.
 */
public class TemplateAdminPage extends VIACBasePage
{

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 5547828241705634006L;

    /**
     * Instantiates a new template admin page.
     */
    public TemplateAdminPage()
    {
        super();
    }

    /**
     * Override default init function. Fill the site with the template table and data.
     */
    @Override
    protected void initialize()
    {
        super.initialize();

        //table with the fields: id, html, (edit, preview)		
        List<IColumn<TemplateTableModel, String>> columns = new ArrayList<>();
        final TemplateDataProvider dataProvider = new TemplateDataProvider();

        final ModalWindow window = new ModalWindow("PreviewWindow");
        add(window);

        columns.add(new PropertyColumn<TemplateTableModel, String>(new Model<>("ID"), "id", "id"));
        columns.add(new PropertyColumn<TemplateTableModel, String>(new Model<>("Text"), "text", "text"));
        columns.add(new AbstractColumn<TemplateTableModel, String>(new Model<>("Aktionen"))
        {
            private static final long serialVersionUID = -229895030474389747L;

            @Override
            public void populateItem(Item<ICellPopulator<TemplateTableModel>> cellItem, String componentId,
                                     IModel<TemplateTableModel> rowModel)
            {

                final MailTemplate template = rowModel.getObject().getTemplate();

                //implement edit button
                Link<String> editLink = new Link<String>("edit")
                {
                    private static final long serialVersionUID = 123L;

                    @Override
                    public void onClick()
                    {
                        setResponsePage(new TemplateAdminEditPage(template));
                    }
                };

                //implement preview button
                AjaxLink<String> previewLink = new AjaxLink<String>("preview")
                {
                    private static final long serialVersionUID = 124L;

                    public void onClick(AjaxRequestTarget target)
                    {
                        window.setTitle("Vorschau");
                        window.setContent(
                            new TemplatePreviewPanel(
                                window.getContentId(),
                                TemplateHelper.getInstance().useTemplate(
                                    template.getMailText(),
                                    VIACSession.getInstance().getUserModel())));
                        window.show(target);
                    }
                };

                cellItem.add(new TemplateActionPanel<TemplateTableModel>(componentId, rowModel, editLink, previewLink));
            }
        });

        DataTable<TemplateTableModel, String> tableTemplates =
            new DataTable<TemplateTableModel, String>("tableTemplates", columns, dataProvider, 100);

        tableTemplates.setOutputMarkupId(true);

        add(tableTemplates);
    }
}