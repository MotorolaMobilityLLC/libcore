/*
 *  Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The ASF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package java.io;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import org.apache.harmony.luni.util.DeleteOnExit;

/**
 * An "abstract" representation of a file system entity identified by a
 * pathname. The pathname may be absolute (relative to the root directory
 * of the file system) or relative to the current directory in which the program
 * is running.
 * <p>
 * The actual file referenced by a {@code File} may or may not exist. It may
 * also, despite the name {@code File}, be a directory or other non-regular
 * file.
 * <p>
 * This class provides limited functionality for getting/setting file
 * permissions, file type, and last modified time.
 * <p>
 * Although Java doesn't specify a character encoding for filenames, on Android
 * Java strings are converted to UTF-8 byte sequences when sending filenames to
 * the operating system, and byte sequences returned by the operating system
 * (from the various {@code list} methods) are converted to Java strings by
 * decoding them as UTF-8 byte sequences.
 *
 * @see java.io.Serializable
 * @see java.lang.Comparable
 */
public class File implements Serializable, Comparable<File> {

    private static final long serialVersionUID = 301077366599181567L;

    /**
     * The system-dependent character used to separate components in filenames ('/').
     * Use of this (rather than hard-coding '/') helps portability to other operating systems.
     *
     * <p>This field is initialized from the system property "file.separator".
     * Later changes to that property will have no effect on this field or this class.
     */
    public static final char separatorChar;

    /**
     * The system-dependent string used to separate components in filenames ('/').
     * See {@link #separatorChar}.
     */
    public static final String separator;

    /**
     * The system-dependent character used to separate components in search paths (':').
     * This is used to split such things as the PATH environment variable and classpath
     * system properties into lists of directories to be searched.
     *
     * <p>This field is initialized from the system property "path.separator".
     * Later changes to that property will have no effect on this field or this class.
     */
    public static final char pathSeparatorChar;

    /**
     * The system-dependent string used to separate components in search paths (":").
     * See {@link #pathSeparatorChar}.
     */
    public static final String pathSeparator;

    /**
     * The path we return from getPath. This is almost the path we were
     * given, but without duplicate adjacent slashes and without trailing
     * slashes (except for the special case of the root directory). This
     * path may be the empty string.
     */
    private String path;

    /**
     * The path we return from getAbsolutePath, and pass down to native code.
     */
    private transient String absolutePath;

    static {
        // The default protection domain grants access to these properties.
        separatorChar = System.getProperty("file.separator", "/").charAt(0);
        pathSeparatorChar = System.getProperty("path.separator", ":").charAt(0);
        separator = String.valueOf(separatorChar);
        pathSeparator = String.valueOf(pathSeparatorChar);
    }

    /**
     * Constructs a new file using the specified directory and name.
     *
     * @param dir
     *            the directory where the file is stored.
     * @param name
     *            the file's name.
     * @throws NullPointerException
     *             if {@code name} is {@code null}.
     */
    public File(File dir, String name) {
        this(dir == null ? null : dir.getPath(), name);
    }

    /**
     * Constructs a new file using the specified path.
     *
     * @param path
     *            the path to be used for the file.
     */
    public File(String path) {
        init(path);
    }

    /**
     * Constructs a new File using the specified directory path and file name,
     * placing a path separator between the two.
     *
     * @param dirPath
     *            the path to the directory where the file is stored.
     * @param name
     *            the file's name.
     * @throws NullPointerException
     *             if {@code name} is {@code null}.
     */
    public File(String dirPath, String name) {
        if (name == null) {
            throw new NullPointerException();
        }
        if (dirPath == null || dirPath.isEmpty()) {
            init(name);
        } else if (name.isEmpty()) {
            init(dirPath);
        } else {
            init(join(dirPath, name));
        }
    }

    /**
     * Constructs a new File using the path of the specified URI. {@code uri}
     * needs to be an absolute and hierarchical Unified Resource Identifier with
     * file scheme and non-empty path component, but with undefined authority,
     * query or fragment components.
     *
     * @param uri
     *            the Unified Resource Identifier that is used to construct this
     *            file.
     * @throws IllegalArgumentException
     *             if {@code uri} does not comply with the conditions above.
     * @see #toURI
     * @see java.net.URI
     */
    public File(URI uri) {
        // check pre-conditions
        checkURI(uri);
        init(uri.getPath());
    }

