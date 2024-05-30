package com.example.tickup;

import androidx.activity.result.ActivityResultLauncher;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.journeyapps.barcodescanner.CaptureActivity;
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;

import okhttp3.OkHttpClient;

public class VerifyTicket extends AppCompatActivity {
    private Button btnScan;
    private String url, idIngresso;
    private OkHttpClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_ticket);

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

    ActivityResultLauncher<ScanOptions> barLaucher = registerForActivityResult(new ScanContract(), result->{
       if (result.getContents() != null){
           AlertDialog.Builder builder = new AlertDialog.Builder(VerifyTicket.this);
           builder.setTitle("Resultado");
           idIngresso = result.getContents();

           //builder.setMessage(resultado(idIngresso));
           builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
               @Override
               public void onClick(DialogInterface dialog, int i) {
                   dialog.dismiss();
               }
           }).show();
       }
    });

    private String resultado(String idIngresso){
        url = "https://tick-up-1fb4969b94c5.herokuapp.com/api/Ingresso/Verificar/" + idIngresso;

        return url;
    }
}