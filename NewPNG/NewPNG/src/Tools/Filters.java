/**
 * NewPNG
 * Submitted by:
 * Tom Basha 	ID# 311425714
 * Roei Lahav	ID# 315808469
 */

package Tools;

import java.io.FileOutputStream;
import java.util.Queue;

public class Filters {
    int[][] red, green, blue;
    int[][] filteredRed, filteredGreen, filteredBlue;
    int[] checkRed, checkGreen, checkBlue;
    int rows, cols, sum = Integer.MAX_VALUE;
    int[] selector;

    // input: RGB channels
    // compression constructor
    public Filters(int[][] red, int[][] green, int[][] blue) {
        this.red = red;
        this.green = green;
        this.blue = blue;
        rows = red.length;
        cols = red[0].length;
        filteredRed = new int[rows][cols];
        filteredGreen = new int[rows][cols];
        filteredBlue = new int[rows][cols];
        checkRed = new int[cols];
        checkGreen = new int[cols];
        checkBlue = new int[cols];
        selector = new int[rows];
    }

    // input: image dimensions
    // decoding constructor
    public Filters(int height, int width) {
        rows = height;
        cols = width;
        filteredRed = new int[rows][cols];
        filteredGreen = new int[rows][cols];
        filteredBlue = new int[rows][cols];
        red = new int[rows][cols];
        green = new int[rows][cols];
        blue = new int[rows][cols];
        selector = new int[rows];
    }

    // input: queue filled with the header, queue items counter
    // output: queue items counter
    // filter each row by itself with every filter
    // calculates summary of each row from all channels
    // selects the filter with the lowest summary for each row
    public int filter(Queue<Integer> toEncode, int queCnt) {
        for (int i = 0; i < red.length; i++) {
            sum = Integer.MAX_VALUE;
            none(i);
            sub(i);
            up(i);
            avg(i);
            paeth(i);
        }

        for (int i = 0; i < rows; i++) {        //  write selectors to queue
            toEncode.add(selector[i]);
            queCnt++;
        }


        for (int i = 0; i < rows; i++) {            // write original first row and column
            if (i == 0) {
                for (int j = 0; j < cols; j++) {
                    toEncode.add(red[i][j]);
                    toEncode.add(green[i][j]);
                    toEncode.add(blue[i][j]);

                    queCnt += 3;
                }
            } else {
                toEncode.add(red[i][0]);
                toEncode.add(green[i][0]);
                toEncode.add(blue[i][0]);
                queCnt += 3;
            }
        }


        for (int i = 0; i < rows; i++) {        // write filtered channels
            for (int j = 0; j < cols; j++) {
                toEncode.add(filteredRed[i][j]);
                toEncode.add(filteredGreen[i][j]);
                toEncode.add(filteredBlue[i][j]);
                queCnt += 3;
            }
        }


        return queCnt;
    }

    // input: row index
    // filter: none => copy original row
    private void none(int i) {
        copyArr(checkRed, red[i]);
        copyArr(checkGreen, green[i]);
        copyArr(checkBlue, blue[i]);

        rowSum(i, checkRed, checkGreen, checkBlue, 0);      // check if minimum
    }

    // input: row index
    // filter: sub => color[i][j] - color[i][j-1]
    private void sub(int i) {
        checkRed[0] = red[i][0];
        checkGreen[0] = green[i][0];
        checkBlue[0] = blue[i][0];

        for (int j = 1; j < cols; j++) {
            checkRed[j] = (red[i][j] - red[i][j - 1] + 256) % 256;
            checkGreen[j] = (green[i][j] - green[i][j - 1] + 256) % 256;
            checkBlue[j] = (blue[i][j] - blue[i][j - 1] + 256) % 256;
        }

        rowSum(i, checkRed, checkGreen, checkBlue, 1);      // check if minimum
    }

    // input: row index
    // filter: up => color[i][j] - color[i-1][j]
    private void up(int i) {
        if (i != 0) {
            for (int j = 0; j < cols; j++) {
                checkRed[j] = (red[i][j] - red[i - 1][j] + 256) % 256;
                checkGreen[j] = (green[i][j] - green[i - 1][j] + 256) % 256;
                checkBlue[j] = (blue[i][j] - blue[i - 1][j] + 256) % 256;
            }

            rowSum(i, checkRed, checkGreen, checkBlue, 2);      // check if minimum
        }
    }

