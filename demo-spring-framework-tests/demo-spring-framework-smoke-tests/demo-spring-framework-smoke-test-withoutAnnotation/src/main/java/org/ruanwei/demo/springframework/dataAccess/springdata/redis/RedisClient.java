package org.ruanwei.demo.springframework.dataAccess.springdata.redis;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.Collections;
import java.util.Deque;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.dao.DataAccessException;
import org.springframework.data.geo.Point;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.connection.RedisClusterConnection;
import org.springframework.data.redis.connection.RedisClusterNode;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisNode;
import org.springframework.data.redis.connection.RedisSentinelConnection;
import org.springframework.data.redis.connection.StringRedisConnection;
import org.springframework.data.redis.core.BoundGeoOperations;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.BoundListOperations;
import org.springframework.data.redis.core.BoundSetOperations;
import org.springframework.data.redis.core.BoundValueOperations;
import org.springframework.data.redis.core.BoundZSetOperations;
import org.springframework.data.redis.core.ClusterOperations;
import org.springframework.data.redis.core.GeoOperations;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.HyperLogLogOperations;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.hash.BeanUtilsHashMapper;
import org.springframework.data.redis.hash.DecoratingStringHashMapper;
import org.springframework.data.redis.hash.HashMapper;
import org.springframework.data.redis.hash.ObjectHashMapper;
import org.springframework.data.redis.support.collections.DefaultRedisList;
import org.springframework.data.redis.support.collections.DefaultRedisMap;
import org.springframework.data.redis.support.collections.DefaultRedisSet;
import org.springframework.data.redis.support.collections.DefaultRedisZSet;
import org.springframework.scripting.support.ResourceScriptSource;

public class RedisClient {
	private static Log log = LogFactory.getLog(RedisClient.class);

	// is thread-safe
	private RedisTemplate<String, String> redisTemplate;
	private StringRedisTemplate stringRedisTemplate;

	// Key Type Operations
	private ValueOperations<String, String> valueOps;
	private ListOperations<String, String> listOps;
	private HashOperations<String, String, String> hashOps;
	private SetOperations<String, String> setOps;
	private ZSetOperations<String, String> zSetOps;
	private GeoOperations<String, String> geoOps;
	private HyperLogLogOperations<String, String> hyperLogLogOps;

	private ClusterOperations<String, String> clusterOps;

	// Key Bound Operations
	private BoundValueOperations<String, String> boundValueOps;
	private BoundListOperations<String, String> boundListOps;
	private BoundHashOperations<String, String, String> boundHashOps;
	private BoundSetOperations<String, String> boundSetOps;
	private BoundZSetOperations<String, String> boundZSetOps;
	private BoundGeoOperations<String, String> boundGeoOps;

	private List<String> list;
	private Queue<String> queue;
	private Deque<String> deque;
	private Map<String, String> map;
	private Set<String> set;
	private Set<String> zset;

	public void testRedis() {
		log.info("testRedis()");
		redisTemplate.opsForValue().set("myKey", "myValue");
		stringRedisTemplate.opsForValue().append("myKey", "-a");
		valueOps.append("myKey", "-b");
		boundValueOps.append("-c");

		listOps.leftPush("myList", "a");
		boundListOps.leftPush("b");

		setOps.add("mySet", "a");
		boundSetOps.add("b");

		zSetOps.add("myZSet", "a", 1);
		boundZSetOps.add("b", 1);

		geoOps.geoAdd("myGeo", new Point(1, 1), "ShenZhen");
		boundGeoOps.geoAdd(new Point(5, 5), "GuangZhou");

		hyperLogLogOps.add("myHll", "abc");

		hashOps.put("myHash", "name", "ruanwei");
		boundHashOps.put("age", "33");
	}

	public void testRedisHash() {
		log.info("testRedisHash()");

		hashOps.put("myHash", "name", "ruanwei");
		boundHashOps.put("age", "33");

		HashMapper<Object, byte[], byte[]> objectHashMapper = new ObjectHashMapper();
		HashMapper<Object, String, String> mapper = new DecoratingStringHashMapper<Object>(
				objectHashMapper);
		Settings settings = new Settings("ruanwei", 33);
		Map<String, String> mappedHash = mapper.toHash(settings);
		hashOps.putAll("myHash", mappedHash);
		Map<String, String> loadedHash = hashOps.entries("myHash");
		settings = (Settings) mapper.fromHash(loadedHash);
		log.info("user=" + settings);

		HashMapper<Settings, String, String> mapper2 = new BeanUtilsHashMapper<Settings>(
				Settings.class);
		Settings user2 = new Settings("ruanwei2", 33);
		Map<String, String> mappedHash2 = mapper2.toHash(user2);
		hashOps.putAll("myHash2", mappedHash2);
		Map<String, String> loadedHash2 = hashOps.entries("myHash2");
		user2 = (Settings) mapper2.fromHash(loadedHash2);
		log.info("user2=" + user2);
	}

