package mephi.algorithms.homm3;


import com.battle.heroes.army.Army;
import com.battle.heroes.army.Unit;
import com.battle.heroes.army.programs.GeneratePreset;

import java.util.*;

public class GeneratePresetImpl implements GeneratePreset {

    // Сохраняем отсортированный список юнитов для оптимизации повторных вызовов метода generate
    private List<Unit> sortedUnits = null;

    /**
     * Метод generate формирует пресет армии компьютера.
     * Сложность: O(n * m), где n — общее число типов юнитов, m — максимальное число юнитов одного типа.
     *
     * @param unitList  список юнитов, содержит объект юнита каждого типа
     * @param maxPoints максимальное число очков для всех юнитов армии
     * @return объект армии компьютера со списком юнитов внутри неё
     */
    @Override
    public Army generate(List<Unit> unitList, int maxPoints) {
        // Если отсортированный список еще не создан, создаем его (O(n log n))
        if (sortedUnits == null) {
            sortUnitsByEffectiveness(unitList);
            sortedUnits = new ArrayList<>(unitList); // Сохраняем отсортированный список (O(n))
        }

        Army computerArmy = new Army();
        List<Unit> selectedUnits = new ArrayList<>();
        int currentPoints = 0;

        // Проходим по каждому типу юнита и добавляем их в армию (O(n * m))
        for (Unit unit : sortedUnits) {
            int unitsToAdd = calculateMaxUnitsToAdd(unit, maxPoints, currentPoints);
            if (unitsToAdd > 0) {
                addUnitsToArmy(unit, unitsToAdd, selectedUnits);
                currentPoints += unitsToAdd * unit.getCost();
            }
        }

        // Присваиваем координаты каждому юниту (O(m))
        assignCoordinates(selectedUnits);

        // Устанавливаем список юнитов и суммарные очки в армии
        computerArmy.setUnits(selectedUnits);
        computerArmy.setPoints(currentPoints);
        return computerArmy;
    }

    /**
     * Сортируем юнитов по их эффективности (атака + здоровье / стоимость).
     * Сложность: O(n log n), где n — количество юнитов.
     *
     * @param units список юнитов для сортировки
     */
    private void sortUnitsByEffectiveness(List<Unit> units) {
        // Кэшируем значения эффективности для каждого юнита (O(n))
        Map<Unit, Double> effectivenessCache = new HashMap<>();
        for (Unit unit : units) {
            double effectiveness = (double) (unit.getBaseAttack() + unit.getHealth()) / unit.getCost();
            effectivenessCache.put(unit, effectiveness);
        }

        // Сортируем юнитов по кэшированным значениям эффективности (O(n log n))
        units.sort(Comparator.comparingDouble(effectivenessCache::get).reversed());
    }

    /**
     * Вычисляем максимальное количество юнитов, которые можно добавить в армию.
     * Сложность: O(1)
     *
     * @param unit         текущий юнит
     * @param maxPoints    максимальное число очков для всех юнитов армии
     * @param currentPoints текущие очки армии
     * @return количество юнитов, которое можно добавить
     */
    private int calculateMaxUnitsToAdd(Unit unit, int maxPoints, int currentPoints) {
        // Ограничиваем количество добавляемых юнитов до 11
        return Math.min(11, (maxPoints - currentPoints) / unit.getCost());
    }

    /**
     * Добавляем юниты в армию.
     * Сложность: O(unitsToAdd), где unitsToAdd — количество добавляемых юнитов.
     *
     * @param unit          текущий юнит
     * @param unitsToAdd    количество юнитов для добавления
     * @param selectedUnits список уже выбранных юнитов
     */
    private void addUnitsToArmy(Unit unit, int unitsToAdd, List<Unit> selectedUnits) {
        for (int i = 0; i < unitsToAdd; i++) {
            Unit newUnit = createNewUnit(unit, i);
            selectedUnits.add(newUnit);
        }
    }

    /**
     * Создаем новый юнит с уникальным именем.
     * Сложность: O(1)
     *
     * @param unit   текущий юнит
     * @param index  индекс нового юнита
     * @return новый юнит с уникальным именем
     */
    private Unit createNewUnit(Unit unit, int index) {
        Unit newUnit = new Unit(unit.getName(), unit.getUnitType(), unit.getHealth(),
                unit.getBaseAttack(), unit.getCost(), unit.getAttackType(),
                unit.getAttackBonuses(), unit.getDefenceBonuses(), -1, -1);
        newUnit.setName(unit.getUnitType() + " " + index);
        return newUnit;
    }

    /**
     * Присваиваем случайные координаты юнитам.
     * Сложность: O(m), где m — количество юнитов.
     *
     * @param units список юнитов для присвоения координат
     */
    private void assignCoordinates(List<Unit> units) {
        Set<String> occupiedCoords = new HashSet<>();
        Random random = new Random();

        // Присваиваем координаты каждому юниту
        for (Unit unit : units) {
            int coordX, coordY;
            do {
                coordX = random.nextInt(3);
                coordY = random.nextInt(21);
            } while (occupiedCoords.contains(coordX + "," + coordY));
            occupiedCoords.add(coordX + "," + coordY);
            unit.setxCoordinate(coordX);
            unit.setyCoordinate(coordY);
        }
    }
}
