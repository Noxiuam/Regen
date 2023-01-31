package rip.kits.regen.report;

import com.mongodb.*;
import com.mongodb.MongoClientOptions.Builder;
import com.mongodb.client.model.DBCollectionUpdateOptions;
import lombok.Getter;
import rip.kits.regen.Regen;
import rip.kits.regen.report.util.ReportBooleanCallback;
import rip.kits.regen.util.GeneralCallback;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Getter
public class ReportBackend {
    private Set<Report> reports = new HashSet<>();
    MongoClient mongoClient;
    DB mongoDatabase;
    DBCollection reportCollection;

    public ReportBackend() {}

    public boolean insertReport(Report report) {
        this.getReports().add(report);
        this.updateReport(report.getPlayerID(), report.getExecutorID(), report);
        return true;
    }

    public void initBackend(GeneralCallback<Boolean> callback) {
        this.mongoClient = new MongoClient(Regen.getInstance().getConfig().getString("mongo.ip"), (new Builder()).connectTimeout(2000).build());

        this.mongoDatabase = this.mongoClient.getDB("regencr");
        this.reportCollection = this.mongoDatabase.getCollection("reports");
        callback.call(true);
    }

    public boolean updateReport(UUID target, UUID reportID, Report report) {
        if (this.getReportOfById(target, reportID) != null) {
            Report s = this.getReportOfById(target, reportID);
            this.getReports().remove(s);
            report.setReportID(s.getReportID());
            report.setPlayerID(s.getPlayerID());
            report.setExecutorID(s.getExecutorID());
            report.setPlayerName(s.getPlayerName());
            report.setExecutorName(s.getExecutorName());
            report.setTime(s.getTime());
            this.getReports().add(report);
        }
        DBObject reportObject = (new BasicDBObjectBuilder()).add("reportID", reportID.toString()).add("playerID", report.getPlayerID().toString()).add("executorID", report.getExecutorID() != null ? report.getExecutorID().toString() : "").add("playerName", report.getPlayerName()).add("executorName", report.getExecutorName()).add("executorName", report.getExecutorName()).add("reason", report.getReason()).add("time", report.getTime()).get();
        this.reportCollection.update(new BasicDBObject("reportID", report.getReportID().toString()), reportObject, (new DBCollectionUpdateOptions()).upsert(true));
        return false;
    }

    public boolean loadFromDatabase() {
        DBCursor cursor = this.reportCollection.find();
        cursor.forEach((dbObject) -> {
            this.parseReports(dbObject, (callback, result) -> {
                if (callback) {
                    this.reports.add(result);
                } else {
                    System.out.println("Failed to parse " + dbObject.get("_id"));
                }
            });
        });
        return true;
    }

    public void shutdown() { this.mongoClient.close(); }

    public void parseReports(DBObject object, ReportBooleanCallback callback) {
        try {
            UUID reportID = UUID.fromString((String) object.get("reportID"));
            UUID playerID = UUID.fromString((String) object.get("playerID"));
            UUID executorID = UUID.fromString((String) object.get("executorID"));
            String playerName = (String) object.get("playerName");
            String executorName = (String) object.get("executorName");
            String reason = (String) object.get("reason");
            long time = (Long) object.get("time");

            callback.call(true, new Report(reportID, playerID, executorID, playerName, executorName, reason, time));
        } catch (Exception ex) {
            ex.printStackTrace();
            callback.call(false, null);
        }
    }

    public Report getReportOfById(UUID target, UUID reportID) {
        return this.getReports().stream().filter((report) -> {
            return report.getReportID().toString().equals(reportID.toString()) && report.getPlayerID().toString().equals(target.toString());
        }).findFirst().orElse(null);
    }

    public List<Report> getReportOf(UUID uuid) {
        return this.getReports().stream().filter((report) -> {
            return report.getPlayerID().toString().equals(uuid.toString());
        }).collect(Collectors.toList());
    }
}
