package org.example;
import java.nio.file.*;
import java.io.*;
import java.util.*;

public class Main {

    /// CONSTANTS
    static final private String rootPath = ".";
    static final private String masterFileName = "masterfile.txt";
    static final private ArrayList<String> videoExtensions = new ArrayList<>(List.of("mkv", "mov", "mp4", "avi"));
    static final private LinkedHashMap<String, String[]> permittedValues = new LinkedHashMap<>(Map.of(
            "Filename", new String[]{""},
            "Title", new String[]{""},
            "ReleaseYear", new String[]{""},
            "SourceType", new String[]{"WEB-DL", "REMUX"},
            "Resolution", new String[]{"1280x720", "1920x1080", "3840x2169"},
            "AudioCodec", new String[]{"AAC", "DD", "DTS-HD", "FLAC"},
            "VideoCodec", new String[]{"H264", "H265", "AV1"}));

// FILE HANDLING

    private LinkedHashMap<String, String[]> getFileInfo(String filename) {
        return null;
    }

//    public class FileHandlerResult {
//        private boolean successful = false;
//        private File fileHandler = null;
//        public FileHandlerResult(boolean success, File fileH) {
//            this.successful = success;
//            this.fileHandler = fileH;
//        }
//        public boolean wasSuccessful(){ return successful; }
//        public File getfileHandler(){ return fileHandler; }
//
//    }

    private static boolean isFileAccessible(String filename) {
        Path filepath = Paths.get(filename);
        return Files.exists(filepath) && Files.isWritable(filepath) && Files.isReadable(filepath);
    }

//    private FileHandlerResult openFile(String filename){
//        if (!isFileAccessible(filename)) { return new FileHandlerResult(false, null); }
//        return new FileHandlerResult(true, null); // TODO: FIX
//    }

    private boolean saveFile(String filePath){ return true; }

    ////
    ///
    private TreeSet<String> getMasterList(){
        TreeSet<String> masterList = new TreeSet<>();
        return masterList;
    }

    private static HashSet<String> getFileList() {
        HashSet<String> currentFileList = new HashSet<>();
        File rootFolder = new File(rootPath);
        ArrayList<String> rootFiles = new ArrayList<>();
        Collections.addAll(rootFiles, rootFolder.list());
        for (var file : rootFiles) {
            String[] stringParts = file.split("\\.");
            if (stringParts.length > 1) {
                if (videoExtensions.contains(stringParts[stringParts.length - 1])){
                    currentFileList.add(file);
                }
            }

        }
        return currentFileList;
    }

    private HashMap<String, ArrayList<String>> compareLists(HashSet<String> currentFiles, TreeSet<String> masterList) {
        HashMap<String, ArrayList<String>> returnMap = new HashMap<String, ArrayList<String>>();
        ArrayList<String> missingItems = new ArrayList<>();
        ArrayList<String> newItems = new ArrayList<>();
        returnMap.put("missingItems", missingItems);
        returnMap.put("newItems", newItems);
        return returnMap;
    }
    private boolean remedyList(HashSet<String> masterList, ArrayList<String> missingItems, ArrayList<String> newItems){
        return true;
    }






    public static void main(String[] vars) {

        HashSet<String> fList = new HashSet<>(getFileList());
        System.out.println(fList.size());
        fList.forEach(System.out::println);
    }

}