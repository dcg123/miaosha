集成redis:
  1：添加redis依赖,添加Fastjson依赖（用于序列化）
  2:加载redis配置文件(RedisConfig 使用@ConfigurationProperties来读取配置文件)
  3:通过RedisConfig获取JedisPool池 吧JedisPool注入到spring容器中 供RedisService使用
  4:为了防止多人开发项目下key被别人覆盖（同意使用前缀 不同模块使用不同的前缀 比如说用户模块使用用户模块的前缀）
     通过key封装结构：实现类-》抽象类-》接口
两次MD5：
  1：用户端：PASS=MD5(明文+固定+Salt)
  2: 服务端：PASS=MD5(用户输入+随机Salt)
  
JSR303参数校验+全局异常处理器
  JSR303参数校验(自定义注解进行手机号格式验证)
  定义全局异常处理某一类异常从而能够减少代码重复率和复杂度（@ExceptionHandler）
  @ExceptionHandler：统一处理某一类异常，从而能够减少代码重复率和复杂度
  @ControllerAdvice：异常集中处理，更好的使业务逻辑与异常处理剥离开
  @ResponseStatus：可以将某种异常映射为HTTP状态码
  