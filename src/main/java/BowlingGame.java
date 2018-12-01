import java.util.*;
import java.util.stream.Collectors;

/**
 * A bowling game which can play maximum 11 frames to have a perfect game.
 * Its function is simply to roll balls and retrieve scores so far, which at last is the final score.
 */
public class BowlingGame {

    //internal helper class that holds information on which frame should be rewarded with what bonus
    class BonusInfo {
        Bonus bonus;
        int rewardFrame;

        public BonusInfo(Bonus b, int frame) {
            this.bonus = b;
            this.rewardFrame = frame;
        }
    }

    //constants
    public static final int TOTAL_FRAMES = 11;
    public static final int TOTAL_PINS = 10;

    //first or second ball in a frame
    enum Rolling {
        FIRST, SECOND
    }

    //represent bonus type
    enum Bonus {
        SPARE, STRIKE
    }

    //running totalScore
    private int totalScore;
    private boolean isGameOver = false;
    private int frameNo;//frame number, starting form 1
    private Rolling rollingInFrame;//first or second rolling in a frame
    //future task for reward bonus back to a frame. key is round of rolling a bonus is ready for calculation
    private Map<Integer, BonusInfo> bonusBack = new HashMap<>();
    //log of rolling scores
    private List<Integer> rollings = new ArrayList<>();
    //score for each frame
    private int[] frameScore = new int[TOTAL_FRAMES - 1];

    public BowlingGame() {
        this.resetGame();
    }

    /**
     * initialize game or clean the status of game
     */
    public void resetGame() {
        isGameOver = false;
        totalScore = 0;
        frameNo = 1;
        rollingInFrame = Rolling.FIRST;
        bonusBack.clear();
        rollings.clear();
        Arrays.fill(frameScore, 0);
    }

    /**
     * check if game is over
     *
     * @return
     */
    public boolean isGameOver() {
        return isGameOver;
    }

    /**
     * roll a ball
     *
     * @param pins how many pins have been knocked down
     * @return
     */
    public void roll(int pins) {
        if (isGameOver) {
            return;
        }
        //record score in each rolling
        rollings.add(pins);
        int numRolls = rollings.size();
        //for first 10 frames
        if (frameNo < TOTAL_FRAMES) {
            //strike
            if (rollingInFrame == Rolling.FIRST && pins == TOTAL_PINS) {
                //at 10th, it can still win bonus
                bonusBack.put(numRolls + 2, new BonusInfo(Bonus.STRIKE, frameNo - 1));
                frameScore[frameNo - 1] = TOTAL_PINS;
                totalScore += TOTAL_PINS;
                frameNo++;
                rollingInFrame = Rolling.FIRST;
            }
            //spare
            else if (rollingInFrame == Rolling.SECOND
                    && pins + rollings.get(rollings.size() - 2) == TOTAL_PINS) {
                bonusBack.put(numRolls + 1, new BonusInfo(Bonus.SPARE, frameNo - 1));
                frameScore[frameNo - 1] = TOTAL_PINS;//reassign frame score
                totalScore += pins;//first part has been added in
                frameNo++;
                rollingInFrame = Rolling.FIRST;
            }
            //normal rolling
            else {
                if (rollingInFrame == Rolling.FIRST) {
                    frameScore[frameNo - 1] = pins;
                    totalScore += pins;
                    rollingInFrame = Rolling.SECOND;
                } else if (rollingInFrame == Rolling.SECOND) {
                    frameScore[frameNo - 1] += pins;
                    totalScore += pins;
                    rollingInFrame = Rolling.FIRST;
                    if (frameNo < TOTAL_FRAMES) {
                        frameNo++;
                        //after 10th, bonus is empty, game is over
                        if (frameNo == TOTAL_FRAMES && bonusBack.isEmpty()) {
                            isGameOver = true;
                        }
                    }
                }
            }
        }

        //process rewards
        if (bonusBack.containsKey(numRolls)) {
            BonusInfo bi = bonusBack.get(numRolls);
            int bonusValue = 0;
            if (bi.bonus == Bonus.STRIKE) {
                bonusValue = rollings.get(numRolls - 1) + rollings.get(numRolls - 2);
                frameScore[bi.rewardFrame] += bonusValue;
                totalScore += bonusValue;
            } else {//this is SPARE
                bonusValue = rollings.get(numRolls - 1);
                frameScore[bi.rewardFrame] += bonusValue;
                totalScore += bonusValue;
            }
            bonusBack.remove(numRolls);//we are clear
        }
        //now a chance to end game
        if (frameNo >= TOTAL_FRAMES && bonusBack.isEmpty()) {
            isGameOver = true;
        }
    }

    /**
     * get current known total score
     *
     * @return
     */
    public int getScore() {
        return totalScore;
    }

    /**
     * get score of each frame
     *
     * @return
     */
    public int[] getFrameScores() {
        return frameScore;
    }

    /**
     * A calculation based on all rolling results. This is what asked in assignment.
     * It assumes the input is valid.
     *
     * @param rollings score of each rolling
     * @return
     */
    public static int getTotalScore(int[] rollings) {
        if (rollings == null || rollings.length == 0) return 0;
        int frame = 1, totalScore = 0;
        boolean firstRoll = true;
        //last frame is for a perfect game, it only collects points, no more scores to collect after that
        for (int i = 0; i < rollings.length && frame <= TOTAL_FRAMES - 1; i++) {
            //strick
            if (firstRoll && rollings[i] == TOTAL_PINS) {
//                if (i + 1 >= rollings.length || i + 2 >= rollings.length) {
//                    throw new RuntimeException("Invalid Data");
//                }
                totalScore += TOTAL_PINS + (i + 2 < rollings.length ? ((i + 1 < rollings.length ? rollings[i + 1] : 0) + rollings[i + 2]) : 0);
                firstRoll = true;//go next frame
                frame++;
            }
            //spare
            else if (!firstRoll && rollings[i - 1] + rollings[i] == 10) {
                totalScore += TOTAL_PINS + rollings[i + 1];
                frame++;
                firstRoll = true;
            }
            //none perfect rollings
            else {
                //only adding to final score when it is at second ball
                if (!firstRoll) {
                    totalScore += rollings[i - 1] + rollings[i];
                    frame++;
                }
                firstRoll = !firstRoll;
            }
        }
        return totalScore;
    }

    public static void main(String[] args) {
        BowlingGame game = new BowlingGame();
        //Purely calculation
        int[] rolls = new int[]{9, 1, 0, 10, 10, 10, 6, 2, 7, 3, 8, 2, 10, 9, 0, 10, 10, 8};
        System.out.println("First demo, the assignment:");
        System.out.println("For this sequence of rolls:"+Arrays.stream(rolls).boxed().collect(Collectors.toList()));
        System.out.println("Total Score is:"+getTotalScore(rolls));
        //live play
        System.out.println("\nSecond demo, the fun part:");
        System.out.println("Now let's play a game and update the score board.");
        System.out.println(getTotalScore(rolls));
        for (int i : rolls) {
            System.out.println("Roll a ball, scored "+i+".");
            game.roll(i);
            System.out.println("Total Scored so far:"+game.getScore());
            System.out.println("Scored Board:"+Arrays.stream(game.getFrameScores()).boxed().collect(Collectors.toList()));
            System.out.println();
        }
    }
}