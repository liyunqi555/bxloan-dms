package com.coamctech.bxloan.dms.service;

import java.util.Collections;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.hql.internal.ast.QueryTranslatorImpl;
import org.hibernate.hql.spi.QueryTranslator;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import com.coamctech.bxloan.dms.commons.base.BaseEntity;

@Repository
public class DynamicQuery {

	@PersistenceContext
	private EntityManager em;

	/**
	 * 从EntityManager获取Hibernate的Session
	 * 下面所有方式的实现都基于Hibernate
	 */
	private Session getHibernateSession() { 
		return em.unwrap(Session.class);
	}
	
	/**
	 * 获取Hibernate的SessionFactory对象
	 * @return
	 */
	private SessionFactory getHibernateSessionFactory() {
		return getHibernateSession().getSessionFactory();
	}
	
	// -====================================== jpql查询 ======================================-

	public List<?> query(String jpql, Object... params) {
		return createQuery(jpql, params).getResultList();
	}
	
	public <T> T querySingleResult(Class<T> resultClass, String jpql, Object...params) {
		return createTypedQuery(resultClass, jpql, params).getSingleResult();
	}
	
	public <T> List<T> query(Class<T> resultClass, String jpql, Object... params) {
		return createTypedQuery(resultClass, jpql, params).getResultList();
	}

	public List<?> queryPagingList(Pageable pageable, String jpql, Object... params) {
		Integer pageNumber = pageable.getPageNumber();
		Integer pageSize = pageable.getPageSize();
		Integer startPosition = pageNumber * pageSize;
		return createQuery(jpql, params).setFirstResult(startPosition).setMaxResults(pageSize).getResultList();
	}
	
