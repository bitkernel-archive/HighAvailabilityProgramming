package org.bitkernel.cache.dao;

import com.sun.istack.internal.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.bitkernel.cache.entity.AccountInfo;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

@Component
@Repository
@Transactional("accountTransactionManager")
@Slf4j
public class AccountDao {
    protected SessionFactory _sessionFactory;

    @Autowired
    public AccountDao(@Qualifier("accountSessionFactory") SessionFactory _sessionFactory) {
        this._sessionFactory = _sessionFactory;
    }

    @NotNull
    public Session getSession() {
        return _sessionFactory.getCurrentSession();
    }

    public AccountInfo getAccountInfo(long accountId) {
        CriteriaBuilder cb = getSession().getCriteriaBuilder();
        CriteriaQuery<AccountInfo> cq = cb.createQuery(AccountInfo.class);
        Root<AccountInfo> root = cq.from(AccountInfo.class);
        cq.select(root);
        cq.where(cb.equal(root.get("id"), accountId));
        return getSession().createQuery(cq).uniqueResult();
    }
}
