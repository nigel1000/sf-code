package cn.sf.project.controller.redis;

import cn.sf.bean.beans.Response;
import cn.sf.project.controller.redis.out.CacheInfo;
import cn.sf.redis.client.JedisClientUtil;
import cn.sf.redis.client.JedisManagerUtil;
import cn.sf.redis.enums.BizRedisEnum;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 缓存管理
 */
@Controller
@RequestMapping("/redis")
@Slf4j
public class JedisManagerController {


	@Autowired
	private JedisClientUtil jedisClientUtil;
	@Autowired
	private JedisManagerUtil jedisManagerUtil;

	//http://127.0.0.1:9091/redis/listKeyHashCache?level=true
	@RequestMapping(value = "/listKeyHashCache", method = RequestMethod.GET)
	public String listKeyHashCache(
			Model model,
			@RequestParam(value = "level", required = false) Boolean level,
			@RequestParam(value = "showObj", defaultValue = "false") Boolean showObj){

		List<CacheInfo> cacheInfos = Lists.newArrayList();

		for (BizRedisEnum item : BizRedisEnum.values()) {
			if(!item.equals(BizRedisEnum.NULL)) {
				Set<String> keys = jedisManagerUtil.keys(item.getGroupName()+"*");
				for(String key : keys){
					Map<String,Object> fieldMap = jedisManagerUtil.hGetAll(key);
					if(!CollectionUtils.isEmpty(fieldMap)){
						CacheInfo cacheInfo = new CacheInfo();
						cacheInfo.setCount(fieldMap.size());
						cacheInfo.setKey(key);
						cacheInfo.setExpireTime(item.getExpireTime());
						cacheInfo.setTtl(jedisManagerUtil.ttl(key));
						cacheInfo.setUrl("/redis/clearKeyHash?key=" + key);
						fieldMap.forEach((field,obj)->{
							try {
								cacheInfo.addFieldInfo(
										field,
										"/redis/clearKeyHash?key=" + key + "&field=" + URLEncoder.encode(field,"utf-8"),
										showObj?JSONObject.toJSONString(obj):"");
							} catch (UnsupportedEncodingException e) {
								e.printStackTrace();
							}

						});
						cacheInfos.add(cacheInfo);
					}
				}
			}
		}
		if(cacheInfos.size()>0) {
			model.addAttribute("cacheInfos", cacheInfos);
		}
		model.addAttribute("level",level);
		model.addAttribute("showObj",showObj);

		return "pages/redis/KeyHashCacheList";
	}

	//http://127.0.0.1:9091/redis/clearKeyHash?groupName=zcy_default&redirect=true&level=true
	@RequestMapping(value = "/clearKeyHash", method = RequestMethod.GET)
	public @ResponseBody Response<Boolean> clearKeyHashByKey(
			HttpServletResponse response,
			@RequestParam(value = "key") String key,
			@RequestParam(value = "field", required = false) String field,
			@RequestParam(value = "redirect" , required = false) String redirect,
			@RequestParam(value = "level" , required = false) String level
	){
		Long count = jedisClientUtil.hDel(key,field);
		if(count>0) {
			if ("true".equals(redirect)) {
				this.redirctToListKeyHashCache(response, level);
			}
		}
		return Response.ok(count>0);
	}

	private void redirctToListKeyHashCache(HttpServletResponse response,String level) {
		try {
			response.sendRedirect("/redis/listKeyHashCache?level="+level);
		} catch (IOException e) {
			log.error("redirect err", e);
		}
	}
}