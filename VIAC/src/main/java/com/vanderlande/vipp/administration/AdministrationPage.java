package com.vanderlande.vipp.administration;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.commons.io.FilenameUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.IAjaxIndicatorAware;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.extensions.ajax.markup.html.IndicatingAjaxButton;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.upload.FileUpload;
import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.hwpf.usermodel.Paragraph;
import org.apache.poi.hwpf.usermodel.Range;
import org.apache.poi.hwpf.usermodel.Table;
import org.apache.poi.hwpf.usermodel.TableCell;
import org.apache.poi.hwpf.usermodel.TableRow;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;

import com.vanderlande.util.DatabaseProvider;
import com.vanderlande.util.MultiFileUploadForm;
import com.vanderlande.viac.dialog.NotificationDialog;
import com.vanderlande.vipp.VIPPBasePage;
import com.vanderlande.vipp.model.VIPPApprentice;
import com.vanderlande.vipp.model.VIPPApprenticeshipArea;
import com.vanderlande.vipp.model.VIPPDocument;
import com.vanderlande.vipp.model.VIPPLearningUnit;
import com.vanderlande.vipp.model.VIPPPresentationCycle;
import com.vanderlande.vipp.model.VIPPPresentedOn;
import com.vanderlande.vipp.model.VIPPSubject;
import com.vanderlande.vipp.model.VIPPSubjectArea;

/**
 * The Class AdministrationPage.
 * 
 * @author dermic
 * 
 *         The upload form to import the data of subjects and apprentices.
 */
public class AdministrationPage extends VIPPBasePage implements Serializable
{
	private MultiFileUploadForm fileUploadForm;
	private String path;
	private File file;
	// the notification dialog for files that could not be uploaded
	private NotificationDialog notificationDialog;
	private String listOfCorruptedUploads = "";
	private int wrongFiles = 0;
	private List<VIPPSubject> savedSubjects = new ArrayList<VIPPSubject>();
	private List<String> subjects = new ArrayList<String>();
	
