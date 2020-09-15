package com.example.vegetableserviceengine;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.vegetableserviceengine.adapters.VegetableAdapter;
import com.example.vegetableserviceengine.models.Vegetable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

public class ListVegetablesActivity extends AppCompatActivity implements VegetableAdapter.ItemClickListener  {

    private String TAG;

    private List<Vegetable> vegetableList = new ArrayList<>();
    private VegetableAdapter vAdapter;
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_vegetables);

        TAG = getApplicationContext().getClass().getName();

        recyclerView = (RecyclerView) findViewById(R.id.itemsList);
        vAdapter = new VegetableAdapter(vegetableList);
        vAdapter.setClickListener(this);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(vAdapter);

        fetchVegetableList();
    }

    private void fetchVegetableList() {
        final Map<String, String> params = new HashMap<String, String>();
        final ProgressDialog pDialog = new ProgressDialog(this);
        pDialog.setMessage("Fetching Vegetables");
        pDialog.setCancelable(false);
        pDialog.show();

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(getString(R.string.api_url),
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        pDialog.dismiss();
                        try {
                            for (int i=0; i<response.length(); i++) {
                                JSONObject object = response.getJSONObject(i);
                                Vegetable vegetable = new Vegetable();
                                vegetable.setId(object.getInt("id"));
                                vegetable.setName(object.getString("name"));
                                vegetable.setQuantity(object.getInt("quantity"));
                                vegetable.setPrice(object.getDouble("price"));
                                vegetableList.add(vegetable);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        vAdapter.notifyDataSetChanged();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                pDialog.dismiss();
                NetworkResponse networkResponse = error.networkResponse;

                Toast.makeText(getApplicationContext(), "Error", Toast.LENGTH_LONG).show();

                if (networkResponse != null && networkResponse.data != null) {
                    try {
                        final String body = new String(networkResponse.data, "UTF-8");

                        Toast.makeText(getApplicationContext(), body, Toast.LENGTH_SHORT).show();
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }

                } else {
                    Log.e(TAG + " Error", error.getMessage());
                }
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                return params;
            }

        };
        AppController.getInstance().addToRequestQueue(jsonArrayRequest);
    }

    @Override
    public void onItemClick(final View view, final int position) {
        Vegetable vegetable = vegetableList.get(position);
        final String submitUrl = view.getContext().getString(R.string.api_url) + "delete/" + String.valueOf(vegetable.getId());
        if (view.getId() == R.id.delete) {
            new AlertDialog.Builder(view.getContext())
                    .setTitle("Confirmation")
//                        .setIcon()
                    .setMessage("Are you sure you want to delete this item?")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            final ProgressDialog pDialog = new ProgressDialog(view.getContext());
                            pDialog.setMessage("Deleting Vegetable");
                            pDialog.setCancelable(false);
                            pDialog.show();
                            StringRequest request = new StringRequest(Request.Method.POST,submitUrl ,
                                    new Response.Listener<String>() {
                                        @Override
                                        public void onResponse(String response) {
                                            vegetableList.remove(position);
                                            vAdapter.setVegetableList(vegetableList);
                                            vAdapter.notifyDataSetChanged();
                                            Toast.makeText(view.getContext(), " Vegetable deleted successfully.", Toast.LENGTH_LONG).show();
                                            pDialog.dismiss();
                                        }
                                    },
                                    new Response.ErrorListener() {
                                        @Override
                                        public void onErrorResponse(VolleyError error) {
                                            NetworkResponse networkResponse = error.networkResponse;
                                            pDialog.dismiss();
                                            if (networkResponse != null && networkResponse.data != null) {
                                                try {
                                                    final String body = new String(networkResponse.data, "UTF-8");
                                                    Log.e("Error", body);
                                                    Toast.makeText(view.getContext(), body, Toast.LENGTH_SHORT).show();
                                                } catch (UnsupportedEncodingException e) {
                                                    e.printStackTrace();
                                                }
                                            } else {
                                                Toast.makeText(view.getContext(), view.getContext().getString(R.string.error), Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    }
                            );
                            AppController.getInstance().addToRequestQueue(request);
                        }
                    })
                    .setNegativeButton("No", null)
                    .show();
        } else if (view.getId() == R.id.update) {
            Intent intent = new Intent(view.getContext(), AddVegetableActivity.class);
            intent.putExtra("vegetable", (Parcelable) vegetable);
            view.getContext().startActivity(intent);
        }
    }
}