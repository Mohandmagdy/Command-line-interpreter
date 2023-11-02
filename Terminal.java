import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Objects;
import java.io.File;
import java.util.Arrays;
import java.util.Scanner;

public class Terminal {
    Parser parser;
    ArrayList<String> history ;
    String curr_directory;

    public Terminal() {
        parser = new Parser();
        curr_directory = System.getProperty("user.home");
        history = new ArrayList<>();
    }

    public void echo() {
        for (String text : parser.args) {
            System.out.print(text + ' ');
        }
        System.out.println();
    }

    public void pwd() {
        if (parser.args[0] != null) {
            System.out.println("Wrong arguments");
            return;
        }
        File currentDir = new File(curr_directory);
        System.out.println(currentDir.getAbsoluteFile());
    }

    public void cd() {
        if (parser.args.length > 1 && parser.args[0] != null ) {
            System.out.println("Wrong arguments");
            return;
        }
        if (Objects.equals(parser.args[0], "..")) {
            File file = new File(curr_directory);
            String file_parent = file.getParent();
            if (file_parent != null) {
                curr_directory = file_parent;
            } else {
                System.out.println("The file doesn't have a parent directory");
            }
        } else if (parser.args[0] == null) {
            curr_directory = System.getProperty("user.home");
        } else {
            if (parser.args[0].contains("/")) {
                File file = new File(parser.args[0]);
                if (file.exists()) {
                    curr_directory = parser.args[0];
                } else {
                    System.out.println("The directory is not found");
                }
            } else {
                File file = new File(curr_directory + "\\" + parser.args[0]);
                if (file.exists()) {
                    curr_directory = curr_directory + "\\" + parser.args[0];
                } else {
                    System.out.println("The directory is not found");
                }
            }
        }
    }

    public void ls() {
        if (Objects.equals(parser.args[0], "-r") && parser.args.length == 1) {
            ls_reversed();
            return;
        } else if (parser.args[0] != null) {
            System.out.println("Wrong arguments");
            return;
        }
        File currentDir = new File(curr_directory);
        File[] files = currentDir.listFiles();
        if (files != null) {
            Arrays.sort(files, (f1, f2) -> f1.getName().compareTo(f2.getName()));
            for (File file : files) {
                System.out.print(file.getName() + "  ");
            }
            System.out.println();
        } else {
            System.out.println("The current directory has no content");
        }
    }

    public void ls_reversed() {
        File currentDir = new File(curr_directory);
        File[] files = currentDir.listFiles();
        if (files != null) {
            Arrays.sort(files, (f1, f2) -> f2.getName().compareTo(f1.getName()));
            for (File file : files) {
                System.out.print(file.getName() + "  ");
            }
            System.out.println();
        } else {
            System.out.println("The current directory has no content");
        }
    }

    public void mkdir() {
        for (String dir : parser.args) {
            if (dir.contains("/")) {
                File file = new File(dir);
                boolean state = file.mkdir();
                if (state) {
                    System.out.println("Directory created successfully");
                } else {
                    System.out.println(dir + " directory couldn't be created");
                }
            } else {
                dir = curr_directory + "\\" + dir;
                File file = new File(dir);
                boolean state = file.mkdir();
                if (state) {
                    System.out.println("Directory created successfully");
                } else {
                    System.out.println(dir + " directory couldn't be created");
                }
            }
        }
    }
    
    public void rmdir() {
        if (parser.args.length != 1) {
            System.out.println("Usage: rmdir <directory_path>");
            return;
        }
        
        String directoryPath = parser.args[0];
        
        if (directoryPath.equals("*")) {
            // Case 1: Remove all empty directories in the current directory
            File currentDir = new File(curr_directory);
            removeEmptyDirectories(currentDir);
        } else {
            // Case 2: Remove the specified directory only if it is empty
            File dir = new File(directoryPath);
            if (!dir.exists() || !dir.isDirectory()) {
                System.out.println("The directory does not exist or is not a directory");
                return;
            }
            
            if (isEmptyDirectory(dir)) {
                if (dir.delete()) {
                    System.out.println("Directory removed successfully");
                } else {
                    System.out.println("An error occurred while removing the directory");
                }
            } else {
                System.out.println("The directory is not empty and cannot be removed");
            }
        }
    }
    
    private void removeEmptyDirectories(File directory) {
        if (directory.exists() && directory.isDirectory()) {
            File[] subDirs = directory.listFiles();
            if (subDirs != null) {
                for (File subDir : subDirs) {
                    if (subDir.isDirectory()) {
                        removeEmptyDirectories(subDir);
                    }
                }
            }
            // After processing subdirectories, try to delete the current directory
            if (isEmptyDirectory(directory)) {
                if (directory.delete()) {
                    System.out.println("Empty directory removed: " + directory.getAbsolutePath());
                } else {
                    System.out.println("An error occurred while removing the directory: " + directory.getAbsolutePath());
                }
            }
        }
    }
    
    private boolean isEmptyDirectory(File directory) {
        return directory.exists() && directory.isDirectory() && directory.list().length == 0;
    }
    
    public void touch() {
        if (parser.args.length != 1) {
            System.out.println("Usage: touch <file_path>");
            return;
        }
        
        String filePath = parser.args[0];
        
        if (filePath.contains("/")) {
            // If the input contains a "/", it's treated as an absolute path
            File file = new File(filePath);
            
            try {
                if (file.createNewFile()) {
                    System.out.println("File created successfully");
                } else {
                    System.out.println("File already exists or couldn't be created");
                }
            } catch (IOException e) {
                System.out.println("An error occurred while creating the file: " + e.getMessage());
            }
        } else {
            // If the input is a relative (short) path, combine it with the current directory
            File file = new File(curr_directory + File.separator + filePath);
            
            try {
                if (file.createNewFile()) {
                    System.out.println("File created successfully");
                } else {
                    System.out.println("File already exists or couldn't be created");
                }
            } catch (IOException e) {
                System.out.println("An error occurred while creating the file: " + e.getMessage());
            }
        }
    }
    
