package plainmapper.test.test1;


import plainmapper.PM;
import plainmapper.rdb.RdbContext;
import plainmapper.test.models.Actor;

import java.util.List;


public class Test2 {

    public static void main(String[] args) throws Exception {

        RdbContext rdb = PM.Rdb("jdbc:mysql://localhost:3306/sakila", "root", "root");

        List<Actor> query = rdb.query(Actor.class, "select * from actor limit 10");
        query.forEach(e-> System.out.println(e.toString()));

/*        List<Category> categoryList = dapper.query(Category.class, "select * from category limit 10");
        categoryList.forEach(e-> System.out.println(e.toString()));

        List<CategoryLink> categoryLinkList = dapper.query(CategoryLink.class, "select * from categorylinks limit 10");
        categoryLinkList.forEach(e-> System.out.println(e.toString()));*/
    }
}
