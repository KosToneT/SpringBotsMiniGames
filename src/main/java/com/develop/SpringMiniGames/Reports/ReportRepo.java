package com.develop.SpringMiniGames.Reports;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReportRepo extends CrudRepository<Report, Long>  {
    Report findById(long id);
}
