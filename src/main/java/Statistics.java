import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
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

    //новые поля по теме StreamApi задание 1
    private HashSet<String> uniqueUserIPs;
    private int userVisitsCount;
    private int errorRequestsCount;

    //новые поля по теме StreamApi задание 2
    private HashMap<Long, Integer> visitsPerSecond; // для пиковой посещаемости
    private HashSet<String> referringDomains; // для списка ссылающихся сайтов
    private HashMap<String, Integer> userVisitCounts; // для подсчета посещений по пользователям

    public Statistics() {
        this.totalTraffic = 0;
        this.minTime = null;
        this.maxTime = null;
        this.entryCount = 0;
        this.existingPages = new HashSet<>();
        this.osCount = new HashMap<>();
        this.notFoundPages = new HashSet<>();
        this.browserCount = new HashMap<>();

        //инициализирем поля по теме StreamApi задание 1
        this.uniqueUserIPs = new HashSet<>();
        this.userVisitsCount = 0;
        this.errorRequestsCount = 0;

        //инициализирем поля по теме StreamApi задание 2
        this.visitsPerSecond = new HashMap<>();
        this.referringDomains = new HashSet<>();
        this.userVisitCounts = new HashMap<>();
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

        // StreamApi
        boolean isBot = isBot(entry.getUserAgent()); //проверить пользователь бот ?
        // если нет, то зачем его в статистику реальных пользователей
        if (!isBot) {userVisitsCount++;
            uniqueUserIPs.add(entry.getIpAddress());}

        //логика для задания 2 StreamApi
        // Подсчет посещений по секундам для пиковой посещаемости
        long secondTimestamp = entryTime.toEpochSecond(java.time.ZoneOffset.UTC);
        visitsPerSecond.put(secondTimestamp, visitsPerSecond.getOrDefault(secondTimestamp, 0) + 1);

        // Сбор доменов из referer
        if (entry.getReferer() != null && !entry.getReferer().isEmpty() &&
                !entry.getReferer().equals("-")) {
            String domain = extractDomain(entry.getReferer());
            if (domain != null && !domain.isEmpty()) {
                referringDomains.add(domain);
            }
        }

        // Подсчет посещений по пользователям (ip)
        userVisitCounts.put(entry.getIpAddress(),
                userVisitCounts.getOrDefault(entry.getIpAddress(), 0) + 1);

        // Подсчет ошибочных запросов (400/500)
        if (isErrorResponse(entry.getResponseCode())){
            errorRequestsCount++;
        }
    }

    private boolean isBot(UserAgent userAgent){
        String browser = userAgent.getBrowserType().toLowerCase();
        return browser.contains("bot") ||
                browser.equals("googlebot") ||
                browser.equals("yandexbot") ||
                browser.equals("bingbot");
    }

    //определим ошибочный ответ
    private boolean isErrorResponse(int responseCode) {
        return (responseCode >= 400 && responseCode < 600);
    }

    // Метод для извлечения домена из referer
    // Метод, возвращающий список
    // сайтов, со страниц которых есть ссылки на текущий сайт.
    // Для получения данного списка собирайте домены для всех referer-ов в HashSet<String>.
    // Причём, важно собирать именно адреса доменов. К примеру, для referer: https://nova-news.ru/wp-login.php,
    // доменное имя будет: nova-news.ru
    private String extractDomain(String referer) {
        try {
            // Убираем протокол
            String domain = referer.replaceFirst("^(https?://)?(www\\.)?", "");

            // Убираем путь после домена
            int slashIndex = domain.indexOf('/');
            if (slashIndex > 0) {
                domain = domain.substring(0, slashIndex);
            }

            // Убираем порт если есть
            int colonIndex = domain.indexOf(':');
            if (colonIndex > 0) {
                domain = domain.substring(0, colonIndex);
            }

            return domain.trim();
        } catch (Exception e) {
            System.out.println("Ошибка при извлечении домена из: " + referer);
            return "";
        }
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

    //StreamApi
    //подсчет среднего количества почещений за час (реальные пользователи)
    public double getAverageVisitsPerHour() {
        if (minTime == null || maxTime == null || userVisitsCount == 0) {
            return 0.0;
        }

        long hoursBetween = ChronoUnit.HOURS.between(minTime,maxTime);
        if (hoursBetween == 0 ) {
            return userVisitsCount;
        }

        return (double) userVisitsCount/hoursBetween;
    }

    //подсчет среднего количества ошибочных запросов в час
    public double getAverageErrorRequestsPerHour() {
        if (minTime == null || maxTime == null || errorRequestsCount == 0) {
            return 0.0;
        }

        long hoursBetween = ChronoUnit.HOURS.between(minTime, maxTime);
        if (hoursBetween == 0) {
            return errorRequestsCount;
        }

        return (double) errorRequestsCount / hoursBetween;
    }

    //средняя посещаемость одним пользователем
    public double getAverageVisitsPerUser() {
        if (uniqueUserIPs.isEmpty() || userVisitsCount == 0) {
            return 0.0;
        }

        return (double) userVisitsCount / uniqueUserIPs.size();
    }

    // Методы для задания 2 StreamApi
    // 1. Метод расчёта пиковой посещаемости сайта (в секунду)
    // Возвращает максимальное количество посещений за одну секунду
    public int getPeakVisitsPerSecond() {
        if (visitsPerSecond.isEmpty()) {
            return 0;
        }

        // Используем Stream API для нахождения максимального значения
        return visitsPerSecond.values().stream()
                .mapToInt(Integer::intValue)
                .max()
                .orElse(0);
    }

    //1.1 Дополнительный метод: получение детальной информации о пиковой секунде
    public Map.Entry<Long, Integer> getPeakSecondInfo() {
        if (visitsPerSecond.isEmpty()) {
            return null;
        }

        return visitsPerSecond.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .orElse(null);
    }

    //2. Метод, возвращающий список сайтов, со страниц которых есть ссылки на текущий сайт
    // Возвращает отсортированный список доменов
    public List<String> getReferringDomains() {
        return referringDomains.stream()
                .sorted()
                .collect(Collectors.toList());
    }

    //2.1 Дополнительный метод: получение количества ссылающихся доменов
    public int getReferringDomainsCount() {
        return referringDomains.size();
    }

    //Метод расчёта максимальной посещаемости одним пользователем
    // Возвращает максимальное количество посещений для одного пользователя (IP)
    public int getMaxVisitsBySingleUser() {
        if (userVisitCounts.isEmpty()) {
            return 0;
        }

        // Используем Stream API для нахождения максимального значения
        return userVisitCounts.values().stream()
                .mapToInt(Integer::intValue)
                .max()
                .orElse(0);
    }

    //3.1 Дополнительный метод: получение информации о самом активном пользователе
    public Map.Entry<String, Integer> getMostActiveUserInfo() {
        if (userVisitCounts.isEmpty()) {
            return null;
        }

        return userVisitCounts.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .orElse(null);
    }

    //3.2 Дополнительный метод: топ N самых активных пользователей
    public List<Map.Entry<String, Integer>> getTopActiveUsers(int limit) {
        return userVisitCounts.entrySet().stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                .limit(limit)
                .collect(Collectors.toList());
    }

    //доп метод получения статистики с использованием StreamApi
    public void printStreamAPIStatistics() {
        System.out.println("\n --СТАТИСТИКА (StreamApi) Задание #1--");

        System.out.printf("Среднее количество посещений в час (люди): %.2f\n",
                getAverageVisitsPerHour());
        System.out.printf("Среднее количество ошибочных запросов в час: %.2f\n",
                getAverageErrorRequestsPerHour());
        System.out.printf("Средняя посещаемость одним пользователем: %.2f\n",
                getAverageVisitsPerUser());

        // Методы для задания 2 StreamApi
        // Вывод статистики для задания #2
        printStreamAPIStatisticsPart2();
    }

    //Метод для вывода статистики задания #2 Stream API
    public void printStreamAPIStatisticsPart2() {
        System.out.println("\n --СТАТИСТИКА (StreamApi) Задание #2--");

        // 1. Пиковая посещаемость
        int peakVisits = getPeakVisitsPerSecond();
        Map.Entry<Long, Integer> peakSecondInfo = getPeakSecondInfo();

        System.out.println("Пиковая посещаемость: " + peakVisits + " запросов/секунду");
        if (peakSecondInfo != null) {
            LocalDateTime peakTime = LocalDateTime.ofEpochSecond(peakSecondInfo.getKey(), 0, java.time.ZoneOffset.UTC);
            System.out.println("  Пиковая секунда: " + peakTime + " (" + peakSecondInfo.getValue() + " запросов)");
        }

        // 2. Ссылающиеся домены (возвращающий список сайтов, со страниц которых есть ссылки на текущий сайт)
        List<String> domains = getReferringDomains();
        System.out.println("Количество ссылающихся доменов: " + domains.size());
        if (!domains.isEmpty()) {
            System.out.println("Ссылающиеся домены (первые 10):");
            domains.stream().limit(10).forEach(domain -> System.out.println("  - " + domain));
        }

        // 3. Максимальная посещаемость пользователем
        int maxUserVisits = getMaxVisitsBySingleUser();
        Map.Entry<String, Integer> mostActiveUser = getMostActiveUserInfo();

        System.out.println("Максимальная посещаемость одним пользователем: " + maxUserVisits + " запросов");
        if (mostActiveUser != null) {
            System.out.println("  Самый активный пользователь: IP " + mostActiveUser.getKey() +
                    " (" + mostActiveUser.getValue() + " запросов)");
        }
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