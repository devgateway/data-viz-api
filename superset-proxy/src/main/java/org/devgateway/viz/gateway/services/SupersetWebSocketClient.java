package org.devgateway.viz.gateway.services;
import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import org.json.JSONException;
import org.json.JSONObject;
public class SupersetWebSocketClient {


    private Socket socket;

    public void connect(String supersetUrl, String channelId) {
        try {
            IO.Options options = new IO.Options();
            options.forceNew = true;
            options.reconnection = true;

            // Superset WebSocket endpoint, typically /ws
            socket = IO.socket("ws://superset.alive.dgstg.org/socket.io/?transport=websocket" , options);

            socket.on(Socket.EVENT_CONNECT, args -> {
                System.out.println("Connected to Superset WS");

                // Subscribe to your channel (this is how Superset does it)
                JSONObject subscribePayload = new JSONObject();
                try {
                    subscribePayload.put("channel", channelId);
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
                socket.emit("subscribe", subscribePayload);
            });

            socket.on(channelId, new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    System.out.println("Received message on channel: " + channelId);
                    System.out.println(args[0].toString());
                }
            });

            socket.on(Socket.EVENT_DISCONNECT, args -> System.out.println("Disconnected"));

            socket.connect();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public Boolean gotResults() {
        return false;
    }

    public void disconnect() {
        if (socket != null) {
            socket.disconnect();
            socket.close();
        }
    }

  /*  public static void main(String[] args) {
        SupersetWebSocketClient client = new SupersetWebSocketClient();
        String supersetHost = "http://localhost:8088";  // adjust your Superset URL
        String channelId = "06d1fc25-6ed3-49e5-9e28-4a0dd3023e31";  // from your API response
        client.connect(supersetHost, channelId);
    }
   */
}
