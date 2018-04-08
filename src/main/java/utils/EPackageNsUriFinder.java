package utils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import caches.InsureExporterCacheManager;

public class EPackageNsUriFinder {

    InsureExporterCacheManager cm = InsureExporterCacheManager.INSTANCE;
    private Map<String, ArrayList<String>> packageAndClassesMap = new HashMap<String, ArrayList<String>>();
    private Map<String, String> prefix2NsUri = new HashMap<String, String>();
    private List<String> ecorePaths;

    public EPackageNsUriFinder(List<String> ecorePaths) {
        this.setEcorePaths(ecorePaths);
        for (int k = 0; k < ecorePaths.size(); k++) {
            init(parseEcoreDoc(ecorePaths.get(k)));
        }
    }

    public Document parseEcoreDoc(String ecorePath) {
        // Get Document Builder
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = null;
        try {
            builder = factory.newDocumentBuilder();
        } catch (ParserConfigurationException e1) {
            // TODO Auto-generated catch block
        }
        // Build Document
        Document ecoreDocument = null;
        try {
            ecoreDocument = builder.parse(this.getClass().getResourceAsStream(ecorePath));
        } catch (SAXException | IOException e) {
            // TODO Auto-generated catch block
        }

        ecoreDocument.getDocumentElement().normalize();
        return ecoreDocument;
    }

    public void init(Document document) {
        prefix2NsUri.put(document.getDocumentElement().getAttribute("name"), document.getDocumentElement().getAttribute("nsURI"));
        NodeList nList = document.getElementsByTagName("eSubpackages");
        for (int i = 0; i < nList.getLength(); i++) {
            Node item = nList.item(i);
            visitPackage(item, document);
        }
    }

    public Map<String, String> getPrefix2NsUri() {
        return prefix2NsUri;
    }

    public void setPrefix2NsUri(Map<String, String> prefix2NsUri) {
        this.prefix2NsUri = prefix2NsUri;
    }

    public void visitPackage(Node node, Document document) {
        String packageName = node.getAttributes().getNamedItem("name").getNodeValue();
        String NsUri = node.getAttributes().getNamedItem("nsURI").getNodeValue();
        if (prefix2NsUri.containsKey(packageName)) {
            packageName = packageName + "_" + NsUri.hashCode();
            prefix2NsUri.put(packageName, NsUri);
            // cm.putInCache(packageName, NsUri);
        } else {
            prefix2NsUri.put(packageName, NsUri);
        }
        ArrayList<String> classes = new ArrayList<String>();
        for (int i = 0; i < node.getChildNodes().getLength(); i++) {
            Node item = node.getChildNodes().item(i);
            if (item.getNodeName().contentEquals("eClassifiers") && item.getNodeType() == 1) {
                classes.add(item.getAttributes().getNamedItem("name").getNodeValue());
            }
        }
        packageAndClassesMap.put(packageName, classes);

    }

    public Map<String, ArrayList<String>> getPackageAndClassesMap() {
        return packageAndClassesMap;
    }

    public void setPackageAndClassesMap(Map<String, ArrayList<String>> packageAndClassesMap) {
        this.packageAndClassesMap = packageAndClassesMap;
    }

    public List<String> getEcorePaths() {
        return ecorePaths;
    }

    public void setEcorePaths(List<String> ecorePaths2) {
        this.ecorePaths = ecorePaths2;
    }

}
