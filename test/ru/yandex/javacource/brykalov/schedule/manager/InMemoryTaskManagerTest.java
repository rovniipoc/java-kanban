package ru.yandex.javacource.brykalov.schedule.manager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.javacource.brykalov.schedule.task.Epic;
import ru.yandex.javacource.brykalov.schedule.task.Status;
import ru.yandex.javacource.brykalov.schedule.task.Subtask;
import ru.yandex.javacource.brykalov.schedule.task.Task;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static ru.yandex.javacource.brykalov.schedule.task.Status.*;

public class InMemoryTaskManagerTest {

    InMemoryTaskManager inMemoryTaskManager = new InMemoryTaskManager();

    @BeforeEach
    void clearAllTasks() {
        inMemoryTaskManager.deleteAllTasks();
        inMemoryTaskManager.deleteAllEpics();
        inMemoryTaskManager.deleteAllSubtasks();
    }

    @Test
    void addNewAllTypeTasks() {
        // проверка работы методов по добавлению задач разных типов
        Task task = new Task("Задача", "Описание задачи", Status.NEW);
        int taskId = inMemoryTaskManager.addNewTask(task);

        Epic epic = new Epic("Эпик", "Описание эпика");
        int epicId = inMemoryTaskManager.addNewEpic(epic);

        Subtask subtask = new Subtask("Подзадача", "Описание подзадачи", epicId, Status.NEW);
        int subtaskId = inMemoryTaskManager.addNewSubtask(subtask);

        Task savedTask = inMemoryTaskManager.getTask(taskId);
        Task savedEpic = inMemoryTaskManager.getEpic(epicId);
        Task savedSubtask = inMemoryTaskManager.getSubtask(subtaskId);

        assertNotNull(savedTask, "Задача не найдена.");
        assertNotNull(savedEpic, "Эпик не найден.");
        assertNotNull(savedSubtask, "Подзадача не найдена.");

        assertEquals(task, savedTask, "Задачи не совпадают.");
        assertEquals(epic, savedEpic, "Эпики не совпадают.");
        assertEquals(subtask, savedSubtask, "Подзадачи не совпадают.");
    }

    @Test
    void shouldBeUnchangedAfterAdding() {
        // проверка на сохранность параметров задач после их добавления в хранилище и извлечения из него
        String nameTask = "Задача";
        String descriptionTask = "Описание задачи";
        Status statusTask = NEW;
        Task task = new Task(nameTask, descriptionTask, statusTask);
        int taskId = inMemoryTaskManager.addNewTask(task);
        Task taskFromManager = inMemoryTaskManager.getTask(taskId);
        assertEquals(nameTask, taskFromManager.getName());
        assertEquals(descriptionTask, taskFromManager.getDescription());
        assertEquals(statusTask, taskFromManager.getStatus());

        String nameEpic = "Эпик";
        String descriptionEpic = "Описание эпика";
        Epic epic = new Epic(nameEpic, descriptionEpic);
        int epicId = inMemoryTaskManager.addNewEpic(epic);
        Epic epicFromManager = inMemoryTaskManager.getEpic(epicId);
        assertEquals(nameEpic, epicFromManager.getName());
        assertEquals(descriptionEpic, epicFromManager.getDescription());

        String nameSubtask = "Подзадача";
        String descriptionSubtask = "Описание подзадачи";
        Status statusSubtask = NEW;
        Subtask subtask = new Subtask(nameSubtask, descriptionSubtask, epicId, statusSubtask);
        int subtaskId = inMemoryTaskManager.addNewSubtask(subtask);
        Subtask subtaskFromManager = inMemoryTaskManager.getSubtask(subtaskId);
        assertEquals(nameSubtask, subtaskFromManager.getName());
        assertEquals(descriptionSubtask, subtaskFromManager.getDescription());
        assertEquals(statusSubtask, subtaskFromManager.getStatus());
        assertEquals(epicId, subtaskFromManager.getEpicId());
    }

