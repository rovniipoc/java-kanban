package ru.yandex.javacource.brykalov.schedule.manager;

import ru.yandex.javacource.brykalov.schedule.task.Epic;
import ru.yandex.javacource.brykalov.schedule.task.Subtask;
import ru.yandex.javacource.brykalov.schedule.task.Task;

import java.util.List;

interface TaskManager {
    List<Task> getTaskList();

    List<Epic> getEpicList();

    List<Subtask> getSubtaskList();

    void deleteAllTasks();

    void deleteAllEpics();

    void deleteAllSubtasks();

    Task getTask(int id);

    Task getEpic(int id);

    Task getSubtask(int id);

    int addNewTask(Task task);

    int addNewEpic(Epic epic);

    Integer addNewSubtask(Subtask subtask);

    void updateTask(Task task);

    void updateEpic(Epic epic);

    void updateSubtask(Subtask subtask);

    void deleteTaskById(int id);

    void deleteEpicById(int id);

    void deleteSubtaskById(int id);

    List<Subtask> getSubtasksFromEpicById(int id);

    List<Task> getHistory();

}
