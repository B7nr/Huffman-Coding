import java.util.Iterator;
import java.util.LinkedList;
import java.util.NoSuchElementException;

// Priority queue class that prioritizes based on Comparable objects
// Higher priority is placed towards the front while low priority is
// placed towards the end
// In the case of a tie, put object being added behind objects already
// present with the same priority 
public class PriorityQueue <E extends Comparable<? super E>>{
    private LinkedList<E> container; 
    
    // Initialize internal storage container ofPriorityQueue
    public PriorityQueue() {
        container = new LinkedList<>();
    }
    
    // Dequeue from the front of the list and return its value
    // Pre: size() > 0
    // Post: Dequeues from the front of PrioritiyQueue and return value
    public E dequeue() {
        if(size() <= 0) {
            throw new NoSuchElementException("No such element exception in dequeue(). "
                    + "Priority queue is empty!");
        }
        return container.removeFirst();
    }
    
    // Enqueue object to correct spot based on priority of object
    // Higher priority is placed towards the front while low priority is
    // placed towards the end
    // In the case of a tie, put object being added behind objects already
    // present with the same priority 
    // Pre: value != null
    // Post: Enqueue value into correct spot based on priority
    public void enqueue(E elementToAdd) { // TODO: WHAT SHOULD THIS RETURN? BOOLEAN OR VOID?
        Iterator<E> iterator = container.iterator();
        boolean isAdded = false;
        
        int index = 0;
        while(iterator.hasNext() && !isAdded) {
            E currentElement = iterator.next();
            // If  currentElement > elementToAdd, this is correct spot to add elementToAdd 
            if(currentElement.compareTo(elementToAdd) > 0) { 
                container.add(index, elementToAdd);
                isAdded = true;
            }
            index++;
        }
        
        if(!isAdded) {
            container.addLast(elementToAdd);
        }
    }
    
    // Return size of this priority queue
    // Pre: None
    // Post: Returns size of priority queue
    public int size() {
        return container.size();
    }
    
    // toString() method to print out contents of PriorityQueue
    // For testing purposes
    public String toString() {
        String result = "";
        for(E currentElement: container) {
            result += currentElement.toString() + "\n";
        }
        
        return result;
    }
}
