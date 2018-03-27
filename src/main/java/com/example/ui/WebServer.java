package com.example.ui;

import static spark.Spark.get;
import static spark.Spark.post;
import static spark.SparkBase.staticFileLocation;

import java.util.Objects;

import com.example.appl.GameCenter;

import spark.TemplateEngine;



public class WebServer {

  //
  // Constants
  //

  /**
   * The URL pattern to request the Home page.
   */
  public static final String HOME_URL = "/";

  /**
   * The URL pattern to request the Game page.
   */
  public static final String GAME_URL = "/game";

  //
  // Attributes
  //

  private final GameCenter gameCenter;
  private final TemplateEngine templateEngine;

  //
  // Constructor
  //

  /**
   * The constructor for the Web Server.
   *
   * @param gameCenter
   *    The {@link GameCenter} for the application.
   * @param templateEngine
   *    The default {@link TemplateEngine} to render views.
   */
  public WebServer(
      final GameCenter gameCenter,
      final TemplateEngine templateEngine) {
    // validation
    Objects.requireNonNull(gameCenter, "gameCenter must not be null");
    Objects.requireNonNull(templateEngine, "templateEngine must not be null");
    //
    this.gameCenter = gameCenter;
    this.templateEngine = templateEngine;
  }

  //
  // Public methods
  //

  /**
   * Initialize all of the HTTP routes that make up this web application.
   *
   * <p>
   * Initialization of the web server includes defining the location for static
   * files, and defining all routes for processing client requests. The method
   * returns after the web server finishes its initialization.
   * </p>
   */
  public void initialize() {
    // Configuration to serve static files
    staticFileLocation("/public");

    //// Setting any route (or filter) in Spark triggers initialization of the
    //// embedded Jetty web server.

    //// A route is set for a request verb by specifying the path for the
    //// request, and the function callback (request, response) -> {} to
    //// process the request. The order that the routes are defined is
    //// important. The first route (request-path combination) that matches
    //// is the one which is invoked. Additional documentation is at
    //// http://sparkjava.com/documentation.html and in Spark tutorials.

    //// Each route (processing function) will check if the request is valid
    //// from the client that made the request. If it is valid, the route
    //// will extract the relevant data from the request and pass it to the
    //// application object delegated with executing the request. When the
    //// delegate completes execution of the request, the route will create
    //// the parameter map that the response template needs. The data will
    //// either be in the value the delegate returns to the route after
    //// executing the request, or the route will query other application
    //// objects for the data needed.

    //// FreeMarker defines the HTML response using templates. Additional
    //// documentation is at
    //// http://freemarker.org/docs/dgui_quickstart_template.html.
    //// The Spark FreeMarkerEngine lets you pass variable values to the
    //// template via a map. Additional information is in online
    //// tutorials such as
    //// http://benjamindparrish.azurewebsites.net/adding-freemarker-to-java-spark/.

    //// These route definitions are examples. You will define the routes
    //// that are appropriate for the HTTP client interface that you define.
    //// Create separate Route classes to handle each route; this keeps your
    //// code clean; using small classes.

    // Shows Home page.
    get(HOME_URL, new GetHomeRoute(gameCenter), templateEngine);

    // Shows Game page.
    get(GAME_URL, new GetGameRoute(gameCenter), templateEngine);

    // Post a guess.
    post("/guess", new PostGuessRoute(gameCenter), templateEngine);
  }

}
