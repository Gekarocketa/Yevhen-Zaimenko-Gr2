package com.jsfcourse.calc;

import jakarta.enterprise.context.RequestScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.context.ExternalContext;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.ejb.EJB;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.jsfcourse.dao.LoanResultDAO;

@Named
@RequestScoped
public class CalcBB {
	private Double kwota;
	private Double okres;
	private Double procent;
	private Double result;

	@Inject
	FacesContext ctx;

	@EJB
	LoanResultDAO loanResultDAO;

	public Double getKwota() {
		return kwota;
	}

	public void setKwota(Double kwota) {
		this.kwota = kwota;
	}

	public Double getOkres() {
		return okres;
	}

	public void setOkres(Double okres) {
		this.okres = okres;
	}

	public Double getProcent() {
		return procent;
	}

	public void setProcent(Double procent) {
		this.procent = procent;
	}

	public Double getResult() {
		return result;
	}

	public void setResult(Double result) {
		this.result = result;
	}

	public String calc() {
		if (kwota == null || okres == null || procent == null) {
			try {
				ExternalContext ec = ctx.getExternalContext();
				Map<String, String> p = ec.getRequestParameterMap();
				if (kwota == null && p.get("kwota_input") != null) {
					kwota = Double.valueOf(p.get("kwota_input").replace(",", "."));
				}
				if (okres == null && p.get("okres_input") != null) {
					okres = Double.valueOf(p.get("okres_input").replace(",", "."));
				}
				if (procent == null && p.get("procent_input") != null) {
					procent = Double.valueOf(p.get("procent_input").replace(",", "."));
				}
			} catch (Exception ignore) {
			}
			if (kwota == null || okres == null || procent == null) {
				ctx.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Wprowadź wszystkie wartości", null));
				return null;
			}
		}

		try {
			if (kwota <= 0 || okres <= 0 || procent < 0) {
				ctx.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Wartości muszą być większe od zera", null));
				return null;
			}

			result = kwota * Math.pow(1 + (procent / 100), okres);
			
			LoanResult loan = new LoanResult(kwota, okres, procent, result);
			
			try {
				loanResultDAO.create(loan);
			} catch (Exception e) {
				e.printStackTrace();
				ctx.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Błąd podczas zapisu do bazy danych", null));
				return null;
			}
			
			ExternalContext ec = ctx.getExternalContext();
			ec.getRequestMap().put("loan", loan);

			List<LoanResult> history = (List<LoanResult>) ec.getSessionMap().get("history");
			if (history == null) {
				history = new ArrayList<>();
				ec.getSessionMap().put("history", history);
			}
			history.add(loan);

			ctx.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Operacja wykonana poprawnie", null));
			return "/pages/calc/showresult?faces-redirect=true";

		} catch (Exception e) {
			e.printStackTrace();
			ctx.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Błąd podczas przetwarzania", null));
			return null;
		}
	}

	public String loadFromGet() {
		try {
			ExternalContext ec = ctx.getExternalContext();
			Map<String, String> p = ec.getRequestParameterMap();
			if (p.get("kwota") != null) kwota = Double.valueOf(p.get("kwota").replace(",", "."));
			if (p.get("okres") != null) okres = Double.valueOf(p.get("okres").replace(",", "."));
			if (p.get("procent") != null) procent = Double.valueOf(p.get("procent").replace(",", "."));
			ctx.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Załadowano z GET", null));
		} catch (Exception e) {
			ctx.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Błąd ładowania GET", null));
		}
		return null;
	}

	public String info() {
		try {
			ExternalContext ec = ctx.getExternalContext();
			ec.setResponseHeader("X-Demo-Header", "StudentProject");
		} catch (Exception ignore) {
		}
		return "/pages/calc/info?faces-redirect=true";
	}
}
