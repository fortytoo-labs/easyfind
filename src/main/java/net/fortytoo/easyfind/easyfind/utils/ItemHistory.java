package net.fortytoo.easyfind.easyfind.utils;

import com.google.common.collect.EvictingQueue;
import net.minecraft.item.Item;

import java.util.LinkedList;
import java.util.Queue;
import java.util.Stack;

public class ItemHistory {
    private final Queue<Item> internalStack;
    private final int maxSize;

    public ItemHistory(int maxSize) {
        this.internalStack = EvictingQueue.create(maxSize);
        this.maxSize = maxSize;
    }
    
    public void push(Item element) {
        internalStack.remove(element);
        
        if (internalStack.size() >= maxSize) {
            internalStack.remove();
        }
        internalStack.add(element);
    }
    
    public Queue<Item> getItemHistory() {
        Stack<Item> stack = new Stack<>();
        Queue<Item> reversedQueue = new LinkedList<>(internalStack);

        // Move elements from queue to stack
        while (!reversedQueue.isEmpty()) {
            stack.push(reversedQueue.poll());
        }

        // Move elements from stack to new queue (reversed order)
        while (!stack.isEmpty()) {
            reversedQueue.offer(stack.pop());
        }

        return reversedQueue;
    }
}
