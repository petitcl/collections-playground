package com.petitcl.collections.models;

/**
 * Util test class that can force its hash code.
 * Useful when trying to simulate the presence or absence of hash collisions
 */
public class HashCollider {

	private String name;

	private int hashCode;

	public static HashCollider of(String name, int hashCode) {
		return new HashCollider(name, hashCode);
	}

	public HashCollider(String name, int hashCode) {
		this.name = name;
		this.hashCode = hashCode;
	}

	public String getName() {
		return this.name;
	}

	public long getHashCode() {
		return this.hashCode;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setHashCode(int hashCode) {
		this.hashCode = hashCode;
	}

	public boolean equals(final Object o) {
		if (o == this) return true;
		if (!(o instanceof HashCollider)) return false;
		final HashCollider other = (HashCollider) o;
		if (!other.canEqual((Object) this)) return false;
		final Object this$name = this.getName();
		final Object other$name = other.getName();
		if (this$name == null ? other$name != null : !this$name.equals(other$name)) return false;
		return true;
	}

	protected boolean canEqual(final Object other) {
		return other instanceof HashCollider;
	}

	public int hashCode() {
		return this.hashCode;
	}

	public String toString() {
		return "HashCollider(name=" + this.getName() + ", hashCode=" + this.getHashCode() + ")";
	}
}
