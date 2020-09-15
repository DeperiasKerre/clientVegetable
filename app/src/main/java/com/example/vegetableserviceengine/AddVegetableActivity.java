package com.example.vegetableserviceengine;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.example.vegetableserviceengine.models.Vegetable;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

public class AddVegetableActivity extends AppCompatActivity {

    TextView title;
    EditText nameEditText, quantityEditText, priceText;
    ProgressBar progressBar;
    Button submitBtn;
    Vegetable vegetable;
    String submitUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_vegetable);

        // TODO Add Back button on the NavBar;
        // TODO Add single source of truth;

        submitUrl = getString(R.string.api_url);
        title = findViewById(R.id.title);
        nameEditText = findViewById(R.id.name);
        quantityEditText = findViewById(R.id.quantity);
        priceText = findViewById(R.id.price);
        submitBtn = findViewById(R.id.submit);
        progressBar = findViewById(R.id.progressBar);

        if (getIntent().hasExtra("vegetable")) {
            vegetable = getIntent().getParcelableExtra("vegetable");
            if (vegetable != null) {
                Log.e("Vegetable", vegetable.describeVegetable());
                nameEditText.setText(vegetable.getName());
                quantityEditText.setText(String.valueOf(vegetable.getQuantity()));
                priceText.setText(String.valueOf(vegetable.getPrice()));
                title.setText("Edit Vegetable");
                submitUrl = getString(R.string.api_url) + "update/" + String.valueOf(vegetable.getId());
            }
        }


        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                submitBtn.setVisibility(View.GONE);
                progressBar.setVisibility(View.VISIBLE);

                final String name = nameEditText.getText().toString();
                final String price = priceText.getText().toString();
                final String quantity = quantityEditText.getText().toString();



                StringRequest request = new StringRequest(Request.Method.POST, submitUrl,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                Toast.makeText(getApplicationContext(), name + " added successfully.", Toast.LENGTH_LONG).show();
                                boolean flag = true;
                                if (vegetable == null) {
                                    vegetable = new Vegetable();
                                    flag = false;
                                }
                                vegetable.setName(name);
                                vegetable.setPrice(Double.parseDouble(price));
                                vegetable.setQuantity(Integer.parseInt(quantity));
                                nameEditText.setText((vegetable != null && flag) ? vegetable.getName() : "");
                                quantityEditText.setText((vegetable != null && flag) ? String.valueOf(vegetable.getQuantity()) : "");
                                priceText.setText((vegetable != null  && flag)? String.valueOf(vegetable.getPrice()) : "");
                                submitBtn.setVisibility(View.VISIBLE);
                                progressBar.setVisibility(View.GONE);
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                NetworkResponse networkResponse = error.networkResponse;
                                submitBtn.setVisibility(View.VISIBLE);
                                progressBar.setVisibility(View.GONE);

                                if (networkResponse != null && networkResponse.data != null) {
                                    try {
                                        final String body = new String(networkResponse.data, "UTF-8");
                                        Log.e("Error", body);
                                        Toast.makeText(getApplicationContext(), body, Toast.LENGTH_SHORT).show();
                                    } catch (UnsupportedEncodingException e) {
                                        e.printStackTrace();
                                    }
                                } else {
                                    Toast.makeText(getApplicationContext(), getString(R.string.error), Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                ) {
                    @Override
                    public Map<String, String> getHeaders() {
                        Map<String, String> pars = new HashMap<String, String>();
                        pars.put("Content-Type", "application/x-www-form-urlencoded");
                        return pars;
                    }

                    @Override
                    public String getBodyContentType() {
                        return "application/x-www-form-urlencoded; charset=" + getParamsEncoding();
                    }

                    @Override
                    protected Map<String, String> getParams() {
                        Map<String, String> params = new HashMap<String, String>();
                        params.put("name", name);
                        params.put("quantity", quantity);
                        params.put("price", price);

                        return params;
                    }
                };

                AppController.getInstance().addToRequestQueue(request);
            }
        });

    }
}