    @Test
    void addAndReadHistory() {
        // проверка работы истории, в т.ч. проверка что при изменении задачи в хранилище
        // в истории остается неизмененная версия задачи
        Task task = new Task("Задача", "Описание задачи", Status.NEW);
        int taskId = inMemoryTaskManager.addNewTask(task);

        Epic epic = new Epic("Эпик", "Описание эпика");
        int epicId = inMemoryTaskManager.addNewEpic(epic);

        Subtask subtask = new Subtask("Подзадача", "Описание подзадачи", epicId, Status.NEW);
        int subtaskId = inMemoryTaskManager.addNewSubtask(subtask);

        // всего в проверке 11 методов .get...(), это сделано для проверки "перезаписываемости" истории
        // по достижению 10+ просмотренных объектов.
        // Это первый метод getTask, поэтому он не должен попасть в историю,
        // т.к. после него еще 10 подобных "просмотров"
        inMemoryTaskManager.getTask(taskId);

        // создаем и наполняем "проверочный список". В список вносим последние 10 "просмотренных"
        // с помощь get...() объекты
        List<Task> tasks = new ArrayList<>();
        tasks.add(inMemoryTaskManager.getSubtask(subtaskId));
        tasks.add(inMemoryTaskManager.getSubtask(subtaskId));
        tasks.add(inMemoryTaskManager.getEpic(epicId));
        tasks.add(inMemoryTaskManager.getEpic(epicId));
        tasks.add(inMemoryTaskManager.getEpic(epicId));
        tasks.add(inMemoryTaskManager.getSubtask(subtaskId));
        tasks.add(inMemoryTaskManager.getTask(taskId));
        tasks.add(inMemoryTaskManager.getTask(taskId));
        tasks.add(inMemoryTaskManager.getEpic(epicId));
        tasks.add(inMemoryTaskManager.getSubtask(subtaskId));

        // выполняем проверку на "перезаписываемость" 10 элементов в истории
        List<Task> history = inMemoryTaskManager.getHistory();
        assertArrayEquals(new ArrayList[]{(ArrayList) tasks}, new ArrayList[]{(ArrayList) history},
                "История сохраняется неверно.");

        // модифицируем задачи и добавляем замещаем ими соответствующие задачи в проверочном листе
        // Т.о. сравнив задачу из истории и из проверочного списка, поймем сохраняется
        // ли в истории "старый" объект или тоже модифицируется. Повторяем это для Epic и Subtask.
        Task modifedTask = new Task("Мод.Задача", "Описание мод.задачи", Status.NEW);
        modifedTask.setId(taskId);
        inMemoryTaskManager.updateTask(modifedTask);
        tasks.set(7, modifedTask);

        Epic modifedEpic = new Epic("Мод.Эпик", "Описание мод.эпика");
        modifedEpic.setId(epicId);
        inMemoryTaskManager.updateEpic(modifedEpic);
        tasks.set(8, modifedEpic);

        Subtask modifedSubtask = new Subtask("Мод.подзадача", "Описание мод.подзадачи", epicId, Status.NEW);
        modifedSubtask.setId(subtaskId);
        inMemoryTaskManager.updateSubtask(modifedSubtask);
        tasks.set(9, modifedSubtask);

        history = inMemoryTaskManager.getHistory();

        // проверяем, что после изменения задач те же задачи не изменились в истории
        assertNotEquals(history.get(7), tasks.get(7), "В истории должны храниться задачи" +
                " в том состоянии, в котором они были в момент" +
                " добавления в историю. Task после update изменился и в истории тоже");
        assertNotEquals(history.get(8), tasks.get(8), "В истории должны храниться задачи" +
                " в том состоянии, в котором они были в момент" +
                " добавления в историю. Epic после update изменился и в истории тоже");
        assertNotEquals(history.get(9), tasks.get(9), "В истории должны храниться задачи" +
                " в том состоянии, в котором они были в момент" +
                " добавления в историю. Subtask после update изменился и в истории тоже");
    }

    @Test
    void cannotBeSubtasksAsEpic() {
        // проверка на невозможность присвоить Id сабтаски как Id эпика другой сабтаски
        Epic epic = new Epic("Эпик", "Описание эпика");
        int epicId = inMemoryTaskManager.addNewEpic(epic);

        Subtask subtask1 = new Subtask("Подзадача1", "Описание подзадачи1", epicId, Status.NEW);
        int subtaskId1 = inMemoryTaskManager.addNewSubtask(subtask1);

        Subtask subtask2 = new Subtask("Подзадача2", "Описание подзадачи2", subtaskId1, Status.NEW);
        Integer subtaskId2 = inMemoryTaskManager.addNewSubtask(subtask2);

        assertNull(subtaskId2, "Для поздадачи другая подзадача не может служить эпиком");
    }

