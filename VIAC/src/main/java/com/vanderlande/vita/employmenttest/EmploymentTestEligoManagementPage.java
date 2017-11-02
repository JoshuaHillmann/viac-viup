package com.vanderlande.vita.employmenttest;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.upload.FileUpload;
import org.apache.wicket.markup.html.link.DownloadLink;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.panel.Fragment;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.RepeatingView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.util.time.Duration;

import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfImportedPage;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfWriter;
import com.vanderlande.util.CSVUtils;
import com.vanderlande.util.DatabaseProvider;
import com.vanderlande.util.MultiFileUploadForm;
import com.vanderlande.viac.controls.VIACDownloadActionPanel;
import com.vanderlande.viac.dialog.NotificationDialog;
import com.vanderlande.viac.model.VIACConfiguration;
import com.vanderlande.viac.session.VIACSession;
import com.vanderlande.viac.userManagement.password.PasswordHandler;
import com.vanderlande.vita.VITABasePage;
import com.vanderlande.vita.employmenttest.provider.TestApplicantsDataProvider;
import com.vanderlande.vita.model.VITAApplicant;
import com.vanderlande.vita.model.VITAApplicantStatus;
import com.vanderlande.vita.model.VITACareer;
import com.vanderlande.vita.model.VITADocument;
import com.vanderlande.vita.model.VITAEligoTest;

/**
 * The Class EmploymentTestEligoManagementPage.
 * 
 * @author desczu, dekscha
 * 
 *         This class shows and generates all eligo-relevant information of the test-applicants. You can download
 *         information-cards (containing firstname, lastname, eligo-name and eligo-password) for the applicants. You can
 *         also download the .csv-file to register the testgroup on the eligo-website. When the user has the
 *         eligo-test-results he can upload them here in a multi-upload form -> the files will automatically be attached
 *         to the correct applicant
 */
public class EmploymentTestEligoManagementPage extends VITABasePage
{
    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 111933073363413099L;

    /** The test group. */
    private VITAEligoTest testGroup;

    /** The download pdf link. */
    private DownloadLink downloadPdfLink;

    /** The download link. */
    private DownloadLink downloadLink;

    /** The notification dialog. */
    private NotificationDialog notificationDialog;

    /** The eligo file upload form. */
    private MultiFileUploadForm eligoFileUploadForm;

    /** The applicants. */
    private Set<VITAApplicant> applicants;

    /**
     * Instantiates a new employment test eligo management page.
     *
     * @param testGroup
     *            the test group
     * @param antiCache
     *            the anti cache
     */
    public EmploymentTestEligoManagementPage(VITAEligoTest testGroup, String antiCache)
    {
        this.testGroup = testGroup;
        applicants = new HashSet<VITAApplicant>();
        for (VITAApplicant applicant : testGroup.getTestApplicants())
        {
            if (applicant.getStatus().equals(DatabaseProvider.getInstance().getAll(VITAApplicantStatus.class).get(2)) ||
                applicant.getStatus().equals(DatabaseProvider.getInstance().getAll(VITAApplicantStatus.class).get(0)))
            {
                applicants.add(applicant);
            }
        }
        init();
    }

