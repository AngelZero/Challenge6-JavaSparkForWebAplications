$(function() {
  $('#offer-toggle').on('click', function (e) {
    e.preventDefault();
    var $col = $('#offer-form-col');
    if ($col.is(':visible')) {
      $col.hide();
    } else {
      $col.show();
      $('#offer-form').show();
    }
  });

  $('#offer-form').on('submit', function (event) {
    event.preventDefault();

    var itemId = $('#current-item-id').val();
    var name   = $('#name-input').val();
    var email  = $('#email-input').val();
    var amount = $('#amount-input').val();

    if (!name || !email || !amount) {
      alert('Please fill out all fields!');
      return;
    }

    var amountNum = Number(amount);
    var payload = {
      id: itemId,
      name: name,
      email: email,
      amount: Number.isFinite(amountNum) ? amountNum.toFixed(2) : amount
    };

    $.ajax({
      url: '/api/offer',
      method: 'POST',
      data: payload,
      success: function () {
        $('#offer-form')[0].reset();
        var $ok = $('#offer-success');
        if ($ok.length) {
          $ok.text('Offer submitted!').show();
          setTimeout(function(){ $ok.fadeOut(200); }, 1800);
        } else {
          alert('Your offer has been submitted!');
        }
      },
      error: function (jqXHR) {
        var msg = jqXHR && jqXHR.responseText ? jqXHR.responseText : 'Error submitting offer';
        var $err = $('#offer-error');
        if ($err.length) {
          $err.text(msg).show();
          setTimeout(function(){ $err.fadeOut(200); }, 2500);
        } else {
          alert(msg);
        }
      }
    });
  });
});
