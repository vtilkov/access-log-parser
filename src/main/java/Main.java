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
    //переменные для подсчета ботов
    private static int googlebotCount = 0;
    private static int yandexbotCount = 0;

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

        Statistics statistics = new Statistics();

        /*int maxLength = 0;
        int minLength = Integer.MAX_VALUE;*/

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

                //проверим на превышение длины символов 1024
                if (length > 1024) {
                    throw new LineTooLongException(
                            "Обнаружена строка длиной " + length + " символов в строке " +
                                    totalLines + ". Максимально допустимая длина: 1024 символа"
                    );
                }

                //создадим объект LogEntry и добавим в статистику
                try {
                    LogEntry entry = new LogEntry(line);
                    statistics.addEntry(entry);
                    processUserAgent(entry.getUserAgent());

                    // Для отладки - выводим первые несколько записей
                    if (totalLines <= 5) {
                        System.out.println("Обработана строка " + totalLines + ": " + entry);
                    }

                } catch (Exception e) {
                    // Пропускаем некорректные строки
                    System.out.println("Ошибка при обработке строки " + totalLines + ": " + e.getMessage());
                    System.out.println("Проблемная строка: " + line);
                }
            }

            //вычислим доли запросов от ботов
            double googlebotShare = statistics.getEntryCount() > 0 ?
                    (double) googlebotCount / statistics.getEntryCount() * 100 : 0;
            double yandexbotShare = statistics.getEntryCount() > 0 ?
                    (double) yandexbotCount / statistics.getEntryCount() * 100 : 0;

            //Выведем результат анализа файла
            /*Допишите самостоятельно код
                таким образом, чтобы он по итогам выполнения программы выводил:

                общее количество строк в
                файле;
                длину самой длинной строки в файле;
                длину самой короткой строки в файле*/
            System.out.println("\nРезультаты анализа файла:");
            System.out.println("Общее количество строк: " + totalLines);
            System.out.println("Успешно обработано записей: " + statistics.getEntryCount());
            System.out.println("Количество запросов от Googlebot: " + googlebotCount);
            System.out.println("Количество запросов от YandexBot: " + yandexbotCount);
            System.out.printf("Доля запросов от Googlebot: %.2f%%\n", googlebotShare);
            System.out.printf("Доля запросов от YandexBot: %.2f%%\n", yandexbotShare);

            //выыедем cтатистику трафика
            System.out.printf("Средний объем трафика за час: %.2f байт/час\n", statistics.getTrafficRate());
            System.out.println("Общий объем трафика: " + statistics.getTotalTraffic() + " байт");
            System.out.println("Период анализа: с " + statistics.getMinTime() + " по " + statistics.getMaxTime());


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

        /*Напишите код, который будет
            разделять каждую строку на составляющие. Описание составляющих находится во введении ко всем
            заданиям (раздел “Файл состоит из строк следующего вида”).
            Мда.. самое вкусное ))*/
        private static void processUserAgent(UserAgent userAgent){

            String browser = userAgent.getBrowserType();
            /*try {
                //разделим строку на составляющие по кавычкам
                Формат:
                    В этих строках содержатся следующие компоненты:
                    IP-адрес клиента, который сделал запрос к серверу (в примере выше — 37.231.123.209).
                    Два пропущенных свойства, на месте которых обычно стоят дефисы, но могут встречаться также и пустые строки ("").
                    Дата и время запроса в квадратных скобках.
                    Метод запроса (в примере выше — GET) и путь, по которому сделан запрос.
                    Код HTTP-ответа (в примере выше — 200).
                    Размер отданных данных в байтах (в примере выше — 61096).
                    Путь к странице, с которой перешли на текущую страницу, — referer (в примере выше — “https://nova-news.ru/search/?rss=1&lg=1”).
                    User-Agent — информация о браузере или другом клиенте, который выполнил запрос.

                String[] parts = logLine.split("\"");

                //User-Agent находится в последней части после разделения по кавычкам
                if (parts.length < 6) {
                    return; //принудительно вышли, неправильный формат строки
                }

                String userAgent = parts[5]; //Индекс 5 соответствует User-Agent

                //Ищем первые скобки в User-Agent
                int bracketsStart = userAgent.indexOf('(');
                int bracketsEnd = userAgent.indexOf(')', bracketsStart);
                if (bracketsStart == -1 || bracketsEnd == -1) {
                    return;
                }

                //Выделяем часть, которая находится в первых скобках
                String firstBrackets = userAgent.substring(bracketsStart + 1, bracketsEnd);

                //Разделяем эту часть по точке с запятой
                String[] bracketParts = firstBrackets.split(";");
                if (bracketParts.length >= 2) {
                    //Берем второй фрагмент и очищаем от пробелов
                    String fragment = bracketParts[1].trim();

                    //Отделяем часть до слэша
                    int slashIndex = fragment.indexOf('/');
                    String programName = (slashIndex != -1) ?
                            fragment.substring(0, slashIndex).trim() : fragment.trim();

                    //Сравниваем с названиями ботов используя equals()*/
                    if ("Googlebot".equals(browser)) {
                        googlebotCount++;
                    } else if ("YandexBot".equals(browser)) {
                        yandexbotCount++;
                    }/*
                }
            } catch (Exception e) {
                //игнорим ошибки парсинга для отдельных строк
            }*/
    }
}