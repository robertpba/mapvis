package plainmapper.test.models;


import plainmapper.annotation.Column;
import plainmapper.annotation.CsvOrder;

import java.util.Date;

public class Actor {
    @Override
    public String toString() {
        return "Actor{" +
                "actor_id=" + actor_id +
                ", first_name='" + first_name + '\'' +
                ", last_name='" + last_name + '\'' +
                ", last_update=" + last_update +
                '}';
    }

    public int actor_id;
    public String first_name;
    public String last_name;
    public Date last_update;
}
