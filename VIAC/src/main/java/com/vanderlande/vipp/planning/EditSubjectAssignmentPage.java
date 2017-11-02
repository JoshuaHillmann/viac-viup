package com.vanderlande.vipp.planning;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.OnChangeAjaxBehavior;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.extensions.ajax.markup.html.IndicatingAjaxButton;
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
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.link.DownloadLink;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.Model;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.vanderlande.util.DatabaseProvider;
import com.vanderlande.viac.dialog.NotificationDialog;
import com.vanderlande.viac.filter.FilterOption;
import com.vanderlande.viac.filter.VIACFilterPanel;
import com.vanderlande.vipp.VIPPBasePage;
import com.vanderlande.vipp.archive.ArchivePage;
import com.vanderlande.vipp.model.VIPPApprentice;
import com.vanderlande.vipp.model.VIPPApprenticeshipArea;
import com.vanderlande.vipp.model.VIPPGroup;
import com.vanderlande.vipp.model.VIPPPresentationCycle;
import com.vanderlande.vipp.model.VIPPPresentedOn;
import com.vanderlande.vipp.model.VIPPSubject;
import com.vanderlande.vipp.planning.data.ApprenticeSubjectTableModel;
import com.vanderlande.vipp.planning.provider.SortableApprenticeSubjectDataProvider;
/**
 * The Class EditSubjectAssignmentPage.
 * 
 * @author dermic
 *       
 */
public class EditSubjectAssignmentPage extends VIPPBasePage implements Serializable{

	private static final long serialVersionUID = -9054994392995203364L;

	private boolean archive = true;
    
    private NotificationDialog notificationDialog;
    
    private List<VIPPSubject> subjectsAvailableAll = DatabaseProvider.getInstance().getAllAvailableSubjects(1);
    
    private DataTable<ApprenticeSubjectTableModel, String> tableWithFilterForm;
    
    private DownloadLink generatePDFButton;
    
    public IndicatingAjaxButton submitButton;
    
    private GregorianCalendar c = new GregorianCalendar();
    
    private List<VIPPPresentationCycle> cycles = DatabaseProvider.getInstance().getAll(VIPPPresentationCycle.class);
    private VIPPPresentationCycle cycle = cycles.get(cycles.size()-1);
    
    /** The data provider. */
    private SortableApprenticeSubjectDataProvider dataProvider;
    
	public EditSubjectAssignmentPage()
	{
		init();
	}
	
	public EditSubjectAssignmentPage(List<VIPPSubject> subjectsAvailableAll) {
		this.subjectsAvailableAll = subjectsAvailableAll;
		init();
	}

