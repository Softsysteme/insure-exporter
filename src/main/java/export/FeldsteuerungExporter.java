package export;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import insure.tools.feldsteuerung.persistence.FeldsteuerungService;
import insure.tools.feldsteuerung.persistence.entities.Context;
import insure.tools.feldsteuerung.persistence.entities.ContextType;
import insure.tools.feldsteuerung.persistence.entities.Field;
import insure.tools.feldsteuerung.persistence.entities.FieldConfiguration;
import insure.tools.feldsteuerung.persistence.entities.Form;
import utils.EPackageNsUriFinder;

public class FeldsteuerungExporter extends AbstractExporter {

    private String identifierName;
    private String feldsteuerungSystemName;

    public FeldsteuerungExporter(String ecorePath, FeldsteuerungService service, String identifierName, String feldsteuerungName, String outputXmlName) {
        super(ecorePath, service, outputXmlName);
        this.feldsteuerungSystemName = feldsteuerungName;
        this.identifierName = identifierName;
    }

    @Override
    public void setDocumentHead() {
        domainData += "<?xml version=" + "\"1.0\"" + "encoding=" + "\"UTF-8\"" + "?>" + "\n";
        domainData += "<core:RootRepository xmi:version = " + "\"2.0\"" + " xmlns:xmi = " + "\"http://www.omg.org/XMI\"" + " xmlns:xsi = " + "\"http://www.w3.org/2001/XMLSchema-instance\"";
        for (Map.Entry<String, String> entry : uriFinder.getPrefix2NsUri().entrySet()) {
            domainData += "  xmlns:" + entry.getKey() + "=" + "\"" + entry.getValue() + "\"";
        }

        domainData += " " + "name = " + "\"" + "exportedinfoservicereference" + "\"" + " beschreibung = " + "\"" + "Datenmodell des infoservice fuer exportierten Referenzdomaene" + "\"" + ">" + "\n";
    }

    @Override
    public void setDocumentBody() {
        this.setEnumerationsFields();
        this.setEnumerationsContext();
        this.setPrototypeFieldsCharacteristics();
    }

    @Override
    public void setDocumentFooter() {
        domainData += "</core:RootRepository>";
    }

    public void setEnumerationsContext() {
        FeldsteuerungService feldsteuerungService = (FeldsteuerungService) service;
        for (int i = 0; i < feldsteuerungService.getContextTypes().size(); i++) {
            ContextType contextType = feldsteuerungService.getContextTypes().get(i);
            domainData += "  <repositories modelElementId=" + "\"" + contextType.getUuid() + "\"" + " name =" + "\"" + "kontexte" + "\"" + " pattern =" + "\""
                    + contextType.getName() + "\"" + ">" + "\n";
            for (int j = 0; j < feldsteuerungService.getContexts().size(); j++) {
                Context next = feldsteuerungService.getContexts().get(j);
                if (next.getType().getUuid().contentEquals(contextType.getUuid())) {
                    String findNsUri = findNsUri(next.getType().getName(), getUriFinder());
                    if (findNsUri != null) {
                        domainData +=
                                "    <enumerations xsi:type = " + "\"" + findNsUri + ":" + next.getType().getName() + "\"" + " modelElementId = " + "\"" + next.getUuid() + "\"" + " name = " + "\""
                                        + next.getAlias()
                                        + "\"" + " beschreibung = " + "\"" + next.getName() + "\"" + "/>" + "\n";
                    }
                }
            }
            domainData += "  </repositories>" + "\n";
        }

    }

