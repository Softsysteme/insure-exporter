package exportertests;

import java.io.InputStream;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import export.FeldsteuerungsToolExporter;

public class FeldsteuerungsToolExporterTest {

    static FeldsteuerungsToolExporter exporter;
    static String ecorePath = "/infoservice.ecore";
    static String dataName = "name";
    static String dataDescription = "description";

    @BeforeClass
    public static void init() {
        exporter = new FeldsteuerungsToolExporter(ecorePath, dataName, dataDescription, "./muster.fs.h2.db");
    }

    @Test
    public void exportWorksWell() {
        InputStream is = exporter.export();
        Assert.assertNotNull(is);
    }

}
