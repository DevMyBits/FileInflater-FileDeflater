import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

/**
 * Créer le     : vendredi 13 juin 2025
 * Auteur       : Yoann Meclot (DevMyBits)
 * E-mail       : devmybits@gmail.com
 */
public final class FileInflater implements Closeable, AutoCloseable
{
    private final OutputStream output;

    private final byte[] temp = new byte[8];

    public FileInflater(final File out) throws IOException
    {
        output = new FileOutputStream(out, true);
    }

    public void write(int value) throws IOException
    {
        temp[0] = (byte)((value >>> 24) & 0xFF);
        temp[1] = (byte)((value >>> 16) & 0xFF);
        temp[2] = (byte)((value >>> 8) & 0xFF);
        temp[3] = (byte)((value) & 0xFF);

        output.write(temp, 0, 4);
    }

    public void write(long value) throws IOException
    {
        temp[0] = (byte)((value >>> 56) & 0xFF);
        temp[1] = (byte)((value >>> 48) & 0xFF);
        temp[2] = (byte)((value >>> 40) & 0xFF);
        temp[3] = (byte)((value >>> 32) & 0xFF);
        temp[4] = (byte)((value >>> 24) & 0xFF);
        temp[5] = (byte)((value >>> 16) & 0xFF);
        temp[6] = (byte)((value >>> 8) & 0xFF);
        temp[7] = (byte)((value) & 0xFF);

        output.write(temp, 0, 8);
    }

    public void write(boolean value) throws IOException
    {
        output.write(value ? 2 : 1);
    }

    public void write(String value) throws IOException
    {
        byte[] data = value.getBytes(StandardCharsets.UTF_8);
        write(data);
    }

    public void write(byte[] value) throws IOException
    {
        if (value.length == 0) throw new IOException("Unable to write empty data bytes");
        write(value.length);
        output.write(value);
    }

    public void write(File in) throws IOException
    {
        if (in == null || !in.exists()) throw new IOException("Wrong file");
        if (in.isDirectory()) throw new IOException("File is a directory");

        write(in.length());

        byte[] buffer = new byte[8196];
        FileInputStream input = new FileInputStream(in);

        int count;
        while ((count = input.read(buffer)) != -1) output.write(buffer, 0, count);

        input.close();
    }

    @Override
    public void close() throws IOException
    {
        output.close();
        Arrays.fill(temp, (byte) 0);
    }
}
