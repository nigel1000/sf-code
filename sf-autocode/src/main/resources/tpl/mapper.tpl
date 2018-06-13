<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE mapper PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN" "http://mybatis.org/dtd/mybatis-3-mapper.dtd" >
<mapper namespace="${daoPackage}.${className}${daoType}">

    <resultMap id="${className}Map" type="${domainPackage}.${className}">
    <#list mapperVos as vo>
        <result column="${vo.dbName}" property="${vo.javaName}"/>
    </#list>
    </resultMap>

    <sql id="tb">
       ${tableName}
    </sql>

    <sql id="cols_all">
    <#list mapperVos as vo>
       ${vo.dbName}<#if vo_has_next>,</#if>
    </#list>
    </sql>

    <sql id="vals">
    <#list mapperVos as vo>
    <#if vo.javaName == 'id'>
        <if test="id != null ">${"#"}{id},</if>
        <if test="id == null ">null,</if>
    <#else>
        <#if dateNowValList?seq_contains(vo.javaName)>now()<#else>${"#"}{${vo.javaName}}</#if><#if vo_has_next>,</#if>
    </#if>
    </#list>
    </sql>

    <sql id="vals_list">
        (
    <#list mapperVos as vo>
    <#if vo.javaName == 'id'>
        <if test="item.id != null ">${"#"}{item.id},</if>
        <if test="item.id == null ">null,</if>
    <#else>
        <#if dateNowValList?seq_contains(vo.javaName)>now()<#else>${"#"}{item.${vo.javaName}}</#if><#if vo_has_next>,</#if>
    </#if>
    </#list>
        )
    </sql>

    <sql id="dynamic_condition">
    <#list mapperVos as vo>
    <#if dynamicCondList?seq_contains(vo.javaName)>
    <#else>
        <if test="${vo.javaName} != null ">AND ${vo.dbName} = ${"#"}{${vo.javaName}}</if>
        <#if vo.type == 'String'>
        <if test="${vo.javaName}Like != null and ${vo.javaName}Like != '' ">AND ${vo.dbName} like concat('%', ${"#"}{${vo.javaName}Like},'%')</if>
        </#if>
    </#if>
    </#list>
    </sql>


    <sql id="set">
    <#list mapperVos as vo>
    <#if dynamicCondList?seq_contains(vo.javaName)>
    <#else>
        <if test="${vo.javaName} !=null">,${vo.dbName} = ${"#"}{${vo.javaName}}</if>
    </#if>
    </#list>
    </sql>

    <#if mapperIdList?seq_contains("create")>
    <insert id="create" parameterType="${domainPackage}.${className}" keyProperty="id" useGeneratedKeys="true">
        INSERT INTO
        <include refid="tb"/>
        (
        <include refid="cols_all"/>
        )
        VALUES
        (
        <include refid="vals"/>
        )
    </insert>
    </#if>
    <#if mapperIdList?seq_contains("creates")>

    <insert id="creates" parameterType="list">
        INSERT INTO
        <include refid="tb"/>
        (
        <include refid="cols_all"/>
        )
        VALUES
        <foreach collection="list" item="item" index="index" separator=",">
            <include refid="vals_list"/>
        </foreach>
    </insert>
    </#if>
    <#if mapperIdList?seq_contains("update")>

    <update id="update" parameterType="${domainPackage}.${className}">
        UPDATE
        <include refid="tb"/>
        <set>
            updated_at=now()
            <include refid="set"/>
        </set>
        WHERE id=${"#"}{id}
    </update>
    </#if>
    <#if mapperIdList?seq_contains("paging")>

    <select id="paging" parameterType="map" resultMap="BusMap">
        SELECT
        <include refid="cols_all"/>
        FROM
        <include refid="tb"/>
        WHERE 1=1
        <include refid="dynamic_condition"/>
        LIMIT ${"#"}{offset}, ${"#"}{limit}
    </select>
    </#if>
    <#if mapperIdList?seq_contains("count")>

    <select id="count" parameterType="map" resultType="long">
        SELECT
        count(1)
        FROM
        <include refid="tb"/>
        WHERE 1=1
        <include refid="dynamic_condition"/>
    </select>
    </#if>
    <#if mapperIdList?seq_contains("load")>

    <select id="load" parameterType="long" resultMap="BusMap">
        SELECT
        <include refid="cols_all" />
        FROM
        <include refid="tb" />
        WHERE id = ${"#"}{id}
    </select>
    </#if>
    <#if mapperIdList?seq_contains("loads")>

    <select id="loads" resultMap="BusMap" parameterType="list">
       SELECT
           <include refid="cols_all"/>
           FROM
           <include refid="tb"/>
       WHERE 1=1
       AND id IN
       <foreach collection="list" item="item" index="index" open="(" separator="," close=")">
           ${"#"}{item}
       </foreach>
    </select>
   </#if>
    <#if mapperIdList?seq_contains("list")>

    <select id="list" parameterType="map" resultMap="BusMap">
        SELECT
        <include refid="cols_all"/>
        FROM
        <include refid="tb"/>
        WHERE 1=1
        <include refid="dynamic_condition"/>
    </select>
    </#if>
    <#if mapperIdList?seq_contains("delete")>

    <delete id="delete" parameterType="long">
        DELETE FROM
        <include refid="tb"/>
        WHERE id = ${"#"}{id}
    </delete>
    </#if>
    <#if mapperIdList?seq_contains("deletes")>

    <delete id="deletes" parameterType="list">
        DELETE FROM
        <include refid="tb"/>
        WHERE id IN
        <foreach collection="list" index="index" item="id" open="(" separator="," close=")">
            ${"#"}{id}
        </foreach>
    </delete>
    </#if>
</mapper>
