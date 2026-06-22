package space.earlygrey.shapedrawer;

import com.badlogic.gdx.math.MathUtils;

public class DefaultSideEstimator implements SideEstimator {

	//================================================================================
	// MEMBERS
	//================================================================================

	/**
	 * Minimum value returned by {@link #estimateSidesRequired(float, float, float)}
	 */
	protected int minimumSides;
	/**
	 * Maximum value returned by {@link #estimateSidesRequired(float, float, float)}
	 */
	protected int maximumSides;
	/**
	 * Multiply the number of sides return by this value. {@link #minimumSides} and {@link #maximumSides} are not affected
	 */
	protected float sideMultiplier;

	//================================================================================
	// CONSTRUCTOR
	//================================================================================

	public DefaultSideEstimator() {
		this(20, 4000, 1f);
	}

	public DefaultSideEstimator(int minimumSides, int maximumSides, float sideMultiplier) {
		this.minimumSides = minimumSides;
		this.maximumSides = maximumSides;
		this.sideMultiplier = sideMultiplier;
	}

	//================================================================================
	// HELPERS
	//================================================================================

	public int estimateSidesRequired(float pixelSize, float radiusX, float radiusY) {
		float circumference = (float) (ShapeUtils.PI2 * Math.sqrt((radiusX * radiusX + radiusY * radiusY) / 2f));
		int sides = (int) (circumference / (16 * pixelSize));
		float a = Math.min(radiusX, radiusY), b = Math.max(radiusX, radiusY);
		float eccentricity = (float) Math.sqrt(1 - ((a * a) / (b * b)));
		sides += (sides * eccentricity * sideMultiplier) / 16;
		return MathUtils.clamp(sides, minimumSides, maximumSides);
	}

	//================================================================================
	// GETTERS AND SETTERS
	//================================================================================

	public int getMinimumSides() {
		return minimumSides;
	}

	public void setMinimumSides(int minimumSides) {
		this.minimumSides = minimumSides;
	}

	public int getMaximumSides() {
		return maximumSides;
	}

	public void setMaximumSides(int maximumSides) {
		this.maximumSides = maximumSides;
	}

	public float getSideMultiplier() {
		return sideMultiplier;
	}

	public void setSideMultiplier(float sideMultiplier) {
		this.sideMultiplier = sideMultiplier;
	}
}