    // input: row index
    // filter: average => color[i][j] - (color[i][j-1] + color[i-1][j] / 2)
    private void avg(int i) {
        checkRed[0] = red[i][0];
        checkGreen[0] = green[i][0];
        checkBlue[0] = blue[i][0];

        if (i != 0) {
            for (int j = 1; j < cols; j++) {
                checkRed[j] = (red[i][j] - Math.floorDiv((red[i][j - 1] + red[i - 1][j]), 2) + 256) % 256;
                checkGreen[j] = (green[i][j] - Math.floorDiv((green[i][j - 1] + green[i - 1][j]), 2) + 256) % 256;
                checkBlue[j] = (blue[i][j] - Math.floorDiv((blue[i][j - 1] + blue[i - 1][j]), 2) + 256) % 256;
            }
        } else {
            for (int j = 1; j < cols; j++) {
                checkRed[j] = (red[i][j] - Math.floorDiv(red[i][j - 1], 2) + 256) % 256;
                checkGreen[j] = (green[i][j] - Math.floorDiv(green[i][j - 1], 2) + 256) % 256;
                checkBlue[j] = (blue[i][j] - Math.floorDiv(blue[i][j - 1], 2) + 256) % 256;
            }
        }

        rowSum(i, checkRed, checkGreen, checkBlue, 3);      // check if minimum
    }

    // input: row index
    // filter: paeth => color[i][j] - min(VL, VU, VUL)
    private void paeth(int i) {
        paethFilter(i, red, checkRed, null, true);
        paethFilter(i, green, checkGreen, null, true);
        paethFilter(i, blue, checkBlue, null, true);

        rowSum(i, checkRed, checkGreen, checkBlue, 4);      // check if minimum
    }

    // paeth calculations
    private void paethFilter(int i, int[][] color, int[] checkColor, int[] filteredColor, boolean compressPhase) {
        int up, left, upLeft;
        int V, VL, VU, VUL;     // v=u+l-ul, vl=v-l, vu=v-u, vul=v-ul | u=up, l=left, ul=up left (diagonally)

        for (int j = 0; j < cols; j++) {
            if (i == 0) up = 0;
            else up = color[i - 1][j];

            if (j == 0) left = 0;
            else left = color[i][j - 1];

            if (i == 0 || j == 0) upLeft = 0;
            else upLeft = color[i - 1][j - 1];

            V = Math.abs(up + left - upLeft);
            VL = Math.abs(V - left);
            VU = Math.abs(V - up);
            VUL = Math.abs(V - upLeft);

            int min = Math.min(VL, Math.min(VU, VUL));              // choose min(VL, VU, VUL)
            if (compressPhase)
                checkColor[j] = (color[i][j] - min + 256) % 256;        // compression version
            else
                checkColor[j] = (filteredColor[j] + min) % 256;         // decoding version
        }
    }

    // input: current row index, filtered arrays to check, filter number
    // compares current row summery with new filter summary and replace data if needed
    private void rowSum(int i, int[] checkRed, int[] checkGreen, int[] checkBlue, int filter) {
        int currSum = 0;
        for (int j = 0; j < cols; j++) {
            currSum = currSum + checkRed[j] + checkGreen[j] + checkBlue[j];         // calculate row summary
        }
        if (currSum < sum) {                                // if new summery is lower, replaces filtered rows with current filter
            copyArr(filteredRed[i], checkRed);
            copyArr(filteredGreen[i], checkGreen);
            copyArr(filteredBlue[i], checkBlue);
            selector[i] = filter;                       // update row filter selection
            sum = currSum;                              // update curr summary
        }
    }

    // input: destination array, source array
    // copy array values from source to destiny
    private void copyArr(int[] dest, int[] src) {
        System.arraycopy(src, 0, dest, 0, cols);
    }      // copy array


    /***************** DECODING *****************/

