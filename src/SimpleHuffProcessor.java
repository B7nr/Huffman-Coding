/*  Student information for assignment:
 *
 *  On our honor, Ryan Arifin and Ben Rehfeld, this programming assignment is our own work
 *  and we have not provided this code to any other student.
 *
 *  Number of slip days used: //TODO:
 *
 *  Student 1 (Student whose turnin account is being used)
 *  UTEID: raa2954
 *  email address: ryanarifin134@gmail.com
 *  Grader name: Dayanny
 *
 *  Student 2
 *  UTEID: bjr2653
 *  email address: ben@rehf27.com
 *
 */

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;

public class SimpleHuffProcessor implements IHuffProcessor {

    private IHuffViewer myViewer;
    private HashMap<Integer, String> huffmanMap;// TODO: WHERE SHOULD WE STORE THE HUFFMAN MAP TO USE FOR PREPROCESS AND COMPRESS???
    
    /**
     * Compresses input to output, where the same InputStream has
     * previously been pre-processed via <code>preprocessCompress</code>
     * storing state used by this call.
     * <br> pre: <code>preprocessCompress</code> must be called before this method
     * @param in is the stream being compressed (NOT a BitInputStream)
     * @param out is bound to a file/stream to which bits are written
     * for the compressed file (not a BitOutputStream)
     * @param force if this is true create the output file even if it is larger than the input file.
     * If this is false do not create the output file if it is larger than the input file.
     * @return the number of bits written. 
     * @throws IOException if an error occurs while reading from the input file or
     * writing to the output file.
     */
    
    public int compress(InputStream in, OutputStream out, boolean force) throws IOException {
        throw new IOException("compress is not implemented");
        //return 0;
    }

    /**
     * Preprocess data so that compression is possible ---
     * count characters/create tree/store state so that
     * a subsequent call to compress will work. The InputStream
     * is <em>not</em> a BitInputStream, so wrap it int one as needed.
     * @param in is the stream which could be subsequently compressed
     * @param headerFormat a constant from IHuffProcessor that determines what kind of
     * header to use, standard count format, standard tree format, or
     * possibly some format added in the future.
     * @return number of bits saved by compression or some other measure
     * Note, to determine the number of 
     * bits saved, the number of bits written includes 
     * ALL bits that will be written including the  // TODO: IMPORTANT !!!!!
     * magic number, the header format number, the header to  // TODO: HOW TO DEAL WITH HEADERTO REPRODUCE THE TREE???
     * reproduce the tree, AND the actual data.
     * @throws IOException if an error occurs while reading from the input file.
     */
    public int preprocessCompress(InputStream in, int headerFormat) throws IOException { 
        showString("Not working yet");
        myViewer.update("Still not working");
        int[] freqTable = getFreqTable(in);

        HuffmanTree encodingTree = new HuffmanTree(freqTable);
        
        // Mutable integer value to keep track of total bits in new compressed file (actual data)
        int[] bitsInCompressedData = new int[1]; 
        huffmanMap = getHuffmanMap(encodingTree.getHuffmanRoot(), bitsInCompressedData);
        System.out.println("HUFFMAN ENCODINGS FOR EASY FILE: " + huffmanMap);
        // Constant to represent the number of bits contained in the MAGIC_NUMBER
        // and the bits in the Store constants (both ints)
        final int CONSTANT_BITS = BITS_PER_INT * 2;
        // Variable to store number of bits in the header to reproduce the tree
        int bitsInHeaderData = 0; 
        if(headerFormat == STORE_COUNTS) { // If header to reproduce tree is stored in Standard Count Format
            bitsInHeaderData = ALPH_SIZE * BITS_PER_INT; // Array of ALPH_SIZE length and each index stores BITS_PER_INT bits
        } else { // If header to reproduce the tree is stored in Standard Tree Format
            // Add BITS_PER_INT to end because these 32 bits is an int that reps. how many bits are used to store Standard Tree Format
            bitsInHeaderData = encodingTree.numBitsInStandardTreeFormat() + BITS_PER_INT;
        }
        int compressedFileBits = CONSTANT_BITS + bitsInHeaderData + bitsInCompressedData[0];
        int originalFileBits = getNumBitsOriginal(freqTable);
        //throw new IOException("preprocess not implemented");
        return originalFileBits - compressedFileBits;
    }
    
