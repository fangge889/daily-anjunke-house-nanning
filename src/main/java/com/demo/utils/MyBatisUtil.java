package com.demo.utils;

import java.io.InputStream;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

public class MyBatisUtil {
	private static String resource = "mybatis.xml";
	private static InputStream is = MyBatisUtil.class.getClassLoader().getResourceAsStream(resource);
	private static SqlSessionFactory factory = new SqlSessionFactoryBuilder().build(is);
    /**
     * 获取SqlSessionFactory
     * @return SqlSessionFactory
     */
//    public static SqlSessionFactory getSqlSessionFactory() {
//        String resource = "mybatis.xml";
//        InputStream is = MyBatisUtil.class.getClassLoader().getResourceAsStream(resource);
//        SqlSessionFactory factory = new SqlSessionFactoryBuilder().build(is);
//        return factory;
//    }
    /**
     * 获取SqlSession
     * @return SqlSession
     */
    public static SqlSession getSqlSession() {
        return factory.openSession();
    }
    
    /**
     * 获取SqlSession
     * @param isAutoCommit 
     *         true 表示创建的SqlSession对象在执行完SQL之后会自动提交事务
     *         false 表示创建的SqlSession对象在执行完SQL之后不会自动提交事务，这时就需要我们手动调用sqlSession.commit()提交事务
     * @return SqlSession
     */
    public static SqlSession getSqlSession(boolean isAutoCommit) {
        return factory.openSession(isAutoCommit);
    }
}
