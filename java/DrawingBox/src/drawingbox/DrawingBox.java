/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package drawingbox;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


/**
 *
 * @author Devin
 */
public class DrawingBox<AnyType> implements Iterable<AnyType>{
    private static final int DEFAULT_CAPACITY = 100;
    private int theSize;
    private AnyType[] theItems;
    
    public DrawingBox(){
        doClear();
    }
    public void clear(){
        doClear();
    }
    private void doClear(){
        theSize = 0;
        ensureCapacity(DEFAULT_CAPACITY);
    }
    public int size(){
        return theSize;
    }
    public boolean isEmpty(){
        return size() == 0;
    }
    public void trimToSize(){
        ensureCapacity(size());
    }
    public AnyType get(int idx){
        if (idx < 0 || idx >= size()){
            throw new ArrayIndexOutOfBoundsException();
        }
        return theItems[idx];
    }
    public AnyType set(int idx, AnyType newVal){
        if (idx < 0 || idx >= size()){
            throw new ArrayIndexOutOfBoundsException();
        }
        AnyType old = theItems[idx];
        theItems[idx] = newVal;
        return old;
    }
    public void ensureCapacity(int newCapacity){
        if (newCapacity < theSize){
            return;
        }
        AnyType[] old = theItems;
        theItems = (AnyType[]) new Object[newCapacity];
        for (int i = 0; i < size(); i++){
            theItems[i] = old[i];
        }
    }
    public boolean add(AnyType x){
        add(size(), x);
        return true;
    }
    public void add(int idx, AnyType x){
        if (theItems.length == size()){
            ensureCapacity(size() * 2 + 1);
        }
        for (int i = theSize; i > idx; i--){
            theItems[i] = theItems[i - 1];
        }
        theItems[idx] = x;
        theSize++;
    }
    public AnyType remove(int idx){
        AnyType removedItem = theItems[idx];
        for (int i = idx; i < size() - 1; i++){
            theItems[i] = theItems[i + 1];
        }
        theSize--;
        return removedItem;
    }
    public AnyType drawItem(){
        if (isEmpty()){
            return null;
        } else {
            int idx = (int)(Math.random() * size());
            return get(idx);
        }
    }
    
    @Override
    public Iterator<AnyType> iterator() {
        return new ArrayListIterator();
    }
    private class ArrayListIterator implements java.util.Iterator<AnyType>{
        private int current = 0;
        @Override
        public boolean hasNext() {
            return current < size();
        }

        @Override
        public AnyType next() {
            if (!hasNext()){
                throw new java.util.NoSuchElementException();
            }
            return theItems[current++];
        }

        @Override
        public void remove() {
            DrawingBox.this.remove(--current);
        }
    }
    
    public static void main(String args[]){
        DrawingBox test = new DrawingBox();
        System.out.println("Array Length:" + test.theItems.length);
        System.out.println("List Size: " + test.size());
        System.out.println("Test isEmpty(): " + test.isEmpty());
        System.out.println("Filling array with 100 objects.");
        int i = 0;
        while (i < 100){
            if (i % 2 == 0){
                test.add(i);
            } else if (i % 2 != 0){
                test.add("String " + i);
            }
            i++;
        }
        System.out.println("New List Size: " + test.size());
        System.out.println("Test isEmpty(): " + test.isEmpty());
        System.out.println("Test drawItem: " + test.drawItem());
    }
}