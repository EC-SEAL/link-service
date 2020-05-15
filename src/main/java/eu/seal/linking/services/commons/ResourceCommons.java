package eu.seal.linking.services.commons;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class ResourceCommons
{
    public static void writeFileContent(String path, String content) throws IOException
    {
        FileWriter fileWriter = new FileWriter(path);
        fileWriter.write(content);
        fileWriter.close();
    }

    public static long getFileLastUpdate(String path)
    {
        File file = new File(path);
        return file.lastModified();
    }
}
