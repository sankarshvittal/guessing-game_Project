package com.example.ui;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import spark.ModelAndView;
import spark.Request;
import spark.Response;
import spark.Session;
import spark.TemplateViewRoute;

import com.example.appl.GameCenter;
import com.example.model.GuessGame;

/**
 * The {@code POST /guess} route handler.
 *
 * @author <a href='mailto:bdbvse@rit.edu'>Bryan Basham</a>
 */
public class PostGuessRoute implements TemplateViewRoute {

  //
  // Constants
  //

  static final String GUESS_PARAM = "myGuess";
  static final String MESSAGE_ATTR = "message";
  static final String MESSAGE_TYPE_ATTR = "messageType";
  static final String YOU_WON_ATTR = "youWon";

  static final String ERROR_TYPE = "error";
  static final String BAD_GUESS = "Nope, try again...";
  static final String VIEW_NAME = "game_form.ftl";

  private int totalWins = 0;

  public int getTotalGamesUsers() {
    return totalGamesUsers;
  }

  public void setTotalGamesUsers(int totalGamesUsers) {
    this.totalGamesUsers = totalGamesUsers;
  }

  private int totalGamesUsers = 0;

  public int getTotoalWinsUsers() {
    return totoalWinsUsers;
  }

  public void setTotoalWinsUsers(int totoalWinsUsers) {
    this.totoalWinsUsers = totoalWinsUsers;
  }

  private int totoalWinsUsers = 0;

  //
  // Static methods
  //

  /**
   * Make an error message when the guess is not a number.
   */
  static String makeBadArgMessage(final String guessStr) {
    return String.format("You entered '%s' but that's not a number.", guessStr);
  }

  /**
   * Make an error message when the guess is not in the guessing range.
   */
  static String makeInvalidArgMessage(final String guessStr) {
    return String.format("You entered %s; make a guess between zero and nine.", guessStr);
  }

  //
  // Attributes
  //

  private final GameCenter gameCenter;

  //
  // Constructor
  //

  /**
   * The constructor for the {@code POST /guess} route handler.
   *
   * @param gameCenter The {@link GameCenter} for the application.
   * @throws NullPointerException when the {@code gameCenter} parameter is null
   */
  PostGuessRoute(final GameCenter gameCenter) {
    // validation
    Objects.requireNonNull(gameCenter, "gameCenter must not be null");
    //
    this.gameCenter = gameCenter;
  }

  //
  // TemplateViewRoute method
  //

  /**
   * {@inheritDoc}
   */
  @Override
  public ModelAndView handle(Request request, Response response) {
    // start building the View-Model
    final Map<String, Object> vm = new HashMap<>();
    vm.put(GetHomeRoute.TITLE_ATTR, GetGameRoute.TITLE);
    vm.put(GetHomeRoute.NEW_SESSION_ATTR, Boolean.FALSE);

    // retrieve the game object
    final Session session = request.session();
    final GuessGame game = gameCenter.get(session);
    vm.put(GetGameRoute.GAME_BEGINS_ATTR, game.isGameBeginning());
    vm.put(GetGameRoute.GUESSES_LEFT_ATTR, game.guessesLeft());

    // retrieve request parameter
    final String guessStr = request.queryParams(GUESS_PARAM);

    // convert the input
    int guess = -1;
    try {
      guess = Integer.parseInt(guessStr);
    } catch (NumberFormatException e) {
      // re-display the guess form with an error message
      return error(vm, makeBadArgMessage(guessStr));
    }

    // validate that the guess is in the range
    if (!game.isValidGuess(guess)) {
      // re-display the guess form with an error message
      return error(vm, makeInvalidArgMessage(guessStr));
    }

    // submit the guess to the game
    final boolean correct = game.makeGuess(guess);

    // Select the next View

    // did you win?
    if (correct) {
      return youWon(vm, session);
    }
    // no, but you have more guesses?
    else if (game.hasMoreGuesses()) {


      final boolean hint = game.getHint(guess);
      if (hint) {
        vm.put("hint", "the number is higher than your guess");
      } else {
        vm.put("hint", "the number is lower than your guess");
      }

      vm.put(GetGameRoute.GUESSES_LEFT_ATTR, game.guessesLeft());
      return error(vm, BAD_GUESS);
    }
    // otherwise, you lost
    else {
      return youLost(vm, session);
    }
  }

  //
  // Private methods
  //

  private ModelAndView error(final Map<String, Object> vm, final String message) {
    vm.put(MESSAGE_ATTR, message);
    vm.put(MESSAGE_TYPE_ATTR, ERROR_TYPE);
    return new ModelAndView(vm, VIEW_NAME);
  }

  private ModelAndView youWon(final Map<String, Object> vm, final Session session) {
    return endGame(true, vm, session);
  }

  private ModelAndView youLost(final Map<String, Object> vm, final Session session) {
    return endGame(false, vm, session);
  }

  private ModelAndView endGame(final boolean youWon, final Map<String, Object> vm, final Session session) {
    gameCenter.end(session);
    // report application-wide game statistics

    if (youWon) {
      totalWins++;
    }
    if (totalWins > 0 && gameCenter.getTotalGames() > 0) {
      int totalGames = gameCenter.getTotalGames();
      float result = (float) totalWins / (float) totalGames;
      result = result * 100;
      vm.put("totalWins", String.format(" You have won an average of %f percent of this session's %d games.", result, totalWins));
    }

    if (totalWins == 0) {
      vm.put("totalWins", "You have not won a game, yet. But I *feel* your luck changing.");
    }

    //file
    try {
      if (Files.exists(Paths.get("C:\\Guessing game\\guessingGame.txt"))) {
        byte[] bytes = Files.readAllBytes(Paths.get("C:\\Guessing game\\guessingGame.txt"));
        String[] values = new String(bytes).split("-");
        totalGamesUsers = Integer.parseInt(values[0]);
        totoalWinsUsers = Integer.parseInt(values[1]);
      }
      totalGamesUsers++;
      if (youWon) {
        totoalWinsUsers++;
      }
      String stats = new StringBuilder().append(totalGamesUsers).append("-").append(totoalWinsUsers).toString();
      Files.write(Paths.get("C:\\Guessing game\\guessingGame.txt"), stats.getBytes());
      float results = 0;
      if(totalGamesUsers != 0) {
         results = (float) totoalWinsUsers / (float) totalGamesUsers;
        results = results * 100;
      }
      vm.put("totalGamesUsers", String.format("Total number of games played by all the users is %d ",totalGamesUsers));
      vm.put("totalWinsUsers", String.format("Total number of wins among all the users is %d ",totoalWinsUsers));
      vm.put("gobalAverage", String.format("The global average is  %f percent",results));
    } catch (IOException e) {
      e.printStackTrace();
    }
    //
    vm.put(GetHomeRoute.GAME_STATS_MSG_ATTR, gameCenter.getGameStatsMessage());
    vm.put(YOU_WON_ATTR, youWon);
    return new ModelAndView(vm, GetHomeRoute.VIEW_NAME);
  }

}