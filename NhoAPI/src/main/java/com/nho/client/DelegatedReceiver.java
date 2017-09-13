package com.nho.client;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import com.lmax.disruptor.EventFactory;
import com.lmax.disruptor.ExceptionHandler;
import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.WorkerPool;
import com.nhb.common.BaseLoggable;
import com.nho.message.MessageEvent;

/**
 * This class is a worker pool, it got data as byte[] and publish to ringbuffer,
 * MessageHandler will handle those messages
 * 
 * @author bachden
 *
 */
public class DelegatedReceiver extends BaseLoggable implements ExceptionHandler {

	private int numWorkers = 1;

	private WorkerPool<MessageEvent> workerPool;
	private RingBuffer<MessageEvent> ringBuffer;
	private ExecutorService executor;

	private final boolean useRingBuffer;

	private NhoClient client;

	public DelegatedReceiver(NhoClient client, boolean useRingBuffer) {
		this.useRingBuffer = useRingBuffer;
		this.client = client;

		if (this.useRingBuffer) {
			ThreadFactory factory = new ThreadFactory() {
				
				private int id = 1;
				
				@Override
				public Thread newThread(Runnable r) {
					return new Thread(r,String.format("Socket Receiver #%d", this.id++));
				}
			};

			this.executor = new ThreadPoolExecutor(numWorkers, numWorkers, 6L, TimeUnit.SECONDS,
					new LinkedBlockingQueue<Runnable>(), factory);

			NhoWorker[] handlers = new NhoWorker[numWorkers];
			for (int i = 0; i < handlers.length; i++) {
				handlers[i] = new NhoWorker(client);
			}

			this.ringBuffer = RingBuffer.createMultiProducer(new EventFactory<MessageEvent>() {
				@Override
				public MessageEvent newInstance() {
					return new MessageEvent();
				}
			}, 1024);

			this.workerPool = new WorkerPool<MessageEvent>(this.ringBuffer, this.ringBuffer.newBarrier(), this,
					handlers);
		} else {
			this.executor = Executors.newCachedThreadPool();
		}
	}

	public void publish(Object data) {
		if (this.useRingBuffer) {
			long sequence = ringBuffer.next(); // Grab the next sequence
			try {
				MessageEvent event = ringBuffer.get(sequence);
				event.setData(data);
			} finally {
				ringBuffer.publish(sequence);
			}
		} else {
			this.executor.submit(new NhoWorker(this.client, new MessageEvent(data)));
		}
	}

	public void start() {
		if (this.useRingBuffer) {
			if (this.workerPool.isRunning()) {
				return;
			}
			this.workerPool.start(this.executor);
		}
	}

	public void shutdown() {
		if (this.useRingBuffer) {
			if (this.workerPool.isRunning()) {
				this.workerPool.halt();
			}
		} else {
			this.executor.shutdown();
			try {
				if (this.executor.awaitTermination(2, TimeUnit.SECONDS)) {
					this.executor.shutdownNow();
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void handleEventException(Throwable ex, long sequence, Object event) {
		getLogger().error("Error occurs when handling event: ", ex);
	}

	@Override
	public void handleOnStartException(Throwable ex) {
		getLogger().error("Error occurs when starting event handler: ", ex);
	}

	@Override
	public void handleOnShutdownException(Throwable ex) {
		getLogger().error("Error occurs when shutting down event handler: ", ex);
	}
}
