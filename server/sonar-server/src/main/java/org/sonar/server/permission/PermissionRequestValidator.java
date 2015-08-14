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

package org.sonar.server.permission;

import javax.annotation.Nullable;
import org.sonar.core.permission.GlobalPermissions;
import org.sonar.core.permission.ProjectPermissions;

import static java.lang.String.format;
import static org.sonar.api.security.DefaultGroups.isAnyone;
import static org.sonar.server.ws.WsUtils.checkRequest;

public class PermissionRequestValidator {
  private PermissionRequestValidator() {
    // static methods only
  }

  public static void validateProjectPermission(String permission) {
    checkRequest(ProjectPermissions.ALL.contains(permission),
      format("The 'permission' parameter for project permissions must be one of %s. '%s' was passed.", ProjectPermissions.ALL_ON_ONE_LINE, permission));
  }

  public static void validateGlobalPermission(String permission) {
    checkRequest(GlobalPermissions.ALL.contains(permission),
      format("The 'permission' parameter for global permissions must be one of %s. '%s' was passed.", GlobalPermissions.ALL_ON_ONE_LINE, permission));
  }

  public static void validateNotAnyoneAndAdminPermission(String permission, @Nullable String groupName) {
    checkRequest(!GlobalPermissions.SYSTEM_ADMIN.equals(permission) || !isAnyone(groupName),
      String.format("It is not possible to add the '%s' permission to the '%s' group.", permission, groupName));
  }
}