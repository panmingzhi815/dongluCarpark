package com.dongluhitec.card.domain.db.singlecarpark;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.criteria4jpa.Criteria;
import org.criteria4jpa.criterion.Criterion;
import org.criteria4jpa.criterion.MatchMode;
import org.criteria4jpa.criterion.Restrictions;
import org.criteria4jpa.projection.Projection;
import org.criteria4jpa.projection.ProjectionList;
import org.criteria4jpa.projection.Projections;

import com.dongluhitec.card.domain.util.StrUtil;

public class QueryParameter implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 3769855555280275068L;

	enum QueryParameterType {
		eq, ge, le, gt, lt, in, firstResult, maxResult, between, notBetween, ne, like, isNull, isNotNull, isEmpty, isNotEmpty, not, and, or, leftJoin, rowCount,countMutil, sum
	}

	private QueryParameterType type;
	private String relativePath;
	private Object value;
	private Object value2;
	private List<QueryParameter> list = new ArrayList<>();
	private String className;

	public QueryParameter(String relativePath, Object value, QueryParameterType type) {
		super();
		this.type = type;
		this.relativePath = relativePath;
		this.value = value;
		if (value!=null) {
			className = value.getClass().getName();
		}
	}

	public QueryParameter(String relativePath, Object value, Object value2, QueryParameterType type) {
		super();
		this.value2 = value2;
		this.type = type;
		this.relativePath = relativePath;
		this.value = value;
	}

	public QueryParameter(String relativePath, QueryParameterType type) {
		this.relativePath = relativePath;
		this.type = type;
	}

	public void set(Criteria c) {
		
		switch (type) {
		case firstResult:
			c.setFirstResult((int) value);
			break;
        case maxResult:
        	c.setMaxResults((int) value);	
        	break;
        case leftJoin:
        	Criteria cc = c.createCriteria(relativePath);
        	for (QueryParameter queryParameter : list) {
				cc.add(queryParameter.toCriterion());
			}
        	break;
        case rowCount:
        	c.setProjection(toProjection());
        	break;
        case countMutil:
        	ProjectionList projectionList = Projections.projectionList();
        	for (QueryParameter queryParameter : list) {
        		projectionList.add(queryParameter.toProjection());
			}
			c.setProjection(projectionList);
        	break;
		default:
			if (value==null) {
				return;
			}
			c.add(toCriterion());
			break;
		}
	}
	
	private Projection toProjection() {
		switch (type) {
		case rowCount:
			return Projections.rowCount();
		case sum:
			return Projections.sum(relativePath);
		default:
			break;
		}
		return null;
	}

	public Criterion toCriterion(){
 		Object value=getValue();
		switch (type) {
		case eq:
			return Restrictions.eq(relativePath, value);
		case ne:
			return Restrictions.ne(relativePath, value);
		case ge:
			return Restrictions.ge(relativePath, value);
		case le:
			return Restrictions.le(relativePath, value);
		case gt:
			return Restrictions.gt(relativePath, value);
		case lt:
			return Restrictions.lt(relativePath, value);
		case like:
			return Restrictions.like(relativePath, value.toString());
		case isEmpty:
			return Restrictions.isEmpty(relativePath);
		case isNotEmpty:
			return Restrictions.isNotEmpty(relativePath);
		case isNull:
			return Restrictions.isNull(relativePath);
		case isNotNull:
			return Restrictions.isNotNull(relativePath);
		case or:
			Criterion cs[]=new Criterion[list.size()];
			for (int i = 0; i < list.size(); i++) {
				QueryParameter queryParameter = list.get(i);
				cs[i]=queryParameter.toCriterion();
			}
			return Restrictions.or(cs);
		case and:
			cs=new Criterion[list.size()];
			for (int i = 0; i < list.size(); i++) {
				QueryParameter queryParameter = list.get(i);
				cs[i]=queryParameter.toCriterion();
			}
			return Restrictions.and(cs);
		default:
			break;
		}
		return null;
	}

	public QueryParameterType getType() {
		return type;
	}

	public void setType(QueryParameterType type) {
		this.type = type;
	}

	public String getRelativePath() {
		return relativePath;
	}

	public void setRelativePath(String relativePath) {
		this.relativePath = relativePath;
	}

	public Object getValue() {
		if (value!=null&&className!=null) {
			try {
				if (className.equals(Float.class.getName())) {
					return Float.valueOf(value+"");
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return value;
	}

	public void setValue(Object value) {
		this.value = value;
	}
	
	public static QueryParameter firstResult(int value) {
		return new QueryParameter(null, value, QueryParameterType.firstResult);
	}
	public static QueryParameter maxResult(int value) {
		return new QueryParameter(null, value, QueryParameterType.maxResult);
	}
	public static QueryParameter maxResult(String relativePath,QueryParameter... parameters) {
		QueryParameter q = new QueryParameter(relativePath, null, QueryParameterType.leftJoin);
		for (QueryParameter queryParameter : parameters) {
			q.list.add(queryParameter);
		}
		return q;
	}
	
	public static QueryParameter eq(String relativePath, Object value) {
		return new QueryParameter(relativePath, value, QueryParameterType.eq);
	}

	/**
	 * Adds a "not equal" constraint to a <i>persistent field</i> or a <i>single-valued relationship
	 * field</i>.
	 * 
	 * @param relativePath
	 *            relative path of the field
	 * @param value
	 *            The value to check equality against.
	 * @return {@link Criterion} instance
	 */
	public static QueryParameter ne(String relativePath, Object value) {
		return new QueryParameter(relativePath, value, QueryParameterType.ne);
	}

	/**
	 * Adds a "like" constraint to a <i>persistent field</i>. This method expects the value to
	 * already contain the required wildcard characters. Alternatively you can call
	 * {@link #like(String, String, MatchMode)} to let the restriction add the wildcards.
	 * 
	 * @param relativePath
	 *            relative path of the field
	 * @param value
	 *            the match string
	 * @return {@link Criterion} instance
	 */
	public static QueryParameter like(String relativePath, String value) {
		if (StrUtil.isEmpty(value)||value.equals("%%")||value.equals("%null%")) {
			value=null;
		}
		return new QueryParameter(relativePath, value, QueryParameterType.like);
	}

	public static QueryParameter like(String relativePath, String value, MatchMode matchMode) {
		return new QueryParameter(relativePath, matchMode.toMatchString(value), QueryParameterType.like);
	}

	/**
	 * Adds a "greater than" constraint to a <i>persistent field</i>.
	 * 
	 * @param relativePath
	 *            relative path of the field
	 * @param value
	 *            The value to check against.
	 * @return {@link Criterion} instance
	 */
	public static QueryParameter gt(String relativePath, Object value) {
		return new QueryParameter(relativePath, value, QueryParameterType.gt);
	}

	/**
	 * Adds a "less than" constraint to a <i>persistent field</i>.
	 * 
	 * @param relativePath
	 *            relative path of the field
	 * @param value
	 *            The value to check against.
	 * @return {@link Criterion} instance
	 */
	public static QueryParameter lt(String relativePath, Object value) {
		return new QueryParameter(relativePath, value, QueryParameterType.lt);
	}

	/**
	 * Adds a "less than or equal" constraint to a <i>persistent field</i>.
	 * 
	 * @param relativePath
	 *            relative path of the field
	 * @param value
	 *            The value to check against.
	 * @return {@link Criterion} instance
	 */
	public static QueryParameter le(String relativePath, Object value) {
		return new QueryParameter(relativePath, value, QueryParameterType.le);
	}

	/**
	 * Adds a "greater than or equal" constraint to a <i>persistent field</i>.
	 * 
	 * @param relativePath
	 *            relative path of the field
	 * @param value
	 *            The value to check against.
	 * @return {@link Criterion} instance
	 */
	public static QueryParameter ge(String relativePath, Object value) {
		return new QueryParameter(relativePath, value, QueryParameterType.ge);
	}

	/**
	 * Adds a "between" constraint to a <i>persistent field</i>.
	 * 
	 * @param relativePath
	 *            relative path of the field
	 * @param lo
	 *            the low end of the "between" expression
	 * @param hi
	 *            the high end of the "between" expression
	 * @return {@link Criterion} instance
	 */
	public static QueryParameter between(String relativePath, Object lo, Object hi) {
		return new QueryParameter(relativePath, lo, hi, QueryParameterType.between);
	}

	/**
	 * Adds a "not between" constraint to a <i>persistent field</i>.
	 * 
	 * @param relativePath
	 *            relative path of the field
	 * @param lo
	 *            the low end of the "not between" expression
	 * @param hi
	 *            the high end of the "not between" expression
	 * @return {@link Criterion} instance
	 */
	public static QueryParameter notBetween(String relativePath, Object lo, Object hi) {
		return new QueryParameter(relativePath, lo, hi, QueryParameterType.notBetween);
	}

	/**
	 * Adds a "is null" constraint to a <i>persistent field</i> or a <i>single-valued relationship
	 * field</i>.
	 * 
	 * @param relativePath
	 *            relative path of the field
	 * @return {@link Criterion} instance
	 */
	public static QueryParameter isNull(String relativePath) {
		return new QueryParameter(relativePath,"", QueryParameterType.isNull);
	}

	/**
	 * Adds a "is not null" constraint to a <i>persistent field</i> or a <i>single-valued
	 * relationship field</i>.
	 * 
	 * @param relativePath
	 *            relative path of the field
	 * @return {@link Criterion} instance
	 */
	public static QueryParameter isNotNull(String relativePath) {
		return new QueryParameter(relativePath,"", QueryParameterType.isNotNull);
	}

	/**
	 * Adds a "is empty" constraint to a <i>collection-valued relationship field</i>.
	 * 
	 * @param relativePath
	 *            relative path of the collection
	 * @return {@link Criterion} instance
	 */
	public static QueryParameter isEmpty(String relativePath) {
		return new QueryParameter(relativePath,"", QueryParameterType.isEmpty);
	}

	/**
	 * Adds a "is not empty" constraint to a <i>collection-valued relationship field</i>.
	 * 
	 * @param relativePath
	 *            relative path of the collection
	 * @return {@link Criterion} instance
	 */
	public static QueryParameter isNotEmpty(String relativePath) {
		return new QueryParameter(relativePath,"", QueryParameterType.isNotEmpty);
	}

	/**
	 * Creates a "not" restriction. Creates a negated version of the supplied restriction.
	 * 
	 * @param criterion
	 *            Restriction to negate
	 * @return {@link Criterion} instance
	 */
	public static QueryParameter not(QueryParameter criterion) {
		QueryParameter queryParameter = new QueryParameter("", QueryParameterType.not);
		queryParameter.list.add(criterion);
		return queryParameter;
	}

	/**
	 * Connects multiple restrictions with an logical conjunction. Calling this method is a shortcut
	 * for creating a {@link Conjunction} by calling {@link #conjunction()} and adding all
	 * restrictions to it.
	 * 
	 * @param criterionList
	 *            The restrictions to add to the conjunction.
	 * @return {@link Conjunction} instance
	 */
	public static QueryParameter and(QueryParameter... criterionList) {
		QueryParameter queryParameter = new QueryParameter("", QueryParameterType.and);
		for (QueryParameter criterion : criterionList) {
			queryParameter.list.add(criterion);
		}
		return queryParameter;
	}

	/**
	 * Connects multiple restrictions with an logical disjunction. Calling this method is a shortcut
	 * for creating a {@link Disjunction} by calling {@link #disjunction()} and adding all
	 * restrictions to it.
	 * 
	 * @param criterionList
	 *            The restrictions to add to the disjunction.
	 * @return {@link Disjunction} instance
	 */
	public static QueryParameter or(QueryParameter... criterionList) {
		QueryParameter queryParameter = new QueryParameter("", QueryParameterType.or);
		for (QueryParameter criterion : criterionList) {
			queryParameter.list.add(criterion);
		}
		return queryParameter;
	}

	/**
	 * Adds an "in" restriction to a persistent field.
	 * 
	 * @param relativePath
	 *            relative path of the persistent field
	 * @param values
	 *            expected values of the field
	 * @return {@link Criterion} instance
	 */
	public static QueryParameter in(String relativePath, Object[] values) {
		return in(relativePath, Arrays.asList(values));
	}

	/**
	 * Adds an "in" restriction to a persistent field.
	 * 
	 * @param relativePath
	 *            relative path of the persistent field
	 * @param values
	 *            expected values of the field
	 * @return {@link Criterion} instance
	 */
	public static QueryParameter in(String relativePath, Collection<?> values) {
		return new QueryParameter(relativePath, values, QueryParameterType.in);
	}

	public Object getValue2() {
		return value2;
	}

	public void setValue2(Object value2) {
		this.value2 = value2;
	}

	public List<QueryParameter> getList() {
		return list;
	}

	public static QueryParameter rowCount() {
		return new QueryParameter(null, QueryParameterType.rowCount);
	}
	public static QueryParameter sum(String relativePath) {
		return new QueryParameter(relativePath, QueryParameterType.sum);
	}
	public static QueryParameter countMutil(QueryParameter... parameters){
		QueryParameter queryParameter = new QueryParameter(null, "", QueryParameterType.countMutil);
		for (QueryParameter qp : parameters) {
			queryParameter.list.add(qp);
		}
		return queryParameter;
	}
}
