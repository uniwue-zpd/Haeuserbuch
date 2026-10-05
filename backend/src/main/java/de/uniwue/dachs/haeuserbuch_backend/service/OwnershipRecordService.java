package de.uniwue.dachs.haeuserbuch_backend.service;

import de.uniwue.dachs.haeuserbuch_backend.model.OwnershipRecord;
import de.uniwue.dachs.haeuserbuch_backend.repository.OwnershipRecordRepository;
import de.uniwue.dachs.haeuserbuch_backend.utils.Mappers.OwnershipRecordMapper;
import de.uniwue.dachs.haeuserbuch_backend.utils.XML.validators.OwnershipRecordValidator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class OwnershipRecordService {

    private final OwnershipRecordRepository ownershipRecordRepository;
    private final OwnershipRecordMapper ownershipRecordMapper;
    private final OwnershipRecordValidator ownershipRecordValidator;

    public OwnershipRecordService(
            OwnershipRecordRepository ownershipRecordRepository,
            OwnershipRecordMapper ownershipRecordMapper,
            OwnershipRecordValidator ownershipRecordValidator
    ) {
        this.ownershipRecordRepository = ownershipRecordRepository;
        this.ownershipRecordMapper = ownershipRecordMapper;
        this.ownershipRecordValidator = ownershipRecordValidator;
    }

    @Transactional
    public OwnershipRecord create(String xml) {
        OwnershipRecord record = ownershipRecordMapper.toEntity(xml);
        record = ownershipRecordRepository.save(record);
        record.setEntryXml(ownershipRecordMapper.enrichXml(xml, record));
        return ownershipRecordRepository.save(record);
    }

    public Optional<String> getXmlById(Long id) {
        return ownershipRecordRepository.findById(id)
                .map(OwnershipRecord::getEntryXml);
    }


    public List<String> validate(String xml) {
        return ownershipRecordValidator.validate(xml);
    }
}
