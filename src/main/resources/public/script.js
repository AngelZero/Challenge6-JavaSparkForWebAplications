$(document).ready(function() {
  function normalizeItem(x) {
    const id        = x.id ?? x.Id ?? x.itemId ?? x.ID ?? "";
    const name      = x.name ?? x.Name ?? "";
    const priceRaw  = x.price ?? x.Price ?? "";
    const currency  = x.currency ?? x.Currency ?? "";

    const priceNum = Number(priceRaw);
    const priceText = Number.isFinite(priceNum) ? priceNum.toFixed(2) : String(priceRaw ?? "");

    return { id, name, price: priceText, currency };
  }

  function fetchAndRender(params) {
    $.get("/items", params, function(data) {
      if (!Array.isArray(data)) { console.warn("Unexpected /items payload:", data); data = []; }
      const items = data.map(normalizeItem);
      renderRows(items);
    }, "json");
  }

  function renderRows(items) {
    const $tbody = $("#item-list");
    const tplEl = $("#row-template");
    if (tplEl.length) {
      const tpl = tplEl.html();
      let html = "";
      items.forEach(item => { html += Mustache.render(tpl, item); });
      $tbody.html(html);
    } else {
      let html = "";
      items.forEach(item => {
        html += '<tr class="list-item" data-id="'+item.id+'">'
              +   '<td><a class="item-name-link" href="/ui/items/'+item.id+'">'+item.name+'</a></td>'
              +   '<td><span id="price-'+item.id+'">'+item.price+'</span></td>'
              +   '<td>'+item.currency+'</td>'
              +   '<td><a class="btn" href="/ui/items/'+item.id+'">View details</a></td>'
              + '</tr>';
      });
      $tbody.html(html);
    }
  }

  fetchAndRender({});

  const table = document.getElementById('item-list-table');
  if (table) {
    table.addEventListener('click', function(event) {
    });
  }

  $("#filter-form").submit(function(e) {
    e.preventDefault();
    const p = {
      min_price: $("#min-price").val(),
      max_price: $("#max-price").val(),
      currency:  $("#currency").val(),
      q:         $("#q").val()
    };
    const allEmpty = Object.values(p).every(v => !v || String(v).trim() === "");
    if (allEmpty) { fetchAndRender({}); return; }
    Object.keys(p).forEach(k => { if (!p[k] || String(p[k]).trim() === "") delete p[k]; });
    fetchAndRender(p);
  });
});
