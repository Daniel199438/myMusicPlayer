/*
 *  MusicTag Copyright (C)2003,2004
 *
 *  This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser
 *  General Public  License as published by the Free Software Foundation; either version 2.1 of the License,
 *  or (at your option) any later version.
 *
 *  This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even
 *  the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *  See the GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License along with this library; if not,
 *  you can get a copy from http://www.opensource.org/licenses/lgpl-license.php or write to the Free Software
 *  Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA
 */
package jaudiotagger.org.jaudiotagger.tag.id3.framebody;

import jaudiotagger.org.jaudiotagger.tag.InvalidTagException;
import jaudiotagger.org.jaudiotagger.tag.datatype.DataTypes;
import jaudiotagger.org.jaudiotagger.tag.datatype.NumberHashMap;
import jaudiotagger.org.jaudiotagger.tag.datatype.PartOfSet;
import jaudiotagger.org.jaudiotagger.tag.id3.ID3v24Frames;
import jaudiotagger.org.jaudiotagger.tag.id3.valuepair.TextEncoding;

import java.nio.ByteBuffer;

/**
 * Track number/position in set Text Information frame.
 *
 * <p>The 'Track number/Position in set' frame is a numeric string containing the order number of the audio-file on its original recording.
 *
 * This may be extended with a "/" character and a numeric string containing the total number of tracks/elements on the original recording.
 *  e.g. "4/9".
 *
 * Some applications like to prepend the track number with a zero to aid sorting, (i.e 02 comes before 10)
 *
 *
 * <p>For more details, please refer to the ID3 specifications:
 * <ul>
 * <li><a href="http://www.id3.org/id3v2.3.0.txt">ID3 v2.3.0 Spec</a>
 * </ul>
 *
 * @author : Paul Taylor
 * @author : Eric Farng
 * @version $Id$
 */
public class FrameBodyTRCK extends AbstractID3v2FrameBody implements ID3v23FrameBody, ID3v24FrameBody
{

    /**
     * Creates a new FrameBodyTRCK datatype.
     */
    public FrameBodyTRCK()
    {
        setObjectValue(DataTypes.OBJ_TEXT_ENCODING, TextEncoding.ISO_8859_1);
        setObjectValue(DataTypes.OBJ_TEXT, new PartOfSet.PartOfSetValue());
    }

    public FrameBodyTRCK(FrameBodyTRCK body)
    {
        super(body);
    }

    /**
     * Creates a new FrameBodyTRCK datatype, the value is parsed literally
     *
     * @param textEncoding
     * @param text
     */
    public FrameBodyTRCK(byte textEncoding, String text)
    {
        setObjectValue(DataTypes.OBJ_TEXT_ENCODING, textEncoding);
        setObjectValue(DataTypes.OBJ_TEXT, new PartOfSet.PartOfSetValue(text));
    }

    public FrameBodyTRCK(byte textEncoding, Integer trackNo,Integer trackTotal)
    {
        super();
        setObjectValue(DataTypes.OBJ_TEXT_ENCODING, textEncoding);
        setObjectValue(DataTypes.OBJ_TEXT, new PartOfSet.PartOfSetValue(trackNo,trackTotal));
    }

    public String getUserFriendlyValue()
    {
        return String.valueOf(getTrackNo());
    }

    /**
     * Creates a new FrameBodyTRCK datatype.
     *
     * @param byteBuffer
     * @param frameSize
     * @throws java.io.IOException
     * @throws InvalidTagException
     */
    public FrameBodyTRCK(ByteBuffer byteBuffer, int frameSize) throws InvalidTagException
    {
        super(byteBuffer, frameSize);
    }


    /**
     * The ID3v2 frame identifier
     *
     * @return the ID3v2 frame identifier  for this frame type
     */
    public String getIdentifier()
    {
        return ID3v24Frames.FRAME_ID_TRACK;
    }

    public Integer getTrackNo()
    {
        PartOfSet.PartOfSetValue value = (PartOfSet.PartOfSetValue)getObjectValue(DataTypes.OBJ_TEXT);
        return value.getCount();
    }

    public String getTrackNoAsText()
    {
        return ((PartOfSet.PartOfSetValue)getObjectValue(DataTypes.OBJ_TEXT)).getCountAsText();
    }

    public String getText()
    {
        return getObjectValue(DataTypes.OBJ_TEXT).toString();
    }
    public void setTrackNo(Integer trackNo)
    {
        ((PartOfSet.PartOfSetValue)getObjectValue(DataTypes.OBJ_TEXT)).setCount(trackNo);
    }

    public void setTrackNo(String trackNo)
    {
        ((PartOfSet.PartOfSetValue)getObjectValue(DataTypes.OBJ_TEXT)).setCount(trackNo);
    }

    public Integer getTrackTotal()
    {
        return ((PartOfSet.PartOfSetValue)getObjectValue(DataTypes.OBJ_TEXT)).getTotal();
    }

    public String getTrackTotalAsText()
    {
        return ((PartOfSet.PartOfSetValue)getObjectValue(DataTypes.OBJ_TEXT)).getTotalAsText();
    }


    public void setTrackTotal(Integer trackTotal)
    {
         ((PartOfSet.PartOfSetValue)getObjectValue(DataTypes.OBJ_TEXT)).setTotal(trackTotal);
    }

    public void setTrackTotal(String trackTotal)
    {
        ((PartOfSet.PartOfSetValue)getObjectValue(DataTypes.OBJ_TEXT)).setTotal(trackTotal);
    }

    public void setText(String text)
    {
        setObjectValue(DataTypes.OBJ_TEXT, new PartOfSet.PartOfSetValue(text));
    }
    
    protected void setupObjectList()
    {
        objectList.add(new NumberHashMap(DataTypes.OBJ_TEXT_ENCODING, this, TextEncoding.TEXT_ENCODING_FIELD_SIZE));
        objectList.add(new PartOfSet(DataTypes.OBJ_TEXT, this));
    }
}