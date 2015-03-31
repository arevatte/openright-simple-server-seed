package net.openright.infrastructure.rest;

import java.io.IOException;
import java.io.Writer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;

public class JsonGetController implements GetController {

	private JsonController jsonController;

	public JsonGetController(JsonController jsonController) {
		this.jsonController = jsonController;
	}

    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        JSONObject response = jsonController.getJSON(req);
        if (response == null) {
            resp.setStatus(204);
            return;
        }

        resp.setContentType("application/json");
        try (Writer writer = resp.getWriter()) {
            writer.write(response.toString());
        }
    }

}
