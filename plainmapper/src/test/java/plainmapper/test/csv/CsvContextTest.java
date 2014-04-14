package plainmapper.test.csv;

import org.testng.Assert;
import org.testng.annotations.Test;
import plainmapper.PM;
import plainmapper.csv.CsvContext;
import plainmapper.test.models.Actor;

import java.io.ByteArrayInputStream;

public class CsvContextTest {

    @Test
    public void testRead() throws Exception {
        String input = "1\t2\t3\t1990-12-12 23:12:34";
        ByteArrayInputStream stream = new ByteArrayInputStream(input.getBytes("utf8"));

        CsvContext csv = PM.Csv(stream);

        Actor read = csv.Read(Actor.class);

        Assert.assertNotNull(read);
        Assert.assertEquals(1, read.actor_id);
        Assert.assertEquals("2", read.first_name);
        Assert.assertEquals("3", read.last_name);
    }
}
