package nz.ac.auckland.alm.swing.responsive;

import javax.swing.*;
import java.awt.*;

/**
 * Created by Marc Janßen on 30.06.2015.
 */
public interface IResponsivePart {

    public Dimension getDesiredSize();

    public Algebra getAlgebra();
}
