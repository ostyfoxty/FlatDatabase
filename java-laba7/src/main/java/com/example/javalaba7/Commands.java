package com.example.javalaba7;

/**
 * @author ostro
 */

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;


public class Commands {

    public static abstract class Report implements Runnable {
        protected boolean error = false;

        public boolean isError() {
            return error;
        }

        protected String message = null;

        public String getErrorMessage() {
            return message;
        }

        protected List<String> result = null;

        public List<String> getResult() {
            return result;
        }

        public void go() {
            go(null);
        }

        public void go(String name) {
            if (name == null) {
                name = "Report";
            }
            Thread t = new Thread(this, name);
            t.start();
            while (t.isAlive()) {
                try {
                    Thread.sleep(100);
                } catch (Exception ex) {
                }
            }
        }
    }

    ;

    static String fileName;
    static String idxName;
    static String fileNameBak;
    static String idxNameBak;
    static String path;

    static final String IDX_EXT = "idx";
    static String fileExt;

    static public synchronized void setFile(File file) {
        fileName = file.getName();
        path = file.getPath();
        path = path.substring(0, path.indexOf(fileName));
        String[] str = fileName.split("\\.");
        fileExt = str[1];
        idxName = str[0] + "." + IDX_EXT;
        fileNameBak = str[0] + ".~" + fileExt;
        idxNameBak = str[0] + ".~" + IDX_EXT;

        fileName = path + fileName;
        idxName = path + idxName;
        fileNameBak = path + fileNameBak;
        idxNameBak = path + idxNameBak;
    }

    private static synchronized void deleteBackup() {
        new File(fileNameBak).delete();
        new File(idxNameBak).delete();
    }

    private static synchronized void backup() {
        deleteBackup();
        new File(fileName).renameTo(new File(fileNameBak));
        new File(idxName).renameTo(new File(idxNameBak));
    }

    public static void fileCopy(String from, String to) throws IOException {
        byte[] buf = new byte[8192];
        int size;
        try (FileInputStream fis = new FileInputStream(from);
             FileOutputStream fos = new FileOutputStream(to)) {
            while ((size = fis.read(buf)) > 0) {
                fos.write(buf, 0, size);
            }
        }
    }

    private static synchronized void backupCopy() throws IOException {
        backup();
        fileCopy(fileNameBak, fileName);
        fileCopy(idxNameBak, idxName);
    }

    public static synchronized void appendFile(boolean zipped, Flat flat)
            throws FileNotFoundException, IOException, ClassNotFoundException,
            KeyNotUniqueException {

        //
        backupCopy();
        try (Index idx = Index.load(idxName);
             RandomAccessFile raf = new RandomAccessFile(fileName, "rw")) {
            if (flat == null) {
                return;
            }
            long pos = Buffer.writeObject(raf, flat, zipped);
            idx.put(flat, pos);
        }
    }

    public static synchronized List<String> readFile()
            throws FileNotFoundException, IOException, ClassNotFoundException {

        //
        ArrayList<String> list = new ArrayList<>();
        long pos;
        try (RandomAccessFile raf = new RandomAccessFile(fileName, "rw")) {
            while ((pos = raf.getFilePointer()) < raf.length()) {
                Flat flat = (Flat) Buffer.readObject(raf, pos, null);
                list.add(flat.toString());
            }
            System.out.flush();
        }
        return list;
    }

    private static IndexBase indexByArg(String arg, Index idx)
            throws IllegalArgumentException {
        switch (arg) {
            case "Number" -> {
                return idx.nums;
            }
            case "Area" -> {
                return idx.areas;
            }
            case "Rooms" -> {
                return idx.rooms;
            }
            case "Lifetime" -> {
                return idx.lifetimes;
            }
            default -> throw new IllegalArgumentException("Illegal argument: " + arg);
        }

    }

