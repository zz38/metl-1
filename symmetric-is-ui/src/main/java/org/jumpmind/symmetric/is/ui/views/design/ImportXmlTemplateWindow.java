package org.jumpmind.symmetric.is.ui.views.design;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.xml.XMLConstants;
import javax.xml.namespace.QName;
import javax.xml.transform.stream.StreamResult;

import jlibs.xml.sax.XMLDocument;
import jlibs.xml.xsd.XSInstance;
import jlibs.xml.xsd.XSParser;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.xerces.xs.XSConstants;
import org.apache.xerces.xs.XSModel;
import org.apache.xerces.xs.XSNamedMap;
import org.jdom2.Document;
import org.jdom2.input.SAXBuilder;
import org.jdom2.input.sax.XMLReaders;
import org.jumpmind.symmetric.is.ui.views.design.ChooseWsdlServiceOperationWindow.ServiceChosenListener;
import org.reficio.ws.builder.SoapBuilder;
import org.reficio.ws.builder.SoapOperation;
import org.reficio.ws.builder.core.Wsdl;
import org.vaadin.aceeditor.AceEditor;
import org.vaadin.aceeditor.AceMode;

import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Label;
import com.vaadin.ui.OptionGroup;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.Upload;
import com.vaadin.ui.Upload.Receiver;
import com.vaadin.ui.Upload.SucceededEvent;
import com.vaadin.ui.Upload.SucceededListener;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.ValoTheme;

@SuppressWarnings("serial")
public class ImportXmlTemplateWindow extends Window implements ValueChangeListener, ClickListener, Receiver, SucceededListener {

    private static String OPTION_TEXT = "Text";
    
    private static String OPTION_FILE = "File";
    
    private static String OPTION_URL = "URL";
    
    VerticalLayout optionLayout;
    
    OptionGroup optionGroup;
    
    AceEditor editor;
    
    Upload upload;
    
    TextField urlTextField;
    
    ByteArrayOutputStream uploadedData;
    
    ImportXmlListener listener;

    public ImportXmlTemplateWindow(ImportXmlListener listener) {
        this.listener = listener;
        setCaption("Import XML Template");
        setModal(true);
        setWidth(600.0f, Unit.PIXELS);
        setHeight(500.0f, Unit.PIXELS);

        VerticalLayout layout = new VerticalLayout();
        layout.setSpacing(true);
        layout.setMargin(true);
        layout.addStyleName(ValoTheme.WINDOW_BOTTOM_TOOLBAR);
        setContent(layout);

        layout.addComponent(new Label("Import XML from either an XSD or WSDL source."));
        
        optionGroup = new OptionGroup("Select the location of the XSD or WSDL.");
        optionGroup.addItem(OPTION_TEXT);
        optionGroup.addItem(OPTION_FILE);
        optionGroup.addItem(OPTION_URL);
        optionGroup.setNullSelectionAllowed(false);
        optionGroup.setImmediate(true);
        optionGroup.select(OPTION_TEXT);
        optionGroup.addValueChangeListener(this);
        layout.addComponent(optionGroup);
        
        optionLayout = new VerticalLayout();
        editor = new AceEditor();
        editor.setCaption("Enter the XML text:");
        editor.setMode(AceMode.xml);
        editor.setWidth(100f, Unit.PERCENTAGE);
        editor.setHighlightActiveLine(true);
        editor.setShowPrintMargin(false);

        upload = new Upload(null, this);
        upload.addSucceededListener(this);
        upload.setButtonCaption(null);
        urlTextField = new TextField("Enter the URL:");
        urlTextField.setWidth(100.0f, Unit.PERCENTAGE);
        layout.addComponent(optionLayout);
        layout.setExpandRatio(optionLayout, 1.0f);
        rebuildOptionLayout();
        
        Button importButton = new Button("Import");
        importButton.addClickListener(this);
        layout.addComponent(importButton);
    }