	private void init()
	{
        notificationDialog = new NotificationDialog("notificationDialog");
        notificationDialog.setCloseButtonCallback(new ModalWindow.CloseButtonCallback()
        {
            /** The Constant serialVersionUID. */
            private static final long serialVersionUID = 5613838020934203498L;

            @Override
            public boolean onCloseButtonClicked(AjaxRequestTarget target)
            {
                target.add(EditSubjectAssignmentPage.this);
                return true;
            }
        });
        add(notificationDialog);
		
		List<VIPPApprentice> apprentices = DatabaseProvider.getInstance().getAll(VIPPApprentice.class);
		// if there is an apprentice without a subject the user is not allowed to archive this presentationCycle and the button will be disabled
		for(VIPPApprentice apprentice :  apprentices)
		{
			if(apprentice.getCurrentGroup() != null && apprentice.getAssignedSubject() == null)
			{
				archive = false;
			}
		}
		Form<String> form = new Form<String>("form");
        List<FilterOption> filters = new ArrayList<>();
        filters.add(new FilterOption("Vorname", "firstname"));
        filters.add(new FilterOption("Nachname", "lastname"));
        filters.add(new FilterOption("Ausbildungsjahr", "yearOfApprenticeship"));
        filters.add(new FilterOption("Ausbildungsberuf", "apprenticeshipArea"));
        filters.add(new FilterOption("Gruppe", "group"));
        filters.add(new FilterOption("Thema", "subject"));

        dataProvider = new SortableApprenticeSubjectDataProvider(filters);

        tableWithFilterForm = this.initDataTable(filters);

        submitButton = new IndicatingAjaxButton("submit")
        {
            /** The Constant serialVersionUID. */
            private static final long serialVersionUID = -4037454663308029262L;

            @Override
            public void onSubmit(AjaxRequestTarget target, Form<?> form)
            {
            	Iterator<? extends ApprenticeSubjectTableModel> iterator = tableWithFilterForm.getDataProvider().iterator(0, tableWithFilterForm.getDataProvider().size());
            	while(iterator.hasNext())
                {
                	VIPPApprentice apprentice = iterator.next().getApprentice();  
                	DatabaseProvider.getInstance().merge(apprentice);
                }
            	
            	setResponsePage(new EditSubjectAssignmentPage(subjectsAvailableAll));
            }
            @Override
            public String getAjaxIndicatorMarkupId() {
                         return "indicator";
            }
        };

        FilterForm<ApprenticeSubjectTableModel> apprenticeFilterForm = new FilterForm<ApprenticeSubjectTableModel>("filterForm", dataProvider);
        FilterToolbar filterToolbar = new FilterToolbar(tableWithFilterForm, apprenticeFilterForm);
        tableWithFilterForm.addTopToolbar(filterToolbar);
        tableWithFilterForm.addTopToolbar(new HeadersToolbar<>(tableWithFilterForm, dataProvider));
        tableWithFilterForm.addBottomToolbar(new NavigationToolbar(tableWithFilterForm));
        apprenticeFilterForm.add(tableWithFilterForm);

        VIACFilterPanel<ApprenticeSubjectTableModel> filterPanel =
            new VIACFilterPanel<>("filterPanel", dataProvider, tableWithFilterForm);
        apprenticeFilterForm.add(filterPanel);
        
        generatePDFButton = new DownloadLink("generatePDF", new LoadableDetachableModel<File>()
        {
            /** The Constant serialVersionUID. */
            private static final long serialVersionUID = 1L;

            @Override
            protected File load()
            {
                return generatePDF();

            }
        });
        
        IndicatingAjaxButton test = new IndicatingAjaxButton("archiveButton") {
			@Override
			public void onSubmit(AjaxRequestTarget target, Form<?> form) {
				List<VIPPApprentice> apprentices = DatabaseProvider.getInstance().getAll(VIPPApprentice.class);
				VIPPApprentice apprentice = apprentices.get(0);
				VIPPGroup group = apprentice.getCurrentGroup();
				VIPPPresentationCycle cycle = group.getCycle();
				for(VIPPApprentice vippApprentice : apprentices)
				{
					VIPPSubject subject;
					subject = vippApprentice.getAssignedSubject();
					if(subject != null)
					{
						subject.setLastPresentedBy(vippApprentice);
						subject.setPresentationCycle(cycle);
						vippApprentice.getPerformedPresentations().add(vippApprentice.getAssignedSubject());
						vippApprentice.setCurrentGroup(null);						
					}
					VIPPPresentedOn presentedOn = new VIPPPresentedOn(vippApprentice, subject, cycle);
					DatabaseProvider.getInstance().create(presentedOn);
					vippApprentice.setAssignedSubject(null);
					DatabaseProvider.getInstance().merge(vippApprentice);
				}
            	setResponsePage(new ArchivePage(cycle));
			}
            @Override
            public String getAjaxIndicatorMarkupId() {
                         return "indicator";
            }
		};
		
        
        form.add(apprenticeFilterForm);
        form.add(generatePDFButton);
        form.add(test);
        if(!archive)
        {
        	test.setEnabled(false);
        }
        form.add(submitButton);
        add(form);
	}
	
