package ru.yandex.javacource.brykalov.schedule.manager;

import ru.yandex.javacource.brykalov.schedule.task.Epic;
import ru.yandex.javacource.brykalov.schedule.task.Status;
import ru.yandex.javacource.brykalov.schedule.task.Subtask;
import ru.yandex.javacource.brykalov.schedule.task.Task;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

public class InMemoryTaskManager implements TaskManager {
    protected final Map<Integer, Task> tasks = new HashMap<>();
    protected final Map<Integer, Epic> epics = new HashMap<>();
    protected final Map<Integer, Subtask> subtasks = new HashMap<>();
    protected final Set<Task> prioritizedTasks = new TreeSet<>(Comparator.comparing(Task::getStartTime));
    protected int tasksCounter = 0;
    protected final HistoryManager historyManager = Managers.getDefaultHistory();

    @Override
    public List<Task> getTaskList() {
        return new ArrayList<>(tasks.values());
    }

    @Override
    public List<Epic> getEpicList() {
        return new ArrayList<>(epics.values());
    }

    @Override
    public List<Subtask> getSubtaskList() {
        return new ArrayList<>(subtasks.values());
    }

    @Override
    public List<Task> getPrioritizedTasks() {
        return prioritizedTasks.stream().toList();
    }

    @Override
    public void deleteAllTasks() {
        for (Task task : tasks.values()) {
            historyManager.remove(task.getId());
            if (task.getStartTime() != null) {
                prioritizedTasks.remove(task);
            }
        }

        tasks.clear();

        // Я не понимаю зачем менять for-each на stream: кода не становится меньше
        // и он становится более сложным для понимания как мне кажется...
//
//        tasks.values().stream()
//                .peek(task -> {
//                    if (task.getStartTime() != null) {
//                        prioritizedTasks.remove(task);
//                    }
//                })
//                .map(Task::getId)
//                .peek(historyManager::remove);
//
//        tasks.clear();
    }

    @Override
    public void deleteAllEpics() {
        for (Epic epic : epics.values()) {
            historyManager.remove(epic.getId());
        }
        for (Subtask subtask : subtasks.values()) {
            historyManager.remove(subtask.getId());
            if (subtask.getStartTime() != null) {
                prioritizedTasks.remove(subtask);
            }
        }

        epics.clear();
        subtasks.clear();
    }

    @Override
    public void deleteAllSubtasks() {
        for (Subtask subtask : subtasks.values()) {
            historyManager.remove(subtask.getId());
            if (subtask.getStartTime() != null) {
                prioritizedTasks.remove(subtask);
            }
        }
        for (Epic epic : epics.values()) {
            epic.clearSubtaskIds();
            updateEpicStatus(epic.getId());
            updateEpicDuration(epic.getId());
        }

        subtasks.clear();
    }

    @Override
    public Task getTask(int id) {
        final Task task = tasks.get(id);
        historyManager.add(task);
        return task;
    }

    @Override
    public Epic getEpic(int id) {
        final Epic epic = epics.get(id);
        historyManager.add(epic);
        return epic;
    }

    @Override
    public Subtask getSubtask(int id) {
        final Subtask subtask = subtasks.get(id);
        historyManager.add(subtask);
        return subtask;
    }

    @Override
    public int addNewTask(Task task) {
        // Проверяем не пересекается ли по времени задача с другой задачей или подзадачей.
        // Если пересекается, то задачу отбрасываем.
        if (getPrioritizedTasks().stream()
                .anyMatch(taskFromStream -> isOverlap(taskFromStream, task))) {
            return -1;
        }
        // Проверяем имеет ли задача временной интервал.
        // Если да, то добавляем в коллекцию задач, отсортированных по приоритету.
        if (task.getStartTime() != null) {
            prioritizedTasks.add(task);
        }

        int id = ++tasksCounter;
        task.setId(id);
        tasks.put(id, task);

        return id;
    }

    @Override
    public int addNewEpic(Epic epic) {
        int id = ++tasksCounter;
        epic.setId(id);
        epics.put(id, epic);
        return id;
    }

    @Override
    public Integer addNewSubtask(Subtask subtask) {
        // Проверяем не пересекается ли по времени подзадача с другой задачей или подзадачей.
        // Если пересекается, то подзадачу отбрасываем.
        if (getPrioritizedTasks().stream()
                .anyMatch(taskFromStream -> isOverlap(taskFromStream, subtask))) {
            return -1;
        }
        // Проверяем имеет ли подзадача временной интервал.
        // Если да, то добавляем в коллекцию задач, отсортированных по приоритету.
        if (subtask.getStartTime() != null) {
            prioritizedTasks.add(subtask);
        }

        int epicId = subtask.getEpicId();
        Epic epic = epics.get(epicId);
        if (epic == null) {
            return null;
        }
        int id = ++tasksCounter;
        subtask.setId(id);
        subtasks.put(id, subtask);
        epic.addSubtaskId(subtask.getId());
        updateEpicStatus(epicId);
        updateEpicDuration(epicId);

        return id;
    }

