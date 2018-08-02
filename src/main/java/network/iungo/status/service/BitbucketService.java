/*
 * BitbucketService.java
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

import network.iungo.status.bitbucket.BitbucketApi;
import network.iungo.status.bitbucket.auth.BitbucketCredentials;
import network.iungo.status.bitbucket.response.commits.Commit;
import network.iungo.status.bitbucket.response.commits.Commits;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Bitbucket service periodically calls Bitbucket REST API and aggregates commits
 * <p>
 * by MP
 */
@Service
public class BitbucketService {

    private static Logger LOG = LoggerFactory.getLogger(BitbucketService.class);

    private BitbucketApi bitbucketApi;

    private ArrayList<String> repositories = new ArrayList<>();

    private ConcurrentHashMap<String, Commit> commits = new ConcurrentHashMap<>();

    private int[][] totalAggregatedCommits = null;

    private final static int TIME_BETWEEN_REQUESTS = 5000;

    /**
     * Instantiates a new Bitbucket service.
     *
     * @param credentials           the credentials
     * @param monitoredRepositories the monitored repositories
     */
    @Autowired
    public BitbucketService(BitbucketCredentials credentials, @Value("${bitbucket.repositories}") String monitoredRepositories) {
        bitbucketApi = new BitbucketApi(credentials);

        for (String repo : monitoredRepositories.split(",")) {
            repositories.add(repo.trim());
        }

        new Timer("BitbucketTimer").scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                try {
                    for (String repo : repositories) {
                        Commits repoCommits = bitbucketApi.getCommits(repo);
                        for (Commit c : repoCommits.getValues()) {
                            commits.putIfAbsent(c.getHash(), c);
                        }
                        LOG.info("Found {} commits in '{}' repository", repoCommits.getValues().size(), repo);
                        aggregate();

                        Thread.sleep(TIME_BETWEEN_REQUESTS);
                    }
                } catch (Exception e) {
                    LOG.error(e.getMessage());
                }
            }
        }, 0, 24 * 60 * 60 * 1000);
    }

    private void aggregate() {
        totalAggregatedCommits = new int[7][24];
        for (Commit c : commits.values()) {
            LocalDateTime localDateTime = c.getDate().toLocalDateTime();
            int dow = localDateTime.getDayOfWeek().getValue();
            int hod = localDateTime.getHour();
            totalAggregatedCommits[dow - 1][hod - 1]++;
        }
    }

    /**
     * Get aggregated commits int [ ] [ ].
     *
     * @return the int [ ] [ ]
     */
    public int[][] getAggregatedCommits() {
        return totalAggregatedCommits;
    }

}
