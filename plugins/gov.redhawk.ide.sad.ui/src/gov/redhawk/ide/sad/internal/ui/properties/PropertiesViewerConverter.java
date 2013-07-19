/*******************************************************************************
 * This file is protected by Copyright. 
 * Please refer to the COPYRIGHT file distributed with this source distribution.
 *
 * This file is part of REDHAWK IDE.
 *
 * All rights reserved.  This program and the accompanying materials are made available under 
 * the terms of the Eclipse Public License v1.0 which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package gov.redhawk.ide.sad.internal.ui.properties;

import gov.redhawk.ide.sad.internal.ui.properties.model.ViewerProperty;
import gov.redhawk.ide.sad.internal.ui.properties.model.ViewerSequenceProperty;
import gov.redhawk.ide.sad.internal.ui.properties.model.ViewerSimpleProperty;

import org.eclipse.nebula.widgets.xviewer.edit.CellEditDescriptor;
import org.eclipse.nebula.widgets.xviewer.edit.XViewerConverter;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Text;

/**
 * 
 */
public class PropertiesViewerConverter implements XViewerConverter {

	private PropertiesViewerLabelProvider labelProvider;

	public PropertiesViewerConverter(PropertiesViewerLabelProvider labelProvider) {
		this.labelProvider = labelProvider;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.nebula.widgets.xviewer.edit.XViewerConverter#setInput(org.eclipse.swt.widgets.Control, org.eclipse.nebula.widgets.xviewer.edit.CellEditDescriptor, java.lang.Object)
	 */
	public void setInput(Control c, CellEditDescriptor ced, Object selObject) {
		if (ced.getInputField().equals(PropertiesViewerFactory.EXTERNAL.getId()) && selObject instanceof ViewerProperty< ? >) {
			String value = labelProvider.getExternalValue(selObject);
			if (value == null) {
				value = ((ViewerProperty< ? >) selObject).getID();
			}
			setControlValue(c, value);
		} else if (ced.getInputField().equals(PropertiesViewerFactory.SAD_VALUE.getId())) {
			String value = labelProvider.getSadValue(selObject);
			setControlValue(c, value);
		}
	}

	private void setControlValue(Control c, String value) {
		if (value == null) {
			value = "";
		}
		if (c instanceof Combo) {
			Combo combo = (Combo) c;
			combo.setText(value);
			combo.setSelection(new Point(0, combo.getText().length()));
		} else if (c instanceof Text) {
			Text text = (Text) c;
			text.setText(value);
			text.setSelection(new Point(0, text.getText().length()));
		}
	}

	/* (non-Javadoc)
	 * @see org.eclipse.nebula.widgets.xviewer.edit.XViewerConverter#getInput(org.eclipse.swt.widgets.Control, org.eclipse.nebula.widgets.xviewer.edit.CellEditDescriptor, java.lang.Object)
	 */
	public void getInput(Control c, CellEditDescriptor ced, Object selObject) {
		if (ced.getInputField().equals(PropertiesViewerFactory.EXTERNAL.getId())) {
			setExternalValue(c, selObject);
		} else if (ced.getInputField().equals(PropertiesViewerFactory.SAD_VALUE.getId())) {
			setSadValue(c, selObject);
		}
	}

	protected void setSadValue(Control c, Object selObject) {
		String newValue = null;
		if (c instanceof Combo) {
			Combo combo = (Combo) c;
			newValue = combo.getText();
		} else if (c instanceof Text) {
			Text text = (Text) c;
			newValue = text.getText();
		} else {
			return;
		}

		if (selObject instanceof ViewerSimpleProperty) {
			ViewerSimpleProperty simpleProp = (ViewerSimpleProperty) selObject;
			if (simpleProp.checkValue(newValue)) {
				simpleProp.setValue(newValue);
			}
		} else if (selObject instanceof ViewerSequenceProperty) {
			ViewerSequenceProperty seqProp = (ViewerSequenceProperty) selObject;
			try {
				String[] newArray = split(newValue);
				if (seqProp.checkValues(newArray)) {
					seqProp.setValues(newArray);
				}
			} catch (IllegalArgumentException e) {
				// PASS
			}
		}
	}

	public static String[] split(String seqValue) {
		if (seqValue == null || seqValue.isEmpty()) {
			return null;
		}
		if (seqValue.charAt(0) != '[' || seqValue.charAt(seqValue.length() - 1) != ']') {
			throw new IllegalArgumentException("Invalid Array Value.");
		}
		seqValue = seqValue.substring(1, seqValue.length() - 1);
		String[] newArray = seqValue.split(",");
		for (int i = 0; i < newArray.length; i++) {
			newArray[i] = newArray[i].trim();
		}
		return newArray;
	}

	protected void setExternalValue(Control c, Object selObject) {
		String newValue = null;
		if (c instanceof Combo) {
			Combo combo = (Combo) c;
			newValue = combo.getText();
		} else {
			return;
		}

		if (selObject instanceof ViewerProperty< ? >) {
			((ViewerProperty< ? >) selObject).setExternalID(newValue);
		}
	}

}