package de.uniwue.dachs.haeuserbuch_backend.search;

import de.uniwue.dachs.haeuserbuch_backend.repository.GlobalSearchRepository;
import jakarta.annotation.PreDestroy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Component
public class GlobalSearchRefreshCoordinator {
    private static final Logger LOGGER = LoggerFactory.getLogger(GlobalSearchRefreshCoordinator.class);

    private final GlobalSearchRepository searchRepository;
    private final Duration refreshDelay;
    private final Duration refreshRetryDelay;
    private final ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor(
            Thread.ofPlatform().name("global-search-refresh").daemon(true).factory()
    );
    private final Object monitor = new Object();
    private boolean refreshRequested;
    private boolean workerScheduled;

    public GlobalSearchRefreshCoordinator(
            GlobalSearchRepository searchRepository,
            @Value("${haeuserbuch.search.refresh-delay:750ms}") Duration refreshDelay,
            @Value("${haeuserbuch.search.refresh-retry-delay:30s}") Duration refreshRetryDelay
    ) {
        this.searchRepository = searchRepository;
        this.refreshDelay = refreshDelay;
        this.refreshRetryDelay = refreshRetryDelay;
    }

    public void requestRefresh() {
        boolean scheduleWorker = false;
        synchronized (monitor) {
            refreshRequested = true;
            if (!workerScheduled) {
                workerScheduled = true;
                scheduleWorker = true;
            }
        }

        if (scheduleWorker) {
            executor.schedule(this::refreshUntilClean, refreshDelay.toMillis(), TimeUnit.MILLISECONDS);
        }
    }

    private void refreshUntilClean() {
        while (true) {
            synchronized (monitor) {
                if (!refreshRequested) {
                    workerScheduled = false;
                    return;
                }
                refreshRequested = false;
            }

            try {
                searchRepository.refreshSearchDocuments();
                LOGGER.info("Refreshed global search documents");
            } catch (RuntimeException exception) {
                LOGGER.error("Could not refresh global search documents", exception);
                synchronized (monitor) {
                    refreshRequested = true;
                }
                executor.schedule(this::refreshUntilClean, refreshRetryDelay.toMillis(), TimeUnit.MILLISECONDS);
                return;
            }
        }
    }

    @PreDestroy
    void shutdown() {
        executor.shutdown();
    }
}