    @Override
    public void updateTask(Task task) {
        int id = task.getId();
        Task savedTask = tasks.get(id);
        if (savedTask == null) {
            return;
        }
        // Проверяем не пересекается ли по времени задача с другой задачей или подзадачей.
        // Если пересекается, то задачу отбрасываем (отклоняем обновление).
        if (getPrioritizedTasks().stream()
                .anyMatch(taskFromStream -> isOverlap(taskFromStream, task))) {
            return;
        }
        // Проверяем имеет ли обновленная задача временной интервал.
        // Если да, то добавляем в коллекцию задач, отсортированных по приоритету.
        if (task.getStartTime() != null) {
            prioritizedTasks.add(task);
        }

        tasks.put(id, task);
    }

    @Override
    public void updateEpic(Epic epic) {
        final Epic savedEpic = epics.get(epic.getId());
        if (savedEpic == null) {
            return;
        }
        epic.setSubtaskIds(savedEpic.getSubtaskIds());
        epic.setStatus(savedEpic.getStatus());
        epics.put(epic.getId(), epic);
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        if (subtask == null) {
            return;
        }
        if (epics.get(subtask.getEpicId()) == null) {
            return;
        }
        // Проверяем не пересекается ли по времени подзадача с другой задачей или подзадачей.
        // Если пересекается, то подзадачу отбрасываем (отклоняем обновление).
        if (getPrioritizedTasks().stream()
                .anyMatch(taskFromStream -> isOverlap(taskFromStream, subtask))) {
            return;
        }
        // Проверяем имеет ли обновленная подзадача временной интервал.
        // Если да, то добавляем в коллекцию задач, отсортированных по приоритету.
        if (subtask.getStartTime() != null) {
            prioritizedTasks.add(subtask);
        }

        subtasks.replace(subtask.getId(), subtask);
        updateEpicStatus(subtask.getEpicId());
        updateEpicDuration(subtask.getEpicId());
    }

    @Override
    public void deleteTaskById(int id) {
        tasks.remove(id);
        historyManager.remove(id);
        prioritizedTasks.remove(tasks.get(id));
    }

    @Override
    public void deleteEpicById(int id) {
        List<Integer> subtaskIds = epics.get(id).getSubtaskIds();
        for (Integer subtaskId : subtaskIds) {
            subtasks.remove(subtaskId);
            prioritizedTasks.remove(subtasks.get(subtaskId));
            historyManager.remove(subtaskId);
        }
        epics.remove(id);
        historyManager.remove(id);
    }

    @Override
    public void deleteSubtaskById(int id) {
        Subtask subtask = subtasks.remove(id);
        if (subtask == null) {
            return;
        }
        Epic epic = epics.get(subtask.getEpicId());
        epic.removeSubtask(id);
        updateEpicStatus(epic.getId());
        updateEpicDuration(epic.getId());
        historyManager.remove(id);
        prioritizedTasks.remove(subtasks.get(id));
    }

    @Override
    public List<Subtask> getSubtasksFromEpicById(int id) {
        Epic epic = epics.get(id);
        if (epic == null) {
            return null;
        }

        return epic.getSubtaskIds().stream()
                .map(subtasks::get)
                .toList();
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    public boolean isOverlap(Task task1, Task task2) {
        if (task1.getStartTime() == null || task1.getEndTime() == null
                || task2.getStartTime() == null || task2.getEndTime() == null) {
            return false;
        }

        LocalDateTime start1 = task1.getStartTime();
        LocalDateTime end1 = task1.getEndTime();
        LocalDateTime start2 = task2.getStartTime();
        LocalDateTime end2 = task2.getEndTime();

        return (start1.isAfter(start2) && start1.isBefore(end2))
                || (end1.isAfter(start2) && end1.isBefore(end2))
                || (start2.isAfter(start1) && start2.isBefore(end1))
                || (end2.isAfter(start1) && end2.isBefore(end1))
                || (start1.isEqual(start2) && end2.isEqual(end2));
    }

    private void updateEpicStatus(int epicId) {
        List<Integer> subtaskIds = epics.get(epicId).getSubtaskIds();
        int numberOfNewTask = 0;
        int numberOfCompletedTask = 0;

        for (Integer subtaskId : subtaskIds) {
            if (subtasks.get(subtaskId).getStatus() == Status.NEW) {
                numberOfNewTask++;
            }
            if (subtasks.get(subtaskId).getStatus() == Status.DONE) {
                numberOfCompletedTask++;
            }
        }

        if (!subtaskIds.isEmpty() && numberOfCompletedTask == subtaskIds.size()) {
            epics.get(epicId).setStatus(Status.DONE);
        } else if (subtaskIds.isEmpty() || (numberOfNewTask == subtaskIds.size())) {
            epics.get(epicId).setStatus(Status.NEW);
        } else {
            epics.get(epicId).setStatus(Status.IN_PROGRESS);
        }
    }

    private void updateEpicDuration(int epicId) {
        final Epic epic = epics.get(epicId);
        final List<Integer> subtaskIds = epic.getSubtaskIds();

        Optional<LocalDateTime> maybeStartTime = subtaskIds.stream().map(subtasks::get)
                .map(Task::getStartTime)
                .filter(Objects::nonNull)
                .min(LocalDateTime::compareTo);
        Optional<LocalDateTime> maybeEndTime = subtaskIds.stream().map(subtasks::get)
                .map(Task::getEndTime)
                .filter(Objects::nonNull)
                .max(LocalDateTime::compareTo);

        if (maybeStartTime.isPresent() && maybeEndTime.isPresent()) {
            epic.setStartTime(maybeStartTime.get());
            epic.setDuration(Duration.between(maybeStartTime.get(), maybeEndTime.get()));
        }
    }
}
