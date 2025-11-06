package app.web;

import app.repo.ItemRepository;
import app.repo.OfferRepository;
import app.service.ItemService;
import spark.ModelAndView;
import spark.template.mustache.MustacheTemplateEngine;

import java.math.BigDecimal;
import java.util.HashMap;

import static spark.Spark.get;

public class ItemViewController {
    private final MustacheTemplateEngine engine = new MustacheTemplateEngine();
    private final ItemRepository repo = new ItemRepository();
    private final ItemService service = new ItemService(repo);
    private final OfferRepository offerRepo = new OfferRepository();

    public void register() {
        // avoid 404 on trailing slash
        get("/ui/items/", (req, res) -> { res.redirect("/ui/items"); return null; });

        get("/ui/items", (req, res) -> {
            String minStr = req.queryParams("min_price");
            String maxStr = req.queryParams("max_price");
            String currency = req.queryParams("currency");
            String q = req.queryParams("q");

            BigDecimal min = parseDecimal(minStr);
            BigDecimal max = parseDecimal(maxStr);

            boolean noFilters =
                    min == null &&
                            max == null &&
                            (currency == null || currency.isBlank()) &&
                            (q == null || q.isBlank());

            var items = noFilters
                    ? service.list()
                    : service.listFiltered(min, max, currency, q);

            var model = new HashMap<String, Object>();
            model.put("items", items);
            // echo current filters back into the form
            model.put("min_price", minStr == null ? "" : minStr);
            model.put("max_price", maxStr == null ? "" : maxStr);
            model.put("currency",  currency == null ? "" : currency);
            model.put("q",         q == null ? "" : q);

            return engine.render(new ModelAndView(model, "items.mustache"));
        });

        get("/ui/items/:id", (req, res) -> {
            var id = req.params(":id");
            var opt = repo.findById(id);
            if (opt.isEmpty()) { res.status(404); return "Not found"; }
            var i = opt.get();
            var m = new HashMap<String,Object>();
            m.put("id", i.getId());
            m.put("name", i.getName());
            m.put("description", i.getDescription());
            m.put("price", i.getPrice());
            m.put("currency", i.getCurrency());
            m.put("offers", offerRepo.findByItemId(id));
            return engine.render(new ModelAndView(m, "item_detail.mustache"));
        });
    }

    private static BigDecimal parseDecimal(String s) {
        try { return (s == null || s.isBlank()) ? null : new BigDecimal(s); }
        catch (NumberFormatException nfe) { return null; }
    }
}
