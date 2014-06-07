package com.xorinc.xorgrid;

import java.util.Iterator;

/*
 * Use: 
 * 
 * int[] ints = new int[6]
 * 
 * for(int i : range(0, ints.length)) {
 *     
 *     mash(ints[i]);
 * }
 */

public class Range implements Iterable<Integer> {

	private final int a, b;
	
	public Range(int a, int b){
		
		this.a = a;
		this.b = b;
	}
	
	public static Range range(int a, int b){ // for static imports
		
		return new Range(a, b);
	}
	
	
	@Override
	public Iterator<Integer> iterator() {

		return new Iterator<Integer>() {

			int c = a;
			
			@Override
			public boolean hasNext() {
				return c < b;
			}

			@Override
			public Integer next() {
				int result = c;
				c++;
				return result;
			}

			@Override
			public void remove() {
				throw new UnsupportedOperationException();
			}	
		};
	}
}

