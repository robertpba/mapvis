package plainmapper.test.test1;


import plainmapper.PM;
import plainmapper.csv.CsvContext;
import plainmapper.test.models.Actor;

import java.io.ByteArrayInputStream;

public class Test {

    public static void main(String[] args) throws Exception {

        String input = "1\t2\t3\t1990-12-12 23:12:34";


        CsvContext cvs = PM.Csv(new ByteArrayInputStream(input.getBytes("utf8")));

        Actor e = cvs.Read(Actor.class);
        //query.forEach(e-> System.out.println(e.toString()));
        System.out.println(e.toString());

/*        List<Category> categoryList = dapper.query(Category.class, "select * from category limit 10");
        categoryList.forEach(e-> System.out.println(e.toString()));

        List<CategoryLink> categoryLinkList = dapper.query(CategoryLink.class, "select * from categorylinks limit 10");
        categoryLinkList.forEach(e-> System.out.println(e.toString()));*/
    }
}
