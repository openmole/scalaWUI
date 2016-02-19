package fr.iscpif.ext

/*
 * Copyright (C) 19/02/16 // mathieu.leclaire@openmole.org
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

object Data {

  sealed trait ID {
    def id: String
  }

  case class ExecutionId(id: String = java.util.UUID.randomUUID.toString) extends ID

  case class EnvironmentId(id: String = java.util.UUID.randomUUID.toString) extends ID

  sealed trait ErrorStateLevel {
    def name: String
  }

  case class DebugLevel() extends ErrorStateLevel {
    val name = "DEBUG"
  }

  case class ErrorLevel() extends ErrorStateLevel {
    val name = "ERROR"
  }

  case class EnvironmentError(environmentId: EnvironmentId, errorMessage: String, stack: Error, date: Long, level: ErrorStateLevel)

  case class RunningEnvironmentData(id: ExecutionId, errors: Seq[(EnvironmentError, Int)])

  case class RunningOutputData(id: ExecutionId, output: String)

  case class RunningData(environmentsData: Seq[RunningEnvironmentData], outputsData: Seq[RunningOutputData])


}
