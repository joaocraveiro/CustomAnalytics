package models;

import javax.persistence.*;
import com.avaje.ebean.Model;

import java.text.SimpleDateFormat;
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

    public HashMap getData(String profileId){
        HashMap<String,Integer> data = new HashMap<String,Integer>();
        SimpleDateFormat formatter = null;
        if(!timeFrame.isEmpty()) {
            switch (timeFrame) {
                case "Minute":
                    formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                    break;
                case "Hourly":
                    formatter = new SimpleDateFormat("yyyy-MM-dd HH:00");
                    break;
                case "Weekly":
                    formatter = new SimpleDateFormat("yyyy 'W'w");
                    break;
                case "Monthly":
                    formatter = new SimpleDateFormat("yyyy-MM");
                    break;
                case "Yearly":
                    formatter = new SimpleDateFormat("yyyy");
                    break;
                case "Daily":
                default:
                    formatter = new SimpleDateFormat("yyyy-MM-dd");
                    break;
            }
        }

        if(!groupCategory && !groupUser && timeFrame.isEmpty()){
            int sum = 0;
            for(MetricEntry entry : metric.metricEntries){
                sum += entry.value;
            }
            data.put("\"Value\"",sum);
        }

        else {
            // EVENTUALLY WE SHOULD HAVE A CACHE FOR THIS :) OR DO IT WITH WITH A SPECIFIC FRAMEWORK FOR THE PURPOSE
            for (MetricEntry entry : metric.metricEntries) {
                if (profileId == null || profileId.equals(entry.profile.id)) {
                    String key = "\"";
                    if (groupCategory) {
                        if (entry.category == null) key += "Not specified" + " ";
                        else key += entry.category + " ";
                    }
                    if (groupUser){
                      if(entry.profile.name == null) key += entry.profile.id + " ";
                      else key += entry.profile.name + " ";
                    }
                    if (!timeFrame.isEmpty()) key += formatter.format(entry.date) + " ";
                    key = key.trim();
                    key += "\"";
                    if (data.containsKey(key)) {
                        data.put(key, data.get(key) + entry.value);
                    } else {
                        data.put(key, entry.value);
                    }
                }
            }
        }
        return data;
    }

    public static List<MetricDisplay> getDisplaysByMetric(Long id){
        return MetricDisplay.find.where().eq("metric.id",id).findList();
    }

    public static Finder<Long,MetricDisplay> find = new Finder<Long,MetricDisplay>(
        Long.class, MetricDisplay.class
    );

    public static MetricDisplay getMetricDisplayById(String id){
        return MetricDisplay.find.where().eq("id", Long.parseLong(id)).findUnique();
    }
}