    private void init(String dirtyPath) {
        // Cache the path and the absolute path.
        // We can't call isAbsolute() here (http://b/2486943).
        String cleanPath = fixSlashes(dirtyPath);
        boolean isAbsolute = cleanPath.length() > 0 && cleanPath.charAt(0) == separatorChar;
        if (isAbsolute) {
            this.path = this.absolutePath = cleanPath;
        } else {
            String userDir = System.getProperty("user.dir");
            this.absolutePath = cleanPath.isEmpty() ? userDir : join(userDir, cleanPath);
            // We want path to be equal to cleanPath, but we'd like to reuse absolutePath's char[].
            this.path = absolutePath.substring(absolutePath.length() - cleanPath.length());
        }
    }

    // Removes duplicate adjacent slashes and any trailing slash.
    private String fixSlashes(String origPath) {
        // Remove duplicate adjacent slashes.
        boolean lastWasSlash = false;
        char[] newPath = origPath.toCharArray();
        int length = newPath.length;
        int newLength = 0;
        for (int i = 0; i < length; ++i) {
            char ch = newPath[i];
            if (ch == '/') {
                if (!lastWasSlash) {
                    newPath[newLength++] = separatorChar;
                    lastWasSlash = true;
                }
            } else {
                newPath[newLength++] = ch;
                lastWasSlash = false;
            }
        }
        // Remove any trailing slash (unless this is the root of the file system).
        if (lastWasSlash && newLength > 1) {
            newLength--;
        }
        // Reuse the original string if possible.
        return (newLength != length) ? new String(newPath, 0, newLength) : origPath;
    }

    // Joins two path components, adding a separator only if necessary.
    private String join(String prefix, String suffix) {
        int prefixLength = prefix.length();
        boolean haveSlash = (prefixLength > 0 && prefix.charAt(prefixLength - 1) == separatorChar);
        if (!haveSlash) {
            haveSlash = (suffix.length() > 0 && suffix.charAt(0) == separatorChar);
        }
        return haveSlash ? (prefix + suffix) : (prefix + separatorChar + suffix);
    }

    private void checkURI(URI uri) {
        if (!uri.isAbsolute()) {
            throw new IllegalArgumentException("URI is not absolute: " + uri);
        } else if (!uri.getRawSchemeSpecificPart().startsWith("/")) {
            throw new IllegalArgumentException("URI is not hierarchical: " + uri);
        }
        if (!"file".equals(uri.getScheme())) {
            throw new IllegalArgumentException("Expected file scheme in URI: " + uri);
        }
        String rawPath = uri.getRawPath();
        if (rawPath == null || rawPath.isEmpty()) {
            throw new IllegalArgumentException("Expected non-empty path in URI: " + uri);
        }
        if (uri.getRawAuthority() != null) {
            throw new IllegalArgumentException("Found authority in URI: " + uri);
        }
        if (uri.getRawQuery() != null) {
            throw new IllegalArgumentException("Found query in URI: " + uri);
        }
        if (uri.getRawFragment() != null) {
            throw new IllegalArgumentException("Found fragment in URI: " + uri);
        }
    }

    /**
     * Lists the file system roots. The Java platform may support zero or more
     * file systems, each with its own platform-dependent root. Further, the
     * {@link #getCanonicalPath canonical} path of any file on the system will
     * always begin with one of the returned file system roots.
     *
     * @return the array of file system roots.
     */
    public static File[] listRoots() {
        return new File[] { new File("/") };
    }

    /**
     * Tests whether or not this process is allowed to execute this file.
     * Note that this is a best-effort result; the only way to be certain is
     * to actually attempt the operation.
     *
     * @return {@code true} if this file can be executed, {@code false} otherwise.
     * @since 1.6
     */
    public boolean canExecute() {
        if (path.isEmpty()) {
            return false;
        }
        return canExecuteImpl(absolutePath);
    }
    private static native boolean canExecuteImpl(String path);

    /**
     * Indicates whether the current context is allowed to read from this file.
     *
     * @return {@code true} if this file can be read, {@code false} otherwise.
     */
    public boolean canRead() {
        if (path.isEmpty()) {
            return false;
        }
        return canReadImpl(absolutePath);
    }
    private static native boolean canReadImpl(String path);

