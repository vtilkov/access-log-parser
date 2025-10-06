import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.stream.Collectors;

/* Создайте класс для расчётов
статистики — Statistics. У этого класса должен быть конструктор без параметров,
в котором должны инициализироваться переменные класса.

●     Добавьте в класс Statistics
метод addEntry, принимающий в качестве параметра объект класса LogEntry.

Реализуйте в классе
Statistics подсчёт среднего объёма трафика сайта за час. Для этого:

создайте у класса свойство
(поле) int
totalTraffic, в
которое в методе addEntry добавляйте объём данных,
отданных сервером;
создайте свойства (поля) minTime и maxTime класса LocalDateTime и заполняйте их в методе addEntry, если время в добавляемой записи из лога меньше minTime или больше maxTime соответственно;
реализуйте в классе метод getTrafficRate, в котором вычисляйте разницу между maxTime и minTime в часах и делите общий объём трафика на эту разницу.*/

class Statistics {
    private int totalTraffic;
    private LocalDateTime minTime;
    private LocalDateTime maxTime;
    private int entryCount;

    // поля для HashSet / HashMap
    private HashSet<String> existingPages;
    private HashMap<String, Integer> osCount;

    // новые поля для Задание #2 по теме "Collections"
    private HashSet<String> notFoundPages;
    private HashMap<String, Integer> browserCount;

    public Statistics() {
        this.totalTraffic = 0;
        this.minTime = null;
        this.maxTime = null;
        this.entryCount = 0;
        this.existingPages = new HashSet<>();
        this.osCount = new HashMap<>();
        this.notFoundPages = new HashSet<>();
        this.browserCount = new HashMap<>();
    }

    public void addEntry(LogEntry entry) {
        // Добавляем объем трафика
        this.totalTraffic += entry.getDataSize();
        this.entryCount++;

        // Обновляем минимальное и максимальное время
        LocalDateTime entryTime = entry.getTime();
        if (minTime == null || entryTime.isBefore(minTime)) {
            minTime = entryTime;
        }
        if (maxTime == null || entryTime.isAfter(maxTime)) {
            maxTime = entryTime;
        }

        // Для этого создайте в классе переменную класса HashSet<String>. В
        //эту переменную при выполнении метода addEntry добавляйте адреса существующих
        //страниц (с кодом ответа 200) сайта.
        if (entry.getResponseCode() == 200) {
            existingPages.add(entry.getPath());
        }

        // Для этого создайте в классе
        //переменную класса HashMap<String, Integer>, в которой подсчитывайте
        //частоту встречаемости каждой операционной системы.
        String os = entry.getUserAgent().getOsType();
        osCount.put(os, osCount.getOrDefault(os, 0) + 1);

        // Задание #2 по теме "Collections"
        if (entry.getResponseCode() == 404) { // страница не существует 404
            notFoundPages.add(entry.getPath());
        }

        String browser = entry.getUserAgent().getBrowserType();
        browserCount.put(browser, browserCount.getOrDefault(browser, 0) + 1); // подсчитаем браузеры
    }

    public double getTrafficRate() {
        if (minTime == null || maxTime == null || totalTraffic == 0) {
            return 0.0;
        }

        // Вычисляем разницу во времени в часах
        long hoursBetween = ChronoUnit.HOURS.between(minTime, maxTime);
        if (hoursBetween == 0) {
            return totalTraffic; // Если все записи в один час
        }

        // Возвращаем средний объем трафика в час
        return (double) totalTraffic / hoursBetween;
    }

    // Дополнительные геттеры для статистики
    public int getTotalTraffic() { return totalTraffic; }
    public LocalDateTime getMinTime() { return minTime; }
    public LocalDateTime getMaxTime() { return maxTime; }
    public int getEntryCount() { return entryCount; }

    // Возвращает список всех существующих страниц сайта (с кодом ответа 200)
    public HashSet<String> getExistingPages() {
        return existingPages;
    }

    // Метод в результате должен создавать новый HashMap<String, Double>
    // и в качестве ключей рассчитывать долю для каждой операционной системы (от 0 до 1).
    // Чтобы рассчитать долю конкретной операционной системы, нужно разделить количество
    // конкретной операционной системы на общее количество для всех операционных систем.
    public HashMap<String, Double> getOsStatistics() {
        HashMap<String, Double> osStatistics = new HashMap<>();

        // Вычисляем общее количество записей
        int totalEntries = getTotalOsCount();

        if (totalEntries > 0) {
            // Рассчитываем долю для каждой ОС
            for (Map.Entry<String, Integer> entry : osCount.entrySet()) {
                String os = entry.getKey();
                int count = entry.getValue();
                double share = (double) count / totalEntries;
                osStatistics.put(os, share);
            }
        }
        return osStatistics;
    }

    // Вспомогательный метод для получения общего количества записей ОС
    private int getTotalOsCount() {
        int total = 0;
        for (int count : osCount.values()) {
            total += count;
        }
        return total;
    }

    // НОВЫЕ МЕТОДЫ ДЛЯ ЗАДАНИЯ #2
    // Возвращает список всех несуществующих страниц сайта (с кодом ответа 404)
    public HashSet<String> getNotFoundPages() {
        return notFoundPages;
    }

    // Возвращает статистику браузеров в виде долей (от 0 до 1)
    public HashMap<String, Double> getBrowserStatistics() {
        HashMap<String, Double> browserStatistics = new HashMap<>();

        // Вычисляем общее количество записей браузеров
        int totalBrowsers = getTotalBrowserCount();

        if (totalBrowsers > 0) {
            // Рассчитываем долю для каждого браузера
            for (Map.Entry<String, Integer> entry : browserCount.entrySet()) {
                String browser = entry.getKey();
                int count = entry.getValue();
                double share = (double) count / totalBrowsers;
                browserStatistics.put(browser, share);
            }
        }

        return browserStatistics;
    }

    // Вспомогательный метод для получения общего количества браузеров
    private int getTotalBrowserCount() {
        int total = 0;
        for (int count : browserCount.values()) {
            total += count;
        }
        return total;
    }

    // Дополнительный метод для отладки - выводит сырые данные по браузерам
    public HashMap<String, Integer> getRawBrowserData() {
        return new HashMap<>(browserCount);
    }

    // Дополнительный метод: получает количество 404 ошибок
    public int getNotFoundPagesCount() {
        return notFoundPages.size();
    }

    // Дополнительный метод: получает самые популярные браузеры
    public java.util.List<String> getTopBrowsers(int limit) {
        return browserCount.entrySet().stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                .limit(limit)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }

    public HashMap<String, Integer> getRawOsData() {
        return new HashMap<>(osCount);
    }
}