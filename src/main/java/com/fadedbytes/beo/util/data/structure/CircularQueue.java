package com.fadedbytes.beo.util.data.structure;

import org.jetbrains.annotations.Nullable;

/**
 * A circular queue implementation. It is a queue that uses a fixed-size array as its underlying.
 * When the queue is full and a new element is enqueued, the oldest element in the queue is discarded
 * to make room for the new element.
 * @param <T> The type of the elements in the queue.
 */
public class CircularQueue<T> {
    private final T[] queue;
    private int front;
    private int rear;
    private int size;

    /**
     * Creates a new circular queue with the specified capacity.
     * @param capacity The capacity of the queue.
     * @throws IllegalArgumentException If the capacity is less than 1.
     */
    @SuppressWarnings("unchecked")
    public CircularQueue(int capacity) throws IllegalArgumentException {

        if (capacity <= 0) {
            throw new IllegalArgumentException("Capacity must be greater than 0");
        }

        queue = (T[]) new Object[capacity];
        front = 0;
        rear = -1;
        size = 0;
    }

    /**
     * @return whether the queue is empty.
     */
    public boolean isEmpty() {
        return size == 0;
    }

    /**
     * @return whether the queue is full.
     */
    public boolean isFull() {
        return size == queue.length;
    }

    /**
     * @return the current number of elements in the queue.
     */
    public int size() {
        return size;
    }

    /**
     * @return the capacity of the queue.
     */
    public int capacity() {
        return queue.length;
    }

    /**
     * Enqueues the specified element into the queue.
     * @param item The element to enqueue.
     */
    public void enqueue(T item) {
        if (isFull()) {
            dequeue();
        }
        rear = (rear + 1) % queue.length;
        queue[rear] = item;
        size++;
    }

    /**
     * Dequeues an element from the queue.
     * @return the dequeued element.
     */
    public @Nullable T dequeue() {
        if (isEmpty()) {
            return null;
        }
        T item = queue[front];
        front = (front + 1) % queue.length;
        size--;

        return item;
    }

    /**
     * @return the element at the front of the queue.
     */
    public @Nullable T first() {
        if (isEmpty()) {
            return null;
        }
        return queue[front];
    }

    /**
     * @return the element at the rear of the queue.
     */
    public @Nullable T last() {
        if (isEmpty()) {
            return null;
        }
        return queue[rear];
    }
}
