package ru.yandex.javacource.brykalov.schedule.manager;

import ru.yandex.javacource.brykalov.schedule.task.Epic;
import ru.yandex.javacource.brykalov.schedule.task.Subtask;
import ru.yandex.javacource.brykalov.schedule.task.Task;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class FileBackedTaskManager extends InMemoryTaskManager {

    File file = null;

    public FileBackedTaskManager(File file) {
        this.file = file;
        //Files.createFile(Path.of(file.toURI()));
    }

    public void save() {
        List<String> tasksForSave = allTasksToString();
        List<String> epicsForSave = allEpicsToString();
        List<String> subtasksForSave = allSubtasksToString();

        try (Writer fw = new FileWriter(file, StandardCharsets.UTF_8)) {
            fw.write("id,type,name,status,description,epicId_or_subtasks\n");
            for (String str : tasksForSave) {
                fw.write(str);
            }
            for (String str : epicsForSave) {
                fw.write(str);
            }
            for (String str : subtasksForSave) {
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
            StringBuilder epicSubtasks = new StringBuilder("");
            for (Integer subtaskId : entry.getValue().getSubtaskIds()) {
                epicSubtasks.append(subtaskId).append(" ");
            }

            epicsForSave.add(String.format("%s,%s,%s,%s,%s,%s\n",
                    entry.getKey(),
                    TaskType.EPIC,
                    entry.getValue().getName(),
                    entry.getValue().getStatus(),
                    entry.getValue().getDescription(),
                    epicSubtasks.toString()));
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

    public void read() throws IOException {
        System.out.println(Files.readString(file.toPath()));
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
