package com.dji.sample.common.domain.value.usertype;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.valid4j.Assertive.require;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Types;
import java.util.Objects;

import org.hibernate.usertype.UserType;

abstract class AbstractUserType<T> implements UserType {

	private final Class<T> javaType;

	private final int[] sqlTypes;

	AbstractUserType(Class<T> javaType, int[] sqlTypes) {
		require(javaType, notNullValue());
		require(sqlTypes, notNullValue());
		this.javaType = javaType;
		this.sqlTypes = sqlTypes;
	}

	protected AbstractUserType(Class<T> javaType, Class<?> internalValueClass) {
		require(javaType, notNullValue());
		require(internalValueClass, notNullValue());
		this.javaType = javaType;
		this.sqlTypes = new int[] { sqlType(internalValueClass) };
	}

	private int sqlType(Class<?> internalValueClass) {
		if (internalValueClass == String.class) {
			return Types.VARCHAR;
		}
		if (internalValueClass == Integer.class) {
			return Types.INTEGER;
		}
		if (internalValueClass == Long.class) {
			return Types.BIGINT;
		}
		if (internalValueClass == Double.class) {
			return Types.DOUBLE;
		}
		if (internalValueClass == Float.class) {
			return Types.FLOAT;
		}
		if (internalValueClass == BigDecimal.class) {
			return Types.NUMERIC;
		}
		if (internalValueClass == Boolean.class) {
			return Types.BOOLEAN;
		}

		throw new AssertionError(
			"SqlType mapping for class " + internalValueClass.getSimpleName() + " not implemented yet");
	}

	@Override
	public Object assemble(Serializable cached, Object owner) {
		return cached;
	}

	@Override
	public Object deepCopy(Object value) {
		// simple implementation because value is immutable
		return value;
	}

	@Override
	public Serializable disassemble(Object value) {
		return (Serializable) value;
	}

	@Override
	public boolean equals(Object a, Object b) {
		return Objects.equals(a, b);
	}

	@Override
	public int hashCode(Object value) {
		return value.hashCode();
	}

	@Override
	public boolean isMutable() {
		return false;
	}

	@Override
	public Object replace(Object original, Object target, Object owner) {
		return original;
	}

	@Override
	public int[] sqlTypes() {
		return sqlTypes;
	}

	@Override
	public Class<T> returnedClass() {
		return javaType;
	}
}