    private DataTable<ApprenticeSubjectTableModel, String> initDataTable(List<FilterOption> filters)
    {
        List<IColumn<ApprenticeSubjectTableModel, String>> columns = new ArrayList<>();
        columns.add(new PropertyColumn<ApprenticeSubjectTableModel, String>(new Model<>("Vorname"), "firstname", "firstname"));
        columns.add(new PropertyColumn<ApprenticeSubjectTableModel, String>(new Model<>("Nachname"), "lastname", "lastname"));
        columns.add(new PropertyColumn<ApprenticeSubjectTableModel, String>(new Model<>("Ausbildungsjahr"), "yearOfApprenticeship", "yearOfApprenticeship"));
        columns.add(new PropertyColumn<ApprenticeSubjectTableModel, String>(new Model<>("Ausbildungsberuf"), "apprenticeshipArea", "apprenticeshipArea"));
        columns.add(new PropertyColumn<ApprenticeSubjectTableModel, String>(new Model<>("Gruppe"), "group", "group"));
        columns.add(new PropertyColumn<ApprenticeSubjectTableModel, String>(new Model<>("Thema"), "subject", "subject"));
        columns.add(new AbstractColumn<ApprenticeSubjectTableModel, String>(new Model<>("Thema ändern"))
        {
			@Override
			public void populateItem(Item<ICellPopulator<ApprenticeSubjectTableModel>> cellItem, String componentId,
					IModel<ApprenticeSubjectTableModel> rowModel) {
				
				final VIPPApprentice apprentice = rowModel.getObject().getApprentice();
			    final DropdownPanelSubject panel = new DropdownPanelSubject(componentId, subjectsAvailableAll, apprentice);
				
				panel.subjectChoice.add(new OnChangeAjaxBehavior() {
					private static final long serialVersionUID = 1L;
					// indicates whether the apprentice already presented the chosen subject
			    	boolean again;
			    	// indicates whether there is already an apprentice that has this subject assigned
			    	boolean doubled;
			    	boolean area;
			    	// indicates whether the subject is relevant for the apprenticeshipArea of this apprentice 
			    	boolean wrongArea;
			    	// indicates whether an apprentice of the same group already presented that subject
			    	boolean groupSubject;
			    	String unmetConditions = "";
					@Override
					protected void onUpdate(AjaxRequestTarget target) {
						apprentice.setAssignedSubject(panel.subjectChoice.getModelObject());
						for(VIPPSubject subject : apprentice.getPerformedPresentations())
						{
							if(subject.equals(apprentice.getAssignedSubject()))
							{
								again = true;
								unmetConditions += apprentice.getAssignedSubject().getName() + " wurde von dem Azubi bereits präsentiert, ";
								break;
							}
						}
						for(VIPPApprentice vippApprentice : DatabaseProvider.getInstance().getAll(VIPPApprentice.class))
						{
							if(vippApprentice.getAssignedSubject() != null)
							{
								if(apprentice != vippApprentice && apprentice.getAssignedSubject().getName().equals(vippApprentice.getAssignedSubject().getName()))
								{
									doubled = true;
									unmetConditions += vippApprentice.getFirstname() + " " + vippApprentice.getLastname() + " hat dieses Thema bereits zugewiesen bekommen, ";
									break;
								}								
							}
						}
						for(VIPPApprenticeshipArea vippArea : apprentice.getAssignedSubject().getApprenticeshipArea())
						{
							if(vippArea.equals(apprentice.getApprenticeshipArea()))
							{
								area = true;
								break;
							}
						}
						if(!area)
						{
							wrongArea = true;
							unmetConditions += apprentice.getAssignedSubject().getName() + " ist nicht relevant für den Azubi, ";
						}
						for(VIPPApprentice vippApprentice : apprentice.getCurrentGroup().getPersons())
						{
							for(VIPPSubject vippSubject : vippApprentice.getPerformedPresentations())
							{
								if(vippSubject.equals(apprentice.getAssignedSubject()))
								{
									groupSubject = true;
									unmetConditions += apprentice.getAssignedSubject().getName() + " wurde bereits von einem Gruppenmitglied präsentiert, ";
								}
							}
						}
						if(wrongArea || doubled || again || groupSubject)
						{
							unmetConditions = unmetConditions.substring(0, unmetConditions.length() - 2);
							notificationDialog.showMessage(
									"Information zu dem manuell zugewiesenen Thema: " + unmetConditions,
									getRequestCycle().find(AjaxRequestTarget.class));
						}
					}
				});
				cellItem.add(panel);
			}

        });
        DataTable<ApprenticeSubjectTableModel, String> dataTable =
            new DataTable<ApprenticeSubjectTableModel, String>("tableWithFilterForm", columns, dataProvider, 100);
        
        dataTable.setOutputMarkupId(true);

        return dataTable;
    }
    
