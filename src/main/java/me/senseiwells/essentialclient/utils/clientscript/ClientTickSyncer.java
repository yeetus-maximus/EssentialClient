package me.senseiwells.essentialclient.utils.clientscript;

import me.senseiwells.arucas.api.ISyntax;
import me.senseiwells.arucas.throwables.CodeError;
import me.senseiwells.arucas.utils.impl.ArucasThread;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class ClientTickSyncer {
	private static final Set<CountDownLatch> OBJECTS_TO_SYNC = new HashSet<>();

	@SuppressWarnings("all")
	public synchronized static void syncToTick() throws CodeError {
		CountDownLatch countDownLatch = new CountDownLatch(1);
		OBJECTS_TO_SYNC.add(countDownLatch);

		if (Thread.currentThread() instanceof ArucasThread) {
			try {
				countDownLatch.await(50, TimeUnit.MILLISECONDS);
				return;
			}
			catch (InterruptedException e) {
				throw new CodeError(CodeError.ErrorType.INTERRUPTED_ERROR, "", ISyntax.EMPTY);
			}
		}
		throw new RuntimeException("Tried to sync non Arucas Thread");
	}

	public synchronized static void triggerSync() {
		for (CountDownLatch countDownLatch : OBJECTS_TO_SYNC) {
			countDownLatch.countDown();
		}
		OBJECTS_TO_SYNC.clear();
	}
}
