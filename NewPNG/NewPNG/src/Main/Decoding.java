/**
 * NewPNG
 * Submitted by:
 * Tom Basha 	ID# 311425714
 * Roei Lahav	ID# 315808469
 */

package Main;

import Tools.Filters;
import Tools.Huffman.Huffman;
import Tools.LZ77.LZ77;

import javax.swing.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

public class Decoding extends SwingWorker<Void, String> {
    JButton resetBtn;
    FileInputStream input;
    FileOutputStream output;
    String outputDst;
    File NewPNG;
    String outputDir;


    // constructors
    public Decoding(File NewPNG, String outputDir, JButton reset){
        this.NewPNG = NewPNG;
        this.outputDir = outputDir;
        this.resetBtn = reset;
    }
    public Decoding(File NewPNG, String outputDir){
        this.NewPNG = NewPNG;
        this.outputDir = outputDir;
    }

    // input: a NewPNG and location to save the bmp file.
    // output: bmp file.
    // decode NewPNG file. decode huffman code, lz77 and filters.
    public void Decode() {
        double start = System.currentTimeMillis() / 1000.0;
        System.out.println("Decoding begins\n");

        try {
            input = new FileInputStream(NewPNG);
            String name = NewPNG.getName().replace(".NewPNG", "");
            outputDst = outputDir + name + ".bmp";
            output = new FileOutputStream(outputDst);
            StringBuilder readInfo = new StringBuilder();
            int[] toDecode;
            int height, pxSize, dataStart, width;

            /** Read Basic info **/
            System.out.println("Reading some information:");
            readInfo.append(String.format("%8s", Integer.toBinaryString(input.read())).replace(' ', '0'));      // read height
            readInfo.append(String.format("%8s", Integer.toBinaryString(input.read())).replace(' ', '0'));
            height = Integer.parseInt(String.valueOf(readInfo), 2);
            readInfo = new StringBuilder();
            readInfo.append(String.format("%8s", Integer.toBinaryString(input.read())).replace(' ', '0'));      // read width
            readInfo.append(String.format("%8s", Integer.toBinaryString(input.read())).replace(' ', '0'));
            width = Integer.parseInt(String.valueOf(readInfo), 2);
            pxSize = input.read();
            readInfo = new StringBuilder();
            readInfo.append(String.format("%8s", Integer.toBinaryString(input.read())).replace(' ', '0'));      // read header size
            readInfo.append(String.format("%8s", Integer.toBinaryString(input.read())).replace(' ', '0'));
            dataStart = Integer.parseInt(String.valueOf(readInfo), 2);
            readInfo = new StringBuilder();
            readInfo.append(String.format("%8s", Integer.toBinaryString(input.read())).replace(' ', '0'));
            int lzLength, lzByteLength = Integer.parseInt(String.valueOf(readInfo), 2);
            readInfo = new StringBuilder();
            for (int i = 0; i < lzByteLength; i++) {
                readInfo.append(String.format("%8s", Integer.toBinaryString(input.read())).replace(' ', '0'));
            }
            lzLength = Integer.parseInt(String.valueOf(readInfo), 2);

            System.out.println("File name: " + name +
                    "\nDimensions: " + width + "x" + height + " Pixels" +
                    "\nPixel size: " + pxSize +
                    "\nHeader size: " + dataStart);                               // prints data

            /** Huffman Decoding **/
            System.out.println("\nDecoding Huffman code");                      // print status
            Huffman huff = new Huffman();
            toDecode = huff.Decode(input);                       // Huffman decoding


            /** LZ77 **/
            System.out.println("\nDecoding LZ77");                      // print status
            LZ77 lz = new LZ77();
            toDecode = lz.Decode(toDecode, lzLength);              // LZ77 decoding


            /** Filtering back **/
            if (pxSize == 24) {                                     // 24bit write
                System.out.println("\nReapplying filters");                      // print status
                for (int i = 0; i < dataStart; i++) {                    // writes header
                    output.write(toDecode[i]);
                }

                Filters filters = new Filters(height, width);
                filters.originalForm(toDecode, dataStart, output);          // unfilter
            } else {                                                     // 8bit write
                for (int i = 0; i < height * width + dataStart; i++) {
                    output.write(toDecode[i]);
                }
            }

            double finish = System.currentTimeMillis() / 1000.0 - start;
            System.out.printf("\nDecoding finished in %.2f seconds", finish);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected Void doInBackground() throws Exception {
        Decode();
        resetBtn.setEnabled(true);
        return null;
    }
}
