package Programming_Assignment_2;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.lang.UnsupportedOperationException;
import edu.princeton.cs.algs4.StdRandom;


//原本最初的打算是直接用链表进行实现的，
//但是后来才发现性能要求迭代器的next的方法必须是常数时间，
//而用链表实现不了，所以改用数组实现。

public class RandomizedQueue<Item> implements Iterable<Item> {
    private Item[] array;
    private int size;

    public RandomizedQueue(){
        size = 0;
        array = (Item[]) new Object[1];
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public int size() {
        return size;
    }

    private void check() {
        if (size >= array.length){
            resize(array.length * 2);
        }

        else if (size < array.length / 4){
            resize(array.length / 2);
        }
    }

    private void resize(int n) {
        Item[] tmp = (Item[]) new Object[n];
        for (int i = 0;i < size;i++){
            tmp[i] = array[i];
        }
        array = tmp;
    }

    public void enqueue(Item item) {
        if (item == null)
            throw new IllegalArgumentException("wrong input");
        check();
        array[size++] = item;
    }

    public Item dequeue() {
        if (isEmpty())
            throw new NoSuchElementException();
        int random = StdRandom.uniform(size);
        Item cnt =array[random];
        array[random] = array[size - 1];
        array[--size] = null;
        check();
        return cnt;
    }

    public Item sample() {
        if (isEmpty())
            throw new NoSuchElementException();
        return array[StdRandom.uniform(size)];
    }

    public Iterator<Item> iterator() {
        return new RandomIterator();
    }

    private class RandomIterator implements Iterator<Item> {
        private int rank;
        private Item[] iarray;

        public RandomIterator() {
            rank = size;
            iarray = (Item[]) new Object[rank];
            for (int i = 0;i < rank;i++){
                iarray[i] = array[i];
            }
        }

        public boolean hasNext() {
            return rank > 0;
        }

        public void remove(){
            throw new UnsupportedOperationException();
        }

        public Item next (){
            if (!hasNext())
                throw new NoSuchElementException();
            int random = StdRandom.uniform(rank);
            rank--;
            Item item = iarray[random];
            iarray[random] = iarray[rank];
            //这里需要注意的是 与上面直接在原数组上面操作的区别就在于不能直接令后面等于null
            //如 iarray[rank] = null 这样造成的后果是多用几次迭代器使用不了了。
            iarray[rank] = null;
            return item;
        }

    }


}