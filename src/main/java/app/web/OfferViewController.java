package app.web;

import app.repo.OfferRepository;
import spark.ModelAndView;
import spark.template.mustache.MustacheTemplateEngine;

import java.util.HashMap;

import static spark.Spark.get;

public class OfferViewController {
    private final MustacheTemplateEngine engine = new MustacheTemplateEngine();
    private final OfferRepository repo = new OfferRepository();

    public void register() {
        get("/ui/offers", (req, res) -> {
            var model = new HashMap<String,Object>();
            model.put("offers", repo.findAll());
            return engine.render(new ModelAndView(model, "offers.mustache"));
        });
    }
}
