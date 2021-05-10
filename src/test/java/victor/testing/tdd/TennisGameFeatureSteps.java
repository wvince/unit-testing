package victor.testing.tdd;

import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

import static org.junit.Assert.assertEquals;

public class TennisGameFeatureSteps {
   private TennisGame tennisGame;

   @Given("^A new tennis game$")
   public void a_new_tennis_game() throws Throwable {
      tennisGame = new TennisGame();
   }

   @Then("^Score is \"([^\"]*)\"$")
   public void score_is(String expected) throws Throwable {
      assertEquals(expected, tennisGame.getScore());
   }


   @When("^Player \"([^\"]*)\" scores$")
   public void playerScores(TennisParty party) throws Throwable {
      tennisGame.addPoint(party);
   }

   @When("^Player \"([^\"]*)\" scores (\\d+) points$")
   public void playerScoresPoints(TennisParty party, int points) throws Throwable {
      for (int i = 0; i < points; i++) {
         tennisGame.addPoint(party);

      }
   }
}
