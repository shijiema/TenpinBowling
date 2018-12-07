package learning.services;

import org.junit.Before;
import org.junit.Test;
import learning.services.BowlingGame;

import static org.junit.Assert.*;

public class BowlingGameTest {
    BowlingGame game;
    @Before
    public void setup(){
        game = new BowlingGame();
    }
    @Test
    public void playWorstGame() {
        int rolls[] = new int[]{0,0,0,0,0,0,0,0,0,0};
        int expectedScore = 0;
        assertEquals(BowlingGame.getTotalScore(rolls),expectedScore);
    }
    @Test
    public void playPerfectGame() {
        int rolls[] = new int[]{10,10,10,10,10,10,10,10,10,10,10,10};
        int expectedScore = 300;
        assertEquals(BowlingGame.getTotalScore(rolls),expectedScore);
    }
    @Test
    public void getTotalScore() {
        int rolls[] = new int[]{8,2,0,10,10,10,6,2,7,3,8,2,10,9,0,10,10,5};
        int expectedScore = 173;
        assertEquals(BowlingGame.getTotalScore(rolls),expectedScore);
    }
    @Test
    public void getTotalScoreAfterPlayingGame() {
        int rolls[] = new int[]{8,2,0,10,10,10,6,2,7,3,8,2,10,9,0,10,10,5};
        int expectedScore = 173;
        for (int i : rolls) {
            game.roll(i);
        }
        assertEquals(game.getScore(),expectedScore);
    }
    @Test
    public void verifyRunningScores() {
        game.roll(1);
        game.roll(2);
        assertEquals(game.getScore(),3);
        game.roll(4);
        game.roll(1);
        assertEquals(game.getScore(),8);
        game.roll(10);
        game.roll(2);
        game.roll(5);
        assertEquals(game.getScore(),32);
        assertFalse(game.isGameOver());
        game.roll(2);
        game.roll(8);
        game.roll(3);
        assertEquals(game.getScore(),48);
        game.roll(6);
        assertEquals(game.getScore(),54);
        game.roll(5);
        game.roll(4);//63
        game.roll(2);
        game.roll(3);//68
        game.roll(6);
        game.roll(4);//83
        game.roll(5);
        game.roll(4);//92
        assertEquals(game.getScore(),92);
    }
}