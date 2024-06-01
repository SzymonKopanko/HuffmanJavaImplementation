package pl.edu.pw.ee;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

public class Huffman {
    public int huffman(String pathToRootDir, boolean compress) {
        int amount;
        String pathToFile = pathToRootDir + "tekst.txt";
        if (compress) {
            amount = compressFile(pathToFile);
        } else {
            amount = decompressFile(pathToFile);
        }
        return amount;
    }

    private int compressFile(String pathToRootDir) {
        ArrayList<Character> textList = takeCharsToList(pathToRootDir);

        if (textList.isEmpty()) {
            return 0;
        }
        if (textList.size() == 1) {
            return 1;
        }
        ArrayList<Character> textListCopy = (ArrayList<Character>) textList.clone();
        ArrayList<Node> frequencyList = makeFrequencyList(textListCopy);
        ArrayList<Node> frequencyListCopy = (ArrayList<Node>) frequencyList.clone();
        ArrayList<Node> finalList = addCodeValues(frequencyList);
        return compression(finalList, frequencyListCopy, textList, pathToRootDir);
    }


    private int compression(ArrayList<Node> finalList, ArrayList<Node> frequencyListCopy, ArrayList<Character> textList, String pathToRootDir) {
        try {
            String binaryTextString = "";
            int i;
            HashMap<Character, String> keyMap = new HashMap<>();
            for (i = 0; i < finalList.size(); i++) {
                keyMap.put(finalList.get(i).getCharacter(), finalList.get(i).getCode());
            }
            File file = new File(pathToRootDir);
            for (i = 0; i < textList.size(); i++) {
                binaryTextString += keyMap.get(textList.get(i));
            }
            int amount = binaryTextString.length() / 8;
            int rest = binaryTextString.length() % 8;
            createKeyFile(frequencyListCopy, rest, pathToRootDir);
            if (rest != 0) {
                amount++;
            }
            String tmpString;
            int tmpInt;
            FileOutputStream writer = new FileOutputStream(file, false);
            for (i = 0; i < binaryTextString.length() - 8; i += 8) {
                tmpString = binaryTextString.substring(i, i + 8);
                tmpInt = Integer.parseInt(tmpString, 2);
                writer.write((char) tmpInt);
            }
            if (binaryTextString.length() % 8 != 0) {
                tmpString = binaryTextString.substring(i);
                while (tmpString.length() != 0 && tmpString.length() < 8) {
                    tmpString += "0";
                }
                tmpInt = Integer.parseInt(tmpString, 2);
                writer.write((char) tmpInt);
            }
            writer.close();
            return amount;
        } catch (IOException e) {
            throw new RuntimeException("Błąd pisania skompresowanego pliku!");
        }

    }

    private void createKeyFile(ArrayList<Node> finalList, int rest, String pathToRootDir) {
        try {
            int pointId = pathToRootDir.lastIndexOf(".");
            String keyFileName = pathToRootDir.substring(0, pointId) + "k" + pathToRootDir.substring(pointId);
            FileWriter writer = new FileWriter(keyFileName);
            int i;
            String restString = Integer.toBinaryString(rest);
            while (restString.length() < 3) {
                restString = "0" + restString;
            }
            writer.write(restString);
            for (i = 0; i < finalList.size(); i++) {
                writer.write(finalList.get(i).getCharacter());
                writer.write(finalList.get(i).getFrequency() + "\n");
            }
            writer.close();
        } catch (IOException e) {
            throw new RuntimeException("Błąd tworzenia klucza!");
        }
    }

    private ArrayList<Node> addCodeValues(ArrayList<Node> frequencyList) {
        while (frequencyList.size() > 1) {
            frequencyList.add(new Node(frequencyList.remove(0), frequencyList.remove(0)));
            Collections.sort(frequencyList);
        }
        String tmpCode;
        Node root;
        Node tmpRoot;
        if (frequencyList.isEmpty()) {
            throw new IllegalArgumentException("Plik klucza jest nieodpowiedni!");
        }
        frequencyList.get(0).setRoot(true);
        ArrayList<Node> finalList = new ArrayList<>();
        while (!frequencyList.isEmpty()) {
            root = frequencyList.get(0);
            frequencyList.remove(0);

            if (root.getLeftChild() == null) {
                tmpCode = "";
                tmpRoot = root;
                while (!tmpRoot.isRoot()) {
                    if (tmpRoot.isLeftChild()) {
                        tmpCode = "0" + tmpCode;
                    } else {
                        tmpCode = "1" + tmpCode;
                    }
                    tmpRoot = tmpRoot.getParent();
                }
                root.setCode(tmpCode);
                finalList.add(root);
            } else {
                frequencyList.add(0, root.getRightChild());
                frequencyList.get(0).setIsLeftChild(false);
                frequencyList.get(0).setParent(root);
                frequencyList.add(0, root.getLeftChild());
                frequencyList.get(0).setIsLeftChild(true);
                frequencyList.get(0).setParent(root);
            }
        }
        return finalList;
    }


    private ArrayList<Node> makeFrequencyList(ArrayList<Character> textList) {
        ArrayList<Node> frequencyList = new ArrayList<>();
        while (!textList.isEmpty()) {
            int sizeBefore = textList.size();
            char deletedChar = textList.get(0);
            textList.removeAll(Collections.singleton(deletedChar));
            frequencyList.add(new Node(deletedChar, sizeBefore - textList.size()));
        }
        Collections.sort(frequencyList);
        return frequencyList;
    }

