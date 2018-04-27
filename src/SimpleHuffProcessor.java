/*  Student information for assignment:
 *
 *  On our honor, Ryan Arifin and Ben Rehfeld, this programming assignment is our own work
 *  and we have not provided this code to any other student.
 *
 *  Number of slip days used: 1
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
import java.util.HashMap;

public class SimpleHuffProcessor implements IHuffProcessor {

    private IHuffViewer myViewer;
    private HuffmanTree encodingTree;
    private HashMap<Integer, String> huffmanMap;
    private int headerFormat; 
    private int[] freqTable; 
    private int bitsSaved;
   
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
    // TODO: ALSO: FINISH WRITE UP OF EXPERIMENT IN READ-ME.TXT!!!
    public int compress(InputStream in, OutputStream out, boolean force) throws IOException { 
        if(!force && bitsSaved < 0) { // TODO: THROWING THE ERROR CORRECTLY, BUT FILE STILL BEING CREATED???
            myViewer.showError("Compressed file has " + bitsSaved + " more bits than uncompressed file. "
                    + "Select \'force compression\' option to compress.");
            return -1; // TODO :IS THIS RIGHT???
        } else {
        
            BitInputStream bitInput = new BitInputStream(in);
            BitOutputStream bitOutput = new BitOutputStream(out);
            
            int[] numBitsWritten = new int[1]; // Immutable integer value to keep track of # bits written to compressed file 
            
            writeHeaderInfo(bitInput, bitOutput, numBitsWritten);
            
            writeCompressedData(bitInput, bitOutput, numBitsWritten);
            bitOutput.close();
            return numBitsWritten[0];
       }
    }

    // Helper method to manage writing all header information to the compressed file
    // Pre: None
    // Post: Writes header information (MAGIC_NUMBER, STORE_COUNTS or STORE_TREE constant,
    //       and information for Huffman Tree to be recreated)
    private void writeHeaderInfo(BitInputStream bitInput, BitOutputStream bitOutput, 
            int[] numBitsWritten) {
        bitOutput.writeBits(BITS_PER_INT, MAGIC_NUMBER);
        if(headerFormat == STORE_COUNTS) { // Store constant showing file is using Standard Count Format!
            bitOutput.writeBits(BITS_PER_INT, STORE_COUNTS);
            writeStandardCountFormatHeader(bitOutput, numBitsWritten);
        } else { // Store constant showing file is using Standard Tree Format!
            int numBitsInTreeFormat = encodingTree.getTreeFormatBitSize();
            bitOutput.writeBits(BITS_PER_INT, STORE_TREE);
            bitOutput.writeBits(BITS_PER_INT, numBitsInTreeFormat); // Stores # of bits in tree rep. in STF
            // These bits account for BITS_PER_INT value indicating how many bits are in tree rep.,
            // followed by the total number of bits used to represent tree in STF
            numBitsWritten[0] += BITS_PER_INT + numBitsInTreeFormat;
            
            writeStandardTreeHeader(bitOutput, encodingTree.getHuffmanRoot());
        }  
        numBitsWritten[0] += BITS_PER_INT * 2; // Includes bits for MAGIC_NUMBER and header format constant
    }
    
    // Helper method to write Standard Count Format header information to compressed file
    // Pre: None
    // Post: Writes SCF info. to compressed file to recreate Huffman Tree. For each value in
    //      alphabet, write frequency of that value in order with BITS_PER_INT bits
    private void writeStandardCountFormatHeader(BitOutputStream bitOutput, 
            int[] numBitsWritten) {
        for(int alphVal = 0; alphVal < ALPH_SIZE; alphVal++) {
            bitOutput.writeBits(BITS_PER_INT, freqTable[alphVal]); // Write freq. for each alphabet value!
            numBitsWritten[0] += BITS_PER_INT;
        }
    }
    
    // Recursive helper method to write Standard Tree Format header information to compressed file
    // Pre: None
    // Post: Writes STF info. to compressed file to recreate Huffman Tree. Store representation
    //       of tree with internal nodes represented by a 0 bit, leaf nodes represented by a 1 bit,
    //       and when reaching the leaf node, BITS_PER_WORD + 1 bits to rep. alphabet value
    private void writeStandardTreeHeader(BitOutputStream bitOutput, TreeNode currentNode) {
        if(currentNode != null) {
            if(currentNode.getLeft() == null && currentNode.getRight() == null) { // Leaf!
                bitOutput.writeBits(1, 1); // 1 represents a leaf!
                bitOutput.writeBits(BITS_PER_WORD + 1, currentNode.getValue());
            } else { // Not a leaf!
                bitOutput.writeBits(1, 0); // Bc not at leaf, write 0 to represent internal node!
                writeStandardTreeHeader(bitOutput, currentNode.getLeft());
                writeStandardTreeHeader(bitOutput, currentNode.getRight());
            }
        }
    }
    
    // Helper method to write compressed data to file reading in input bits from
    // original file and outputting associated Huffman encoding using the HashMap!
    // Pre: None
    // Post: Write original file with new Huffman Encodings
    private void writeCompressedData(BitInputStream bitInput, BitOutputStream bitOutput, 
            int[] numBitsWritten) throws IOException{
        int currentChunkVal = 0; // Gets an integer representation of the current value read
        while((currentChunkVal = bitInput.readBits(BITS_PER_WORD)) != -1) {
            String encoding = huffmanMap.get(currentChunkVal);
            writeHuffEncoding(bitOutput, encoding, numBitsWritten);
        }
        
        String endOfFileCode = huffmanMap.get(PSEUDO_EOF);
        writeHuffEncoding(bitOutput, endOfFileCode, numBitsWritten);
    }
    
    // Helper method to write Huffman encodings to compressed file
    // If it's encoding is a 0, write a 0 bit to file
    // If it's encoding is a 1, write a 1 bit to file
    // Pre: None
    // Post: Write this Huffman Encoding to File
    private void writeHuffEncoding(BitOutputStream bitOutput, String encoding, 
            int[] numBitsWritten) {
        for(int indexOfCode = 0; indexOfCode < encoding.length(); indexOfCode++) {
            if(encoding.charAt(indexOfCode) == '0') {
                bitOutput.writeBits(1, 0); // Write a 0 bit for each 0 bit in the encoding 
            } else {
                bitOutput.writeBits(1, 1); // Write a 1 bit for each 1 bit in the encoding
            }
            numBitsWritten[0]++; // Always adding one bit each time
        }
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
     * ALL bits that will be written including the  
     * magic number, the header format number, the header to  
     * reproduce the tree, AND the actual data.
     * @throws IOException if an error occurs while reading from the input file.
     */
    public int preprocessCompress(InputStream in, int headerFormat) throws IOException { 
        showString("Not working yet");
        myViewer.update("Still not working");
        freqTable = getFreqTable(in);
        
        encodingTree = new HuffmanTree(freqTable);
        this.headerFormat = headerFormat;
        
        // Mutable integer value to keep track of total bits in new compressed file (actual data)
        int[] bitsInCompressedData = new int[1]; 
        huffmanMap = getHuffmanMap(encodingTree.getHuffmanRoot(), bitsInCompressedData);
        
        // Constant to represent the number of bits contained in the MAGIC_NUMBER
        // and the bits in the Store constants (both ints)
        final int CONSTANT_BITS = BITS_PER_INT * 2;
        // Variable to store number of bits in the header to reproduce the tree
        int bitsInHeaderData = 0; 
        if(headerFormat == STORE_COUNTS) { // If header to reproduce tree is stored in Standard Count Format
            bitsInHeaderData = ALPH_SIZE * BITS_PER_INT; // Array of ALPH_SIZE length and each index stores BITS_PER_INT bits
        } else { // If header to reproduce the tree is stored in Standard Tree Format
            // Add BITS_PER_INT to end because these 32 bits is an int that reps. how many bits are used to store Standard Tree Format
            bitsInHeaderData = encodingTree.calculateTreeFormatBitSize() + BITS_PER_INT;
        }
        int compressedFileBits = CONSTANT_BITS + bitsInHeaderData + bitsInCompressedData[0];
        int originalFileBits = getNumBitsOriginal(freqTable);
        
        bitsSaved = originalFileBits - compressedFileBits;
        return bitsSaved;
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
        inputReader.close();
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
        if(currentNode != null) { 
            if(currentNode.getLeft() == null && currentNode.getRight() == null) {
                encodingMap.put(currentNode.getValue(), encoding);
                bitsInCompressed[0] += currentNode.getFrequency() * encoding.length(); 
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

    /**
     * Uncompress a previously compressed stream in, writing the
     * uncompressed bits/data to out.
     * @param in is the previously compressed data (not a BitInputStream) 
     * @param out is the uncompressed file/stream
     * @return the number of bits written to the uncompressed file/stream
     * @throws IOException if an error occurs while reading from the input file or
     * writing to the output file.
     */
    public int uncompress(InputStream in, OutputStream out) throws IOException {
        BitInputStream bitInput = new BitInputStream(in);
        BitOutputStream bitOutput = new BitOutputStream(out);
        int magicNumber = bitInput.readBits(BITS_PER_INT);
        if(magicNumber != MAGIC_NUMBER) {
//            myViewer.showError("Error reading compressed file. \n "
//                    + "File did not start with the huff magic number.");
            bitInput.close();
            bitOutput.close();
            throw new IOException ("Error reading compressed file. \n "
                    + "File did not start with the huff magic number."); 
        }
        
        final int HEADER_FORMAT = bitInput.readBits(BITS_PER_INT); 
        checkValidUncompression(HEADER_FORMAT, "No HEADER constant!"); 
        
        HuffmanTree reconstructedTree = getHuffmanTreeFromCompressed(bitInput, HEADER_FORMAT);
        
        int[] numBitsWritten = new int[1]; // Mutable int value to keep track of bits written
        unCompressBodyData(bitInput, bitOutput, reconstructedTree, numBitsWritten);
        bitOutput.close();
        showString("NUMBER OF BITS WRITTEN WHEN UNCOMPRESSING: " + numBitsWritten[0]);
        return numBitsWritten[0];
    }
    
    // Helper method to uncompress(). Return a Huffman Tree to be used to
    // decode compressed Huff file. Gets Huffman Tree depending on whether
    // header was Standard Count Format or Standard Tree Format
    // Pre: None
    // Post: Returns the associated Huffman Tree for decoding
    private HuffmanTree getHuffmanTreeFromCompressed(BitInputStream bitInput, 
            int HEADER_FORMAT) throws IOException {
        if(HEADER_FORMAT == STORE_COUNTS) { // Header stored in Standard Count Format!
            return huffmanTreeFromStandardCount(bitInput);
        } else if (HEADER_FORMAT == STORE_TREE) { // Header stored in Standard Tree Format!
            final int NUM_BITS_IN_TREE_HEADER = bitInput.readBits(BITS_PER_INT);
            checkValidUncompression(NUM_BITS_IN_TREE_HEADER, "Standard Tree Format in header not "
                    + "formed correctly."); // Check if ran out of bits when reading Standard Tree Format!
            return new HuffmanTree(bitInput, NUM_BITS_IN_TREE_HEADER);
        } else {
            myViewer.showError("Not using a Standard Count Format or Standard Tree Format!");
            return null; // TODO: IS THIS RIGHT?
        }
    }

    // Helper method to getHuffmanTreeFromCompressed(). Constructs and returns
    // a Huffman Tree assuming header is stored in Standard Count Format.
    // Pre: None
    // Post: Returns Huffman Tree reconstructed with Standard Count Format
    private HuffmanTree huffmanTreeFromStandardCount(BitInputStream bitInput) throws IOException {
        int[] reconstructedFreqTable = new int[ALPH_SIZE];

        for(int alphValue = 0; alphValue < ALPH_SIZE; alphValue++) { // For each alphabetic value, get freq.!
            int currentFreq = bitInput.readBits(BITS_PER_INT);
            // Check if you ran out of bits when reading Standard Count Format Header!
            checkValidUncompression(currentFreq, "Standard Count Header not formed correctly."); 
            reconstructedFreqTable[alphValue] = currentFreq;
        }
        return new HuffmanTree(reconstructedFreqTable);
    }
    
    // Helper method for uncompress(). Use reconstructed Huffman Tree to decompress 
    // and write to a file the original data of the file.
    // Pre: None
    // Post: Writes out to file original data stored by the Huffed (compressed) file
    private void unCompressBodyData(BitInputStream bitInput, BitOutputStream bitOutput, 
            HuffmanTree reconstructedTree, int[] numBitsWritten) throws IOException {
        boolean done = false; // Make sure to stop at psuedo-EOF
        TreeNode currentNode = reconstructedTree.getHuffmanRoot();
        
        while(!done) { 
            int direction = bitInput.readBits(1); 
            checkValidUncompression(direction, "No PSUEDO_EOF value."); // Check if you ran out of bits when reading body data!
            
            if(direction == 0) { // Go left in Huffman tree!
                currentNode = currentNode.getLeft();
            } else { // Go right in Huffman tree
                currentNode = currentNode.getRight();
            }
            if(currentNode.getLeft() == null && currentNode.getRight() == null) { // At a leaf!
                if(currentNode.getValue() == PSEUDO_EOF) {
                    done = true; //Finish file traversal when you find the leaf with EOF!
                } else {
                    bitOutput.writeBits(BITS_PER_WORD, currentNode.getValue()); // Write original data to new file!
                    numBitsWritten[0] += BITS_PER_WORD;
                    currentNode = reconstructedTree.getHuffmanRoot(); // Go back to root!
                }
            }
        }
    }
    
    // Helper method for uncompress() and uncompress() helper methods. Takes in an integer
    // to make sure file is well formed. If integer passed in is -1, we have run out of bits to
    // read in the data, and file is NOT well formed. Display an error!
    // Pre: None
    // Post: Display an error if int passed in is -1 (file not well formed)
    private void checkValidUncompression(int check, String error) throws IOException {
        if(check == -1) { // Tried to read from input but no more bits left!
            throw new IOException("Error reading compressed file. \n "
                    + "unexpected end of input. " + error);
        }
    }
    
    private void showString(String s){
        if(myViewer != null)
            myViewer.update(s);
    }
}
