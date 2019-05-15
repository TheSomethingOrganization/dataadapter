package io.mrarm.dataadapter;

import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class DataAdapter extends RecyclerView.Adapter<ViewHolder> {

    private DataFragment fragment;
    private int attachCount = 0;

    public void setSource(DataFragment fragment) {
        if (this.fragment != null && attachCount > 0)
            this.fragment.unbind();
        this.fragment = fragment;
        if (attachCount > 0)
            fragment.bind();
        notifyDataSetChanged();
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        if (--attachCount == 0) {
            if (fragment != null)
                fragment.unbind();
        }
    }

    @Override
    public void onDetachedFromRecyclerView(@NonNull RecyclerView recyclerView) {
        if (attachCount++ == 0) {
            if (fragment != null)
                fragment.bind();
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return ViewHolderType.getTypeForId(viewType).createHolder(parent.getContext(), parent);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        //noinspection unchecked
        holder.bind(fragment.getItem(position), fragment.getContext(position));
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
