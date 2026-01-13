package org.afterlike.openutils.event.handler;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
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

public final class ListenerHandler<T extends Event> {
	private final Type type;
	private final Listener<T> listener;
	private final Class<?> typeClass;
	public ListenerHandler(final Type type, final Listener<T> listener) {
		this.type = type;
		this.listener = listener;
		if (type instanceof Class<?>)
			this.typeClass = (Class<?>) type;
		else if (type instanceof ParameterizedType)
			this.typeClass = (Class<?>) ((ParameterizedType) this.type).getActualTypeArguments()[0];
		else
			throw new IllegalArgumentException("Type must be a class or a parameterized type");
	}

	@SuppressWarnings("unchecked")
	public void call(final Event event) {
		if (!this.typeClass.equals(event.getClass()) && !this.typeClass.equals(Event.class))
			return;
		this.listener.call((T) event);
	}

	public Class<?> getTypeClass() {
		return typeClass;
	}

	public Type getType() {
		return type;
	}
}
