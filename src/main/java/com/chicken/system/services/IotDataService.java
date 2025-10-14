package com.chicken.system.services;

import com.chicken.system.dto.CreateIotDataRequest;
import com.chicken.system.dto.IotDataResponse;
import com.chicken.system.entity.IotData;
import com.chicken.system.repository.IotDataRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

@Service
public class IotDataService {

    private final IotDataRepository repository;
    private final IotDataViewService iotDataViewService; // <- for SSE push

    public IotDataService(IotDataRepository repository,
                          IotDataViewService iotDataViewService) {
        this.repository = repository;
        this.iotDataViewService = iotDataViewService;
    }

    @Transactional
    public IotDataResponse create(CreateIotDataRequest req) {
        IotData entity = new IotData();
        entity.setTemperatureC(req.getTemperatureC());
        entity.setHumidityPct(req.getHumidityPct());
        entity.setWaterLevelPct(req.getWaterLevelPct());

        // Save reading
        IotData saved = repository.save(entity);
        // ensure DB-generated fields (e.g., createdAt) are materialized now
        repository.flush();

        // After COMMIT, broadcast to all SSE subscribers
        if (TransactionSynchronizationManager.isSynchronizationActive()) {
            final long id = saved.getId();
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                @Override
                public void afterCommit() {
                    iotDataViewService.pushNewReading(id);
                }
            });
        } else {
            // fallback (non-transactional contexts)
            iotDataViewService.pushNewReading(saved.getId());
        }

        return new IotDataResponse(
                saved.getId(),
                saved.getTemperatureC(),
                saved.getHumidityPct(),
                saved.getWaterLevelPct(),
                saved.getCreatedAt()
        );
    }
}
