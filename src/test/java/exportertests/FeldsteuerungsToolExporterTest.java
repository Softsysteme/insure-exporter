package exportertests;

import java.io.File;
import java.io.InputStream;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import export.FeldsteuerungExporter;
import insure.tools.feldsteuerung.persistence.FeldsteuerungPersistenceService;
import insure.tools.feldsteuerung.persistence.FeldsteuerungService;

public class FeldsteuerungsToolExporterTest {

    static FeldsteuerungExporter exporter;
    static String ecorePath = "/infoservice.ecore";
    static String identName = "MUSTER";
    static String feldsteuerungName = "MUSTERSYSTEM";
    static String fileName = "exported-infoservice-reference.insure";

    static FeldsteuerungService service = new FeldsteuerungService(FeldsteuerungPersistenceService.getInstance(getTestDatabaseFile()));

    public static String getTestDatabaseFile() {
        File file = new File("C:/Users/Mpouma/Desktop/feldsteuerung/muster.fs.h2.db");
        return file.getAbsolutePath();
    }

    @BeforeClass
    public static void init() {
        exporter = new FeldsteuerungExporter(ecorePath, service, identName, feldsteuerungName, fileName);
    }

    @Test
    public void exportWorksWell() {
        InputStream is = exporter.export();
        Assert.assertNotNull(is);
    }

}
