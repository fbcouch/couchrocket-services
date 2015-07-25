package controllers;

import akka.actor.ActorRef;
import akka.actor.Cancellable;
import akka.actor.Props;
import com.fasterxml.jackson.databind.JsonNode;
import models.GameRoom;
import models.Pinger;
import play.libs.Akka;
import play.libs.F;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.WebSocket;
import scala.concurrent.duration.Duration;

import static java.util.concurrent.TimeUnit.SECONDS;

/**
 * Created by jami on 7/25/15.
 */
public class Socket extends Controller {
    public static WebSocket<JsonNode> ws(String username) {
        return new WebSocket<JsonNode>() {
            public void onReady(WebSocket.In<JsonNode> in, WebSocket.Out<JsonNode> out) {
                try {
                    GameRoom.join(username, in, out);
                } catch(Exception e) {
                    e.printStackTrace();
                }
            }

        };
    }

    public static Result pingJs() {
        return ok(views.js.sockets.ping.render("test"));
    }

    public static Result index() {
        return ok(views.html.sockets.index.render());
    }
}
