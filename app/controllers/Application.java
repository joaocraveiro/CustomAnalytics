package controllers;

import play.*;
import play.mvc.*;
import views.html.*;
import models.*;

import java.util.Date;
import java.util.List;
import java.util.ArrayList;

import play.libs.Json;
import com.fasterxml.jackson.databind.JsonNode;

/**
 * Created by craveiro on 03-09-2015.
 */

public class Application extends Controller {

 	/**
     * Renders the index view that lists all the registered auras.<br>
     * @return 200 OK
     */
    @Security.Authenticated(AdminAuthenticator.class)
    public Result index() {
    	String ses = session().get("aura");
        if(ses == null) ok(login.render());
    	if(ses.equals("superadmin")) return ok(index.render(Aura.find.all(),true));
    	else{    		
    		return ok(index.render(Aura.find.where().eq("id", Long.parseLong(ses)).findList(),false));
    	}
    }

    public Result logout(){
    	session().clear();
    	return redirect("/login");
    }

    public Result login(){
        return ok(login.render());
    }

    public Result authenticate(){
    	JsonNode json = request().body().asJson();
    	String code = json.get("code").asText();
    	String auraName = json.get("aura").asText();
    	if(auraName.equals("admin") && code.equals("123456")){
    		session().clear();
        	session("aura", "superadmin");
        	return ok();
    	} else {
    		Aura aura = Aura.getAuraByName(auraName.trim());
    		if(aura == null) return badRequest();
    		if(!aura.adminToken.equals(code)) return unauthorized();
    		session().clear();
        	session("aura", aura.id.toString());
    		return ok();
    	}    	
	}

	@Security.Authenticated(AdminAuthenticator.class)
    public Result auraAdmin(String auraName){
    	Aura aura = Aura.getAuraByName(auraName);   	
    	if(aura == null) return badRequest();
    	String auraId = session().get("aura");
    	if(auraId.equals("superadmin")) return ok(auraAdmin.render(aura));    	
    	if(auraId.equals(aura.id.toString())) return ok(auraAdmin.render(aura));
    	else return unauthorized();
    }
	
	@Security.Authenticated(SuperAdminAuthenticator.class)
	public Result createAura(String auraName){
		Aura aura = Aura.getAuraByName(auraName);
        if(aura == null){
        	aura = new Aura();
            aura.name = auraName;
            aura.save();
            System.out.println("Aura " + auraName + " created.");
			return index();
        } else {
        	System.out.println("Aura already exists");
            return index();
        }
	}
    
    public Result auralytics(String auraName) {
        Aura aura = Aura.getAuraByName(auraName);
        if(aura == null){    
        	System.out.println("Aura " + auraName + " doesn't exist");
            return index();
        }    
        String login = session().get("aura");
        if(login == null){
            play.mvc.Http.Cookie c = request().cookies().get("aurasma-customanalytics");    
            if(c != null)
                return ok(auraPublic.render(aura,c.value()));
            else
                return index();
        }
        else if(login.equals("superadmin") || Aura.getAuraById(login).name.equals(auraName))
            return ok(auraPublic.render(aura,null));
            else return index();
    }

    @Security.Authenticated(AdminAuthenticator.class)
    public Result createMetric() {
        JsonNode json = request().body().asJson();
        String auraName = json.get("auraName").asText();
        String name = json.get("metricname").asText();
		String redirect = json.get("metricredirect").asText();
        Aura aura = Aura.getAuraByName(auraName);
		if(aura == null){
			System.out.println("Aura " + auraName + " doesn't exist");
			return index();
		}
		Metric metric = aura.getMetricByName(name);
		if(metric != null){
			System.out.println("Metric " + name + " already exists");
			return auraAdmin(aura.name);
		}
		else{
			metric = new Metric();
			metric.name = name;
			metric.aura = aura;
            if(!redirect.isEmpty()) metric.redirectAddress = redirect;
			metric.save();
			aura.metrics.add(metric);
			aura.save();
			System.out.println("Metric " + name + " created.");
			return auraAdmin(aura.name);
		}
    }

    @Security.Authenticated(AdminAuthenticator.class)
    public Result createDisplay(){
    	JsonNode json = request().body().asJson();
    	String auraName = json.get("auraName").asText();
		Aura aura = Aura.getAuraByName(auraName);
		if(aura == null){
			System.out.println("Aura " + auraName + " doesn't exist");
			return badRequest();
		}
		String displayName = json.get("displayName").asText();
		if(displayName == null) return badRequest(); // THIS IS NOT AVOIDING EMPTY NAMES
		String metricName = json.get("metric").asText();
		if(metricName == null) return badRequest(); // THIS IS NOT AVOIDING EMPTY METRICS
		String plot = json.get("plot").asText();
        String timeFrame = json.get("timeFrame").asText();
		Boolean category = json.get("category").asBoolean();
		Boolean user = json.get("user").asBoolean();
		MetricDisplay display = new MetricDisplay();
		display.name = displayName;
		display.plot = plot;
		display.metric = aura.getMetricByName(metricName);
		display.groupCategory = category;        
		display.groupUser = user;
		display.aura = aura;
        display.timeFrame = timeFrame;
		display.save();
		return ok();
    }

