import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

/**
 * Créer le     : vendredi 13 juin 2025
 * Auteur       : Yoann Meclot (MSay2)
 * E-mail       : yoannmeclot@hotmail.com
 */
public final class FileDeflater implements Closeable, AutoCloseable
{
    private final InputStream input;

    private final byte[] temp = new byte[8];

    public FileDeflater(final File file) throws IOException
    {
        if (file == null || !file.exists()) throw new IOException("Wrong file");

        input = new FileInputStream(file);
    }

    public int available() throws IOException
    {
        return input.available();
    }

    public int readInt() throws IOException
    {
        if (input.read(temp, 0, 4) <= 0) return -1;
        return ((temp[0] & 0xFF) << 24) + ((temp[1] & 0xFF) << 16) +
                ((temp[2] & 0xFF) << 8) + (temp[3] & 0xFF);
    }

    public long readLong() throws IOException
    {
        if (input.read(temp, 0, 8) <= 0) return -1L;
        return (((long) (temp[0] & 0xFF) << 56) + ((long)(temp[1] & 0xFF) << 48) +
            ((long)(temp[2] & 0xFF) << 40) + ((long)(temp[3] & 0xFF) << 32) +
            ((long)(temp[4] & 0xFF) << 24) + (long)((temp[5] & 0xFF) << 16) +
            (long)((temp[6] & 0xFF) << 8) + (long)((temp[7] & 0xFF)));
    }

    public boolean readBoolean() throws IOException
    {
        if (input.read(temp, 0, 1) <= 0) return false;
        return temp[0] == 2;
    }

    public byte[] readBytes() throws IOException
    {
        int length = readInt();
        if (length <= 0) return null;

        byte[] data = new byte[length];
        if (input.read(data) <= 0) return null;

        return data;
    }

    public String readString() throws IOException
    {
        byte[] data = readBytes();
        if (data == null || data.length == 0) return null;

        return new String(data);
    }

    public void readFile(File out) throws IOException
    {
        if (out == null) throw new IOException();

        File parent = out.getParentFile();
        if (parent != null && !parent.mkdirs()) throw new IOException("Unable to create folders tree.");

        long length = readLong();
        if (length < 0) throw new IOException("No size found for reading file.");

        byte[] buffer = new byte[8196];
        FileOutputStream output = new FileOutputStream(out);
        if (length > 8196)
        {
            int count = 8196;
            while (length > 0 && input.read(buffer, 0, count) != -1) {
                output.write(buffer, 0, count);
                length -= count;

                if (length < count) count = (int) length;
            }
            output.close();
        }
        else
        {
            while (input.read(buffer, 0, (int) length) != -1) output.write(buffer, 0, (int) length);
            output.close();
        }
    }

    @Override
    public void close() throws IOException
    {
        input.close();
        Arrays.fill(temp, (byte) 0);
    }
}
