package org.rapidoid.benchmark.common;

/*
 * #%L
 * rapidoid-benchmark
 * %%
 * Copyright (C) 2014 - 2017 Nikolche Mihajlovski and contributors
 * %%
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
 * #L%
 */

public class Helper {

	// most of this configuration was copied from the other projects
	public static final String MYSQL_CONFIG = "useSSL=false&jdbcCompliantTruncation=false" +
		"&elideSetAutoCommits=true&useLocalSessionState=true&cachePrepStmts=true&cacheCallableStmts=true" +
		"&alwaysSendSetIsolation=false&prepStmtCacheSize=4096&cacheServerConfiguration=true&prepStmtCacheSqlLimit=2048" +
		"&zeroDateTimeBehavior=convertToNull&traceProtocol=false&useServerPrepStmts=true&enableQueryTimeouts=false" +
		"&useUnbufferedIO=false&useReadAheadInput=false&maintainTimeStats=false&cacheRSMetadata=true";

	// most of this configuration was copied from the other projects
	public static final String POSTGRES_CONFIG = "jdbcCompliantTruncation=false" +
		"&elideSetAutoCommits=true&useLocalSessionState=true&cachePrepStmts=true&cacheCallableStmts=true" +
		"&alwaysSendSetIsolation=false&prepStmtCacheSize=4096&cacheServerConfiguration=true&prepStmtCacheSqlLimit=2048" +
		"&zeroDateTimeBehavior=convertToNull&traceProtocol=false&useServerPrepStmts=true&enableQueryTimeouts=false" +
		"&useUnbufferedIO=false&useReadAheadInput=false&maintainTimeStats=false&cacheRSMetadata=true";

}