    private File generatePDF()
    {
        Document document = new Document(PageSize.A4, 0, 0, 0, 0);
        String presentationCycleName = "";
        if (cycle.getName() != null)
        {
            presentationCycleName = cycle.getName();
        }
        File pdf = new File(
            "Präsentationsplanung_" + presentationCycleName + ".pdf");
        try
        {
            // Variables
            FileOutputStream os = new FileOutputStream(pdf);
            PdfWriter.getInstance(document, os);

            BaseFont bf, bf_b;
            Font font_heading, font_info;
            PdfPTable table;
            PdfPCell cell;
            String build_text;
            Date date = Calendar.getInstance().getTime();
            SimpleDateFormat sdf_date = new SimpleDateFormat("dd.MM.yyyy");
            SimpleDateFormat sdf_time = new SimpleDateFormat("HH:mm");

            // Font Variables
            bf = BaseFont.createFont("c:/windows/fonts/arial.ttf", BaseFont.WINANSI, BaseFont.EMBEDDED);
            bf_b = BaseFont.createFont("c:/windows/fonts/arialbd.ttf", BaseFont.WINANSI, BaseFont.EMBEDDED);
            font_heading = new Font(bf_b, 16);
            font_info = new Font(bf, 12);

            document.open();
            
            Paragraph p = new Paragraph("Planung für den Präsentationsdurchlauf " + presentationCycleName, font_heading);
            p.setAlignment(Element.ALIGN_CENTER);
            Paragraph p2 = new Paragraph("Angefordert am: " + c.get(Calendar.DAY_OF_MONTH) + "." + c.get(Calendar.MONTH) + "." + c.get(Calendar.YEAR), font_info);
            p2.setAlignment(Element.ALIGN_CENTER);
            
            document.add(new Paragraph(" "));
            document.add(p);
            document.add(p2);
            document.add(new Paragraph(" "));
            
            List<VIPPApprentice> apprentices = DatabaseProvider.getInstance().getAll(VIPPApprentice.class);
            
            table = new PdfPTable(new float[] {2, 1, 1, 1, 1});
            
            PdfPCell c1 = new PdfPCell(new Phrase("Thema", font_info));
            PdfPCell c2 = new PdfPCell(new Phrase("Azubi", font_info));
            PdfPCell c3 = new PdfPCell(new Phrase("Gruppe", font_info));
            PdfPCell c4 = new PdfPCell(new Phrase("Geplant für", font_info));
            PdfPCell c5 = new PdfPCell(new Phrase("Durchgeführt am", font_info));

            table.setWidthPercentage(80);
            table.addCell(c1);
            table.addCell(c2);
            table.addCell(c3);
            table.addCell(c4);
            table.addCell(c5);
            
            for(VIPPApprentice vippApprentice : apprentices)
            {
            	if(vippApprentice.getAssignedSubject() != null)
            	{
	            	table.addCell(vippApprentice.getAssignedSubject().getName());
	            	table.addCell(vippApprentice.getFirstname() + " " + vippApprentice.getLastname());
	            	table.addCell(vippApprentice.getCurrentGroup().getName());
	            	table.addCell("");
	            	table.addCell("");
            	}
            }
            
            document.add(table);
            
            document.close();
        }
        catch (IOException | DocumentException e)
        {
            e.printStackTrace();
        }

        return pdf;
    }
    
}