    protected void rebuildOptionLayout() {
        optionLayout.removeAllComponents();
        if (optionGroup.getValue().equals(OPTION_TEXT)) {
            optionLayout.addComponent(editor);
            editor.focus();
        } else if (optionGroup.getValue().equals(OPTION_FILE)) {
            optionLayout.addComponent(upload);
            upload.focus();
        } else if (optionGroup.getValue().equals(OPTION_URL)) {
            optionLayout.addComponent(urlTextField);
            urlTextField.focus();
        }        
    }

    @Override
    public void valueChange(ValueChangeEvent event) {
        rebuildOptionLayout();
    }

    @Override
    public void uploadSucceeded(SucceededEvent event) {
        importXml(new String(uploadedData.toByteArray()));
    }

    @Override
    public OutputStream receiveUpload(String filename, String mimeType) {
        return uploadedData = new ByteArrayOutputStream();
    }

    @Override
    public void buttonClick(ClickEvent event) {
        if (optionGroup.getValue().equals(OPTION_TEXT)) {
            importXml(editor.getValue());
        } else if (optionGroup.getValue().equals(OPTION_FILE)) {
            upload.submitUpload();
        } else if (optionGroup.getValue().equals(OPTION_URL)) {
            InputStream in = null;
            String text = null;
            try {
                in = new URL(urlTextField.getValue()).openStream();
                text = IOUtils.toString(in);
            } catch (Exception e) {
                throw new RuntimeException(e);
            } finally {
                IOUtils.closeQuietly(in);
            }
            importXml(text);
        }
    }

    protected void importXml(String text) {
        SAXBuilder builder = new SAXBuilder();
        builder.setXMLReaderFactory(XMLReaders.NONVALIDATING);
        builder.setFeature("http://xml.org/sax/features/validation", false);
        try {
            Document document = builder.build(new StringReader(text));
            String rootName = document.getRootElement().getName();
            if (rootName.equals("definitions")) {
                importFromWsdl(text);
            } else if (rootName.equals("schema")) {
                importFromXsd(text);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    protected void importFromXsd(String text) throws Exception {
        XSModel xsModel = new XSParser().parseString(text, "");

        XSInstance xsInstance = new XSInstance();
        xsInstance.minimumElementsGenerated = 1;
        xsInstance.maximumElementsGenerated = 1;
        xsInstance.generateOptionalElements = Boolean.TRUE;

        XSNamedMap map = xsModel.getComponents(XSConstants.ELEMENT_DECLARATION);

        QName rootElement = new QName(map.item(0).getNamespace(), map.item(0).getName(),
                XMLConstants.DEFAULT_NS_PREFIX);
        StringWriter writer = new StringWriter();
        XMLDocument sampleXml = new XMLDocument(new StreamResult(writer), true, 4, null);
        xsInstance.generate(xsModel, rootElement, sampleXml);

        String xml = writer.toString();
        listener.onImport(xml);
    }

    protected void importFromWsdl(String text) throws Exception {
        File wsdlFile = File.createTempFile("import", "wsdl");
        FileUtils.write(wsdlFile, text);
        final Wsdl wsdl = Wsdl.parse(wsdlFile.toURI().toURL());
        List<SoapOperation> allOperations = new ArrayList<>();
        List<QName> bindings = wsdl.getBindings();
        for (QName binding : bindings) {
            SoapBuilder builder = wsdl.getBuilder(binding);
            List<SoapOperation> operations = builder.getOperations();
            allOperations.addAll(operations);
        }
        
        ChooseWsdlServiceOperationWindow dialog = new ChooseWsdlServiceOperationWindow(
                allOperations, new ServiceChosenListener() {
            public boolean onOk(SoapOperation operation) {
                try {
                    SoapBuilder builder = wsdl.getBuilder(operation.getBindingName());
                    String xml = builder.buildInputMessage(operation);
                    listener.onImport(xml);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
                return true;
            }
        });
        UI.getCurrent().addWindow(dialog);
    }

    public static interface ImportXmlListener extends Serializable {
        public void onImport(String xml);
    }

}
