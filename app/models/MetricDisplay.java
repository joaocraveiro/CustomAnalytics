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
public class MetricDisplay extends Model {

    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    public long id;
    public String name;

    @ManyToOne
    public Aura aura;

    @ManyToOne
    public Metric metric;

    public Boolean category;
    public Boolean user;

    public String plot;  

    public static Finder<Long,MetricDisplay> find = new Finder<Long,MetricDisplay>(
        Long.class, MetricDisplay.class
    );

    public static MetricDisplay getMetricDisplayById(String id){
        MetricDisplay metricDisplay = MetricDisplay.find.where().eq("id", Long.parseLong(id)).findUnique();
        return metricDisplay;
    }
}
