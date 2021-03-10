<#import "parts/common.ftlh" as c>
<#import "parts/login.ftlh" as l>
<br>
Add new user<br>
<@c.page>
${message?ifExists}
<@l.login "/registration" />
</@c.page>