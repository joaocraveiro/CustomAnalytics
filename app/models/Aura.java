package models;

import javax.persistence.*;
import com.avaje.ebean.Model;
import java.util.*;

import java.security.SecureRandom;
import java.math.BigInteger;

/**
 * Created by craveiro on 03-09-2015.
 */

@Entity
public class Aura extends Model {

    @Id
    @GeneratedValue
    public Long id;

    public String name;

    public String adminToken = new BigInteger(130, (new SecureRandom())).toString(32); //used to manage aura

    public String userToken =  new BigInteger(130, (new SecureRandom())).toString(32);; //used to create new entries

    @OneToMany(cascade=CascadeType.ALL, mappedBy="aura")
    public List<Metric> metrics = new ArrayList<Metric>();

    @OneToMany(cascade=CascadeType.ALL, mappedBy="aura")
    public List<MetricDisplay> displays = new ArrayList<MetricDisplay>();

    public static Finder<Long,Aura> find = new Finder<Long,Aura>(
        Long.class, Aura.class
    );

    public static Aura getAuraById(Long id){
        Aura aura = Aura.find.where().eq("id", id).findUnique();
        return aura;
    }

    public static Aura getAuraById(String id){
        Aura aura = Aura.find.where().eq("id", Long.parseLong(id)).findUnique();
        return aura;
    }

    public static Aura getAuraByName(String name){
        Aura aura = Aura.find.where().eq("name", name).findUnique();
        return aura;
    }
}
