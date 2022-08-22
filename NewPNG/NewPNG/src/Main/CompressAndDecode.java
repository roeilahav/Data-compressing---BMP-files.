/**
 * NewPNG
 * Submitted by:
 * Tom Basha 	ID# 311425714
 * Roei Lahav	ID# 315808469
 */

package Main;

import javax.swing.*;
import java.io.File;

public class CompressAndDecode extends SwingWorker<Void, String> {
    JButton resetBtn;
    String selectedFilePath, selectedPath, fileName;
    File selectedFile;

    public CompressAndDecode(File selectedFile, String selectedPath, String fileName, JButton reset) {
        this.selectedFile = selectedFile;
        this.selectedPath = selectedPath;
        this.fileName = fileName;
        this.resetBtn = reset;
    }

    public void CompressDecode() {
        Compression compress = new Compression(selectedFile, selectedPath);
        compress.Compress();

        System.out.println("\n\n");
        selectedFilePath = selectedPath + fileName;
        selectedFile = new File(selectedFilePath);

        Decoding decoding = new Decoding(selectedFile, selectedPath);
        decoding.Decode();

    }

    @Override
    protected Void doInBackground() throws Exception {
        CompressDecode();
        resetBtn.setEnabled(true);
        return null;
    }
}