    public void setEnumerationsFields() {
        List<Field> fieldList = ((FeldsteuerungService) service).getFields();
        domainData +=
                "  <repositories modelElementId = " + fieldList.hashCode() + " name = " + "\"Felderdefinition\"" + " pattern = " + "\"" + "Repository|Eingabeelement|Steuerelement" + "\"" + ">" + "\n";
        Iterator<?> iterField = fieldList.iterator();
        while (iterField.hasNext()) {
            Field next = (Field) iterField.next();
            String fieldtype;
            switch (next.getMetadata().getType().name()) {
                case "BUTTON": {
                    fieldtype = "Steuerelement";
                    break;
                }
                default: {
                    fieldtype = "Eingabeelement";
                    break;
                }
            }
            String findNsUri = findNsUri(fieldtype, getUriFinder());
            if (findNsUri != null)
                domainData += "   <enumerations xsi:type = " + "\"" + findNsUri + ":" + fieldtype + "\"" + " modelElementId = " + "\"" + next.getUuid() + "\"" + " name = " + "\"" + next.getName()
                        + "\"" + "\"" + "/>" + "\n";
        }
        domainData += "  </repositories>" + "\n";

    }

    public void setPrototypeFieldsCharacteristics() {
        domainData += "  <repositories modelElementId = " + "\"_f_ZT0BsOEeWeoYGlWqgNWQ\"" + " name = " + "\"feldelementeigenschaften\"" + " pattern = "
                + "\"Repository|Feldelementeigenschaften|StandardFeldelementeigenschaften|TemplateFeldelementeigenschaften" + "\"" + ">"
                + "\n";
        FeldsteuerungService feldsteuerungService = (FeldsteuerungService) service;
        for (int i = 0; i < feldsteuerungService.getForms().size(); i++) {
            for (int j = 0; j < feldsteuerungService.getContexts().size(); j++) {
                Form form = feldsteuerungService.getForms().get(i);
                Context context = feldsteuerungService.getContexts().get(j);
                List<FieldConfiguration> configList = feldsteuerungService.getFieldConfigurations(form, context);
                if (configList != null) {
                    domainData +=
                            "  <prototypes xsi:type =" + "\"" + "feldsteuerung" + ":" + "Feldelementeigenschaften" + "\"" + " modelElementId=" + "\"" + form.getUuid() + context.getUuid() + "\""
                                    + "\"" + " name = "
                                    + "\""
                                    + context.getName()
                                    + "\"" + "/>" + "\n";

                    Iterator<FieldConfiguration> iterConfig = configList.iterator();
                    while (iterConfig.hasNext()) {
                        FieldConfiguration nextConfig = iterConfig.next();
                        Field field = nextConfig.getField();
                        switch (field.getMetadata().getType().name()) {
                            case "BUTTON": {
                                domainData += "     <steuerelementeigenschaften key = " + "\"" + field.getUuid() + "\"" + "/>" + "\n";
                                if (nextConfig.isRendered()) {
                                    domainData +=
                                            "      <value href = " + "\"" + "platform:/plugin/insure.infoservice.daten/src/main/resources/infoservice.insure#_W312d9RiEeSM0uBkjWoRCg" + "\"" + "/>"
                                                    + "\n";
                                } else {
                                    domainData +=
                                            "      <value href = " + "\"" + "platform:/plugin/insure.infoservice.daten/src/main/resources/infoservice.insure#_dP-dTNRiEeSM0uBkjWoRCg" + "\"" + "/>"
                                                    + "\n";
                                }
                                domainData += "     </steuerelementeigenschaften>" + "\n";
                                break;
                            }
                            default: {
                                domainData += "     <engabeelementeigenschaften key = " + "\"" + field.getUuid() + "\"" + "/>" + "\n";
                                if (nextConfig.isRendered()) {
                                    if (nextConfig.isEditable()) {
                                        if (nextConfig.isRequired()) {
                                            domainData +=
                                                    "      <value href = " + "\"" + "platform:/plugin/insure.infoservice.daten/src/main/resources/infoservice.insure#_2ASSStO_EeSVoOolBb6ZYQ" + "\""
                                                            + "/>"
                                                            + "\n";
                                        } else {
                                            domainData +=
                                                    "      <value href = " + "\"" + "platform:/plugin/insure.infoservice.daten/src/main/resources/infoservice.insure#_zaTuitO_EeSVoOolBb6ZYQ" + "\""
                                                            + "/>"
                                                            + "\n";
                                        }
                                    } else {
                                        domainData +=
                                                "      <value href = " + "\"" + "platform:/plugin/insure.infoservice.daten/src/main/resources/infoservice.insure#_u9rLKtO_EeSVoOolBb6ZYQ" + "\"" + "/>"
                                                        + "\n";
                                    }
                                } else {
                                    if (nextConfig.isEditable()) {
                                        if (nextConfig.isRequired()) {
                                            domainData +=
                                                    "      <value href=" + "\"" + "platform:/plugin/insure.infoservice.daten/src/main/resources/infoservice.insure#__hrI-tO_EeSVoOolBb6ZYQ" + "\""
                                                            + "/>"
                                                            + "\n";
                                        } else {
                                            domainData +=
                                                    "      <value href = " + "\"" + "platform:/plugin/insure.infoservice.daten/src/main/resources/infoservice.insure#_8DP6StO_EeSVoOolBb6ZYQ" + "\""
                                                            + "/>"
                                                            + "\n";
                                        }
                                    } else {
                                        domainData +=
                                                "      <value href = " + "\"" + "platform:/plugin/insure.infoservice.daten/src/main/resources/infoservice.insure#_5vjZWtO_EeSVoOolBb6ZYQ" + "\"" + "/>"
                                                        + "\n";
                                    }
                                }
                                domainData += "     </eingabeelementeigenschaften>" + "\n";
                                break;
                            }
                        }
                    }
                    domainData += "    </prototypes>" + "\n";
                }
            }

        }

        domainData += "    <prototypes xsi:type = " + "\"" + "feldsteuerung" + ":" + "Feldsteuerung" + "\"" + " modelElementId = " + "\"" + feldsteuerungService.hashCode() + "\"" + " name = " + "\""
                + feldsteuerungSystemName
                + "\"" + "identifier = " + "\"" + this.identifierName.hashCode() + "\"" + "/>" + "\n";
        for (int i = 0; i < feldsteuerungService.getForms().size(); i++) {
            for (int j = 0; j < feldsteuerungService.getContexts().size(); j++) {
                Form form = feldsteuerungService.getForms().get(i);
                Context context = feldsteuerungService.getContexts().get(j);
                List<FieldConfiguration> configList = feldsteuerungService.getFieldConfigurations(form, context);
                if (configList != null) {
                    domainData += "     <feldelementeigenschaften  value = " + "\"" + form.getUuid() + context.getUuid() + "\"" + "  key = " + "\""
                            + context.getUuid() + "\"" + "/>" + "\n";
                }

            }
        }
        domainData += "    </prototypes>" + "\n";
        domainData += "    <enumerations xsi:type = " + "\"" + "feldsteuerung:FeldsteuerungIdentifier" + "\"" + " modelElementId = " + "\"" + this.identifierName.hashCode() + "\"" + " name = " + "\""
                + this.identifierName
                + "\"" + "/>" + "\n";
        domainData += "  </repositories>" + "\n";
    }

    public FeldsteuerungService getService() {
        return (FeldsteuerungService) service;
    }

    public void setService(FeldsteuerungService service) {
        this.service = service;
    }

    public String getEcorePath() {
        return ecorePath;
    }

    public void setEcorePath(String ecorePath) {
        this.ecorePath = ecorePath;
    }

    public EPackageNsUriFinder getUriFinder() {
        return uriFinder;
    }

    public void setUriFinder(EPackageNsUriFinder uriFinder) {
        this.uriFinder = uriFinder;
    }

    public List<String> getReferencedEcoreFilePaths() {
        return referencedEcoreFilePaths;
    }

    public void setReferencedEcoreFilePaths(List<String> referencedEcoreFilePaths) {
        this.referencedEcoreFilePaths = referencedEcoreFilePaths;
    }

    public List<String> getAllPaths() {
        return allPaths;
    }

    public void setAllPaths(List<String> allPaths) {
        this.allPaths = allPaths;
    }

}
