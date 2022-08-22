/**
 * NewPNG
 * Submitted by:
 * Tom Basha 	ID# 311425714
 * Roei Lahav	ID# 315808469
 */

package Tools.Huffman;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.LinkedList;
import java.util.Queue;

public class Huffman {
    Node freqTreeRoot = null;
    final int BYTE = 256, ZERO = 0;
    Node[] possibleValues = new Node[BYTE];
    private int uniqueValues = 0, totalBytes = 0;

    public Huffman() {
    }

    // input: input and output files directories
    public void Compress(int[] input, FileOutputStream output) {
        MinHeap values = calculateFrequencies(input);
        freqTreeRoot = createFrequencyTree(values);
        encode(input, output);
    }

    // input: file directory
    // output: min heap (property: node frequency)
    // count appearances for every single value from the file
    // creates minimum heap
    private MinHeap calculateFrequencies(int[] input) {
        MinHeap values;

        for (int i = 0; i < input.length; i++) {
            int value = input[i];

            if (possibleValues[value] == null) {
                possibleValues[value] = new Node(value);
                uniqueValues++;
            }
            possibleValues[value].incCnt();
            totalBytes++;
        }

        values = new MinHeap(uniqueValues);
        values.heapifyRealValues(possibleValues, totalBytes);
        return values;
    }

    // input: minimum heap
    // output: frequency tree root
    // creates frequency tree by extracting 2 minimums, combine them to a new node and repeat
    // goes on until 1 node left (the root)
    private Node createFrequencyTree(MinHeap values) {
        Node min1, min2;
        while (MinHeap.usedSpace != 1) {
            min1 = values.extractMin();
            min2 = values.extractMin();
            values.insert(new Node(min1, min2));
        }
        values.getRoot().setHuffmanCode("");
        return values.getRoot();
    }

    // input: array with Huffman codes, file directory to encode, file directory for new file
    // writes the Huffman code counters for the decoding process
    // read the file given and replace every byte with its Huffman code from the array
    private void encode(int[] input, FileOutputStream output) {
        StringBuilder encoded = new StringBuilder();
        String convertToWrite;
        int converted;

        try {

            freqTreeRoot.setPadding();
            writeMap(output);

            for (int i = 0; i < input.length; i++) {
                int x = input[i];

                encoded.append(possibleValues[x].getCode());
                while (encoded.length() >= 8) {
                    convertToWrite = encoded.substring(0, 8);
                    encoded = new StringBuilder(encoded.substring(8));
                    converted = Integer.parseInt(convertToWrite, 2);
                    output.write(converted);
                }
            }

            for (int j = 0; j < freqTreeRoot.getPadding(); j++) {            // padding last byte to 8 bits
                encoded.append(0);
            }
            output.write(Integer.parseInt(encoded.toString(), 2));
            output.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // input: output stream
    // writes the counters to the compressed file
    private void writeMap(FileOutputStream output) {
        String mapCnt;
        try {
            for (int i = 0; i < BYTE; i++) {                 // write map to encoded file
                if (possibleValues[i] == null) {
                    mapCnt = String.format("%24s", Integer.toBinaryString(ZERO)).replace(' ', '0');
                } else
                    mapCnt = String.format("%24s", Integer.toBinaryString(possibleValues[i].getCnt())).replace(' ', '0');
                output.write(Integer.parseInt(mapCnt.substring(0, 8), 2));            // first byte
                output.write(Integer.parseInt(mapCnt.substring(8, 16), 2));            // second byte
                output.write(Integer.parseInt(mapCnt.substring(16, 24), 2));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /************** DECODE ***************/
    public int[] Decode(FileInputStream input) {
        Queue<Integer> decodedHuffman = new LinkedList<Integer>();
        int queCnt = 0;
        prepareReading(input);

        int split;
        String decodedBinary = "";
        try {
            int next = input.read();
            while (true)                // decoding compressed file
            {
                int x = next;
                next = input.read();
                String decToBin = String.format("%8s", Integer.toBinaryString(x)).replace(' ', '0');
                if (x != -1) {
                    if (next == -1) {
                        decToBin = decToBin.substring(0, 8 - freqTreeRoot.getPadding());        // delete the padding from last byte
                    }
                    decodedBinary = decodedBinary + decToBin;
                    while (decodedBinary.length() >= freqTreeRoot.getLongestHoffmanLength()) {
                        split = extractValue(decodedBinary, decodedHuffman);
                        queCnt++;
                        decodedBinary = decodedBinary.substring(split);
                    }
                } else {
                    while (decodedBinary.length() != 0) {
                        split = extractValue(decodedBinary, decodedHuffman);
                        queCnt++;
                        decodedBinary = decodedBinary.substring(split);
                    }
                    break;
                }
            }

            input.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        int[] decoded = new int[queCnt];
        for (int i = 0; i < queCnt; i++) {
           decoded[i] = decodedHuffman.poll();
        }

        return decoded;
    }

    private void prepareReading(FileInputStream input) {
        int cnt;
        StringBuilder readCnt;
        try {
            for (int i = 0; i < BYTE; i++) {                // read counters
                readCnt = new StringBuilder(String.format("%8s", Integer.toBinaryString(input.read())).replace(' ', '0'));
                readCnt.append(String.format("%8s", Integer.toBinaryString(input.read())).replace(' ', '0'));
                readCnt.append(String.format("%8s", Integer.toBinaryString(input.read())).replace(' ', '0'));
                cnt = Integer.parseInt(String.valueOf(readCnt), 2);
                if (cnt == 0)
                    possibleValues[i] = null;
                else {
                    possibleValues[i] = new Node(i, cnt);
                    uniqueValues++;
                    totalBytes += cnt;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        MinHeap values = new MinHeap(uniqueValues);
        values.heapifyRealValues(possibleValues, totalBytes);    // creates minimum heap (property: frequency)
        freqTreeRoot = createFrequencyTree(values);            // creates frequency tree
        freqTreeRoot.setPadding();                            // calculates padding
    }

    // input: encoded string, output file directory
    // output: index of the end of byte written
    // read byte by byte until reaches to a value in the frequency tree and write it to the output file
    private int extractValue(String decodedBinary, Queue<Integer> decodedHuffman) {
        int index = 0;
        Node n = freqTreeRoot;
        while (decodedBinary.length() != 0) {
            try {
                if (n.getValue() != -1) {
                    decodedHuffman.add(n.getValue());
                    return index;
                }
                if (decodedBinary.charAt(index) == '0') {
                    n = n.getLeft();
                } else {
                    n = n.getRight();
                }
                index++;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return index;
    }

}