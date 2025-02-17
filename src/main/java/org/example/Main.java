package org.example;
import java.nio.file.*;
import java.io.*;
import java.util.*;

public class Main {

    static final private Scanner cinScan = new Scanner(System.in);
    static final private String rootPath = ".";
    static final private String masterFileName = "masterlist.txt";
    static final private ArrayList<String> videoExtensions = new ArrayList<>(List.of("mkv", "mov", "mp4", "avi"));
    static final private LinkedHashMap<String, String[]> permittedValues = new LinkedHashMap<>(Map.of(
            "Filename", new String[]{""},
            "Title", new String[]{""},
            "ReleaseYear", new String[]{""},
            "SourceType", new String[]{"WEB-DL", "REMUX"},
            "Resolution", new String[]{"1280x720", "1920x1080", "3840x2169"},
            "AudioCodec", new String[]{"AAC", "DD", "DTS-HD", "FLAC"},
            "VideoCodec", new String[]{"H264", "H265", "AV1"}));


    private static boolean saveMasterFile(String filepath, Iterable<String> filecontent){
        try {
            Files.write(Path.of(filepath.substring(0, filepath.lastIndexOf(".")) + ".txt"), filecontent);
            System.out.println("Content written to: " + filepath);
            return true;
        }
        catch (IOException e) {
            System.out.println("FAILED to write to: " + filepath);
            System.out.println("ERROR: " + e);
            return false;
        }
    }

    private static boolean saveInfoFile(String filepath, HashMap<String, String> itemInfo){
        List<String> content = itemInfo.entrySet().stream().map(entry -> entry.getKey() + " : " +
                entry.getValue()).toList();
        try {
            Files.write(Path.of(filepath.substring(0, filepath.lastIndexOf(".")) + ".txt"), content);
            System.out.println("Content written to: " + filepath);
            return true;
        }
        catch (IOException e) {
            System.out.println("FAILED to write to: " + filepath);
            System.out.println("ERROR: " + e);
            return false;
        }
    }


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

    private static HashMap<String, ArrayList<String>> compareLists(HashSet<String> currentFiles, TreeSet<String> masterList) {
        HashMap<String, ArrayList<String>> returnMap = new HashMap<String, ArrayList<String>>();
        ArrayList<String> missingItems = new ArrayList<>();
        ArrayList<String> newItems = new ArrayList<>();
        for (var item : currentFiles) {
            if (!masterList.contains(item)) {
                newItems.add(item);
            }
        }
        for (var item : masterList) {
            if (!currentFiles.contains(item)) {
                missingItems.add(item);
            }
        }
        returnMap.put("missingItems", missingItems);
        returnMap.put("newItems", newItems);
        return returnMap;
    }
    private static void remedyList(TreeSet<String> masterList, ArrayList<String> missingItems, ArrayList<String> newItems){
        System.out.println("First we will add all the new items to the masterlist, then we'll remove missing files.");
        for (var item : newItems) {
            saveInfoFile(item, getVideoInfo(item));
            masterList.add(item);
        }
        for (var item : missingItems) {
            promptDelete(item, masterList);
            masterList.remove(item);
        }
        saveMasterFile("./masterlist.txt", masterList);
        System.out.println("Masterlist brought up to date. Program Closing.");
    }

    private static LinkedHashMap<String, String> getVideoInfo(String filename) {
        LinkedHashMap<String, String> itemInfo = new LinkedHashMap<>();
        System.out.println("We'll need to collect information for '"+ filename + "' file's info file.");
        for (var entry : permittedValues.entrySet()) {
            String key = entry.getKey();
            if (key.equals("Filename")) {
                itemInfo.put(key, filename);
                continue; }
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
                        Files.delete(Path.of(rootPath + "/" + filepath));
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

        TreeSet<String> masterList = getMasterList();
        HashSet<String> currentList = getFileList();
        HashMap<String, ArrayList<String>> diffMap = compareLists(currentList, masterList);
        if (diffMap.get("missingItems").isEmpty() && diffMap.get("newItems").isEmpty()) {
            System.out.println("Masterlist is already update to date. Closing program.");
        }
        else {
            System.out.println(diffMap.get("newItems").size() + " - Items to add.");
            System.out.println(diffMap.get("missingItems").size() + " - Items to remove.");
            remedyList(masterList, diffMap.get("missingItems"), diffMap.get("newItems"));
        }
//        promptDelete("testSave.txt", getMasterList());
//        getVideoInfo("SomeMovie.mov");
//        getMasterList();
//        runTests(
//                new TestFilelist(),
//                new TestSaveFile()
//        );
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
            return saveMasterFile(rootPath + "/testSave.txt",  testCase);
        }
    }

}

