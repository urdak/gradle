/*
 * Copyright 2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.gradle.api.internal.changedetection;

import javax.annotation.Nullable;
import java.util.Optional;

/**
 * Keeps information about the execution mode of a task.
 */
public class TaskExecutionMode {
    public static final TaskExecutionMode INCREMENTAL = new TaskExecutionMode(null, true, true);
    public static final TaskExecutionMode NO_OUTPUTS = new TaskExecutionMode("Task has not declared any outputs despite executing actions.", false, false);
    public static final TaskExecutionMode RERUN_TASKS_ENABLED = new TaskExecutionMode("Executed with '--rerun-tasks'.", true, false);
    public static final TaskExecutionMode UNTRACKED = new TaskExecutionMode("Task state is not tracked.", false, false);

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    private final Optional<String> rebuildReason;
    private final boolean taskHistoryMaintained;
    private final boolean allowedToUseCachedResults;

    private TaskExecutionMode(@Nullable String rebuildReason, boolean taskHistoryMaintained, boolean allowedToUseCachedResults) {
        this.rebuildReason = Optional.ofNullable(rebuildReason);
        this.taskHistoryMaintained = taskHistoryMaintained;
        this.allowedToUseCachedResults = allowedToUseCachedResults;
    }

    /**
     * Return rebuild reason if any.
     */
    public Optional<String> getRebuildReason() {
        return rebuildReason;
    }

    /**
     * Returns whether the execution history should be stored.
     */
    public boolean isTaskHistoryMaintained() {
        return taskHistoryMaintained;
    }

    /**
     * Returns whether it is okay to use results loaded from cache instead of executing the task.
     */
    public boolean isAllowedToUseCachedResults() {
        return allowedToUseCachedResults;
    }

    public static class UpToDateWhenFalse extends TaskExecutionMode {
        private UpToDateWhenFalse(@Nullable String rebuildReason) {
            super(rebuildReason, true, false);
        }
    }

    public static TaskExecutionMode upToDateWhenFalse(@Nullable String rebuildReason) {
        return new UpToDateWhenFalse(rebuildReason);
    }
}
