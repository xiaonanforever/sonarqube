/*
 * SonarQube, open source software quality management tool.
 * Copyright (C) 2008-2014 SonarSource
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
package org.sonar.db;

import java.util.Arrays;
import java.util.List;
import org.sonar.db.activity.ActivityDao;
import org.sonar.db.component.ComponentLinkDao;
import org.sonar.db.component.ResourceDao;
import org.sonar.db.component.ResourceIndexerDao;
import org.sonar.db.component.ResourceKeyUpdaterDao;
import org.sonar.db.component.SnapshotDao;
import org.sonar.db.compute.AnalysisReportDao;
import org.sonar.db.dashboard.ActiveDashboardDao;
import org.sonar.db.dashboard.DashboardDao;
import org.sonar.db.dashboard.WidgetDao;
import org.sonar.db.dashboard.WidgetPropertyDao;
import org.sonar.db.debt.CharacteristicDao;
import org.sonar.db.duplication.DuplicationDao;
import org.sonar.db.event.EventDao;
import org.sonar.db.issue.ActionPlanDao;
import org.sonar.db.issue.ActionPlanStatsDao;
import org.sonar.db.issue.IssueChangeDao;
import org.sonar.db.issue.IssueDao;
import org.sonar.db.issue.IssueFilterDao;
import org.sonar.db.issue.IssueFilterFavouriteDao;
import org.sonar.db.loadedtemplate.LoadedTemplateDao;
import org.sonar.db.measure.MeasureDao;
import org.sonar.db.measure.MeasureFilterDao;
import org.sonar.db.notification.NotificationQueueDao;
import org.sonar.db.permission.PermissionDao;
import org.sonar.db.permission.PermissionTemplateDao;
import org.sonar.db.property.PropertiesDao;
import org.sonar.db.purge.PurgeDao;
import org.sonar.db.qualitygate.QualityGateConditionDao;
import org.sonar.db.qualityprofile.QualityProfileDao;
import org.sonar.db.rule.RuleDao;
import org.sonar.db.semaphore.SemaphoreDao;
import org.sonar.db.source.FileSourceDao;
import org.sonar.db.user.AuthorDao;
import org.sonar.db.user.AuthorizationDao;
import org.sonar.db.user.GroupMembershipDao;
import org.sonar.db.user.RoleDao;
import org.sonar.db.user.UserDao;
import org.sonar.db.user.UserGroupDao;

public final class DaoUtils {

  private DaoUtils() {
    // only static stuff
  }

  public static List<Class<? extends Dao>> getDaoClasses() {
    return Arrays.asList(
      ActionPlanDao.class,
      ActionPlanStatsDao.class,
      ActiveDashboardDao.class,
      ActivityDao.class,
      AnalysisReportDao.class,
      AuthorDao.class,
      AuthorizationDao.class,
      ComponentLinkDao.class,
      DashboardDao.class,
      DuplicationDao.class,
      EventDao.class,
      FileSourceDao.class,
      GroupMembershipDao.class,
      IssueDao.class,
      IssueChangeDao.class,
      IssueFilterDao.class,
      IssueFilterFavouriteDao.class,
      LoadedTemplateDao.class,
      MeasureDao.class,
      MeasureFilterDao.class,
      NotificationQueueDao.class,
      PermissionDao.class,
      PermissionTemplateDao.class,
      PropertiesDao.class,
      QualityGateConditionDao.class,
      QualityProfileDao.class,
      PurgeDao.class,
      CharacteristicDao.class,
      ResourceIndexerDao.class,
      ResourceDao.class,
      ResourceKeyUpdaterDao.class,
      RoleDao.class,
      RuleDao.class,
      SemaphoreDao.class,
      SnapshotDao.class,
      UserDao.class,
      UserGroupDao.class,
      WidgetDao.class,
      WidgetPropertyDao.class
      );
  }
}