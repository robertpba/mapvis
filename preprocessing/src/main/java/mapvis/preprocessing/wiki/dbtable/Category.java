package mapvis.preprocessing.wiki.dbtable;


// http://www.mediawiki.org/wiki/Manual:Category_table


import plainmapper.annotation.Column;
import plainmapper.annotation.Table;

@Table("category")
public class Category {
    @Column("cat_Id")
    public long id;
    @Column("cat_title")
    public String title;
    @Column("cat_pages")
    public int numPages;
    @Column("cat_subcats")
    public int numChildren;
    @Column("cat_files")
    public int numFiles;
}
