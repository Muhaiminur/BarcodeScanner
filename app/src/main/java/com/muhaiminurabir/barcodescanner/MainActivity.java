package com.muhaiminurabir.barcodescanner;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
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

import com.facebook.ads.*;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private AdView adView1;

    TextView resultview;
    Button capture;
    //CircleButton capture;
    static String result_String="Scane is not complete";
    int BARCODE_READER_REQUEST_CODE = 1;

    private final String TAG = MainActivity.class.getSimpleName();
    private NativeAd nativeAd;

    private LinearLayout nativeAdContainer;
    private LinearLayout adView2;




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
        adView1 = new AdView(this, "1466405976804656_1466406060137981", AdSize.BANNER_HEIGHT_50);

        // Find the Ad Container
        LinearLayout adContainer = (LinearLayout) findViewById(R.id.banner_container);

        // Add the ad view to your activity layout
        adContainer.addView(adView1);

        // Request an ad
        adView1.loadAd();


        loadNativeAd();

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
        if (adView1 != null) {
            adView1.destroy();
        }
        super.onDestroy();
    }

    private void loadNativeAd() {
        // Instantiate a NativeAd object.
        // NOTE: the placement ID will eventually identify this as your App, you can ignore it for
        // now, while you are testing and replace it later when you have signed up.
        // While you are using this temporary code you will only get test ads and if you release
        // your code like this to the Google Play your users will not receive ads (you will get a no fill error).
        nativeAd = new NativeAd(this, "189975318293620_270939626863855");
        nativeAd.setAdListener(new NativeAdListener() {
            @Override
            public void onMediaDownloaded(Ad ad) {
                // Native ad finished downloading all assets
                Log.e(TAG, "Native ad finished downloading all assets.");
            }

            @Override
            public void onError(Ad ad, AdError adError) {
                // Native ad failed to load
                Log.e(TAG, "Native ad failed to load: " + adError.getErrorMessage());
            }

            @Override
            public void onAdLoaded(Ad ad) {
                // Native ad is loaded and ready to be displayed
                Log.d(TAG, "Native ad is loaded and ready to be displayed!");

                // Race condition, load() called again before last ad was displayed
                if (nativeAd == null || nativeAd != ad) {
                    return;
                }
                // Inflate Native Ad into Container
                inflateAd(nativeAd);
            }

            @Override
            public void onAdClicked(Ad ad) {
                // Native ad clicked
                Log.d(TAG, "Native ad clicked!");
            }

            @Override
            public void onLoggingImpression(Ad ad) {
                // Native ad impression
                Log.d(TAG, "Native ad impression logged!");
            }
        });

        // Request an ad
        nativeAd.loadAd();
    }
    private void inflateAd(NativeAd nativeAd) {

        nativeAd.unregisterView();

        // Add the Ad view into the ad container.
        nativeAdContainer = findViewById(R.id.native_ad_container);
        LayoutInflater inflater = LayoutInflater.from(MainActivity.this);
        // Inflate the Ad view.  The layout referenced should be the one you created in the last step.
        adView2 = (LinearLayout) inflater.inflate(R.layout.native_ad_layout, nativeAdContainer, false);
        nativeAdContainer.addView(adView2);

        // Add the AdChoices icon
        LinearLayout adChoicesContainer = findViewById(R.id.ad_choices_container);
        AdChoicesView adChoicesView = new AdChoicesView(MainActivity.this, nativeAd, true);
        adChoicesContainer.addView(adChoicesView, 0);

        // Create native UI using the ad metadata.
        AdIconView nativeAdIcon = adView2.findViewById(R.id.native_ad_icon);
        TextView nativeAdTitle = adView2.findViewById(R.id.native_ad_title);
        MediaView nativeAdMedia = adView2.findViewById(R.id.native_ad_media);
        TextView nativeAdSocialContext = adView2.findViewById(R.id.native_ad_social_context);
        TextView nativeAdBody = adView2.findViewById(R.id.native_ad_body);
        TextView sponsoredLabel = adView2.findViewById(R.id.native_ad_sponsored_label);
        Button nativeAdCallToAction = adView2.findViewById(R.id.native_ad_call_to_action);

        // Set the Text.
        nativeAdTitle.setText(nativeAd.getAdvertiserName());
        nativeAdBody.setText(nativeAd.getAdBodyText());
        nativeAdSocialContext.setText(nativeAd.getAdSocialContext());
        nativeAdCallToAction.setVisibility(nativeAd.hasCallToAction() ? View.VISIBLE : View.INVISIBLE);
        nativeAdCallToAction.setText(nativeAd.getAdCallToAction());
        sponsoredLabel.setText(nativeAd.getSponsoredTranslation());

        // Create a list of clickable views
        List<View> clickableViews = new ArrayList<>();
        clickableViews.add(nativeAdTitle);
        clickableViews.add(nativeAdCallToAction);

        // Register the Title and CTA button to listen for clicks.
        nativeAd.registerViewForInteraction(
                adView2,
                nativeAdMedia,
                nativeAdIcon,
                clickableViews);
    }
}
