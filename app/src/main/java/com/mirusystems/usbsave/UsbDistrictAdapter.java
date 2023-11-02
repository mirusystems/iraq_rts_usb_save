package com.mirusystems.usbsave;

import android.graphics.Color;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.mirusystems.usbsave.data.UsbDistrict;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class UsbDistrictAdapter extends RecyclerView.Adapter<UsbDistrictAdapter.ViewHolder> {
    private static final String TAG = "UsbDistrictAdapter";

    private OnItemSelectListener<UsbDistrict> listener;
    private List<UsbDistrict> list = new ArrayList<>();

    public UsbDistrictAdapter(OnItemSelectListener<UsbDistrict> listener) {
        this.listener = listener;
    }

    public void setList(List<UsbDistrict> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_usb_district, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        UsbDistrict d = list.get(position);
        holder.codeText.setText(String.valueOf(d.code));
        holder.nameText.setText(d.name);
        holder.psCountText.setText(String.format(Locale.ENGLISH, "( %d / %d )", d.getCompletedCount(), d.getTotalCount()));
        holder.rootView.setOnClickListener(v -> {
            listener.onItemSelected(d);
        });

        if (d.getCompletedCount() != d.getTotalCount()) {
            holder.codeText.setTypeface(null, Typeface.BOLD);
            holder.nameText.setTypeface(null, Typeface.BOLD);
            holder.psCountText.setTypeface(null, Typeface.BOLD);
            holder.codeText.setTextColor(Color.BLACK);
            holder.nameText.setTextColor(Color.BLACK);
            holder.psCountText.setTextColor(Color.BLACK);
        } else {
            holder.codeText.setTypeface(null, Typeface.NORMAL);
            holder.nameText.setTypeface(null, Typeface.NORMAL);
            holder.psCountText.setTypeface(null, Typeface.NORMAL);
            holder.codeText.setTextColor(Color.GRAY);
            holder.nameText.setTextColor(Color.GRAY);
            holder.psCountText.setTextColor(Color.GRAY);
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        final View rootView;
        final TextView codeText;
        final TextView nameText;
        final TextView psCountText;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            rootView = itemView;
            codeText = itemView.findViewById(R.id.codeText);
            nameText = itemView.findViewById(R.id.nameText);
            psCountText = itemView.findViewById(R.id.psCountText);
        }
    }
}
