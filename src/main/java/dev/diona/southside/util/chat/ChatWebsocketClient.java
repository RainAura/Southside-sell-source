package dev.diona.southside.util.chat;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import dev.diona.southside.util.authentication.AuthenticationStatus;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import javax.net.ssl.*;
import java.io.File;
import java.io.FileInputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Paths;
import java.security.KeyStore;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.HashMap;

public class ChatWebsocketClient extends WebSocketClient {
    private final Chat chat;
    private final Gson gson = new GsonBuilder().serializeNulls().create();
    public ChatWebsocketClient(final Chat chat, String token) throws Exception {
        super(new URI("ws://38.6.216.26:25565"));
        this.chat = chat;
        // load up the key store
        String STORETYPE = "JKS";
        String STOREPASSWORD = "test@123";
        String KEYPASSWORD = "keypass";

        KeyStore ks = KeyStore.getInstance(STORETYPE);
        ks.load(this.getClass().getResourceAsStream("/sb.jks"), STOREPASSWORD.toCharArray());

        KeyManagerFactory kmf = KeyManagerFactory.getInstance("SunX509");
        kmf.init(ks, KEYPASSWORD.toCharArray());
        TrustManager[] trustAllCerts = new TrustManager[1];
        TrustManager tm = new miTM();
        trustAllCerts[0] = tm;

        SSLContext sslContext = null;
        sslContext = SSLContext.getInstance("TLS");
        sslContext.init(kmf.getKeyManagers(), trustAllCerts, null);
        // sslContext.init( null, null, null ); // will use java's default key and trust store which is sufficient unless you deal with self-signed certificates

        SSLSocketFactory factory = sslContext
                .getSocketFactory();// (SSLSocketFactory) SSLSocketFactory.getDefault();

        //setSocketFactory(factory);
        connect();
    }

    @Override
    public void onOpen(ServerHandshake serverHandshake) {
        System.out.println("Connected");
        final var jsonObject = new JsonObject();
        jsonObject.addProperty("type", "handshake");
        jsonObject.addProperty("name", AuthenticationStatus.INSTANCE.user.getName());
        jsonObject.addProperty("client", "Southside");
        send(Xor.e(gson.toJson(jsonObject)));
    }

    @Override
    public void onMessage(String s) {
        s = Xor.e(s);
        chat.handleMessage(gson.fromJson(s, JsonObject.class));
    }

    @Override
    public void onClose(int i, String s, boolean b) {
        Chat.onConnectionLost();
    }

    @Override
    public void onError(Exception e) {
        if (isClosed()) {
            Chat.onConnectionLost();
            e.printStackTrace();
        }
    }

    static class miTM implements TrustManager,X509TrustManager {
        public X509Certificate[] getAcceptedIssuers() {
            return null;
        }

        public boolean isServerTrusted(X509Certificate[] certs) {
            return true;
        }

        public boolean isClientTrusted(X509Certificate[] certs) {
            return true;
        }

        public void checkServerTrusted(X509Certificate[] certs, String authType)
                throws CertificateException {
            return;
        }

        public void checkClientTrusted(X509Certificate[] certs, String authType)
                throws CertificateException {
            return;
        }
    }
}
