package se.liu.simjolucul.dopeslope.handlers.interaction;

import javax.swing.*;
import java.awt.event.ActionEvent;

public class QuitAction extends AbstractAction
    {

	@Override public void actionPerformed(final ActionEvent e) {
	    int result = JOptionPane.showOptionDialog(
		    null,
		    "Är du säker på att du vill avsluta spelet:\n",
		    "Avsluta",
		    JOptionPane.YES_NO_CANCEL_OPTION,
		    JOptionPane.QUESTION_MESSAGE,
		    null,
		    new Object[] { "Ja", "Nej" },
		    "Nej"
	    );

	    if (result == JOptionPane.YES_OPTION) {
		System.exit(0);
	    }
	}
    }