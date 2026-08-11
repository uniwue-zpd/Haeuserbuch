package de.uniwue.dachs.haeuserbuch_backend.search;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.aop.aspectj.annotation.AspectJProxyFactory;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

class GlobalSearchMutationAspectTests {
    static class SearchMutationService {
        @SearchIndexAffecting
        public void importResearchData() {
        }

        public void readResearchData() {
        }
    }

    @AfterEach
    void clearTransactionState() {
        if (TransactionSynchronizationManager.isSynchronizationActive()) {
            TransactionSynchronizationManager.clearSynchronization();
        }
        TransactionSynchronizationManager.setActualTransactionActive(false);
    }

    @Test
    void requestsRefreshOnlyAfterTheSurroundingTransactionCommits() {
        GlobalSearchRefreshCoordinator coordinator = mock(GlobalSearchRefreshCoordinator.class);
        GlobalSearchMutationAspect aspect = new GlobalSearchMutationAspect(coordinator);
        TransactionSynchronizationManager.setActualTransactionActive(true);
        TransactionSynchronizationManager.initSynchronization();

        aspect.refreshAfterSuccessfulMutation();

        verify(coordinator, never()).requestRefresh();
        for (TransactionSynchronization synchronization : TransactionSynchronizationManager.getSynchronizations()) {
            synchronization.afterCommit();
        }
        verify(coordinator).requestRefresh();
    }

    @Test
    void requestsRefreshImmediatelyWhenNoTransactionIsActive() {
        GlobalSearchRefreshCoordinator coordinator = mock(GlobalSearchRefreshCoordinator.class);
        GlobalSearchMutationAspect aspect = new GlobalSearchMutationAspect(coordinator);

        aspect.refreshAfterSuccessfulMutation();

        verify(coordinator).requestRefresh();
    }

    @Test
    void detectsExplicitlyAnnotatedMutationsRegardlessOfTheirMethodName() {
        GlobalSearchRefreshCoordinator coordinator = mock(GlobalSearchRefreshCoordinator.class);
        AspectJProxyFactory proxyFactory = new AspectJProxyFactory(new SearchMutationService());
        proxyFactory.addAspect(new GlobalSearchMutationAspect(coordinator));
        SearchMutationService service = proxyFactory.getProxy();

        service.importResearchData();

        verify(coordinator).requestRefresh();
    }

    @Test
    void ignoresUnannotatedMethodsEvenWhenTheyShareTheSameService() {
        GlobalSearchRefreshCoordinator coordinator = mock(GlobalSearchRefreshCoordinator.class);
        AspectJProxyFactory proxyFactory = new AspectJProxyFactory(new SearchMutationService());
        proxyFactory.addAspect(new GlobalSearchMutationAspect(coordinator));
        SearchMutationService service = proxyFactory.getProxy();

        service.readResearchData();

        verify(coordinator, never()).requestRefresh();
    }
}
