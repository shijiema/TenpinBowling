# TenpinBowling
Ten-pin Bowling take home test from Boeing

## American Ten-Pin Bowling Scoring Rules
* The game consists of 10 frames.
* In each frame the player has two opportunities to knock down 10 pins. The score for the frame is the total number of pins knocked down,plus bonuses for strikes and spares.
* A spare is when the player knocks down all 10 pins in two tries. The bonus for that frame is the number of pins knocked down by the next roll. So in frame 3 above, the score is 10 (the total number knocked down) plus a bonus of 5 (the number of pins knocked down on the next roll.)
* A strike is when the player knocks down all 10 pins on his first try. The bonus for that frame is the value of the next two balls rolled.
* In the tenth frame a player who rolls a spare or strike is allowed to roll the extra balls to complete the frame. However no more than three balls can be rolled in tenth frame.

## Out of scope
Here are some things that the program does not need to do:
* We will not check for valid rolls
* We will not check for correct number of rolls and frames
* We will not provide scores for intermediate frames

## The Solution
    Solutions are provided in a Java class called BowlingGame.

    Two solutions are provided, one is for assignment. It is defined as a class method, which takes in an array
    of rolling result, and calculates out the total score.

    The second solution is not part of assignment, but rather a development for fun. It mimics a real bowling game,
    so it allows rolling the ball one by one and displays a score board for the known scores after a roll.

    The two solution uses different approaches for figuring out total scores. The first one is simple because
    it can use future data. The second one is more challenging because it has more states to deal with as ball
    rolls. I tried to make the second solution a little simpler by introducing scheduled events so that the
    algorithm is closer to human experience thus easier to understand.

### Solution for assignment
```Java
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
```
### Solution for fun: realtime play and score update
```Java
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
```
## Checkout the project with git
* Open command line window
* Go to a folder, such as /tmp
* checkout git repository by
    * git clone https://github.com/shijiema/TenpinBowling.git

## Build project
* Change to TenpinBowing folder
    * cd /tmp/TenpinBowling
* Execute gradle build
    * ./gradlew build

## See a demo
Assume java is available in searching path
* Run the demo by issuing this command
    * java -cp ./build/libs/TenpinBowling-1.0-SNAPSHOT.jar BowlingGame
    * below is the output

> First demo, the assignment:
> For this sequence of rolls: {9, 1, 0, 10, 10, 10, 6, 2, 7, 3, 8, 2, 10, 9, 0, 10, 10, 8}
> Total Score is:176

    Second demo, the fun part:
    Now let's play a game and update the score board.

    Roll a ball, scored 9
    Scored so far:9
    Scored Board:[9, 0, 0, 0, 0, 0, 0, 0, 0, 0]

    Roll a ball, scored 1
    Scored so far:10
    Scored Board:[10, 0, 0, 0, 0, 0, 0, 0, 0, 0]

    Roll a ball, scored 0
    Scored so far:10
    Scored Board:[10, 0, 0, 0, 0, 0, 0, 0, 0, 0]

    Roll a ball, scored 10
    Scored so far:20
    Scored Board:[10, 10, 0, 0, 0, 0, 0, 0, 0, 0]

    Roll a ball, scored 10
    Scored so far:40
    Scored Board:[10, 20, 10, 0, 0, 0, 0, 0, 0, 0]

    Roll a ball, scored 10
    Scored so far:50
    Scored Board:[10, 20, 10, 10, 0, 0, 0, 0, 0, 0]

    Roll a ball, scored 6
    Scored so far:72
    Scored Board:[10, 20, 26, 10, 6, 0, 0, 0, 0, 0]

    Roll a ball, scored 2
    Scored so far:82
    Scored Board:[10, 20, 26, 18, 8, 0, 0, 0, 0, 0]

    Roll a ball, scored 7
    Scored so far:89
    Scored Board:[10, 20, 26, 18, 8, 7, 0, 0, 0, 0]

    Roll a ball, scored 3
    Scored so far:92
    Scored Board:[10, 20, 26, 18, 8, 10, 0, 0, 0, 0]

    Roll a ball, scored 8
    Scored so far:108
    Scored Board:[10, 20, 26, 18, 8, 18, 8, 0, 0, 0]

    Roll a ball, scored 2
    Scored so far:110
    Scored Board:[10, 20, 26, 18, 8, 18, 10, 0, 0, 0]

    Roll a ball, scored 10
    Scored so far:130
    Scored Board:[10, 20, 26, 18, 8, 18, 20, 10, 0, 0]

    Roll a ball, scored 9
    Scored so far:139
    Scored Board:[10, 20, 26, 18, 8, 18, 20, 10, 9, 0]

    Roll a ball, scored 0
    Scored so far:148
    Scored Board:[10, 20, 26, 18, 8, 18, 20, 19, 9, 0]

    Roll a ball, scored 10
    Scored so far:158
    Scored Board:[10, 20, 26, 18, 8, 18, 20, 19, 9, 10]

    Roll a ball, scored 10
    Scored so far:158
    Scored Board:[10, 20, 26, 18, 8, 18, 20, 19, 9, 10]

    Roll a ball, scored 8
    Scored so far:176
    Scored Board:[10, 20, 26, 18, 8, 18, 20, 19, 9, 28]

