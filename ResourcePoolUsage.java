import java.io.File;

/**
 * ResourcePoolUsage
 */
public class ResourcePoolUsage {

    public static void main(String[] args) {
        ResourcePool<File> pool = new ResourcePool<>(() -> new File("cool"));

        // This is creating while not returning value to the pool
        File takenNotReturned = pool.take();

        // Thhis is loaning while not calling .get() on the returned resource, it is not
        // created yet, but is returned to the pool in the close with try-with-resources
        Resource<File> loanedResource;
        try (Resource<File> given = pool.loan()) {
            loanedResource = given;
            System.out.println("File not created yet");
        }

        // This is loaning from the pool, we get exactly the same pointer as before
        // but this time we are calling .get() and the resource is created for real.
        // We then return it to the pool
        File createdFile;
        try (Resource<File> given = pool.loan()) {
            createdFile = given.get();
            System.out.println("File created here");
            System.out.println("Same as previous resource: " + (given == loanedResource));
        }

        // The same file as created in the previous loan
        File takenAlreadyExisting = pool.take();
        System.out.println("Same as previous file: " + (createdFile == takenAlreadyExisting));
    }
}