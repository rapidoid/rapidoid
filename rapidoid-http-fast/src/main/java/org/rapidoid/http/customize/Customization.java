package org.rapidoid.http.customize;

import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;

@Authors("Nikolche Mihajlovski")
@Since("5.1.0")
public class Customization {

	public static final String[] DEFAULT_STATIC_FILES_LOCATIONS = {"static", "rapidoid/static"};

	private volatile String[] staticFilesPath;

	private volatile ErrorHandler errorHandler;

	private volatile ViewRenderer viewRenderer;

	private volatile JsonResponseRenderer jsonResponseRenderer;

	private volatile JsonBodyParser jsonBodyParser;

	private volatile LoginProvider loginProvider;

	private volatile RolesProvider rolesProvider;

	public Customization() {
		reset();
	}

	public void reset() {
		staticFilesPath = DEFAULT_STATIC_FILES_LOCATIONS;
		errorHandler = new DefaultErrorHandler();
		viewRenderer = new DefaultViewRenderer();
		jsonResponseRenderer = new DefaultJsonResponseRenderer();
		jsonBodyParser = new DefaultJsonBodyParser();
		loginProvider = new DefaultLoginProvider();
		rolesProvider = new DefaultRolesProvider();
	}

	public void staticFilesPath(String... staticFilesPath) {
		this.staticFilesPath = staticFilesPath;
	}

	public String[] staticFilesPath() {
		return staticFilesPath;
	}

	public ErrorHandler errorHandler() {
		return errorHandler;
	}

	public void errorHandler(ErrorHandler errorHandler) {
		this.errorHandler = errorHandler;
	}

	public ViewRenderer viewRenderer() {
		return viewRenderer;
	}

	public void viewRenderer(ViewRenderer viewRenderer) {
		this.viewRenderer = viewRenderer;
	}

	public JsonResponseRenderer jsonResponseRenderer() {
		return jsonResponseRenderer;
	}

	public void jsonResponseRenderer(JsonResponseRenderer jsonResponseRenderer) {
		this.jsonResponseRenderer = jsonResponseRenderer;
	}

	public JsonBodyParser jsonBodyParser() {
		return jsonBodyParser;
	}

	public void jsonBodyParser(JsonBodyParser jsonBodyParser) {
		this.jsonBodyParser = jsonBodyParser;
	}

	public LoginProvider loginProvider() {
		return loginProvider;
	}

	public void loginProvider(LoginProvider loginProvider) {
		this.loginProvider = loginProvider;
	}

	public RolesProvider rolesProvider() {
		return rolesProvider;
	}

	public void rolesProvider(RolesProvider rolesProvider) {
		this.rolesProvider = rolesProvider;
	}

}
