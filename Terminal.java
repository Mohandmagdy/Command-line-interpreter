import java.util.Objects;
import java.io.File;
import java.util.Arrays;

public class Terminal {
    Parser parser;
    String curr_directory;
    public Terminal(){
        parser = new Parser();
        curr_directory = ".";
    }
    public void echo(){
        for(String text : parser.args){
            System.out.print(text + ' ');
        }
        System.out.println();
    }
    public void pwd(){
        File currentDir = new File(curr_directory);
        System.out.println(currentDir.getAbsoluteFile());
    }
    public void cd(){
        if(Objects.equals(parser.args[0], "..")){
            File file = new File(curr_directory);
            String file_parent = file.getParent();
            if(file_parent != null){
                curr_directory = file_parent;
            } else {
                System.out.println("The file doesn't have a parent directory");
            }
        } else if(parser.args[0] == null){
            curr_directory = System.getProperty("user.home");
        } else{
            if(parser.args[0].contains("/")){
                File file = new File(parser.args[0]);
                if(file.exists()){
                    curr_directory = parser.args[0];
                } else{
                    System.out.println("The directory is not found");
                }
            } else{
                File file = new File(curr_directory+"\\"+parser.args[0]);
                if(file.exists()){
                    curr_directory = curr_directory+"\\"+parser.args[0];
                } else{
                    System.out.println("The directory is not found");
                }
            }
        }
    }
    public void ls(){
        File currentDir = new File(curr_directory);
        File[] files = currentDir.listFiles();
        if(files != null){
            Arrays.sort(files, (f1, f2) -> f1.getName().compareTo(f2.getName()));
            for(File file : files){
                System.out.print(file.getName() + "  ");
            }
            System.out.println();
        }
        else{
            System.out.println("The current directory has no content");
        }
    }
    public void ls_reversed(){
        File currentDir = new File(curr_directory);
        File[] files = currentDir.listFiles();
        if(files != null){
            Arrays.sort(files, (f1, f2) -> f2.getName().compareTo(f1.getName()));
            for(File file : files){
                System.out.print(file.getName() + "  ");
            }
            System.out.println();
        }
        else{
            System.out.println("The current directory has no content");
        }
    }
    public void mkdir(){
        for(String dir: parser.args){
            if(dir.contains("/")) {
                File file = new File(dir);
                boolean state = file.mkdir();
                if (state) {
                    System.out.println("Directory created successfully");
                } else {
                    System.out.println(dir + " directory couldn't be created");
                }
            } else{
                dir = curr_directory + "\\" + dir;
                System.out.println(dir);
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
    public void exit_code(){
        System.out.println("Thank you for using our program");
        System.exit(0);
    }
    public void wrong_command() {
        if (Objects.equals(parser.args[0], "arguments")) {
            System.out.println("wrong arguments");
        } else {
            System.out.println(parser.commandName + " is not a command");
        }
    }

    public void chooseCommandAction(String input){
        boolean state = parser.parse(input);
        if(state) {
            if(Objects.equals(parser.commandName, "exit")){
                exit_code();
            } else if (Objects.equals(parser.commandName, "echo")) {
                echo();
            } else if (Objects.equals(parser.commandName, "pwd")) {
                pwd();
            } else if (Objects.equals(parser.commandName, "cd")) {
                cd();
            } else if (Objects.equals(parser.commandName, "ls")) {
                ls();
            } else if (Objects.equals(parser.commandName, "ls-r")) {
                ls_reversed();
            } else if (Objects.equals(parser.commandName, "mkdir")) {
                mkdir();
            }
        }
        else{
            wrong_command();
        }
    }

}
