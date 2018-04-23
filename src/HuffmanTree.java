import java.util.HashMap;

// Class to store the Huffman Tree created based on the
// frequencys of each value in the alphabet
public class HuffmanTree {
    private TreeNode root;
    
    // Create Huffman Tree based off frequency table
    public HuffmanTree(int[] freqTable) {
        PriorityQueue<TreeNode> priorityQueue = getInitialPriorityQueue(freqTable); // TODO:DO WE NEED TO DEAL WITH IF THE FREQUENCY IS EMPTY?? (I.E.THE FILE IS EMPTY)
        root = getHuffmanTree(priorityQueue);
        
        // TODO: TESTING PURPOSES!!!!
        // System.out.println(priorityQueue);
        // inOrderTraversal(root);
    }
    
    // Helper method to create initial priority queue with frequency table
    private PriorityQueue<TreeNode> getInitialPriorityQueue(int[] freqTable) {
        PriorityQueue<TreeNode> result = new PriorityQueue<>();
        for(int alphValue = 0; alphValue < freqTable.length; alphValue++) {
            if(freqTable[alphValue] != 0) { // This alphabet value appears in file (has a frequency!)
                // alphValue represents alphabet element interpreted as an integer
                TreeNode newNode = new TreeNode(alphValue, freqTable[alphValue]);
                result.enqueue(newNode);
            }
        }
        
        // After creating initial priority queue, add EOF value to queue with freq of 1
        result.enqueue(new TreeNode(IHuffConstants.PSEUDO_EOF, 1)); 
        
        return result;
    }
    
    // Helper method to create encoding Huffman Tree based on initial Priority Queue
    private TreeNode getHuffmanTree(PriorityQueue<TreeNode> priorityQueue) {
        TreeNode finalRoot = null;
        while(priorityQueue.size() >= 2) {
            TreeNode left = priorityQueue.dequeue();
            TreeNode right = priorityQueue.dequeue();
            TreeNode newNode = new TreeNode(left, -1, right); // -1 is arbitrary dummy value. These nodes don't hold alphabet element! // TODO: WILL DUMMY VALUE OF -1 CAUSE A BUG?
            priorityQueue.enqueue(newNode);
            
        }
        finalRoot = priorityQueue.dequeue(); // This gives you the root of the full Huffman Tree! Always will be 1 root left.
        return finalRoot;
    }
    
    // Return the root of the Huffman Tree
    // Pre: None
    // Post: Return root of Huffman Tree. If tree is empty, return null // TODO: WILL THE HUFFMAN TREE EVER BE EMPTY IN TEST CASES?
    public TreeNode getHuffmanRoot() {
        return root;
    }
    
     // TODO: TESTING PURPOSES FOR HUFFMAN TREE!
    private void inOrderTraversal(TreeNode node) {
        if (node != null) {
            inOrderTraversal(node.getLeft());
            System.out.println("Value: " + (node.getValue() != -1 ?  node.getValue() : -1) + " Frequency: " + node.getFrequency());
            inOrderTraversal(node.getRight());
        }
    }    
}
