package export;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import insure.tools.feldsteuerung.persistence.FeldsteuerungService;
import insure.tools.feldsteuerung.persistence.entities.Context;
import insure.tools.feldsteuerung.persistence.entities.Field;
import utils.EPackageNsUriFinder;

public class FeldsteuerungsToolExporter {

    private FeldsteuerungService service;
    private String ecorePath;
    private EPackageNsUriFinder uriFinder;
    private List<String> referencedEcoreFilePaths = new ArrayList<String>();
    private List<String> allPaths = new ArrayList<String>();
    private String dataName;
    private String dataDescription;
    private String domainData;

    public FeldsteuerungsToolExporter(FeldsteuerungService service, String ecorePath, String dataName, String dataDescription) {
        this.dataName = dataName;
        this.dataDescription = dataDescription;
        this.service = service;
        this.ecorePath = ecorePath;
        findReferences(this.getClass().getResourceAsStream(ecorePath));
        allPaths.addAll(referencedEcoreFilePaths);
        allPaths.add(ecorePath);
        uriFinder = new EPackageNsUriFinder((String[]) allPaths.toArray());

    }

    public InputStream export() {
        setDocumentHead(uriFinder, domainData);
        return null;

    }

    public void findReferences(InputStream is) {
        BufferedReader br = null;
        String newString = "";
        try {

            br = new BufferedReader(new InputStreamReader(is));
            while ((newString = br.readLine()) != null) {
                if (newString.contains("platform:" + "\"/" + "plugin")) {
                    String fullPath = StringUtils.substringAfterLast(StringUtils.substringBeforeLast(newString, "#"), "/");
                    referencedEcoreFilePaths.add("/" + fullPath);
                }
            }

        } catch (IOException e) {
            // TODO Auto-generated catch block
        } // calls it
    }

    public void setDocumentHead(EPackageNsUriFinder finder, String doc) {
        doc = "<?xml version=" + "\"1.0\"" + "encoding=" + "\"UTF-8\"" + "?>" + "\n";
        doc += "<core:RootRepository xmi:version=" + "\"2.0\"" + " xmlns:xmi=" + "\"http://www.omg.org/XMI\"" + " xmlns:xsi=" + "\"http://www.w3.org/2001/XMLSchema-instance\"";
        for (Map.Entry<String, String> entry : finder.getPrefix2NsUri().entrySet()) {
            doc += " " + entry.getKey() + ":" + entry.getValue();
        }

        doc += " " + "name=" + dataName + " beschreibung=" + dataDescription;
    }

    public void setDocumentBody(EPackageNsUriFinder finder, FeldsteuerungService service, String doc) {

    }

    public void setEnumerationsContext(EPackageNsUriFinder finder, List<Context> contextList, String doc) {
        doc += "<repositories modelElementId=" + contextList.hashCode() + " name = kontext " + "pattern =" + "\"Repository\"" + ">";
        Iterator<?> iterContext = contextList.iterator();
        while (iterContext.hasNext()) {
            Context next = (Context) iterContext.next();
            String findNsUri = findNsUri(next.getType().getName(), finder);
            if (findNsUri != null)
                doc += "<enumerations xsi:type =" + "\"" + findNsUri + ":" + next.getType().getName() + "\"" + " modelElementId=" + "\"" + next.getUuid() + "\"" + " name=" + "\"" + next.getAlias()
                        + "\"" + " Beschreibung=" + "\"" + next.getName() + "\"" + "/>" + "\n";
        }
        doc += "</repositories>" + "\n";

    }

