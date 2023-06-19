package com.dji.sample.common.domain.value.usertype;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.valid4j.Assertive.require;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;

public abstract class AbstractSimpleValueUserType<T, U> extends AbstractUserType<T> {

	private final Class<T> valueClass;

	private final Class<U> internalValueClass;

	protected AbstractSimpleValueUserType(Class<T> valueClass, Class<U> internalValueClass) {
		super(valueClass, internalValueClass);
		require(valueClass, notNullValue());
		require(internalValueClass, notNullValue());

		this.valueClass = valueClass;
		this.internalValueClass = internalValueClass;
	}

	protected abstract U toDatabaseValue(T value);

	protected abstract T fromDatabaseValue(U databaseValue);

	@Override
	public Object nullSafeGet(ResultSet rs, String[] names, SharedSessionContractImplementor session, Object owner)
		throws HibernateException, SQLException {
		require(names.length == 1, "names.length == 1");
		U databaseValue = rs.getObject(names[0], internalValueClass);
		return databaseValue != null ? fromDatabaseValue(databaseValue) : null;
	}

	@Override
	public void nullSafeSet(PreparedStatement st, Object value, int index, SharedSessionContractImplementor session)
		throws HibernateException, SQLException {
		if (value != null) {
			T valueObject = returnedClass().cast(value);
			st.setObject(index, toDatabaseValue(valueObject), sqlTypes()[0]);
		} else {
			st.setNull(index, sqlTypes()[0]);
		}
	}

	public Class<T> getValueObjectClass() {
		return valueClass;
	}

	public Class<U> getInternalValueClass() {
		return internalValueClass;
	}
}
