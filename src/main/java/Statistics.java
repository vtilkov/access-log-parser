import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

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

    public Statistics() {
        this.totalTraffic = 0;
        this.minTime = null;
        this.maxTime = null;
        this.entryCount = 0;
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
}