package nicelist.rounds;

import common.Constants;
import enums.CityStrategyEnum;
import enums.ElvesType;

import gifts.Gifts;
import gifts.assignment.AssignmentStrategy;
import gifts.assignment.IdStrategy;
import gifts.assignment.NiceScoreCityStrategy;
import gifts.assignment.NiceScoreStrategy;
import gifts.elves.Santa;
import gifts.elves.BlackElf;
import gifts.elves.Elf;
import gifts.elves.PinkElf;
import gifts.elves.WhiteElf;

import input.InputData;
import nicelist.Child;
import nicelist.ages.AgeRangeFactory;

import java.util.ArrayList;
import java.util.List;

public abstract class AnnualChildren {
    private List<Child> children = new ArrayList<>();

    public final List<Child> getChildren() {
        return children;
    }

    public final void setChildren(final List<Child> children) {
        this.children = children;
    }

    /**
     * This method is used to identify which age range fits to a child
     *
     * @param age the age of a child used to identify their age category
     */
    public static AgeRangeFactory.AgeRange getAgeRange(final int age) {
        if (age < Constants.BABY_AGE_LIMIT) {
            return AgeRangeFactory.AgeRange.Baby;
        } else if (age >= Constants.BABY_AGE_LIMIT && age < Constants.KID_AGE_LIMIT) {
            return AgeRangeFactory.AgeRange.Kid;
        } else if (age >= Constants.KID_AGE_LIMIT && age <= Constants.TEEN_AGE_LIMIT) {
            return AgeRangeFactory.AgeRange.Teen;
        }
        return AgeRangeFactory.AgeRange.YoungAdult;
    }

    public final void assignBudget(final Double budgetUnit, final InputData input) {
        for (Child child : getChildren()) {
            child.setAssignedBudget(child.getAverageScore() * budgetUnit);
            if (!child.getElf().equals(ElvesType.YELLOW)) {
                child = doElvesJob(child.getElf(), child,
                        input.getInitialData().getSantaGiftsList());
            }
        }
    }

    private static Child doElvesJob(final ElvesType elf, final Child child,
                                    final List<Gifts> giftsList) {
        Santa santa = new Santa();
        switch (elf) {
            case PINK: return santa.talkToElves(new PinkElf(new Elf(child, giftsList)));
            case BLACK: return santa.talkToElves(new BlackElf(new Elf(child, giftsList)));
            default: return santa.talkToElves(new WhiteElf(new Elf(child, giftsList)));
        }
    }

    public static AssignmentStrategy chooseStrategy(final CityStrategyEnum strategy,
                                                    final List<Gifts> giftsList,
                                                    final List<Child> childrenList) {
        switch (strategy) {
            case NICE_SCORE: return new NiceScoreStrategy(childrenList, giftsList);
            case NICE_SCORE_CITY: return new NiceScoreCityStrategy(childrenList,
                    giftsList);
            default: return new IdStrategy(childrenList, giftsList);
        }
    }
}
