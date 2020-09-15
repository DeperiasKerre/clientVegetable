package com.example.vegetableserviceengine.adapters;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.vegetableserviceengine.AddVegetableActivity;
import com.example.vegetableserviceengine.AppController;
import com.example.vegetableserviceengine.R;
import com.example.vegetableserviceengine.models.Vegetable;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class VegetableAdapter extends RecyclerView.Adapter<VegetableAdapter.MyViewHolder> {

    private List<Vegetable> vegetableList;
    private ItemClickListener mClickListener;

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView name, price, stock;
        public Button update, delete;

        public MyViewHolder(View view) {
            super(view);
            name = (TextView) view.findViewById(R.id.name);
            price = (TextView) view.findViewById(R.id.price);
            stock = (TextView) view.findViewById(R.id.stock);
            update = (Button) view.findViewById(R.id.update);
            delete = (Button) view.findViewById(R.id.delete);

            delete.setOnClickListener(this);
            update.setOnClickListener(this);
        }

        @Override
        public void onClick(final View view) {
            if (mClickListener != null) mClickListener.onItemClick(view, getAdapterPosition());
        }
    }


    public VegetableAdapter(List<Vegetable> vegetableList) {
        this.vegetableList = vegetableList;
    }

    public void setVegetableList(List<Vegetable> vegetableList) {
        this.vegetableList = vegetableList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.vegetable_item, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Vegetable vegetable = vegetableList.get(position);
        holder.name.setText(vegetable.getName());
        holder.price.setText(String.valueOf(vegetable.getPrice()));
        holder.stock.setText(String.valueOf(vegetable.getQuantity()));
    }

    // allows clicks events to be caught
    public void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    // parent activity will implement this method to respond to click events
    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }
    @Override
    public int getItemCount() {
        return vegetableList.size();
    }
}