    /**
     * Indicates whether the current context is allowed to write to this file.
     *
     * @return {@code true} if this file can be written, {@code false}
     *         otherwise.
     */
    public boolean canWrite() {
        if (path.isEmpty()) {
            return false;
        }
        return canWriteImpl(absolutePath);
    }
    private static native boolean canWriteImpl(String path);

    /**
     * Returns the relative sort ordering of the paths for this file and the
     * file {@code another}. The ordering is platform dependent.
     *
     * @param another
     *            a file to compare this file to
     * @return an int determined by comparing the two paths. Possible values are
     *         described in the Comparable interface.
     * @see Comparable
     */
    public int compareTo(File another) {
        return this.getPath().compareTo(another.getPath());
    }

    /**
     * Deletes this file. Directories must be empty before they will be deleted.
     *
     * <p>Note that this method does <i>not</i> throw {@code IOException} on failure.
     * Callers must check the return value.
     *
     * @return {@code true} if this file was deleted, {@code false} otherwise.
     */
    public boolean delete() {
        if (path.isEmpty()) {
            return false;
        }
        return deleteImpl(absolutePath);
    }

    private static native boolean deleteImpl(String path);

    /**
     * Schedules this file to be automatically deleted when the VM terminates normally.
     *
     * <p><i>Note that on Android, the application lifecycle does not include VM termination,
     * so calling this method will not ensure that files are deleted</i>. Instead, you should
     * use the most appropriate out of:
     * <ul>
     * <li>Use a {@code finally} clause to manually invoke {@link #delete}.
     * <li>Maintain your own set of files to delete, and process it at an appropriate point
     * in your application's lifecycle.
     * <li>Use the Unix trick of deleting the file as soon as all readers and writers have
     * opened it. No new readers/writers will be able to access the file, but all existing
     * ones will still have access until the last one closes the file.
     * </ul>
     */
    public void deleteOnExit() {
        DeleteOnExit.getInstance().addFile(getAbsoluteName());
    }

