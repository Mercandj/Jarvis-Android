package mercandalli.com.filespace.persistence;

import android.content.Context;

import mercandalli.com.filespace.models.better.FileModel;

/**
 * Created by Jonathan on 24/10/2015.
 */
public class FileLocalDataApi implements LocalDataApi<FileModel> {
    @Override
    public FileModel get(Context context, int id) {
        return null;
    }

    @Override
    public void add(Context context, FileModel entity) {

    }

    @Override
    public FileModel update(Context context, FileModel entity) {
        return null;
    }

    @Override
    public boolean delete(Context context, FileModel entity) {
        return false;
    }

    @Override
    public long count(Context context) {
        return 0;
    }
}
