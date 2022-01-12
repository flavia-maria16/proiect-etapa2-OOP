package nicelist.rounds;

import enums.Category;
import enums.Cities;
import enums.ElvesType;
import gifts.DeliverPresents;
import gifts.assignment.AssignmentStrategy;
import gifts.assignment.IdStrategy;
import gifts.elves.Elf;
import gifts.elves.SantaWorkshop;
import input.AnnualChange;
import input.ChildUpdate;
import gifts.Gifts;
import input.InputData;
import nicelist.Child;
import nicelist.ages.AgeRangeFactory;
import nicelist.ages.YoungAdult;

import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;


public final class YearlyRound extends AnnualChildren {
    public void makeNiceList(final List<Child> childrenList, final AnnualChange change) {
        for (Child child : childrenList) {
            calculateAge(child.getAge() + 1, child, change);
        }
    }

    public void calculateAge(final int age, final Child child, final AnnualChange change) {
        Child niceChild = AgeRangeFactory.chooseRange(getAgeRange(age), child);
        niceChild.setAge(niceChild.getAge() + 1);
        checkIfYoungAdult(niceChild, change);
    }

    public void checkIfYoungAdult(final Child niceChild, final AnnualChange change) {
        if (!(niceChild instanceof YoungAdult)) {
            // Update the nice score history and gifts preferences based on children's ids
            childrenUpdate(niceChild, change);
            niceChild.calculateAverageScore();
            niceChild.roundAverageScore();
            getChildren().add(niceChild);
        }
    }

    public void childrenUpdate(final Child child, final AnnualChange change) {
        for (ChildUpdate childUpdate : change.getChildrenUpdates()) {
            if (child.getId() == childUpdate.getId()) {
                child.setElf(childUpdate.getElf());
                if (childUpdate.getNiceScore() != null) {
                    child.getNiceScoreHistory().add(childUpdate.getNiceScore());
                }
                if (!childUpdate.getGiftsPreferences().isEmpty()) {
                    updatePreferences(child.getGiftsPreferences(),
                            childUpdate.getGiftsPreferences());
                }
                break;
            }
        }
    }

    public void updatePreferences(final List<Category> oldGiftsPreferences,
                                  final List<Category> newGiftsPreferences) {
        List<Category> newList = new LinkedList<>(new LinkedHashSet<>(newGiftsPreferences));
        for (Category category : oldGiftsPreferences) {
            if (!newList.contains(category)) {
                newList.add(category);
            }
        }
        oldGiftsPreferences.clear();
        oldGiftsPreferences.addAll(newList);
    }

    public void receiveGifts(final int year, final InputData input) {
        updateSantaGiftList(year, input);

        Double santaBudget = input.getAnnualChanges().get(year).getNewSantaBudget();
        Double budgetUnit = DeliverPresents.calculateBudgetUnit(santaBudget, getChildren());
        for (Child child : getChildren()) {
            child.setAssignedBudget(child.getAverageScore() * budgetUnit);
            Elf elf = chooseElf(child.getElf(), child, input.getInitialData().getSantaGiftsList());
            SantaWorkshop workshop = new SantaWorkshop();
            if (!child.getElf().equals(ElvesType.YELLOW)) {
                workshop.work(elf);
            }
        }
        AssignmentStrategy strategy = chooseStrategy(input.getAnnualChanges().get(year).getStrategy(),
                input.getInitialData().getSantaGiftsList(), getChildren());
                strategy.assignGifts();
    }

    private void updateSantaGiftList(final int year, final InputData input) {
        List<Gifts> santaGifts = new LinkedList<>();
        santaGifts.addAll(input.getInitialData().getSantaGiftsList());
        santaGifts.addAll(input.getAnnualChanges().get(year).getNewGifts());
        List<Gifts> santaGiftsAux = new LinkedList<>();
        santaGiftsAux.addAll(santaGifts);
        input.getInitialData().setSantaGiftsList(santaGifts);
    }
}
