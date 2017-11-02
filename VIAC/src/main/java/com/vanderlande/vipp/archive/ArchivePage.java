package com.vanderlande.vipp.archive;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.extensions.markup.html.repeater.data.grid.ICellPopulator;
import org.apache.wicket.extensions.markup.html.repeater.data.table.AbstractColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.DataTable;
import org.apache.wicket.extensions.markup.html.repeater.data.table.HeadersToolbar;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.NavigationToolbar;
import org.apache.wicket.extensions.markup.html.repeater.data.table.PropertyColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.filter.FilterForm;
import org.apache.wicket.extensions.markup.html.repeater.data.table.filter.FilterToolbar;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.upload.FileUpload;
import org.apache.wicket.markup.html.link.DownloadLink;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.Model;

import com.vanderlande.util.DatabaseProvider;
import com.vanderlande.util.MultiFileUploadForm;
import com.vanderlande.viac.controls.VIACDownloadActionPanel;
import com.vanderlande.viac.dialog.NotificationDialog;
import com.vanderlande.viac.filter.FilterOption;
import com.vanderlande.viac.filter.VIACFilterPanel;
import com.vanderlande.vipp.VIPPBasePage;
import com.vanderlande.vipp.archive.provider.ArchiveDataProvider;
import com.vanderlande.vipp.archive.provider.ArchiveTableModel;
import com.vanderlande.vipp.model.VIPPApprentice;
import com.vanderlande.vipp.model.VIPPPresentation;
import com.vanderlande.vipp.model.VIPPPresentationCycle;
/**
 * The Class ArchivePage.
 * 
 * @author dermic
 * 
 *         used to display the important data of a given presentationCycle.
 */
public class ArchivePage extends VIPPBasePage implements Serializable{
	private static final long serialVersionUID = 3930686100957311346L;
	private VIPPPresentationCycle cycle;
    private ArchiveDataProvider dataProvider;
    private DataTable<ArchiveTableModel, String> tableWithFilterForm;
    private MultiFileUploadForm uploadForm;
    private NotificationDialog notificationDialog;
    private DownloadLink downloadLink;
    
	public ArchivePage()
	{
		init();
	}
	
	public ArchivePage(VIPPPresentationCycle cycle)
	{
		this.cycle = cycle;
		init();
	}
	
	private void init()
	{
		setOutputMarkupPlaceholderTag(true);
		Form<String> form = new Form<String>("form");
        List<FilterOption> filters = new ArrayList<>();
        filters.add(new FilterOption("Vorname", "firstname"));
        filters.add(new FilterOption("Nachname", "lastname"));
        filters.add(new FilterOption("Ausbildungsjahr", "yearOfApprenticeship"));
        filters.add(new FilterOption("Ausbildungsberuf", "apprenticeshipArea"));
        filters.add(new FilterOption("Gruppe", "group"));
        filters.add(new FilterOption("Thema", "subject"));

        dataProvider = new ArchiveDataProvider(filters, cycle);

        tableWithFilterForm = this.initDataTable(filters);

        FilterForm<ArchiveTableModel> archiveFilterForm = new FilterForm<ArchiveTableModel>("filterForm", dataProvider);
        FilterToolbar filterToolbar = new FilterToolbar(tableWithFilterForm, archiveFilterForm);
        tableWithFilterForm.addTopToolbar(filterToolbar);
        tableWithFilterForm.addTopToolbar(new HeadersToolbar<>(tableWithFilterForm, dataProvider));
        tableWithFilterForm.addBottomToolbar(new NavigationToolbar(tableWithFilterForm));
        archiveFilterForm.add(tableWithFilterForm);

        VIACFilterPanel<ArchiveTableModel> filterPanel =
            new VIACFilterPanel<>("filterPanel", dataProvider, tableWithFilterForm);
        archiveFilterForm.add(filterPanel);
        
        form.add(archiveFilterForm);
        add(form);
        
        uploadForm = new MultiFileUploadForm("uploadForm")
        {
            /** The Constant serialVersionUID. */
            private static final long serialVersionUID = -1679965729560874496L;

            @Override
            protected void onSubmit()
            {
                uploadPresentations();

            }
        };
        
        // upload button
        AjaxButton uploadButton = new AjaxButton("uploadButton")
        {
            /** The Constant serialVersionUID. */
            private static final long serialVersionUID = 8541385679979075416L;

            @Override
            public void onSubmit(AjaxRequestTarget target, Form<?> form)
            {
                super.onSubmit();
            }
        };
        
        uploadForm.add(uploadButton);
        add(uploadForm);		
        
        notificationDialog = new NotificationDialog("notificationDialog");
        notificationDialog.setCloseButtonCallback(new ModalWindow.CloseButtonCallback()
        {
            /** The Constant serialVersionUID. */
            private static final long serialVersionUID = 5613838020934203498L;

            @Override
            public boolean onCloseButtonClicked(AjaxRequestTarget target)
            {
                target.add(ArchivePage.this);
                return true;
            }
        });
        add(notificationDialog);
	}
	private DataTable<ArchiveTableModel, String> initDataTable(List<FilterOption> filters)
	{
		List<IColumn<ArchiveTableModel, String>> columns = new ArrayList<>();
		columns.add(new PropertyColumn<ArchiveTableModel, String>(new Model<>("Vorname"), "firstname", "firstname"));
		columns.add(new PropertyColumn<ArchiveTableModel, String>(new Model<>("Nachname"), "lastname", "lastname"));
		columns.add(new PropertyColumn<ArchiveTableModel, String>(new Model<>("Ausbildungsjahr"), "yearOfApprenticeship", "yearOfApprenticeship"));
		columns.add(new PropertyColumn<ArchiveTableModel, String>(new Model<>("Ausbildungsberuf"), "apprenticeshipArea", "apprenticeshipArea"));
		columns.add(new PropertyColumn<ArchiveTableModel, String>(new Model<>("Gruppe"), "group", "group"));
		columns.add(new PropertyColumn<ArchiveTableModel, String>(new Model<>("Thema"), "subject", "subject"));
        columns.add(new AbstractColumn<ArchiveTableModel, String>(new Model<>("Präsentation"))
        {
            /** The Constant serialVersionUID. */
            private static final long serialVersionUID = 7453935826814920470L;

            @Override
            public void populateItem(Item<ICellPopulator<ArchiveTableModel>> cellItem, String componentId,
                                     final IModel<ArchiveTableModel> rowModel)
            {
            	final VIPPPresentation present = rowModel.getObject().getPresentation();            	

                if (present != null)
                {
                    downloadLink = new DownloadLink("download", new LoadableDetachableModel<File>()
                    {
                        /** The Constant serialVersionUID. */
                        private static final long serialVersionUID = 1L;

                        @Override
                        protected File load()
                        {
                            final File file = new File(present.getFileName());
                            try
                            {
                                FileUtils.writeByteArrayToFile(
                                    new File(present.getFileName()),
                                    present.getData());
                                return file;
                            }
                            catch (IOException e)
                            {
                                return null;
                            }
                        }
                    });

                    Label fileName = new Label("navItemLabel", present.getFileName());
                    downloadLink.add(fileName);

                    // delete link
                    AjaxLink<String> deleteLink = new AjaxLink<String>("deleteLink")
                    {
                        /** The Constant serialVersionUID. */
                        private static final long serialVersionUID = -5367520491398976029L;

                        @Override
                        public void onClick(AjaxRequestTarget target)
                        {
                            DatabaseProvider.getInstance().delete(present);
                            setResponsePage(new ArchivePage(cycle));
                        }
                    };
                    cellItem.add(new VIACDownloadActionPanel<>(componentId, rowModel, downloadLink, deleteLink));
                }
                else
                {
                    cellItem.add(new Label(componentId, ""));
                }
            }
        });

		DataTable<ArchiveTableModel, String> dataTable =
				new DataTable<ArchiveTableModel, String>("tableWithFilterForm", columns, dataProvider, 100);
		
		dataTable.setOutputMarkupId(true);
		
		return dataTable;
	}
	
