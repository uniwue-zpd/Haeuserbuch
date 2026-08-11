package de.uniwue.dachs.haeuserbuch_backend.search;

import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

@Aspect
@Component
public class GlobalSearchMutationAspect {
    private final GlobalSearchRefreshCoordinator refreshCoordinator;

    public GlobalSearchMutationAspect(GlobalSearchRefreshCoordinator refreshCoordinator) {
        this.refreshCoordinator = refreshCoordinator;
    }

    @AfterReturning("@annotation(de.uniwue.dachs.haeuserbuch_backend.search.SearchIndexAffecting)")
    public void refreshAfterSuccessfulMutation() {
        if (TransactionSynchronizationManager.isActualTransactionActive()
                && TransactionSynchronizationManager.isSynchronizationActive()) {
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                @Override
                public void afterCommit() {
                    refreshCoordinator.requestRefresh();
                }
            });
            return;
        }

        refreshCoordinator.requestRefresh();
    }
}
