/*
 * StatusController.java
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

package network.iungo.status.controller;

import network.iungo.status.service.BitbucketService;
import network.iungo.status.service.MonitoringService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * The type Status controller.
 */
@Controller
public class StatusController {

    private final MonitoringService monitoringService;

    private final BitbucketService bitbucketService;

    /**
     * Instantiates a new Status controller.
     *
     * @param monitoringService the monitoring service
     * @param bitbucketService  the bitbucket service
     */
    @Autowired
    public StatusController(MonitoringService monitoringService, BitbucketService bitbucketService) {
        this.monitoringService = monitoringService;
        this.bitbucketService = bitbucketService;
    }

    /**
     * Index string.
     *
     * @param m the m
     * @return the string
     */
    @GetMapping("/")
    public String index(final Model m) {
        m.addAttribute("services", monitoringService.getServices());
        return "index";
    }

    /**
     * Commits int [ ] [ ].
     *
     * @return the int [ ] [ ]
     */
    @GetMapping("/commits")
    @ResponseBody
    public int[][] commits() {
        return bitbucketService.getAggregatedCommits();
    }

}