    @Test
    void cannotBeEpicAsSubtask() {
        // проверка на невозможность добавления эпика как сабтаски для другого эпика
        Epic epic = new Epic("Эпик", "Описание эпика");
        int epicId = inMemoryTaskManager.addNewEpic(epic);

        Epic epic2 = new Epic("Эпик2", "Описание эпика2");
        int epicId2 = inMemoryTaskManager.addNewEpic(epic);

        // я даже не знаю как запихнуть эпик в эпик, если только так:

        //inMemoryTaskManager.getEpic(epicId).addSubtaskId(epicId2);
        //assertEquals(0, inMemoryTaskManager.getEpic(epicId).getSubtaskIds());

        // но в таком случае я не понимаю как от этого защититься, ведь метод эпика addSubtaskId()
        // принимает простое число Integer и сам принимающий эпик не знает что за этим Integer кроется -
        // или сабтаск, или эпик, или такой задачи с таким id вообще не существует...

    }

    @Test
    void DeletingTasks() {
        // проверка работы методов удаления задач разных типов
        Task task = new Task("Задача", "Описание задачи", Status.NEW);
        int taskId = inMemoryTaskManager.addNewTask(task);

        Epic epic = new Epic("Эпик", "Описание эпика");
        int epicId = inMemoryTaskManager.addNewEpic(epic);

        Subtask subtask = new Subtask("Подзадача", "Описание подзадачи", epicId, Status.NEW);
        int subtaskId = inMemoryTaskManager.addNewSubtask(subtask);

        Subtask subtask2 = new Subtask("Подзадача2", "Описание подзадачи2", epicId, Status.NEW);
        int subtaskId2 = inMemoryTaskManager.addNewSubtask(subtask2);

        inMemoryTaskManager.deleteTaskById(taskId);
        inMemoryTaskManager.deleteSubtaskById(subtaskId2);
        inMemoryTaskManager.deleteEpicById(epicId); //удалятся и эпик и все его сабтаски

        assertEquals(0, inMemoryTaskManager.getTaskList().size());
        assertEquals(0, inMemoryTaskManager.getEpicList().size());
        assertEquals(0, inMemoryTaskManager.getSubtaskList().size());

        task = new Task("Задача", "Описание задачи", Status.NEW);
        taskId = inMemoryTaskManager.addNewTask(task);

        epic = new Epic("Эпик", "Описание эпика");
        epicId = inMemoryTaskManager.addNewEpic(epic);

        subtask = new Subtask("Подзадача", "Описание подзадачи", epicId, Status.NEW);
        subtaskId = inMemoryTaskManager.addNewSubtask(subtask);

        subtask2 = new Subtask("Подзадача2", "Описание подзадачи2", epicId, Status.NEW);
        subtaskId2 = inMemoryTaskManager.addNewSubtask(subtask2);

        inMemoryTaskManager.deleteAllSubtasks();
        assertEquals(0, inMemoryTaskManager.getSubtaskList().size());

        subtask = new Subtask("Подзадача", "Описание подзадачи", epicId, Status.NEW);
        subtaskId = inMemoryTaskManager.addNewSubtask(subtask);

        inMemoryTaskManager.deleteAllTasks();
        inMemoryTaskManager.deleteAllEpics();

        assertEquals(0, inMemoryTaskManager.getTaskList().size());
        assertEquals(0, inMemoryTaskManager.getEpicList().size());
        assertEquals(0, inMemoryTaskManager.getSubtaskList().size());

        epic = new Epic("Эпик", "Описание эпика");
        epicId = inMemoryTaskManager.addNewEpic(epic);

        subtask = new Subtask("Подзадача", "Описание подзадачи", epicId, Status.DONE);
        subtaskId = inMemoryTaskManager.addNewSubtask(subtask);

        inMemoryTaskManager.deleteSubtaskById(subtaskId);
        assertEquals(0, inMemoryTaskManager.getSubtaskList().size());

        inMemoryTaskManager.deleteEpicById(epicId);
        assertEquals(0, inMemoryTaskManager.getEpicList().size());
    }

