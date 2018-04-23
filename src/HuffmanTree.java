// Class to store the Huffman Tree created based on the
// frequencys of each value in the alphabet
public class HuffmanTree {

    // Create Huffman Tree based off frequency table
    public HuffmanTree(int[] freqTable) {
        PriorityQueue<TreeNode> priorityQueue = getInitialPriorityQueue(freqTable);
        
        // TODO: TESTING PURPOSES!!!!
        System.out.println(priorityQueue);
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
}
