import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static java.lang.String.format;

public class Main {
    public static void main(String[] args) throws IOException, ParseException {

        //Задание 1.

        //Анализатор курса валют.
        //Пользователь вводит дату.
        //Произвести анализ курса валют на эту дату, предыдущую и следующую:
        //- Вывести, все три  курса
        //- насколько курс вырос/упал.
        //- Наибольший и наименьшие значения из этих трех
        //- Сохранить в отдельную директорию лучший снимок NASA за эту дату :)


        Scanner scanner = new Scanner(System.in);
        System.out.print("Введите дату в формате дд.мм.гггг: ");
        String dateStr = scanner.nextLine();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        LocalDate date = LocalDate.parse(dateStr, formatter);

        String prevDateUrl = "https://www.cbr.ru/scripts/XML_dynamic.asp?date_req1=" + date.minusDays(1).format(formatter) +
                "&date_req2=" + date.format(formatter) +
                "&VAL_NM_RQ=R01235";
        String prevDateResult = downloadWebPage(prevDateUrl);


        String currentDateUrl = "https://www.cbr.ru/scripts/XML_dynamic.asp?date_req1=" + date.format(formatter) +
                "&date_req2=" + date.plusDays(1).format(formatter) +
                "&VAL_NM_RQ=R01235";
        String currentDateResult = downloadWebPage(currentDateUrl);

        String nextDateUrl = "https://www.cbr.ru/scripts/XML_dynamic.asp?date_req1=" + date.plusDays(1).format(formatter) +
                "&date_req2=" + date.plusDays(2).format(formatter) +
                "&VAL_NM_RQ=R01235";
        String nextDateResult = downloadWebPage(nextDateUrl);


        int wellOneStart = prevDateResult.indexOf("<Value>");
        int wellOneEnd = prevDateResult.indexOf("</Value>");
        double prevDateUsdRate = Double.parseDouble(prevDateResult.substring(wellOneStart + 7, wellOneEnd).replace(",", "."));


        wellOneStart = currentDateResult.indexOf("<Value>");
        wellOneEnd = currentDateResult.indexOf("</Value>");
        double currentDateUsdRate = Double.parseDouble(currentDateResult.substring(wellOneStart + 7, wellOneEnd).replace(",", "."));

        wellOneStart = nextDateResult.indexOf("<Value>");
        wellOneEnd = nextDateResult.indexOf("</Value>");
        double nextDateUsdRate = Double.parseDouble(nextDateResult.substring(wellOneStart + 7, wellOneEnd).replace(",", "."));





            System.out.println("Курс на " + date.minusDays(1).format(formatter) + ": " + prevDateUsdRate);
            System.out.println("Курс на " + date.format(formatter) + ": " + currentDateUsdRate);
            System.out.println("Курс на " + date.plusDays(1).format(formatter) + ": " + nextDateUsdRate);


            double currentToPrevChange = currentDateUsdRate - prevDateUsdRate;
            double nextToCurrentChange = nextDateUsdRate - currentDateUsdRate;

            if (currentToPrevChange > 0) {
                System.out.println("Курс  вырос на " + currentToPrevChange + ". (от предыдущей даты до выбранной)");
            } else if (currentToPrevChange < 0) {
                System.out.println("Курс упал на " + Math.abs(currentToPrevChange) + ". (от предыдущей даты до выбранной)");
            } else {
                System.out.println("Курс не изменился (от предыдущей даты до выбранной)");
            }

            if (nextToCurrentChange > 0) {
                System.out.println("Курс вырос на " + nextToCurrentChange + ". (от текущей даты до следующей)");
            } else if (nextToCurrentChange < 0) {
                System.out.println("Курс упал на " + Math.abs(nextToCurrentChange) + ". (от текущей даты до следующей)");
            } else {
                System.out.println("Курс не изменился (от текущей даты до следующей)");
            }
            double max = (double)Math.max(Math.max(prevDateUsdRate, currentDateUsdRate), nextDateUsdRate);
            System.out.println("Наибольшее значение: " + max);
            double min = (double) Math.min(Math.min(prevDateUsdRate, currentDateUsdRate), nextDateUsdRate);
            System.out.println("Наименьшее значение: " + min);


            SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
            Date dateNasa = sdf.parse(dateStr);

            Calendar calendar = Calendar.getInstance();
            calendar.setTime(dateNasa);

            int day = calendar.get(Calendar.DAY_OF_MONTH);
            int month = calendar.get(Calendar.MONTH) + 1;
            int year = calendar.get(Calendar.YEAR);

            String result = downloadWebPage("https://api.nasa.gov/planetary/apod?api_key=DEMO_KEY&date=" + year + "-" + month + "-" + day);
            int Start = result.lastIndexOf("url");
            int End = result.lastIndexOf("}");
            String resultApiNasa = result.substring(Start + 6, End - 1);

            try (InputStream in = new URL(resultApiNasa).openStream()) {
                Files.copy(in, Paths.get(day + "new.jpg"));
            }
            System.out.println("Снимок сохранён..");
        }

