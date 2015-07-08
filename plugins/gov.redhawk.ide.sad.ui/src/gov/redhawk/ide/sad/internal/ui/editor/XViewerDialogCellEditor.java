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
package gov.redhawk.ide.sad.internal.ui.editor;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Layout;

public class XViewerDialogCellEditor extends XViewerCellEditor {

	private Label label;
	private Button button;
	private Object value;

	/**
	 * Internal class for laying out the dialog.
	 */
	private class DialogEditorLayout extends Layout {
		@Override
		public void layout(Composite editor, boolean force) {
			Rectangle bounds = editor.getClientArea();
			Point size = button.computeSize(SWT.DEFAULT, SWT.DEFAULT, force);
			if (label != null) {
				label.setBounds(0, 0, bounds.width - size.x, bounds.height);
			}
			button.setBounds(bounds.width - size.x, 0, size.x, bounds.height);
		}

		@Override
		public Point computeSize(Composite editor, int wHint, int hHint, boolean force) {
			if (wHint != SWT.DEFAULT && hHint != SWT.DEFAULT) {
				return new Point(wHint, hHint);
			}
			Point contentsSize = label.computeSize(SWT.DEFAULT, SWT.DEFAULT, force);
			Point buttonSize = button.computeSize(SWT.DEFAULT, SWT.DEFAULT, force);
			// Just return the button width to ensure the button is not clipped
			// if the label is long.
			// The label will just use whatever extra width there is
			Point result = new Point(buttonSize.x, Math.max(contentsSize.y, buttonSize.y));
			return result;
		}
	}

	public XViewerDialogCellEditor(Composite parent, int style) {
		super(parent, style);
		value = null;
		createControl();
	}

	protected void createControl() {
		label = new Label(this, SWT.SHADOW_NONE);

		button = new Button(this, SWT.DOWN);
		button.setText("...");
		button.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				Object newValue = openDialogBox();
				doSetValue(newValue);
			}

		});

		setLayout(new DialogEditorLayout());
	}

	@Override
	protected void doSetValue(Object value) {
		this.value = value;
		updateContents(value);
	}

	@Override
	protected Object doGetValue() {
		return value;
	}

	@Override
	public boolean setFocus() {
		return button.setFocus();
	}

	protected void updateContents(Object value) {
		String text = "";
		if (value != null) {
			text = value.toString();
		}
		label.setText(text);
	}

	protected Object openDialogBox() {
		return null;
	}

	@Override
	protected Control getMainControl() {
		return button;
	}
}
