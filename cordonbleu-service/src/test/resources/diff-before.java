package com.jobheroes.rest.integration;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.text.StrSubstitutor;
import org.hamcrest.Matcher;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMultipartHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.util.FileCopyUtils;
import org.springframework.util.ResourceUtils;
import org.springframework.web.context.WebApplicationContext;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.jobheroes.repository.CandidateRepository;
import com.jobheroes.repository.OccupationRepository;
import com.jobheroes.repository.Repository;
import com.jobheroes.repository.mongo.TestEnvironment;
import com.jobheroes.rest.Dependencies;
import com.jobheroes.rest.WebConfig;
import com.jobheroes.util.ResourceUtil;
import com.jobheroes.util.jackson.JsonEqualsMatcher;
import com.jobheroes.util.jackson.ObjectMapperFactory;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(classes = WebConfig.class)
public abstract class GatewayIntegrationTest implements Repository {
	private static final String FILE_ROOT = "integration/";

	private MockMvc mockMvc;
	private MockHttpSession session;

	@Autowired
	private WebApplicationContext wac;

	@ClassRule
	public static TestEnvironment testEnvironment = new TestEnvironment();

	@BeforeClass
	public static void setupClass() {
		Dependencies.setDependencies(new Dependencies(testEnvironment.getRepository()));
	}

	@Before
	public void setup() {
		mockMvc = MockMvcBuilders.webAppContextSetup(wac).build();
		session = new MockHttpSession();
	}

	protected MockHttpSession getSession() {
		return session;
	}

	protected MockMvc getMockMvc() {
		return mockMvc;
	}

	private String getContent(String requestFilename, Object... parameters) throws IOException {
		File requestFile = ResourceUtils.getFile("classpath:" + getFilePath(requestFilename));
		String fileContent = FileCopyUtils.copyToString(new FileReader(requestFile));
		Map<String, Object> parameterMap = new HashMap<>();
		for (int i = 0; i < parameters.length; i++) {
			parameterMap.put(Integer.toString(i), convertParameterToJson(parameters[i]));
		}
		return StrSubstitutor.replace(fileContent, parameterMap);
	}

	private String convertParameterToJson(Object parameter) throws JsonProcessingException {
		return ObjectMapperFactory.getObjectMapper().writeValueAsString(parameter);
	}

	protected Matcher<String> getContentMatcher(String requestFilename, Object... parameters) throws IOException {
		return JsonEqualsMatcher.jsonEquals(getContent(requestFilename, parameters));
	}

	protected ResultActions post(String url, String requestFilename, Object... parameters) throws Exception {
		return getMockMvc().perform(
				MockMvcRequestBuilders.post(url).content(getContent(requestFilename, parameters))
						.contentType(MediaType.APPLICATION_JSON).session(session));
	}

	protected ResultActions post(String url) throws Exception {
		return getMockMvc().perform(MockMvcRequestBuilders.post(url).contentType(MediaType.APPLICATION_JSON).session(session));
	}

	protected ResultActions fileUpload(String url, String fileParam, String... filePaths) throws Exception {
		MockMultipartHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.fileUpload(url);
		for (String filePath : filePaths) {
			InputStream fileInputStream = ResourceUtil.getInputStream(getFilePath(filePath));
			requestBuilder.file(new MockMultipartFile(fileParam, filePath, null, fileInputStream));
		}
		return getMockMvc().perform(requestBuilder.session(session));
	}

	protected String getFilePath(String filePath) {
		return FILE_ROOT + filePath;
	}

	protected ResultActions postWithParams(String url, Object... parameters) throws Exception {
		return getMockMvc().perform(
				MockMvcRequestBuilders.post(url, parameters).contentType(MediaType.APPLICATION_JSON).session(session));
	}

	@Override
	public CandidateRepository getCandidateRepository() {
		return testEnvironment.getRepository().getCandidateRepository();
	}

	@Override
	public OccupationRepository getOccupationRepository() {
		return testEnvironment.getRepository().getOccupationRepository();
	}

}
