package jaudiotagger.org.jaudiotagger.audio.real;

import jaudiotagger.org.jaudiotagger.audio.generic.GenericTag;
import jaudiotagger.org.jaudiotagger.tag.FieldDataInvalidException;
import jaudiotagger.org.jaudiotagger.tag.FieldKey;
import jaudiotagger.org.jaudiotagger.tag.KeyNotFoundException;
import jaudiotagger.org.jaudiotagger.tag.TagField;

public class RealTag extends GenericTag
{
    public String toString()
    {
        String output = "REAL " + super.toString();
        return output;
    }

    public TagField createCompilationField(boolean value) throws KeyNotFoundException, FieldDataInvalidException
    {
        return createField(FieldKey.IS_COMPILATION,String.valueOf(value));
    }
}
