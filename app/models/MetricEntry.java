package models;

import com.avaje.ebean.Model;
import javax.persistence.*;
import java.util.Date;

/**
 * Created by craveiro on 03-09-2015.
 */

@Entity
public class MetricEntry {

    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
	public long id;

    public String name;

    @Temporal(TemporalType.TIMESTAMP)
    public Date date;

    public Integer value;

    @ManyToOne
    public Metric metric;

    public static Finder<Long,MetricEntry> find = new Finder<Long,MetricEntry>(
        Long.class, MetricEntry.class
    );
}
