package ua.dream.chat.util;

import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.val;

import java.util.Collection;
import java.util.LinkedList;

@AllArgsConstructor
@RequiredArgsConstructor
public class EstimatedSizeList<T> extends LinkedList<T> {

    private int estimatedSize = 100;

    public void sliceSize() {
        while (estimatedSize <= size())
            remove(0);
    }

    @Override
    public boolean add(T t) {
        sliceSize();

        return super.add(t);
    }

    @Override
    public boolean addAll(Collection<? extends T> c) {
        val result = super.addAll(c);

        sliceSize();

        return result;
    }

}
