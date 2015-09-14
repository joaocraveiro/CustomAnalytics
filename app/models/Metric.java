package models;

import javax.persistence.*;
import java.util.List;
import java.util.ArrayList;

/**
 * Created by craveiro on 03-09-2015.
 */

@Entity
public class Metric {

    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
	public long id;

    public String name;

	public String plotType;	
    
    @ManyToOne
    public Aura aura;
	
	@OneToMany(cascade=CascadeType.ALL, mappedBy="metric")
    public List<MetricEntry> metricEntries = new ArrayList<MetricEntry>();
}
