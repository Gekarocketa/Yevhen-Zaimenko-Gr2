package com.jsfcourse.calc;

import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.enterprise.context.RequestScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;

@Named
@RequestScoped
//@SessionScoped
public class CalcBB {
	private String kwota;
	private String okres;
        private String procent;
	private Double result;

	@Inject
	FacesContext ctx;

	public String getKwota() {
		return kwota;
	}

	public void setKwota(String kwota) {
		this.kwota = kwota;
	}
        public String getProcent() {
		return procent;
	}

	public void setProcent(String procent) {
		this.procent = procent;
	}

	public Double getResult() {
		return result;
	}

	public void setResult(Double result) {
		this.result = result;
	}
        public String getOkres() {
		return okres;
	}

	public void setOkres(String okres) {
		this.okres = okres;
	}
	public String calc() {
		try {
			double kwota = Double.parseDouble(this.kwota);
			double okres = Double.parseDouble(this.okres);
			double procent = Double.parseDouble(this.procent);

			// Walidacja maksymalnych wartości
			if (kwota > 1000000) {
				ctx.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Kwota kredytu nie może przekraczać 1,000,000", null));
				return null;
			}
			if (okres > 50) {
				ctx.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Okres spłaty nie może przekraczać 50 lat", null));
				return null;
			}
			if (procent > 100) {
				ctx.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Stopa procentowa nie może przekraczać 100%", null));
				return null;
			}

			// Poprawiona formuła: kapitalizacja roczna dla kredytu
			// Całkowita kwota = Kwota kredytu * (1 + procent/100)^okres
			result = kwota * Math.pow(1 + (procent / 100), okres);

			ctx.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Obliczenia wykonane pomyślnie", null));
			return "showresult"; 
		} catch (NumberFormatException e) {
			ctx.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Błąd: Wprowadź poprawne liczby", null));
			return null; 
		} catch (Exception e) {
			ctx.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Błąd podczas przetwarzania parametrów", null));
			return null; 
		}
	}


	public String info() {
		return "info";
	}
}
