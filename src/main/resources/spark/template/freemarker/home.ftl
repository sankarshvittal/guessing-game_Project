<!DOCTYPE html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"></meta>
    <title>${title}</title>
    <link rel="stylesheet" type="text/css" href="/styles/main.css">
</head>
<body>
    <h1>${title}</h1>
    
    <div class="body">
    
      <h2>Application Stats</h2>

        <h3>Global Satistics:</h3>
        <p>

        ${totalGamesUsers}<br/>
        ${totalWinsUsers}  <br/>
        ${gobalAverage}
            <br/><br/>
        </p>

        <h3>User Satistics:</h3>

      <p>
        ${gameStatsMessage}
      </p>


<#if newSession>
    <p>
        No game stats yet.
        <br/><br/>
    </p>
<#else>
        <p>
        ${totalWins}
            <br/><br/>
        </p>
</#if>





      <#if newSession>
        <p>
          <a href="/game">Want to play a game?!?</a>
        </p>
      <#else>
        <#if youWon>
          <p>
            Congratulations!  You must have read my mind.
            <br/><br/>
            <a href="/game">Do it again</a>
          </p>
        <#else>
          <p>
            Aww, too bad.  Better luck next time.
            <br/><br/>
            <a href="/game">How about it?</a>
          </p>
        </#if>
      </#if>
    
    </div>
  </div>
</body>
</html>
