package ru.yandex.javacource.brykalov.schedule.manager;

import ru.yandex.javacource.brykalov.schedule.task.Epic;
import ru.yandex.javacource.brykalov.schedule.task.Subtask;
import ru.yandex.javacource.brykalov.schedule.task.Task;

import java.util.ArrayList;
import java.util.List;

public interface TaskManager {
    public ArrayList<Task> getTaskList();

    public ArrayList<Epic> getEpicList();

    public ArrayList<Subtask> getSubtaskList();

    public void deleteAllTasks();

    public void deleteAllEpics();

    public void deleteAllSubtasks();

    public Task getTask(int id);

    public Task getEpic(int id);

    public Task getSubtask(int id);

    public int addNewTask(Task task);

    public int addNewEpic(Epic epic);

    public Integer addNewSubtask(Subtask subtask);

    public void updateTask(Task task);

    public void updateEpic(Epic epic);

    public void updateSubtask(Subtask subtask);

    public void deleteTaskById(int id);

    public void deleteEpicById(int id);

    public void deleteSubtaskById(int id);

    public ArrayList<Subtask> getSubtasksFromEpicById(int id);

    public void updateEpicStatus(int epicId);

    public ArrayList<Task> getHistory();

    public void updateHistory(Task task);

}
