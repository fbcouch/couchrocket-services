package models;


import java.util.*;
import javax.persistence.*;

import play.api.libs.json.Json;
import play.db.ebean.*;
import play.data.format.*;
import play.data.validation.*;

@Entity
public class Score extends Model {

    @Id
    @Constraints.Min(10)
    public Long id;

    @Constraints.Required
    public String name;

    @Constraints.Required
    public Long score;

    public static Finder<Long,Score> find = new Finder<Long,Score>(
            Long.class, Score.class
    );


}