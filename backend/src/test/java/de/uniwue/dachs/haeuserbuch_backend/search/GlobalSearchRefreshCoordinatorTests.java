package de.uniwue.dachs.haeuserbuch_backend.search;

import de.uniwue.dachs.haeuserbuch_backend.repository.GlobalSearchRepository;
import org.junit.jupiter.api.Test;

import java.time.Duration;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.verify;

class GlobalSearchRefreshCoordinatorTests {
    @Test
    void coalescesABurstOfRefreshRequests() {
        GlobalSearchRepository repository = mock(GlobalSearchRepository.class);
        GlobalSearchRefreshCoordinator coordinator = new GlobalSearchRefreshCoordinator(
                repository,
                Duration.ofMillis(25),
                Duration.ofSeconds(1)
        );

        try {
            coordinator.requestRefresh();
            coordinator.requestRefresh();
            coordinator.requestRefresh();

            verify(repository, timeout(1_000).times(1)).refreshSearchDocuments();
        } finally {
            coordinator.shutdown();
        }
    }

    @Test
    void retriesAfterATransientRefreshFailure() {
        GlobalSearchRepository repository = mock(GlobalSearchRepository.class);
        doThrow(new IllegalStateException("temporary failure"))
                .doNothing()
                .when(repository)
                .refreshSearchDocuments();
        GlobalSearchRefreshCoordinator coordinator = new GlobalSearchRefreshCoordinator(
                repository,
                Duration.ofMillis(5),
                Duration.ofMillis(20)
        );

        try {
            coordinator.requestRefresh();

            verify(repository, timeout(1_000).times(2)).refreshSearchDocuments();
        } finally {
            coordinator.shutdown();
        }
    }
}
