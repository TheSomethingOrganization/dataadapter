package io.mrarm.dataadapter;

import androidx.databinding.ObservableList;

import java.util.List;

public class ListData<T> extends BaseDataFragment<T> {

    private List<? extends T> list;
    private int oldListItemCount = 0;
    private ListViewHolderTypeResolver<T> viewHolderTypeResolver;
    private ObservableListListener listListener;

    public ListData(List<? extends T> list, ListViewHolderTypeResolver<T> typeResolver) {
        setSource(list, typeResolver);
    }

    public ListData(List<? extends T> list, ViewHolderTypeResolver<T> typeResolver) {
        setSource(list, typeResolver);
    }

    public ListData(List<? extends T> list, ViewHolderType<T> type) {
        setSource(list, type);
    }

    public ListData(ObservableList<? extends T> list, ListViewHolderTypeResolver<T> typeResolver) {
        setSource(list, typeResolver);
    }

    public ListData(ObservableList<? extends T> list, ViewHolderTypeResolver<T> typeResolver) {
        setSource(list, typeResolver);
    }

    public ListData(ObservableList<? extends T> list, ViewHolderType<T> type) {
        setSource(list, type);
    }

    private void updateItemCounts() {
        if (oldListItemCount > 0)
            notifyItemRangeRemoved(0, oldListItemCount);
        oldListItemCount = list.size();
        notifyItemRangeInserted(0, oldListItemCount);
    }

    public void setSource(List<? extends T> list, ListViewHolderTypeResolver<T> typeResolver) {
        if (listListener != null) {
            //noinspection unchecked
            ((ObservableList<? extends T>) this.list).removeOnListChangedCallback(listListener);
            listListener = null;
        }
        this.list = list;
        this.viewHolderTypeResolver = typeResolver;
        if (isBound())
            updateItemCounts();
    }

    public void setSource(List<? extends T> list, ViewHolderTypeResolver<T> typeResolver) {
        setSource(list, (i, value) -> typeResolver.resolveType(value));
    }

    public void setSource(List<? extends T> list, ViewHolderType<T> type) {
        setSource(list, (i, value) -> type);
    }


    public void setSource(ObservableList<? extends T> list, ListViewHolderTypeResolver<T> typeResolver) {
        setSource((List<? extends T>) list, typeResolver);
        listListener = new ObservableListListener();
        if (isBound()) {
            //noinspection unchecked
            list.addOnListChangedCallback(listListener);
        }
    }

    public void setSource(ObservableList<? extends T> list, ViewHolderTypeResolver<T> typeResolver) {
        setSource(list, (i, value) -> typeResolver.resolveType(value));
    }

    public void setSource(ObservableList<? extends T> list, ViewHolderType<T> type) {
        setSource(list, (i, value) -> type);
    }


    @Override
    public T getItem(int index) {
        return list.get(index);
    }

    @Override
    public int getItemCount() {
        return oldListItemCount;
    }

    @Override
    public ViewHolderType<T> getHolderTypeFor(int index) {
        return viewHolderTypeResolver.resolveType(index, list.get(index));
    }

    @Override
    protected void onBind() {
        updateItemCounts();
        if (listListener != null) {
            //noinspection unchecked
            ((ObservableList<? extends T>) this.list).addOnListChangedCallback(listListener);
        }
    }

    @Override
    protected void onUnbind() {
        if (listListener != null) {
            //noinspection unchecked
            ((ObservableList<? extends T>) this.list).removeOnListChangedCallback(listListener);
        }
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

    private class ObservableListListener extends ObservableList.OnListChangedCallback {

        @Override
        public void onChanged(ObservableList sender) {
            int newItemCount = list.size();
            notifyItemRangeChanged(0, Math.min(oldListItemCount, newItemCount));
            if (newItemCount > oldListItemCount)
                notifyItemRangeInserted(oldListItemCount, newItemCount - oldListItemCount);
            else if (newItemCount < oldListItemCount)
                notifyItemRangeRemoved(newItemCount, oldListItemCount - newItemCount);
            oldListItemCount = newItemCount;
        }

        @Override
        public void onItemRangeChanged(ObservableList sender, int positionStart, int itemCount) {
            notifyItemRangeChanged(positionStart, itemCount);
        }

        @Override
        public void onItemRangeInserted(ObservableList sender, int positionStart, int itemCount) {
            oldListItemCount += itemCount;
            notifyItemRangeInserted(positionStart, itemCount);
        }

        @Override
        public void onItemRangeMoved(ObservableList sender, int fromPosition, int toPosition, int itemCount) {
            notifyItemRangeMoved(fromPosition, toPosition, itemCount);
        }

        @Override
        public void onItemRangeRemoved(ObservableList sender, int positionStart, int itemCount) {
            oldListItemCount -= itemCount;
            notifyItemRangeRemoved(positionStart, itemCount);
        }

    }

}
