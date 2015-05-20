package nz.ac.auckland.alm;

import java.util.ArrayList;
import java.util.List;

import nz.ac.auckland.linsolve.Constraint;
import nz.ac.auckland.linsolve.OperatorType;

/**
 * Rectangular area in the GUI, defined by a tab on each side.
 */
public class Area {
	public static class Size {
		double width;
		double height;

		final public static int UNDEFINED = -2;

		public Size() {
		}

		public Size(Size size) {
			this.width = size.width;
			this.height = size.height;
		}

		public Size(double width, double height) {
			this.width = width;
			this.height = height;
		}

		public double getWidth() {
			return width;
		}

		public double getHeight() {
			return height;
		}

		public void setWidth(double width) {
			this.width = width;
		}

		public void setHeight(double height) {
			this.height = height;
		}
	}

	public static class Rect {
		public float left;
		public float top;
		public float right;
		public float bottom;

		public Rect(float left, float top, float right, float bottom) {
			this.left = left;
			this.top = top;
			this.right = right;
			this.bottom = bottom;
		}

		public float getWidth() {
			return right - left;
		}

		public float getHeight() {
			return bottom - top;
		}
	}

	/**
	 * Undefined size. Used if a certain size constraint is not set.
	 */
	static final Size UNDEFINED_SIZE = new Size(Size.UNDEFINED, Size.UNDEFINED);

	/**
	 * The layout specification this area belongs to.
	 */
	LayoutSpec ls;

	// the boundaries (tabs) of the area
	XTab left;
	XTab right;
	YTab top;
	YTab bottom;

	/**
	 * A list of constraints which are removed form the speciifcation when the
	 * area is removed.
	 */
	List<Constraint> constraints = new ArrayList<Constraint>();

	Size minSize = new Size(0, 0);
	Size preferredSize = UNDEFINED_SIZE;
	Size maxSize = new Size(Integer.MAX_VALUE, Integer.MAX_VALUE);;

	/**
	 * Size constraint for the content. Valid even if the content is actually in
	 * a child area.
	 */
	Constraint minWidthConstraint;
	Constraint maxWidthConstraint;
	Constraint minHeightConstraint;
	Constraint maxHeightConstraint;
	Constraint preferredWidthConstraint;
	Constraint preferredHeightConstraint;

	HorizontalAlignment hAlignment = HorizontalAlignment.CENTER;
	VerticalAlignment vAlignment = VerticalAlignment.CENTER;
	int leftInset = 0;
	int topInset = 0;
	int rightInset = 0;
	int bottomInset = 0;

    // TODO remove
	//Size shrinkPenalties = new Size(2, 2);
	double shrinkPenaltyWidth;
    double shrinkPenaltyHeight;

    Size growPenalties = new Size(1, 1);

	double aspectRatio = -1;
	Constraint aspectRatioConstraint;

	/**
	 * Left tab of the area.
	 */
	public XTab getLeft() {
		return left;
	}

	/**
	 * Top tab of the area.
	 */
	public YTab getTop() {
		return top;
	}

	/**
	 * Right tab of the area.
	 */
	public XTab getRight() {
		return right;
	}

	/**
	 * Bottom tab of the area.
	 */
	public YTab getBottom() {
		return bottom;
	}

	void updateHorizontalConstraintVars() {
		minWidthConstraint.setLeftSide(-1, left, 1, right);

		// if a maximum constraint was set, then it nees to use the new tab
		if (maxWidthConstraint != null)
			maxWidthConstraint.setLeftSide(-1, left, 1, right);

		if (preferredWidthConstraint != null)
			maxWidthConstraint.setLeftSide(-1, left, 1, right);

		ls.invalidateLayout();
	}

	void updateVerticalConstraintVars() {
		// the respective minimum constraint needs to use the new tab
		minHeightConstraint.setLeftSide(-1, top, 1, bottom);

		// if a maximum constraint was set, then it nees to use the new tab
		if (maxHeightConstraint != null)
			maxHeightConstraint.setLeftSide(-1, top, 1, bottom);

		if (preferredHeightConstraint != null)
			preferredHeightConstraint.setLeftSide(-1, top, 1, bottom);

		ls.invalidateLayout();
	}

	/**
	* Set the left tab of the area
	* @param value The tab specification to be used.
	*/
	public void setLeft(XTab value) {
		left = value;
		updateHorizontalConstraintVars();
	}

	/**
	* Set the rigth tab of the area
	* @param value The tab specification to be used.
	*/
	public void setRight(XTab value) {
		right = value;
		updateHorizontalConstraintVars();
	}

