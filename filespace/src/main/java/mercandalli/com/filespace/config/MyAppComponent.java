package mercandalli.com.filespace.config;

import android.app.Application;

import javax.inject.Singleton;

import dagger.Component;
import mercandalli.com.filespace.manager.file.FileManager;
import mercandalli.com.filespace.manager.file.FileModule;
import mercandalli.com.filespace.ui.activities.MainActivity;

@Singleton
@Component(
        modules = {
                MyAppModule.class,
                FileModule.class
        }
)
public interface MyAppComponent {

    //Injections
    void inject(MyApp app);

    void inject(MainActivity mainActivity);

    //void inject(HomeFragment homeFragment);

    //Providers
    Application provideApp();

    FileManager provideFileManager();
}
