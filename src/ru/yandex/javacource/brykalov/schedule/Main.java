package ru.yandex.javacource.brykalov.schedule;

import ru.yandex.javacource.brykalov.schedule.manager.InMemoryTaskManager;
import ru.yandex.javacource.brykalov.schedule.task.*;

public class Main {

    public static void main(String[] args) {
        InMemoryTaskManager taskManager = new InMemoryTaskManager();

        // Создаем задачи: эпик без подзадач и эпик с тремя подзадачами
        Epic epic1 = new Epic("Эпик1", "Описание эпика1");
        int epicId1 = taskManager.addNewEpic(epic1);

        Epic epic2 = new Epic("Эпик2", "Описание эпика2");
        int epicId2 = taskManager.addNewEpic(epic2);

        Subtask subtask1 = new Subtask("Подзадача1", "Описание подзадачи1", epicId2, Status.NEW);
        int subtaskId1 = taskManager.addNewSubtask(subtask1);

        Subtask subtask2 = new Subtask("Подзадача2", "Описание подзадачи2", epicId2, Status.NEW);
        int subtaskId2 = taskManager.addNewSubtask(subtask2);

        Subtask subtask3 = new Subtask("Подзадача3", "Описание подзадачи3", epicId2, Status.NEW);
        int subtaskId3 = taskManager.addNewSubtask(subtask3);

        // Запрашиваем задачи несколько раз в разном порядке
        taskManager.getSubtask(subtaskId2);
        taskManager.getEpic(epicId1);
        taskManager.getEpic(epicId2);
        taskManager.getSubtask(subtaskId1);
        taskManager.getSubtask(subtaskId3);

        // Выводим историю просмотров
        System.out.println("Список истории должен быть (подзадача2, эпик1, эпик2, подзадача1, подзадача3):");
        System.out.println(taskManager.getHistory());

        // Запрашиваем еще задачи, убеждаемся что в истории нет повторов
        taskManager.getSubtask(subtaskId1);
        taskManager.getEpic(epicId1);
        taskManager.getSubtask(subtaskId1);
        taskManager.getEpic(epicId2);
        taskManager.getSubtask(subtaskId2);
        taskManager.getSubtask(subtaskId2);
        taskManager.getEpic(epicId2);
        taskManager.getSubtask(subtaskId1);
        taskManager.getSubtask(subtaskId3);
        taskManager.getSubtask(subtaskId3);
        System.out.println("Список истории должен быть (эпик1, подзадача2, эпик2, подзадача1, подзадача3):");
        System.out.println(taskManager.getHistory());

        // Удаляем задачу, убеждаемся, что из истории она тоже исчезла
        taskManager.deleteSubtaskById(subtaskId1);
        System.out.println("Список истории должен быть (эпик1, подзадача2, эпик2, подзадача3):");
        System.out.println(taskManager.getHistory());

        // Удаляем эпик с подзадачами, убеждаемся, что из истории он исчез вместе с подзадачами
        taskManager.deleteEpicById(epicId2);
        System.out.println("Список истории должен быть (эпик1):");
        System.out.println(taskManager.getHistory());

        // Проверяем быстродействие
        int numberOfTasks = 1_000_000;
        Task[] array = new Task[numberOfTasks];
        for (int i = 0; i < numberOfTasks; i++) {
            array[i] = new Task("Задача", "Описание задачи", Status.NEW);
            taskManager.addNewTask(array[i]);
            taskManager.getTask(array[i].getId());
        }
        long t1 = System.nanoTime();
        taskManager.getTask(numberOfTasks / 2);
        taskManager.deleteTaskById(numberOfTasks / 2);
        long t2 = System.nanoTime();
        System.out.println("Затраченное время на просмотр и удаление одной из N задач (в т.ч. изменение истории), наносекунд: " + (t2 - t1));
        System.out.println("Количество задач N = " + numberOfTasks);

    }
}
