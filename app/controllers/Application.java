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
    	String login = session().get("aura");
        if(login == null) return login();
    	if(login.equals("superadmin")) return ok(index.render(Aura.find.all(),true));
    	else{    		
    		return ok(index.render(Aura.find.where().eq("id", Long.parseLong(login)).findList(),false));
    	}
    }
    
    public Result login(){
        return ok(login.render());
    }

    public Result logout(){
    	session().clear();
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
    		System.out.println(code);
    		System.out.println(auraName);
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
		
    public Result createMetricEntry(String auraName, String metricName, String user, String category, Integer value, String mode) {

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

		// mode: request username, category or both. Maybe be able to list existing between categories in some cases?
        if(mode == null) ok(auralytics.render(aura.name, aura.metrics, aura.displays));
        else if(mode.equals("form")) return ok(views.html.nameMetricEntry.render(auraName,metricName,value));
        
        // User: if provided assign
    	play.mvc.Http.Cookie c = request().cookies().get("aurasma-customanalytics");
		if(c != null) metricEntry.profile = Profile.getProfileById(c.value());
		if(metricEntry.profile == null){
			Profile profile = new Profile();
			profile.registerDate = new Date();			
			profile.save();
			metricEntry.profile = profile;			
			response().setCookie("aurasma-customanalytics", ""+profile.id, 86400);
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
        return auraAdmin(aura.name);
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
        return auraAdmin(aura.name);
    }

}