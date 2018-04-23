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

public class SimpleHuffProcessor implements IHuffProcessor {

    private IHuffViewer myViewer;
    // TODO: HASHMAP INSTANCE VARIABLE?

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
     * ALL bits that will be written including the 
     * magic number, the header format number, the header to 
     * reproduce the tree, AND the actual data.
     * @throws IOException if an error occurs while reading from the input file.
     */
    public int preprocessCompress(InputStream in, int headerFormat) throws IOException { // TODO: WHAT IS "headerFormat" FOR IN PREPROCESS?
        showString("Not working yet");
        myViewer.update("Still not working");
        int[] freqTable = getFreqTable(in);
//        for(int chunkVal = 0; chunkVal < freqTable.length; chunkVal++) { // TEST TEST TEST 
//            System.out.println("CURRENT ASCII VAL: " + chunkVal + "   FREQUENCY: " + freqTable[chunkVal]);
//        }
        HuffmanTree encodingTree = new HuffmanTree(freqTable);
        
        throw new IOException("preprocess not implemented");
        
        
        //return 0;
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
