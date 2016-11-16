package controllers;

import play.*;
import play.mvc.*;
import play.mvc.Http.*;

import models.*;

public class SuperAdminAuthenticator extends Security.Authenticator {

    @Override
    public String getUsername(Context ctx) {        
        String aura = ctx.session().get("aura");       
        if(aura.equals("superadmin")) return "superadmin";
        else return null;
    }

    @Override
    public Result onUnauthorized(Context ctx) {
        return redirect("/");
    }
    
}