#{if session.username && controllers.security.Security.invoke("check", _arg)}
    #{doBody /}
#{/if}
