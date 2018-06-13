<html>
<head>
  <title>cache区域块列表</title>
</head>
<body>
<#if cacheInfos?exists>
<br/><font color="red">缓存类型:  jedis key Hash</font>
<br/>
<#list cacheInfos as keys>
    <br/>
    Cache Map Key:${keys.key}
    <br/>
    缓存时间:${keys.expireTime}秒         剩余时间:${keys.ttl}秒
<br/>
    拥有元素个数:${keys.count}
    <a href="${keys.url}&redirect=true<#if level>&level=${level?c}</#if>">删除</a>
    <br/>
    ----------------------------------------------------------------------------
    <#if level>
      <#list keys.fieldInfos as field>
       <br/>
       Field Key:${field.field}
       <#if showObj>
           <br/>
           Field Value:${field.objJson}
       </#if>
       <br/>
       <a href="${field.url}&redirect=true<#if level>&level=${level?c}</#if>">删除</a>
       <br/>
       </#list>
     <p>
     ---------------------------------------------------------------------------
    </#if>
</#list> 
</#if>

<br/>

</body>
</html> 