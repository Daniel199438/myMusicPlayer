package jaudiotagger.org.jaudiotagger.audio.mp3;

import jaudiotagger.org.jaudiotagger.audio.AudioFile;
import jaudiotagger.org.jaudiotagger.audio.exceptions.CannotReadException;
import jaudiotagger.org.jaudiotagger.audio.exceptions.InvalidAudioFrameException;
import jaudiotagger.org.jaudiotagger.audio.exceptions.ReadOnlyFileException;
import jaudiotagger.org.jaudiotagger.audio.generic.AudioFileReader;
import jaudiotagger.org.jaudiotagger.audio.generic.GenericAudioHeader;
import jaudiotagger.org.jaudiotagger.tag.Tag;
import jaudiotagger.org.jaudiotagger.tag.TagException;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * Read Mp3 Info (retrofitted to entagged ,done differently to entagged which is why some methods throw RuntimeException)
 * because done elsewhere
 */
public class MP3FileReader extends AudioFileReader
{
    protected GenericAudioHeader getEncodingInfo(RandomAccessFile raf) throws CannotReadException, IOException
    {
        throw new RuntimeException("MP3FileReader.getEncodingInfo should be called");
    }

    protected Tag getTag(RandomAccessFile raf) throws CannotReadException, IOException
    {
        throw new RuntimeException("MP3FileReader.getEncodingInfo should be called");
    }

    /**
     * @param f
     * @return
     */
    //Override because we read mp3s differently to the entagged code
    public AudioFile read(File f) throws IOException, TagException, ReadOnlyFileException, InvalidAudioFrameException
    {
        MP3File mp3File = new MP3File(f, MP3File.LOAD_IDV1TAG | MP3File.LOAD_IDV2TAG, true);
        return mp3File;
    }

    /**
     * Read
     *
     * @param f
     * @return
     * @throws ReadOnlyFileException thrown if the file is not writable
     * @throws jaudiotagger.org.jaudiotagger.tag.TagException
     * @throws java.io.IOException
     * @throws jaudiotagger.org.jaudiotagger.audio.exceptions.InvalidAudioFrameException
     */
    public AudioFile readMustBeWritable(File f) throws IOException, TagException, ReadOnlyFileException, InvalidAudioFrameException
    {
        MP3File mp3File = new MP3File(f, MP3File.LOAD_IDV1TAG | MP3File.LOAD_IDV2TAG, false);
        return mp3File;
    }

}