    @Test
    void getSubtasksFromEpicByIdTest() {
        // проверка метода getSubtasksFromEpicById()
        Epic epic = new Epic("Эпик", "Описание эпика");
        int epicId = inMemoryTaskManager.addNewEpic(epic);
        Subtask subtask = new Subtask("Подзадача", "Описание подзадачи", epicId, Status.NEW);
        int subtaskId = inMemoryTaskManager.addNewSubtask(subtask);
        Subtask subtask2 = new Subtask("Подзадача2", "Описание подзадачи2", epicId, Status.NEW);
        int subtaskId2 = inMemoryTaskManager.addNewSubtask(subtask2);
        Subtask subtask3 = new Subtask("Подзадача3", "Описание подзадачи3", epicId, Status.NEW);
        int subtaskId3 = inMemoryTaskManager.addNewSubtask(subtask3);
        Subtask subtask4 = new Subtask("Подзадача4", "Описание подзадачи4", epicId, Status.NEW);
        int subtaskId4 = inMemoryTaskManager.addNewSubtask(subtask4);

        List<Subtask> inputSubtasks = new ArrayList<>(List.of(subtask, subtask2, subtask3, subtask4));

        assertEquals(inputSubtasks, inMemoryTaskManager.getSubtasksFromEpicById(epicId));

        inMemoryTaskManager.deleteAllEpics();

        // проверка извлечения списка сабтасок несуществующего эпика
        assertNull(inMemoryTaskManager.getSubtasksFromEpicById(epicId));
    }

    @Test
    void updateEpicStatusTest() {
        // проверка логики работы смены статуса эпиков
        Epic epic = new Epic("Эпик", "Описание эпика");
        int epicId = inMemoryTaskManager.addNewEpic(epic);

        assertEquals(NEW, inMemoryTaskManager.getEpic(epicId).getStatus());

        Subtask subtask = new Subtask("Подзадача", "Описание подзадачи", epicId, Status.NEW);
        int subtaskId = inMemoryTaskManager.addNewSubtask(subtask);

        assertEquals(NEW, inMemoryTaskManager.getEpic(epicId).getStatus());

        Subtask subtask2 = new Subtask("Подзадача2", "Описание подзадачи2", epicId, Status.DONE);
        int subtaskId2 = inMemoryTaskManager.addNewSubtask(subtask2);

        assertEquals(IN_PROGRESS, inMemoryTaskManager.getEpic(epicId).getStatus());

        inMemoryTaskManager.deleteSubtaskById(subtaskId2);

        assertEquals(NEW, inMemoryTaskManager.getEpic(epicId).getStatus());

        Subtask subtask3 = new Subtask("Подзадача3", "Описание подзадачи3", epicId, IN_PROGRESS);
        int subtaskId3 = inMemoryTaskManager.addNewSubtask(subtask3);

        assertEquals(IN_PROGRESS, inMemoryTaskManager.getEpic(epicId).getStatus());

        inMemoryTaskManager.deleteSubtaskById(subtaskId3);

        assertEquals(NEW, inMemoryTaskManager.getEpic(epicId).getStatus());

        Subtask subtask4 = new Subtask("Подзадача4", "Описание подзадачи4", epicId, Status.DONE);
        int subtaskId4 = inMemoryTaskManager.addNewSubtask(subtask4);

        subtask = new Subtask("Подзадача", "Описание подзадачи", epicId, Status.DONE);
        subtask.setId(subtaskId);
        inMemoryTaskManager.updateSubtask(subtask);

        assertEquals(DONE, inMemoryTaskManager.getEpic(epicId).getStatus());

        subtask = new Subtask("Подзадача", "Описание подзадачи", epicId, IN_PROGRESS);
        subtask.setId(subtaskId);
        inMemoryTaskManager.updateSubtask(subtask);

        assertEquals(IN_PROGRESS, inMemoryTaskManager.getEpic(epicId).getStatus());

        inMemoryTaskManager.deleteAllSubtasks();

        assertEquals(NEW, inMemoryTaskManager.getEpic(epicId).getStatus());
    }
}