    public static synchronized List<String> readFile(String arg, boolean reverse)
            throws ClassNotFoundException, IOException, IllegalArgumentException {

        ArrayList<String> list = new ArrayList<>();
        try (Index idx = Index.load(idxName);
             RandomAccessFile raf = new RandomAccessFile(fileName, "rw")) {
            IndexBase pidx = indexByArg(arg, idx);
            String[] keys =
                    pidx.getKeys(reverse ? new KeyCompReverse() : new KeyComp());
            for (String key : keys) {
                Long[] poss = pidx.get(key);
                for (long pos : poss) {
                    Flat flat = (Flat) Buffer.readObject(raf, pos, null);
                    if (arg.equals("Area")) {
                        flat.setArea(key);
                    }
                    list.add(flat.toString());
                }
            }
        }
        return list;
    }

    public static synchronized List<String> findByKey(String type, String value)
            throws ClassNotFoundException, IOException, IllegalArgumentException {

        ArrayList<String> list = new ArrayList<>();
        try (Index idx = Index.load(idxName);
             RandomAccessFile raf = new RandomAccessFile(fileName, "rw")) {
            System.out.println(type);
            System.out.println();
            IndexBase pidx = indexByArg(type, idx);
            System.out.println(pidx == null);

            if (!pidx.contains(value)) {
                throw new IOException("Key not found: " + value);
            }
            Long[] poss = pidx.get(value);
            for (long pos : poss) {
                Flat flat = (Flat) Buffer.readObject(raf, pos, null);
                list.add(flat.toString());
            }
        }
        return list;
    }

    public static synchronized List<String> findByKey(String type, String value, int cmp)
            throws ClassNotFoundException, IOException, IllegalArgumentException {

        ArrayList<String> list = new ArrayList<>();
        try (Index idx = Index.load(idxName);
             RandomAccessFile raf = new RandomAccessFile(fileName, "rw")) {
            IndexBase pidx = indexByArg(type, idx);
            if (!pidx.contains(value)) {
                throw new IOException("Key not found: " + value);
            }
            Comparator<String> comp = (cmp == 2) ? new KeyComp() : new KeyCompReverse();
            String[] keys = pidx.getKeys(comp);
            for (String key : keys) {
                Long[] poss = pidx.get(key);
                if ((cmp == 2) ? compareX(Integer.parseInt(key), Integer.parseInt(value)) == -1 : compareX(Integer.parseInt(key), Integer.parseInt(value)) == 1) {
                    for (long pos : poss) {
                        Flat flat = (Flat) Buffer.readObject(raf, pos, null);
                        list.add(flat.toString());
                    }
                }
            }
        }
        return list;
    }

    private static int compareX(int first, int second) {
        if (first > second) return 1;
        else if (first < second) return -1;
        else return 0;
    }

    public static synchronized void deleteFile(String type, String value)
            throws ClassNotFoundException, IOException, KeyNotUniqueException,
            IllegalArgumentException {


        //
        Long[] poss;
        try (Index idx = Index.load(idxName)) {
            IndexBase pidx = indexByArg(type, idx);
            if (pidx.contains(value) == false) {
                throw new IOException("Key not found: " + value);
            }
            poss = pidx.get(value);
        }
        backup();
        Arrays.sort(poss);
        try (Index idx = Index.load(idxName);
             RandomAccessFile fileBak = new RandomAccessFile(fileNameBak, "rw");
             RandomAccessFile file = new RandomAccessFile(fileName, "rw")) {
            boolean[] wasZipped = new boolean[]{false};
            long pos;
            while ((pos = fileBak.getFilePointer()) < fileBak.length()) {
                Flat flat = (Flat) Buffer.readObject(fileBak, pos, wasZipped);
                if (Arrays.binarySearch(poss, pos) < 0) { // if not found in deleted
                    long ptr = Buffer.writeObject(file, flat, wasZipped[0]);
                    idx.put(flat, ptr);
                }
            }
        }
    }
}

