package com.example.tickup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.TypedValue;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MyTickets extends AppCompatActivity {
    private TextView txtViewIngressos;
    private LinearLayout qrCodeContainer;
    private String email, url;
    private OkHttpClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_tickets);

        txtViewIngressos = findViewById(R.id.teste);
        qrCodeContainer = findViewById(R.id.qrCodeContainer);
        email = getIntent().getStringExtra("emailUsuario");
        url = "https://tick-up-1fb4969b94c5.herokuapp.com/api/Compra/Usuario/" + email;
        obterIngressos(email);
    }

    private void obterIngressos(String email) {
        client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(url)
                .build();

        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        txtViewIngressos.setText("Requisição falhou: " + e.getMessage());
                    }
                });
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    final String json = response.body().string();

                    Gson gson = new Gson();
                    Type listType = new TypeToken<List<Ingresso>>() {}.getType();
                    List<Ingresso> ingressos = gson.fromJson(json, listType);

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (ingressos != null && !ingressos.isEmpty()) {
                                qrCodeContainer.removeAllViews();
                                for (Ingresso ingresso : ingressos) {
                                    adicionarIngressosView(ingresso);
                                }
                                txtViewIngressos.setText("");
                            } else {
                                txtViewIngressos.setText("O usuário cadastrado não possui ingressos.");
                            }
                        }
                    });
                } else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            txtViewIngressos.setText("Falha em encontrar os ingressos: " + response.code());
                        }
                    });
                }
            }
        });
    }

    private void adicionarIngressosView(Ingresso ingresso) {
        TextView title = new TextView(this);
        title.setText("Evento: " + ingresso.getNomeEvento());

        title.setTextColor(getResources().getColor(R.color.cinza));
        title.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
        Typeface typeface = ResourcesCompat.getFont(this, R.font.jockey_one);
        title.setTypeface(typeface);

        ImageView qrCodeView = new ImageView(this);
        Bitmap qrCodeBitmap = gerarQRCode(ingresso.getIdIngresso());
        if (qrCodeBitmap != null) {
            qrCodeView.setImageBitmap(qrCodeBitmap);
        }

        qrCodeContainer.addView(title);
        qrCodeContainer.addView(qrCodeView);
    }

    private Bitmap gerarQRCode(String text) {
        try {
            BitMatrix bitMatrix = new MultiFormatWriter().encode(text, BarcodeFormat.QR_CODE, 600, 600);
            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
            return barcodeEncoder.createBitmap(bitMatrix);
        } catch (WriterException e) {
            e.printStackTrace();
            return null;
        }
    }
}
