/**
 * NewPNG
 * Submitted by:
 * Tom Basha 	ID# 311425714
 * Roei Lahav	ID# 315808469
 */

package Tools.LZ77;

import java.util.LinkedList;
import java.util.Queue;

import static java.lang.Integer.parseInt;

public class LZ77 {
    private PrefixTable threesPrefix = new PrefixTable();          // holds prefix in length 3
    private int[] toEncode;
    private final int s = 32768;
    private final int t = 258;

    public LZ77() {
    }

    // input: data to compress
    public int[] Compress(int[] input) {
        toEncode = input;
        int curr = 0, prefixFillPtr = 0;        // pointers
        Queue<Integer> output = new LinkedList<Integer>();
        int queCnt = 0;
        Duo nextDuo;

        while (curr < input.length) {
            nextDuo = findBestMatch(curr);            // finds/creates the next Duo


            if (nextDuo.getLength() == 0) {
                output.add(input[curr]);
                curr++;
                queCnt++;
            } else {
                queCnt += addDuo(output, nextDuo);
                curr += nextDuo.getLength();          // update current position pointer
            }

            if (curr == input.length) {
                break;
            }

            fillPrefixTable(curr, prefixFillPtr);      // fill prefix arrays
            prefixFillPtr = curr;
        }

        int[] compressed = new int[queCnt];
        for (int i = 0; i < queCnt; i++) {
            compressed[i] = output.poll();
        }

        return compressed;
    }

    private int addDuo(Queue<Integer> output, Duo nextDuo) {
        String duo = nextDuo.toString();
        for (int i = 0; i < duo.length(); i++) {
            output.add((int) duo.charAt(i));
        }
        return duo.length();
    }

    // input: current location.
    // output: the next tuple.
    // finds matching prefix and counts the match length.
    // if not found returns default tuple.
    private Duo findBestMatch(int curr) {
        int currMatchLength = -1;
        String find;
        PrefixNode check;
        Duo currDuo = new Duo(0, 0);      // creates default tuple
        int bufferStart = curr - s;
        if (bufferStart < 0) bufferStart = 0;

        /*********** SEARCH IN LENGTH 3 ***********/
        if (curr > 2 && curr < toEncode.length - 3) {
            find = "" + (char) toEncode[curr] + (char) toEncode[curr + 1] + (char) toEncode[curr + 2];
            check = threesPrefix.search(find, bufferStart);

            while (check != null && check.getStart() >= bufferStart) {          // looking for longest match
                currMatchLength = checkMatchLength(check.getStart(), curr, 3);
                if (currMatchLength > currDuo.getLength())
                    currDuo = new Duo(curr - check.getStart(), currMatchLength);
                if (currMatchLength == t)
                    break;
                check = threesPrefix.searchNext(find, check.getNext(), bufferStart);
            }
        }

        return currDuo;
    }

    // input: prefix pointer, current location pointer, prefix length.
    // output: match length.
    // calculates match length by comparing char by char.
    private int checkMatchLength(int checkPtr, int currPtr, int matchCnt) {
        checkPtr += matchCnt;
        currPtr += matchCnt;
        int maxSteps = t - matchCnt;         // calculates how many steps available
        while (currPtr < toEncode.length - 1 && maxSteps != 0) {
            if (toEncode[checkPtr] == toEncode[currPtr]) {
                matchCnt++;
                checkPtr++;
                currPtr++;
                maxSteps--;
            } else
                return matchCnt;
        }
        return matchCnt;
    }

    // input: current location, from where to start filling.
    // fill prefixes in length 3 from the start position to current location.
    private void fillPrefixTable(int curr, int prefixFillPtr) {
        String pref;

        /***** FILL IN LENGTH 3 *****/
        while (curr > 2 && prefixFillPtr != curr && prefixFillPtr + 2 < toEncode.length - 3) {
            pref = "" + (char) toEncode[prefixFillPtr] + (char) toEncode[prefixFillPtr + 1] + (char) toEncode[prefixFillPtr + 2];
            threesPrefix.insert(new PrefixNode(prefixFillPtr, pref));
            prefixFillPtr++;
        }

    }


    public int[] Decode(int[] toDecode, int lzLength) {
        int[] decodedLZ = new int[lzLength];
        int newChar, curr = 0;

        for (int i = 0; i < toDecode.length; i++) {
            newChar = toDecode[i];

            if (newChar != '<') {
                decodedLZ[curr] = newChar;
                curr++;
            } else if (i < toDecode.length - 4) {
                if (verifyDuo(toDecode, i)) {
                    curr += readDuo(toDecode, i, decodedLZ, curr);
                    while (toDecode[i] != '>')
                        i++;
                } else {
                    decodedLZ[curr] = newChar;
                    curr++;
                }
            }
        }

        return decodedLZ;
    }

    private boolean verifyDuo(int[] toDecode, int i) {
        int j, k;
        int curr;
        for (j = 1; j < 6; j++) {
            curr = toDecode[i + j];
            if (j == 1) {
                if (curr < '1' || curr > '9')
                    return false;
                else continue;
            } else {
                if (curr < '0' || curr > '9')
                    break;
            }
        }
        curr = toDecode[i + j];
        if (curr != ',')
            return false;

        for (k = 1; k < 4; k++) {
            curr = toDecode[i + j + k];
            if (k == 1) {
                if (curr < '1' || curr > '9')
                    return false;
            } else {
                if (curr < '0' || curr > '9')
                    break;
            }
        }
        curr = toDecode[i + j + k];
        if (curr != '>')
            return false;
        return true;
    }

    private int readDuo(int[] toDecode, int i, int[] decodedLZ, int curr) {
        int newChar;
        String startBuilder = "", lengthBuilder = "";
        int start, finish;

        i++;
        newChar = toDecode[i];
        while (newChar != ',') {
            startBuilder += (char) newChar;
            i++;
            newChar = toDecode[i];
        }

        i++;
        newChar = toDecode[i];
        while (newChar != '>') {
            lengthBuilder += (char) newChar;
            i++;
            newChar = toDecode[i];
        }

        start = curr - parseInt(startBuilder);
        if (start < 0)
            start = 0;

        finish = start + parseInt(lengthBuilder);

        decodeDuo(decodedLZ, start, finish, curr);
        return parseInt(lengthBuilder);
    }

    // input: decoded string so far, copy start location, copy finish location.
    // output: decoded string with another tuple decoded.
    // copy chars from start position to the end of the string until reaches finish.
    private void decodeDuo(int[] output, int start, int finish, int curr) {
        for (int i = start; i < finish; i++) {
            output[curr] = output[i];
            curr++;
        }
    }
}