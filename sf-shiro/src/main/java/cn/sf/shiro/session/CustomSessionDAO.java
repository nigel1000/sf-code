package cn.sf.shiro.session;

import cn.sf.redis.client.JedisClientUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.session.Session;
import org.apache.shiro.session.UnknownSessionException;
import org.apache.shiro.session.mgt.eis.AbstractSessionDAO;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.Serializable;
import java.util.Collection;

@Service
@Slf4j
public class CustomSessionDAO extends AbstractSessionDAO {

    //自行选择一个分布式存储方式
    @Resource
    private JedisClientUtil jedisClientUtil;

    private final String sessionPre = "shiro:session:";
    //30分钟失效
    private final int timeout = 30*60*60;

    @Override
    protected Serializable doCreate(Session session) {
        Serializable sessionId = generateSessionId(session);
        assignSessionId(session, sessionId);
        jedisClientUtil.setex(sessionPre +sessionId,session,timeout);
        return sessionId;
    }

    @Override
    protected Session doReadSession(Serializable sessionId) {
        Session session = null;
        if (sessionId != null) {
            session = (Session)jedisClientUtil.get(sessionPre +sessionId);
            if (session != null) {
                jedisClientUtil.expire(sessionPre +sessionId,timeout);
            }
        }
        return session;
    }
    @Override
    public void update(Session session) throws UnknownSessionException {
        if (session == null) {
            return;
        }
        jedisClientUtil.setex(sessionPre +session.getId(),session,timeout);
    }

    @Override
    public void delete(Session session) {
        System.out.println("删除session host=" + session.getHost()+";session id="+session.getId());
        jedisClientUtil.del(sessionPre +session.getId());
    }

    @Override
    public Collection<Session> getActiveSessions() {
        return null;
    }
}