	public void testRedisAdvanced() {
		log.info("testRedisAdvanced()");
		// native connection
		Long result = redisTemplate.execute(new RedisCallback<Long>() {
			@Override
			public Long doInRedis(RedisConnection connection)
					throws DataAccessException {
				connection.lPush("myList".getBytes(Charset.forName("UTF8")),
						"c".getBytes(Charset.forName("UTF8")));
				// if use stringRedisTemplate
				// ((StringRedisConnection) connection).lPush("myList", "d");
				return connection.dbSize();
			}
		});
		log.info("size of myList is " + result);

		// transaction
		Long txResults = redisTemplate.execute(new SessionCallback<Long>() {
			@Override
			public Long execute(RedisOperations operations)
					throws DataAccessException {
				operations.multi();
				operations.opsForList().leftPush("myList", "e");
				operations.exec();
				return operations.opsForList().size("myList");
			}
		});
		log.info("size of myList is " + txResults);

		// pipelining
		List<Object> results = stringRedisTemplate
				.executePipelined(new RedisCallback<Long>() {
					@Override
					public Long doInRedis(RedisConnection connection)
							throws DataAccessException {
						StringRedisConnection stringRedisConn = (StringRedisConnection) connection;
						for (int i = 0; i < 3; i++) {
							stringRedisConn.lPush("myList", "" + i);
						}
						return stringRedisConn.dbSize();
					}
				});
		log.info("size of resultList is " + results.size());

		// scripting
		DefaultRedisScript<Boolean> redisScript = new DefaultRedisScript<Boolean>();// 可以选择注入
		redisScript.setScriptSource(new ResourceScriptSource(
				new ClassPathResource("redis.lua")));
		redisScript.setResultType(Boolean.class);
		Boolean scriptResult = redisTemplate.execute(redisScript,
				Collections.singletonList("myList"), "a", "b");
		log.info("the result of the script is " + scriptResult);

		// messaging/PubSub
		String message = "message from publisher";
		byte[] msgBytes = message.getBytes(Charset.forName("UTF8"));
		byte[] chBytes = "myChannel".getBytes(Charset.forName("UTF8"));
		RedisConnection redisConnection = redisTemplate.getConnectionFactory()
				.getConnection();
		// blocking until canceled
		redisConnection.subscribe(new MessageListener() {
			@Override
			public void onMessage(Message message, byte[] pattern) {
				try {
					String body = new String(message.getBody(), "utf-8");
					String channel = new String(message.getChannel(), "utf-8");
					String p = new String(pattern, "utf-8");
					log.info("channel=" + channel + " body=" + body
							+ " pattern=" + p);
				} catch (UnsupportedEncodingException e) {
					log.error(e);
				}
			}
		}, chBytes);

		redisConnection.publish(chBytes, msgBytes);
		redisTemplate.convertAndSend("myChannel", message);
	}

	public void testRedisAsCollections() {
		log.info("testRedisAsCollections()");

		list.add("a");
		queue.add("b");
		deque.push("c");
		set.add("d");
		zset.add("e");
		map.put("f", "f");
	}

	public void testRedisClusterAndSentinel() {
		log.info("testRedisClusterAndSentinel()");

		RedisSentinelConnection sentinelConnection = redisTemplate
				.getConnectionFactory().getSentinelConnection();
		sentinelConnection.failover(new RedisNode("master", 123456));

		RedisClusterConnection clusterConnection = redisTemplate
				.getConnectionFactory().getClusterConnection();
		clusterConnection.shutdown();
		clusterOps.shutdown(new RedisClusterNode("host", 12345));
	}

	public void setRedisTemplate(RedisTemplate<String, String> redisTemplate) {
		this.redisTemplate = redisTemplate;

		this.list = new DefaultRedisList<String>("list", redisTemplate);
		this.queue = new DefaultRedisList<String>("queue", redisTemplate);
		this.deque = new DefaultRedisList<String>("deque", redisTemplate);
		this.map = new DefaultRedisMap<String, String>("map", redisTemplate);
		this.set = new DefaultRedisSet<String>("set", redisTemplate);
		this.zset = new DefaultRedisZSet<String>("zset", redisTemplate);

		this.valueOps = redisTemplate.opsForValue();
		this.listOps = redisTemplate.opsForList();
		this.setOps = redisTemplate.opsForSet();
		this.zSetOps = redisTemplate.opsForZSet();
		this.hashOps = redisTemplate.opsForHash();
		this.geoOps = redisTemplate.opsForGeo();
		this.hyperLogLogOps = redisTemplate.opsForHyperLogLog();

		this.clusterOps = redisTemplate.opsForCluster();

		this.boundValueOps = redisTemplate.boundValueOps("myKey");
		this.boundListOps = redisTemplate.boundListOps("myList");
		this.boundSetOps = redisTemplate.boundSetOps("mySet");
		this.boundZSetOps = redisTemplate.boundZSetOps("myZSet");
		this.boundHashOps = redisTemplate.boundHashOps("myHash");
		this.boundGeoOps = redisTemplate.boundGeoOps("myGeo");
	}

	public void setStringRedisTemplate(StringRedisTemplate stringRedisTemplate) {
		this.stringRedisTemplate = stringRedisTemplate;
	}
}