    // input: filtered data, current index in filtered data, output stream
    // output: new bmp file
    public void originalForm(int[] toDecode, int curr, FileOutputStream output) {       // read selectors
        int i;
        for (i = 0; i < rows; i++, curr++) {
            selector[i] = toDecode[curr];
        }

        for (i = 0; i < cols; i++, curr++) {                      // read first original row
            red[0][i] = toDecode[curr];
            curr++;
            green[0][i] = toDecode[curr];
            curr++;
            blue[0][i] = toDecode[curr];
        }

        for (i = 1; i < rows; i++, curr++) {                     // read first original column

            red[i][0] = toDecode[curr];
            curr++;
            green[i][0] = toDecode[curr];
            curr++;
            blue[i][0] = toDecode[curr];
        }

        for (i = 0; i < rows; i++) {                             // fill matrix with filtered content

            for (int j = 0; j < cols; j++) {
                filteredRed[i][j] = toDecode[curr];
                curr++;
                filteredGreen[i][j] = toDecode[curr];
                curr++;
                filteredBlue[i][j] = toDecode[curr];
                curr++;
            }
        }

        unfilter();                 // returns matrix to original state

        try {
            for (i = 0; i < rows; i++) {        // writes to new bmp image
                for (int j = 0; j < cols; j++) {
                    output.write(blue[i][j]);
                    output.write(green[i][j]);
                    output.write(red[i][j]);
                }
            }

            output.close();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    // return filtered matrix to original values
    private void unfilter() {
        initMaterials();                                 // init arrays

        for (int i = 0; i < selector.length; i++) {     // calls needed unfilter function by selector
            switch (selector[i]) {
                case 0:
                    decodeNone(i);
                    break;
                case 1:
                    decodeSub(i);
                    break;
                case 2:
                    decodeUp(i);
                    break;
                case 3:
                    decodeAvg(i);
                    break;
                case 4:
                    decodePaeth(i);
            }
        }
    }

    // init arrays
    private void initMaterials() {
        checkRed = new int[cols];
        checkGreen = new int[cols];
        checkBlue = new int[cols];
    }

    // input: row index
    // filter: none decode
    private void decodeNone(int i) {
        copyArr(red[i], filteredRed[i]);
        copyArr(green[i], filteredGreen[i]);
        copyArr(blue[i], filteredBlue[i]);
    }

    // input: row index
    // filter: sub decode
    private void decodeSub(int i) {
        for (int j = 1; j < cols; j++) {
            red[i][j] = (filteredRed[i][j] + red[i][j - 1]) % 256;
            green[i][j] = (filteredGreen[i][j] + green[i][j - 1]) % 256;
            blue[i][j] = (filteredBlue[i][j] + blue[i][j - 1]) % 256;
        }
    }

    // input: row index
    // filter: up decode
    private void decodeUp(int i) {
        if (i != 0) {
            for (int j = 0; j < cols; j++) {
                red[i][j] = (filteredRed[i][j] + red[i - 1][j]) % 256;
                green[i][j] = (filteredGreen[i][j] + green[i - 1][j]) % 256;
                blue[i][j] = (filteredBlue[i][j] + blue[i - 1][j]) % 256;
            }
        }
    }

    // input: row index
    // filter: average decode
    private void decodeAvg(int i) {
        if (i != 0) {
            for (int j = 1; j < cols; j++) {
                red[i][j] = (filteredRed[i][j] + Math.floorDiv((red[i][j - 1] + red[i - 1][j]), 2)) % 256;
                green[i][j] = (filteredGreen[i][j] + Math.floorDiv((green[i][j - 1] + green[i - 1][j]), 2)) % 256;
                blue[i][j] = (filteredBlue[i][j] + Math.floorDiv((blue[i][j - 1] + blue[i - 1][j]), 2)) % 256;
            }
        } else {
            for (int j = 1; j < cols; j++) {
                checkRed[j] = (red[i][j] + Math.floorDiv(red[i][j - 1], 2)) % 256;
                checkGreen[j] = (green[i][j] + Math.floorDiv(green[i][j - 1], 2)) % 256;
                checkBlue[j] = (blue[i][j] + Math.floorDiv(blue[i][j - 1], 2)) % 256;
            }
        }
    }

    // input: row index
    // filter: paeth decode
    public void decodePaeth(int i) {
        paethFilter(i, red, red[i], filteredRed[i], false);
        paethFilter(i, green, green[i], filteredGreen[i], false);
        paethFilter(i, blue, blue[i], filteredBlue[i], false);
    }
}


