package Programming_Assignment_2;

import java.util.Iterator;
import java.util.NoSuchElementException;

public class Deque<Item> implements Iterable<Item> {

    private Node first,last;
    private int size;

    private class Node {
        Item item;
        Node next;
        Node previous;
        Node (Item i){
            item = i;
            next = null;
            previous = null;
        }
    }

    public Deque() {
        first = last = null;
        size = 0;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public int size() {
        return size;
    }

    public void addFirst(Item item) {
        if (item == null){
            throw new IllegalArgumentException();
        }
        Node oldFirst = first;
        first = new Node(item);
        first.previous = null;
        if (isEmpty()){
            last = first;
            first.next = null;
        }
        else {
            first.next = oldFirst;
            oldFirst.previous = first;
        }
        size++;
    }


    public void addLast(Item item) {
        if (item == null){
            throw new IllegalArgumentException();
        }
        Node tmp = new Node(item);
        tmp.next = null;
        if (isEmpty()){
            first = tmp;
            last = tmp;
            last.previous = null;
        }
        else {
            last.next = tmp;
            tmp.previous = last;
            last = tmp;
        }
        size++;
    }

    public Item removeFirst() {
        if (isEmpty()){
            throw new NoSuchElementException();
        }
        Item cnt = first.item;
        first = first.next;
        size--;
        if (isEmpty()) {
             last = first =null;
        }
        else {
            first.previous = null;
        }
        return cnt;
    }

    public Item removeLast() {
        if (isEmpty()){
            throw new NoSuchElementException();
        }
        Item cnt = last.item;
        last = last.previous;
        size--;
        if (isEmpty()){
            first = last = null;
        }
        else {
            last.next = null;
        }
        return cnt;
    }

    public Iterator<Item> iterator() {
        return new DequeIterator(first);
    }

    private class DequeIterator implements Iterator<Item>{
        private Node current;

        public DequeIterator(Node first) {
            current = first;
        }

        public boolean hasNext(){
            return current != null;
        }

        public void remove() {
            throw new UnsupportedOperationException();
        }

        public Item next(){
            if (!hasNext()){
                throw new NoSuchElementException();
            }
            Item cnt = current.item;
            current = current.next;
            return cnt;
        }

    }



}