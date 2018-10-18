package lee.com.audiotalkie.presenter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import lee.com.audiotalkie.R;

/**
 * CreateDate：18-10-18 on 下午5:30
 * Describe:
 * Coder: lee
 */
public class LogAdapter extends RecyclerView.Adapter<LogAdapter.LogViewHolder>{

    private List<String> mData;

    public LogAdapter(List<String> mData) {
        this.mData = mData;
    }

    public void updateData(List<String> mData) {
        this.mData = mData;
        notifyDataSetChanged();
    }

    public void addNewItem(String msg) {
        if(mData != null) {
            mData.add(msg);
            notifyItemInserted(mData.size());
        }
    }

    @Override
    public void onBindViewHolder(LogViewHolder holder, int position) {
        holder.title.setText(mData.get(position));
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    @Override
    public LogViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //LayoutInflater.from指定写法
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_log_recycleview, parent, false);
        return new LogViewHolder(v);
    }

    public static class LogViewHolder extends RecyclerView.ViewHolder{

        public final TextView title;

        public LogViewHolder(View v) {
            super(v);
            title = (TextView) v.findViewById(R.id.item_log_msg);
        }
    }

}
