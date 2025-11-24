package com.jsfcourse.dao;

import java.util.List;
import java.util.Map;

import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;

import com.jsfcourse.calc.LoanResult;


@Stateless
public class LoanResultDAO {
	private final static String UNIT_NAME = "jsfcourse-simplePU";

	@PersistenceContext(unitName = UNIT_NAME)
	protected EntityManager em;

	public void create(LoanResult loanResult) {
		em.persist(loanResult);
	}

	public LoanResult merge(LoanResult loanResult) {
		return em.merge(loanResult);
	}

	public void remove(LoanResult loanResult) {
		em.remove(em.merge(loanResult));
	}

	public LoanResult find(Object id) {
		return em.find(LoanResult.class, id);
	}

	public List<LoanResult> getFullList() {
		List<LoanResult> list = null;

		Query query = em.createQuery("select l from LoanResult l order by l.createdAt desc");

		try {
			list = query.getResultList();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return list;
	}

	public List<LoanResult> getList(Map<String, Object> searchParams) {
		List<LoanResult> list = null;

		// budowanie zapytania
		String select = "select l ";
		String from = "from LoanResult l ";
		String where = "";
		String orderby = "order by l.createdAt desc";

		Double kwotaMin = (Double) searchParams.get("kwotaMin");
		Double kwotaMax = (Double) searchParams.get("kwotaMax");
		if (kwotaMin != null || kwotaMax != null) {
			if (where.isEmpty()) {
				where = "where ";
			} else {
				where += "and ";
			}
			if (kwotaMin != null && kwotaMax != null) {
				where += "l.kwota between :kwotaMin and :kwotaMax ";
			} else if (kwotaMin != null) {
				where += "l.kwota >= :kwotaMin ";
			} else {
				where += "l.kwota <= :kwotaMax ";
			}
		}

		Query query = em.createQuery(select + from + where + orderby);

		// ustawienie parametrów
		if (kwotaMin != null) {
			query.setParameter("kwotaMin", kwotaMin);
		}
		if (kwotaMax != null) {
			query.setParameter("kwotaMax", kwotaMax);
		}

		try {
			list = query.getResultList();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return list;
	}

}

