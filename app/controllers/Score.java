package controllers;

import actions.CorsComposition;
import com.fasterxml.jackson.databind.JsonNode;
import play.libs.Json;
import play.mvc.BodyParser;
import play.mvc.Controller;
import play.mvc.Result;

import java.util.List;

/**
 * Created by jami on 7/25/15.
 */
public class Score extends Controller {
    @CorsComposition.Cors
    public static Result index() {
        List<models.Score> scores = models.Score.find.orderBy("score desc").setMaxRows(5).findList();
        return ok(play.libs.Json.toJson(scores));
    }

    @CorsComposition.Cors
    @BodyParser.Of(BodyParser.Json.class)
    public static Result create() {
        JsonNode json = request().body().asJson();
        models.Score scoreModel = new models.Score();
        scoreModel.name = json.findPath("name").textValue();
        scoreModel.score = json.findPath("score").longValue();
        scoreModel.save();
        if (scoreModel.id != null) {
            return ok(Json.toJson(scoreModel));
        } else {
            return badRequest("Unable to save model");
        }
    }
}
