/*
 * Entagged Audio Tag library
 * Copyright (c) 2003-2005 Christian Laireiter <liree@web.de>
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *  
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 */
package jaudiotagger.org.jaudiotagger.audio.generic;

import jaudiotagger.org.jaudiotagger.audio.AudioFile;
import jaudiotagger.org.jaudiotagger.audio.exceptions.ModifyVetoException;

import java.io.File;

/**
 *
 *
 * @author Christian Laireiter
 */
public class AudioFileModificationAdapter implements AudioFileModificationListener
{

    /**
     * (overridden)
     *
     * @see jaudiotagger.org.jaudiotagger.audio.generic.AudioFileModificationListener#fileModified(jaudiotagger.org.jaudiotagger.audio.AudioFile,
     *File)
     */
    public void fileModified(AudioFile original, File temporary) throws ModifyVetoException
    {
        // Nothing to do
    }

    /**
     * (overridden)
     *
     * @see jaudiotagger.org.jaudiotagger.audio.generic.AudioFileModificationListener#fileOperationFinished(File)
     */
    public void fileOperationFinished(File result)
    {
        // Nothing to do
    }

    /**
     * (overridden)
     *
     * @see jaudiotagger.org.jaudiotagger.audio.generic.AudioFileModificationListener#fileWillBeModified(jaudiotagger.org.jaudiotagger.audio.AudioFile,
     *boolean)
     */
    public void fileWillBeModified(AudioFile file, boolean delete) throws ModifyVetoException
    {
        // Nothing to do
    }

    /**
     * (overridden)
     *
     * @see jaudiotagger.org.jaudiotagger.audio.generic.AudioFileModificationListener#vetoThrown(jaudiotagger.org.jaudiotagger.audio.generic.AudioFileModificationListener,
     *jaudiotagger.org.jaudiotagger.audio.AudioFile,
     *jaudiotagger.org.jaudiotagger.audio.exceptions.ModifyVetoException)
     */
    public void vetoThrown(AudioFileModificationListener cause, AudioFile original, ModifyVetoException veto)
    {
        // Nothing to do
    }

}
