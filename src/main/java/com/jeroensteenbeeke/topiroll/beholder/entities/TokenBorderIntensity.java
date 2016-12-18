package com.jeroensteenbeeke.topiroll.beholder.entities;

public enum TokenBorderIntensity {
	HEALTHY(100), MINOR_INJURIES(75), MODERATELY_INJURED(50), HEAVILY_INJURED(25), DEAD(0);
	
	private final int percentage;

	private TokenBorderIntensity(int percentage) {
		this.percentage = percentage;
	}
	
	public String getColor(TokenBorderType type) {
		String red = hex(type.getRed() * percentage, 2);
		String green = hex(type.getGreen() * percentage, 2);
		String blue = hex(type.getBlue() * percentage, 2);
		
		return String.format("#%d%d%d", red, green, blue);
	}
	
	public String hex(int value, int length) {
		if (length <= 0) {
			throw new IllegalArgumentException("Length must be positive");
		}
		
		if (length > 7) {
			throw new IllegalArgumentException("Length must be 7 or less to prevent overflows");
		}
		
		int max = (int) Math.pow(16, length);
		
		if (value < 0) {
			throw new IllegalArgumentException("Values must be non-negative");
		}
		
		if (value > max) {
			throw new IllegalArgumentException(String.format("Values with length %d cannot be greater than %d", length, max));
		}
		
		String raw = Integer.toHexString(value);
		
		if (raw.length() > length) {
			return raw.substring(raw.length()-length, raw.length());
		}
		
		return raw;
	}
	
	
}
