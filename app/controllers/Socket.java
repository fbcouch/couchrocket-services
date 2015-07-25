package controllers;

import akka.actor.ActorRef;
import akka.actor.Cancellable;
import akka.actor.Props;
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
    public static WebSocket<String> pingWs() {
        return new WebSocket<String>() {
            public void onReady(WebSocket.In<String> in, WebSocket.Out<String> out) {
                final ActorRef pingActor = Akka.system().actorOf(Props.create(Pinger.class, in, out));
                final Cancellable cancellable = Akka.system().scheduler().schedule(Duration.create(1, SECONDS),
                        Duration.create(1, SECONDS),
                        pingActor,
                        "Tick",
                        Akka.system().dispatcher(),
                        null
                );

                in.onClose(new F.Callback0() {
                    @Override
                    public void invoke() throws Throwable {
                        cancellable.cancel();
                    }
                });
            }

        };
    }

    public static Result pingJs() {
        return ok(views.js.sockets.ping.render());
    }

    public static Result index() {
        return ok(views.html.sockets.index.render());
    }
}
