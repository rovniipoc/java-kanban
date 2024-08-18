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
        return new ArrayList<>(prioritizedTasks);
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
            updateEpic(epic.getId());
        }

        subtasks.clear();
    }

    @Override
    public Task getTask(int id) throws NotFoundException {
        if (tasks.containsKey(id)) {
            final Task task = tasks.get(id);
            historyManager.add(task);
            return task;
        } else {
            throw new NotFoundException("Задача с id = " + id + " не найдена.");
        }

//        final Task task = tasks.get(id);
//        historyManager.add(task);
//        return task;
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
    public int addNewTask(Task task) throws TaskValidationException {
        // Проверяем не пересекается ли по времени задача с другой задачей или подзадачей.
        // Если пересекается, то выкидываем исключение.
        taskValidate(task);
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
        // Если пересекается, то выкидываем исключение.
        taskValidate(subtask);
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
        updateEpic(epicId);

        return id;
    }

    @Override
    public void updateTask(Task task) throws NotFoundException, TaskValidationException {
        int id = task.getId();
        Task savedTask = tasks.get(id);

        if (!tasks.containsKey(id)) {
            throw new NotFoundException("Задача с id = " + id + " не найдена.");
        }

        // Проверяем не пересекается ли по времени задача с другой задачей или подзадачей.
        // Если пересекается, то выкидываем исключение. Предварительно удаляем из prioritizedTasks
        // старый экземпляр
        prioritizedTasks.remove(savedTask);
        taskValidate(task);

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
        int id = subtask.getId();
        // Проверяем не пересекается ли по времени подзадача с другой задачей или подзадачей.
        // Если пересекается, то выкидываем исключение. Предварительно удаляем из prioritizedTasks
        // старый экземпляр
        prioritizedTasks.remove(subtasks.get(id));
        taskValidate(subtask);
        // Проверяем имеет ли обновленная подзадача временной интервал.
        // Если да, то добавляем в коллекцию задач, отсортированных по приоритету.
        if (subtask.getStartTime() != null) {
            prioritizedTasks.add(subtask);
        }

        subtasks.replace(subtask.getId(), subtask);
        updateEpic(subtask.getEpicId());
    }

    @Override
    public void deleteTaskById(int id) {
        Task task = tasks.remove(id);
        if (task == null) {
            return;
        }
        historyManager.remove(id);
        prioritizedTasks.remove(task);
    }

    @Override
    public void deleteEpicById(int id) {
        List<Integer> subtaskIds = epics.get(id).getSubtaskIds();
        for (Integer subtaskId : subtaskIds) {
            Subtask subtask = subtasks.remove(subtaskId);
            prioritizedTasks.remove(subtask);
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
        updateEpic(epic.getId());
        historyManager.remove(id);
        prioritizedTasks.remove(subtask);
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

    @Override
    public void taskValidate(Task task) throws TaskValidationException {
        if (task.getStartTime() == null || task.getEndTime() == null) {
            return;
        }

        LocalDateTime start1 = task.getStartTime();
        LocalDateTime end1 = task.getEndTime();
        LocalDateTime start2;
        LocalDateTime end2;

        for (Task prioritizedTask : prioritizedTasks) {
            start2 = prioritizedTask.getStartTime();
            end2 = prioritizedTask.getEndTime();

            if ((start1.isAfter(start2) && start1.isBefore(end2))
                    || (end1.isAfter(start2) && end1.isBefore(end2))
                    || (start2.isAfter(start1) && start2.isBefore(end1))
                    || (end2.isAfter(start1) && end2.isBefore(end1))
                    || (start1.isEqual(start2) && end2.isEqual(end2))) {
                throw new TaskValidationException("Задача пересекается с id=" + prioritizedTask.getId()
                        + " (с " + prioritizedTask.getStartTime() + " по " + prioritizedTask.getEndTime() + ")");
            }
        }
    }

    protected void updateEpic(int epicId) {
        updateEpicStatus(epicId);
        updateEpicDuration(epicId);
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
            epic.setEndTime();
        }
    }
}
