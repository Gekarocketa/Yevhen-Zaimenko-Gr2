package com.jsfcourse.calc;

import java.io.IOException;
import java.io.Serializable;

import jakarta.ejb.EJB;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.context.Flash;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import com.jsfcourse.dao.LoanResultDAO;

@Named
@ViewScoped
public class LoanResultEditBB implements Serializable {
	private static final long serialVersionUID = 1L;

	private static final String PAGE_LOAN_RESULT_LIST = "/pages/calc/loanResultList?faces-redirect=true";
	private static final String PAGE_STAY_AT_THE_SAME = null;

	private LoanResult loanResult = new LoanResult();
	private LoanResult loaded = null;

	@EJB
	LoanResultDAO loanResultDAO;

	@Inject
	FacesContext context;

	@Inject
	Flash flash;

	public LoanResult getLoanResult() {
		return loanResult;
	}

	public void onLoad() throws IOException {
		loaded = (LoanResult) flash.get("loanResult");

		if (loaded != null) {
			loanResult = loaded;
		} else {
			context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Błędne użycie systemu", null));
		}
	}

	public String saveData() {
		if (loaded == null) {
			return PAGE_STAY_AT_THE_SAME;
		}

		try {
			if (loanResult.getIdloanResult() == null) {
				loanResultDAO.create(loanResult);
			} else {
				loanResultDAO.merge(loanResult);
			}
		} catch (Exception e) {
			e.printStackTrace();
			context.addMessage(null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR, "wystąpił błąd podczas zapisu", null));
			return PAGE_STAY_AT_THE_SAME;
		}

		return PAGE_LOAN_RESULT_LIST;
	}
}