	/**
	* Set the top tab of the area
	* @param value the tab specification to be used.
	*/
	public void setTop(YTab value) {
		top = value;
		updateVerticalConstraintVars();
	}

	/**
	* Set the bottom tab of the area
	* @param value the tab specification to be used.
	*/
	public void setBottom(YTab value) {
		bottom = value;
		updateVerticalConstraintVars();
	}

	/**
	 * Minimum size of the area's content. May be different from the minimum
	 * size of the area.
	 */
	public Size getMinSize() {
		return minSize;
	}

	/**
	* Set the minimum size of the area's content.
	* @param value Size that defines the desired minimum size.
	*/
	public void setMinSize(Size value) {
        minSize = value;
		minWidthConstraint.setRightSide(value.getWidth());
		minHeightConstraint.setRightSide(value.getHeight());

        ls.invalidateLayout();
	}

    public void setMinContentSize(double width, double height) {
        setMinSize(new Size(width, height));
    }

	/**
	 * Maximum size of the area's content. May be different from the maximum
	 * size of the area.
	 */
	public Size getMaxSize() {
		return maxSize;
	}
	/**
	* Set the maximal size of the area's content.
	* @param value Size that defines the desired maximal size.
	*/
	public void setMaxSize(Size value) {
        maxSize = value;

		if (maxSize.getWidth() > 0) {
			if (maxWidthConstraint == null) {
				maxWidthConstraint = ls.linearSpec.addConstraint(-1, left, 1, right, OperatorType.LE, 0,
						growPenalties.getWidth());
				maxWidthConstraint.Owner = this;
				constraints.add(maxWidthConstraint);
			}
			updateRightSideHorizontal(maxWidthConstraint, maxSize.getWidth());
		} else if (maxWidthConstraint != null) {
			maxWidthConstraint.remove();
			maxWidthConstraint = null;
		}

		if (maxSize.getHeight() > 0) {
			if (maxHeightConstraint == null) {
				maxHeightConstraint = ls.linearSpec.addConstraint(-1, top, 1, bottom, OperatorType.LE, 0,
						growPenalties.getHeight());
				maxHeightConstraint.Owner = this;
				constraints.add(maxHeightConstraint);
			}
			updateRightSideVertical(maxHeightConstraint, maxSize.getHeight());
		} else if (maxHeightConstraint != null) {
			maxHeightConstraint.remove();
			maxHeightConstraint = null;
		}

        ls.invalidateLayout();
	}

    public void setMaxSize(double width, double height) {
        setMaxSize(new Size(width, height));
    }

	/**
	 * Preferred size of the area's content. May be different from the preferred
	 * size of the area. Manual changes of PreferredContentSize are ignored
	 * unless autoPreferredContentSize is set to false.
	 */
	public Size getPreferredSize() {
		return preferredSize;
	}
	/**
	* Set the preferred size of the area's content. Manual changes
	* of PreferredContentSize are ignored unless
	* autoPreferredContentSize is set to false.
	* @param value Size that defines the preferred size.
	*/
	public void setPreferredSize(Size value) {
        preferredSize = value;

		if (preferredSize.getWidth() > 0) {
			if (preferredWidthConstraint == null) {
				preferredWidthConstraint = ls.linearSpec.addConstraint(-1, left, 1, right, OperatorType.EQ, 0,
						shrinkPenaltyWidth);
				preferredWidthConstraint.Owner = this;
				constraints.add(preferredWidthConstraint);
			}
			updateRightSideHorizontal(preferredWidthConstraint, preferredSize.getWidth());
		} else if (preferredWidthConstraint != null) {
			preferredWidthConstraint.remove();
			preferredWidthConstraint = null;
		}

		if (preferredSize.getHeight() > 0) {
			if (preferredHeightConstraint == null) {
				preferredHeightConstraint = ls.linearSpec.addConstraint(-1, top, 1, bottom, OperatorType.EQ, 0,
						shrinkPenaltyWidth);
				preferredHeightConstraint.Owner = this;
				constraints.add(preferredWidthConstraint);
			}
			updateRightSideVertical(preferredHeightConstraint, preferredSize.getHeight());
		} else if (preferredHeightConstraint != null){
			preferredHeightConstraint.remove();
			preferredHeightConstraint = null;
		}

        ls.invalidateLayout();
	}

    public void setPreferredContentSize(double width, double height) {
        setPreferredSize(new Size(width, height));
    }

	/**
	 * The reluctance with which the area's content shrinks below its preferred
	 * size. The bigger the less likely is such shrinking.
	 */
	public double getShrinkPenaltyWidth() {
		return shrinkPenaltyWidth;
	}

