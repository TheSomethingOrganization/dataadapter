package io.mrarm.dataadapter;

import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class DataAdapter extends RecyclerView.Adapter<ViewHolder> {

    private DataFragment fragment;

    public void setSource(DataFragment fragment) {
        this.fragment = fragment;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return ViewHolderType.getTypeForId(viewType).createHolder(parent.getContext(), parent);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        //noinspection unchecked
        holder.bind(fragment.getItem(position));
    }

    @Override
    public int getItemCount() {
        return fragment.getItemCount();
    }

    @Override
    public int getItemViewType(int position) {
        return fragment.getHolderTypeFor(position).getId();
    }

}
