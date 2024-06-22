package projects.main;

import java.util.ArrayList;
import java.util.List;

public class myQueue<T> {

    static class Node<T> {
        T data;
        Node<T> next;
        Node<T> prev;

        Node(T data, Node<T> prev, Node<T> next) {
            this.data = data;
            this.prev = prev;
            this.next = next;
        }
    }

    public Node<T> front, rear;
    private int size = 0;

    public void enqueue(T data) {
        Node<T> last = rear;
        Node<T> newNode = new Node<>(data, last, null);
        if (front == null) {
            front = rear = newNode;
        } else {
            last.next = rear = newNode;
        }
        size++;
    }

    public T dequeue() {
        if (front == null) {
            return null;
        }
        return unlinkFirst(front);
    }

    public T peek() {
        return (front != null) ? front.data : null;
    }

    private T unlinkFirst(Node<T> h) {
        final T el = h.data;
        final Node<T> next = h.next;
        h.data = null;
        h.next = null;
        front = next;
        if (next == null) {
            rear = null;
        } else {
            next.prev = null;
        }
        size--;
        return el;
    }

    public int size() {
        return size;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public List<T> toList() {
        List<T> list = new ArrayList<>();
        Node<T> current = front;
        while (current != null) {
            list.add(current.data);
            current = current.next;
        }
        return list;
    }
    
    public boolean hapusTransaksi(T dt) {
        Node<T> first = front;
        Node<T> prev = null;
        boolean state = false;
        
        if(dt == first.data) {
            if(first.next == null) {
                front = rear = null;
            } else {
                front = front.next;
                first.next = null;
            }
            
            state = true;
            size--;
        } else {
            for (; first != null; first = first.next) {
                if(dt == first.data) {
                    break;
                }
                
                prev = first;
            }
            
            // data paling belakang dihapus
            if(first.next == null) {
                rear = prev;
                rear.next = null;
            } else {
                prev.next = first.next;
                first.next = null;
            }
            
            state = true;
            size--;
        }
     
        return state;
    }
}