    public double getShrinkPenaltyHeight() {
        return shrinkPenaltyHeight;
    }

	/**
	* Set the reluctance with which the area's content shrinks below its preferred
	* size. The bigger the less likely is such shrinking.
	*/
	public void setShrinkPenalties(double width, double height) {
		shrinkPenaltyWidth = width;
		shrinkPenaltyHeight = height;

		// penalties are only relevant if a preferred size is set
		if (preferredWidthConstraint != null) {
			// TODO: was previously setPenaltyNeg; maybe use two
			// inequalities for preferredWidthConstraint
			preferredWidthConstraint.setPenalty(width);
			preferredHeightConstraint.setPenalty(height);
		}

		ls.invalidateLayout();
	}

	/**
	 * The reluctance with which the area's content grows over its preferred
	 * size. The bigger the less likely is such expansion.
	 */
	public Size getGrowPenalties() {
		return growPenalties;
	}
	/**
	* Set the reluctance with which the area's content grows over its preferred
	* size. The bigger the less likely is such expansion.
	* @param value Size that defines the grow penalties.
	*/
	public void setGrowPenalties(Size value) {
		growPenalties = value;
		if (preferredWidthConstraint != null) { // penalties are only relevant
			// if a
			// preferred size is set
			preferredWidthConstraint.setPenalty(value.getWidth());
			preferredHeightConstraint.setPenalty(value.getHeight());
		}

		ls.invalidateLayout();
	}

	/**
	 * Aspect ratio of the area's content. May be different from the aspect
	 * ratio of the area.
	 */
	public double getAspectRatio() {
		return aspectRatio;
	}
	/**
	* Set the aspect ratio of the area's content. May be different from the aspect
	* ratio of the area.
	* @param value double that defines the desired aspect ratio.
	*/
	public void setAspectRatio(double value) {
		aspectRatio = value;
		if (aspectRatio > 0) {
			if (aspectRatioConstraint == null) {
				aspectRatioConstraint = ls.linearSpec.addConstraint(-1, left, 1, right, aspectRatio, top, -aspectRatio,
						bottom,	OperatorType.EQ, 0);
				aspectRatioConstraint.Owner = this;
				constraints.add(aspectRatioConstraint);
			} else
				aspectRatioConstraint.setLeftSide(-1, left, 1, right, aspectRatio, top, -aspectRatio, bottom);
		} else if (aspectRatioConstraint != null) {
			aspectRatioConstraint.remove();
			aspectRatioConstraint = null;
		}

		ls.invalidateLayout();
	}

	/**
	 * Horizontal alignment of the content in its area.
	 */
	public HorizontalAlignment getHorizontalAlignment() {
		return hAlignment;
	}

	/**
	 * Set the horizontal alignment of the content in its area.
	 * @param value HorizontalAlignment to set
	 */
	public void setHorizontalAlignment(HorizontalAlignment value) {
		hAlignment = value;
	}

	/**
	 * Vertical alignment of the content in its area.
	 */
	public VerticalAlignment getVerticalAlignment() {
		return vAlignment;
	}
	/**
	 * Set the vertical alignment of the content in its area.
	 * @param value VerticalAlignment to set
	 */
	public void setVerticalAlignment(VerticalAlignment value) {
		vAlignment = value;
	}

	public void setAlignment(HorizontalAlignment hAlignment, VerticalAlignment vAlignment) {
		setHorizontalAlignment(hAlignment);
		setVerticalAlignment(vAlignment);
	}

	private float relativeHorizontal(HorizontalAlignment alignment) {
		switch (alignment) {
			case LEFT:
				return 0;
			case RIGHT:
				return 1;
			case CENTER:
				return 0.5f;
		}
		return 0;
	}

	private float relativeVertical(VerticalAlignment alignment) {
		switch (alignment) {
			case TOP:
				return 0;
			case BOTTOM:
				return 1;
			case CENTER:
				return 0.5f;
		}
		return 0;
	}

	public Rect getContentRect() {
		Rect frame = new Rect((float)(getLeft().getValue() + getLeftInset()),
				(float)(getTop().getValue() + getTopInset()), (float)(getRight().getValue() - getRightInset()),
				(float)(getBottom().getValue() - getBottomInset()));

		// Taken from the Haiku source code:
		// align according to the given alignment
		if (maxSize.width < frame.getWidth() && hAlignment != HorizontalAlignment.FILL) {
			frame.left += (frame.getWidth() - maxSize.width) * relativeHorizontal(hAlignment);
			frame.right = (float)(frame.left + maxSize.width);
		}
		if (maxSize.height < frame.getHeight() && vAlignment != VerticalAlignment.FILL) {
			frame.top += (frame.getHeight() - maxSize.height) * relativeVertical(vAlignment);
			frame.bottom = (float)(frame.top + maxSize.height);
		}

		return frame;
	}

