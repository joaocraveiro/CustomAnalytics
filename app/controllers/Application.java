package controllers;

import play.mvc.*;
import play.db.jpa.*;
import views.html.*;
import models.*;

import java.util.Date;
import java.util.List;
import java.util.ArrayList;

/**
 * Created by craveiro on 03-09-2015.
 */

public class Application extends Controller {

 /**
     * Renders the index view that lists all the registered auras.<br>
     * @return 200 OK
     */
    @Transactional
    public Result index() {
        List<Aura> auras = Aura.find.all();
        return ok(index.render(auras));
    }
	
	@Transactional
	public Result createAura(String auraName){
		Aura aura;
        try {
            aura = JPA.em().createQuery("from Aura where name = :auraName", Aura.class)
						   .setParameter("auraName", auraName)
						   .getSingleResult();
		   return ok(message.render("ERROR: The Aura '" + auraName + "' already exists."));			
        } catch(Exception e)
        {
            aura = new Aura();
            aura.name = auraName;
            JPA.em().persist(aura);
			List<Aura> auras = Aura.find.all();
			return ok(index.render(auras));
        }             
	}

    @Transactional
    public Result auralytics(String auraName) {
        Aura aura;
        try {
            aura = Aura.getAuraByName(auraName);
			return ok(auralytics.render(aura.name, aura.metrics));
        } catch(Exception e)
        {
               return ok(message.render("ERROR: The Aura '" + auraName + "' doesn't exist."));
        }               
    }


    @Transactional
    public Result createMetric(String auraName, String name, String plotType) {
		Aura aura;
		try{
        aura = Aura.getAuraByName(auraName);
		}
		catch(Exception e)
		{
			return ok(message.render("ERROR: The Aura '" + auraName + "' doesn't exist."));
		}
			try{
			Metric metric = JPA.em().createQuery("from Metric where name = :metricName", Metric.class)
                .setParameter("metricName", name).getSingleResult();
				return ok(message.render("ERROR: The Metric '" + name + " already exists."));
		}
		catch(Exception e)
		{
			Metric metric = new Metric();
			metric.name = name;
			metric.plotType = plotType;
			metric.aura = aura;
			metric.save();
			aura.metrics.add(metric);
			aura.save();
		}
		               
        return redirect(routes.Application.auralytics(auraName));
    }
	
	@Transactional
    public Result createMetricEntry(String auraName, String metricName, String name, Integer value) {
		Aura aura;
		Metric metric;	
		MetricEntry metricEntry;

		play.mvc.Http.Cookie c = request().cookies().get("name");
		if(c != null)
		System.out.println("Cookie test:" + c);

        try{
        aura = Aura.getAuraByName(auraName);
		}
		catch(Exception e)
		{
			return ok(message.render("ERROR: The requested Aura doesn't exist."));
		}
		try{
        metric = Aura.getMetricByName(metricName);
		}
		catch(Exception e)
		{
			return ok(message.render("ERROR: Metric '" + metricName + "' doesn't exist."));
		}
		
		/*try{
        metricEntry = JPA.em().createQuery("from MetricEntry where name = :metricEntryName", MetricEntry.class)
                .setParameter("metricEntryName", name).getSingleResult();
				     
		metricEntry.value++;
		}
		catch(Exception e)
		{*/
		metricEntry = new MetricEntry();
        metricEntry.name = name;
		metricEntry.value = value;
        metricEntry.date = new Date();
		
		metricEntry.metric = metric;
		metricEntry.save();
		metric.metricEntries.add(metricEntry);
		metric.save();

        response().setCookie("test", "userid");
        return redirect(routes.Application.auralytics(auraName));
    }
	
	@Transactional
    public Result createUnnamedMetricEntry(String auraName, String metricName, Integer value) {
		return ok(views.html.nameMetricEntry.render(auraName,metricName,value)); 
    }

}