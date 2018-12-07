package learning.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import learning.services.BowlingGame;

@RestController
public class BowlingGameController {
    /**
     * http://localhost:8080/calculateTotalScore?rollings=10,10,10,10,10,10,10,10,10,10,10,10
     * @param rollings
     * @return
     */
    @RequestMapping(value="/calculateTotalScore",method= RequestMethod.GET, produces="application/json")
    public int calculateBowlingScore(@RequestParam(value="rollings") int rollings[]){
        return BowlingGame.getTotalScore(rollings);
    }
}
