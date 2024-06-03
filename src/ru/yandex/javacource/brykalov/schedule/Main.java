package ru.yandex.javacource.brykalov.schedule;

import ru.yandex.javacource.brykalov.schedule.manager.TaskManager;
import ru.yandex.javacource.brykalov.schedule.task.*;

public class Main {

    public static void main(String[] args) {
        TaskManager taskManager = new TaskManager();

        taskManager.addNewTask(new Task("Прост.зад.1",
                                    "Опис.прост.зад.1", Status.NEW));
        taskManager.addNewTask(new Task("Прост.зад.2",
                                    "Опис.прост.зад.2", Status.NEW));
        taskManager.addNewEpic(new Epic("Слож.зад.1",
                                    "Опис.слож.зад.1"));
        taskManager.addNewSubtask(new Subtask("Подзад.1слож.зад.1",
                                            "Опис.подзад.1слож.зад.1",
                                                3, Status.NEW));
        taskManager.addNewSubtask(new Subtask("Подзад.2слож.зад.1",
                                            "Опис.подзад.2слож.зад.1",
                                                3, Status.NEW));
        taskManager.addNewEpic(new Epic("Слож.зад.2",
                                    "Опис.слож.зад.2"));
        taskManager.addNewSubtask(new Subtask("Подзад.1слож.зад.2",
                                            "Опис.подзад.1слож.зад.2",
                                                6, Status.NEW));


        int epicId = taskManager.addNewEpic(new Epic("Слож.зад.3",
                                                 "Опис.слож.зад.3"));

        System.out.println(taskManager.getTaskList());
        System.out.println(taskManager.getEpicList());
        System.out.println(taskManager.getSubtaskList());

        taskManager.addNewSubtask(new Subtask("Подзад.2слож.зад.2",
                                            "Опис.подзад.2слож.зад.2",
                                                6, Status.IN_PROGRESS));

        taskManager.deleteTaskById(1);
        taskManager.deleteEpicById(epicId);

        System.out.println();
        System.out.println(taskManager.getTaskList());
        System.out.println(taskManager.getEpicList());
        System.out.println(taskManager.getSubtaskList());
    }
}
