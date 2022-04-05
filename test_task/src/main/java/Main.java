import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Main {

    public static final String PATH = "src/main/resources/tickets.json";

    public static void main(String[] args) {
        int countTickets = 0;
        long sumDuration = 0;
        List<Double> durationList = new ArrayList<>();
        try {
            String jsonString = readFile(PATH);
            JSONParser parser = new JSONParser();
            JSONObject rootObj = (JSONObject) parser.parse(jsonString);
            JSONArray ticketsArr = (JSONArray) rootObj.get("tickets");
            for (Object ticket : ticketsArr) {
                JSONObject ticketObj = (JSONObject) ticket;
                if ((ticketObj.get("origin").equals("VVO")) && (ticketObj.get("destination").equals("TLV"))) {
                    countTickets++;
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yy H:mm");
                    LocalDateTime dateDeparture = LocalDateTime.parse(ticketObj.get("departure_date") +
                            " " + ticketObj.get("departure_time"), formatter);
                    LocalDateTime dateArrival = LocalDateTime.parse(ticketObj.get("arrival_date") +
                            " " + ticketObj.get("arrival_time"), formatter);
                    long duration = ChronoUnit.MINUTES.between(dateDeparture, dateArrival);
                    durationList.add((double) duration);
                    sumDuration += duration;

                }
            }
            double avgTime = (double) sumDuration / countTickets;
            System.out.println("Среднее время полета между Владивостоком и Тель-Авивом = " + avgTime + " минут");

            System.out.println("90-й процентиль равен " + percentile(durationList, 90) + " минут");


        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }
    }

    public static String readFile(String path) throws IOException {
        // Здесь удаляю также символ BOM, т.к. он нарушает синтаксис json
        char bom = 65279;
        StringBuilder stringBuilder = new StringBuilder();
        Files.readAllLines(Paths.get(path)).forEach(line -> stringBuilder.append(line + System.lineSeparator()));
        return stringBuilder.toString().replaceAll(String.valueOf(bom), "");
    }

    public static double percentile(List<Double> values, double percentile) {
        Collections.sort(values);
        int index = (int) Math.ceil(percentile / 100.0 * values.size());
        return values.get(index - 1);
    }
}
