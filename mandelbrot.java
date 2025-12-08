import javax.imageio.ImageIO;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * Mandelbrot fractal renderer for Java 21
 * Demonstrates resource limits and computational intensity
 */
public class mandelbrot {
    // Image dimensions
    private static final int WIDTH = 3840;
    private static final int HEIGHT = 2160;

    // Complex plane bounds
    private static final double MIN_RE = -2.5;
    private static final double MAX_RE = 1.0;
    private static final double MIN_IM = -1.0;
    private static final double MAX_IM = 1.0;

    // Maximum iterations for convergence test
    private static final int MAX_ITERATIONS = 1000;

    public static void main(String[] args) {
        String outputFile = args.length > 0 ? args[0] : "mandelbrot.png";

        System.out.println("Generating Mandelbrot fractal...");
        System.out.printf("Resolution: %dx%d pixels%n", WIDTH, HEIGHT);
        System.out.printf("Max iterations: %d%n", MAX_ITERATIONS);

        long startTime = System.currentTimeMillis();

        BufferedImage image = computeMandelbrot();

        long computeTime = System.currentTimeMillis() - startTime;
        System.out.printf("Computation completed in %.2f seconds%n", computeTime / 1000.0);

        try {
            savePNG(image, outputFile);
            System.out.printf("Saved to: %s%n", outputFile);
        } catch (IOException e) {
            System.err.println("Error saving image: " + e.getMessage());
            System.exit(1);
        }
    }

    /**
     * Compute the Mandelbrot set for the configured bounds
     */
    private static BufferedImage computeMandelbrot() {
        BufferedImage image = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);

        // Use parallel streams for performance (Java 21 virtual threads would work here too)
        java.util.stream.IntStream.range(0, HEIGHT).parallel().forEach(y -> {
            for (int x = 0; x < WIDTH; x++) {
                // Map pixel coordinates to complex plane
                double re = MIN_RE + (x * (MAX_RE - MIN_RE)) / WIDTH;
                double im = MIN_IM + (y * (MAX_IM - MIN_IM)) / HEIGHT;

                // Compute iterations until escape or max iterations
                int iterations = mandelbrotIterations(re, im);

                // Color based on iteration count
                Color color = getColor(iterations);
                image.setRGB(x, y, color.getRGB());
            }
        });

        return image;
    }

    /**
     * Compute number of iterations before escape for complex point (re, im)
     *
     * The Mandelbrot set consists of complex numbers c for which the sequence:
     * z_0 = 0
     * z_{n+1} = z_n^2 + c
     * does not diverge (i.e., |z_n| remains bounded)
     */
    private static int mandelbrotIterations(double cRe, double cIm) {
        double zRe = 0.0;
        double zIm = 0.0;
        int iterations = 0;

        // Escape radius is 2.0 (squared = 4.0 for efficiency)
        while (zRe * zRe + zIm * zIm <= 4.0 && iterations < MAX_ITERATIONS) {
            // Compute z = z^2 + c
            double newRe = zRe * zRe - zIm * zIm + cRe;
            double newIm = 2 * zRe * zIm + cIm;

            zRe = newRe;
            zIm = newIm;
            iterations++;
        }

        return iterations;
    }

    /**
     * Map iteration count to color using smooth gradient
     */
    private static Color getColor(int iterations) {
        if (iterations == MAX_ITERATIONS) {
            // Points in the set are black
            return Color.BLACK;
        }

        // Create smooth color gradient
        double t = (double) iterations / MAX_ITERATIONS;

        // Use HSB color space for smooth gradients
        float hue = (float) (0.6 + t * 0.4); // Blue to red
        float saturation = 0.8f;
        float brightness = (float) (0.2 + t * 0.8);

        return Color.getHSBColor(hue, saturation, brightness);
    }

    /**
     * Save BufferedImage as PNG file
     */
    private static void savePNG(BufferedImage image, String filename) throws IOException {
        File outputFile = new File(filename);
        ImageIO.write(image, "PNG", outputFile);
    }
}
