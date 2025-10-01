/*Создайте класс UserAgent по
тому же принципу: с final-свойствами (полями), соответствующими свойствам,
заданным в строке User-Agent (см. ниже), и геттерами для этих свойств.

Создайте также в классе
UserAgent конструктор, который будет принимать в качестве параметра строку
User-Agent и извлекать из неё свойства. Из строки User-Agent необходимо
извлекать два свойства: тип операционной системы (Windows, macOS или Linux) и
браузера (Edge, Firefox, Chrome, Opera или другой). Для определения типа
операционной системы и браузера воспользуйтесь инструкцией.*/

enum HttpMethod {
    GET, POST, PUT, DELETE, HEAD, OPTIONS, PATCH, CONNECT, TRACE, UNKNOWN
}

class UserAgent {
    private final String osType;
    private final String browserType;

    public UserAgent(String userAgentString) {
        this.osType = parseOperatingSystem(userAgentString);
        this.browserType = parseBrowser(userAgentString);
    }

    public String getOsType() {
        return osType;
    }

    public String getBrowserType() {
        return browserType;
    }

    private String parseOperatingSystem(String userAgent) {
        if (userAgent == null || userAgent.equals("-") || userAgent.isEmpty()) {
            return "Unknown";
        }

        userAgent = userAgent.toLowerCase();

        if (userAgent.contains("windows")) return "Windows";
        if (userAgent.contains("mac os") || userAgent.contains("macos")) return "macOS";
        if (userAgent.contains("linux")) return "Linux";
        if (userAgent.contains("android")) return "Android";
        if (userAgent.contains("ios") || userAgent.contains("iphone")) return "iOS";
        if (userAgent.contains("x11") || userAgent.contains("unix")) return "Unix";

        return "Other";
    }

    private String parseBrowser(String userAgent) {
        if (userAgent == null || userAgent.equals("-") || userAgent.isEmpty()) {
            return "Unknown";
        }

        userAgent = userAgent.toLowerCase();

        // Проверка ботов
        if (userAgent.contains("googlebot")) return "Googlebot";
        if (userAgent.contains("yandexbot")) return "YandexBot";
        if (userAgent.contains("bingbot")) return "BingBot";

        // Проверка браузеров
        if (userAgent.contains("edg/") || userAgent.contains("edge")) return "Edge";
        if (userAgent.contains("firefox")) return "Firefox";
        if (userAgent.contains("chrome") && !userAgent.contains("edg/")) return "Chrome";
        if (userAgent.contains("safari") && !userAgent.contains("chrome")) return "Safari";
        if (userAgent.contains("opera")) return "Opera";
        if (userAgent.contains("msie") || userAgent.contains("trident")) return "Internet Explorer";

        return "Other";
    }
}