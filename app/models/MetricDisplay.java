package models;

import javax.persistence.*;
import com.avaje.ebean.Model;
import java.util.*;

import java.security.SecureRandom;
import java.math.BigInteger;

import play.api.libs.json.Json;

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

    public Boolean groupCategory;
    public Boolean groupUser;

    public String plot;
    public String timeFrame;

    public HashMap<String,Integer> userGrouped(){
        HashMap<String,Integer> data = new HashMap<String,Integer>();
        for(MetricEntry entry : metric.metricEntries){
            String key = "\"" + entry.profile.id + "\"";
            if(data.containsKey(key)){
                data.put(key, data.get(key) + entry.value);
            } else{
                data.put(key, entry.value);    
            }                    
        }
        return data;
    }

     public HashMap<String,Integer> userCategoryGrouped(String profileId){
        HashMap<String,Integer> data = new HashMap<String,Integer>();
        for(MetricEntry entry : metric.metricEntries){
            if(profileId == null || (profileId != null && entry.profile.id == Long.parseLong(profileId))){
                String key = "\"" + entry.profile.id + " " + entry.category + "\"";
                if(data.containsKey(key)){                
                    data.put(key, data.get(key) + entry.value);
                } else {
                    data.put(key, entry.value);    
                }
            }
        }
        return data;
    }

    public static Finder<Long,MetricDisplay> find = new Finder<Long,MetricDisplay>(
        Long.class, MetricDisplay.class
    );

    public static MetricDisplay getMetricDisplayById(String id){
        MetricDisplay metricDisplay = MetricDisplay.find.where().eq("id", Long.parseLong(id)).findUnique();
        return metricDisplay;
    }
}
