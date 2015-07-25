package controllers;

import com.avaje.ebean.Ebean;
import com.fasterxml.jackson.databind.JsonNode;
import models.Score;

import play.libs.Json;
import play.mvc.*;

import java.util.List;

public class Application extends Controller {

    public static Result index() {
        List<Score> scores = Score.find.orderBy("score desc").setMaxRows(5).findList();
        return ok(play.libs.Json.toJson(scores));
    }

    @BodyParser.Of(BodyParser.Json.class)
    public static Result create() {
        JsonNode json = request().body().asJson();
        Score scoreModel = new Score();
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
