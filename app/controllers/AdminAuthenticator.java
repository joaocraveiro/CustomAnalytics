package controllers;

import play.*;
import play.mvc.*;
import play.mvc.Http.*;

import models.*;

public class AdminAuthenticator extends Security.Authenticator {

    @Override
    public String getUsername(Context ctx) {
        return ctx.session().get("aura");
    }

    @Override
    public Result onUnauthorized(Context ctx) {
        return redirect(routes.Application.login());
    }
    
}