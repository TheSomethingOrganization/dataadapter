package io.mrarm.dataadapter;

import java.util.ArrayList;
import java.util.List;


public class DataMerger extends BaseDataFragment {

    private final List<DataFragment> fragments = new ArrayList<>();
    private final List<ChildListener> fragmentListeners = new ArrayList<>();
    private final List<Integer> startIndexes = new ArrayList<>();
    private int firstInvalidStartIndex = 1;
    private int totalItemCount = 0;

    public DataMerger() {
        startIndexes.add(0);
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

    public DataFragment get(int index) {
        return fragments.get(index);
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

    public <T> DataMerger add(DataFragment<T> fragment) {
        add(fragments.size(), fragment);
        return this;
    }

    public <T> void add(int index, DataFragment<T> fragment) {
        ChildListener listener = new ChildListener(index);
        fragments.add(index, fragment);
        fragmentListeners.add(index, listener);
        for (int i = index + 1; i < fragmentListeners.size(); i++)
            ++fragmentListeners.get(i).index;
        int count = fragment.getItemCount();
        startIndexes.add(-1);
        invalidateStartIndex(index + 1);
        totalItemCount += count;
        notifyItemRangeInserted(getStartIndex(index), count);
        fragment.addListener(listener);
    }

    public DataFragment remove(int index) {
        DataFragment fragment = fragments.remove(index);
        ChildListener listener = fragmentListeners.remove(index);
        fragment.removeListener(listener);
        for (int i = index; i < fragmentListeners.size(); i++)
            --fragmentListeners.get(i).index;
        int count = fragment.getItemCount();
        startIndexes.remove(startIndexes.size() - 1);
        invalidateStartIndex(index + 1);
        totalItemCount -= count;
        notifyItemRangeRemoved(getStartIndex(index), count);
        return fragment;
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
    public ViewHolderType getHolderTypeFor(int index) {
        int fragment = getFragmentAt(index);
        return fragments.get(fragment).getHolderTypeFor(index - startIndexes.get(fragment));
    }

    private class ChildListener implements Listener {

        private int index;

        public ChildListener(int index) {
            this.index = index;
        }

        @Override
        public void onItemRangeInserted(DataFragment fragment, int index, int count) {
            notifyItemRangeInserted(getStartIndex(this.index) + index, count);
        }

        @Override
        public void onItemRangeRemoved(DataFragment fragment, int index, int count) {
            notifyItemRangeRemoved(getStartIndex(this.index) + index, count);
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
