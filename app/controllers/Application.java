package controllers;

import play.*;
import play.mvc.*;
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
    public Result index() {
        List<Aura> auras = Aura.find.all();
        return ok(index.render(auras));
    }
		
	public Result createAura(String auraName){
		Aura aura = Aura.getAuraByName(auraName);
        if(aura == null){
        	aura = new Aura();
            aura.name = auraName;
            aura.save();
            System.out.println("Aura " + auraName + " created.");
			return ok(index.render(Aura.find.all()));
        } else {        	
        	System.out.println("Aura already exists");
            return ok(index.render(Aura.find.all()));
        }             
	}
          
    public Result auralytics(String auraName) {
        Aura aura = Aura.getAuraByName(auraName);
        if(aura == null){    
        	System.out.println("Aura " + auraName + " doesn't exist");
            return ok(index.render(Aura.find.all()));	
        }
        else{
        	play.mvc.Http.Cookie c = request().cookies().get("test");
			if(c != null)
			System.out.println("Cookie test:" + c.value());
        	return ok(auralytics.render(aura.name, aura.metrics));
        }              
    }
    
    public Result createMetric(String auraName, String name, String plotType) {
		Aura aura = Aura.getAuraByName(auraName);
		if(aura == null){
			System.out.println("Aura " + auraName + " doesn't exist");
			return ok(index.render(Aura.find.all()));
		}
		Metric metric = Metric.getMetricByName(name);
		if(metric != null){
			System.out.println("Metric " + name + " already exists");
			return ok(auralytics.render(aura.name, aura.metrics)); 
		}
		else{
			metric = new Metric();
			metric.name = name;
			metric.plotType = plotType;
			metric.aura = aura;
			metric.save();
			aura.metrics.add(metric);
			aura.save();
			System.out.println("Metric " + name + " created.");
			return redirect("/auralytics/" + auraName);
		}
    }
		
    public Result createMetricEntry(String auraName, String metricName, String name, Integer value) {

		play.mvc.Http.Cookie c = request().cookies().get("test");
		if(c != null)
			System.out.println("Cookie test:" + c.value());

        Aura aura = Aura.getAuraByName(auraName);
		if(aura == null){
			System.out.println("Aura " + auraName + " doesn't exist.");
			return ok(index.render(Aura.find.all()));
		} 

		Metric metric = Metric.getMetricByName(metricName);
		if(metric == null){
			System.out.println("Metric " + metricName + " doesn't exist.");
			return ok(auralytics.render(aura.name, aura.metrics));
		}
		/*try{
        metricEntry = JPA.em().createQuery("from MetricEntry where name = :metricEntryName", MetricEntry.class)
                .setParameter("metricEntryName", name).getSingleResult();
				     
		metricEntry.value++;
		}
		catch(Exception e)
		{*/

		MetricEntry metricEntry = new MetricEntry();
        metricEntry.name = name;
		metricEntry.value = value;
        metricEntry.date = new Date();		
		metricEntry.metric = metric;
		metricEntry.save();		

		metric.metricEntries.add(metricEntry);
		metric.save();
		System.out.println("Metric entry for" + name + " created");

        response().setCookie("test", ""+value, 86400);
        return redirect("/auralytics/" + auraName);
    }
		
    public Result createUnnamedMetricEntry(String auraName, String metricName, Integer value) {
		return ok(views.html.nameMetricEntry.render(auraName,metricName,value)); 
    }

}