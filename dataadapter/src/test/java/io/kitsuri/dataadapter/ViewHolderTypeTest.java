package io.kitsuri.dataadapter;

import org.junit.Test;

public class ViewHolderTypeTest {

    @Test
    public void testDifferentIds() {
        ViewHolderType type1 = ViewHolderType.from((ctx, parent) -> { throw new UnsupportedOperationException(); });
        ViewHolderType type2 = ViewHolderType.from((ctx, parent) -> { throw new UnsupportedOperationException(); });
        assertNotEquals(type1.getId(), type2.getId());
    }

    @Test
    public void testResolveById() {
        ViewHolderType type = ViewHolderType.from((ctx, parent) -> { throw new UnsupportedOperationException(); });
        assertEquals(type, ViewHolderType.getTypeForId(type.getId()));
    }

}
