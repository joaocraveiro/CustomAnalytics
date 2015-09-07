package models;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by craveiro on 03-09-2015.
 */

@Entity
public class Metric {

    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
	public long id;

    public String name;

    @Temporal(TemporalType.TIMESTAMP)
    public Date date;

    public Integer value;

    @ManyToOne
    public Aura aura;
}
