package com.example.refactoringwnamqos.logs;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.example.refactoringwnamqos.R;
import com.example.refactoringwnamqos.enteties.LogItem;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;


public class LogAdapter extends RecyclerView.Adapter<LogAdapter.MyViewHodler>{

    private List<LogItem> logItems;


    public LogAdapter(List<LogItem> logItems){
        this.logItems = logItems;
    }

    @NonNull
    @Override
    public LogAdapter.MyViewHodler onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        Context context = viewGroup.getContext();

        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.item_log, viewGroup, false);
        return new MyViewHodler(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull LogAdapter.MyViewHodler myViewHodler, int i) {
        LogItem logItem = logItems.get(i);
        myViewHodler.textCaption.setText(logItem.getmCaption());
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat formatForDateNow = new SimpleDateFormat("E yyyy.MM.dd 'время' HH:mm:ss");
        Date parsingDate = new Date(Long.parseLong(logItem.getmDate())*1000);
        String date = formatForDateNow.format(parsingDate);
        myViewHodler.textData.setText(logItem.getmInfo() + " в " + date);
    }

    @Override
    public int getItemCount() {
        return logItems.size();
    }

    public static class MyViewHodler extends RecyclerView.ViewHolder {

        TextView textCaption;
        TextView textData;

        public MyViewHodler(View itemView) {
            super(itemView);
            textCaption = itemView.findViewById((R.id.textViewCaption));
            textData = itemView.findViewById((R.id.textViewData));
        }

    }
}
