package com.javarush.lesson12.hibernate;

import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.usertype.UserType;

import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Objects;

public class PasswordType implements UserType<Password> {

    @Override
    public int getSqlType() {
        return Types.VARCHAR;
    }

    @Override
    public Class<Password> returnedClass() {
        return Password.class;
    }

    @Override
    public boolean equals(Password x, Password y) {
        return Objects.equals(x, y);
    }

    @Override
    public int hashCode(Password x) {
        return Objects.hashCode(x);
    }

    @Override
    public Password nullSafeGet(ResultSet rs, int position, SharedSessionContractImplementor session, Object owner) throws SQLException {
        String password = rs.getString(position);
        return password == null ? null : Password.builder().password(new StringBuilder(password).reverse().toString()).build();
    }


    @Override
    public void nullSafeSet(PreparedStatement st, Password value, int index, SharedSessionContractImplementor session) throws HibernateException, SQLException {
        if (value == null) {
            st.setNull(index, Types.VARCHAR);
        } else {
            st.setString(index, new StringBuilder(value.getPassword()).reverse().toString());
        }
    }

    @Override
    public Password deepCopy(Password value) throws HibernateException {
        return value == null ? null : Password.builder().password(new StringBuilder(value.getPassword()).reverse().toString()).build();
    }

    @Override
    public boolean isMutable() {
        return false;
    }

    @Override
    public Serializable disassemble(Password value) throws HibernateException {
        return value.toString();
    }

    @Override
    public Password assemble(Serializable cached, Object owner) throws HibernateException {
        return (Password) cached;
    }

    @Override
    public Password replace(Password original, Password target, Object owner) throws HibernateException {
        return original;
    }
}