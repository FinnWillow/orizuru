package studio.paperwing;

import com.artemis.ComponentMapper;
import com.artemis.World;
import com.artemis.WorldConfiguration;
import com.artemis.WorldConfigurationBuilder;
import com.artemis.annotations.All;
import com.artemis.systems.IteratingSystem;
import org.junit.jupiter.api.Test;
import studio.paperwing.components.Hello;

public class TestECS {
    // step 1: create a component (inside my.game.components)

    // step 2: create a system.
    @All(Hello.class)
    class HelloWorldSystem extends IteratingSystem {
        protected ComponentMapper<Hello> mHello;

        @Override
        protected void process(int id) {
            System.out.println(mHello.get(id).message);
        }
    }

    // step 3: setup a world and process the systems.
    @Test
    void passingWorld() {
        // Register any plugins and set up the world.
        WorldConfiguration setup = new WorldConfigurationBuilder()
            .with(new HelloWorldSystem())
            .build();

        // create the world
        World world = new World(setup);

        // create a entity.
        int entityId = world.create();
        world.edit(entityId).create(Hello.class).message = "Hello World!\n";

        world.process();
    }
}
