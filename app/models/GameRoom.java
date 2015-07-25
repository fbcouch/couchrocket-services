package models;

import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.UntypedActor;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import play.libs.Akka;
import play.libs.F;
import play.libs.Json;
import play.mvc.WebSocket;
import scala.concurrent.Await;
import scala.concurrent.duration.Duration;

import java.util.HashMap;
import java.util.Map;

import static java.util.concurrent.TimeUnit.*;
import static akka.pattern.Patterns.ask;

/**
 * Created by jami on 7/25/15.
 */
public class GameRoom extends UntypedActor {
    static ActorRef defaultGame = Akka.system().actorOf(Props.create(GameRoom.class));

    public static void join(final String username, WebSocket.In<JsonNode> in, WebSocket.Out<JsonNode> out) throws Exception {
        String result = (String) Await.result(ask(defaultGame, new Join(username, out), 1000), Duration.create(1, SECONDS));

        if("OK".equals(result)) {

            // For each event received on the socket,
            in.onMessage(new F.Callback<JsonNode>() {
                public void invoke(JsonNode event) {

                    if (event.get("type").asText().equals("gameUpdate")) {
                        defaultGame.tell(new GameUpdate(username, event.get("data")), null);
                    } else {
                        // Send a Chat message to the room.
                        defaultGame.tell(new Chat(username, event.get("text").asText()), null);
                    }

                }
            });

            // When the socket is closed.
            in.onClose(new F.Callback0() {
                public void invoke() {

                    // Send a Quit message to the room.
                    defaultGame.tell(new Quit(username), null);

                }
            });

        } else {

            // Cannot connect, create a Json error.
            ObjectNode error = Json.newObject();
            error.put("error", result);

            // Send the error to the socket.
            out.write(error);

        }
    }

    Map<String, WebSocket.Out<JsonNode>> members = new HashMap<String, WebSocket.Out<JsonNode>>();

    @Override
    public void onReceive(Object message) throws Exception {

        if(message instanceof Join) {

            // Received a Join message
            Join join = (Join)message;

            // Check if this username is free.
            if(members.containsKey(join.username)) {
                getSender().tell("This username is already used", getSelf());
            } else {
                members.put(join.username, join.channel);
                notifyAll("join", join.username, "has entered the room");
                getSender().tell("OK", getSelf());
            }

        } else if(message instanceof Chat)  {

            // Received a Chat message
            Chat talk = (Chat)message;

            notifyAll("talk", talk.username, talk.text);

        } else if(message instanceof Quit)  {

            // Received a Quit message
            Quit quit = (Quit)message;

            members.remove(quit.username);

            notifyAll("quit", quit.username, "has left the room");

        } else {
            unhandled(message);
        }

    }

    // Send a Json event to all members
    public void notifyAll(String kind, String user, String text) {
        for(WebSocket.Out<JsonNode> channel: members.values()) {

            ObjectNode event = Json.newObject();
            event.put("kind", kind);
            event.put("user", user);
            event.put("message", text);

            ArrayNode m = event.putArray("members");
            for(String u: members.keySet()) {
                m.add(u);
            }

            channel.write(event);
        }
    }

    public static class Join {

        final String username;
        final WebSocket.Out<JsonNode> channel;

        public Join(String username, WebSocket.Out<JsonNode> channel) {
            this.username = username;
            this.channel = channel;
        }

    }

    public static class Chat {

        final String username;
        final String text;

        public Chat(String username, String text) {
            this.username = username;
            this.text = text;
        }

    }

    public static class GameUpdate {
        final String username;
        final JsonNode data;

        public GameUpdate(String username, JsonNode data) {
            this.username = username;
            this.data = data;
        }
    }

    public static class Quit {

        final String username;

        public Quit(String username) {
            this.username = username;
        }

    }
}
