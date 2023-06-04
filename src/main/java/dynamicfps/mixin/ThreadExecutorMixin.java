package dynamicfps.mixin;

import net.minecraft.util.thread.BlockableEventLoop;
import org.spongepowered.asm.mixin.Mixin;

import java.util.concurrent.locks.LockSupport;

@Mixin(targets = {"net.minecraft.server.level.ServerChunkCache$MainThreadExecutor"})
public abstract class ThreadExecutorMixin
		extends BlockableEventLoop<Runnable> {

	protected ThreadExecutorMixin(String name) {
		super(name);
	}

	/**
	 @author Julian Dunskus
	 reason: The vanilla version is simply broken, taking up way too many resources in the background.
	 */
	@Override
	public void waitForTasks() {
		// yield() here is a terrible idea
		LockSupport.parkNanos("waiting for tasks", 500_000); // increased wait to 0.5 ms
	}
}
