/*
 * PagedResource.java
 *
 * Copyright (c) 2018 IUNGO (https://iungo.network)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package network.iungo.status.bitbucket.response;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * The type Paged resource.
 *
 * @param <T> the type parameter
 */
public abstract class PagedResource<T> {

    @JsonProperty("pagelen")
    private String pageLength;

    @JsonProperty("values")
    private List<T> values;

    @JsonProperty("next")
    private String next;

    /**
     * Gets page length.
     *
     * @return the page length
     */
    public String getPageLength() {
        return pageLength;
    }

    /**
     * Sets page length.
     *
     * @param pageLength the page length
     */
    public void setPageLength(String pageLength) {
        this.pageLength = pageLength;
    }

    /**
     * Gets values.
     *
     * @return the values
     */
    public List<T> getValues() {
        return values;
    }

    /**
     * Sets values.
     *
     * @param values the values
     */
    public void setValues(List<T> values) {
        this.values = values;
    }

    /**
     * Gets next.
     *
     * @return the next
     */
    public String getNext() {
        return next;
    }

    /**
     * Sets next.
     *
     * @param next the next
     */
    public void setNext(String next) {
        this.next = next;
    }
}
