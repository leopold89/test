package test.TestTaskWiki.model;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.HashSet;
import java.util.Set;

public class Offer {

	private int id;
	private BigInteger hash;
	private String mark = "";
	private Set<String> pictures;
	

	public Offer(int id) {
		this.id = id;
		pictures = new HashSet<>();
		hash = new BigInteger("0");
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public BigInteger getHash() {
		return hash;
	}

	public void addHash(int h) {
		 hash = hash.add(BigInteger.valueOf(Math.abs(h))).multiply(BigInteger.valueOf(31));
	}
	
	public String getMark() {
		return mark;
	}
	
	public void setMark(String mark) {
		this.mark += mark;
	}

	public Set<String> getPictures() {
		return pictures;
	}

	public void setPictures(Set<String> pictures) {
		this.pictures = pictures;
	}

	@Override
	public String toString() {
		return "Offer [id=" + id + ", hash=" + hash + ", mark=" + mark
				+ ", pictures=" + pictures + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + id;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Offer other = (Offer) obj;
		if (id != other.id)
			return false;
		return true;
	}

}
