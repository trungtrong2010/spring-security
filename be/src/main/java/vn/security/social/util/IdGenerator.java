package vn.security.social.util;

import org.hibernate.HibernateException;
import org.hibernate.MappingException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.Configurable;
import org.hibernate.id.IdentifierGenerator;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.type.Type;

import java.io.Serializable;
import java.util.Properties;
import java.util.stream.Stream;


public class IdGenerator implements IdentifierGenerator, Configurable {

    private String prefix;

    @Override
    public void configure(Type type, Properties properties, ServiceRegistry serviceRegistry) throws MappingException {
        prefix = properties.getProperty("prefix");
    }

    @Override
    public Serializable generate(SharedSessionContractImplementor session, Object o) throws HibernateException {


        String query = String.format("select %s from %s",
                session.getEntityPersister(o.getClass().getName(), o)
                        .getIdentifierPropertyName(),
                o.getClass().getSimpleName());

        Stream<String> ids = session.createQuery(query, String.class).stream();

        long max = ids.map(obj -> obj.replace(prefix, ""))
                .mapToLong(Long::parseLong)
                .max()
                .orElse(0L);

        return prefix + (String.format("%04d", max + 1));
    }
}