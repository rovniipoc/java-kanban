package ru.yandex.javacource.brykalov.schedule.manager;
import ru.yandex.javacource.brykalov.schedule.task.Epic;
import ru.yandex.javacource.brykalov.schedule.task.Subtask;
import ru.yandex.javacource.brykalov.schedule.task.Task;

import java.util.ArrayList;

public interface HistoryManager {

    public void add(Task task);

    public ArrayList<Task> getHistory();
}
