import models.LiftRide;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.exceptions.JedisException;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class JedisMain {

    //the jedis connection pool..
    private static JedisPool pool = null;

    public JedisMain(String redisHost, Integer redisPort) {
        //configure our pool connection
        pool = new JedisPool(redisHost, redisPort);

    }

    public void addSets() {
        //let us first add some data in our redis server using Redis SET.
        String key = "a";
        LiftRide liftRide = new LiftRide("a", "a","a", "a", "a","a");
        LiftRide liftRide2 = new LiftRide("b", "b","b", "b", "b","b");

        //get a jedis connection jedis connection pool
        Jedis jedis = pool.getResource();
        try {
            //save to redis
            jedis.sadd(key, liftRide.toString(), liftRide2.toString());

            //after saving the data, lets retrieve them to be sure that it has really added in redis
            Set<String> members = jedis.smembers(key);
            for (String member : members) {
                System.out.println(member);
            }
        } catch (JedisException e) {
            //if something wrong happen, return it back to the pool
            if (null != jedis)
                pool.returnBrokenResource(jedis);
                jedis = null;

        } finally {
            ///it's important to return the Jedis instance to the pool once you've finished using it
            if (null != jedis)
                pool.returnResource(jedis);
        }
    }

    public void addHash() {
        //add some values in Redis HASH
        String key = "javapointers";
        Map<String, String> map = new HashMap<>();
        map.put("name", "Java Pointers");
        map.put("domain", "www.javapointers.com");
        map.put("description", "Learn how to program in Java");

        Jedis jedis = pool.getResource();
        try {
            //save to redis
            jedis.hmset(key, map);
            jedis.hmset(key, map);

            //after saving the data, lets retrieve them to be sure that it has really added in redis
            Map<String, String> retrieveMap = jedis.hgetAll(key);
            for (String keyMap : retrieveMap.keySet()) {
                System.out.println(keyMap + " " + retrieveMap.get(keyMap));
            }

        } catch (JedisException e) {
            //if something wrong happen, return it back to the pool
            if (null != jedis) {
                pool.returnBrokenResource(jedis);
                jedis = null;
            }
        } finally {
            ///it's important to return the Jedis instance to the pool once you've finished using it
            if (null != jedis)
                pool.returnResource(jedis);
        }
    }

    public static void main(String[] args){
        JedisMain main = new JedisMain("18.237.204.48", 6379);
        main.addSets();
        main.addHash();
    }
}