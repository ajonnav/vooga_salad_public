package api;

import java.io.File;


public interface ISystem extends ISerializable {
    void update ();

    void evaluate (File f);

    File serialize ();
}
