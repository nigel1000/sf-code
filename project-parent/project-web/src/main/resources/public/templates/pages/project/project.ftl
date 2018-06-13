<html>
<head>
  <title>项目详细信息</title>
</head>
<body>

<#if project?exists>
<br/>
项目ID:<#if project.id?exists>${project.id}</#if>
<br/>
项目名称:<#if project.projectName?exists>${project.projectName}</#if>
</#if>

<br/>

</body>
</html> 