    public void setEnumerationsFields(EPackageNsUriFinder finder, List<Field> fieldList, String doc) {
        doc += "<repositories modelElementId=" + fieldList.hashCode() + " name = Felder " + "pattern =" + "\"Repository\"" + "|" + "EingabeElement" + "|" + "Steuerelement" + ">";
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
                    fieldtype = "EingabeElement";
                    break;
                }
            }
            String findNsUri = findNsUri(fieldtype, finder);
            if (findNsUri != null)
                doc += "<enumerations xsi:type =" + "\"" + findNsUri + ":" + fieldtype + "\"" + " modelElementId=" + "\"" + next.getIdentifier() + "\"" + " name=" + "\"" + next.getAlias()
                        + "\"" + "/>" + "\n";
        }
        doc += "</repositories>" + "\n";

    }

    public void setEnumerationsInputFieldsCharacteristics(String doc) {
        doc += "<repositories modelElementId=" + "_3XCrwNOpEeSVoOolBb6ZYQ" + " name =" + "\"eingabeelementeigenschaften\"" + "pattern =" + "\"Repository\"" + "|" + "EingabeElementeigenschaft" + ">"
                + "\n";
        doc += "<enumerations xsi:type =" + "\"" + "feldsteuerung" + ":" + "Eingabeelementeigenschaft" + "\"" + " modelElementId=" + "\"" + "_u9rLKtO_EeSVoOolBb6ZYQ" + "\"" + " name=" + "\""
                + "SICHTBAR"
                + "\"" + "/>" + "\n";
        doc += "<enumerations xsi:type =" + "\"" + "feldsteuerung" + ":" + "Eingabeelementeigenschaft" + "\"" + " modelElementId=" + "\"" + "_zaTuitO_EeSVoOolBb6ZYQ" + "\"" + " name=" + "\""
                + "SICHTBAR_EDITIERBAR"
                + "\"" + "/>" + "\n";
        doc += "<enumerations xsi:type =" + "\"" + "feldsteuerung" + ":" + "Eingabeelementeigenschaft" + "\"" + " modelElementId=" + "\"" + "_2ASSStO_EeSVoOolBb6ZYQ" + "\"" + " name=" + "\""
                + "SICHTBAR_EDITIERBAR_NOTWENDIG"
                + "\"" + "/>" + "\n";
        doc += "<enumerations xsi:type =" + "\"" + "feldsteuerung" + ":" + "Eingabeelementeigenschaft" + "\"" + " modelElementId=" + "\"" + "_5vjZWtO_EeSVoOolBb6ZYQ" + "\"" + " name=" + "\""
                + "UNSICHTBAR"
                + "\"" + "/>" + "\n";
        doc += "<enumerations xsi:type =" + "\"" + "feldsteuerung" + ":" + "Eingabeelementeigenschaft" + "\"" + " modelElementId=" + "\"" + "_8DP6StO_EeSVoOolBb6ZYQ" + "\"" + " name=" + "\""
                + "UNSICHTBAR_EDITIERBAR"
                + "\"" + "/>" + "\n";
        doc += "<enumerations xsi:type =" + "\"" + "feldsteuerung" + ":" + "Eingabeelementeigenschaft" + "\"" + " modelElementId=" + "\"" + "__hrI-tO_EeSVoOolBb6ZYQ" + "\"" + " name=" + "\""
                + "UNSICHTBAR_EDITIERBAR_NOTWENDIG"
                + "\"" + "/>" + "\n";

        doc += "</repositories>" + "\n";

    }

    public void setEnumerationsControlFieldsCharacteristics(String doc) {
        doc += "<repositories modelElementId=" + "_BSH00NOqEeSVoOolBb6ZYQ" + " name =" + "\"steuerelementeigenschaften\"" + "pattern =" + "\"Repository\"" + "|" + "Steuerelementeigenschaft" + ">"
                + "\n";
        doc += "<enumerations xsi:type =" + "\"" + "feldsteuerung" + ":" + "Steuerelementeigenschaft" + "\"" + " modelElementId=" + "\"" + "_W312d9RiEeSM0uBkjWoRCg" + "\"" + " name=" + "\""
                + "SICHTBAR_AKTIVIERT"
                + "\"" + "/>" + "\n";
        doc += "<enumerations xsi:type =" + "\"" + "feldsteuerung" + ":" + "Steuerelementeigenschaft" + "\"" + " modelElementId=" + "\"" + "_bBArXNRiEeSM0uBkjWoRCg" + "\"" + " name=" + "\""
                + "SICHTBAR_DEAKTIVIERT"
                + "\"" + "/>" + "\n";
        doc += "<enumerations xsi:type =" + "\"" + "feldsteuerung" + ":" + "Steuerelementeigenschaft" + "\"" + " modelElementId=" + "\"" + "_dP-dTNRiEeSM0uBkjWoRCg" + "\"" + " name=" + "\""
                + "UNSICHTBAR"
                + "\"" + "/>" + "\n";
        doc += "</repositories>" + "\n";

    }

    public void setPrototypeFieldsCharacteristics(String doc) {
        doc += "<repositories modelElementId=" + "_f_ZT0BsOEeWeoYGlWqgNWQ" + " name =" + "\"feldelementeigenschaften\"" + "pattern =" + "\"Repository\"" + "|" + "Feldelementeigenschaften" + "|"
                + "StandardFeldelementeigenschaften" + "|" + "TemplateFeldelementeigenschaften" + ">"
                + "\n";
        doc += "<prototypes xsi:type =" + "\"" + "feldsteuerung" + ":" + "StandardFeldelementeigenschaften" + "\"" + " modelElementId=" + "\"" + "_SuVeKSbaEeWDoMiQXiXZDQ" + "\"" + " name=" + "\""
                + "Standard"
                + "\"" + "/>" + "\n";
        doc += "<standardEingabeelementeigenschaft href =" + "\"" + "_5vjZWtO_EeSVoOolBb6ZYQ" + "\"" + "/>" + "\n";
        doc += "<standardSteuerelementeigenschaft href =" + "\"" + "_dP-dTNRiEeSM0uBkjWoRCg" + "\"" + "/>" + "\n";
        doc += "</repositories>" + "\n";

    }

    public String findNsUri(String name, EPackageNsUriFinder finder) {

        for (Map.Entry<String, ArrayList<String>> entry : finder.getPackageAndClassesMap().entrySet()) {
            if (entry.getValue().contains(name)) {
                return entry.getKey();
            }
        }
        return null;

    }

    public FeldsteuerungService getService() {
        return service;
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
