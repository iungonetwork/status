/*
 * MonitoredEndpoint.java
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

package network.iungo.status.domain;

import org.ocpsoft.prettytime.PrettyTime;

import java.util.Date;

/**
 * The type Monitored endpoint.
 */
public class MonitoredEndpoint {

    private String url;
    private String title;
    private String description;
    private String status;
    private int expectedResponseStatus;
    private Date checked;
    private boolean online;

    /**
     * Gets url.
     *
     * @return the url
     */
    public String getUrl() {
        return url;
    }

    /**
     * Sets url.
     *
     * @param url the url
     */
    public void setUrl(String url) {
        this.url = url;
    }

    /**
     * Gets title.
     *
     * @return the title
     */
    public String getTitle() {
        return title;
    }

    /**
     * Sets title.
     *
     * @param title the title
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * Gets description.
     *
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets description.
     *
     * @param description the description
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Gets status.
     *
     * @return the status
     */
    public String getStatus() {
        return status;
    }

    /**
     * Sets status.
     *
     * @param status the status
     */
    public void setStatus(String status) {
        this.status = status;
    }

    /**
     * Gets expected response status.
     *
     * @return the expected response status
     */
    public int getExpectedResponseStatus() {
        return expectedResponseStatus;
    }

    /**
     * Sets expected response status.
     *
     * @param expectedResponseStatus the expected response status
     */
    public void setExpectedResponseStatus(int expectedResponseStatus) {
        this.expectedResponseStatus = expectedResponseStatus;
    }

    /**
     * Is online boolean.
     *
     * @return the boolean
     */
    public boolean isOnline() {
        return online;
    }

    /**
     * Sets online.
     *
     * @param online the online
     */
    public void setOnline(boolean online) {
        this.online = online;
    }

    /**
     * Gets checked.
     *
     * @return the checked
     */
    public Date getChecked() {
        return checked;
    }

    /**
     * Sets checked.
     *
     * @param checked the checked
     */
    public void setChecked(Date checked) {
        this.checked = checked;
    }

    /**
     * Gets checked pretty.
     *
     * @return the checked pretty
     */
    public String getCheckedPretty() {
        return new PrettyTime().format(checked);
    }

}
