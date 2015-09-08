package models;

import javax.persistence.*;
import java.util.List;
import java.util.ArrayList;

/**
 * Created by craveiro on 03-09-2015.
 */

@Entity
public class Aura  {

    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    public long id;

    public String name;

    @OneToMany(cascade=CascadeType.ALL, mappedBy="aura")
    public List<Metric> metrics = new ArrayList<Metric>();
}
