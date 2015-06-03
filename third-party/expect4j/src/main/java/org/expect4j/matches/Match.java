/*
 * Copyright (c) 2007 Justin Ryan
 * Copyright (c) 2013 Chris Verges <chris.verges@gmail.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you
 * may not use this file except in compliance with the License.  You may
 * obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied.  See the License for the specific language governing
 * permissions and limitations under the License.
 */

package org.expect4j.matches;

import org.expect4j.*;

/**
 * TODO
 *
 * @author Chris Verges
 * @author Justin Ryan
 */
public class Match {
    
    Closure closure;
    
    /** Creates a new instance of EofMatch */
    public Match(Closure closure) {
        this.closure = closure;
    }

    public Match() {
        this.closure = null;
    }

    public Closure getClosure() {
        return closure;
    }
}
