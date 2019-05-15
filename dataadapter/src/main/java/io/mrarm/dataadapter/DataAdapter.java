package io.mrarm.dataadapter;

import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class DataAdapter extends RecyclerView.Adapter<ViewHolder> {

    private DataFragment fragment;
    private int attachCount = 0;
    private boolean autoBindFragment = false;

    public void setSource(DataFragment fragment, boolean autoBindFragment) {
        if (this.fragment != null && attachCount > 0 && this.autoBindFragment)
            this.fragment.unbind();
        this.fragment = fragment;
        this.autoBindFragment = autoBindFragment;
        if (attachCount > 0 && autoBindFragment)
            fragment.bind();
        notifyDataSetChanged();
    }

    public void setSource(DataFragment fragment) {
        setSource(fragment, true);
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        if (--attachCount == 0) {
            if (fragment != null && autoBindFragment)
                fragment.unbind();
        }
    }

    @Override
    public void onDetachedFromRecyclerView(@NonNull RecyclerView recyclerView) {
        if (attachCount++ == 0) {
            if (fragment != null && autoBindFragment)
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