	private void uploadPresentations()
	{
        // this list contains all files that cannot be matched
        List<FileUpload> corruptedUploads = new ArrayList<FileUpload>();
        for (FileUpload upload : uploadForm.getUploads())
        {
            boolean fileCouldBeMatched = false;
            // Create a new file
            File newFile = new File(upload.getClientFileName());

            // Check new file, delete if it already existed
            uploadForm.checkFileExists(newFile);
            try
            {
                upload.writeTo(newFile);

                Path path = Paths.get(newFile.getPath());
                VIPPPresentation document = new VIPPPresentation();
                String fileName = upload.getClientFileName();
                String apprenticeFirstname = fileName.split("_")[1];
                String apprenticeLastname = fileName.split("_")[2];
                List<VIPPApprentice> apprentices = DatabaseProvider.getInstance().getAll(VIPPApprentice.class);
                for (VIPPApprentice vippApprentice : apprentices)
                {
                    if (!fileCouldBeMatched)
                    {
                        if (apprenticeFirstname.equals(vippApprentice.getFirstname()) && apprenticeLastname.contains(vippApprentice.getLastname()))
                        {
                            fileCouldBeMatched = true;
                            try
                            {
                                File pdfFile = new File(path.toString());
                                byte[] pdfData = new byte[(int) pdfFile.length()];
                                DataInputStream dis = new DataInputStream(new FileInputStream(pdfFile));
                                dis.readFully(pdfData);
                                dis.close();

                                document.setApprentice(vippApprentice);
                                document.setPresentationCycle(cycle);
                                document.setData(pdfData);
                                document.setFileName(fileName);
                                document.setFileType(upload.getContentType());
                                DatabaseProvider.getInstance().create(document);
                            }
                            catch (Exception e)
                            {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
            if (!fileCouldBeMatched)
            {
                corruptedUploads.add(upload);
            }
        }
        String listOfCorruptedUploads = "";
        for (FileUpload upload : corruptedUploads)
        {
            listOfCorruptedUploads += upload.getClientFileName() + ", ";
        }
        if (corruptedUploads.size() > 0)
        {
            listOfCorruptedUploads = listOfCorruptedUploads.substring(0, listOfCorruptedUploads.length() - 2);
            notificationDialog.showMessage(
                "Folgende Dateien konnten nicht hinzugefügt werden: " + listOfCorruptedUploads,
                getRequestCycle().find(AjaxRequestTarget.class));
        }
        else
        {
            setResponsePage(
                new ArchivePage(cycle));
        }
    }
}
