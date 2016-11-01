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
public class Profile extends Model {

    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    public long id;

    public String name;

    @Temporal(TemporalType.TIMESTAMP)
    public Date registerDate;

    @OneToMany(cascade=CascadeType.ALL, mappedBy="profile")
    public List<MetricEntry> entries = new ArrayList<MetricEntry>();

    public static Finder<Long,Profile> find = new Finder<Long,Profile>(
        Long.class, Profile.class
    );

    public static Profile getProfileById(String id){
        Profile aura = Profile.find.where().eq("id", Long.parseLong(id)).findUnique();
        return aura;
    }

    public static Profile getProfileByName(String name){
        Profile aura = Profile.find.where().eq("name", name).findUnique();
        return aura;
    }
}
