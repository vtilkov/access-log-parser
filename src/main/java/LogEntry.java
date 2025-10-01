import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.Locale;

/*В этом задании вам
необходимо разработать класс LogEntry, объекты которого будут соответствовать
строкам из лог-файла, а свойства (поля) — отдельным частям каждой такой строки.

●     Создайте класс LogEntry со
свойствами (полями), соответствующими компонентам строк лог-файла: IP-адресу,
дате и времени запроса, методу запроса, пути запроса, коду ответа, размеру
отданных сервером данных, referer, а также User-Agent. Возможные методы
HTTP-запросов положите в enum. Типы остальных полей определите самостоятельно.

●     Для всех созданных в классе
LogEntry свойств (полей) создайте геттеры, а сами свойства (поля) пометьте
ключевым словом final.

●     Создайте в классе LogEntry
конструктор, который будет принимать в качестве единственного параметра строку,
разбирать её на составляющие и устанавливать значения всех свойств (полей)
класса.*/

// Класс LogEntry для представления строки лога
class LogEntry {
    private final String ipAddress;
    private final LocalDateTime time;
    private final HttpMethod method;
    private final String path;
    private final int responseCode;
    private final int dataSize;
    private final String referer;
    private final UserAgent userAgent;

    public LogEntry(String logLine) {
        try {
            System.out.println("Парсим строку: " + logLine); //покажем наглядно !!

            //извлекаем IP
            this.ipAddress = logLine.substring(0, logLine.indexOf(' ')).trim();
            System.out.println("IP: " + ipAddress);

            //начало и конец времени
            int timeStart = logLine.indexOf('[');
            int timeEnd = logLine.indexOf(']', timeStart);
            if (timeStart == -1 || timeEnd == -1) {
                throw new IllegalArgumentException("Неверный формат времени");
            }

            //извлечем время
            String timeString = logLine.substring(timeStart + 1, timeEnd);
            System.out.println("Время строка: " + timeString);

            //создаем форматтер с английской локалью для месяцев
            // тут я разберался ОЧЕНЬ долго !!!!!!!!!!!!!!!!!!!!!!!!
            DateTimeFormatter formatter = new DateTimeFormatterBuilder()
                    .parseCaseInsensitive()
                    .appendPattern("dd/MMM/yyyy:HH:mm:ss Z")
                    .toFormatter(Locale.ENGLISH);

            this.time = LocalDateTime.parse(timeString, formatter);
            System.out.println("Время parsed: " + time);

            //найдем кавычки для запроса
            int firstQuote = logLine.indexOf('"', timeEnd);
            int secondQuote = logLine.indexOf('"', firstQuote + 1);
            if (firstQuote == -1 || secondQuote == -1) {
                throw new IllegalArgumentException("Invalid request format");
            }

            //извлечем метод и путь из запроса
            String request = logLine.substring(firstQuote + 1, secondQuote);
            System.out.println("Запрос: " + request);

            String[] requestParts = request.split(" ");
            if (requestParts.length >= 2) {
                this.method = parseHttpMethod(requestParts[0]);
                this.path = requestParts[1];
            } else {
                this.method = HttpMethod.UNKNOWN;
                this.path = "";
            }
            System.out.println("Метод: " + method + ", Путь: " + path);

            //извлечем код ответа и размер данных
            String afterRequest = logLine.substring(secondQuote + 1).trim();
            System.out.println("После запроса: " + afterRequest);

            String[] responseParts = afterRequest.split("\\s+");
            if (responseParts.length >= 2) {
                this.responseCode = Integer.parseInt(responseParts[0]);
                this.dataSize = Integer.parseInt(responseParts[1]);
            } else {
                this.responseCode = 0;
                this.dataSize = 0;
            }
            System.out.println("Код: " + responseCode + ", Размер: " + dataSize);

            //извлекаем referer и user-agent
            String[] remainingParts = logLine.split("\"");
            System.out.println("Всего частей после split: " + remainingParts.length);

            if (remainingParts.length >= 6) {
                this.referer = remainingParts[3].equals("-") ? "" : remainingParts[3].trim();
                String userAgentString = remainingParts[5].equals("-") ? "" : remainingParts[5].trim();
                this.userAgent = new UserAgent(userAgentString);
                System.out.println("Referer: '" + referer + "', UserAgent: '" + userAgentString + "'");
            } else if (remainingParts.length >= 4) {
                this.referer = remainingParts[3].equals("-") ? "" : remainingParts[3].trim();
                this.userAgent = new UserAgent("");
                System.out.println("Referer: '" + referer + "', UserAgent: ''");
            } else {
                this.referer = "";
                this.userAgent = new UserAgent("");
                System.out.println("Referer: '', UserAgent: ''");
            }

            System.out.println("Успешно создан LogEntry\n");

        } catch (Exception e) {
            System.out.println("ОШИБКА при парсинге: " + e.getMessage());
            e.printStackTrace();
            throw new IllegalArgumentException("Invalid log line format: " + logLine, e);
        }
    }

    private HttpMethod parseHttpMethod(String method) {
        try {
            return HttpMethod.valueOf(method.toUpperCase());
        } catch (IllegalArgumentException e) {
            return HttpMethod.UNKNOWN;
        }
    }

    //геттеры для всех полей
    public String getIpAddress() { return ipAddress; }
    public LocalDateTime getTime() { return time; }
    public HttpMethod getMethod() { return method; }
    public String getPath() { return path; }
    public int getResponseCode() { return responseCode; }
    public int getDataSize() { return dataSize; }
    public String getReferer() { return referer; }
    public UserAgent getUserAgent() { return userAgent; }

    @Override
    public String toString() {
        return String.format("LogEntry{ip='%s', time=%s, method=%s, path='%s', response=%d, size=%d}",
                ipAddress, time, method, path, responseCode, dataSize);
    }
}