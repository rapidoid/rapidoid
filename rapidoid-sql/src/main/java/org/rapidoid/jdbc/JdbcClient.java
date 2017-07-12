package org.rapidoid.jdbc;

import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.cls.Cls;
import org.rapidoid.concurrent.Callback;
import org.rapidoid.concurrent.Callbacks;
import org.rapidoid.config.Conf;
import org.rapidoid.config.Config;
import org.rapidoid.datamodel.Results;
import org.rapidoid.datamodel.impl.ResultsImpl;
import org.rapidoid.group.AutoManageable;
import org.rapidoid.group.ManageableBean;
import org.rapidoid.io.Res;
import org.rapidoid.lambda.Mapper;
import org.rapidoid.lambda.Operation;
import org.rapidoid.log.Log;
import org.rapidoid.u.U;
import org.rapidoid.util.LazyInit;
import org.rapidoid.util.Msc;
import org.rapidoid.util.MscOpts;

import javax.sql.DataSource;
import java.sql.*;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

/*
 * #%L
 * rapidoid-sql
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

@Authors("Nikolche Mihajlovski")
@Since("3.0.0")
@ManageableBean(kind = "jdbc")
public class JdbcClient extends AutoManageable<JdbcClient> {

	private volatile boolean initialized;

	private volatile String username;
	private volatile String password;
	private volatile String driver;
	private volatile String url;

	private volatile boolean usePool = true;
	private volatile DataSource dataSource;

	private volatile ReadWriteMode mode = ReadWriteMode.READ_WRITE;

	private final Config config;

	private final LazyInit<JdbcWorkers> workers = new LazyInit<>(new Callable<JdbcWorkers>() {
		@Override
		public JdbcWorkers call() throws Exception {
			return new JdbcWorkers(JdbcClient.this);
		}
	});

	public JdbcClient(String name) {
		super(name);

		this.config = Conf.JDBC.defaultOrCustom(name);

		configure();
	}

	public void configure() {
		url(config.entry("url").str().getOrNull());
		username(config.entry("username").str().getOrNull());
		password(config.entry("password").str().getOrNull());
		driver(config.entry("driver").str().getOrNull());

		if (U.isEmpty(driver) && U.notEmpty(url)) {
			driver(inferDriverFromUrl(url));
		}
	}

	public static String inferDriverFromUrl(String url) {
		if (url.startsWith("jdbc:mysql:")) {
			return "com.mysql.jdbc.Driver";
		} else if (url.startsWith("jdbc:h2:")) {
			return "org.hibernate.dialect.H2Dialect";
		} else if (url.startsWith("jdbc:hsqldb:")) {
			return "org.hsqldb.jdbc.JDBCDriver";
		}

		return null;
	}

	public synchronized JdbcClient username(String username) {
		if (U.neq(this.username, username)) {
			this.username = username;
			this.initialized = false;
		}
		return this;
	}

	public synchronized JdbcClient password(String password) {
		if (U.neq(this.password, password)) {
			this.password = password;
			this.initialized = false;
		}
		return this;
	}

	public synchronized JdbcClient driver(String driver) {
		if (U.neq(this.driver, driver)) {
			this.driver = driver;
			this.initialized = false;
		}
		return this;
	}

	/**
	 * Use dataSource(...) instead.
	 */
	@Deprecated
	public synchronized JdbcClient pool(DataSource pool) {
		return dataSource(pool);
	}

	public synchronized JdbcClient dataSource(DataSource dataSource) {
		if (U.neq(this.dataSource, dataSource)) {
			this.dataSource = dataSource;
			this.usePool = dataSource != null;
			this.initialized = false;
		}
		return this;
	}

	public synchronized JdbcClient url(String url) {
		if (U.neq(this.url, url)) {
			this.url = url;
			this.initialized = false;
		}
		return this;
	}

	public synchronized JdbcClient usePool(boolean usePool) {
		if (U.neq(this.usePool, usePool)) {
			this.usePool = usePool;
			this.initialized = false;
		}
		return this;
	}

	public synchronized JdbcClient mode(ReadWriteMode mode) {
		if (U.neq(this.mode, mode)) {
			this.mode = mode;
			this.initialized = false;
		}
		return this;
	}

	/**
	 * Use <code>usePool(true)</code> instead.
	 */
	@Deprecated
	public JdbcClient pooled() {
		usePool(true);
		return this;
	}

	public JdbcClient mysql(String host, int port, String databaseName) {
		return driver("com.mysql.jdbc.Driver").url(U.frmt("jdbc:mysql://%s:%s/%s", host, port, databaseName));
	}

	public JdbcClient h2(String databaseName) {
		return driver("org.h2.Driver").url("jdbc:h2:mem:" + databaseName + ";DB_CLOSE_DELAY=-1").username("sa").password("");
	}

	public JdbcClient hsql(String databaseName) {
		return driver("org.hsqldb.jdbc.JDBCDriver").url("jdbc:hsqldb:mem:" + databaseName).username("sa").password("");
	}

	private void registerJDBCDriver() {
		if (driver == null && url != null) {
			driver = inferDriverFromUrl(url);
		}

		validateArgNotNull("driver", driver);

		try {
			Class.forName(driver);
		} catch (ClassNotFoundException e) {
			throw U.rte("Cannot find JDBC driver class: " + driver);
		}
	}

	private void validateArgNotNull(String argName, String argValue) {
		if (argValue == null) {
			throw U.rte("The JDBC parameter '" + argName + "' must be configured!");
		}
	}

	private synchronized void ensureIsInitialized() {
		if (!initialized) {
			validate();
			registerJDBCDriver();

			if (this.dataSource == null) {
				this.dataSource = this.usePool ? createPool() : null;
			}

			String ds = dataSource != null ? Cls.of(dataSource).getSimpleName() : null;
			Log.info("Initialized JDBC API", "!url", url, "!driver", driver, "!username", username, "!password", maskedPassword(), "!dataSource", ds);

			initialized = true;
		}
	}

	private String maskedPassword() {
		return U.isEmpty(password) ? "<empty>" : "<specified>";
	}

	private DataSource createPool() {

		if (MscOpts.hasC3P0()) {
			Log.info("Initializing JDBC connection pool with C3P0", "!url", url, "!driver", driver, "!username", username, "!password", maskedPassword());
			return C3P0Factory.createDataSourceFor(this);
		}

		if (MscOpts.hasHikari()) {
			Log.info("Initializing JDBC connection pool with Hikari", "!url", url, "!driver", driver, "!username", username, "!password", maskedPassword());
			return HikariFactory.createDataSourceFor(this);
		}

		throw U.rte("Cannot create JDBC connection pool, couldn't find Hikari nor C3P0!");
	}

	private void validate() {
		U.must(U.notEmpty(username != null), "The database username must be specified!");
		U.must(U.notEmpty(password != null), "The database password must be specified!");
		U.must(U.notEmpty(url != null), "The database connection URL must be specified!");
		U.must(U.notEmpty(driver != null), "The database driver must be specified!");
	}

	public Connection getConnection() {
		ensureIsInitialized();

		return provideConnection();
	}

	private static void close(Connection conn) {
		try {
			if (conn != null) conn.close();

		} catch (SQLException e) {
			throw U.rte("Error occurred while closing the connection!", e);
		}
	}

	private static void close(PreparedStatement stmt) {
		try {
			if (stmt != null) stmt.close();

		} catch (SQLException e) {
			throw U.rte("Error occurred while closing the statement!", e);
		}
	}

	private static void close(ResultSet rs) {
		try {
			if (rs != null) rs.close();

		} catch (SQLException e) {
			throw U.rte("Error occurred while closing the ResultSet!", e);
		}
	}

	public int execute(String sql, Object... args) {
		return doExecute(sql, null, args);
	}

	public int execute(String sql, Map<String, ?> namedArgs) {
		return doExecute(sql, namedArgs, null);
	}

	private int doExecute(String sql, Map<String, ?> namedArgs, Object[] args) {
		ensureIsInitialized();

		sql = toSql(sql);

		Log.debug("SQL", "sql", sql, "args", args);

		try (Connection conn = provideConnection();
		     PreparedStatement stmt = JDBC.prepare(conn, sql, namedArgs, args)) {

			String q = sql.trim().toUpperCase();

			if (q.startsWith("INSERT ")
				|| q.startsWith("UPDATE ")
				|| q.startsWith("DELETE ")) {

				return stmt.executeUpdate();

			} else {
				return stmt.execute() ? 1 : 0;
			}

		} catch (SQLException e) {
			throw U.rte(e);
		}
	}

	public int tryToExecute(String sql, Object... args) {
		return doTryToExecute(sql, null, args);
	}

	public int tryToExecute(String sql, Map<String, ?> namedArgs) {
		return doTryToExecute(sql, namedArgs, null);
	}

	private int doTryToExecute(String sql, Map<String, ?> namedArgs, Object[] args) {
		try {
			return doExecute(sql, namedArgs, args);

		} catch (Exception e) {
			// ignore the exception
			Log.warn("Ignoring JDBC error", "error", Msc.errorMsg(e));
		}

		return 0;
	}

	public <T> Results<T> query(Class<T> resultType, String sql, Object... args) {
		return doQuery(resultType, null, sql, null, args);
	}

	public <T> Results<T> query(Class<T> resultType, String sql, Map<String, ?> namedArgs) {
		return doQuery(resultType, null, sql, namedArgs, null);
	}

	public <T> Results<T> query(Mapper<ResultSet, T> resultMapper, String sql, Object... args) {
		return doQuery(null, resultMapper, sql, null, args);
	}

	public <T> Results<T> query(Mapper<ResultSet, T> resultMapper, String sql, Map<String, ?> namedArgs) {
		return doQuery(null, resultMapper, sql, namedArgs, null);
	}

	private <T> Results<T> doQuery(Class<T> resultType, Mapper<ResultSet, T> resultMapper, String sql, Map<String, ?> namedArgs, Object[] args) {
		sql = toSql(sql);
		JdbcData<T> data = new JdbcData<>(this, resultType, resultMapper, sql, namedArgs, args);
		return new ResultsImpl<>(data);
	}

	<T> List<T> runQuery(Class<T> resultType, Mapper<ResultSet, T> resultMapper,
	                     String sql, Map<String, ?> namedArgs, Object[] args,
	                     long skip, long limit) {

		ensureIsInitialized();

		if (limit == -1) {
			// unlimited
			limit = Integer.MAX_VALUE;
		}

		U.must(skip >= 0);
		U.must(limit >= 0);

		// replace the paging parameters
		sql = sql.replace("$skip", skip + "");
		sql = sql.replace("$limit", limit + "");

		try (Connection conn = provideConnection();
		     PreparedStatement stmt = JDBC.prepare(conn, sql, namedArgs, args);
		     ResultSet rs = stmt.executeQuery()) {

			if (resultMapper != null) {
				return JDBC.rows(resultMapper, rs);

			} else {
				if (resultType.equals(Map.class)) {
					return U.cast(JDBC.rows(rs));
				} else {
					return JDBC.rows(resultType, rs);
				}
			}

		} catch (Exception e) {
			throw U.rte(e);
		}
	}

	long getQueryCount(String sql, Map<String, ?> namedArgs, Object[] args) {
		// FIXME find a better way
		return -1; // unknown
	}

	private static String toSql(String sql) {
		if (sql.endsWith(".sql")) {
			sql = Res.from(sql).mustExist().getContent();
		}

		return sql;
	}

	public Results<Map<String, Object>> query(String sql, Object... args) {
		return U.cast(query(Map.class, sql, args));
	}

	public Results<Map<String, Object>> query(String sql, Map<String, ?> namedArgs) {
		return U.cast(query(Map.class, sql, namedArgs));
	}

	private Connection provideConnection() {
		try {
			if (dataSource != null) {

				Connection conn = dataSource.getConnection();
				U.notNull(conn, "JDBC connection");
				return conn;

			} else {
				U.must(!usePool, "Expecting connection pool, but the data source is null!");
				return getConnectionFromDriver();
			}

		} catch (SQLException e) {
			throw U.rte("Cannot create JDBC connection!", e);
		}
	}

	private Connection getConnectionFromDriver() throws SQLException {
		if (username != null) {
			String pass = U.safe(password);
			return DriverManager.getConnection(url, username, pass);

		} else {
			return DriverManager.getConnection(url);
		}
	}

	public void release(Connection connection) {
		try {
			connection.close();
		} catch (SQLException e) {
			Log.error("Error while releasing a JDBC connection!", e);
		}
	}

	public String username() {
		return username;
	}

	public String password() {
		return password;
	}

	public String driver() {
		return driver;
	}

	public String url() {
		return url;
	}

	/**
	 * Use dataSource() instead.
	 */
	@Deprecated
	public DataSource pool() {
		return dataSource();
	}

	public DataSource dataSource() {
		return dataSource;
	}

	public boolean usePool() {
		return usePool;
	}

	public ReadWriteMode mode() {
		return mode;
	}

	public JdbcClient init() {
		ensureIsInitialized();
		return this;
	}

	@Override
	public String toString() {
		return "JdbcClient{" +
			"initialized=" + initialized +
			", username='" + username + '\'' +
			", password='" + "*" + '\'' +
			", driver='" + driver + '\'' +
			", url='" + url + '\'' +
			", usePool=" + usePool +
			", pool=" + dataSource +
			", mode=" + mode +
			'}';
	}

	public void execute(Operation<Connection> operation) {
		workers.get().execute(operation);
	}

	public void execute(final Callback<Void> callback, final Operation<Connection> operation) {
		execute(new Operation<Connection>() {

			@Override
			public void execute(Connection conn) {

				try {
					operation.execute(conn);

				} catch (Throwable e) {
					Callbacks.done(callback, null, e);
					return;
				}

				Callbacks.done(callback, null, null);
			}
		});
	}

	public <T> void execute(final Mapper<ResultSet, T> resultMapper, final Callback<List<T>> callback, final String sql, final Object... args) {
		execute(new Operation<Connection>() {

			@Override
			public void execute(Connection conn) throws Exception {
				List<T> results = U.list();

				try (PreparedStatement stmt = JdbcUtil.prepare(conn, sql, null, args)) {

					ResultSet rs = stmt.executeQuery();
					while (rs.next()) {
						results.add(resultMapper.map(rs));
					}

				} catch (Throwable e) {
					Callbacks.done(callback, null, e);
					return;
				}

				Callbacks.done(callback, results, null);
			}

		});
	}

}
