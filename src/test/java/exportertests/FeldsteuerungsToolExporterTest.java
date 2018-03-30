
import java.io.InputStream;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import export.FeldsteuerungsToolExporter;
import insure.tools.feldsteuerung.persistence.FeldsteuerungPersistenceService;
import insure.tools.feldsteuerung.persistence.FeldsteuerungService;

public class FeldsteuerungsToolExporterTest {

    static FeldsteuerungsToolExporter exporter;
    static FeldsteuerungService service = new FeldsteuerungService(FeldsteuerungPersistenceService.getInstance("muster.fs.h2.db"));
    static String ecorePath = "/infoservice.ecore";
    static String dataName = "name";
    static String dataDescription = "description";

    @BeforeClass
    public static void init() {
        exporter = new FeldsteuerungsToolExporter(service, ecorePath, dataName, dataDescription);
    }

    @Test
    public void exportWorksWell() {
        InputStream is = exporter.export();
        Assert.assertNotNull(is);
    }

}
