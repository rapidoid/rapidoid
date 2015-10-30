package org.rapidoidx.db.impl.inmem;

/*
 * #%L
 * rapidoid-x-db-inmem
 * %%
 * Copyright (C) 2014 - 2015 Nikolche Mihajlovski and contributors
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * #L%
 */

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.beany.Beany;
import org.rapidoid.beany.Prop;
import org.rapidoid.log.Log;
import org.rapidoid.u.U;
import org.rapidoidx.db.Database;
import org.rapidoidx.db.DbList;
import org.rapidoidx.db.DbRef;
import org.rapidoidx.db.DbSet;
import org.rapidoidx.db.impl.DbHelper;
import org.rapidoidx.db.impl.DbRelationInternals;
import org.rapidoidx.inmem.EntitySerializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.node.ObjectNode;

@Authors("Nikolche Mihajlovski")
@Since("3.0.0")
public class JacksonEntitySerializer implements EntitySerializer {

	private final Database db;

	private final ObjectMapper mapper = new ObjectMapper();

	public JacksonEntitySerializer(Database db) {
		this.db = db;
		initDbMapper();
	}

	@SuppressWarnings("rawtypes")
	private void initDbMapper() {
		SimpleModule dbModule = new SimpleModule("DbModule", new Version(1, 0, 0, null, null, null));

		dbModule.addDeserializer(DbList.class, new JsonDeserializer<DbList>() {
			@SuppressWarnings("unchecked")
			@Override
			public DbList deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException,
					JsonProcessingException {
				Map<String, Object> data = jp.readValueAs(Map.class);
				String relation = (String) data.get("relation");
				List<? extends Number> ids = (List<Number>) data.get("ids");
				return new InMemDbList(db, null, relation, ids);
			}
		});

		dbModule.addDeserializer(DbSet.class, new JsonDeserializer<DbSet>() {
			@SuppressWarnings("unchecked")
			@Override
			public DbSet deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException,
					JsonProcessingException {
				Map<String, Object> data = jp.readValueAs(Map.class);
				String relation = (String) data.get("relation");
				List<? extends Number> ids = (List<Number>) data.get("ids");
				return new InMemDbSet(db, null, relation, ids);
			}
		});

		dbModule.addDeserializer(DbRef.class, new JsonDeserializer<DbRef>() {
			@SuppressWarnings("unchecked")
			@Override
			public DbRef deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException,
					JsonProcessingException {
				Map<String, Object> data = jp.readValueAs(Map.class);
				String relation = (String) data.get("relation");
				List<? extends Number> ids = (List<Number>) data.get("ids");
				U.must(ids.size() <= 1, "Expected 0 or 1 IDs!");
				long id = !ids.isEmpty() ? ids.get(0).longValue() : -1;
				return new InMemDbRef(db, null, relation, id);
			}
		});

		mapper.registerModule(dbModule);

		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
	}

	public String stringify(Object value) {
		try {
			return mapper.writeValueAsString(value);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * @param extras
	 *            extra JSON attributes in format (key1, value1, key2, value2...)
	 */
	private byte[] stringifyWithExtras(Object value, Object... extras) {
		if (extras.length % 2 != 0) {
			throw new IllegalArgumentException(
					"Expected even number of extras (key1, value1, key2, value2...), but found: " + extras.length);
		}

		try {
			JsonNode node = mapper.valueToTree(value);

			if (!(node instanceof ObjectNode)) {
				throw new RuntimeException("Cannot add extra attributes on a non-object value: " + value);
			}

			ObjectNode obj = (ObjectNode) node;

			int extrasN = extras.length / 2;
			for (int i = 0; i < extrasN; i++) {
				Object key = extras[2 * i];
				if (key instanceof String) {
					obj.put((String) key, String.valueOf(extras[2 * i + 1]));
				} else {
					throw new RuntimeException("Expected extra key of type String, but found: " + key);
				}
			}

			return mapper.writeValueAsBytes(node);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@SuppressWarnings("unchecked")
	public <T> void parse(byte[] bytes, T destination) {
		try {
			Map<String, Object> map = mapper.readValue(bytes, Map.class);
			Beany.update(destination, map, false);
		} catch (Exception e) {
			Log.error("Cannot parse JSON!", e);
			throw new RuntimeException(e);
		}
	}

	@Override
	public byte[] serialize(Object entity) {
		Class<?> entityType = db.schema().getEntityTypeFor(entity.getClass());
		return stringifyWithExtras(Beany.serialize(entity), "_class", entityType.getCanonicalName());
	}

	@Override
	public <T> void deserialize(byte[] bytes, T destination) {
		parse(bytes, destination);

		for (Prop prop : Beany.propertiesOf(destination).select(DbHelper.DB_REL_PROPS)) {
			DbRelationInternals rel = prop.get(destination);
			U.notNull(rel, prop.getName());
			rel.setHolder(destination);
		}
	}

}
