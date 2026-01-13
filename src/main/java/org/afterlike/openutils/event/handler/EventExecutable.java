package org.afterlike.openutils.event.handler;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import org.afterlike.openutils.event.api.Event;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
/*
 * Derived from AzuraClient’s EventBus
 * https://github.com/AzuraClient/Azura-Event-Bus
 *
 * MIT License
 *
 * Copyright (c) 2025 Azura
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

public final class EventExecutable {
	private static final Logger logger = LogManager.getLogger(EventExecutable.class);
	// Parental object
	private final Object parent;
	// MethodHandler instance for registered methods
	private MethodHandler method;
	// ListenerHandler instance for registered listeners
	private ListenerHandler<? extends Event> listener;
	private final int priority;
	public EventExecutable(final Method method, final Object parent, final int eventPriority) {
		this(method, null, parent, eventPriority);
	}

	public EventExecutable(final Field field, final Object parent, final int eventPriority) {
		this(null, field, parent, eventPriority);
	}

	public <U extends Event> EventExecutable(final Class<U> clazz, final Listener<U> listener,
			final Object parent, final int eventPriority) {
		this((Method) null, null, parent, eventPriority);
		this.listener = new ListenerHandler<>(clazz, listener);
	}

	public EventExecutable(final Method method, final Field field, final Object parent,
			final int priority) {
		this.parent = parent;
		this.priority = priority;
		// Registering a listener if the field isn't null
		if (field != null) {
			try {
				field.setAccessible(true);
				this.listener = new ListenerHandler<>(field.getGenericType(),
						(Listener<?>) field.get(parent));
			} catch (final Exception e) {
				this.listener = null;
				logger.error(e);
			}
		} else {
			this.listener = null;
		}
		// Registering the method if it isn't null
		if (method != null && method.getParameterCount() == 1) {
			this.method = new MethodHandler(method, parent);
		}
	}

	public Object getParent() {
		return parent;
	}

	public int getPriority() {
		return priority;
	}

	public void call(final Event event) {
		if (listener != null)
			listener.call(event);
		if (method != null)
			method.call(event);
	}
}
