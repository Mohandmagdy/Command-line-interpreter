import java.util.Scanner;
public class Main {
    public static void main(String[] args) {
        Terminal terminal = new Terminal();

        while(true){
            Scanner scanner = new Scanner(System.in);
            System.out.println("Please enter your command.");
            String command = scanner.nextLine();
            terminal.chooseCommandAction(command);
        }
    }
}