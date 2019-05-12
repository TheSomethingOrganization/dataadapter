package io.mrarm.dataadapter;

import org.junit.BeforeClass;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class DataMergerTest {

    private static ViewHolderType<String> type1;
    private static ViewHolderType<String> type2;

    @BeforeClass
    public static void initTypes() {
        type1 = ViewHolderType.from((ctx, parent) -> { throw new UnsupportedOperationException(); });
        type2 = ViewHolderType.from((ctx, parent) -> { throw new UnsupportedOperationException(); });
    }

    @Test
    public void testMergeItemList() {
        DataMerger merger = new DataMerger();
        merger.add(new SingleItemData<>("Header", type1));
        List<String> test = new ArrayList<>();
        for (int i = 0; i < 50; i++)
            test.add("List" + i);
        merger.add(new ListData<>(test, type2));
        merger.add(new SingleItemData<>("Footer", type1));
        assertEquals(0, merger.getFragmentAt(0));
        assertEquals(1, merger.getFragmentAt(1));
        assertEquals(1, merger.getFragmentAt(50));
        assertEquals(2, merger.getFragmentAt(51));

        assertEquals(50 + 2, merger.getItemCount());
        assertEquals("Header", merger.getItem(0));
        assertEquals("Footer", merger.getItem(51));
        for (int i = 0; i < 50; i++)
            assertEquals(test.get(i), merger.getItem(i + 1));

        assertEquals(type1, merger.getHolderTypeFor(0));
        for (int i = 0; i < 50; i++)
            assertEquals(type2, merger.getHolderTypeFor(i + 1));
        assertEquals(type1, merger.getHolderTypeFor(51));
    }
}