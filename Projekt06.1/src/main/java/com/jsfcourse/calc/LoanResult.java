package com.jsfcourse.calc;

import java.io.Serializable;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.Table;

@Entity
@Table(name = "loan_result")
@NamedQuery(name = "LoanResult.findAll", query = "SELECT l FROM LoanResult l")
public class LoanResult implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "idloan_result", unique = true, nullable = false)
	private Integer idloanResult;

	@Column(nullable = false)
	private Double kwota;

	@Column(nullable = false)
	private Double okres;

	@Column(nullable = false)
	private Double procent;

	@Column(nullable = false)
	private Double result;

	@Column(name = "created_at")
	private LocalDateTime createdAt = LocalDateTime.now();

	public LoanResult() {
	}

	public LoanResult(Double kwota, Double okres, Double procent, Double result) {
		this.kwota = kwota;
		this.okres = okres;
		this.procent = procent;
		this.result = result;
	}

	public Integer getIdloanResult() {
		return this.idloanResult;
	}

	public void setIdloanResult(Integer idloanResult) {
		this.idloanResult = idloanResult;
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

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}
}

