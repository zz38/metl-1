/**
 * Licensed to JumpMind Inc under one or more contributor
 * license agreements.  See the NOTICE file distributed
 * with this work for additional information regarding
 * copyright ownership.  JumpMind Inc licenses this file
 * to you under the GNU General Public License, version 3.0 (GPLv3)
 * (the "License"); you may not use this file except in compliance
 * with the License.
 *
 * You should have received a copy of the GNU General Public License,
 * version 3.0 (GPLv3) along with this library; if not, see
 * <http://www.gnu.org/licenses/>.
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.jumpmind.metl.core.runtime;

import java.util.Map;

import org.jumpmind.metl.core.runtime.component.ComponentContext;

public interface IExecutionTracker {
    
    public void flowStepStarted(int threadNumber, ComponentContext context);
    
    public void beforeHandle(int threadNumber, ComponentContext context);
    
    public void afterHandle(int threadNumber, ComponentContext context, Throwable error);
    
    public void updateStatistics(int threadNumber, ComponentContext context);
    
    public void flowStepFinished(int threadNumber, ComponentContext context, Throwable error, boolean cancelled);
    
    public void beforeFlow(String executionId, Map<String, String> flowParameters);
    
    public void afterFlow();
    
    public void flowStepFailedOnComplete(ComponentContext context, Throwable error);

    public void log (int threadNumber, LogLevel level, ComponentContext context, String output, Object... args);
    
    public String getExecutionId();
    
}
