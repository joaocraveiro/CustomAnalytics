package controllers;

import play.mvc.*;
import play.db.jpa.*;
import views.html.*;
import models.*;

import java.util.Date;
import java.util.List;

/**
 * Created by craveiro on 03-09-2015.
 */

public class Application extends Controller {

    @Transactional
    public Result index() {
        List<Aura> auras = (List<Aura>) JPA.em().createQuery("select a from Aura a").getResultList();
        return ok(index.render(auras));
    }

    @Transactional
    public Result auralytics(String auraName) {
        Aura aura;
        /*try {
            aura = JPA.em().createQuery("from Aura where name = :auraName", Aura.class)
						   .setParameter("auraName", auraName)
						   .getSingleResult();
        } catch(Exception e)
        {*/
            aura = new Aura();
            aura.name = auraName;
            JPA.em().persist(aura);
        //}

        //List<Metric> metrics = (List<Metric>) JPA.em().createQuery("select m from Metric m").getResultList();		
        return ok(auralytics.render(aura.metrics));
    }


    @Transactional
    public Result addMetric(String auraName, String name, Integer value) {
        Aura aura = JPA.em().createQuery("from Aura where name = :auraName", Aura.class)
                .setParameter("auraName", auraName).getSingleResult();
        Metric metric = new Metric();
        metric.name = name;
        metric.value = value;
        metric.date = new Date();
        metric.aura = aura;
        aura.metrics.add(metric);
        JPA.em().persist(aura);

        List<Metric> metrics = aura.metrics;
        return redirect(routes.Application.auralytics(auraName));
    }

}