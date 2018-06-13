<html>
<head>
    <title>登录页面</title>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
</head>
<body>
<div>用户登录表单</div>
<form name="frmLogin" action="/security/login" method="post">
    name: <input type="text" name="username"><br/>
    pass: <input type="password" name="password"><br/>
    <#if (RequestParameters.redirectURL)?exists>
            <input type="hidden" value="${RequestParameters.redirectURL}" name="redirectURL" >
    </#if>
    <input type="submit">
</form>
</body>
</html>