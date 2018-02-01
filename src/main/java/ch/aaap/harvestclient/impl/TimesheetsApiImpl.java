package ch.aaap.harvestclient.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.aaap.harvestclient.api.TimesheetsApi;
import ch.aaap.harvestclient.api.filter.TimeEntryListFilter;
import ch.aaap.harvestclient.domain.TimeEntry;
import ch.aaap.harvestclient.domain.pagination.PaginatedTimeEntry;
import ch.aaap.harvestclient.domain.param.TimeEntryCreationInfo;
import ch.aaap.harvestclient.domain.param.TimeEntryUpdateInfo;
import ch.aaap.harvestclient.domain.reference.TimeEntryReference;
import ch.aaap.harvestclient.service.TimeEntryService;
import retrofit2.Call;

public class TimesheetsApiImpl implements TimesheetsApi {
    private final static Logger log = LoggerFactory.getLogger(TimesheetsApiImpl.class);
    private static final int PER_PAGE = 100;
    private final TimeEntryService service;

    public TimesheetsApiImpl(TimeEntryService timeEntryService) {
        service = timeEntryService;
    }

    @Override
    public List<TimeEntry> list(TimeEntryListFilter filter) {

        Integer nextPage = 1;

        List<TimeEntry> timeEntries = new ArrayList<>();

        while (nextPage != null) {
            log.debug("Getting page {} of timeentries list", nextPage);

            Map<String, Object> filterMap = filter.toMap();
            // add pagination settings
            filterMap.put("page", nextPage);
            filterMap.put("per_page", PER_PAGE);

            Call<PaginatedTimeEntry> call = service.list(filterMap);

            PaginatedTimeEntry paginatedTimeEntry = ExceptionHandler.callOrThrow(call);

            timeEntries.addAll(paginatedTimeEntry.getTimeEntries());
            nextPage = paginatedTimeEntry.getNextPage();
        }

        log.debug("Listed {} timeentries: {}", timeEntries.size(), timeEntries);

        return timeEntries;

    }

    @Override
    public TimeEntry get(TimeEntryReference timeEntryReference) {
        Call<TimeEntry> call = service.get(timeEntryReference.getId());
        TimeEntry entry = ExceptionHandler.callOrThrow(call);

        log.debug("Got entry {}", entry);
        return entry;
    }

    @Override
    public TimeEntry create(TimeEntryCreationInfo creationInfo) {

        Call<TimeEntry> call = service.create(creationInfo);
        TimeEntry timeEntry = ExceptionHandler.callOrThrow(call);

        log.debug("Created {} ", timeEntry);

        return timeEntry;

    }

    @Override
    public TimeEntry update(TimeEntryReference timeEntryReference, TimeEntryUpdateInfo updatedInfo) {

        log.debug("Updating properties {} for timeentry {}", updatedInfo, timeEntryReference);
        Call<TimeEntry> call = service.update(timeEntryReference.getId(), updatedInfo);
        return ExceptionHandler.callOrThrow(call);

    }

    @Override
    public void delete(TimeEntryReference timeEntryReference) {
        log.debug("Deleting TimeEntry {}", timeEntryReference);

        Call<Void> call = service.delete(timeEntryReference.getId());
        ExceptionHandler.callOrThrow(call);
    }

    @Override
    public TimeEntry restart(TimeEntryReference timeEntryReference) {

        Call<TimeEntry> call = service.restart(timeEntryReference.getId());
        TimeEntry entry = ExceptionHandler.callOrThrow(call);

        log.debug("Restarted entry {}", entry);

        return entry;
    }

    @Override
    public TimeEntry stop(TimeEntryReference timeEntryReference) {
        Call<TimeEntry> call = service.stop(timeEntryReference.getId());
        TimeEntry entry = ExceptionHandler.callOrThrow(call);

        log.debug("Stopped entry {}", entry);

        return entry;
    }
}
