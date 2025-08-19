import java.util.Scanner;

public class Main {
    public static void main(String [] args) {
        System.out.println("Введите число:");
        Scanner number = new Scanner(System.in);

        int firstNumber= number.nextInt();
        int secondNumber= number.nextInt();

        int quotient = firstNumber + secondNumber;
        System.out.println(quotient);
        quotient = firstNumber - secondNumber;
        System.out.println(quotient);
        quotient = firstNumber * secondNumber;
        System.out.println(quotient);
        quotient = firstNumber % secondNumber;
        System.out.println((double)quotient);
    }
}