    // Create frequency table from InputStream to use for Priority Queue
    // Pre: None
    // Post: Returns an array of ints that represents frequency of values in alphabet.
    //       Array is ALPH_SIZE large. The index of the array represents the value in the
    //      alphabet, and the value in number in the array represents that value's frequency
    private int[] getFreqTable(InputStream in) throws IOException{
        int[] frequencyTable = new int[IHuffConstants.ALPH_SIZE];
        BitInputStream inputReader = new BitInputStream(in);
        
        int currentChunk = 0; // Gets an integer representation of the current value read
        while((currentChunk = inputReader.readBits(BITS_PER_WORD)) != -1) {
            frequencyTable[currentChunk]++; // Updates frequency for current alphabet value
        }
        return frequencyTable;
    }
    
    // Use HuffMan Tree to create a HashMap of all alphabet values and associated Huffman Encodings
    // Pre: None
    // Post: Returns a HashMap with alphabet values as keys and associated Huffman encoding as values (stored in a string)
    private HashMap<Integer, String> getHuffmanMap(TreeNode huffmanRoot, 
            int[] bitsInCompressedData) { 
        HashMap<Integer, String> encodingMap = new HashMap<>();
        getHuffMapHelper(encodingMap, bitsInCompressedData, huffmanRoot, "");
        return encodingMap;
    }
    
    // Helper method to getHuffmanMap(). Recursively find each leaf in this Huffman Tree.
    // Traversing down the leaf, going left adds a 0 to the Huffman encoding, going right
    // adds a 1 to the Huffman encoding. When you hit a leaf, add that leaf's value as the 
    // key value and the current Huffman encoding as the value in the HashMap
    // Also, each time you hit a leaf calculate howmany bits are being used to represent
    // the alphabet value with new encoding and add to new compressed file total!
    // Pre: None
    // Post: Update the HashMap with leaf values and Huffman encodings and correctly total
    //       number of bits using the new encodings
    private void getHuffMapHelper(HashMap<Integer, String> encodingMap, int[] bitsInCompressed, 
            TreeNode currentNode, String encoding) {
        if(currentNode != null) { // TODO: IS THE FILE EMPTY? DO WE NEED TO DEAL WITH THIS?
            if(currentNode.getLeft() == null && currentNode.getRight() == null) {
                encodingMap.put(currentNode.getValue(), encoding);
                bitsInCompressed[0] += currentNode.getFrequency() * encoding.length(); // TODO: TEST THIS TO BE CORRECT!
            } else {
                getHuffMapHelper(encodingMap, bitsInCompressed, currentNode.getLeft(), encoding + "0");
                getHuffMapHelper(encodingMap, bitsInCompressed, currentNode.getRight(), encoding + "1");
            }
        }
    }
    
    // Method to get original size of file (bits)
    // Simply total frequency * BITS_PER_WORD 
    // Pre: None
    // Post: Return original size of file (total frequency * BITS_PER_WORD)
    private int getNumBitsOriginal(int[] freqTable) {
        int totalFreq = 0;
        for(int currentFreq : freqTable) {
            totalFreq += currentFreq;
        }
        
        return totalFreq * BITS_PER_WORD;
    }

    public void setViewer(IHuffViewer viewer) {
        myViewer = viewer;
    }

    public int uncompress(InputStream in, OutputStream out) throws IOException {
        throw new IOException("uncompress not implemented");
        //return 0;
    }

    private void showString(String s){
        if(myViewer != null)
            myViewer.update(s);
    }
}
