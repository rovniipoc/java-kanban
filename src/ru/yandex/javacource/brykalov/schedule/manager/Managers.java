package ru.yandex.javacource.brykalov.schedule.manager;

public class Managers {

    public HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }

    public static TaskManager getDefault() {
        return new InMemoryTaskManager();
    }

}