	/**
	 * Left inset between area and its content.
	 */
	public int getLeftInset() {
		return leftInset;
	}
	/**
	 * Set the left inset between area and its content.
	 * @param value int that defines the left inset
	 */
	public void setLeftInset(int value) {
		leftInset = value;
		updateHorizontal();
		ls.invalidateLayout();
	}

	/**
	 * Top inset between area and its content.
	 */
	public int getTopInset() {
		return topInset;
	}
	/**
	 * Set the top inset between area and its content.
	 * @param value int that defines the top inset
	 */
	public void setTopInset(int value) {
		topInset = value;
		updateVertical();
		ls.invalidateLayout();
	}

	/**
	 * Right inset between area and its content.
	 */
	public int getRightInset() {
		return rightInset;
	}
	/**
	 * Set the right inset between area and its content.
	 * @param value int that defines the right inset
	 */
	public void setRightInset(int value) {
		rightInset = value;
		updateHorizontal();
		ls.invalidateLayout();
	}

	/**
	 * Bottom inset between area and its content.
	 */
	public int getBottomInset() {
		return bottomInset;
	}
	/**
	 * Set the bottom inset between area and its content.
	 * @param value int that defines the bottom inset
	 */
	public void setBottomInset(int value) {
		bottomInset = value;
		updateVertical();
		ls.invalidateLayout();
	}

	void updateRightSideHorizontal(Constraint constraint, double rightSide) {
		constraint.setRightSide(rightSide + leftInset + rightInset);
	}

	/**
	 * Update the constraints for horizontal insets and alignment.
	 */
	void updateHorizontal() {
		updateRightSideHorizontal(minWidthConstraint, minSize.getWidth());
		if (preferredWidthConstraint != null)
			updateRightSideHorizontal(preferredWidthConstraint, preferredSize.getWidth());
		if (maxWidthConstraint != null)
			updateRightSideHorizontal(maxWidthConstraint, maxSize.getWidth());
	}

	void updateRightSideVertical(Constraint constraint, double rightSide) {
		constraint.setRightSide(rightSide + topInset + bottomInset);
	}

	/**
	 * Update the constraints for vertical insets and alignment.
	 */
	void updateVertical() {
		updateRightSideVertical(minHeightConstraint, minSize.getHeight());
		if (preferredWidthConstraint != null)
			updateRightSideVertical(preferredWidthConstraint, preferredSize.getHeight());
		if (maxWidthConstraint != null)
			updateRightSideVertical(maxWidthConstraint, maxSize.getHeight());
	}

	/**
	* Returns a string that displays the area's layout specification
	* @return string identifies the area's layout specification
	*/
	public String toString() {
		return "Area(" + left.toString() + "," + top.toString() + ","
				+ right.toString() + "," + bottom.toString() + ")";
	}

	/**
	* Construct the area based on the given tabs
	* @param ls the layout specification
	* @param left the left vertical grid line
	* @param top the top horziontal grid line
	* @param right the right vertical grid line
	* @param bottom the bottom horziontal grid line
	*/
	Area(LayoutSpec ls, XTab left, YTab top, XTab right, YTab bottom) {
		this.ls = ls;
		this.left = left;
		this.right = right;
		this.top = top;
		this.bottom = bottom;

		// adds the two essential constraints of the area that make sure that
		// the left x-tab is really
		// to the left of the right x-tab, and the top y-tab really above the
		// bottom y-tab
		// TODO: the preferred size constraints need to have a smaller penalty
		// (not INFINITY as per default)
		minWidthConstraint = ls.linearSpec.addConstraint(-1, left, 1, right, OperatorType.GE, minSize.getWidth());
		minWidthConstraint.Owner = this;
		minWidthConstraint.setName("minWidthConstraint");
		constraints.add(minWidthConstraint);
		minHeightConstraint = ls.linearSpec.addConstraint(-1, top, 1, bottom, OperatorType.GE, minSize.getHeight());
		minHeightConstraint.Owner = this;
		minHeightConstraint.setName("minHeightConstraint");
		constraints.add(minHeightConstraint);

		setGrowPenalties(new Size(0, 0));
 		setShrinkPenalties(Constraint.PREFERRED_SIZE_PENALTY, Constraint.PREFERRED_SIZE_PENALTY);
	}

	/**
	 * Removes the area from its specification.
	 */
	public void remove() {
		for (Constraint c : constraints)
			c.remove();
		ls.getAreas().remove(this);
	}
}
