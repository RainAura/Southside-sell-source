package net.optifine.util;

import java.lang.management.BufferPoolMXBean;
import java.lang.management.ManagementFactory;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.function.LongSupplier;

import net.lenni0451.reflect.Methods;
import net.optifine.Config;

public class NativeMemory {
	private static final LongSupplier bufferAllocatedSupplier = makeLongSupplier(new String[][] {{"sun.misc.SharedSecrets", "getJavaNioAccess", "getDirectBufferPool", "getMemoryUsed"}, {"jdk.internal.misc.SharedSecrets", "getJavaNioAccess", "getDirectBufferPool", "getMemoryUsed"}}, makeDefaultAllocatedSupplier());
	private static final LongSupplier bufferMaximumSupplier = makeLongSupplier(new String[][] {{"sun.misc.VM", "maxDirectMemory"}, {"jdk.internal.misc.VM", "maxDirectMemory"}}, makeDefaultMaximumSupplier());

	public static long getBufferAllocated() {
		return bufferAllocatedSupplier == null ? -1L : bufferAllocatedSupplier.getAsLong();
	}

	public static long getBufferMaximum() {
		return bufferMaximumSupplier == null ? -1L : bufferMaximumSupplier.getAsLong();
	}

	private static LongSupplier makeLongSupplier(String[][] paths, LongSupplier defaultSupplier)
	{
		final List<Throwable> list = new ArrayList<>();

		for (final String[] string : paths) {
			try {
				LongSupplier longsupplier = makeLongSupplier(string);
				if (longsupplier != null) return longsupplier;
			} catch (Throwable throwable) {
				list.add(throwable);
			}
		}

		for (Throwable throwable1 : list)
		{
			Config.warn("(Reflector) " + throwable1.getClass().getName() + ": " + throwable1.getMessage());
		}

		return defaultSupplier;
	}

	private static LongSupplier makeLongSupplier(String[] path) throws Exception
	{
		if (path.length < 2) {
			return null;
		} else {
			final Class<?> oclass = Class.forName(path[0]);
			Method method = Methods.getDeclaredMethod(oclass, path[1]);
			Object object = null;

			for (int i = 2; i < path.length; ++i) {
				String s = path[i];
				object = Methods.invoke(object, method);
				method = Methods.getDeclaredMethod(object.getClass(), s);
			}

			final Method method1 = method;
			final Object object1 = object;
			return new LongSupplier() {
				private boolean disabled = false;
				public long getAsLong() {
					if (this.disabled) {
						return -1L;
					} else {
						try {
							return Methods.invoke(object1, method1);
						} catch (Throwable throwable) {
							Config.warn("(Reflector) " + throwable.getClass().getName() + ": " + throwable.getMessage());
							this.disabled = true;
							return -1L;
						}
					}
				}
			};
		}
	}

	private static BufferPoolMXBean getDirectBufferPoolMXBean() {
		for (final BufferPoolMXBean bufferpoolmxbean : ManagementFactory.getPlatformMXBeans(BufferPoolMXBean.class)) {
			if (Config.equals(bufferpoolmxbean.getName(), "direct")) {
				return bufferpoolmxbean;
			}
		}

		return null;
	}

	private static LongSupplier makeDefaultAllocatedSupplier() {
		final BufferPoolMXBean bufferpoolmxbean = getDirectBufferPoolMXBean();
		return bufferpoolmxbean == null ? null : bufferpoolmxbean::getMemoryUsed;
	}

	private static LongSupplier makeDefaultMaximumSupplier() {
		return () -> Runtime.getRuntime().maxMemory();
	}
}