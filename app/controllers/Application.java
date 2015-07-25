package controllers;

import actions.CorsComposition;

import play.mvc.*;

public class Application extends Controller {
    @CorsComposition.Cors
    @BodyParser.Of(value = BodyParser.Text.class, maxLength = 10 * 1024)
    public static Result options(String any) {
        return ok();
    }
}
