/*
 * SonarQube, open source software quality management tool.
 * Copyright (C) 2008-2013 SonarSource
 * mailto:contact AT sonarsource DOT com
 *
 * SonarQube is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * SonarQube is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package org.sonar.core.persistence;

import com.google.common.io.Files;
import org.apache.commons.dbcp.BasicDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonar.api.ServerComponent;
import org.sonar.api.issue.Issue;
import org.sonar.api.platform.ServerFileSystem;
import org.sonar.api.utils.SonarException;

import javax.annotation.Nullable;
import javax.sql.DataSource;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;

public class DryRunDatabaseFactory implements ServerComponent {
  private static final Logger LOG = LoggerFactory.getLogger(DryRunDatabaseFactory.class);
  private static final String DIALECT = "h2";
  private static final String DRIVER = "org.h2.Driver";
  private static final String URL = "jdbc:h2:";
  private static final String H2_FILE_SUFFIX = ".h2.db";
  private static final String SONAR = "sonar";
  private static final String USER = SONAR;
  private static final String PASSWORD = SONAR;

  private final Database database;
  private final ServerFileSystem serverFileSystem;

  public DryRunDatabaseFactory(Database database, ServerFileSystem serverFileSystem) {
    this.database = database;
    this.serverFileSystem = serverFileSystem;
  }

  public byte[] createDatabaseForDryRun(@Nullable Long projectId) {
    long startup = System.currentTimeMillis();
    String name = serverFileSystem.getTempDir().getAbsolutePath() + "db-" + System.nanoTime();

    try {
      DataSource source = database.getDataSource();
      BasicDataSource destination = create(DIALECT, DRIVER, USER, PASSWORD, URL + name);

      copy(source, destination, projectId);
      close(destination);

      if (LOG.isDebugEnabled()) {
        File dbFile = new File(name + H2_FILE_SUFFIX);
        long size = dbFile.length();
        long duration = System.currentTimeMillis() - startup;
        if (projectId == null) {
          LOG.debug("Dry Run Database created in " + duration + " ms, size is " + size + " bytes");
        } else {
          LOG.debug("Dry Run Database for project " + projectId + " created in " + duration + " ms, size is " + size + " bytes");
        }
      }

      return dbFileContent(name);
    } catch (SQLException e) {
      throw new SonarException("Unable to create database for DryRun", e);
    }
  }

  private void copy(DataSource source, DataSource dest, @Nullable Long projectId) {
    DbTemplate template = new DbTemplate();
    template
      .copyTable(source, dest, "active_rules")
      .copyTable(source, dest, "active_rule_parameters")
      .copyTable(source, dest, "characteristics")
      .copyTable(source, dest, "characteristic_edges")
      .copyTable(source, dest, "characteristic_properties")
      .copyTable(source, dest, "metrics")
      .copyTable(source, dest, "permission_templates")
      .copyTable(source, dest, "perm_templates_users")
      .copyTable(source, dest, "perm_templates_groups")
      .copyTable(source, dest, "quality_models")
      .copyTable(source, dest, "rules")
      .copyTable(source, dest, "rules_parameters")
      .copyTable(source, dest, "rules_profiles")
      .copyTable(source, dest, "alerts");
    if (projectId != null) {
      template.copyTable(source, dest, "projects", projectQuery(projectId, false));

      template.copyTable(source, dest, "snapshots", "SELECT * FROM snapshots WHERE project_id=" + projectId);
      template.copyTable(source, dest, "project_measures", "SELECT m.* FROM project_measures m INNER JOIN snapshots s on m.snapshot_id=s.id WHERE s.project_id=" + projectId);

      StringBuilder issueQuery = new StringBuilder()
        .append("SELECT issues.* FROM issues")
        .append(" INNER JOIN (")
        .append(projectQuery(projectId, true))
        .append(") resources")
        .append(" ON issues.component_id=resources.id")
        .append(" AND status <> '").append(Issue.STATUS_CLOSED).append("'");
      template.copyTable(source, dest, "issues", issueQuery.toString());
    }
  }

  private String projectQuery(Long projectId, boolean returnOnlyIds) {
    return new StringBuilder()
      .append("SELECT p.").append(returnOnlyIds ? "id" : "*")
      .append(" FROM projects p INNER JOIN snapshots s ON p.id = s.project_id")
      .append(" WHERE s.islast=").append(database.getDialect().getTrueSqlValue())
      .append(" AND (")
      .append("   s.root_project_id=").append(projectId)
      .append("   OR p.id=").append(projectId)
      .append("   OR p.root_id=").append(projectId)
      .append(" )").toString();
  }

  private BasicDataSource create(String dialect, String driver, String user, String password, String url) {
    BasicDataSource dataSource = new DbTemplate().dataSource(driver, user, password, url);
    new DbTemplate().createSchema(dataSource, dialect);
    return dataSource;
  }

  private void close(BasicDataSource destination) throws SQLException {
    destination.close();
  }

  private byte[] dbFileContent(String name) {
    try {
      File dbFile = new File(name + H2_FILE_SUFFIX);
      byte[] content = Files.toByteArray(dbFile);
      dbFile.delete();
      return content;
    } catch (IOException e) {
      throw new SonarException("Unable to read h2 database file", e);
    }
  }
}
