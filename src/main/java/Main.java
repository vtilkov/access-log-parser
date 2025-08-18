import java.util.Scanner;

public class Main {
    public static void main(String [] args) {
        System.out.println(("Введите текст и нажмите <Enter>:"+args[0]));
        //String text = new Scanner(System.in).nextLine();
        System.out.println(args[0]);
        System.out.println("Длина текста: " + args[0].length());
    }
}