package ru.yandex.javacource.brykalov.schedule.manager;

import ru.yandex.javacource.brykalov.schedule.task.Task;

import java.util.List;

interface HistoryManager {

    void add(Task task);

    void remove(int id);

    List<Task> getHistory();
}
