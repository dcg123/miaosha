高并发实现:<br>
=====
##用到的技术<br>
![](https://github.com/dcg123/miaosha/blob/master/src/main/resources/static/img/technical.png)<br>
##实现的模块<br>
 ![](https://github.com/dcg123/miaosha/blob/master/src/main/resources/static/img/function.png)<br>
##集成redis:<br>

  >>1：添加redis依赖,添加Fastjson依赖（用于序列化）<br> 
  >>2:加载redis配置文件(RedisConfig 使用@ConfigurationProperties来读取配置文件)<br> 
  >>3:通过RedisConfig获取JedisPool池 吧JedisPool注入到spring容器中 供RedisService使用<br> 
  >>4:为了防止多人开发项目下key被别人覆盖（同意使用前缀 不同模块使用不同的前缀 比如说用户模块使用用户模块的前缀）<br>
    >> 通过key封装结构：实现类-》抽象类-》接口<br> 
##两次MD5：<br> 

  >>1：用户端：PASS=MD5(明文+固定+Salt)<br> 
  >>2: 服务端：PASS=MD5(用户输入+随机Salt)<br> 
  
##JSR303参数校验+全局异常处理器<br> 
  >>JSR303参数校验(自定义注解进行手机号格式验证)\<br> 
  >>定义全局异常处理某一类异常从而能够减少代码重复率和复杂度（@ExceptionHandler）<br> 
  >>@ExceptionHandler：统一处理某一类异常，从而能够减少代码重复率和复杂度<br> 
  >>@ControllerAdvice：异常集中处理，更好的使业务逻辑与异常处理剥离开<br> 
  >>@ResponseStatus：可以将某种异常映射为HTTP状态码<br> 
  
##redis分布式session:<br> 
  >>通过UUIDUtil获取随机数作为token  吧唯一token作为用户的唯一标识符保存到redis中 通过Cookie吧token返回给客户端<br> 
  >>通过重写WebMvcConfigurerAdapter下的方法 在进入到Controller前在redis中获取用户信息 这样减少代码的冗余 在需要用户信息的方法中注入用户实体类<br> 
  
##页面高并发优化<br> 
  >>页面缓存 <br> 
  >>解决超卖(数据库增加唯一索引：防止用户重复购买,sql加库存数量判断：防止库存边负数)<br> 
  >>秒杀静态化<br> 
  >>对象缓存<br> 
##接口优化<br>
  >>Redis 预减库存减少对数据库访问<br>
  >>内存标记减少Redis<br>
  >>请求先入队缓冲（RabbitMQ） 异步下单 增强用户体验<br>
  >>nginx水平扩展<br>
  >>分库分表中间件(Mycat)<br>
  >>思路：减少数据库访问<br>
  >>1:系统初始化，吧商品库存数量加载到Redis<br>
  >>2:收到请求 Redis预减库存，库存不足，直接返回，否则进入3<br>
  >>3:请求入队，立即返回排队中<br>
  >>4:请求出队，生成订单，减少库存<br>
  >>5:客户端轮询，是否秒杀成功<br>
  
  