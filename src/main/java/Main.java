import java.util.Scanner;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.File;

//класс исключения для строк длиннее 1024 символов
/* Допишите код таким образом, чтобы он прекращал своё выполнение (выбрасывал исключение) в случаях, если в файле встретилась строка длиннее 1024 символов. Создайте для данного исключения собственный класс исключения или объект класса RuntimeException, при создании которого в конструктор передайте понятное сообщение об ошибке.*/
class LineTooLongException extends RuntimeException {
    public LineTooLongException(String message) {
        super(message);
    }
}

public class Main {
    public static void main(String [] args) {
        System.out.println("Введите число:");
        Scanner scanner = new Scanner(System.in);

        try {
            int firstNumber = scanner.nextInt();
            int secondNumber = scanner.nextInt();

            int result = firstNumber + secondNumber;
            System.out.println("Сумма: " + result);

            result = firstNumber - secondNumber;
            System.out.println("Разность: " + result);

            result = firstNumber * secondNumber;
            System.out.println("Произведение: " + result);

            //обработка деления на ноль
            if (secondNumber != 0) {
                result = firstNumber % secondNumber;
                System.out.println("Остаток от деления: " + (double) result);
            } else {
                System.out.println("Ошибка: деление на ноль! ");
            }
        } catch (Exception e) {
            System.out.println("Ошибка ввода: необходимо ввести целое число ");
            return;
        }

        //поехали с файлом "access.log"
        System.out.println("\nВведите путь к файлу:");
        scanner.nextLine();
        String fileName = scanner.nextLine();

        analyzeFile(fileName);
    }

    public static void analyzeFile(String fileName) {
        //Проверка есть ли файл и файл ли это а не папка
        /*  После проверки существования
            файла и того, что указанный путь является путём именно к файлу, а не к папке,*/
        File file = new File(fileName);
        if (!file.exists()) {
            System.out.println("Ошибка: файл не существует ");
            return;
        }
        if (!file.isFile()) {
            System.out.println("Ошибка: указанный путь ведет к папке, а не к файлу! ");
            return;
        }

        int totalLines = 0;
        int maxLength = 0;
        int minLength = Integer.MAX_VALUE;

        /*напишите код, который будет построчно читать указанный файл:
            FileReader fileReader = new FileReader(path);
            BufferedReader reader =
               new BufferedReader(fileReader);
            String line;
            while ((line = reader.readLine()) != null) {
               int length = line.length();
            }*/
        try (FileReader fileReader = new FileReader(fileName);
             BufferedReader reader = new BufferedReader(fileReader)) {

            String line;
            while ((line = reader.readLine()) != null) {
                int length = line.length();
                totalLines++;

                //максимальная длина строки
                if (length > maxLength) {
                    maxLength = length;
                }

                //минимальная длина строки
                if (length < minLength) {
                    minLength = length;
                }

                //на привышение длины символов 1024
                if (length > 1024) {
                    throw new LineTooLongException(
                            "Обнаружена строка длиной " + length + " символов в строке " +
                                    totalLines + ". Максимально допустимая длина: 1024 символа(т.ч.к.)"
                    );
                }
            }

            //файл пусто, тогда устанавливаем minLength в 0
            if (totalLines == 0) {
                minLength = 0;
            }

            //Выведем результат анализа файла
            /*Допишите самостоятельно код
                таким образом, чтобы он по итогам выполнения программы выводил:

                общее количество строк в
                файле;
                длину самой длинной строки в файле;
                длину самой короткой строки в файле*/
            System.out.println("\nРезультаты анализа файла:");
            System.out.println("Общее количество строк: " + totalLines);
            System.out.println("Длина самой длинной строки: " + maxLength);
            System.out.println("Длина самой короткой строки: " + minLength);

        } catch (LineTooLongException e) {
            System.out.println("Ошибка: " + e.getMessage());
            System.out.println("Анализ файла прерван из-за слишком длинной строки. ");
            /*Поскольку в данном коде есть
                целых два места, требующих обязательной обработки исключений, поместите этот
                код внутрь конструкции try…catch, внутри которой пропишите обработку всех
                исключений:

                try {
                   // code here
                } catch (Exception ex) {
                   ex.printStackTrace();
                }*/
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}