        private static String downloadWebPage (String url) throws IOException {

            StringBuilder result = new StringBuilder();
            String line;

            URLConnection urlConnection = new URL(url).openConnection();
            urlConnection.addRequestProperty("User-Agent", "Mozilla");
            urlConnection.setReadTimeout(5000);
            urlConnection.setConnectTimeout(5000);

            try (InputStream is = urlConnection.getInputStream(); BufferedReader br = new BufferedReader(new InputStreamReader(is))) {

                while ((line = br.readLine()) != null) {
                    result.append(line);
                }

                return result.toString();
            }
        }
    }

    class BollaMain {
        public static void main(String[] args) throws IOException, ParseException {

            //Задание 2. Пользователь вводит месяц и год. Вывести курс рубля за этот месяц, найти наибольший и наименьшие значения.


            Scanner scanner = new Scanner(System.in);
            System.out.println("Введите пожалуйста месяц в формате: ....MM...");
            System.out.print("Ввод: ");
            String month = scanner.nextLine();
            System.out.println("Введите пожалуйста год в формате: ....гггг");
            System.out.print("Ввод: ");
            String year = scanner.nextLine();
            System.out.println();
            System.out.println("   Дата      Цена");

            String[] prices = new String[31];


            for (int day = 1; day < 31; day++) {
                String dayStr = "";
                if (day < 10) {
                    dayStr = "0" + day;
                } else {
                    dayStr = String.valueOf(day);
                }

                String url = downloadWebPage("https://cbr.ru/scripts/XML_dynamic.asp?date_req1=" + dayStr + "." + month + "." + year + "&date_req2=" + dayStr + "." + month + "." + year + "&VAL_NM_RQ=R01235");
                int indexOfStart = url.indexOf("><Value>");
                if (indexOfStart != -1) {
                    int indexOfEnd = url.indexOf("</Value></Re");
                    String result = url.substring(indexOfStart + 8, indexOfEnd);
                    System.out.println(dayStr + "." + month + "." + year + ": " + result);
                    prices[day - 1] = format(result);
                }
            }
            String[] validPrices = Arrays.stream(prices)
                    .filter(price -> price != null && !price.isEmpty())
                    .toArray(String[]::new);


            String minPrice = Arrays.stream(validPrices)
                    .min(Comparator.naturalOrder())
                    .orElse("Нет данных");


            String maxPrice = Arrays.stream(validPrices)
                    .max(Comparator.naturalOrder())
                    .orElse("Нет данных");

            System.out.println();
            System.out.println("Наименьшее значение: " + minPrice);
            System.out.println("Наибольшее значение: " + maxPrice);


        }


        private static String downloadWebPage(String url) throws IOException {
            StringBuilder result = new StringBuilder();
            String line;

            URLConnection urlConnection = new URL(url).openConnection();
            urlConnection.addRequestProperty("User-Agent", "Mozilla");
            urlConnection.setReadTimeout(5000);
            urlConnection.setConnectTimeout(5000);

            try (InputStream is = urlConnection.getInputStream(); BufferedReader br = new BufferedReader(new InputStreamReader(is))) {
                while ((line = br.readLine()) != null) {
                    result.append(line);
                }
                return result.toString();
            }
        }
    }









