package ru.yandex.javacource.brykalov.schedule;

import ru.yandex.javacource.brykalov.schedule.manager.FileBackedTaskManager;
import ru.yandex.javacource.brykalov.schedule.manager.InMemoryTaskManager;
import ru.yandex.javacource.brykalov.schedule.task.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class Main {

    public static void main(String[] args) throws IOException {
        //InMemoryTaskManager taskManager = new InMemoryTaskManager();

        //Files.createFile(Path.of("src/ru/yandex/javacource/brykalov/schedule/save/file.csv"));
        File file = new File("src/ru/yandex/javacource/brykalov/schedule/save/file.csv");
        FileBackedTaskManager fileBackedTaskManager = new FileBackedTaskManager(file);

        // Создаем задачи:
        Epic epic1 = new Epic("Эпик1", "Описание эпика1");
        int epicId1 = fileBackedTaskManager.addNewEpic(epic1);

        Epic epic2 = new Epic("Эпик2", "Описание эпика2");
        int epicId2 = fileBackedTaskManager.addNewEpic(epic2);

        Subtask subtask1 = new Subtask("Подзадача1", "Описание подзадачи1", epicId2, Status.NEW);
        int subtaskId1 = fileBackedTaskManager.addNewSubtask(subtask1);

        Subtask subtask2 = new Subtask("Подзадача2", "Описание подзадачи2", epicId2, Status.NEW);
        int subtaskId2 = fileBackedTaskManager.addNewSubtask(subtask2);

        Subtask subtask3 = new Subtask("Подзадача3", "Описание подзадачи3", epicId2, Status.NEW);
        int subtaskId3 = fileBackedTaskManager.addNewSubtask(subtask3);

        Task task1 = new Task("Задача", "Описание задачи", Status.NEW);
        int taskId1 = fileBackedTaskManager.addNewTask(task1);

        // Запрашиваем задачи несколько раз в разном порядке
//        fileBackedTaskManager.getSubtask(subtaskId2);
//        fileBackedTaskManager.getEpic(epicId1);
//        fileBackedTaskManager.getEpic(epicId2);
//        fileBackedTaskManager.getSubtask(subtaskId1);
//        fileBackedTaskManager.getSubtask(subtaskId3);

        // Выводим историю просмотров
//        System.out.println("Список истории должен быть (подзадача2, эпик1, эпик2, подзадача1, подзадача3):");
//        System.out.println(fileBackedTaskManager.getHistory());
//
//        // Запрашиваем еще задачи, убеждаемся что в истории нет повторов
//        fileBackedTaskManager.getSubtask(subtaskId1);
//        fileBackedTaskManager.getEpic(epicId1);
//        fileBackedTaskManager.getSubtask(subtaskId1);
//        fileBackedTaskManager.getEpic(epicId2);
//        fileBackedTaskManager.getSubtask(subtaskId2);
//        fileBackedTaskManager.getSubtask(subtaskId2);
//        fileBackedTaskManager.getEpic(epicId2);
//        fileBackedTaskManager.getSubtask(subtaskId1);
//        fileBackedTaskManager.getSubtask(subtaskId3);
//        fileBackedTaskManager.getSubtask(subtaskId3);
//        System.out.println("Список истории должен быть (эпик1, подзадача2, эпик2, подзадача1, подзадача3):");
//        System.out.println(fileBackedTaskManager.getHistory());

        // Удаляем задачу, убеждаемся, что из истории она тоже исчезла
//        taskManager.deleteSubtaskById(subtaskId1);
//        System.out.println("Список истории должен быть (эпик1, подзадача2, эпик2, подзадача3):");
//        System.out.println(taskManager.getHistory());

        // Удаляем эпик с подзадачами, убеждаемся, что из истории он исчез вместе с подзадачами
//        taskManager.deleteEpicById(epicId2);
//        System.out.println("Список истории должен быть (эпик1):");
//        System.out.println(taskManager.getHistory());

        // Проверяем быстродействие
//        int numberOfTasks = 1_000_000;
//        Task[] array = new Task[numberOfTasks];
//        for (int i = 0; i < numberOfTasks; i++) {
//            array[i] = new Task("Задача", "Описание задачи", Status.NEW);
//            taskManager.addNewTask(array[i]);
//            taskManager.getTask(array[i].getId());
//        }
//        long t1 = System.nanoTime();
//        taskManager.getTask(numberOfTasks / 2);
//        taskManager.deleteTaskById(numberOfTasks / 2);
//        long t2 = System.nanoTime();
//        System.out.println("Затраченное время на просмотр и удаление одной из N задач (в т.ч. изменение истории), наносекунд: " + (t2 - t1));
//        System.out.println("Количество задач N = " + numberOfTasks);

        // Удаляем все типы задач
//        fileBackedTaskManager.deleteAllTasks();
//        fileBackedTaskManager.deleteAllEpics();
//        fileBackedTaskManager.deleteAllSubtasks();

        // Проверяем чтение из файла
        fileBackedTaskManager.read();
    }
}
