/*
 * MonitoringService.java
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

package network.iungo.status.service;

import network.iungo.status.domain.MonitoredEndpoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;

/**
 * The type Monitoring service.
 */
@Service
public class MonitoringService {

    private static Logger LOG = LoggerFactory.getLogger(MonitoringService.class);

    private final static int READ_TIMEOUT = 10000;
    private final static int CONNECT_TIMEOUT = 10000;

    private List<MonitoredEndpoint> services = new ArrayList<>();

    /**
     * Instantiates a new Monitoring service.
     */
    public MonitoringService() {
        {
            MonitoredEndpoint m = new MonitoredEndpoint();
            m.setTitle("Website");
            m.setUrl("https://iungo.network");
            m.setDescription("");
            m.setExpectedResponseStatus(200);
            services.add(m);
        }

        {
            MonitoredEndpoint m = new MonitoredEndpoint();
            m.setTitle("APP");
            m.setUrl("https://app.iungo.network");
            m.setDescription("Service gateway");
            m.setExpectedResponseStatus(200);
            services.add(m);
        }

        {
            MonitoredEndpoint m = new MonitoredEndpoint();
            m.setTitle("API");
            m.setUrl("https://api.iungo.network");
            m.setDescription("");
            m.setExpectedResponseStatus(200);
            services.add(m);
        }

        Timer t = new Timer("ServiceMonitoring");
        t.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                for (MonitoredEndpoint e : services) {
                    try {
                        e.setOnline(check(e.getUrl(), e.getExpectedResponseStatus()));
                        e.setStatus("");
                    } catch (Exception ex) {
                        e.setOnline(false); // tikrai, kad offline
                        e.setStatus(ex.getMessage());
                    }
                    e.setChecked(new Date());
                }
            }
        }, 0, 1000 * 60 * 5);
    }

    /**
     * Check some endpoint
     * <p>
     * TODO MORE ELABORATE CHECKS AND METHODS (post?)
     *
     * @param url of the service to be checked
     * @return status online/offline
     * @throws IOException
     */
    private boolean check(String url, int expectedStatus) throws IOException {
        URL u = new URL(url);

        HttpURLConnection con = (HttpURLConnection) u.openConnection();
        con.setRequestMethod("GET");
        con.setConnectTimeout(CONNECT_TIMEOUT);
        con.setReadTimeout(READ_TIMEOUT);
        con.setInstanceFollowRedirects(false);

        int responseCode = con.getResponseCode();
        con.disconnect();

        LOG.debug("Checking {} > {}", url, responseCode);

        return responseCode == expectedStatus;
    }

    /**
     * Gets services.
     *
     * @return the services
     */
    public List<MonitoredEndpoint> getServices() {
        return services;
    }

}
