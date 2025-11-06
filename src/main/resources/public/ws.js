(function() {
  var proto  = (location.protocol === 'https:') ? 'wss:' : 'ws:';
  var socket = new WebSocket(proto + '//' + location.host + '/ws');

  var FADE_IN_MS  = 350, HOLD_MS = 2000, FADE_OUT_MS = 500;
  var RED = '#d00000', BLACK = '#000000';

  function setTextAndPulse(el, newText, isPrice) {
    if (!el) return;
    var current = (el.textContent || '').trim();
    var text = String(newText ?? '');
    if (isPrice) {
      var n = Number(text);
      text = Number.isFinite(n) ? n.toFixed(2) : text;
    }
    if (current === text) {
      return pulse(el);
    }
    el.textContent = text;
    pulse(el);
  }

  function pulse(el) {
    if (!el) return;
    if (el._anim) { clearTimeout(el._anim.toBlack); clearTimeout(el._anim.toRedDone); }
    el._anim = {};
    el.style.transition = 'color ' + FADE_IN_MS + 'ms ease';
    void el.offsetWidth;           // reflow
    el.style.color = RED;          // to red
    el._anim.toRedDone = setTimeout(function() {
      el.style.transition = 'color ' + FADE_OUT_MS + 'ms ease';
      void el.offsetWidth;
      el.style.color = BLACK;      // back to black
    }, FADE_IN_MS + HOLD_MS);
  }

  function currentFilters() {
    var p = new URLSearchParams(location.search);
    return {
      min: p.get('min_price'),
      max: p.get('max_price'),
      cur: p.get('currency'),
      q:   p.get('q')
    };
  }

  function itemMatchesFilters(item, f) {
    // Empty filters → always show
    if (!f) return true;
    var price = Number(item.price);
    if (f.min && !Number.isNaN(Number(f.min)) && !(price >= Number(f.min))) return false;
    if (f.max && !Number.isNaN(Number(f.max)) && !(price <= Number(f.max))) return false;
    if (f.cur && f.cur.trim() && String(item.currency || '').toUpperCase() !== f.cur.trim().toUpperCase()) return false;
    if (f.q && f.q.trim() && String(item.name || '').toUpperCase().indexOf(f.q.trim().toUpperCase()) === -1) return false;
    return true;
  }

  function buildRowInnerHTML(item) {
    var id = item.id, name = item.name, price = item.price, currency = item.currency;
    var priceTxt = Number.isFinite(Number(price)) ? Number(price).toFixed(2) : String(price);
    return '' +
      '<td>' +
        '<a class="item-name-link" href="/ui/items/' + id + '">' +
          '<span id="name-' + id + '">' + escapeHtml(name) + '</span>' +
        '</a>' +
      '</td>' +
      '<td><span id="price-' + id + '">' + priceTxt + '</span></td>' +
      '<td><span id="currency-' + id + '">' + escapeHtml(currency) + '</span></td>' +
      '<td><a class="btn" href="/ui/items/' + id + '">View details</a></td>';
  }

  function upsertRow(item) {
    var tbody = document.getElementById('item-list');
    if (!tbody) return;

    var row = document.getElementById('row-' + item.id);
    if (row) {
      setTextAndPulse(document.getElementById('name-' + item.id), item.name, false);
      setTextAndPulse(document.getElementById('price-' + item.id), item.price, true);
      setTextAndPulse(document.getElementById('currency-' + item.id), item.currency, false);
      return;
    }

    var f = currentFilters();
    if (!itemMatchesFilters(item, f)) return;

    row = document.createElement('tr');
    row.id = 'row-' + item.id;
    row.className = 'list-item';
    row.setAttribute('data-id', item.id);
    row.innerHTML = buildRowInnerHTML(item);
    tbody.appendChild(row);

    pulse(document.getElementById('name-' + item.id));
    pulse(document.getElementById('price-' + item.id));
    pulse(document.getElementById('currency-' + item.id));
  }

  function removeRow(id) {
    var row = document.getElementById('row-' + id);
    if (row) row.remove();

    if (location.pathname === '/ui/items/' + id) {
      setTimeout(function(){ window.location.href = '/ui/items'; }, 500);
    }
  }


  function appendOfferRow(name, email, amount) {
    var tbody = document.getElementById('offer-tbody');
    if (!tbody) return;

    var emptyCell = tbody.querySelector('tr td[colspan]');
    if (emptyCell && emptyCell.parentElement) {
      emptyCell.parentElement.remove();
    }

    var tr = document.createElement('tr');
    var amt = Number.isFinite(Number(amount)) ? Number(amount).toFixed(2) : String(amount);
    tr.innerHTML =
      '<td><span class="offer-name"></span></td>' +
      '<td><span class="offer-email"></span></td>' +
      '<td><span class="offer-amount"></span></td>';

    tbody.insertBefore(tr, tbody.firstChild);

    var n = tr.querySelector('.offer-name');
    var e = tr.querySelector('.offer-email');
    var a = tr.querySelector('.offer-amount');
    n.textContent = name || '';
    e.textContent = email || '';
    a.textContent = amt;

    pulse(n); pulse(e); pulse(a);
  }

  function escapeHtml(s) {
    return String(s ?? '')
      .replace(/&/g,'&amp;').replace(/</g,'&lt;').replace(/>/g,'&gt;')
      .replace(/"/g,'&quot;').replace(/'/g,'&#39;');
  }

  socket.onmessage = function(ev) {
    try {
      var msg = JSON.parse(ev.data);

      if (msg.type === 'updatePrice') {
        setTextAndPulse(document.getElementById('price-' + msg.itemId), msg.price, true);
        return;
      }

      if (msg.type === 'updateItem') {
        var id = msg.id;
        setTextAndPulse(document.getElementById('name-' + id), msg.name, false);
        setTextAndPulse(document.getElementById('price-' + id), msg.price, true);
        setTextAndPulse(document.getElementById('currency-' + id), msg.currency, false);
        setTextAndPulse(document.getElementById('description-' + id), msg.description, false); // detail page if visible
        return;
      }

      if (msg.type === 'itemCreated') {
        upsertRow(msg);
        setTextAndPulse(document.getElementById('name-' + msg.id), msg.name, false);
        setTextAndPulse(document.getElementById('price-' + msg.id), msg.price, true);
        setTextAndPulse(document.getElementById('currency-' + msg.id), msg.currency, false);
        setTextAndPulse(document.getElementById('description-' + msg.id), msg.description, false);
        return;
      }

      if (msg.type === 'itemDeleted') {
        removeRow(msg.id);
        return;
      }

      if (msg.type === 'newOffer') {
        var cur = document.getElementById('current-item-id');
        if (!cur) return;
        if (cur.value !== String(msg.itemId)) return;
        appendOfferRow(msg.name, msg.email, msg.amount);
        return;
      }
    } catch (e) {
    }
  };
})();
