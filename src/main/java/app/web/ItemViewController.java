package app.web;

import app.repo.ItemRepository;
import com.google.gson.Gson;
import spark.ModelAndView;
import spark.template.mustache.MustacheTemplateEngine;

import java.util.HashMap;
import java.util.stream.Collectors;

import static spark.Spark.get;

public class ItemViewController {
    private final MustacheTemplateEngine engine = new MustacheTemplateEngine();
    private final ItemRepository repo = new ItemRepository();

    public void register() {
        get("/ui/items", (req, res) -> {
            var items = repo.findAll().stream().map(i -> {
                var m = new HashMap<String,Object>();
                m.put("id", i.getId());
                m.put("name", i.getName());
                m.put("price", i.getPrice());
                m.put("currency", i.getCurrency());
                return m;
            }).collect(Collectors.toList());
            var model = new HashMap<String,Object>();
            model.put("items", items);
            return engine.render(new ModelAndView(model, "items.mustache"));
        });

        get("/ui/items/:id", (req, res) -> {
            var id = req.params(":id");
            var opt = repo.findById(id);
            if (opt.isEmpty()) {
                res.status(404);
                return engine.render(new ModelAndView(new HashMap<>(), "items.mustache"));
            }
            var i = opt.get();
            var model = new HashMap<String,Object>();
            model.put("id", i.getId());
            model.put("name", i.getName());
            model.put("description", i.getDescription());
            model.put("price", i.getPrice());
            model.put("currency", i.getCurrency());
            return engine.render(new ModelAndView(model, "item_detail.mustache"));
        });
    }
}
