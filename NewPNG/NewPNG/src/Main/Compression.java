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

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.LinkedList;
import java.util.Queue;

public class Compression extends SwingWorker<Boolean, String> {
    JButton resetBtn;
    FileInputStream input;
    File IMAGE;
    String outputDir;

    // constructors
    public Compression(File IMAGE, String outputDir, JButton reset) {
        this.IMAGE = IMAGE;
        this.outputDir = outputDir;
        this.resetBtn = reset;

    }

    public Compression(File IMAGE, String outputDir) {
        this.IMAGE = IMAGE;
        this.outputDir = outputDir;
    }

    // input: an image and location to save compressed file.
    // output: NewPNG file.
    // compress bmp images using filters, lz77 and huffman code.
    public void Compress() {
        try {
            double start = System.currentTimeMillis() / 1000.0;
            System.out.println("Compression begins\n");

            /** Reading BMP & Separating to Channels **/
            BufferedImage img = ImageIO.read(IMAGE);
            int width = img.getWidth();                             // image width
            int height = img.getHeight();                           // image height
            int px = img.getColorModel().getPixelSize();            // pixel size
            long size = IMAGE.length();                             // total size
            long dataStart;                                         // header length
            if (px == 24) {
                dataStart = size - width * height * 3;
            } else {
                dataStart = size - width * height;
            }
            String name = IMAGE.getName().replace(".bmp", "");      // image name
            input = new FileInputStream(IMAGE);
            String outputDst = outputDir + name + ".NewPNG";
            FileOutputStream output = new FileOutputStream(outputDst);
            showInfo(name, size, width, height, px);
            Queue<Integer> toEncode = new LinkedList<Integer>();
            int queCnt = 0;


            /** Write essentials to new file **/
            String prepare = String.format("%16s", Integer.toBinaryString(height)).replace(' ', '0');           // height
            output.write(Integer.parseInt(prepare.substring(0, 8), 2));
            output.write(Integer.parseInt(prepare.substring(8, 16), 2));
            prepare = String.format("%16s", Integer.toBinaryString(width)).replace(' ', '0');                   // width
            output.write(Integer.parseInt(prepare.substring(0, 8), 2));
            output.write(Integer.parseInt(prepare.substring(8, 16), 2));
            output.write(px);
            prepare = String.format("%16s", Integer.toBinaryString((int) dataStart)).replace(' ', '0');         // pixel size
            output.write(Integer.parseInt(prepare.substring(0, 8), 2));
            output.write(Integer.parseInt(prepare.substring(8, 16), 2));


            if (px == 24) {             // copy header for next stage of compression for 24bit bmp images
                for (int i = 0; i < dataStart; i++) {
                    toEncode.add(input.read());
                    queCnt++;                       // counts bits
                }
            }

            int[] compressed;

            /** Filtering **/
            if (px == 24) {     // filter 24bit bmp images
                int[][] red = new int[height][width];
                int[][] green = new int[height][width];
                int[][] blue = new int[height][width];

                for (int i = 0; i < height; i++) {      // separate RGB channels
                    for (int j = 0; j < width; j++) {
                        blue[i][j] = input.read();
                        green[i][j] = input.read();
                        red[i][j] = input.read();
                    }
                }

                System.out.println("\nFiltering");                     // print status
                Filters filters = new Filters(red, green, blue);       // filters preparation
                queCnt = filters.filter(toEncode, queCnt);             // filter channels

                compressed = new int[queCnt];                          // copying queue to array
                for (int i = 0; i < queCnt; i++) {
                    compressed[i] = toEncode.poll();
                }
            } else {        // 8bit images read
                compressed = new int[(int) (size)];
                for (int i = 0; i < size; i++) {
                    compressed[i] = input.read();
                }
            }
            input.close();


            int byteLength = (int) Math.ceil((Math.log(compressed.length) / Math.log(2)) / 8);      // calculates how many bytes needed to write filtered channels + selectors + first row and col
            output.write(byteLength);
            String lzLength = "%" + byteLength * 8 + "s";
            prepare = String.format(lzLength, Integer.toBinaryString(compressed.length)).replace(' ', '0');
            for (int i = 0; i < byteLength; i++) {
                output.write(Integer.parseInt(prepare.substring(0, 8), 2));
                prepare = prepare.substring(8);
            }


            /** LZ77 **/
            System.out.println("\nCompressing with LZ77");          // print status
            LZ77 lz = new LZ77();
            compressed = lz.Compress(compressed);                   // LZ77 compression


            /** Huffman **/
            System.out.println("\nCompressing with Huffman code");  // print status
            Huffman huff = new Huffman();
            huff.Compress(compressed, output);                      // Huffman compression


            System.out.println("\nCreated NewPNG");                // print status
            output.close();

            double finish = System.currentTimeMillis() / 1000.0 - start;
            System.out.printf("\nCompression finished in %.2f seconds", finish);


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // input: image info
    // output: shows image data
    private void showInfo(String name, long size, int width, int height, int px) {
        System.out.println("Reading image information:" +
                "\nFile name: " + name +
                "\nFile size = " + size + " bits" +
                "\nDimensions: " + width + "x" + height + " Pixels" +
                "\nPixel size = " + px + " bits");
    }

    @Override
    protected Boolean doInBackground() throws Exception {
        Compress();
        resetBtn.setEnabled(true);
        return true;
    }
}
