package plainmapper.test.metadata;

import org.testng.Assert;
import org.testng.annotations.Test;
import plainmapper.metadata.object.ClassMetadataBuilder;
import plainmapper.metadata.object.MdClass;
import plainmapper.test.models.Actor;

public class MdClassFactoryTest {

    @Test
    public void testGetClassMetaData() throws Exception {
        MdClass<Actor> MdClass1 = ClassMetadataBuilder.getClassMetaData(Actor.class);

        Assert.assertNotNull(MdClass1);

        MdClass<Actor> MdClass2 = ClassMetadataBuilder.getClassMetaData(Actor.class);

        Assert.assertNotNull(MdClass1);

        // should cache the result
        Assert.assertEquals(MdClass1, MdClass2);
    }

    @Test
    public void createClassMetaData() throws Exception {
        MdClass<Actor> MdClass = ClassMetadataBuilder.createClassMetaData(Actor.class);

        Assert.assertNotNull(MdClass);

        Assert.assertFalse(MdClass.fields.length == 0);
    }

    @Test
    public void testGetFieldMappingAnnotations() throws Exception {

    }

    @Test
    public void testGetClassMappingAnnotations() throws Exception {

    }

    @Test
    public void testIsMappingAnnotationType() throws Exception {

    }
}
