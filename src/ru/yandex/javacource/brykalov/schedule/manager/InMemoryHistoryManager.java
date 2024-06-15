package ru.yandex.javacource.brykalov.schedule.manager;

import ru.yandex.javacource.brykalov.schedule.task.Task;

import java.util.ArrayList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {
    public static final int MAX_SIZE = 10;
    private final List<Task> history = new ArrayList<>(MAX_SIZE);

    @Override
    public void add(Task task) {
        if (task == null) {
            return;
        }
        if (history.size() == MAX_SIZE) {
            history.removeFirst();
        }
        history.add(task);
    }

    @Override
    public List<Task> getHistory() {
        return history;
    }
}
