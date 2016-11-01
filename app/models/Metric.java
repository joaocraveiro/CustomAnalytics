package models;

import com.avaje.ebean.Model;
import javax.persistence.*;
import java.util.*;

/**
 * Created by craveiro on 03-09-2015.
 */

@Entity
public class Metric extends Model{

    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
	public long id;
    
    public String name;

    public String redirectAddress;

    //public List String categories;
    //public Boolean forceCategories;
    
    @ManyToOne
    public Aura aura;
	
	@OneToMany(cascade=CascadeType.ALL, mappedBy="metric")
    public List<MetricEntry> metricEntries = new ArrayList<MetricEntry>();

    public static Finder<Long,Metric> find = new Finder<Long,Metric>(
        Long.class, Metric.class
    );

    public static Metric getMetricByName(String metricName){
        Metric metric = Metric.find.where().eq("name", metricName).findUnique();
        return metric;
    }
}