    /**
     * Compares {@code obj} to this file and returns {@code true} if they
     * represent the <em>same</em> object using a path specific comparison.
     *
     * @param obj
     *            the object to compare this file with.
     * @return {@code true} if {@code obj} is the same as this object,
     *         {@code false} otherwise.
     */
    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof File)) {
            return false;
        }
        return path.equals(((File) obj).getPath());
    }

    /**
     * Returns a boolean indicating whether this file can be found on the
     * underlying file system.
     *
     * @return {@code true} if this file exists, {@code false} otherwise.
     */
    public boolean exists() {
        if (path.isEmpty()) {
            return false;
        }
        return existsImpl(absolutePath);
    }

    private static native boolean existsImpl(String path);

    /**
     * Returns the absolute path of this file. An absolute path is a path that starts at a root
     * of the file system. On Android, there is only one root: {@code /}.
     *
     * <p>A common use for absolute paths is when passing paths to a {@code Process} as
     * command-line arguments, to remove the requirement implied by relative paths, that the
     * child must have the same working directory as its parent.
     */
    public String getAbsolutePath() {
        return absolutePath;
    }

    /**
     * Returns a new file constructed using the absolute path of this file.
     * Equivalent to {@code new File(this.getAbsolutePath())}.
     */
    public File getAbsoluteFile() {
        return new File(this.getAbsolutePath());
    }

    /**
     * Returns the canonical path of this file.
     * An <i>absolute</i> path is one that begins at the root of the file system.
     * A <i>canonical</i> path is an absolute path with symbolic links
     * and references to "." or ".." resolved. If a path element does not exist (or
     * is not searchable), there is a conflict between interpreting canonicalization
     * as a textual operation (where "a/../b" is "b" even if "a" does not exist) .
     *
     * <p>Most callers should use {@link #getAbsolutePath} instead. A canonical path is
     * significantly more expensive to compute, and not generally useful. The primary
     * use for canonical paths is determining whether two paths point to the same file by
     * comparing the canonicalized paths.
     *
     * <p>It can be actively harmful to use a canonical path, specifically because
     * canonicalization removes symbolic links. It's wise to assume that a symbolic link
     * is present for a reason, and that that reason is because the link may need to change.
     * Canonicalization removes this layer of indirection. Good code should generally avoid
     * caching canonical paths.
     *
     * @return the canonical path of this file.
     * @throws IOException
     *             if an I/O error occurs.
     */
    public String getCanonicalPath() throws IOException {
        return realpath(absolutePath);
    }

    /**
     * TODO: move this stuff to libcore.os.
     * @hide
     */
    public static native void symlink(String oldPath, String newPath);
    private static native String realpath(String path);
    private static native String readlink(String path);

    /**
     * Returns a new file created using the canonical path of this file.
     * Equivalent to {@code new File(this.getCanonicalPath())}.
     *
     * @return the new file constructed from this file's canonical path.
     * @throws IOException
     *             if an I/O error occurs.
     */
    public File getCanonicalFile() throws IOException {
        return new File(getCanonicalPath());
    }

    /**
     * Returns the name of the file or directory represented by this file.
     *
     * @return this file's name or an empty string if there is no name part in
     *         the file's path.
     */
    public String getName() {
        int separatorIndex = path.lastIndexOf(separator);
        return (separatorIndex < 0) ? path : path.substring(separatorIndex + 1, path.length());
    }

    /**
     * Returns the pathname of the parent of this file. This is the path up to
     * but not including the last name. {@code null} is returned if there is no
     * parent.
     *
     * @return this file's parent pathname or {@code null}.
     */
    public String getParent() {
        int length = path.length(), firstInPath = 0;
        if (separatorChar == '\\' && length > 2 && path.charAt(1) == ':') {
            firstInPath = 2;
        }
        int index = path.lastIndexOf(separatorChar);
        if (index == -1 && firstInPath > 0) {
            index = 2;
        }
        if (index == -1 || path.charAt(length - 1) == separatorChar) {
            return null;
        }
        if (path.indexOf(separatorChar) == index
                && path.charAt(firstInPath) == separatorChar) {
            return path.substring(0, index + 1);
        }
        return path.substring(0, index);
    }

    /**
     * Returns a new file made from the pathname of the parent of this file.
     * This is the path up to but not including the last name. {@code null} is
     * returned when there is no parent.
     *
     * @return a new file representing this file's parent or {@code null}.
     */
    public File getParentFile() {
        String tempParent = getParent();
        if (tempParent == null) {
            return null;
        }
        return new File(tempParent);
    }

    /**
     * Returns the path of this file.
     *
     * @return this file's path.
     */
    public String getPath() {
        return path;
    }

    /**
     * Returns an integer hash code for the receiver. Any two objects for which
     * {@code equals} returns {@code true} must return the same hash code.
     *
     * @return this files's hash value.
     * @see #equals
     */
    @Override
    public int hashCode() {
        return getPath().hashCode() ^ 1234321;
    }

    /**
     * Indicates if this file's pathname is absolute. Whether a pathname is
     * absolute is platform specific. On Android, absolute paths start with
     * the character '/'.
     *
     * @return {@code true} if this file's pathname is absolute, {@code false}
     *         otherwise.
     * @see #getPath
     */
    public boolean isAbsolute() {
        return path.length() > 0 && path.charAt(0) == separatorChar;
    }

    /**
     * Indicates if this file represents a <em>directory</em> on the
     * underlying file system.
     *
     * @return {@code true} if this file is a directory, {@code false}
     *         otherwise.
     */
    public boolean isDirectory() {
        if (path.isEmpty()) {
            return false;
        }
        return isDirectoryImpl(absolutePath);
    }

    private static native boolean isDirectoryImpl(String path);

    /**
     * Indicates if this file represents a <em>file</em> on the underlying
     * file system.
     *
     * @return {@code true} if this file is a file, {@code false} otherwise.
     */
    public boolean isFile() {
        if (path.isEmpty()) {
            return false;
        }
        return isFileImpl(absolutePath);
    }

    private static native boolean isFileImpl(String path);

    /**
     * Returns whether or not this file is a hidden file as defined by the
     * operating system. The notion of "hidden" is system-dependent. For Unix
     * systems a file is considered hidden if its name starts with a ".". For
     * Windows systems there is an explicit flag in the file system for this
     * purpose.
     *
     * @return {@code true} if the file is hidden, {@code false} otherwise.
     */
    public boolean isHidden() {
        if (path.isEmpty()) {
            return false;
        }
        return getName().startsWith(".");
    }

    /**
     * Returns the time when this file was last modified, measured in
     * milliseconds since January 1st, 1970, midnight.
     * Returns 0 if the file does not exist.
     *
     * @return the time when this file was last modified.
     */
    public long lastModified() {
        if (path.isEmpty()) {
            return 0;
        }
        return lastModifiedImpl(absolutePath);
    }

    private static native long lastModifiedImpl(String path);

    /**
     * Sets the time this file was last modified, measured in milliseconds since
     * January 1st, 1970, midnight.
     *
     * <p>Note that this method does <i>not</i> throw {@code IOException} on failure.
     * Callers must check the return value.
     *
     * @param time
     *            the last modification time for this file.
     * @return {@code true} if the operation is successful, {@code false}
     *         otherwise.
     * @throws IllegalArgumentException
     *             if {@code time < 0}.
     */
    public boolean setLastModified(long time) {
        if (path.isEmpty()) {
            return false;
        }
        if (time < 0) {
            throw new IllegalArgumentException("time < 0");
        }
        return setLastModifiedImpl(absolutePath, time);
    }

    private static native boolean setLastModifiedImpl(String path, long time);

    /**
     * Equivalent to setWritable(false, false).
     *
     * @see #setWritable(boolean, boolean)
     */
    public boolean setReadOnly() {
        return setWritable(false, false);
    }

    /**
     * Manipulates the execute permissions for the abstract path designated by
     * this file.
     *
     * <p>Note that this method does <i>not</i> throw {@code IOException} on failure.
     * Callers must check the return value.
     *
     * @param executable
     *            To allow execute permission if true, otherwise disallow
     * @param ownerOnly
     *            To manipulate execute permission only for owner if true,
     *            otherwise for everyone. The manipulation will apply to
     *            everyone regardless of this value if the underlying system
     *            does not distinguish owner and other users.
     * @return true if and only if the operation succeeded. If the user does not
     *         have permission to change the access permissions of this abstract
     *         pathname the operation will fail. If the underlying file system
     *         does not support execute permission and the value of executable
     *         is false, this operation will fail.
     * @since 1.6
     */
    public boolean setExecutable(boolean executable, boolean ownerOnly) {
        if (path.isEmpty()) {
            return false;
        }
        return setExecutableImpl(absolutePath, executable, ownerOnly);
    }

    /**
     * Equivalent to setExecutable(executable, true).
     * @see #setExecutable(boolean, boolean)
     * @since 1.6
     */
    public boolean setExecutable(boolean executable) {
        return setExecutable(executable, true);
    }

    private static native boolean setExecutableImpl(String path, boolean executable, boolean ownerOnly);

    /**
     * Manipulates the read permissions for the abstract path designated by this
     * file.
     *
     * @param readable
     *            To allow read permission if true, otherwise disallow
     * @param ownerOnly
     *            To manipulate read permission only for owner if true,
     *            otherwise for everyone. The manipulation will apply to
     *            everyone regardless of this value if the underlying system
     *            does not distinguish owner and other users.
     * @return true if and only if the operation succeeded. If the user does not
     *         have permission to change the access permissions of this abstract
     *         pathname the operation will fail. If the underlying file system
     *         does not support read permission and the value of readable is
     *         false, this operation will fail.
     * @since 1.6
     */
    public boolean setReadable(boolean readable, boolean ownerOnly) {
        if (path.isEmpty()) {
            return false;
        }
        return setReadableImpl(absolutePath, readable, ownerOnly);
    }

    /**
     * Equivalent to setReadable(readable, true).
     * @see #setReadable(boolean, boolean)
     * @since 1.6
     */
    public boolean setReadable(boolean readable) {
        return setReadable(readable, true);
    }

    private static native boolean setReadableImpl(String path, boolean readable, boolean ownerOnly);

    /**
     * Manipulates the write permissions for the abstract path designated by this
     * file.
     *
     * @param writable
     *            To allow write permission if true, otherwise disallow
     * @param ownerOnly
     *            To manipulate write permission only for owner if true,
     *            otherwise for everyone. The manipulation will apply to
     *            everyone regardless of this value if the underlying system
     *            does not distinguish owner and other users.
     * @return true if and only if the operation succeeded. If the user does not
     *         have permission to change the access permissions of this abstract
     *         pathname the operation will fail.
     * @since 1.6
     */
    public boolean setWritable(boolean writable, boolean ownerOnly) {
        if (path.isEmpty()) {
            return false;
        }
        return setWritableImpl(absolutePath, writable, ownerOnly);
    }

    /**
     * Equivalent to setWritable(writable, true).
     * @see #setWritable(boolean, boolean)
     * @since 1.6
     */
    public boolean setWritable(boolean writable) {
        return setWritable(writable, true);
    }

    private static native boolean setWritableImpl(String path, boolean writable, boolean ownerOnly);

    /**
     * Returns the length of this file in bytes.
     * Returns 0 if the file does not exist.
     * The result for a directory is not defined.
     *
     * @return the number of bytes in this file.
     */
    public long length() {
        if (path.isEmpty()) {
            return 0;
        }
        return lengthImpl(absolutePath);
    }

    private static native long lengthImpl(String path);

    /**
     * Returns an array of strings with the file names in the directory
     * represented by this file. The result is {@code null} if this file is not
     * a directory.
     * <p>
     * The entries {@code .} and {@code ..} representing the current and parent
     * directory are not returned as part of the list.
     *
     * @return an array of strings with file names or {@code null}.
     */
    public String[] list() {
        if (path.isEmpty()) {
            return null;
        }
        return listImpl(absolutePath);
    }

    private static native String[] listImpl(String path);

    /**
     * Gets a list of the files in the directory represented by this file. This
     * list is then filtered through a FilenameFilter and the names of files
     * with matching names are returned as an array of strings. Returns
     * {@code null} if this file is not a directory. If {@code filter} is
     * {@code null} then all filenames match.
     * <p>
     * The entries {@code .} and {@code ..} representing the current and parent
     * directories are not returned as part of the list.
     *
     * @param filter
     *            the filter to match names against, may be {@code null}.
     * @return an array of files or {@code null}.
     */
    public String[] list(FilenameFilter filter) {
        String[] filenames = list();
        if (filter == null || filenames == null) {
            return filenames;
        }
        List<String> result = new ArrayList<String>(filenames.length);
        for (String filename : filenames) {
            if (filter.accept(this, filename)) {
                result.add(filename);
            }
        }
        return result.toArray(new String[result.size()]);
    }

    /**
     * Returns an array of files contained in the directory represented by this
     * file. The result is {@code null} if this file is not a directory. The
     * paths of the files in the array are absolute if the path of this file is
     * absolute, they are relative otherwise.
     *
     * @return an array of files or {@code null}.
     */
    public File[] listFiles() {
        return filenamesToFiles(list());
    }

    /**
     * Gets a list of the files in the directory represented by this file. This
     * list is then filtered through a FilenameFilter and files with matching
     * names are returned as an array of files. Returns {@code null} if this
     * file is not a directory. If {@code filter} is {@code null} then all
     * filenames match.
     * <p>
     * The entries {@code .} and {@code ..} representing the current and parent
     * directories are not returned as part of the list.
     *
     * @param filter
     *            the filter to match names against, may be {@code null}.
     * @return an array of files or {@code null}.
     */
    public File[] listFiles(FilenameFilter filter) {
        return filenamesToFiles(list(filter));
    }

    /**
     * Gets a list of the files in the directory represented by this file. This
     * list is then filtered through a FileFilter and matching files are
     * returned as an array of files. Returns {@code null} if this file is not a
     * directory. If {@code filter} is {@code null} then all files match.
     * <p>
     * The entries {@code .} and {@code ..} representing the current and parent
     * directories are not returned as part of the list.
     *
     * @param filter
     *            the filter to match names against, may be {@code null}.
     * @return an array of files or {@code null}.
     */
    public File[] listFiles(FileFilter filter) {
        File[] files = listFiles();
        if (filter == null || files == null) {
            return files;
        }
        List<File> result = new ArrayList<File>(files.length);
        for (File file : files) {
            if (filter.accept(file)) {
                result.add(file);
            }
        }
        return result.toArray(new File[result.size()]);
    }

    /**
     * Converts a String[] containing filenames to a File[].
     * Note that the filenames must not contain slashes.
     * This method is to remove duplication in the implementation
     * of File.list's overloads.
     */
    private File[] filenamesToFiles(String[] filenames) {
        if (filenames == null) {
            return null;
        }
        int count = filenames.length;
        File[] result = new File[count];
        for (int i = 0; i < count; ++i) {
            result[i] = new File(this, filenames[i]);
        }
        return result;
    }

    /**
     * Creates the directory named by the trailing filename of this file. Does
     * not create the complete path required to create this directory.
     *
     * <p>Note that this method does <i>not</i> throw {@code IOException} on failure.
     * Callers must check the return value.
     *
     * @return {@code true} if the directory has been created, {@code false}
     *         otherwise.
     * @see #mkdirs
     */
    public boolean mkdir() {
        return mkdirImpl(absolutePath);
    }

    private static native boolean mkdirImpl(String path);

    /**
     * Creates the directory named by the trailing filename of this file,
     * including the complete directory path required to create this directory.
     *
     * <p>Note that this method does <i>not</i> throw {@code IOException} on failure.
     * Callers must check the return value.
     *
     * @return {@code true} if the necessary directories have been created,
     *         {@code false} if the target directory already exists or one of
     *         the directories can not be created.
     * @see #mkdir
     */
    public boolean mkdirs() {
        /* If the terminal directory already exists, answer false */
        if (exists()) {
            return false;
        }

        /* If the receiver can be created, answer true */
        if (mkdir()) {
            return true;
        }

        String parentDir = getParent();
        /* If there is no parent and we were not created, answer false */
        if (parentDir == null) {
            return false;
        }

        /* Otherwise, try to create a parent directory and then this directory */
        return (new File(parentDir).mkdirs() && mkdir());
    }

    /**
     * Creates a new, empty file on the file system according to the path
     * information stored in this file.
     *
     * <p>Note that this method does <i>not</i> throw {@code IOException} on failure.
     * Callers must check the return value.
     *
     * @return {@code true} if the file has been created, {@code false} if it
     *         already exists.
     * @throws IOException if it's not possible to create the file.
     */
    public boolean createNewFile() throws IOException {
        if (path.isEmpty()) {
            throw new IOException("No such file or directory");
        }
        return createNewFileImpl(absolutePath);
    }

    private static native boolean createNewFileImpl(String path);

    /**
     * Creates an empty temporary file using the given prefix and suffix as part
     * of the file name. If {@code suffix} is null, {@code .tmp} is used. This
     * method is a convenience method that calls
     * {@link #createTempFile(String, String, File)} with the third argument
     * being {@code null}.
     *
     * @param prefix
     *            the prefix to the temp file name.
     * @param suffix
     *            the suffix to the temp file name.
     * @return the temporary file.
     * @throws IOException
     *             if an error occurs when writing the file.
     */
    public static File createTempFile(String prefix, String suffix) throws IOException {
        return createTempFile(prefix, suffix, null);
    }

    /**
     * Creates an empty temporary file in the given directory using the given
     * prefix and suffix as part of the file name. If {@code suffix} is null, {@code .tmp} is used.
     *
     * <p>Note that this method does <i>not</i> call {@link #deleteOnExit}, but see the
     * documentation for that method before you call it manually.
     *
     * @param prefix
     *            the prefix to the temp file name.
     * @param suffix
     *            the suffix to the temp file name.
     * @param directory
     *            the location to which the temp file is to be written, or
     *            {@code null} for the default location for temporary files,
     *            which is taken from the "java.io.tmpdir" system property. It
     *            may be necessary to set this property to an existing, writable
     *            directory for this method to work properly.
     * @return the temporary file.
     * @throws IllegalArgumentException
     *             if the length of {@code prefix} is less than 3.
     * @throws IOException
     *             if an error occurs when writing the file.
     */
    public static File createTempFile(String prefix, String suffix, File directory)
            throws IOException {
        // Force a prefix null check first
        if (prefix.length() < 3) {
            throw new IllegalArgumentException("prefix must be at least 3 characters");
        }
        if (suffix == null) {
            suffix = ".tmp";
        }
        File tmpDirFile = directory;
        if (tmpDirFile == null) {
            String tmpDir = System.getProperty("java.io.tmpdir", ".");
            tmpDirFile = new File(tmpDir);
        }
        File result;
        do {
            result = new File(tmpDirFile, prefix + new Random().nextInt() + suffix);
        } while (!result.createNewFile());
        return result;
    }

    /**
     * Renames this file to {@code newPath}. This operation is supported for both
     * files and directories.
     *
     * <p>Many failures are possible. Some of the more likely failures include:
     * <ul>
     * <li>Write permission is required on the directories containing both the source and
     * destination paths.
     * <li>Search permission is required for all parents of both paths.
     * <li>Both paths be on the same mount point. On Android, applications are most likely to hit
     * this restriction when attempting to copy between internal storage and an SD card.
     * </ul>
     *
     * <p>Note that this method does <i>not</i> throw {@code IOException} on failure.
     * Callers must check the return value.
     *
     * @param newPath the new path.
     * @return true on success.
     */
    public boolean renameTo(File newPath) {
        if (path.isEmpty() || newPath.path.isEmpty()) {
            return false;
        }
        return renameToImpl(absolutePath, newPath.absolutePath);
    }

    private static native boolean renameToImpl(String oldPath, String newPath);

    /**
     * Returns a string containing a concise, human-readable description of this
     * file.
     *
     * @return a printable representation of this file.
     */
    @Override
    public String toString() {
        return path;
    }

    /**
     * Returns a Uniform Resource Identifier for this file. The URI is system
     * dependent and may not be transferable between different operating / file
     * systems.
     *
     * @return an URI for this file.
     */
    public URI toURI() {
        String name = getAbsoluteName();
        try {
            if (!name.startsWith("/")) {
                // start with sep.
                return new URI("file", null, new StringBuilder(
                        name.length() + 1).append('/').append(name).toString(),
                        null, null);
            } else if (name.startsWith("//")) {
                return new URI("file", "", name, null); // UNC path
            }
            return new URI("file", null, name, null, null);
        } catch (URISyntaxException e) {
            // this should never happen
            return null;
        }
    }

    /**
     * Returns a Uniform Resource Locator for this file. The URL is system
     * dependent and may not be transferable between different operating / file
     * systems.
     *
     * @return a URL for this file.
     * @throws java.net.MalformedURLException
     *             if the path cannot be transformed into a URL.
     * @deprecated use {@link #toURI} and {@link java.net.URI#toURL} to get
     * correct escaping of illegal characters.
     */
    @Deprecated
    public URL toURL() throws java.net.MalformedURLException {
        String name = getAbsoluteName();
        if (!name.startsWith("/")) {
            // start with sep.
            return new URL("file", "", -1,
                    new StringBuilder(name.length() + 1).append('/').append(name).toString(), null);
        } else if (name.startsWith("//")) {
            return new URL("file:" + name); // UNC path
        }
        return new URL("file", "", -1, name, null);
    }

    private String getAbsoluteName() {
        File f = getAbsoluteFile();
        String name = f.getPath();

        if (f.isDirectory() && name.charAt(name.length() - 1) != separatorChar) {
            // Directories must end with a slash
            name = new StringBuilder(name.length() + 1).append(name)
                    .append('/').toString();
        }
        if (separatorChar != '/') { // Must convert slashes.
            name = name.replace(separatorChar, '/');
        }
        return name;
    }

    private void writeObject(ObjectOutputStream stream) throws IOException {
        stream.defaultWriteObject();
        stream.writeChar(separatorChar);
    }

    private void readObject(ObjectInputStream stream) throws IOException, ClassNotFoundException {
        stream.defaultReadObject();
        char inSeparator = stream.readChar();
        init(path.replace(inSeparator, separatorChar));
    }

    /**
     * Returns the total size in bytes of the partition containing this path.
     * Returns 0 if this path does not exist.
     *
     * @since 1.6
     */
    public long getTotalSpace() {
        return getTotalSpaceImpl(absolutePath);
    }
    private static native long getTotalSpaceImpl(String path);

    /**
     * Returns the number of usable free bytes on the partition containing this path.
     * Returns 0 if this path does not exist.
     *
     * <p>Note that this is likely to be an optimistic over-estimate and should not
     * be taken as a guarantee your application can actually write this many bytes.
     * On Android (and other Unix-based systems), this method returns the number of free bytes
     * available to non-root users, regardless of whether you're actually running as root,
     * and regardless of any quota or other restrictions that might apply to the user.
     * (The {@code getFreeSpace} method returns the number of bytes potentially available to root.)
     *
     * @since 1.6
     */
    public long getUsableSpace() {
        return getUsableSpaceImpl(absolutePath);
    }
    private static native long getUsableSpaceImpl(String path);

    /**
     * Returns the number of free bytes on the partition containing this path.
     * Returns 0 if this path does not exist.
     *
     * <p>Note that this is likely to be an optimistic over-estimate and should not
     * be taken as a guarantee your application can actually write this many bytes.
     *
     * @since 1.6
     */
    public long getFreeSpace() {
        return getFreeSpaceImpl(absolutePath);
    }
    private static native long getFreeSpaceImpl(String path);
}