	public <T> List<T> queryPagingList(Class<T> resultClass, Pageable pageable, String jpql, Object... params) {
		Integer pageNumber = pageable.getPageNumber();
		Integer pageSize = pageable.getPageSize();
		Integer startPosition = pageNumber * pageSize;
		return createTypedQuery(resultClass, jpql, params).setFirstResult(startPosition).setMaxResults(pageSize).getResultList();
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public Page query(Pageable pageable, String jpql, Object... params) {
		List<?> rows = queryPagingList(pageable, jpql, params);
		Long total = queryCount(jpql, params);
		return new PageImpl(rows, pageable, total);
	}
	
	public <T> Page<T> query(Class<T> resultClass, Pageable pageable, String jpql, Object... params) {
		List<T> rows = queryPagingList(resultClass, pageable, jpql, params);
		Long total = queryCount(jpql, params);
		return new PageImpl<T>(rows, pageable, total);
	}
	
	public Long queryCount(String jpql, Object... params) {
		jpql = StringUtils.substringBefore(jpql, "order by"); // 去掉order by, 提升执行效率
		
		// 去重和分组只能使用NativeSQL统计查询
		if (jpql.contains("distinct") || jpql.contains("group by")) {
			String countSql = generateCountSql(jpql);
			Object count = createNativeQuery(countSql, params).getSingleResult();
			return ((Number) count).longValue();
		} else { // 使用jpql统计查询
			String countJpql = generateCountJpql(jpql);
			return (Long) createQuery(countJpql, params).getSingleResult();
		}
	}
	
	public int executeUpdate(String jpql, Object... params) {
		return createQuery(jpql, params).executeUpdate();
	}
	
	
	
	// -====================================== 本地查询 ======================================-
	
	@SuppressWarnings("unchecked")
	public List<Object[]> nativeQuery(String nativeSql, Object... params) {
		return createNativeQuery(nativeSql, params).getResultList();
	}
	
	@SuppressWarnings("unchecked")
	public <T> List<T> nativeQuery(Class<T> resultClass, String nativeSql, Object... params) {
		return getNativeQuery(resultClass, nativeSql, params).getResultList();
	}
	
	@SuppressWarnings("unchecked")
	public <T> T nativeQuerySingleResult(Class<T> resultClass, String nativeSql, Object... params) {
		return (T) getNativeQuery(resultClass, nativeSql, params).getSingleResult();
	}

	@SuppressWarnings("unchecked")
	public List<Object[]> nativeQueryPagingList(Pageable pageable, String nativeSql, Object... params) {
		Integer pageNumber = pageable.getPageNumber();
		Integer pageSize = pageable.getPageSize();
		Integer startPosition = pageNumber * pageSize;
		return createNativeQuery(nativeSql, params).setFirstResult(startPosition).setMaxResults(pageSize).getResultList();
	}
	
	@SuppressWarnings("unchecked")
	public <T> List<T> nativeQueryPagingList(Class<T> resultClass, Pageable pageable, String nativeSql, Object... params) {
		Integer pageNumber = pageable.getPageNumber();
		Integer pageSize = pageable.getPageSize();
		Integer startPosition = pageNumber * pageSize;
		return getNativeQuery(resultClass, nativeSql, params).setFirstResult(startPosition).setMaxResults(pageSize).getResultList();
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public Page nativeQuery(Pageable pageable, String nativeSql, Object... params) {
		List<Object[]> rows = nativeQueryPagingList(pageable, nativeSql, params);
		Long total = nativeQueryCount(nativeSql, params);
		return new PageImpl(rows, pageable, total);
	}
	
	public <T> Page<T> nativeQuery(Class<T> resultClass, Pageable pageable, String nativeSql, Object... params) {
		List<T> rows = nativeQueryPagingList(resultClass, pageable, nativeSql, params);
		Long total = nativeQueryCount(nativeSql, params);
		return new PageImpl<T>(rows, pageable, total);
	}
	
	public Long nativeQueryCount(String nativeSql, Object... params) {
		nativeSql = StringUtils.substringBeforeLast(nativeSql, "order by"); // 去掉order by, 提升执行效率
		String countSql = "select count(*) from (" + nativeSql + ")";
		Object count = createNativeQuery(countSql, params).getSingleResult();
		return ((Number) count).longValue();
	}
	
	public int nativeExecuteUpdate(String nativeSql, Object... params) {
		return createNativeQuery(nativeSql, params).executeUpdate();
	}
	
	// -=================================== 其它 ===========================================-
	
	private Query createQuery(String jpql, Object... params) {
		Query q = em.createQuery(jpql);
		for (int i = 0; i < params.length; i++) {
			q.setParameter(i + 1, params[i]); // 与Hiberante不同,jpa query从位置1开始
		}
		return q;
	}
	
	private <T> TypedQuery<T> createTypedQuery(Class<T> resultClass, String jpql, Object... params) {
		TypedQuery<T> q = em.createQuery(jpql, resultClass);
		for (int i = 0; i < params.length; i++) {
			q.setParameter(i + 1, params[i]); // 与Hiberante不同,jpa query从位置1开始
		}
		return q;
	}
	
	private <T> Query getNativeQuery(Class<T> resultClass, String sql, Object... params) {
		Query q = isEntity(resultClass) ? 
				createNativeQuery(resultClass, sql, params) : createNativeQuery(sql, params);
		return q;
	}
	
	private <T> Boolean isEntity(Class<T> resultClass) {
		return BaseEntity.class.isAssignableFrom(resultClass);
	}
	
	private Query createNativeQuery(String sql, Object... params) {
		Query q = em.createNativeQuery(sql);
		for (int i = 0; i < params.length; i++) {
			q.setParameter(i + 1, params[i]); // 与Hiberante不同,jpa query从位置1开始
		}
		return q;
	}
	
	private <T> Query createNativeQuery(Class<T> resultClass, String sql, Object... params) {
		Query q = em.createNativeQuery(sql, resultClass);
		for (int i = 0; i < params.length; i++) {
			q.setParameter(i + 1, params[i]); // 与Hiberante不同,jpa query从位置1开始
		}
		return q;
	}
	
	/**
	 * 执行统计查询
	 * @param jpql
	 * @param params 命名参数
	 * @return
	 */
	private String generateCountJpql(String jpql) {
		return "select count(*) from " + StringUtils.substringAfter(jpql, "from");
	}

	/**
	 * 通过jpql生成统计sql
	 * @param jpql
	 * @return
	 */
	private String generateCountSql(String jpql) {
		return "select count(*) c from (" + jpqlToSql(jpql) + ") _count";
	}

	/**
	 * 通过hibernate的翻译器(QueryTranslator)将jpql翻译成sql
	 * @param jpql
	 * @return
	 */
	private String jpqlToSql(String jpql) {
		QueryTranslator qt = new QueryTranslatorImpl(jpql, jpql,
				Collections.EMPTY_MAP,
				(SessionFactoryImplementor) getHibernateSessionFactory());
		qt.compile(Collections.EMPTY_MAP, false);
		return qt.getSQLString();
	}

}
