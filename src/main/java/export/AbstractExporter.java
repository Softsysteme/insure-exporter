package export;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import utils.EPackageNsUriFinder;

public abstract class AbstractExporter {
    protected Object service;
    protected String ecorePath;
    protected String outputXmlName;
    protected EPackageNsUriFinder uriFinder;
    protected List<String> referencedEcoreFilePaths = new ArrayList<String>();
    protected List<String> allPaths = new ArrayList<String>();
    protected String domainData = " ";
    protected boolean docHeadSeted;
    protected boolean docBodySeted;
    protected boolean docFooterSeted;

    public AbstractExporter(String ecorePath, Object service, String fileName) {
        this.service = service;
        this.ecorePath = ecorePath;
        this.outputXmlName = fileName;
        findReferences(this.getClass().getResourceAsStream(ecorePath));
        addReferencedFilesToList();

    }

    public InputStream export() {
        setDocumentHead();
        setDocumentBody();
        setDocumentFooter();
        System.out.println(domainData);
        return new ByteArrayInputStream(domainData.toString().getBytes(StandardCharsets.UTF_8));

    }

    public String findNsUri(String name, EPackageNsUriFinder finder) {

        for (Map.Entry<String, ArrayList<String>> entry : finder.getPackageAndClassesMap().entrySet()) {
            if (entry.getValue().contains(name)) {
                return entry.getKey();
            }
        }
        return null;

    }

    public void findReferences(InputStream is) {
        BufferedReader br = null;
        String newString = "";
        try {
            br = new BufferedReader(new InputStreamReader(is));
            while ((newString = br.readLine()) != null) {
                if (newString.contains("platform:")) {
                    String fullPath = StringUtils.substringAfterLast(StringUtils.substringBeforeLast(newString, "#"), "/");
                    if (!referencedEcoreFilePaths.contains("/" + fullPath))
                        referencedEcoreFilePaths.add("/" + fullPath);
                }
            }

        } catch (IOException e) {
            // TODO Auto-generated catch block
        } // calls it
    }

    public void addReferencedFilesToList() {
        Iterator<String> iter = referencedEcoreFilePaths.iterator();
        while (iter.hasNext()) {
            String next = iter.next();
            if (!allPaths.contains(next)) {
                allPaths.add(next);
            }
        }
        if (!allPaths.contains(ecorePath))
            allPaths.add(ecorePath);
        uriFinder = new EPackageNsUriFinder(allPaths);

    }

    public abstract void setDocumentHead();

    public abstract void setDocumentBody();

    public abstract void setDocumentFooter();
}
