package com.fadedbytes.beo.util.data.structure;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CircularQueueTest {

    @Test
    void contentTest() {
        // create 3 element queue
        CircularQueue<Integer> queue = new CircularQueue<>(3);

        // queue capacity is 3
        assertEquals(3, queue.capacity());

        // fill queue
        queue.enqueue(1);
        queue.enqueue(2);
        queue.enqueue(3);

        // queue size should be 3
        assertEquals(3, queue.size());

        // dequeue the elements
        assertEquals(1, queue.dequeue());
        assertEquals(2, queue.dequeue());
        assertEquals(3, queue.dequeue());

        // queue 2 elements
        queue.enqueue(4);
        queue.enqueue(5);

        // queue size should be 2
        assertEquals(2, queue.size());

        // queue a 3rd element
        queue.enqueue(6);

        // dequeue the elements
        assertEquals(4, queue.dequeue());
        assertEquals(5, queue.dequeue());
        assertEquals(6, queue.dequeue());

        // the queue size should be 0
        assertEquals(0, queue.size());
    }

    @Test
    void capacityTest() {
        // create 3 element queue
        CircularQueue<Integer> queue = new CircularQueue<>(3);

        // queue 3 elements
        queue.enqueue(1);
        queue.enqueue(2);
        queue.enqueue(3);

        // queue size should be 3
        assertEquals(3, queue.size());

        // dequeue 2 elements
        assertEquals(1, queue.dequeue());
        assertEquals(2, queue.dequeue());

        // the queue should not be empty
        assertFalse(queue.isEmpty());

        // dequeue the last element
        assertEquals(3, queue.dequeue());

        // the queue should be empty now
        assertTrue(queue.isEmpty());

        // queue 3 elements
        queue.enqueue(4);
        queue.enqueue(5);

        // the queue should not be full
        assertFalse(queue.isFull());

        // queue a 3rd element
        queue.enqueue(6);

        // the queue should be full now
        assertTrue(queue.isFull());
    }

    @Test
    void orderTest() {
        // create 3 element queue
        CircularQueue<Integer> queue = new CircularQueue<>(3);

        // queue 3 elements
        queue.enqueue(1);
        queue.enqueue(2);
        queue.enqueue(3);

        // the first element should be 1
        assertEquals(1, queue.first());
        // dequeue the element
        assertEquals(1, queue.dequeue());

        // the first element should be 2
        assertEquals(2, queue.first());
        // the last element should be 3
        assertEquals(3, queue.last());

        // dequeue the 2
        assertEquals(2, queue.dequeue());

        // the first and last element should be 3
        assertEquals(3, queue.first());
        assertEquals(3, queue.last());

        // dequeue the 3
        assertEquals(3, queue.dequeue());

        // the first and last element should be null
        assertNull(queue.first());
        assertNull(queue.last());
    }

    @Test
    void flowTest() {
        // create 3 element queue
        CircularQueue<Integer> queue = new CircularQueue<>(3);

        // queue 5 elements (should overflow)
        queue.enqueue(1);
        queue.enqueue(2);
        queue.enqueue(3);
        queue.enqueue(4);
        queue.enqueue(5);

        // the first element should be 3
        assertEquals(3, queue.first());
        // the last element should be 5
        assertEquals(5, queue.last());
    }

}