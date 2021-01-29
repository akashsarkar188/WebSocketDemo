package akashsarkar188.network.websocketdemo;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;

public class MainActivity extends AppCompatActivity {

    private TextView dataTextView;
    private MaterialButton disconnectButton, connectButton, clearButton;
    private EditText messageEditText;
    private WebSocket webSocket;
    private boolean connectionStatus = false;
    private OkHttpClient client;
    private Request request;
    private ScrollView dataScrollView;
    private WebSocketHelper listener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        initView();
        onClickListeners();
        startWebSocket();
    }

    private void initView() {
        dataTextView = findViewById(R.id.dataTextView);
        disconnectButton = findViewById(R.id.disconnectButton);
        connectButton = findViewById(R.id.connectButton);
        messageEditText = findViewById(R.id.messageEditText);
        dataScrollView = findViewById(R.id.dataScrollView);
        clearButton = findViewById(R.id.clearButton);
    }

    private void onClickListeners() {

        connectButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!connectionStatus) {
                    webSocket = client.newWebSocket(request, listener);
                    //client.dispatcher().executorService().shutdown();
                }

                if (messageEditText.getText() != null && !messageEditText.getText().toString().isEmpty()) {
                    webSocket.send(messageEditText.getText().toString());
                    output("--> Send : " + messageEditText.getText().toString());
                    messageEditText.setText("");
                }
            }
        });

        disconnectButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                webSocket.close(1010, "User Triggered");
            }
        });

        clearButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                dataTextView.setText("");
            }
        });
    }

    private void startWebSocket() {
        client = new OkHttpClient();
        request = new Request.Builder()
                .url("wss://demo.websocket.me/v3/1?api_key=oCdCMcMPQpbvNjUIzqtvF1d2X2okWpDQj4AwARJuAgtjhzKxVEjQU6IdCjwm&notify_self")
                .build();
        listener = new WebSocketHelper();
    }

    private void output(final String txt) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                dataTextView.setText(dataTextView.getText().toString() + "\n\n" + txt);
                dataScrollView.fullScroll(View.FOCUS_DOWN);
            }
        });
    }

    private final class WebSocketHelper extends WebSocketListener {

        @Override
        public void onClosed(@NotNull WebSocket webSocket, int code, @NotNull String reason) {
            Log.e("XXX", "onClosed: " + reason);
            output("Closed : " + reason);
            connectionStatus = false;
            connectButton.setText("Connect");
            super.onClosed(webSocket, code, reason);
        }

        @Override
        public void onClosing(@NotNull WebSocket webSocket, int code, @NotNull String reason) {
            Log.e("XXX", "onClosing: " + reason);
            output("Closing : " + reason);
            super.onClosing(webSocket, code, reason);
        }

        @Override
        public void onFailure(@NotNull WebSocket webSocket, @NotNull Throwable t, @Nullable Response response) {
            Log.e("XXX", "onFailure: " + t);
            output("Failure : " + t.getMessage());
            super.onFailure(webSocket, t, response);
        }

        @Override
        public void onMessage(@NotNull WebSocket webSocket, @NotNull String text) {
            Log.e("XXX", "onMessage: " + text);
            output("Received : " + text);
            super.onMessage(webSocket, text);
        }

        @Override
        public void onOpen(@NotNull WebSocket webSocket, @NotNull Response response) {
            Log.e("XXX", "onOpen: " + response.message());
            output("Open : " + response);
            connectionStatus = true;
            connectButton.setText("Send");
            super.onOpen(webSocket, response);
        }
    }
}