    public void cp() {
        if (parser.args.length != 2) {
            System.out.println("Usage: cp <source_file> <destination_file>");
            return;
        }
        
        String sourceFilePath = parser.args[0];
        String destinationFilePath = parser.args[1];
        
        File sourceFile = new File(sourceFilePath);
        File destinationFile = new File(destinationFilePath);
        
        if (!sourceFile.exists()) {
            System.out.println("Source file does not exist");
            return;
        }
        
        if (destinationFile.isDirectory()) {
            // If the destination is a directory, create a file with the same name in that directory
            destinationFile = new File(destinationFile.getAbsolutePath() + File.separator + sourceFile.getName());
        }
        
        try {
            Files.copy(sourceFile.toPath(), destinationFile.toPath());
            System.out.println("File copied successfully");
        } catch (IOException e) {
            System.out.println("An error occurred while copying the file: " + e.getMessage());
        }
    }
    
    public void cp_r() {
        if (parser.args.length != 3) {
            System.out.println("Usage: cp -r <source_directory> <destination_directory>");
            return;
        }
        
        String sourceDirectoryPath = parser.args[1];
        String destinationDirectoryPath = parser.args[2];
        
        File sourceDirectory = new File(sourceDirectoryPath);
        File destinationDirectory = new File(destinationDirectoryPath);
        
        if (!sourceDirectory.exists() || !sourceDirectory.isDirectory()) {
            System.out.println("Source directory does not exist or is not a directory");
            return;
        }
        
        if (!destinationDirectory.exists() || !destinationDirectory.isDirectory()) {
            System.out.println("Destination directory does not exist or is not a directory");
            return;
        }
        
        // Construct the destination path for the source directory within the destination directory
        String destinationPath = destinationDirectoryPath + File.separator + sourceDirectory.getName();
        
        File destinationPathFile = new File(destinationPath);
        
        try {
            Files.walk(sourceDirectory.toPath())
                    .forEach(source -> {
                        try {
                            Path dest = destinationPathFile.toPath().resolve(sourceDirectory.toPath().relativize(source));
                            Files.copy(source, dest, StandardCopyOption.COPY_ATTRIBUTES);
                        } catch (IOException e) {
                            System.out.println("An error occurred while copying the directory: " + e.getMessage());
                        }
                    });
            System.out.println("Directory copied successfully");
        } catch (IOException e) {
            System.out.println("An error occurred while copying the directory: " + e.getMessage());
        }
    }
    public void cat() {
        File f = new File(this.parser.args[0]);
        if (!f.canRead()) {
            System.out.println("Wrong Path..!");
        } else {
            String data = "";
            
            try {
                Scanner scanner = new Scanner(f);
                data = data + scanner;
            } catch (FileNotFoundException var6) {
                var6.printStackTrace();
            }
            
            if (this.parser.args.length > 1) {
                new File(this.parser.args[1]);
                if (!f.canRead()) {
                    System.out.println("Wrong Path..!");
                    return;
                }
                
                try {
                    Scanner scanner = new Scanner(f);
                    data = data + scanner;
                } catch (FileNotFoundException var5) {
                    var5.printStackTrace();
                }
            }
            
            System.out.println(data);
        }
    }
    public void rm() {
        String s = this.curr_directory + File.separator + this.parser.args[0];
        System.out.println(s);
        File f = new File(s);
        if (f.delete()) {
            System.out.println("deleted successfully...!");
        } else {
            System.out.println("File not Found");
        }
        
    }
    public void getHistory(){
        for (String s : history) {
            System.out.println(s);
        }
    }

    public void exit_code() {
        System.out.println("Thank you for using our program");
        System.exit(0);
    }

    public void chooseCommandAction(String input) {
        Arrays.fill(parser.args, null);
        boolean execute =parser.parse(input);
        String commandName = parser.getCommandName();
        history.add(this.parser.commandName);
        if (Objects.equals(commandName, "exit")) {
            exit_code();
        } else if (Objects.equals(commandName, "echo")) {
            echo();
        } else if (Objects.equals(commandName, "pwd")) {
            pwd();
        } else if (Objects.equals(commandName, "cd")) {
            cd();
        } else if (Objects.equals(commandName, "ls")) {
            ls();
        } else if (Objects.equals(commandName, "mkdir")) {
            mkdir();
        }
        else if (Objects.equals(commandName, "rmdir")) {
            rmdir();
        }
        else if (Objects.equals(commandName, "touch")) {
            touch();
         } else if (Objects.equals(commandName, "cp") && Objects.equals(parser.args[0], "-r")) {
            cp_r();
        }else if (Objects.equals(commandName, "cp")) {
            cp();
         }else if (Objects.equals(this.parser.commandName, "rm")) {
            if (this.parser.args.length != 1) {
                System.out.println("Wrong Path..!!");
            } else {
                this.rm();
            }
        } else if (Objects.equals(this.parser.commandName, "cat")) {
            if (this.parser.args.length != 2 && this.parser.args.length != 1) {
                System.out.println("need at least one file Path");
            } else {
                this.cat();
            }
        } else if (Objects.equals(this.parser.commandName, "history")) {
            this.getHistory();
        }
        else {
            System.out.println("Wrong command");
        }

    }
}
