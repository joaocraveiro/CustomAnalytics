package models;

import javax.persistence.*;
import com.avaje.ebean.Model;
import java.util.*;

/**
 * Created by craveiro on 03-09-2015.
 */

@Entity
public class Aura extends Model {

    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    public long id;

    public String name;

    @OneToMany(cascade=CascadeType.ALL, mappedBy="aura")
    public List<Metric> metrics = new ArrayList<Metric>();

    public static Finder<Long,Aura> find = new Finder<Long,Aura>(
        Long.class, Aura.class
    );

    public static Aura getAuraByName(String name){
        Aura aura = Aura.find.where().eq("name", name).findUnique();
        return aura;
    }
}
