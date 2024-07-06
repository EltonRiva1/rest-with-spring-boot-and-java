package br.com.elton.integrationtests.controller.withyaml.mapper;

import java.util.logging.Logger;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import io.restassured.mapper.ObjectMapper;
import io.restassured.mapper.ObjectMapperDeserializationContext;
import io.restassured.mapper.ObjectMapperSerializationContext;

public class YMLMapper implements ObjectMapper {
	private com.fasterxml.jackson.databind.ObjectMapper mapper;
	protected TypeFactory factory;
	private Logger logger = Logger.getLogger(YMLMapper.class.getName());

	public YMLMapper() {
		this.mapper = new com.fasterxml.jackson.databind.ObjectMapper(new YAMLFactory());
		this.mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
		this.factory = TypeFactory.defaultInstance();
	}

	@Override
	@SuppressWarnings("rawtypes")
	public Object deserialize(ObjectMapperDeserializationContext context) {
		// TODO Auto-generated method stub
		try {
			String dataToDeserialize = context.getDataToDeserialize().asString();
			Class type = (Class) context.getType();
			this.logger.info("Trying deserialize object of type" + type);
			return this.mapper.readValue(dataToDeserialize, this.factory.constructType(type));
		} catch (JsonMappingException e) {
			// TODO: handle exception
			this.logger.severe("Error deserializing object");
			e.printStackTrace();
		} catch (JsonProcessingException e) {
			this.logger.severe("Error deserializing object");
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public Object serialize(ObjectMapperSerializationContext context) {
		// TODO Auto-generated method stub
		try {
			return this.mapper.writeValueAsString(context.getObjectToSerialize());
		} catch (JsonProcessingException e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return null;
	}
}