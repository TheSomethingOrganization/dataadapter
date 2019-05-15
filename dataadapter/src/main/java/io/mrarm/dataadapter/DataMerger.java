package io.mrarm.dataadapter;

import androidx.annotation.VisibleForTesting;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class DataMerger extends BaseDataFragment {

    private List<DataFragment> fragments;
    private List<ChildListener> fragmentListeners;
    private List<Integer> startIndexes;
    private int firstInvalidStartIndex;
    private int totalItemCount = 0;

    public DataMerger(List<DataFragment> fragments) {
        setSource(fragments);
    }

    public DataMerger() {
        this(new ArrayList<>());
    }

    public void setSource(List<DataFragment> fragments) {
        if (fragments == this.fragments)
            return;

        if (totalItemCount > 0)
            notifyItemRangeRemoved(0, totalItemCount);
        this.fragments = fragments;
        this.fragmentListeners = new ArrayList<>(fragments.size());
        this.startIndexes = new ArrayList<>(fragments.size() + 1);
        startIndexes.add(0);
        for (int i = 0; i < fragments.size(); i++) {
            DataFragment fragment = fragments.get(i);
            int itemCount = fragments.get(i).getItemCount();
            totalItemCount += itemCount;
            ChildListener listener = new ChildListener(fragment, i);
            fragmentListeners.add(listener);
            fragment.addListener(listener);
            startIndexes.add(totalItemCount);
        }
        firstInvalidStartIndex = fragments.size() + 1;
        if (totalItemCount > 0)
            notifyItemRangeInserted(0, totalItemCount);
    }

    // helper method to call the list's add method
    public <T> DataMerger add(DataFragment<T> fragment) {
        fragments.add(fragment);
        notifyFragmentInserted(fragments.size() - 1);
        return this;
    }

    private int getStartIndex(int pos) {
        if (pos >= firstInvalidStartIndex) {
            for (int i = firstInvalidStartIndex; i <= pos; i++)
                startIndexes.set(i, startIndexes.get(i - 1) + fragments.get(i - 1).getItemCount());
            firstInvalidStartIndex = pos + 1;
        }
        return startIndexes.get(pos);
    }

    private void invalidateStartIndex(int firstPos) {
        if (firstInvalidStartIndex > firstPos)
            firstInvalidStartIndex = firstPos;
    }


    @VisibleForTesting
    public List<Integer> getStartIndexes() {
        getStartIndex(fragments.size()); // make sure all sizes are computed
        return startIndexes;
    }

    public int getFragmentAt(int index) {
        getStartIndex(fragments.size()); // make sure all sizes are computed so we can binsearch

        int a = 0, b = fragments.size();
        int c;
        while (b - a > 1) {
            c = (a + b) / 2;
            if (startIndexes.get(c) > index)
                b = c;
            else
                a = c;
        }
        return a;
    }

    public void notifyFragmentInserted(int index) {
        DataFragment fragment = fragments.get(index);
        ChildListener listener = new ChildListener(fragment, index);
        fragmentListeners.add(index, listener);
        for (int i = index + 1; i < fragmentListeners.size(); i++)
            fragmentListeners.get(i).index = i;
        int count = fragment.getItemCount();
        startIndexes.add(-1);
        invalidateStartIndex(index + 1);
        totalItemCount += count;
        notifyItemRangeInserted(getStartIndex(index), count);
        fragment.addListener(listener);
    }

    public void notifyFragmentRangeInserted(int index, int count) {
        if (count == 1) {
            notifyFragmentInserted(index);
            return;
        }
        ChildListener[] listeners = new ChildListener[count];
        int itemCount = 0;
        for (int i = 0; i < count; i++) {
            DataFragment fragment = fragments.get(i + index);
            listeners[i] = new ChildListener(fragment, i + index);
            itemCount += fragment.getItemCount();
            fragment.addListener(listeners[i]);
        }
        fragmentListeners.addAll(index, Arrays.asList(listeners));
        for (int i = index + count; i < fragmentListeners.size(); i++)
            fragmentListeners.get(i).index = i;
        for (int i = 0; i < count; i++)
            startIndexes.add(-1);
        invalidateStartIndex(index + 1);
        totalItemCount += itemCount;
        notifyItemRangeInserted(getStartIndex(index), itemCount);
    }

    public void notifyFragmentRemoved(int index) {
        ChildListener listener = fragmentListeners.remove(index);
        listener.fragment.removeListener(listener);
        for (int i = index; i < fragmentListeners.size(); i++)
            --fragmentListeners.get(i).index;
        int count = listener.fragment.getItemCount();
        startIndexes.remove(startIndexes.size() - 1);
        invalidateStartIndex(index + 1);
        totalItemCount -= count;
        notifyItemRangeRemoved(getStartIndex(index), count);
    }

    public void notifyFragmentRangeRemoved(int index, int count) {
        if (count == 1) {
            notifyFragmentRemoved(index);
            return;
        }
        int itemCount = 0;
        List<ChildListener> rangeListeners = fragmentListeners.subList(index, index + count);
        for (int i = 0; i < count; i++) {
            ChildListener listener = rangeListeners.get(i);
            listener.fragment.removeListener(listener);
            itemCount += listener.fragment.getItemCount();
        }
        rangeListeners.clear();
        for (int i = index; i < fragmentListeners.size(); i++)
            fragmentListeners.get(i).index = i;
        startIndexes.subList(startIndexes.size() - count, startIndexes.size()).clear();
        invalidateStartIndex(index + 1);
        totalItemCount -= itemCount;
        notifyItemRangeRemoved(getStartIndex(index), count);
    }

    @Override
    public int getItemCount() {
        return totalItemCount;
    }

    @Override
    public Object getItem(int index) {
        int fragment = getFragmentAt(index);
        return fragments.get(fragment).getItem(index - startIndexes.get(fragment));
    }

    @Override
    public Object getContext(int index) {
        int fragment = getFragmentAt(index);
        Object ret = fragments.get(fragment).getContext(index - startIndexes.get(fragment));
        if (ret != null)
            return ret;
        return super.getContext(index);
    }

    @Override
    public ViewHolderType getHolderTypeFor(int index) {
        int fragment = getFragmentAt(index);
        return fragments.get(fragment).getHolderTypeFor(index - startIndexes.get(fragment));
    }

    @Override
    protected void onBind() {
        for (DataFragment f : fragments)
            f.bind();
    }

    @Override
    protected void onUnbind() {
        for (DataFragment f : fragments)
            f.unbind();
    }

    private class ChildListener implements Listener {

        private DataFragment fragment;
        private int index;

        public ChildListener(DataFragment fragment, int index) {
            this.fragment = fragment;
            this.index = index;
        }

        @Override
        public void onItemRangeInserted(DataFragment fragment, int index, int count) {
            totalItemCount += count;
            notifyItemRangeInserted(getStartIndex(this.index) + index, count);
            invalidateStartIndex(this.index + 1);
        }

        @Override
        public void onItemRangeRemoved(DataFragment fragment, int index, int count) {
            totalItemCount -= count;
            notifyItemRangeRemoved(getStartIndex(this.index) + index, count);
            invalidateStartIndex(this.index + 1);
        }

        @Override
        public void onItemRangeChanged(DataFragment fragment, int index, int count) {
            notifyItemRangeChanged(getStartIndex(this.index) + index, count);
        }

        @Override
        public void onItemRangeMoved(DataFragment fragment, int index, int toIndex, int count) {
            notifyItemRangeMoved(getStartIndex(this.index) + index,
                    getStartIndex(this.index) + toIndex, count);
        }
    }


}
