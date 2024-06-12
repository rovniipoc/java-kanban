package ru.yandex.javacource.brykalov.schedule;

import ru.yandex.javacource.brykalov.schedule.manager.InMemoryTaskManager;
import ru.yandex.javacource.brykalov.schedule.task.*;

public class Main {

    public static void main(String[] args) {
        InMemoryTaskManager inMemoryTaskManager = new InMemoryTaskManager();

        inMemoryTaskManager.addNewTask(new Task("Прост.зад.1",
                                    "Опис.прост.зад.1", Status.NEW));
        inMemoryTaskManager.addNewTask(new Task("Прост.зад.2",
                                    "Опис.прост.зад.2", Status.NEW));
        inMemoryTaskManager.addNewEpic(new Epic("Слож.зад.1",
                                    "Опис.слож.зад.1"));
        inMemoryTaskManager.addNewSubtask(new Subtask("Подзад.1слож.зад.1",
                                            "Опис.подзад.1слож.зад.1",
                                                3, Status.NEW));
        inMemoryTaskManager.addNewSubtask(new Subtask("Подзад.2слож.зад.1",
                                            "Опис.подзад.2слож.зад.1",
                                                3, Status.NEW));
        inMemoryTaskManager.addNewEpic(new Epic("Слож.зад.2",
                                    "Опис.слож.зад.2"));
        inMemoryTaskManager.addNewSubtask(new Subtask("Подзад.1слож.зад.2",
                                            "Опис.подзад.1слож.зад.2",
                                                6, Status.NEW));

        int epicId = inMemoryTaskManager.addNewEpic(new Epic("Слож.зад.3",
                                                 "Опис.слож.зад.3"));

//        System.out.println(inMemoryTaskManager.getTaskList());
//        System.out.println(inMemoryTaskManager.getEpicList());
//        System.out.println(inMemoryTaskManager.getSubtaskList());

        inMemoryTaskManager.addNewSubtask(new Subtask("Подзад.2слож.зад.2",
                                            "Опис.подзад.2слож.зад.2",
                                                6, Status.IN_PROGRESS));

//        inMemoryTaskManager.deleteTaskById(1);
//        inMemoryTaskManager.deleteEpicById(epicId);
//
//        System.out.println();
//        System.out.println(inMemoryTaskManager.getTaskList());
//        System.out.println(inMemoryTaskManager.getEpicList());
//        System.out.println(inMemoryTaskManager.getSubtaskList());

        inMemoryTaskManager.getTask(1);
        inMemoryTaskManager.getSubtask(4);
        inMemoryTaskManager.getSubtask(5);
        inMemoryTaskManager.getEpic(3);
        inMemoryTaskManager.getEpic(6);
        inMemoryTaskManager.getEpic(6);
        inMemoryTaskManager.getSubtask(7);
        inMemoryTaskManager.getSubtask(9);
        inMemoryTaskManager.getSubtask(9);
        inMemoryTaskManager.getSubtask(7);
        inMemoryTaskManager.getSubtask(7);

        System.out.println("Задачи:");
        for (Task task : inMemoryTaskManager.getTaskList()) {
            System.out.println(task);
        }
        System.out.println("Эпики:");
        for (Task epic : inMemoryTaskManager.getEpicList()) {
            System.out.println(epic);

            for (Task task : inMemoryTaskManager.getSubtasksFromEpicById(epic.getId())) {
                System.out.println("--> " + task);
            }
        }
        System.out.println("Подзадачи:");
        for (Task subtask : inMemoryTaskManager.getSubtaskList()) {
            System.out.println(subtask);
        }

        System.out.println("История:");
        for (Task task : inMemoryTaskManager.getHistory()) {
            System.out.println(task);
        }
    }
}
