package com.elevenzon.mediarecorder;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import java.util.ArrayList;

public class RecordingsAdapter extends RecyclerView.Adapter<RecordingsAdapter.viewHolder> {

    Context context;
    ArrayList<ModelRecordings> audioArrayList;
    public OnItemClickListener onItemClickListener;

    public RecordingsAdapter(Context context, ArrayList<ModelRecordings> audioArrayList) {
        this.context = context;
        this.audioArrayList = audioArrayList;
    }

    @Override
    public RecordingsAdapter.viewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.recordings_list, viewGroup, false);
        return new viewHolder(view);
    }

    @Override
    public void onBindViewHolder(final RecordingsAdapter.viewHolder holder, final int i) {
        holder.title.setText(audioArrayList.get(i).getTitle());
        holder.date.setText(audioArrayList.get(i).getDate());
        holder.duration.setText(audioArrayList.get(i).getDuration());
    }

    @Override
    public int getItemCount() {
        return audioArrayList.size();
    }

    public class viewHolder extends RecyclerView.ViewHolder {
        TextView title, date, duration;

        public viewHolder(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.title);
            date = (TextView) itemView.findViewById(R.id.date);
            duration = (TextView) itemView.findViewById(R.id.duration);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onItemClickListener.onItemClick(getAdapterPosition(), v);
                }
            });
        }
    }
    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }
    public interface OnItemClickListener {
        void onItemClick(int pos, View v);
    }
}