    private ArrayList<Character> takeCharsToList(String pathToRootDir) {
        try {
            File file = new File(pathToRootDir);
            FileInputStream reader = new FileInputStream(file);
            int c;
            ArrayList<Character> textList = new ArrayList<>();
            while ((c = reader.read()) != -1) {
                textList.add((char) c);
            }
            return textList;
        } catch (FileNotFoundException e) {
            try {
                throw new FileNotFoundException("Nie znaleziono pliku");
            } catch (FileNotFoundException ex) {
                throw new RuntimeException(ex);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private int decompressFile(String pathToRootDir) {
        ArrayList<Character> textList = takeCharsToList(pathToRootDir);
        if (textList.isEmpty()) {
            return 0;
        }
        if (textList.size() == 1) {
            return 1;
        }

        ArrayList<Node> frequencyList = readKeyFile(pathToRootDir);
        ArrayList<Node> finalList = addCodeValues(frequencyList);
        String binaryTextString = getBinaryRepresentation(textList, pathToRootDir);
        return decompression(finalList, binaryTextString, pathToRootDir);
    }

    private int readRestFromKeyFile(String pathToRootDir) {
        try {
            FileReader reader = new FileReader(pathToRootDir);
            BufferedReader bufferedReader = new BufferedReader(reader);
            int tmpInt;
            String restString = "";
            for (tmpInt = 0; tmpInt < 3; tmpInt++) {
                if ((tmpInt = bufferedReader.read()) != -1) {
                    restString += String.valueOf((char) tmpInt);
                }
            }
            tmpInt = Integer.parseInt(restString, 2);
            return tmpInt;
        } catch (IOException e) {
            throw new RuntimeException("Błąd czytania z pliku z kluczem!");
        }
    }

    private int decompression(ArrayList<Node> finalList, String binaryTextString, String pathToRootDir) {
        int amount = 0;
        File file = new File(pathToRootDir);
        try {
            int i;
            HashMap<String, Character> keyMap = new HashMap<>();
            for (i = 0; i < finalList.size(); i++) {
                keyMap.put(finalList.get(i).getCode(), finalList.get(i).getCharacter());
            }
            FileOutputStream writer = new FileOutputStream(file.getName(), false);
            String tmpString;
            i = 1;
            while (i <= binaryTextString.length()) {
                tmpString = binaryTextString.substring(0, i);
                if (keyMap.containsKey(tmpString)) {
                    writer.write(keyMap.get(tmpString));
                    binaryTextString = binaryTextString.substring(i);
                    i = 1;
                    amount++;
                } else {
                    i++;
                }
            }
            writer.close();
            return amount;
        } catch (IOException e) {
            throw new RuntimeException("Błąd pisania do pliku przy dekompresji");
        }
    }

    private String getBinaryRepresentation(ArrayList<Character> textList, String pathToRootDir) {
        int pointId = pathToRootDir.lastIndexOf(".");
        String keyFileName = pathToRootDir.substring(0, pointId) + "k" + pathToRootDir.substring(pointId);
        int rest = readRestFromKeyFile(keyFileName);
        String binaryTextString = "";
        char tmpChar;
        int tmpInt;
        String tmpString;
        int i;
        for (i = 0; i < textList.size(); i++) {
            tmpChar = textList.get(i);
            tmpInt = tmpChar;
            tmpString = Integer.toBinaryString(tmpInt);
            while (tmpString.length() < 8) {
                tmpString = "0" + tmpString;
            }
            binaryTextString += tmpString;
        }
        binaryTextString = binaryTextString.substring(0, binaryTextString.length() - rest);
        return binaryTextString;
    }

    private ArrayList<Node> readKeyFile(String pathToRootDir) {
        try {
            ArrayList<Node> frequencyList = new ArrayList<>();
            final int ASCII_MAX = 255;
            int pointId = pathToRootDir.lastIndexOf(".");
            String keyFileName = pathToRootDir.substring(0, pointId) + "k" + pathToRootDir.substring(pointId);
            FileReader reader = new FileReader(keyFileName);
            BufferedReader bufferedReader = new BufferedReader(reader);
            int tmpInt;
            String tmpString;
            for (tmpInt = 0; tmpInt < 3; tmpInt++) {
                bufferedReader.read();
            }
            while ((tmpInt = bufferedReader.read()) != -1) {
                if (tmpInt > ASCII_MAX) {
                    throw new IllegalArgumentException("Znaleziono w kluczu znaki spoza tablicy ASCII!");
                }
                tmpString = bufferedReader.readLine();
                if (tmpString.isEmpty()) {
                    throw new IllegalArgumentException("Plik klucza jest nieodpowiedni!");
                }
                for (int i = 0; i < tmpString.length(); i++) {
                    if (!Character.isDigit(tmpString.charAt(i))) {
                        throw new IllegalArgumentException("Plik klucza jest nieodpowiedni!");
                    }
                }
                frequencyList.add(new Node((char) tmpInt, Integer.parseInt(tmpString)));
            }
            return frequencyList;
        } catch (IOException e) {
            throw new RuntimeException("Błąd pisania skompresowanego pliku!");
        }
    }
}
