package com.muhaiminurabir.barcodescanner;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.ads.AdSize;
import com.facebook.ads.AdView;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.vision.barcode.Barcode;
import com.muhaiminurabir.barcodescanner.barcode.BarcodeCaptureActivity;

import at.markushi.ui.CircleButton;

public class MainActivity extends AppCompatActivity {
    private AdView adView;

    TextView resultview;
    Button capture;
    //CircleButton capture;
    static String result_String="Scane is not complete";
    int BARCODE_READER_REQUEST_CODE = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        resultview=(TextView)findViewById(R.id.cam_qr);
        Intent intent = new Intent(getApplication(), BarcodeCaptureActivity.class);
        startActivityForResult(intent, BARCODE_READER_REQUEST_CODE);
        capture=(Button)findViewById(R.id.start_button);
        capture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplication(), BarcodeCaptureActivity.class);
                startActivityForResult(intent, BARCODE_READER_REQUEST_CODE);
            }
        });
        // Instantiate an AdView view
        adView = new AdView(this, "1466405976804656_1466406060137981", AdSize.BANNER_HEIGHT_50);

        // Find the Ad Container
        LinearLayout adContainer = (LinearLayout) findViewById(R.id.banner_container);

        // Add the ad view to your activity layout
        adContainer.addView(adView);

        // Request an ad
        adView.loadAd();
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        /*if(requestCode == REQUEST_CODE_FOR_QR && resultCode == RESULT_OK && data!= null){
            result_String=data.getStringExtra(result_String);
            resultview.setText(result_String);
        }*/
        if (requestCode == BARCODE_READER_REQUEST_CODE) {
            if (resultCode == CommonStatusCodes.SUCCESS) {
                if (data != null) {
                    Barcode barcode = data.getParcelableExtra(BarcodeCaptureActivity.BarcodeObject);
                    //statusMessage.setText(R.string.barcode_success);
                    resultview.setText(barcode.displayValue);
                    ClipData myClip = ClipData.newPlainText("text", barcode.displayValue);
                    ClipboardManager _clipboard = (ClipboardManager) getApplication().getSystemService(getApplicationContext().CLIPBOARD_SERVICE);
                    _clipboard.setPrimaryClip(myClip);

                    Toast.makeText(getApplicationContext(), "Text Copied to  Clipboard",
                            Toast.LENGTH_SHORT).show();
                    //Log.d(TAG, "Barcode read: " + barcode.displayValue);
                } else {
                    resultview.setText(R.string.no_barcode_captured);
                }
            } else {
                resultview.setText(String.format(getString(R.string.barcode_error_format),
                        CommonStatusCodes.getStatusCodeString(resultCode)));
            }
        }
        else {
            super.onActivityResult(requestCode, resultCode, data);
        }

    }
    @Override
    protected void onDestroy() {
        if (adView != null) {
            adView.destroy();
        }
        super.onDestroy();
    }
}
