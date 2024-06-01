package pl.edu.pw.ee;

import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class HuffmanTest {

    @Test
    public void compressionAndDecompressionTestAmount() {
        //given
        File tekst = new File("tekst.txt");
        String absPath = tekst.getAbsolutePath();
        String pathToRootDir = absPath.substring(0, absPath.lastIndexOf("\\") + 1);
        Huffman huff = new Huffman();
        //when
        //int amountCompressed = huff.huffman(pathToRootDir, true);
        int amountDecompressed = huff.huffman(pathToRootDir, false);
        //then
        //Assert.assertEquals(42, amountCompressed);
        Assert.assertEquals(72, amountDecompressed);
    }

    @Test(expected = IllegalArgumentException.class)
    public void EmptyKeyException() throws IOException {
        File tekst = new File("tekst.txt");
        String absPath = tekst.getAbsolutePath();
        String pathToRootDir = absPath.substring(0, absPath.lastIndexOf("\\") + 1);
        Huffman huff = new Huffman();
        huff.huffman(pathToRootDir, true);
        File key = new File("tekstk.txt");
        FileWriter writer = new FileWriter(key, false);
        writer.close();
        huff.huffman(pathToRootDir, false);
    }

    @Test(expected = IllegalArgumentException.class)
    public void BadKeyException() throws IOException {
        File tekst = new File("tekst.txt");
        String absPath = tekst.getAbsolutePath();
        String pathToRootDir = absPath.substring(0, absPath.lastIndexOf("\\") + 1);
        Huffman huff = new Huffman();
        huff.huffman(pathToRootDir, true);
        File key = new File("tekstk.txt");
        FileWriter writer = new FileWriter(key, false);
        writer.write("121ldkfjlasdjf;las\n0asdf23qweafq q34t");
        writer.close();
        huff.huffman(pathToRootDir, false);
    }
}
