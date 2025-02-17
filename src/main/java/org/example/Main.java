package org.example;
import java.nio.file.*;
import java.io.*;
import java.util.*;

public class Main {

    /// CONSTANTS
    static final private Scanner cinScan = new Scanner(System.in);
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

    private static boolean saveFile(String filepath, Iterable<String> filecontent){
        try {
            Files.write(Path.of(filepath), filecontent);
            System.out.println("Content written to: " + filepath);
            return true;
        }
        catch (IOException e) {
            System.out.println("FAILED to write to: " + filepath);
            System.out.println("ERROR: " + e);
            return false;
        }
    }


    ////
    ///
    private static TreeSet<String> getMasterList(){
        TreeSet<String> masterList = new TreeSet<>();
        String fname = rootPath + "/" + masterFileName;
        try {
            masterList.addAll(Files.readAllLines(Path.of(fname)));
        }
        catch (IOException e) {
            System.out.println("No masterlist, using new container.");
        }
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

    private static LinkedHashMap<String, String> getVideoInfo(String filename) {
        LinkedHashMap<String, String> itemInfo = new LinkedHashMap<>();
        System.out.println("We'll need to collect information for '"+ filename + "' file's info file.");
        for (var entry : permittedValues.entrySet()) {
            String key = entry.getKey();
            if (key.equals("Filename")) { continue; }
            String[] values = entry.getValue();
            if (values.length > 1) {
                System.out.println("Please enter '" + key + "' of the film.");
                System.out.println("Choose a number from the following choices.");
                for (int i = 0; i < values.length; i++) {
                    int displayInt = i + 1;
                    System.out.println("(" + displayInt + ") " + values[i]);
                }
                while (true) {
                    if (cinScan.hasNextInt()){
                        int nextInt = cinScan.nextInt() - 1;
//                        System.out.println("Next Int: " + nextInt);
                        cinScan.nextLine();
                        if (nextInt >= 0 && nextInt <= values.length){
                            itemInfo.put(key, values[nextInt]);
                            System.out.println("Item Selected: " + values[nextInt]);
                            break;
                        }
                        else {
                            System.out.println("Invalid input. Please enter a number 1 through " + values.length);
                        }
                    }
                    else {
                        System.out.println("Invalid input. Please enter an integer.");
                    }
                }
            }
            else {
                System.out.println("Please enter '" + key + "' of the film.");
                String response = cinScan.nextLine();
                itemInfo.put(key, response);
            }
        }
        return itemInfo;
    }


    private static void promptDelete(String filepath, TreeSet<String> masterList) {
        ArrayList<String> allowedResponses = new ArrayList<>(Arrays.asList("y", "yes", "n", "no"));
        System.out.println("'" + filepath + "' was found in the master list but no longer exists." +
                " Would you like to remove it? (y/n)");
        while (true) {
            String response = cinScan.nextLine().toLowerCase();
            if (allowedResponses.contains(response)){
                if (response.equals("y") || response.equals("yes")) {
                    masterList.remove(filepath);
                    try {
                        Files.delete(Path.of(rootPath + filepath + ".txt"));
                        System.out.println("File deleted: " + filepath + ".txt");
                    } catch (IOException e) {
                        System.out.println("Couldn't delete '" + filepath + "': File missing");
                    }
                    break;
                }
                else {
                    System.out.println("File skipped for deletion.");
                    break;
                }
            }
            else {
                System.out.println("Please enter a valid response: y, yes, n, or no.");
            }
        }
    }




    public static void main(String[] vars) {

        getVideoInfo("SomeMovie.mov");
        getMasterList();
        runTests(
                new TestFilelist(),
                new TestSaveFile()
        );
    }

    interface Test {
        boolean test();
        default void run() {
            String className = this.getClass().getSimpleName();
            if (test()) {
                System.out.println("TEST PASSED: " + className);
            }
            else {
                System.out.println("TEST FAILED: " + className);
            }
        }
        

    }

    public static void runTests(Test... tests) {
        for (Test t : tests) {
            t.run();
        }
    }

    static class TestFilelist implements Test {
        HashSet<String> testCase = new HashSet<>(Arrays.asList("movie_one(2010).mp4","movie_three(2022).mkv", "movie_two(1997).mov"));
        @Override
        public boolean test(){
            return getFileList().equals(testCase);
            }
    }

    static class TestSaveFile implements Test {
        @Override
        public boolean test() {
            ArrayList<String> testCase = new ArrayList<>(List.of("This", "Function", "Works"));
            return saveFile(rootPath + "/testSave.txt",  testCase);
        }
    }

}

