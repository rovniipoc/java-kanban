package ru.yandex.javacource.brykalov.schedule.manager;

import ru.yandex.javacource.brykalov.schedule.task.*;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

public class FileBackedTaskManager extends InMemoryTaskManager {

    private static final String HEADERS = "id,type,name,status,description,startTime,duration,epic";
    private final File file;

    public FileBackedTaskManager(File file) {
        this.file = file;

        if (!file.exists()) {
            try {
                Files.createFile(Path.of(file.toURI()));
            } catch (IOException e) {
                throw new ManagerSaveException("Ошибка создания файла: " + file.getName());
            }
        }

        loadFromFile(file);
    }

    public void save() {
        try (BufferedWriter fw = new BufferedWriter(new FileWriter(file, StandardCharsets.UTF_8))) {
            fw.write(HEADERS);
            fw.newLine();

            for (Task task : tasks.values()) {
                fw.write(taskToString(task));
                fw.newLine();
            }
            for (Task epic : epics.values()) {
                fw.write(taskToString(epic));
                fw.newLine();
            }
            for (Task subtask : subtasks.values()) {
                fw.write(taskToString(subtask));
                fw.newLine();
            }

        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка сохранения в файл: " + file.getName());
        }
    }

    public String taskToString(Task task) {
        return task.getId() + ","
                + task.getType() + ","
                + task.getName() + ","
                + task.getStatus() + ","
                + task.getDescription() + ","
                + (task.getType().equals(TaskType.SUBTASK) ? ((Subtask) task).getEpicId() : "") + ","
                + (task.getStartTime() != null ? task.getStartTime() : "") + ","
                + (task.getDuration() != null ? task.getDuration().toMinutes() : "");
    }

    public Task taskFromString(String str) {
        String[] taskParams = str.split(",");
        int id = Integer.parseInt(taskParams[0]);
        TaskType type = TaskType.valueOf(taskParams[1]);
        String name = taskParams[2];
        Status status = Status.valueOf(taskParams[3]);
        String description = taskParams[4];

        int epicId = 0;
        List<Integer> subtasks = null;
        LocalDateTime startTime = null;
        Duration duration = null;

        if (type == TaskType.TASK) {
            if (taskParams.length > 5) {
                startTime = LocalDateTime.parse(taskParams[6]);
                duration = Duration.ofMinutes(Long.parseLong(taskParams[7]));
            }
        }
        if (type == TaskType.SUBTASK) {
            epicId = Integer.parseInt(taskParams[5]);
            if (taskParams.length > 6) {
                startTime = LocalDateTime.parse(taskParams[6]);
                duration = Duration.ofMinutes(Long.parseLong(taskParams[7]));
            }
        }
        if (type == TaskType.EPIC) {
            if (taskParams.length > 5) {
                String[] strArr = taskParams[5].split(" ");
                for (String string : strArr) {
                    if (string.isEmpty()) {
                        continue;
                    }
                    subtasks.add(Integer.parseInt(string));
                }
                if (taskParams.length > 6) {
                    startTime = LocalDateTime.parse(taskParams[6]);
                    duration = Duration.ofMinutes(Long.parseLong(taskParams[7]));
                }
            }
        }

        switch (type) {
            case TASK: {
                Task task = new Task(name, description, status, startTime, duration);
                task.setId(id);
                return task;
            }
            case EPIC: {
                Epic epic = new Epic(name, description);
                epic.setId(id);
                epic.setStatus(status);
                epic.setSubtaskIds(subtasks);
                epic.setStartTime(startTime);
                epic.setDuration(duration);
                return epic;
            }
            case SUBTASK: {
                Subtask subtask = new Subtask(name, description, epicId, status, startTime, duration);
                subtask.setId(id);
                return subtask;
            }
        }

        return null;
    }

    public void loadFromFile(File file) {
        try {
            // Считываем строки из файла в List<String>
            final List<String> savedList = new ArrayList<>(Files.readAllLines(file.toPath()));
            int tasksCounter = 0;

            for (String str : savedList) {
                // Отбрасываем первую строку с заголовками и пустые строки
                if (str.equals(HEADERS) || str.isEmpty() || str.isBlank()) {
                    continue;
                }
                // Создаем задачи из строк
                Task task = taskFromString(str);
                int taskId = task.getId();
                if (taskId > tasksCounter) {
                    tasksCounter = taskId;
                }
                // Добавляем задачи в Map
                addAnyTask(task);
            }

            this.tasksCounter = tasksCounter;
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка чтения из файла: " + file.getName());
        }
    }

    protected void addAnyTask(Task task) {
        int id = task.getId();
        TaskType type = task.getType();

        switch (type) {
            case TASK: {
                tasks.put(id, task);
                if (task.getStartTime() != null) {
                    prioritizedTasks.add(task);
                }
                break;
            }
            case EPIC: {
                epics.put(id, (Epic) task);
                break;
            }
            case SUBTASK: {
                subtasks.put(id, (Subtask) task);
                if (task.getStartTime() != null) {
                    prioritizedTasks.add(task);
                }
                break;
            }
        }
    }

    @Override
    public void deleteAllTasks() {
        super.deleteAllTasks();
        save();
    }

    @Override
    public void deleteAllEpics() {
        super.deleteAllEpics();
        save();
    }

    @Override
    public void deleteAllSubtasks() {
        super.deleteAllSubtasks();
        save();
    }

    @Override
    public int addNewTask(Task task) {
        int id = super.addNewTask(task);
        save();
        return id;
    }

    @Override
    public int addNewEpic(Epic epic) {
        int id = super.addNewEpic(epic);
        save();
        return id;
    }

    @Override
    public Integer addNewSubtask(Subtask subtask) {
        int id = super.addNewSubtask(subtask);
        save();
        return id;
    }

    @Override
    public void updateTask(Task task) {
        super.updateTask(task);
        save();
    }

    @Override
    public void updateEpic(Epic epic) {
        super.updateEpic(epic);
        save();
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        super.updateSubtask(subtask);
        save();
    }

    @Override
    public void deleteTaskById(int id) {
        super.deleteTaskById(id);
        save();
    }

    @Override
    public void deleteEpicById(int id) {
        super.deleteEpicById(id);
        save();
    }

    @Override
    public void deleteSubtaskById(int id) {
        super.deleteSubtaskById(id);
        save();
    }
}
