package nz.ac.auckland.alm.swing.responsive.widgets;

import nz.ac.auckland.alm.swing.ALMLayout;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Marc Janßen on 16.07.2015.
 */
public class WidgetFactory {

    private Map<String, Component> m_widgets = null;

    public WidgetFactory() {
        m_widgets = new HashMap<String, Component>();
    }

    public Component getWidget(String id, JFrame targetsParent, ALMLayout targetLayout) { //TODO: Here I have to copy somehow the Components and keep the origins save
        Component erg = null;
        if (m_widgets.containsKey(id)) {
            erg = m_widgets.get(id);
        } else {
            erg = new JButton(id);
            m_widgets.put(id, erg);
        }
        try {
            erg = copyOf(erg);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        }
        erg.addMouseListener(new PlaceHolder_Listener(targetsParent, targetLayout, id));
        return erg;
    }

    private Component copyOf(Component comp) throws IOException, NoSuchMethodException, InvocationTargetException, IllegalAccessException, InstantiationException {
        Component erg = null;

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        ObjectOutputStream out = new ObjectOutputStream(stream);
        Class[] params = {out.getClass()};
        Method write = comp.getClass().getDeclaredMethod("writeObject", params);
        write.setAccessible(true);
        write.invoke(comp, out);

        byte[] streamResult = stream.toByteArray();
        stream.flush();

        erg = comp.getClass().newInstance();

        ObjectInputStream in = new ObjectInputStream(new ByteArrayInputStream(streamResult));
        Class[] params2 = {in.getClass()};
        Method read = erg.getClass().getMethod("readObject", params2);
        read.setAccessible(true);
        read.invoke(erg, in);

        return erg;
    }

    public Component getOrigin(String id) {
        return m_widgets.get(id);
    }

    public boolean setWidget(String id, Component toSet) {
        if (m_widgets.containsKey(id)) {
            m_widgets.put(id, toSet);
            return true;
        }
        return false;
    }
}
