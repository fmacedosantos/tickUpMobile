package com.example.tickup;

import androidx.activity.result.ActivityResultLauncher;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;

public class VerifyTicket extends AppCompatActivity {
    private Button scanBtn;
    private String url;
    private OkHttpClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_ticket);

        client = new OkHttpClient();

        scanBtn = findViewById(R.id.scanBtn);

        scanBtn.setOnClickListener(new View.OnClickListener() {
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
            String ticketId = result.getContents();

            verifyTicket(ticketId, new VerificationCallback() {
                @Override
                public void onResult(boolean isValid) {
                    String message = isValid ? "Ingresso válido" : "Ingresso inválido";
                    int style = isValid ? R.style.DialogSuccess : R.style.DialogFailure;

                    AlertDialog.Builder builder = new AlertDialog.Builder(VerifyTicket.this, style);
                    builder.setTitle("Resultado");
                    builder.setMessage(message);
                    builder.setPositiveButton("OK", (dialog, i) -> dialog.dismiss()).show();
                    if (isValid) {
                        deleteTicket(ticketId);
                    }
                }
            });
        }
    });

    private void verifyTicket(String ticketId, VerificationCallback callback) {
        url = "https://tick-up-1fb4969b94c5.herokuapp.com/api/Ingresso/Verificar/" + ticketId;

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

    private void deleteTicket(String ticketId) {
        url = "https://tick-up-1fb4969b94c5.herokuapp.com/api/Ingresso/Deletar/" + ticketId;

        Request request = new Request.Builder()
                .url(url)
                .delete()
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(() -> Toast.makeText(VerifyTicket.this, "Falha ao excluir ingresso: " + e.getMessage(), Toast.LENGTH_LONG).show());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                runOnUiThread(() -> {
                    if (!response.isSuccessful()) {
                        Toast.makeText(VerifyTicket.this, "Falha ao excluir ingresso: " + response.message(), Toast.LENGTH_LONG).show();
                    }
                });
            }
        });
    }

    interface VerificationCallback {
        void onResult(boolean isValid);
    }
}
