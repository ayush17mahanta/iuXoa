package com.iuxoa.datadrop;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class SurveyAdapter extends RecyclerView.Adapter<SurveyAdapter.ViewHolder> {

    public interface OnSurveyClickListener {
        void onSurveyClick(SurveyModel model);
    }

    private List<SurveyModel> list;
    private OnSurveyClickListener listener;

    public SurveyAdapter(List<SurveyModel> list, OnSurveyClickListener listener, SurveyListActivity surveyListActivity) {
        this.list = list;
        this.listener = listener;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView title, payout;

        public ViewHolder(View view) {
            super(view);
            title = view.findViewById(R.id.surveyTitle);
            payout = view.findViewById(R.id.surveyPayout);
        }
    }

    @NonNull
    @Override
    public SurveyAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_survey, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull SurveyAdapter.ViewHolder holder, int position) {
        SurveyModel model = list.get(position);
        holder.title.setText(model.getTitle());
        holder.payout.setText("$" + String.format("%.2f", model.getPayout()));
        holder.itemView.setOnClickListener(v -> listener.onSurveyClick(model));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}
