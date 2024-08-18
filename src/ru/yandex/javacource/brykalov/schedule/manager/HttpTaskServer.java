package ru.yandex.javacource.brykalov.schedule.manager;

import com.google.gson.*;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import ru.yandex.javacource.brykalov.schedule.task.Epic;
import ru.yandex.javacource.brykalov.schedule.task.Subtask;
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
import java.time.LocalDateTime;
import java.util.List;

public class HttpTaskServer {
    private static final int PORT = 8080;
    private static final Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeTypeAdapter())
            .registerTypeAdapter(Duration.class, new DurationTypeAdapter())
            .create();
    private static TaskManager manager;
    private static HttpServer httpServer;

    public HttpTaskServer() {
        File file = new File("resources/file.csv");
        manager = Managers.getFileBackedTaskManager(file);
    }

    public HttpTaskServer(TaskManager taskManager) {
        manager = taskManager;
    }

    public static void main(String[] args) throws IOException {
        HttpTaskServer httpTaskServer = new HttpTaskServer();

        manager.deleteAllTasks();
        manager.deleteAllEpics();
        manager.deleteAllSubtasks();

        startHttpServer();
    }

    public static void startHttpServer() throws IOException {
        httpServer = HttpServer.create(new InetSocketAddress(PORT), 0);
        httpServer.createContext("/tasks", new tasksHandler());
        httpServer.createContext("/subtasks", new subtasksHandler());
        httpServer.createContext("/epics", new epicsHandler());
        httpServer.createContext("/history", new historyHandler());
        httpServer.createContext("/prioritized", new prioritizedHandler());
        httpServer.start();
        System.out.println("HTTP-сервер запущен на " + PORT + " порту.");
    }

    public static void stopHttpServer() throws IOException {
        httpServer.stop(0);
        System.out.println("HTTP-сервер на " + PORT + " порту остановлен.");
    }

    public Gson getGson() {
        return gson;
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
                        try {
                            Task task = manager.getTask(taskId);
                            response = gson.toJson(task);
                            rCode = 200;
                        } catch (NotFoundException e) {
                            response = "Not Found:" + e.getMessage();
                            rCode = 404;
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
                    try {
                        manager.updateTask(task);
                        response = "Done: Задача с id = " + taskId + " обновлена.";
                        rCode = 201;
                    } catch (NotFoundException notFoundException) {
                        if (taskId != 0) {
                            response = "Not Found: Обновление задачи не выполнено: " + notFoundException.getMessage();
                            rCode = 404;
                        } else {
                            // пытаемся добавить поступившую задачу как новую в менеджер
                            try {
                                int newTaskId = manager.addNewTask(new Task(task));
                                response = "Done: Новая задача с id = " + newTaskId + " добавлена.";
                                rCode = 201;
                            } catch (TaskValidationException validationException) {
                                response = "Not Acceptable: Добавление задачи не выполнено: " + validationException.getMessage();
                                rCode = 406;
                            }
                        }
                    } catch (TaskValidationException validationException) {
                        response = "Not Acceptable: Обновление задачи не выполнено: " + validationException.getMessage();
                        rCode = 406;
                    }
                    break;

                case "DELETE":
                    if (pathParts.length == 3) {
                        taskId = Integer.parseInt(pathParts[2]);
                        try {
                            manager.deleteTaskById(taskId);
                            response = "Done: Задача с id = " + taskId + " удалена.";
                            rCode = 201;
                        } catch (NotFoundException notFoundException) {
                            response = "Not Found: Удаление задачи не выполнено: " + notFoundException.getMessage();
                            rCode = 404;
                        }
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

    static class subtasksHandler implements HttpHandler {
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
            int subtaskId;

            switch (method) {
                case "GET":
                    if (pathParts.length == 3) {
                        subtaskId = Integer.parseInt(pathParts[2]);
                        try {
                            Subtask subtask = manager.getSubtask(subtaskId);
                            response = gson.toJson(subtask);
                            rCode = 200;
                        } catch (NotFoundException e) {
                            response = "Not Found:" + e.getMessage();
                            rCode = 404;
                        }
                    } else if (pathParts.length == 2) {
                        List<Subtask> subtasks = manager.getSubtaskList();
                        response = gson.toJson(subtasks);
                        rCode = 200;
                    } else {
                        response = "Bad request";
                        rCode = 400;
                    }
                    break;

                case "POST":
                    Subtask subtask = gson.fromJson(body, Subtask.class);
                    subtaskId = subtask.getId();
                    try {
                        manager.updateSubtask(subtask);
                        response = "Done: Подзадача с id = " + subtaskId + " обновлена.";
                        rCode = 201;
                    } catch (NotFoundException notFoundException) {
                        if (subtaskId != 0) {
                            response = "Not Found: Обновление подзадачи не выполнено: " + notFoundException.getMessage();
                            rCode = 404;
                        } else {
                            // пытаемся добавить поступившую подзадачу как новую в менеджер
                            try {
                                int newSubtaskId = manager.addNewSubtask(new Subtask(subtask));
                                response = "Done: Новая задача с id = " + newSubtaskId + " добавлена.";
                                rCode = 201;
                            } catch (TaskValidationException validationException) {
                                response = "Not Acceptable: Добавление подзадачи не выполнено: " + validationException.getMessage();
                                rCode = 406;
                            }
                        }
                    } catch (TaskValidationException validationException) {
                        response = "Not Acceptable: Обновление подзадачи не выполнено: " + validationException.getMessage();
                        rCode = 406;
                    }
                    break;

                case "DELETE":
                    if (pathParts.length == 3) {
                        subtaskId = Integer.parseInt(pathParts[2]);
                        try {
                            manager.deleteSubtaskById(subtaskId);
                            response = "Done: Подзадача с id = " + subtaskId + " удалена.";
                            rCode = 201;
                        } catch (NotFoundException notFoundException) {
                            response = "Not Found: Удаление подзадачи не выполнено: " + notFoundException.getMessage();
                            rCode = 404;
                        }
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
        }
    }

    static class epicsHandler implements HttpHandler {
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
            int epicId;

            switch (method) {
                case "GET":
                    if (pathParts.length == 4 && pathParts[3].equals("subtasks")) {
                        epicId = Integer.parseInt(pathParts[2]);
                        try {
                            Epic epic = manager.getEpic(epicId);
                            List<Integer> subtasks = epic.getSubtaskIds();
                            response = "Подзадачи эпика с id = " + epicId + ": " + subtasks;
                            rCode = 201;
                        } catch (NotFoundException e) {
                            response = "Not Found:" + e.getMessage();
                            rCode = 404;
                        }
                    } else if (pathParts.length == 3) {
                        epicId = Integer.parseInt(pathParts[2]);
                        try {
                            Epic epic = manager.getEpic(epicId);
                            response = gson.toJson(epic);
                            rCode = 200;
                        } catch (NotFoundException e) {
                            response = "Not Found:" + e.getMessage();
                            rCode = 404;
                        }
                    } else if (pathParts.length == 2) {
                        List<Epic> epics = manager.getEpicList();
                        response = gson.toJson(epics);
                        rCode = 200;
                    } else {
                        response = "Bad request";
                        rCode = 400;
                    }
                    break;

                case "POST":
                    Epic epic = gson.fromJson(body, Epic.class);
                    epicId = epic.getId();
                    try {
                        manager.updateEpic(epic);
                        response = "Done: Эпик с id = " + epicId + " обновлен.";
                        rCode = 201;
                    } catch (NotFoundException notFoundException) {
                        if (epicId != 0) {
                            response = "Not Found: Обновление подзадачи не выполнено: " + notFoundException.getMessage();
                            rCode = 404;
                        } else {
                            // пытаемся добавить поступивший эпик как новый в менеджер
                            int newEpicId = manager.addNewEpic(new Epic(epic));
                            response = "Done: Новый эпик с id = " + newEpicId + " добавлен.";
                            rCode = 201;
                        }
                    }
                    break;

                case "DELETE":
                    if (pathParts.length == 3) {
                        epicId = Integer.parseInt(pathParts[2]);
                        try {
                            manager.deleteEpicById(epicId);
                            response = "Done: Эпик с id = " + epicId + " удален.";
                            rCode = 201;
                        } catch (NotFoundException notFoundException) {
                            response = "Not Found: Удаление эпика не выполнено: " + notFoundException.getMessage();
                            rCode = 404;
                        }
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
        }
    }

    static class historyHandler implements HttpHandler {
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
                    List<Task> tasks = manager.getHistory();
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