package org.fastily.jwiki.dwrap;

import okhttp3.HttpUrl;

import java.util.Objects;

/**
 * Container object for a result returned by the ImageInfo MediaWiki module.
 *
 * @author Fastily
 */
public final class ImageInfo extends DataEntry implements Comparable<ImageInfo> {
    /**
     * The image size (in bytes)
     */
    public int size;

    /**
     * The file's height (in pixels), if applicable.
     */
    public int height;

    /**
     * The file's width (in pixels), if applicable.
     */
    public int width;

    /**
     * The sha1 hash for this file
     */
    public String sha1;

    /**
     * The url of the full size image.
     */
    public HttpUrl url;

    /**
     * The MIME string of the file.
     */
    public String mime;

    /**
     * Constructor, creates an ImageInfo with all null fields.
     */
    protected ImageInfo() {

    }

    /**
     * Compares the timestamps of two ImageInfo objects. Orders items newer -&gt; older.
     */
    public int compareTo(ImageInfo o) {
        return o.timestamp.compareTo(timestamp);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        if (!super.equals(o))
            return false;
        ImageInfo imageInfo = (ImageInfo) o;
        return this.size == imageInfo.size && this.height == imageInfo.height && this.width == imageInfo.width && Objects.equals(this.sha1, imageInfo.sha1) && Objects.equals(this.url, imageInfo.url) && Objects.equals(this.mime,
                imageInfo.mime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), this.size, this.height, this.width, this.sha1, this.url, this.mime);
    }
}