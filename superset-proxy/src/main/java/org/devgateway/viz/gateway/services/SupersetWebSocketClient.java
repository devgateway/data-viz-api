package org.devgateway.viz.gateway.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import java.util.Map;

public class SupersetWebSocketClient {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    private Socket socket;

    public void connect(String supersetUrl, String channelId) {
        try {
            IO.Options options = new IO.Options();
            options.forceNew = true;
            options.reconnection = true;

            socket = IO.socket(supersetUrl + "/socket.io/?transport=websocket", options);

            socket.on(Socket.EVENT_CONNECT, args -> {
                System.out.println("Connected to Superset WS");
                try {
                    String subscribePayload = MAPPER.writeValueAsString(Map.of("channel", channelId));
                    socket.emit("subscribe", subscribePayload);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
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
}
