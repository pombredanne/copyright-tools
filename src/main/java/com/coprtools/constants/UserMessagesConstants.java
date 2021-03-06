/*
 * Copyright (C) 2016 Dimcho Nedev
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

package com.coprtools.constants;

/**
 * User messages constants.
 * TODO: Consider to remove this class. May be this is
 * an over-design.
 *
 * @author Dimcho Nedev
 */
public class UserMessagesConstants {
    public static final String SUCCESFULL_OPERATION_MESSAGE = "Operation completed successfully.";

    public static final String FAILD_OPERTION_MESSAGE = "There are some failed insertions. "
            + "Run with --info option to get log conole output and log file generation.\n" + "Path: \"%s\"";
}
