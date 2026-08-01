package studio.paperwing;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.fail;

import java.awt.Frame;
import java.awt.GraphicsEnvironment;
import java.awt.Window;
import java.io.File;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import me.friwi.jcefmaven.CefAppBuilder;
import me.friwi.jcefmaven.MavenCefAppHandlerAdapter;
import me.friwi.jcefmaven.impl.progress.ConsoleProgressHandler;
import org.cef.CefApp;
import org.cef.CefApp.CefAppState;
import org.cef.CefClient;
import org.cef.browser.CefBrowser;
import org.cef.browser.CefFrame;
import org.cef.handler.CefLoadHandler;
import org.cef.network.CefRequest.TransitionType;
import org.junit.jupiter.api.Test;

public class TestWeb {
    @Test
    void passingWebTest() {
        CefApp app = null;
        CefClient client = null;
        CefBrowser browser = null;
        Frame frame = new Frame(GraphicsEnvironment
            .getLocalGraphicsEnvironment()
            .getDefaultScreenDevice()
            .getDefaultConfiguration()
        );

        frame.setSize(640, 480);
        frame.setUndecorated(true);
        frame.setType(Window.Type.UTILITY);
        frame.setLocation(-999999,-999999);

        try {
            // bootstrap jcefmaven
            CefAppBuilder builder = new CefAppBuilder();
            builder.setInstallDir(new File("jcef-bundle")); // default
            builder.setProgressHandler(new ConsoleProgressHandler()); // also default
            builder.addJcefArgs("--disable-gpu"); // example of startup argument
            builder.getCefSettings().windowless_rendering_enabled = true; // default - select OSR mode.

            // set app handler. dont use CefApp.addAppHandler. it breaks the code on MacOSX.
            builder.setAppHandler(new MavenCefAppHandlerAdapter() {
                @Override
                public void stateHasChanged(CefAppState state) {
                    if (state == CefAppState.TERMINATED) {
                        System.exit(0);
                    }
                }
            });

            // spawn app.
            app = builder.build();

            CompletableFuture<Void> cFuture = new CompletableFuture<>();

            // for this test, if the browser ui's bitmap is not empty, we have succeeded.
            CefLoadHandler loadHandler = new CefLoadHandler() {

                @Override
                public void onLoadEnd(CefBrowser arg0, CefFrame arg1, int arg2) {
                    System.out.println("Loading complete!");
                    cFuture.complete(null);
                }

                @Override
                public void onLoadError(CefBrowser arg0, CefFrame arg1, ErrorCode arg2, String arg3, String arg4) {
                    cFuture.completeExceptionally(new Exception("Loading the page failed: " + arg2.toString()));
                }

                @Override
                public void onLoadStart(CefBrowser arg0, CefFrame arg1, TransitionType arg2) {

                }

                @Override
                public void onLoadingStateChange(CefBrowser arg0, boolean arg1, boolean arg2, boolean arg3) {
                }
            };

            // hook one client. note that cef supports multiple clients, but they need to be
            // properly
            // created like how its done bellow, referenced once and dereferenced properly.
            client = app.createClient();
            client.addLoadHandler(loadHandler);

            // hook the clients browser. one client can have multiple browsers just as one
            // app can have
            // multiple clients. same deal of creating properly, rederencing and
            // dereferencing, and one more thing.
            // the ui is a java.awt.Component, and the browser has many methotds for
            // changing what is happening
            // within it like "goBack()", "goForward()" or "loadURL()". so this is from
            // where most of the magic
            // will come from.
            browser = client.createBrowser("https://learnopengl.com", true, true);

            // because there is no swing context, the browser by default waits for that
            // context to get added. this
            // skips that and lets the page load.
            browser.createImmediately();

            // frame.add(browser.getUIComponent());
            // frame.setVisible(true);

            assertNull(cFuture.get(9, TimeUnit.SECONDS));

            // // finally, do the process for transforming the ui into a bitmap.
            // CompletableFuture<BufferedImage> cBitmap = browser.createScreenshot(false);

            // BufferedImage bitmap = cBitmap.get(9, TimeUnit.SECONDS);

            // boolean passed = false;
            // loop: for (int y = 0; y < bitmap.getHeight(); y++) {
            //     for (int x = 0; x < bitmap.getWidth(); x++) {
            //         int pixel = bitmap.getRGB(x, y);
            //         if (pixel != 0) {
            //             passed = true;
            //             break loop;
            //         }
            //     }
            // }

            // assertTrue(passed);
        } catch (Exception e) {
            fail(e);
        } finally {
            if (browser != null) browser.close(true);
            if (client != null) client.dispose();
            if (app != null) app.dispose();
            if (frame != null) frame.dispose();
        }
    }
}
