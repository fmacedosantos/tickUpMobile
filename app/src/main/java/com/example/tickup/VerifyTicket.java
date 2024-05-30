package com.example.tickup;

import androidx.activity.result.ActivityResultLauncher;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.journeyapps.barcodescanner.CaptureActivity;
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;

public class VerifyTicket extends AppCompatActivity {
    private Button btnScan;
    private String url;
    private OkHttpClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_ticket);

        client = new OkHttpClient();

        btnScan = findViewById(R.id.btnScan);

        btnScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                scanCode();
            }
        });
    }

    private void scanCode() {
        ScanOptions options = new ScanOptions();
        options.setPrompt("Verificando validade do ingresso...");
        options.setBeepEnabled(true);
        options.setOrientationLocked(true);
        options.setCaptureActivity(CaptureAct.class);
        barLaucher.launch(options);
    }

    ActivityResultLauncher<ScanOptions> barLaucher = registerForActivityResult(new ScanContract(), result -> {
        if (result.getContents() != null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(VerifyTicket.this);
            builder.setTitle("Resultado");
            String idIngresso = result.getContents();

            verificarIngresso(idIngresso, new VerificationCallback() {
                @Override
                public void onResult(boolean isValid) {
                    String message = isValid ? "Ingresso válido" : "Ingresso inválido";
                    builder.setMessage(message);
                    builder.setPositiveButton("OK", (dialog, i) -> dialog.dismiss()).show();
                }
            });
        }
    });

    private void verificarIngresso(String idIngresso, VerificationCallback callback) {
        url = "https://tick-up-1fb4969b94c5.herokuapp.com/api/Ingresso/Verificar/" + idIngresso;

        Request request = new Request.Builder()
                .url(url)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(() -> callback.onResult(false));
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    runOnUiThread(() -> callback.onResult(true));
                } else {
                    runOnUiThread(() -> callback.onResult(false));
                }
            }
        });
    }

    interface VerificationCallback {
        void onResult(boolean isValid);
    }
}
