import java.util.HashMap;

// Class to store the Huffman Tree created based on the
// frequencys of each value in the alphabet
public class HuffmanTree {
    private TreeNode root;
    private int treeFormatBitSize;
    
    // Create Huffman Tree based off frequency table
    public HuffmanTree(int[] freqTable) {
        PriorityQueue<TreeNode> priorityQueue = getInitialPriorityQueue(freqTable); // TODO:DO WE NEED TO DEAL WITH IF THE FREQUENCY IS EMPTY?? (I.E.THE FILE IS EMPTY)
        root = getHuffmanTree(priorityQueue);
        treeFormatBitSize = 0;
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
    
    // Get number of bits that this Huffman Tree has in a Standard Tree Format header
    // Pre: None
    // Post: Returns number of bits if this Huffman Tree were to be encoded into a Standard
    //       Tree Format
    public int calculateTreeFormatBitSize() {
        int[] totalBits = new int[1];
        numBitsInStandardTreeFormatHelper(totalBits, root);
        treeFormatBitSize = totalBits[0]; // Store size 
        System.out.println("NUM BITS IN STANDARD TREE FORMAT HEADER: " + totalBits[0]); // TODO: TEST TESTTEST!!
        return totalBits[0];
    }
    
    // Recursive helper method to numBitsInStandardTreeFormat()
    // Recursively go through the Huffman Tree and keep track total bits needed
    // to write the Standard Tree Format header
    // Add 1 bit every non-leaf node, and add 1 + (BITS_PER_WORD + 1)
    // Pre: None
    // Post: Updates value in array to show total bits in Standard Tree Format Header
    private void numBitsInStandardTreeFormatHelper(int[] totalBits, TreeNode currentNode) {
        if(currentNode != null) {
            if(currentNode.getLeft() == null && currentNode.getRight() == null) { // Leaf!
                totalBits[0] += 1 + (IHuffConstants.BITS_PER_WORD + 1); 
            } else { // Not a leaf!
                numBitsInStandardTreeFormatHelper(totalBits, currentNode.getLeft());
                totalBits[0]++;
                numBitsInStandardTreeFormatHelper(totalBits, currentNode.getRight());
            }
        }
    }
    
    // Return the number of bits required to store this tree in a Standard Tree
    // Format header
    // Pre: // TODO: WH
    // Post: Return number of bits needed to store tree in STF.
    public int getTreeFormatBitSize() {
        return treeFormatBitSize;
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
