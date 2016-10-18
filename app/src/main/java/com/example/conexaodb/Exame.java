package com.example.conexaodb;

public class Exame {
	private int responsavel;
	private String idPessoa;
	private String duracao;
	private String idExame;
	private String data;
	private String mao;
	private String tipo;
	private String valores;
	private float valMax;
	private float valMed;
	
	
	public String getValores() {
		return valores;
	}
	public void setValores(String valores) {
		this.valores = valores;
	}
	public int getResponsavel() {
		return responsavel;
	}
	public void setResponsavel(int responsavel) {
		this.responsavel = responsavel;
	}
	public String getIdPessoa() {
		return idPessoa;
	}
	public void setIdPessoa(String idPessoa) {
		this.idPessoa = idPessoa;
	}
	public String getDuracao() {
		return duracao;
	}
	public void setDuracao(String duracao) {
		this.duracao = duracao;
	}
	public String getIdExame() {
		return idExame;
	}
	public void setIdExame(String idExame) {
		this.idExame = idExame;
	}
	public String getData() {
		return data;
	}
	public void setData(String data) {
		this.data = data;
	}
	public String getMao() {
		return mao;
	}
	public void setMao(String mao) {
		this.mao = mao;
	}
	public String getTipo() {
		return tipo;
	}
	public void setTipo(String tipo) {
		this.tipo = tipo;
	}
	public float getValMax() {
		return valMax;
	}
	public void setValMax(float valMax) {
		this.valMax = valMax;
	}
	public float getValMed() {
		return valMed;
	}
	public void setValMed(float valMed) {
		this.valMed = valMed;
	}
	
}