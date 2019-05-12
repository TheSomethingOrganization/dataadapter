package io.mrarm.dataadapter;

import java.util.List;

public class ListData<T> extends BaseDataFragment<T> {

    private List<T> list;
    private ListViewHolderTypeResolver<T> viewHolderTypeResolver;

    public ListData(List<T> list, ListViewHolderTypeResolver<T> typeResolver) {
        this.list = list;
        viewHolderTypeResolver = typeResolver;
    }

    public ListData(List<T> list, ViewHolderTypeResolver<T> typeResolver) {
        this.list = list;
        viewHolderTypeResolver = (i, value) -> typeResolver.resolveType(value);
    }

    public ListData(List<T> list, ViewHolderType<T> type) {
        this.list = list;
        viewHolderTypeResolver = (i, value) -> type;
    }


    @Override
    public T getItem(int index) {
        return list.get(index);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    @Override
    public ViewHolderType<T> getHolderTypeFor(int index) {
        return viewHolderTypeResolver.resolveType(index, list.get(index));
    }

    // export the notify methods

    public void notifyItemRangeChanged(int index, int count) {
        super.notifyItemRangeChanged(index, count);
    }

    public void notifyItemRangeInserted(int index, int count) {
        super.notifyItemRangeInserted(index, count);
    }

    public void notifyItemRangeRemoved(int index, int count) {
        super.notifyItemRangeRemoved(index, count);
    }

    public void notifyItemRangeMoved(int index, int toIndex, int count) {
        super.notifyItemRangeMoved(index, toIndex, count);
    }

}
