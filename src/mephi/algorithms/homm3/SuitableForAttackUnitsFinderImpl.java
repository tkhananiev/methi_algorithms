package mephi.algorithms.homm3;

import com.battle.heroes.army.Unit;
import com.battle.heroes.army.programs.SuitableForAttackUnitsFinder;

import java.util.*;

public class SuitableForAttackUnitsFinderImpl implements SuitableForAttackUnitsFinder {

    /**
     * Метод определяет список юнитов, подходящих для атаки, для атакующего юнита одной из армий.
     * Сложность: O(n * m), где n — количество юнитов в ряду, m — количество рядов (фиксированное значение 3).
     *
     * @param unitsByRow      трехслойный массив юнитов противника
     * @param isLeftArmyTarget параметр, указывающий, юниты какой армии подвергаются атаке
     * @return список юнитов, подходящих для атаки
     */
    @Override
    public List<Unit> getSuitableUnits(List<List<Unit>> unitsByRow, boolean isLeftArmyTarget) {
        List<Unit> suitableUnits = new ArrayList<>();

        // Проходим по каждой строке (ряду) юнитов (O(m))
        for (List<Unit> row : unitsByRow) {
            suitableUnits.addAll(findSuitableUnitsInRow(row, isLeftArmyTarget));
        }

        return suitableUnits;
    }

    /**
     * Поиск подходящих юнитов в строке.
     * Сложность: O(n), где n — количество юнитов в строке.
     *
     * @param row             строка юнитов
     * @param isLeftArmyTarget параметр, указывающий, юниты какой армии подвергаются атаке
     * @return список подходящих юнитов в строке
     */
    private List<Unit> findSuitableUnitsInRow(List<Unit> row, boolean isLeftArmyTarget) {
        List<Unit> suitableUnits = new ArrayList<>();

        // Проходим по каждому юниту в строке (O(n))
        for (int index = 0; index < row.size(); index++) {
            Unit unit = row.get(index);

            // Проверяем, является ли юнит живым и подходящим для атаки
            if (unit != null && unit.isAlive() &&
                    (isLeftArmyTarget ? isRightmostUnit(row, index) : isLeftmostUnit(row, index))) {
                suitableUnits.add(unit);
            }
        }

        return suitableUnits;
    }

    /**
     * Проверка, является ли юнит самым правым в ряду.
     * Сложность: O(k), где k — количество юнитов справа от текущего юнита.
     *
     * @param row       строка юнитов
     * @param unitIndex индекс текущего юнита
     * @return true, если юнит самый правый в ряду, иначе false
     */
    private boolean isRightmostUnit(List<Unit> row, int unitIndex) {
        return unitIndex == row.size() - 1 || row.subList(unitIndex + 1, row.size()).stream().allMatch(Objects::isNull);
    }

    /**
     * Проверка, является ли юнит самым левым в ряду.
     * Сложность: O(k), где k — количество юнитов слева от текущего юнита.
     *
     * @param row       строка юнитов
     * @param unitIndex индекс текущего юнита
     * @return true, если юнит самый левый в ряду, иначе false
     */
    private boolean isLeftmostUnit(List<Unit> row, int unitIndex) {
        return unitIndex == 0 || row.subList(0, unitIndex).stream().allMatch(Objects::isNull);
    }
}
