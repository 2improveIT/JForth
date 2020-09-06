package jforth.waves;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.SourceDataLine;
import java.util.HashMap;

public class SynthTone
{
    public static final int SAMPLE_RATE = 11000;
    public static final float SECONDS = 0.5f;
    private static final int SLEN = (int)(SECONDS * SAMPLE_RATE);
    
    private static final byte[] sin = new byte[SLEN];
    private static final byte[] pause = new byte[SLEN];

    private static final HashMap<Character, byte[]> dtmfMap = new HashMap<>();

    static final AudioFormat af =
            new AudioFormat (SAMPLE_RATE, 8, 1, true, true);

    static
    {
        makeSingleWave (1000d, sin);

        makeDoubleWave (1209, 697, '1');
        makeDoubleWave (1336, 697, '2');
        makeDoubleWave (1477, 697, '3');
        makeDoubleWave (1633, 697, 'A');

        makeDoubleWave (1209, 770, '4');
        makeDoubleWave (1336, 770, '5');
        makeDoubleWave (1477, 770, '6');
        makeDoubleWave (1633, 770, 'B');

        makeDoubleWave (1209, 852, '7');
        makeDoubleWave (1336, 852, '8');
        makeDoubleWave (1477, 852, '9');
        makeDoubleWave (1633, 852, 'C');

        makeDoubleWave (1209, 941, '*');
        makeDoubleWave (1336, 941, '0');
        makeDoubleWave (1477, 941, '#');
        makeDoubleWave (1633, 941, 'D');
    }

    private static void makeSingleWave (double f, byte[] out)
    {
        for (int i = 0; i < out.length; i++)
        {
            double period = (double) SAMPLE_RATE / f;
            double angle = 2d * Math.PI * i / period;
            out[i] = (byte) (Math.sin (angle) * 127f);
        }
    }

    private static void makeDoubleWave (double f1, double f2, char key)
    {
        byte[] buff = new byte[SLEN];
        for (int i = 0; i < SLEN; i++)
        {
            double mu = 2d * Math.PI * i;
            double angle1 = mu / ((double) SAMPLE_RATE / f1);
            double angle2 = mu / ((double) SAMPLE_RATE / f2);
            double tmp = Math.sin (angle1)*127f + Math.sin (angle2)*127f;
            buff[i] = (byte)(tmp/2d);
        }
        dtmfMap.put (key, buff);
    }

    private static void playOneDtmf (char c, SourceDataLine line)
    {
        c = Character.toUpperCase (c);
        if (c == ' ')
        {
            play (line, pause, 50);
            return;
        }
        byte[] tone = dtmfMap.get (c);
        if (tone != null)
        {
            play (line, tone, 150);
        }
    }

    private static void playOneMorse (char c, SourceDataLine line)
    {
        if (c == '·')
            playShort (line);
        else if (c == '-')
            playLong (line);
        else if (c == ' ')
            play (line, pause, 200);
    }

    @java.lang.FunctionalInterface
    interface FunctionalInterface
    {
        void doForChar (char c, SourceDataLine line);
    }

    public static void playString (String in, FunctionalInterface functionalInterface)
    {
        try
        {
            SourceDataLine line = AudioSystem.getSourceDataLine (af);
            line.open (af, SAMPLE_RATE);
            line.start ();
            play (line, pause, 500);

            for (char c : in.toCharArray ())
            {
                functionalInterface.doForChar (c, line);
            }

            line.drain ();
            line.close ();
        }
        catch (Exception ignored)
        {
        }
    }

    public static void playDtmfString (String in)
    {
        playString (in, SynthTone::playOneDtmf);
    }

    public static void playMorseString (String in)
    {
        playString (in, SynthTone::playOneMorse);
    }

//    // Test
//    public static void main (String[] args) throws LineUnavailableException
//    {
//        SourceDataLine line = AudioSystem.getSourceDataLine (af);
//
//        line.open (af, SAMPLE_RATE);
//        line.start ();
//        play (line, pause, 500);
//        play (line, dtmf1, 500);
//        line.drain ();
//        line.close ();
//    }

    private static void playShort (SourceDataLine line)
    {
        play (line, sin, 50);
        play (line, pause, 100);
    }

    private static void playLong (SourceDataLine line)
    {
        play (line, sin, 200);
        play (line, pause, 100);
    }

    private static void play (SourceDataLine line, byte[] tone, int ms)
    {
        ms = Math.min (ms, (int)(SECONDS * 1000));
        int length = SAMPLE_RATE * ms / 1000;
        line.write (tone, 0, length);
    }
}
