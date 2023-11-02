package com.mirusystems.usbsave;

import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.mirusystems.usbsave.data.UsbListEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class UsbAdapter extends RecyclerView.Adapter<UsbAdapter.ViewHolder> {
    private static final String TAG = "PsAdapter";

    public static final int STATE_READY = 0;
    public static final int STATE_SUCCESS = 1;
    public static final int STATE_FAILURE = 2;
    public static final int STATE_SELECTED = 3;
    public static final int STATE_DESELECTED = 4;

    private OnItemSelectListener<UsbListEntity> listener;
    private int mode;
    private List<UsbListEntity> list = new ArrayList<>();

    public UsbAdapter(UsbSaveViewModel listener) {
        this.listener = listener;
        this.mode = Manager.getMode();
        Log.v(TAG, "PsAdapter: mode = " + Manager.getModeDescription(mode));
    }

    public void setList(List<UsbListEntity> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_usb_info, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        UsbListEntity ps = list.get(position);

        int index = position + 1;
        holder.indexText.setText(String.format(Locale.ENGLISH, "%d", index));
        holder.pcText.setText(String.format(Locale.ENGLISH, "%d(%s)", ps.getPcId(), ps.getPcName()));
        holder.numText.setText(String.format(Locale.ENGLISH, "%d", ps.getNumPs()));
        holder.doneText.setText(String.format(Locale.ENGLISH, "%d", ps.getDone()));

        int mode = Manager.getMode();
        if (mode == Manager.MODE_4_SIMULATION_USB && ps.done > 0) {
            holder.rootView.setBackgroundColor(Color.BLUE);
            setTextColor(holder, Color.WHITE);
        } else {
            holder.rootView.setBackgroundColor(Color.WHITE);
            setTextColor(holder, Color.BLACK);
        }
        holder.rootView.setOnClickListener(v -> {
            listener.onItemSelected(ps);
        });
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position, @NonNull List<Object> payloads) {
        if (payloads.isEmpty()) {
            super.onBindViewHolder(holder, position, payloads);
        } else {
            UsbListEntity ps = list.get(position);
            for (Object payload : payloads) {
                if (payload instanceof Boolean) {
                    boolean success = (Boolean) payload;
                    if (success) {
                        holder.rootView.setBackgroundColor(Color.BLUE);
                    } else {
                        holder.rootView.setBackgroundColor(Color.RED);
                    }
                } else if (payload instanceof Integer) {
                    int state = (Integer) payload;
                    Log.v(TAG, "onBindViewHolder: state = " + state);
                    if (state == STATE_READY || state == STATE_SELECTED) {
                        holder.rootView.setBackgroundColor(Color.GREEN);
                        setTextColor(holder, Color.BLACK);
                    } else if (state == STATE_SUCCESS) {
                        if (mode == Manager.MODE_4_SIMULATION_USB) {
                            ps.done = 1;
                        }
                        holder.rootView.setBackgroundColor(Color.BLUE);
                        setTextColor(holder, Color.WHITE);
                    } else if (state == STATE_FAILURE) {
                        holder.rootView.setBackgroundColor(Color.RED);
                        setTextColor(holder, Color.WHITE);
                    } else {
                        if (mode == Manager.MODE_SIMULATION_PSO_CARD && ps.done > 0) {
                            holder.rootView.setBackgroundColor(Color.BLUE);
                            setTextColor(holder, Color.WHITE);
                        } else {
                            holder.rootView.setBackgroundColor(Color.WHITE);
                            setTextColor(holder, Color.BLACK);
                        }
                    }
                }
            }
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    private void setTextColor(ViewHolder holder, int color) {
        holder.indexText.setTextColor(color);
        holder.pcText.setTextColor(color);
        holder.numText.setTextColor(color);
        holder.doneText.setTextColor(color);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        final View rootView;
        final TextView indexText;
        final TextView pcText;
        final TextView numText;
        final TextView doneText;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            rootView = itemView;
            indexText = itemView.findViewById(R.id.indexText);
            pcText = itemView.findViewById(R.id.pcText);
            numText = itemView.findViewById(R.id.numText);
            doneText = itemView.findViewById(R.id.doneText);
        }
    }
}
