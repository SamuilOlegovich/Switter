<#import "parts/common.ftlh" as c>
<#import "parts/login.ftlh" as l>

<br>
Login page<br>
<@c.page>
<@l.login "/login" /><br>
<a href="/registration">Add new user</a>
</@c.page>