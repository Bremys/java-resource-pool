import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Supplier;

class ResourcePool<T> {

    private class PoolResource implements Resource<T> {
        private T resource;
        private AtomicBoolean initialized = new AtomicBoolean();

        @Override
        public void close() {
            ResourcePool.this.queue.offer(this);
        }

        @Override
        public T get() {
            if (initialized.compareAndSet(false, true)) {
                this.resource = ResourcePool.this.supp.get();
            }
            return this.resource;
        }
    }

    private BlockingQueue<Resource<T>> queue = new LinkedBlockingQueue<>();
    private Supplier<T> supp;

    public ResourcePool(Supplier<T> supp) {
        this.supp = supp;
    }

    /**
     * Get a resource from the pool with no intention of giving it back
     */
    public T take() {
        return loan().get(); // Loan without returning
    }

    /**
     * Get a resource from the pool with the abillity to return the resource
     */
    public Resource<T> loan() {
        Resource<T> result = queue.poll();
        if (result == null) {
            result = new PoolResource();
        }
        return result;
    }

}