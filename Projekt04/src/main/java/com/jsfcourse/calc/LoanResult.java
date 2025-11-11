package com.jsfcourse.calc;

import java.io.Serializable;
import java.time.LocalDateTime;

public class LoanResult implements Serializable {
	private Double kwota;
	private Double okres;
	private Double procent;
	private Double result;
	private LocalDateTime createdAt = LocalDateTime.now();

	public LoanResult() {
	}

	public LoanResult(Double kwota, Double okres, Double procent, Double result) {
		this.kwota = kwota;
		this.okres = okres;
		this.procent = procent;
		this.result = result;
	}

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

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}
}

