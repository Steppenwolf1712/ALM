package nz.ac.auckland.alm;


public interface IArea {
    IArea clone(XTab clonedLeft, YTab clonedTop, XTab clonedRight, YTab clonedBottom);

    XTab getLeft();
    YTab getTop();
    XTab getRight();
    YTab getBottom();

    void setLeft(XTab value);
    void setRight(XTab value);
    void setTop(YTab value);
    void setBottom(YTab value);
    void setLeftRight(XTab left, XTab right);
    void setTopBottom(YTab top, YTab bottom);

    void attachedToLayoutSpec(LayoutSpec layoutSpec);
    void detachedFromLinearSpec(LayoutSpec layoutSpec);
}