    /**
     * Inits the EmploymentTestEligoManagementPage.
     */
    private void init()
    {
        generateEligoData();
        final TestApplicantsDataProvider testApplicantsDataProvider = new TestApplicantsDataProvider(testGroup);
        final Form<VITAEligoTest> form = new Form<VITAEligoTest>("form");

        // notification-dialog
        notificationDialog = new NotificationDialog("notificationDialog");
        notificationDialog.setCloseButtonCallback(new ModalWindow.CloseButtonCallback()
        {
            /** The Constant serialVersionUID. */
            private static final long serialVersionUID = 5613838020934203498L;

            @Override
            public boolean onCloseButtonClicked(AjaxRequestTarget target)
            {
                target.add(EmploymentTestEligoManagementPage.this);
                return true;
            }
        });
        add(notificationDialog);

        // repeating view
        RepeatingView navigationContainer = new RepeatingView("navContainer");
        form.add(navigationContainer);
        navigationContainer.setOutputMarkupId(true);

        // careers
        Set<VITACareer> careers = new HashSet<>();
        for (VITAApplicant applicant : applicants)
        {
            careers.add(applicant.getCareer());
        }

        for (final VITACareer vitaCareer : careers)
        {
            final List<VITAApplicant> applicantsWithCareer = new ArrayList<>();
            for (VITAApplicant vitaApplicant : applicants)
            {
                if (vitaApplicant.getCareer().equals(vitaCareer))
                {
                    applicantsWithCareer.add(vitaApplicant);
                }
            }
            String fileName =
                "ELIGO_" + testGroup.getName().replaceAll(" ", "_") + "_" + vitaCareer.getName().replaceAll(" ", "_") +
                              "_" + PasswordHandler.getInstance().getRandomPassword(5) + ".csv";

            final File eligoFile = createCSVFile(applicantsWithCareer, fileName);
            downloadLink = new DownloadLink("download", new LoadableDetachableModel<File>()
            {
                /** The Constant serialVersionUID. */
                private static final long serialVersionUID = 1L;

                @Override
                protected File load()
                {
                    return eligoFile;
                }
            });

            final Fragment fragment = new Fragment(navigationContainer.newChildId(), "fragment", this);
            fragment.setOutputMarkupId(true);
            final Label fileNameLabel = new Label("navItemLabel", "Eligo Daten " + vitaCareer.getName());

            fragment.add(downloadLink);
            downloadLink.add(fileNameLabel);
            navigationContainer.add(fragment);
        }

        // download pdf
        downloadPdfLink = new DownloadLink("downloadPdf", new LoadableDetachableModel<File>()
        {
            /** The Constant serialVersionUID. */
            private static final long serialVersionUID = 1L;

            @Override
            protected File load()
            {
                return generateEligoPDF();
            }
        });
        downloadPdfLink.setDeleteAfterDownload(true);
        downloadPdfLink.setCacheDuration(Duration.NONE);

        // back button
        Link<String> backButton = new Link<String>("back")
        {
            /** The Constant serialVersionUID. */
            private static final long serialVersionUID = 1L;

            @Override
            public void onClick()
            {
                setResponsePage(new EmploymentTestDetailsPage(testGroup));
            }
        };
        add(backButton);

        // test-applicants table
        List<IColumn<VITAApplicant, String>> testApplicantsColumns = new ArrayList<>();
        testApplicantsColumns
            .add(new PropertyColumn<VITAApplicant, String>(new Model<>("Vorname"), "firstname", "firstname"));
        testApplicantsColumns
            .add(new PropertyColumn<VITAApplicant, String>(new Model<>("Nachname"), "lastname", "lastname"));
        testApplicantsColumns
            .add(new PropertyColumn<VITAApplicant, String>(new Model<>("Status"), "status.name", "status.name"));
        testApplicantsColumns.add(
            new PropertyColumn<VITAApplicant, String>(new Model<>("Eligo-Username"), "eligoUsername", "eligoUsername"));
        testApplicantsColumns.add(new AbstractColumn<VITAApplicant, String>(new Model<>("Eligo-Testergebnis"))
        {
            /** The Constant serialVersionUID. */
            private static final long serialVersionUID = 7453935826814920470L;

            @Override
            public void populateItem(Item<ICellPopulator<VITAApplicant>> cellItem, String componentId,
                                     final IModel<VITAApplicant> rowModel)
            {
                // find the eligo-document of the applicant and - if found -
                // show a downloadlink and a delete-button
                // for this document
                Set<VITADocument> documents = rowModel.getObject().getDocuments();
                VITADocument eligoTestDocumentTemp = null;
                for (final VITADocument vitaDocument : documents)
                {
                    if (vitaDocument.isEligo())
                    {
                        eligoTestDocumentTemp = vitaDocument;
                    }
                }

                // eligo test document
                final VITADocument eligoTestDocument = eligoTestDocumentTemp;
                if (eligoTestDocument != null)
                {
                    downloadLink = new DownloadLink("download", new LoadableDetachableModel<File>()
                    {
                        /** The Constant serialVersionUID. */
                        private static final long serialVersionUID = 1L;

                        @Override
                        protected File load()
                        {
                            final File file = new File(eligoTestDocument.getFileName());
                            try
                            {
                                FileUtils.writeByteArrayToFile(
                                    new File(eligoTestDocument.getFileName()),
                                    eligoTestDocument.getData());
                                return file;
                            }
                            catch (IOException e)
                            {
                                return null;
                            }
                        }
                    });

                    Label fileName = new Label("navItemLabel", eligoTestDocument.getFileName());
                    downloadLink.add(fileName);

                    // delete link
                    AjaxLink<String> deleteLink = new AjaxLink<String>("deleteLink")
                    {
                        /** The Constant serialVersionUID. */
                        private static final long serialVersionUID = -5367520491398976029L;

                        @Override
                        public void onClick(AjaxRequestTarget target)
                        {
                            DatabaseProvider.getInstance().deleteDocument(eligoTestDocument);
                            target.add(EmploymentTestEligoManagementPage.this);
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

        // table
        DataTable<VITAApplicant, String> testApplicantsTable = new DataTable<VITAApplicant, String>(
            "testApplicantsTable",
            testApplicantsColumns,
            testApplicantsDataProvider,
            5);
        testApplicantsTable.setOutputMarkupId(true);
        testApplicantsTable.addTopToolbar(new HeadersToolbar<>(testApplicantsTable, testApplicantsDataProvider));
        testApplicantsTable.addBottomToolbar(new NavigationToolbar(testApplicantsTable));

        form.add(downloadPdfLink);
        form.add(testApplicantsTable);
        add(form);

        // eligo file upload
        eligoFileUploadForm = new MultiFileUploadForm("eligoFileUploadForm")
        {
            /** The Constant serialVersionUID. */
            private static final long serialVersionUID = -1679965729560874496L;

            @Override
            protected void onSubmit()
            {
                uploadEligoResults();

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

        // add components to form
        eligoFileUploadForm.add(uploadButton);
        add(eligoFileUploadForm);
    }

    /**
     * Generates the eligo data. Every applicant gets the username "Teilnehmer"+ a number which is increased for every
     * applicant (eligoCounter). The applicant only gets new data when he has no eligo-username, so eligo-data is only
     * generated once for every applicant. The password is created randomly (8 digits)
     */
    private void generateEligoData()
    {
        DatabaseProvider provider = DatabaseProvider.getInstance();
        VIACConfiguration configuration = provider.getConfigByKey("eligoCounter");
        if (configuration.getValue().isEmpty())
        {
            configuration = new VIACConfiguration("eligoCounter", "1");
            provider.create(configuration);
        }

        long counter = Long.parseLong(configuration.getValue());
        int i = 0;
        for (VITAApplicant vitaApplicant : applicants)
        {
            if (vitaApplicant.getEligoUsername() == null || vitaApplicant.getEligoUsername().length() == 0)
            {
                long temp = counter + i;
                vitaApplicant.setEligoFirstname("Vorname" + temp);
                vitaApplicant.setEligoLastname("Nachname" + temp);
                vitaApplicant.setEligoPassword(PasswordHandler.getInstance().getRandomPassword(8).toUpperCase());
                vitaApplicant.setEligoUsername("Teilnehmer" + temp);
                provider.merge(vitaApplicant);
                i++;
            }
        }
        counter += applicants.size();
        configuration.setValue(String.valueOf(counter));
        provider.merge(configuration);
    }

    /**
     * Creates the CSV file for the given applicants.
     *
     * @param applicants
     *            the applicants
     * @param fileName
     *            the file name
     * @return the csv file
     */
    private File createCSVFile(List<VITAApplicant> applicants, String fileName)
    {
        try
        {
            FileWriter writer = new FileWriter(fileName);

            CSVUtils.writeLine(
                writer,
                Arrays.asList(
                    "NAME",
                    "NACHNAME",
                    "BENUTZERNAME/EMAIL",
                    "PASSWORT",
                    "GESCHLECHT",
                    "LAND",
                    "SPRACHE",
                    "GEBURTSJAHR",
                    "BILDUNGSGRAD",
                    "BEMERKUNG",
                    "TESTTYPE",
                    "EMAIL",
                    "GESPERRT_AB",
                    "GESPERRT_BIS"),
                ';');

            for (VITAApplicant va : applicants)
            {
                CSVUtils.writeLine(
                    writer,
                    Arrays.asList(
                        va.getEligoFirstname(),
                        va.getEligoLastname(),
                        va.getEligoUsername(),
                        va.getEligoPassword(),
                        "X",
                        "GER",
                        "GE",
                        "1900",
                        "XX",
                        "",
                        "E",
                        "kristine.schroeder-kunze@vanderlande.com",
                        "",
                        ""),
                    ';');
            }
            writer.flush();
            writer.close();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        File eligoData = new File(fileName);
        return eligoData;
    }

    /**
     * Generates the information-cards for every applicant (containing firstname, lastname, eligo-username and
     * eligo-password).
     *
     * @return the file
     */
    private File generateEligoPDF()
    {
        Document document = new Document(PageSize.A4, 0, 0, 0, 0);
        File pdf = new File("Eligo_" + testGroup.getName() + ".pdf");
        try
        {
            FileOutputStream os = new FileOutputStream(pdf);
            PdfWriter.getInstance(document, os);
            document.open();
            List<String> data = new ArrayList<String>();
            for (VITAApplicant vitaApplicant : applicants)
            {
                data.add(
                    "Vorname: " + vitaApplicant.getFirstname() + "\n\nNachname: " + vitaApplicant.getLastname() +
                         "\n\nNutzername: " + vitaApplicant.getEligoUsername() + "\n\nPasswort: " +
                         vitaApplicant.getEligoPassword());
            }

            document.add(new Chunk(""));
            document.add(createPdfTable(data, 2, 3, document.getPageSize().getHeight()));
            document.close();
        }
        catch (IOException | DocumentException e)
        {
            e.printStackTrace();
        }
        return pdf;
    }

    /**
     * Generates the front-pages for the results.
     *
     * @param applicantModel
     *            the applicant model
     * @return the file
     */
    private File generateEligoFrontPDF(VITAApplicant applicantModel)
    {
        Document document = new Document(PageSize.A4);
        File pdf = new File("Eligo_Deckblatt_" + applicantModel.getEligoUsername() + ".pdf");
        try
        {
            FileOutputStream os = new FileOutputStream(pdf);
            PdfWriter.getInstance(document, os);
            document.open();

            document.newPage();
            document.add(new Paragraph("Vorname: " + applicantModel.getFirstname()));
            document.add(new Paragraph("Nachname: " + applicantModel.getLastname()));
            document.add(new Paragraph("Geburtstag: " + applicantModel.getBirthday()));
            document.add(new Paragraph("Nutzername: " + applicantModel.getEligoUsername()));
            document.add(new Paragraph("Testdatum: " + testGroup.getDate()));

            document.close();
        }
        catch (IOException | DocumentException e)
        {
            e.printStackTrace();
        }
        return pdf;
    }

    /**
     * Creates a table for the information-card pdf-file.
     *
     * @param data
     *            the data
     * @param columnCount
     *            the column count
     * @param rowCount
     *            the row count
     * @param tableHeight
     *            the table height
     * @return the pdf P table
     */
    private PdfPTable createPdfTable(List<String> data, int columnCount, int rowCount, float tableHeight)
    {
        PdfPTable table = new PdfPTable(columnCount);
        table.setWidthPercentage(100);
        PdfPCell cell;
        float cellHeight = tableHeight / rowCount;
        for (String s : data)
        {
            cell = new PdfPCell(new Phrase(s));
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setMinimumHeight(cellHeight);
            table.addCell(cell);
        }

        int modulo = data.size() % columnCount;
        if (modulo != 0)
        {
            int missingData = columnCount - modulo;

            for (int i = 0; i < missingData; i++)
            {
                cell = new PdfPCell(new Phrase(""));
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                cell.setMinimumHeight(cellHeight);
                table.addCell(cell);
            }
        }
        return table;
    }

    /**
     * Merge multiple pdf into one pdf.
     *
     * @param list
     *            of pdf input stream
     * @param outputStream
     *            output file output stream
     * @throws DocumentException
     *             the document exception
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    public void mergePDFs(List<InputStream> list, OutputStream outputStream)
        throws DocumentException, IOException
    {
        Document document = new Document();
        PdfWriter writer = PdfWriter.getInstance(document, outputStream);
        document.open();
        PdfContentByte cb = writer.getDirectContent();

        for (InputStream in : list)
        {
            PdfReader reader = new PdfReader(in);
            for (int i = 1; i <= reader.getNumberOfPages(); i++)
            {
                document.newPage();
                // import the page from source pdf
                PdfImportedPage page = writer.getImportedPage(reader, i);
                // add the page to the destination pdf
                cb.addTemplate(page, 0, 0);
            }
        }

        outputStream.flush();
        document.close();
        outputStream.close();
    }

    /**
     * Uploads the eligo-test results and automatically attaches them to the correct applicant. If a file cannot be
     * attached a warning shows up. The file has to match the pattern Eligo_ELIGOUSERNAME.pdf.
     */
    private void uploadEligoResults()
    {
        // this list contains all files that cannot be matched
        List<FileUpload> corruptedUploads = new ArrayList<FileUpload>();
        for (FileUpload upload : eligoFileUploadForm.getUploads())
        {
            boolean fileCouldBeMatched = false;
            // Create a new file
            File newFile = new File(upload.getClientFileName());

            // Check new file, delete if it already existed
            eligoFileUploadForm.checkFileExists(newFile);
            try
            {
                upload.writeTo(newFile);

                Path path = Paths.get(newFile.getPath());
                // byte[] data = java.nio.file.Files.readAllBytes(path);

                VITADocument document = new VITADocument();
                // Eligo_EligoUsername.pdf
                String fileName = upload.getClientFileName();
                // EligoUsername.pdf
                String eligoUsername = fileName.split("_")[1];
                for (VITAApplicant applicant : applicants)
                {
                    if (!fileCouldBeMatched)
                    {
                        if (eligoUsername.contains(applicant.getEligoUsername()))
                        {
                            fileCouldBeMatched = true;
                            testGroup.getTestApplicants().remove(applicant);

                            File frontPage = generateEligoFrontPDF(applicant);
                            List<InputStream> list = new ArrayList<InputStream>();
                            try
                            {
                                // Source pdfs
                                list.add(new FileInputStream(frontPage));
                                list.add(new FileInputStream(new File(path.toString())));
                                // Resulting pdf
                                OutputStream out = new ByteArrayOutputStream();
                                mergePDFs(list, out);
                                ByteArrayOutputStream bout = (ByteArrayOutputStream)out;

                                document.setApplicant(applicant);
                                document.setEligo(true);
                                document.setCreatedBy(VIACSession.getInstance().getUserModel());
                                document.setData(bout.toByteArray());
                                document.setFileName(fileName);
                                document.setFileType(upload.getContentType());
                                DatabaseProvider.getInstance().create(document);
                                applicant.getDocuments().add(document);
                                testGroup.getTestApplicants().add(applicant);
                                DatabaseProvider.getInstance().merge(applicant);
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
                new EmploymentTestEligoManagementPage(testGroup, PasswordHandler.getInstance().getRandomPassword(10)));
        }
    }
}
