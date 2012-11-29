package com.baoxue.task.common;


public abstract class LinkedItem {

	private LinkedItem previous;
	private LinkedItem next;

	public void add(LinkedItem item) {

		LinkedItem p;
		for (p = this; p != null && p.next != null; p = p.next)
			;
		p.next = item;
		item.previous = p;
	}

	public void remove() {

		if (this.previous != null) {
			this.previous.next = this.next;
		}
		if (this.next != null) {
			this.next.previous = this.previous;
		}
	}

	public boolean isEnd() {
		if (this.next == null) {
			return true;
		} else {
			return false;
		}
	}

	public LinkedItem getPrevious() {
		return previous;
	}

	public LinkedItem getNext() {
		return next;
	}

}
