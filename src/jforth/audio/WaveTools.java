package jforth.audio;

import jforth.Utilities;

import javax.sound.sampled.*;
import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * from here: ftp://sccn.ucsd.edu/pub/virtualmedia/AePlayWave.java
 */

public class WaveTools
{
    private static final Charset thisCharset = StandardCharsets.ISO_8859_1;

//    /**
//     * Play wave file from disk
//     * @param wavfile a disk file
//     */
//    public static void playSound (String wavfile)
//    {
//        try
//        {
//            File soundFile = new File(wavfile);
//            InputStream inp = new BufferedInputStream(new FileInputStream(soundFile));
//            playSound(inp);
//        }
//        catch (Exception e)
//        {
//            e.printStackTrace();
//        }
//    }

//    /**
//     * Play wave from an array
//     * @param data Array containing wav data+header
//     */
//    public static Clip playSound (byte[] data)
//    {
//        try
//        {
//            InputStream inp  = new BufferedInputStream(new ByteArrayInputStream (data));
//            return playSound(inp);
//        }
//        catch (Exception e)
//        {
//            return null;
//        }
//    }
//
//    /**
//     * Play wave from Stream
//     * @param inp Stream containing wave data + header
//     */
//    public static Clip playSound (InputStream inp)
//    {
//        try
//        {
//            AudioInputStream audioIn = AudioSystem.getAudioInputStream(inp);
//            Clip clip = AudioSystem.getClip();
//            clip.open(audioIn);
//            clip.start();
//            return clip;
//        }
//        catch (Exception e)
//        {
//            return null;
//        }
//    }

    /**
     * Run the SAM module and convert a text to speech data
     * @param words Text to be spoken
     * @return String containing wave file
     * @throws Exception if smth gone wrong
     */
    public static String SAMtoWaveString (String words) throws Exception
    {
        words = words.replace('-','_');
        String res = Utilities.extractResource("sam.exe");
        Process process = new ProcessBuilder(
                res, "-stdout", "dummy", words)
                .start();
        InputStream is = process.getInputStream();

        StringBuilder textBuilder = new StringBuilder();
        try (Reader reader = new BufferedReader(new InputStreamReader
                (is, thisCharset)))
        {
            int c;
            while ((c = reader.read()) != -1)
            {
                textBuilder.append((char) c);
            }
        }
        is.close();
        return textBuilder.toString();
    }

    /////////////////////////////////////////////////////////////////////////////////

    /**
     * Load wave file and start playing
     * @param file the file object
     * @return tha running clip
     * @throws Exception if smth gone wrong
     */
    public static Clip playWave (File file, boolean cont) throws Exception
    {
        final Clip clip = (Clip) AudioSystem.getLine (new Line.Info (Clip.class));
        clip.open (AudioSystem.getAudioInputStream (file));
        if (cont)
            clip.loop (Clip.LOOP_CONTINUOUSLY);
        clip.start ();
        return clip;
    }

    public static Clip playWave (byte[] data, boolean cont) throws Exception
    {
        final Clip clip = (Clip) AudioSystem.getLine (new Line.Info (Clip.class));
        InputStream inp  = new BufferedInputStream(new ByteArrayInputStream (data));
        clip.open (AudioSystem.getAudioInputStream (inp));
        if (cont)
            clip.loop (Clip.LOOP_CONTINUOUSLY);
        clip.start ();
        return clip;
    }

    /**
     * Stop and close an audio clip
     * @param clip the playing clip
     */
    public static void stopWave (Clip clip)
    {
        clip.stop ();
        clip.close ();
    }
}