    @Security.Authenticated(AdminAuthenticator.class)
    public Result saveTemplate(){
        JsonNode json = request().body().asJson();
        String auraName = json.get("auraName").asText();
        Aura aura = Aura.getAuraByName(auraName);
        if(aura == null){
            System.out.println("Aura " + auraName + " doesn't exist");
            return badRequest();
        }
        String template = json.get("template").asText();        
        aura.template = template;
        aura.save();
        aura = Aura.getAuraByName(auraName);
        return ok();
    }
		
    public Result createMetricEntry(String auraName, String metricName, String user, String category, Integer value) {

    	// validate public code

        Aura aura = Aura.getAuraByName(auraName);
		if(aura == null){
			System.out.println("Aura " + auraName + " doesn't exist.");
			return ok(message.render("Aura doesn't exist"));
		} 

		Metric metric = aura.getMetricByName(metricName);
		if(metric == null){
			System.out.println("Metric " + metricName + " doesn't exist.");
			return ok(message.render("Metric doesn't exist"));
		}

		MetricEntry metricEntry = new MetricEntry();
                
        if(user != null){ // if user is provided we give a profile accordingly
            Profile profile = Profile.getProfileByName(user);
            if(profile == null){
                profile = new Profile();
                profile.name = user;
                profile.registerDate = new Date();
                profile.save();                
            }
            metricEntry.profile = profile;
        } else { // if no user is provided we use a cookie to track a profile
        	play.mvc.Http.Cookie c = request().cookies().get("aurasma-customanalytics");
    		if(c != null) metricEntry.profile = Profile.getProfileById(c.value());
    		if(metricEntry.profile == null){
    			Profile profile = new Profile();
    			profile.registerDate = new Date();			
    			profile.save();
    			metricEntry.profile = profile;			
    			response().setCookie("aurasma-customanalytics", ""+profile.id, 86400);
    		}
        }
		
		// Category: can be null
        metricEntry.category = category;

        if(value == null) metricEntry.value = 1;
		else metricEntry.value = value;

        metricEntry.date = new Date();
		metricEntry.metric = metric;
		metricEntry.save();

		metric.metricEntries.add(metricEntry);
		metric.save();
		System.out.println("Metric entry for category " + category + " with value " + value + " created and assigned to user " + metricEntry.profile.id);
        
        if(metric.redirectAddress == null)
            return auralytics(aura.name);
        else
            return ok(message.render(metric.redirectAddress));
    }

    public Result createNamedMetric(String auraName, String metricName, String category, Integer value){
        return ok(views.html.nameMetricEntry.render(auraName,metricName,category,value));
    }

    public Result createMultiMetricEntries(String auraName, String metrics, String categories) {

        // validate public code
        String[] metricsArray = metrics.split(",");
        String[] categoriesArray = categories.split(",");

        Aura aura = Aura.getAuraByName(auraName);
        if(aura == null){
            System.out.println("Aura " + auraName + " doesn't exist.");
            return ok(message.render("Aura doesn't exist"));
        }

        // User: if provided assign
        Profile profile = null;
        play.mvc.Http.Cookie c = request().cookies().get("aurasma-customanalytics");
        if(c != null) profile = Profile.getProfileById(c.value());
        if(profile == null){
            profile = new Profile();
            profile.registerDate = new Date();          
            profile.save();
            profile = profile;          
            response().setCookie("aurasma-customanalytics", ""+profile.id, 86400);
        }

        for(int i=0; i < metricsArray.length; i++){

            Metric metric = aura.getMetricByName(metricsArray[i]);
            if(metric == null){
                System.out.println("Metric " + metricsArray[i] + " doesn't exist.");
                return ok(message.render("Metric doesn't exist"));
            }

            // validate if all metrics exist and if they are the same size as the values/categories!!
        
            MetricEntry metricEntry = new MetricEntry();
            
            metricEntry.profile = profile;
            
            // Category: can be null
            metricEntry.category = categoriesArray[i];

            metricEntry.value = 1;

            metricEntry.date = new Date();
            metricEntry.metric = metric;
            metricEntry.save();

            metric.metricEntries.add(metricEntry);
            metric.save();
            System.out.println("Metric entry for category " + categoriesArray[i] + " with value " + metricEntry.value + " created and assigned to user " + metricEntry.profile.id);
        }
        
        /*if(metric.redirectAddress == null)
            return auralytics(aura.name);
        else*/
        return ok(message.render("Registration complete"));
    }

    public Result deleteMetric(String auraName, Long metricId){
        Aura aura = Aura.getAuraByName(auraName);
        if(aura == null){
            System.out.println("Aura " + auraName + " doesn't exist.");
            return ok(message.render("Aura doesn't exist"));
        } 

        Metric metric = aura.getMetricById(metricId);
        if(metric == null){
            System.out.println("Metric " + metricId + " doesn't exist.");
            return ok(message.render("Metric doesn't exist"));
        }

        for(MetricDisplay display : MetricDisplay.getDisplaysByMetric(metricId)){
            display.delete();
        }

        metric.delete();
        return redirect("/auraAdmin/" + aura.name);
    }

    public Result deleteDisplay(String auraName, Long displayId){
        Aura aura = Aura.getAuraByName(auraName);
        if(aura == null){
            System.out.println("Aura " + auraName + " doesn't exist.");
            return ok(message.render("Aura doesn't exist"));
        } 

        MetricDisplay display = aura.getDisplayById(displayId);
        if(display == null){
            System.out.println("Display " + displayId + " doesn't exist.");
            return ok(message.render("Display doesn't exist"));
        }
        display.delete();
        return redirect("/auraAdmin/" + aura.name);
    }

}