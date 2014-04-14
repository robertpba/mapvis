package mapvis.preprocessing.wiki.dbtable;

import plainmapper.annotation.Column;
import plainmapper.annotation.Table;

import java.util.Date;

// http://www.mediawiki.org/wiki/Manual:Categorylinks_table

@Table("categorylinks")
public class CategoryLink {
    @Column("cl_from")
    public long pageId;
    @Column("cl_to")
    public String categoryName;
    @Column("cl_sortkey")
    public String sortkey;
    @Column("cl_sortkey_prefix")
    public String sortkeyPrefix;
    @Column("cl_timestamp")
    public Date timestamp;
    @Column("cl_collation")
    public String collation;
    @Column("cl_type")
    public String type;
}
