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
package gov.redhawk.ide.graphiti.dcd.internal.ui;

import gov.redhawk.ide.debug.LocalSca;
import gov.redhawk.model.sca.IDisposable;
import gov.redhawk.model.sca.ScaConnection;
import gov.redhawk.model.sca.ScaDevice;
import gov.redhawk.model.sca.ScaDeviceManager;
import gov.redhawk.model.sca.ScaPackage;
import gov.redhawk.model.sca.ScaUsesPort;

import java.util.Collection;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.emf.ecore.util.EContentAdapter;

/**
 * 
 */
public class ScaGraphitiModelAdapter extends EContentAdapter {

	private final GraphitiDcdModelMap modelMap;

	public ScaGraphitiModelAdapter(final GraphitiDcdModelMap modelMap) {
		this.modelMap = modelMap;
	}

	@Override
	public void notifyChanged(final Notification notification) {
		super.notifyChanged(notification);
		if (notification.getNotifier() instanceof ScaDeviceManager) {
			switch (notification.getFeatureID(LocalSca.class)) {
			case ScaPackage.SCA_DEVICE_MANAGER__ALL_DEVICES:
				switch (notification.getEventType()) {
				case Notification.REMOVE:
					Object oldVal = notification.getOldValue();
					if (oldVal != null) {
						this.modelMap.remove((ScaDevice< ? >) oldVal);
					}
					break;
				default:
					break;
				}
				break;
			default:
				break;
			}

		} else if (notification.getNotifier() instanceof ScaDevice) {
			switch (notification.getFeatureID(ScaDevice.class)) {
			case ScaPackage.SCA_DEVICE__IDENTIFIER:
				switch (notification.getEventType()) {
				case Notification.SET:
					this.modelMap.add((ScaDevice< ? >) notification.getNotifier());
					break;
				default:
					break;
				}
				break;
			case ScaPackage.SCA_DEVICE__STARTED:
				ScaDevice< ? > scaDevice = (ScaDevice< ? >) notification.getNotifier();
				final Boolean started = (Boolean) notification.getNewValue();
				this.modelMap.startStopDevice(scaDevice, started);
				break;
			default:
				break;
			}
			switch (notification.getFeatureID(IDisposable.class)) {
			case ScaPackage.IDISPOSABLE__DISPOSED:
				if (notification.getNotifier() instanceof Notifier) {
					final Notifier notifier = (Notifier) notification.getNotifier();
					notifier.eAdapters().remove(this);
				}
				break;
			default:
				break;
			}
		} else if (notification.getNotifier() instanceof ScaUsesPort) {
			switch (notification.getFeatureID(ScaUsesPort.class)) {
			case ScaPackage.SCA_USES_PORT__CONNECTIONS:
				switch (notification.getEventType()) {
				case Notification.ADD:
					Object newVal = notification.getNewValue();
					if (newVal != null) {
						this.modelMap.add((ScaConnection) newVal);
					}
					break;
				case Notification.ADD_MANY:
					for (final Object obj : (Collection< ? >) notification.getNewValue()) {
						if (obj != null) {
							this.modelMap.add((ScaConnection) obj);
						}
					}
					break;
				case Notification.REMOVE:
					Object oldVal = notification.getOldValue();
					if (oldVal != null) {
						this.modelMap.remove((ScaConnection) oldVal);
					}
					break;
				case Notification.REMOVE_MANY:
					for (final Object obj : (Collection< ? >) notification.getOldValue()) {
						if (obj != null) {
							this.modelMap.remove((ScaConnection) obj);
						}
					}
					break;
				default:
					break;
				}
				break;
			default:
				break;
			}
		}
	}

	@Override
	public void addAdapter(final Notifier notifier) {
		if (notifier instanceof ScaDeviceManager || notifier instanceof ScaDevice || notifier instanceof ScaUsesPort) {
			super.addAdapter(notifier);
		}

	}
}