	public AdministrationPage()
	{
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
                target.add(AdministrationPage.this);
                return true;
            }
        });
        add(notificationDialog);
        
		fileUploadForm = new MultiFileUploadForm("fileUploadForm") {
			/** The Constant serialVersionUID. */
			private static final long serialVersionUID = 1L;

			@Override
			protected void onSubmit() {
				uploadFiles();
			}
		};
		
		IndicatingAjaxButton uploadButton = new IndicatingAjaxButton("uploadButton")
        {
            /** The Constant serialVersionUID. */
            private static final long serialVersionUID = 8541385679979075416L;

            @Override
            public void onSubmit(AjaxRequestTarget target, Form<?> form)
            {
                super.onSubmit();
            }
        	@Override
        	public String getAjaxIndicatorMarkupId() {
        		return "indicator";
        	}
        };
        fileUploadForm.add(uploadButton);
        add(fileUploadForm);		
	}
	
	private void uploadFiles()
	{		
		List<FileUpload> corruptedUploads = new ArrayList<FileUpload>();
		// contains the data for the subjects
		List<VIPPDocument> wordDocuments = new ArrayList<VIPPDocument>();
		// contains the data for apprentices and their performed presentations
		List<VIPPDocument> excelDocuments = new ArrayList<VIPPDocument>();
		boolean doubledData = false;
		for (FileUpload upload : fileUploadForm.getUploads()) {
			
			// Create a new file
			File newFile = new File(upload.getClientFileName());
			// Check new file, delete if it already existed
			fileUploadForm.checkFileExists(newFile);
			try {
				upload.writeTo(newFile);
				Path path = Paths.get(newFile.getPath());
								
				if(!FilenameUtils.getExtension(path.toString()).equals("xlsx") && !FilenameUtils.getExtension(path.toString()).equals("doc"))
				{
					corruptedUploads.add(upload);
					wrongFiles += 1;
				}
				else
				{
					VIPPDocument document = new VIPPDocument();
					document.setFileName(upload.getClientFileName());
					document.setFileType(upload.getContentType());
					document.setFile(newFile);
					
					for(VIPPDocument savedDocument : DatabaseProvider.getInstance().getAll(VIPPDocument.class))
					{
						if(savedDocument.getFile().compareTo(newFile) == 0)
						{
							doubledData = true;
						}
					}
					if(!doubledData)
					{
						DatabaseProvider.getInstance().create(document);	
						savedSubjects = DatabaseProvider.getInstance().getAll(VIPPSubject.class);
						subjects = new ArrayList<String>();
						for(VIPPSubject subject : savedSubjects)
						{
							subjects.add(subject.getName());
						}
						if(FilenameUtils.getExtension(path.toString()).equals("xlsx"))
						{
							excelDocuments.add(document);
						}
						else if(FilenameUtils.getExtension(path.toString()).equals("doc"))
						{
							wordDocuments.add(document);
						}						
					}
					else
					{
						listOfCorruptedUploads += document.getFileName() + " (Datei ist bereits vorhanden), ";
						wrongFiles += 1;
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
				throw new IllegalStateException("Unable to write file");
			}
		}
		if(wordDocuments.size() == 0 && excelDocuments.size() == 0 && !doubledData)
		{
			notificationDialog.showMessage(
					"Es wurde keine Datei ausgewählt!",
					getRequestCycle().find(AjaxRequestTarget.class));
		}
		else
		{
			// the subjects have to be read in first, since you need them to match the performed presentations of an apprentice
			for(VIPPDocument wordDocument : wordDocuments)
			{
				readWord(wordDocument);
			}
			for(VIPPDocument excelDocument : excelDocuments)
			{
				// if there are more subjects than 'Freie Themenwahl'
				if(DatabaseProvider.getInstance().getAll(VIPPSubject.class).size() > 1)
				{
					readExcel(excelDocument);
				}
				else
				{
					listOfCorruptedUploads += excelDocument.getFileName() + " (Es müssen zuerst Themen vorhanden sein), ";
					notificationDialog.showMessage(
							"Die Azubis aus " + listOfCorruptedUploads + " konnten nicht eingelesen werden, da zu erst Themen vorhanden sein müssen.",
							getRequestCycle().find(AjaxRequestTarget.class));
					wrongFiles += 1;
				}
			}
			for (FileUpload file : corruptedUploads)
			{
				listOfCorruptedUploads += file.getClientFileName() + " (Falsches Format), ";
				wrongFiles += 1;
			}
			if (wrongFiles > 0)
			{
				listOfCorruptedUploads = listOfCorruptedUploads.substring(0, listOfCorruptedUploads.length() - 2);
				notificationDialog.showMessage(
						"Folgende Dateien konnten nicht hochgeladen werden: " + listOfCorruptedUploads,
						getRequestCycle().find(AjaxRequestTarget.class));
				listOfCorruptedUploads = "";
				wrongFiles = 0;
			}
			else
			{
				setResponsePage(
						new AdministrationPage());
			}
		}
	}
	public void readExcel(VIPPDocument document)
	{
		try{
			FileInputStream file = new FileInputStream(document.getFileName());
			XSSFWorkbook workbook = new XSSFWorkbook(file);
			XSSFSheet sheet = workbook.getSheetAt(0);
			Iterator<Row> rowIterator = sheet.iterator();
			int i = 0;
			VIPPPresentedOn presentedOn;
			// creates a new presentationCycle to be able to get the last subject area for the subject assignment algorithm 
			VIPPPresentationCycle presentationCycle = new VIPPPresentationCycle("letzter eingelesener");
			DatabaseProvider.getInstance().create(presentationCycle);
			/* if the cell type is not the expected type falseFormat will be set to true and the file data will not be imported
			 * cellType 0 = numeric
			 * cellType 1 = String
			 * cellType 3 = blank */
			boolean falseFormat = false;
			List<VIPPApprentice> savedApprentices = DatabaseProvider.getInstance().getAll(VIPPApprentice.class);
			List<String> apprenticeNames = new ArrayList<String>();
			List<VIPPApprentice> addedApprentices = new ArrayList<VIPPApprentice>();
			
			List<VIPPApprenticeshipArea> savedApprenticeshipAreas = DatabaseProvider.getInstance().getAll(VIPPApprenticeshipArea.class);
			List<String> apprenticeshipAreaNames= new ArrayList<String>();
			for(VIPPApprenticeshipArea area : savedApprenticeshipAreas)
			{
				apprenticeshipAreaNames.add(area.getName());
			}
			
			List<VIPPSubject> savedSubjects = DatabaseProvider.getInstance().getAll(VIPPSubject.class);
			List<String> subjectNames = new ArrayList<String>();
			for(VIPPSubject subject : savedSubjects)
			{
				subjectNames.add(subject.getName());
			}
			
			while(rowIterator.hasNext())
			{
				VIPPApprentice apprentice = new VIPPApprentice();
				List<VIPPSubject> subjectList = new ArrayList<VIPPSubject>();
				Row row = rowIterator.next();
				// row counter
				i = i + 1;
				// cell counter
				int j = 0;
				Iterator<Cell> cellIterator = row.cellIterator();
				while(cellIterator.hasNext())
				{
					Cell cell = cellIterator.next();
					// the first row only contains the caption of the cells and is skipped
					if(i != 1)
					{
						switch (j){
						// case 0 to 3 read in the person data of an apprentice
							case 0:
								if(cell.getCellType() == 1 && cell.getStringCellValue() != "")
								{
									apprentice.setFirstname(cell.getStringCellValue());
								}
								else if(cell.getCellType() != 3)
								{
									falseFormat = true;
									break;
								}
								break;
							case 1:
								if(cell.getCellType() == 1 && cell.getStringCellValue() != "")
								{
									apprentice.setLastname(cell.getStringCellValue());
								}
								else if(cell.getCellType() != 3)
								{
									falseFormat = true;
									break;
								}
								break;
							case 2:
								if(cell.getCellType() == 0 && cell.getNumericCellValue() != 0)
								{
									apprentice.setYearOfApprenticeship((int)cell.getNumericCellValue());
								}
								else if(cell.getCellType() != 3)
								{
									falseFormat = true;
									break;
								}
								break;
							case 3:
								if(cell.getCellType() == 1 && cell.getStringCellValue() != "")
								{
									if(apprenticeshipAreaNames.contains(cell.getStringCellValue()))
									{
										for(VIPPApprenticeshipArea area : savedApprenticeshipAreas)
										{
											if(area.getName().equals(cell.getStringCellValue()))
											{
												apprentice.setApprenticeshipArea(area);										
											}
										}
									}
								}
								else if(cell.getCellType() != 3)
								{
									falseFormat = true;
									break;
								}
								break;
							// every other cell contains subjects that the apprentice presented
							default:
								if(cell.getCellType() == 1 && cell.getStringCellValue() != "")
								{
									if(subjectNames.contains(cell.getStringCellValue()))
									{
										for(VIPPSubject subject : savedSubjects)
										{
											if(subject.getName().equals(cell.getStringCellValue()))
											{
												subjectList.add(subject);	
												break;
											}
										}
									}
								}
								else if(cell.getCellType() != 3)
								{
									falseFormat = true;
									break;
								}
								else
								{
									break;
								}
						}
					}
					j = j + 1;
					if(falseFormat)
					{
						break;
					}
				}
				if(i != 1 && !falseFormat)
				{
					// there are blank rows between the different apprentices and an empty firstName indicates that sort of rows
					if(apprentice.getFirstname() != null)
					{
						boolean newApprentice = true;
						for(VIPPApprentice appr : savedApprentices)
						{
							apprenticeNames.add(appr.getFirstname() + " " + appr.getLastname() + " " + appr.getYearOfApprenticeship());
						}
						if(apprenticeNames.contains(apprentice.getFirstname() + " " + apprentice.getLastname() + " " + apprentice.getYearOfApprenticeship()))
						{
							newApprentice = false;
							break;
						}
						if(newApprentice)
						{
							apprentice.setPerformedPresentations(subjectList);
							DatabaseProvider.getInstance().create(apprentice);
							if(subjectList.size() > 0)
							{
								presentedOn = new VIPPPresentedOn(apprentice, subjectList.get(0), presentationCycle);
								DatabaseProvider.getInstance().create(presentedOn);
							}	
							addedApprentices.add(apprentice);
						}
					}
				}
			}
			if(falseFormat)
			{
				listOfCorruptedUploads += document.getFileName() + " (Falscher Inhalt), ";
				wrongFiles += 1;
				DatabaseProvider.getInstance().delete(presentationCycle);
				DatabaseProvider.getInstance().delete(document);
			}
			if(addedApprentices.size() == 0)
			{
				listOfCorruptedUploads += document.getFileName() + " (Daten bereits vorhanden), ";
				wrongFiles += 1;
				DatabaseProvider.getInstance().delete(presentationCycle);
				DatabaseProvider.getInstance().delete(document);
			}
			file.close();
		}
		catch(FileNotFoundException e)
		{
			e.printStackTrace();
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
	}
	public void readWord(VIPPDocument document)
	{
		boolean falseFormat = false;
		List<VIPPSubject> addedSubjects = new ArrayList<VIPPSubject>();
		String fileName = document.getFileName();
		try {
			FileInputStream file = new FileInputStream(new File(fileName));
			POIFSFileSystem fs = new POIFSFileSystem(file);
			HWPFDocument doc = new HWPFDocument(fs);
			VIPPSubject subject;
			
			Range range = doc.getRange();

			Paragraph tablePar = range.getParagraph(0);
			if(tablePar.isInTable())
			{
				Table table = range.getTable(tablePar);
				for(int rowId = 0; rowId < table.numRows(); rowId ++)
				{
					// the first two rows contain captions and are skipped
					if(rowId > 2)
					{
						subject = new VIPPSubject();
						TableRow row = table.getRow(rowId);
	
							for(int colId = 0; colId < row.numCells(); colId++)
							{
								TableCell cell = row.getCell(colId);
								if(row.numCells() == 6)
								{
									// there is a sign to indicate the end of the text field that has to be cut
									String text = cell.getParagraph(0).text().substring(0, cell.getParagraph(0).text().length()-1);
									// if learningUnits and apprenticeshipAreas have multiple values the text gets separated
									List<String> textParts = new ArrayList<String>();
									if(colId != 0 && text.contains("/"))
									{
										textParts = Arrays.asList(text.split("/"));
									}
									if(colId != 0 && text.contains(","))
									{
										textParts = Arrays.asList(text.split("\\s*,\\s*"));
									}
									// the name column
									if(colId == 0)
									{
										subject.setName(text);
									}
									// the learningUnits column
									else if(colId == 1)
									{
										List<VIPPLearningUnit> learningUnits = new ArrayList<VIPPLearningUnit>();
										List<VIPPLearningUnit> allLearningUnits = DatabaseProvider.getInstance().getAll(VIPPLearningUnit.class);
										
										if(textParts.size() != 0)
										{
											for(String part : textParts)
											{
												for(VIPPLearningUnit vippLearningUnit : allLearningUnits)
												{
													if(vippLearningUnit.getNumber().equals(part))
													{
														learningUnits.add(vippLearningUnit);
													}
												}
											}
										}
										else
										{
											for(VIPPLearningUnit vippLearningUnit : allLearningUnits)
											{
												if(vippLearningUnit.getNumber().equals(text))
												{
													learningUnits.add(vippLearningUnit);
												}
											}
										}
										subject.setLearningUnit(learningUnits);
									}
									// the apprenticeshipAreas column
									else if(colId == 2)
									{
										List<VIPPApprenticeshipArea> allAreas = DatabaseProvider.getInstance().getAll(VIPPApprenticeshipArea.class);
										Set<VIPPApprenticeshipArea> areas = new HashSet<VIPPApprenticeshipArea>();
										if(textParts.size() != 0)
										{
											for(String part : textParts)
											{
												for(VIPPApprenticeshipArea vippApprenticeshipArea : allAreas)
												{
													if(vippApprenticeshipArea.getName().equals(part))
													{
														areas.add(vippApprenticeshipArea);
													}
												}
											}
										}
										if(text.equals("Alle"))
										{
											areas.addAll(allAreas);
										}
										else
										{
											for(VIPPApprenticeshipArea vippApprenticeshipArea : allAreas)
											{
												if(vippApprenticeshipArea.getName().equals(text))
												{
													areas.add(vippApprenticeshipArea);
												}
											}										
										}
										subject.setApprenticeshipArea(areas);
										if(subject.getApprenticeshipArea().size() == 0)
										{
											falseFormat = true;
										}
									}
									// the subjectAreas column
									else if(colId == 3)
									{
										List<VIPPSubjectArea> areas = DatabaseProvider.getInstance().getAll(VIPPSubjectArea.class);
										for(VIPPSubjectArea area : areas)
										{
											if(area.getName().equals(text))
											{
												subject.setSubjectArea(area);
											}
										}
										if(subject.getSubjectArea() == null)
										{
											falseFormat = true;
										}
									}
									//the 4th column is empty
									//the isFinalExamRelevant column
									else if(colId == 5)
									{
										if(text.equals("x"))
										{
											subject.setIsFinalExamRelevant(true);
										}
										else
										{
											subject.setIsFinalExamRelevant(false);
										}
									}
								}
								else if(rowId > 2 && row.numCells() != 1 && row.numCells() != 6)
								{
									falseFormat = true;
									break;
								}
							}	
						if(subject.getName() != null && !falseFormat)
						{
							boolean doubledSubject = false;
							for(String name : subjects)
							{
								if(subjects.contains(subject.getName()))
								{
									doubledSubject = true;
									break;
								}
							}								
							if(!doubledSubject)
							{
								DatabaseProvider.getInstance().create(subject);		
								addedSubjects.add(subject);
							}
						}
						if(falseFormat)
						{
							listOfCorruptedUploads += document.getFileName() + " (Falscher Inhalt), ";
							wrongFiles += 1;
							DatabaseProvider.getInstance().delete(document);
							break;
						}
					}
				}
			}
			if(addedSubjects.size() == 0)
			{
				listOfCorruptedUploads += document.getFileName() + " (Daten bereits vorhanden), ";
				wrongFiles += 1;
				DatabaseProvider.getInstance().delete(document);
			}
		} 
		catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
