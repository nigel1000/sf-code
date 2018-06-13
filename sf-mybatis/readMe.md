
注意点：
启动时禁止DataSource和DataSourceTransactionManager的加载，已集成注册事务管理器
@EnableAutoConfiguration(exclude = {
        DataSourceAutoConfiguration.class,
        DataSourceTransactionManagerAutoConfiguration.class
})
多数据源同时操作只能保证一个数据源事务安全，见TransactionTest.transactional





