package ru.yandex.javacource.brykalov.schedule.manager;

import ru.yandex.javacource.brykalov.schedule.task.Epic;
import ru.yandex.javacource.brykalov.schedule.task.Status;
import ru.yandex.javacource.brykalov.schedule.task.Subtask;
import ru.yandex.javacource.brykalov.schedule.task.Task;

import java.util.ArrayList;
import java.util.HashMap;

public class TaskManager {
    private HashMap<Integer, Task> taskHashMap = new HashMap<>();
    private HashMap<Integer, Epic> epicHashMap = new HashMap<>();
    private HashMap<Integer, Subtask> subtaskHashMap = new HashMap<>();
    private int tasksCounter = 0;

    public ArrayList<Task> getTaskList() {
        return new ArrayList<>(taskHashMap.values());
    }

    public ArrayList<Epic> getEpicList() {
        return new ArrayList<>(epicHashMap.values());
    }

    public ArrayList<Subtask> getSubtaskList() {
        return new ArrayList<>(subtaskHashMap.values());
    }

    public void deleteAllTasks() {
        taskHashMap.clear();
    }

    public void deleteAllEpics() {
        epicHashMap.clear();
        subtaskHashMap.clear();
    }

    public void deleteAllSubtasks() {
        for (Epic epic : epicHashMap.values()) {
            epic.clearSubtaskIds();
            updateEpicStatus(epic.getId());
        }
        subtaskHashMap.clear();
    }

    public Task getTask(int id) {
        return taskHashMap.get(id);
    }

    public Task getEpic(int id) {
        return epicHashMap.get(id);
    }

    public Task getSubtask(int id) {
        return subtaskHashMap.get(id);
    }

    public int addNewTask(Task task) {
        int id = ++tasksCounter;
        task.setId(id);
        taskHashMap.put(id, task);
        return id;
    }

    public int addNewEpic(Epic epic) {
        int id = ++tasksCounter;
        epic.setId(id);
        epicHashMap.put(id, epic);
        return id;
    }

    public Integer addNewSubtask(Subtask subtask) {
        int epicId = subtask.getEpicId();
        Epic epic = epicHashMap.get(epicId);
        if (epic == null) {
            return null;
        }
        int id = ++tasksCounter;
        subtask.setId(id);
        subtaskHashMap.put(id, subtask);
        epic.addSubtaskId(subtask.getId());
        updateEpicStatus(epicId);
        return id;
    }

    public void updateTask(Task task) {
        int id = task.getId();
        Task savedTask = taskHashMap.get(id);
        if (savedTask == null) {
            return;
        }
        taskHashMap.put(id, task);
    }

    public void updateEpic(Epic epic) {
        Epic savedEpic = epicHashMap.get(epic.getId());
        if (savedEpic == null) {
            return;
        }
        savedEpic.setName(epic.getName());
        savedEpic.setDescription(epic.getDescription());
    }

    public void updateSubtask(Subtask subtask) {
        if (subtask == null) {
            return;
        }
        if (epicHashMap.get(subtask.getEpicId()) == null) {
            return;
        }
        subtaskHashMap.replace(subtask.getId(), subtask);
        updateEpicStatus(subtask.getEpicId());
    }

    public void deleteTaskById(int id) {
        taskHashMap.remove(id);
    }

    public void deleteEpicById(int id) {
        ArrayList<Integer> subtaskIds = epicHashMap.get(id).getSubtaskIds();
        for (int subtaskId : subtaskIds) {
            deleteTaskById(subtaskId);
        }
        epicHashMap.remove(id);
    }

    public void deleteSubtaskById(int id) {
        Subtask subtask = subtaskHashMap.remove(id);
        if (subtask == null) {
            return;
        }
        Epic epic = epicHashMap.get(subtask.getEpicId());
        epic.removeSubtask(id);
        updateEpicStatus(epic.getId());
    }

    public ArrayList<Subtask> getSubtasksFromEpicById(int id) {
        ArrayList<Subtask> subtasksList = new ArrayList<>();
        Epic epic = epicHashMap.get(id);
        if (epic == null) {
            return null;
        }
        for (int subtaskId : epic.getSubtaskIds()) {
            subtasksList.add(subtaskHashMap.get(subtaskId));
        }
        return subtasksList;
    }

    private void updateEpicStatus(int epicId) {
        ArrayList<Integer> subtaskIds = epicHashMap.get(epicId).getSubtaskIds();
        int numberOfNewTask = 0;
        int numberOfCompletedTask = 0;

        for (Integer subtaskId : subtaskIds) {
            if (subtaskHashMap.get(subtaskId).getStatus() == Status.NEW) {
                numberOfNewTask++;
            }
            if (subtaskHashMap.get(subtaskId).getStatus() == Status.DONE) {
                numberOfCompletedTask++;
            }
        }

        if (!subtaskIds.isEmpty() && numberOfCompletedTask == subtaskIds.size()) {
            epicHashMap.get(epicId).setStatus(Status.DONE);
        } else if (subtaskIds.isEmpty() || (numberOfNewTask == subtaskIds.size())) {
            epicHashMap.get(epicId).setStatus(Status.NEW);
        } else {
            epicHashMap.get(epicId).setStatus(Status.IN_PROGRESS);
        }
    }
}
