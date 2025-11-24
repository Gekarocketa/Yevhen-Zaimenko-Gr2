package com.jsfcourse.calc;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.enterprise.context.RequestScoped;
import jakarta.ejb.EJB;
import jakarta.faces.context.ExternalContext;
import jakarta.faces.context.FacesContext;
import jakarta.faces.context.Flash;

import com.jsfcourse.dao.LoanResultDAO;

@Named
@RequestScoped
public class LoanResultListBB {
	private static final String PAGE_LOAN_RESULT_EDIT = "/pages/calc/loanResultEdit?faces-redirect=true";
	private static final String PAGE_STAY_AT_THE_SAME = null;

	private Double kwotaMin;
	private Double kwotaMax;

	@Inject
	ExternalContext extcontext;

	@Inject
	Flash flash;

	@EJB
	LoanResultDAO loanResultDAO;

	public Double getKwotaMin() {
		return kwotaMin;
	}

	public void setKwotaMin(Double kwotaMin) {
		this.kwotaMin = kwotaMin;
	}

	public Double getKwotaMax() {
		return kwotaMax;
	}

	public void setKwotaMax(Double kwotaMax) {
		this.kwotaMax = kwotaMax;
	}

	public List<LoanResult> getFullList() {
		return loanResultDAO.getFullList();
	}

	public List<LoanResult> getList() {
		List<LoanResult> list = null;

		Map<String, Object> searchParams = new HashMap<String, Object>();

		if (kwotaMin != null) {
			searchParams.put("kwotaMin", kwotaMin);
		}
		if (kwotaMax != null) {
			searchParams.put("kwotaMax", kwotaMax);
		}

		if (searchParams.isEmpty()) {
			list = loanResultDAO.getFullList();
		} else {
			list = loanResultDAO.getList(searchParams);
		}

		return list;
	}

	public String newLoanResult() {
		LoanResult loanResult = new LoanResult();
		flash.put("loanResult", loanResult);
		return PAGE_LOAN_RESULT_EDIT;
	}

	public String editLoanResult(LoanResult loanResult) {
		flash.put("loanResult", loanResult);
		return PAGE_LOAN_RESULT_EDIT;
	}

	public String deleteLoanResult(LoanResult loanResult) {
		loanResultDAO.remove(loanResult);
		return PAGE_STAY_AT_THE_SAME;
	}
}

