package ru.yandex.javacource.brykalov.schedule.manager;

import ru.yandex.javacource.brykalov.schedule.task.Epic;
import ru.yandex.javacource.brykalov.schedule.task.Status;
import ru.yandex.javacource.brykalov.schedule.task.Subtask;
import ru.yandex.javacource.brykalov.schedule.task.Task;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class FileBackedTaskManager extends InMemoryTaskManager {

    File file;

    public FileBackedTaskManager(File file) throws IOException {
        this.file = file;

        if (!file.exists()) {
            Files.createFile(Path.of(file.toURI()));
        }

        loadFromFile(file);
    }

    public void save() {
        List<String> tasksForSave = allTasksToString();
        List<String> epicsForSave = allEpicsToString();
        List<String> subtasksForSave = allSubtasksToString();

        List<String> allTasksForSave = new ArrayList<>();
        allTasksForSave.addAll(tasksForSave);
        allTasksForSave.addAll(epicsForSave);
        allTasksForSave.addAll(subtasksForSave);

        Comparator<String> comparator = (o1, o2) -> {
            int id1 = Integer.parseInt(o1.substring(0, o1.indexOf(",")));
            int id2 = Integer.parseInt(o2.substring(0, o2.indexOf(",")));
            return id1 - id2;
        };

        allTasksForSave.sort(comparator);

        try (Writer fw = new FileWriter(file, StandardCharsets.UTF_8)) {
            fw.write("id,type,name,status,description,epicId_or_subtasks\n");
            for (String str : allTasksForSave) {
                fw.write(str);
            }
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка создания файла.");
        }
    }

    public List<String> allTasksToString() {
        List<String> tasksForSave = new ArrayList<>();

        for (Map.Entry<Integer, Task> entry : tasks.entrySet()) {
            tasksForSave.add(String.format("%s,%s,%s,%s,%s\n",
                    entry.getKey(),
                    TaskType.TASK,
                    entry.getValue().getName(),
                    entry.getValue().getStatus(),
                    entry.getValue().getDescription()));
        }
        return tasksForSave;
    }

    public List<String> allEpicsToString() {
        List<String> epicsForSave = new ArrayList<>();

        for (Map.Entry<Integer, Epic> entry : epics.entrySet()) {
            StringBuilder epicSubtasks = new StringBuilder();
            for (Integer subtaskId : entry.getValue().getSubtaskIds()) {
                epicSubtasks.append(subtaskId).append(" ");
            }

            epicsForSave.add(String.format("%s,%s,%s,%s,%s,%s\n",
                    entry.getKey(),
                    TaskType.EPIC,
                    entry.getValue().getName(),
                    entry.getValue().getStatus(),
                    entry.getValue().getDescription(),
                    epicSubtasks));
        }
        return epicsForSave;
    }

    public List<String> allSubtasksToString() {
        List<String> subtasksForSave = new ArrayList<>();

        for (Map.Entry<Integer, Subtask> entry : subtasks.entrySet()) {
            subtasksForSave.add(String.format("%s,%s,%s,%s,%s,%s\n",
                    entry.getKey(),
                    TaskType.SUBTASK,
                    entry.getValue().getName(),
                    entry.getValue().getStatus(),
                    entry.getValue().getDescription(),
                    entry.getValue().getEpicId()));
        }
        return subtasksForSave;
    }

    public void loadFromFile(File file) throws IOException {
        List<String> savedList = new ArrayList<>(Files.readAllLines(file.toPath()));

        // Считываем строки из файла в List (в файле строки заранее отсортированы по id), парсим на параметры задач,
        // создаем задачи в том же порядке, в котором они создавались ранее (для того, чтобы id задач остались прежними)

        for (String str : savedList) {
            String[] taskParams = str.split(",");

            // Отбрасываем первую строку с заголовками
            if (Objects.equals(taskParams[0], "id")) {
                continue;
            }

            Integer id = Integer.valueOf(taskParams[0]);
            TaskType type = TaskType.valueOf(taskParams[1]);
            String name = taskParams[2];
            Status status = Status.valueOf(taskParams[3]);
            String description = taskParams[4];
            int epicId = 0;
            String[] subtasks;

            if (type == TaskType.SUBTASK) {
                epicId = Integer.parseInt(taskParams[5]);
            }
            if (type == TaskType.EPIC) {
                if (taskParams.length == 6) {
                    subtasks = taskParams[5].split(" ");
                }
            }

            switch (type) {
                case TASK: {
                    addNewTask(new Task(name, description, status));
                    break;
                }
                case EPIC: {
                    addNewEpic(new Epic(name, description));
                    break;
                }
                case SUBTASK: {
                    addNewSubtask(new Subtask(name, description, epicId, status));
                    break;
                }
            }
        }
    }

    public static class ManagerSaveException extends Error {
        public ManagerSaveException(String message) {
            super(message);
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
