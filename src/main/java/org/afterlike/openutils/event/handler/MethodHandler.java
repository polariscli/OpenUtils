package org.afterlike.openutils.event.handler;

import java.lang.reflect.Method;
import org.afterlike.openutils.event.api.Event;
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

public final class MethodHandler {
	private final Object parent;
	private Method method;
	public MethodHandler(final Method method, final Object parent) {
		this.method = method;
		this.parent = parent;
		if (!this.isValid(this.method)) {
			this.method = null;
			return;
		}
		this.method.setAccessible(true);
	}

	private boolean isValid(final Method method) {
		if (method.getParameterCount() == 0)
			return false;
		final Class<?> parameterTypes = method.getParameterTypes()[0];
		final Class<?>[] interfaces = parameterTypes.getInterfaces();
		final Class<?> superclass = parameterTypes.getSuperclass();
		if (interfaces.length != 0)
			return this.isValid(interfaces[0]);
		if (this.isValid(parameterTypes) || this.isValid(superclass))
			return true;
		return this.isValid(superclass.getInterfaces()[0]);
	}

	private boolean isValid(final Class<?> clazz) {
		return clazz.equals(Event.class);
	}

	public void call(final Event event) {
		if (this.method == null)
			return;
		try {
			this.method.invoke(parent, event);
		} catch (final Exception ignored) {
		}
	}
}
