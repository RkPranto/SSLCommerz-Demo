package com.prantology.paymentgateway;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.sslwireless.sslcommerzlibrary.model.initializer.SSLCommerzInitialization;
import com.sslwireless.sslcommerzlibrary.model.response.TransactionInfoModel;
import com.sslwireless.sslcommerzlibrary.model.util.CurrencyType;
import com.sslwireless.sslcommerzlibrary.model.util.SdkType;
import com.sslwireless.sslcommerzlibrary.view.singleton.IntegrateSSLCommerz;
import com.sslwireless.sslcommerzlibrary.viewmodel.listener.TransactionResponseListener;

public class MainActivity extends AppCompatActivity {
    String TAG = "PAY";
    TextView tv;
    EditText et;
    Button btn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tv = findViewById(R.id.textView);

        et = findViewById(R.id.editText);

        btn = findViewById(R.id.button);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int totalAmmount = Integer.valueOf(et.getText().toString());
                SSLCommerzInitialization sslCommerzInitialization = new SSLCommerzInitialization(
                        "store_id",
                        "password", totalAmmount, CurrencyType.BDT,
                        "transition_id", "product_category", SdkType.TESTBOX); //if you use live version then you will write "SdkType.LIVE"


                IntegrateSSLCommerz
                        .getInstance(MainActivity.this)
                        .addSSLCommerzInitialization(sslCommerzInitialization)
                        .buildApiCall(new TransactionResponseListener() {
                            @Override
                            public void transactionSuccess(TransactionInfoModel transactionInfoModel) {
                                Toast.makeText(MainActivity.this, "Bank Trans: "+transactionInfoModel.getBankTranId(), Toast.LENGTH_LONG).show();

                                if (transactionInfoModel.getRiskLevel().equals("0")) {
                                    Log.d(TAG, "Transaction Successfully completed");
                                    Log.d(TAG, "Trans: "+transactionInfoModel.getTranId());
                                    Log.d(TAG, "bank Trans: "+transactionInfoModel.getBankTranId());
                                    Log.d(TAG, "ammout: "+transactionInfoModel.getAmount());
                                    Log.d(TAG," store amm: " +transactionInfoModel.getStoreAmount());
                                    Log.d(TAG," Date: " +transactionInfoModel.getTranDate());
                                    Log.d(TAG, " Status; "+ transactionInfoModel.getStatus());
                                    tv.setText("Transaction Successfully completed");
                                    et.setText(null);
                                }
                                // Payment is success but payment is not complete yet. Card on hold now.
                                else {
                                    Log.d(TAG, "Transaction in risk. Risk Title : " + transactionInfoModel.getRiskTitle());
                                    tv.setText("Transaction in risk.");
                                    et.setText(null);
                                }
                            }

                            @Override
                            public void transactionFail(String s) {
                                Log.d(TAG, "Failed");
                            }

                            @Override
                            public void merchantValidationError(String s) {
                                Log.d(TAG, "ERROR: "+s);
                            }
                        });
            }
        });


    }
}
