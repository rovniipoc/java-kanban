package ru.yandex.javacource.brykalov.schedule.manager;

import com.google.gson.*;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import ru.yandex.javacource.brykalov.schedule.task.Status;
import ru.yandex.javacource.brykalov.schedule.task.Task;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Type;
import java.net.InetSocketAddress;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class HttpTaskServer {
    private static final int PORT = 8080;
    private static final File file = new File("resources/file.csv");
    private static final TaskManager manager = Managers.getFileBackedTaskManager(file);
    private static final Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeTypeAdapter())
            .registerTypeAdapter(Duration.class, new DurationTypeAdapter())
            .create();

    public static void main(String[] args) throws IOException {
        HttpServer httpServer = HttpServer.create(new InetSocketAddress(PORT), 0);
        httpServer.createContext("/tasks", new tasksHandler());
//        httpServer.createContext("/subtasks", new subtasksHandler());
//        httpServer.createContext("/epics", new epicsHandler());
//        httpServer.createContext("/history", new historyHandler());
        httpServer.createContext("/prioritized", new prioritizedHandler());

        manager.deleteAllTasks();
        manager.deleteAllEpics();
        manager.deleteAllSubtasks();

        httpServer.start();
        System.out.println("HTTP-сервер запущен на " + PORT + " порту.");
    }

    public static class LocalDateTimeTypeAdapter implements JsonSerializer<LocalDateTime>, JsonDeserializer<LocalDateTime> {
        @Override
        public LocalDateTime deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            return LocalDateTime.parse(json.getAsString());
        }

        @Override
        public JsonElement serialize(LocalDateTime localDateTime, Type type, JsonSerializationContext context) {
            return new JsonPrimitive(localDateTime.toString());
        }
    }

    public static class DurationTypeAdapter implements JsonSerializer<Duration>, JsonDeserializer<Duration> {
        @Override
        public Duration deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            return Duration.parse(json.getAsString());
        }

        @Override
        public JsonElement serialize(Duration duration, Type type, JsonSerializationContext context) {
            return new JsonPrimitive(duration.toString());
        }
    }

    static class tasksHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String method = exchange.getRequestMethod();
            URI requestURI = exchange.getRequestURI();
            String path = requestURI.getPath();
            String[] pathParts = path.split("/");
            InputStream inputStream = exchange.getRequestBody();
            String body = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
            String response = "";
            int rCode = 0;
            int taskId;

            switch (method) {
                case "GET":
                    if (pathParts.length == 3) {
                        taskId = Integer.parseInt(pathParts[2]);
                        Task task = manager.getTask(taskId);
                        if (task == null) {
                            response = "Not Found";
                            rCode = 404;
                        } else {
                            response = gson.toJson(task);
                            rCode = 200;
                        }
                    } else if (pathParts.length == 2) {
                        List<Task> tasks = manager.getTaskList();
                        response = gson.toJson(tasks);
                        rCode = 200;
                    } else {
                        response = "Bad request";
                        rCode = 400;
                    }
                    break;

                case "POST":
                    Task task = gson.fromJson(body, Task.class);
                    taskId = task.getId();
                    if (manager.getTask(taskId) != null) {
                        // если у задачи есть id и она есть в менеджере
                        manager.updateTask(task);
                        rCode = 201;
                    } else {
                        // если у задачи нет id или ее нет в хранилище
                        if (task.getDuration() != null) {
                            // у входящей задачи есть временной интервал
                            // пытаемся добавить поступившую задачу как новую в менеджер
                            int newTaskId = manager.addNewTask(new Task(task.getName(), task.getDescription(),
                                    task.getStatus(), task.getStartTime(), task.getDuration()));
                            if (newTaskId == -1) {
                                // .addNewTask() вернул ошибку из-за пересечения по времени с другой задачей
                                response = "Not Acceptable";
                                rCode = 406;
                            } else {
                                // добавление прошло успешно
                                rCode = 201;
                            }
                        } else {
                            // у входящей задачи нет временного интервала,
                            // добавляем поступившую задачу как новую в менеджер
                            manager.addNewTask(new Task(task.getName(), task.getDescription(), task.getStatus(),
                                    task.getStartTime(), task.getDuration()));
                            rCode = 201;
                        }
                    }
                    break;

                case "DELETE":
                    if (pathParts.length == 3) {
                        taskId = Integer.parseInt(pathParts[2]);
                        manager.deleteTaskById(taskId);
                        rCode = 201;

                    } else {
                        response = "Bad request";
                        rCode = 400;
                    }
                    break;

                default:
                    response = "Bad request";
                    rCode = 400;
            }

            try (OutputStream os = exchange.getResponseBody()) {
                exchange.sendResponseHeaders(rCode, 0);
                os.write(response.getBytes(StandardCharsets.UTF_8));
            }
            exchange.close();

            //TODO написать метод writeResponse()
        }
    }


    class subtasksHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {

        }
    }

    class epicsHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {

        }
    }

    class historyHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {

        }
    }

    static class prioritizedHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String method = exchange.getRequestMethod();
            URI requestURI = exchange.getRequestURI();
            String path = requestURI.getPath();
            String[] pathParts = path.split("/");
            InputStream inputStream = exchange.getRequestBody();
            String body = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
            String response = "";
            int rCode = 0;

            switch (method) {
                case "GET":
                    List<Task> tasks = manager.getPrioritizedTasks();
                    response = gson.toJson(tasks);
                    rCode = 200;
                    break;

                default:
                    response = "Bad request";
                    rCode = 400;
            }

            try (OutputStream os = exchange.getResponseBody()) {
                exchange.sendResponseHeaders(rCode, 0);
                os.write(response.getBytes(StandardCharsets.UTF_8));
            }
            exchange.close();
        }
    }
}