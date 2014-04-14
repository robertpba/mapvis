package mapvis.preprocessing.wiki.dbtable;


import plainmapper.annotation.Column;
import plainmapper.annotation.Table;

import java.math.BigInteger;

// http://www.mediawiki.org/wiki/Manual:Page_table

@Table("page")
public class Page {
    @Column("page_Id")
    public long id;
    @Column("page_namespace")
    public int namespace;
    @Column("page_title")
    public String title;
    @Column("page_restrictions")
    public String restrictions;
    @Column("page_counter")
    public BigInteger numViews;
    @Column("page_is_redirect")
    public boolean isRedirect;
    @Column("page_is_new")
    public boolean isNew;
    @Column("page_random")
    public double random;
    @Column("page_touched")
    public String touched;
    @Column("page_latest")
    public long latest;
    @Column("page_len")
    public long length;

}
