package ru.yandex.javacource.brykalov.schedule.manager;

import ru.yandex.javacource.brykalov.schedule.task.Task;

import java.util.ArrayList;

public class InMemoryHistoryManager implements HistoryManager {
    ArrayList<Task> historyObjectList = new ArrayList<>(10);

    @Override
    public void add(Task task) {
        if (historyObjectList.size() == 10) {
            historyObjectList.removeFirst();
        }
        historyObjectList.add(task);
    }

    @Override
    public ArrayList<Task> getHistory() {
        return historyObjectList;
    }
}
