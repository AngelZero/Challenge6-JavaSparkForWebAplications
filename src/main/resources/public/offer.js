$(document).ready(function() {
    $(".offer-btn").click(function() {
      $("#offer-form").toggle();
    });
  
    $("#offer-form").submit(function(event) {
      event.preventDefault();

      let itemId = $("#item-id-input").val();
      let name = $("#name-input").val();
      let email = $("#email-input").val();
      let amount = parseFloat($("#amount-input").val()).toFixed(2);
  
      if (!name || !email || !amount) {
        alert("Please fill out all fields!");
        return;
      }
  
      $.ajax({
        url: "/api/offer",
        method: "POST",
        data: {
          id: itemId,
          name: name,
          email: email,
          amount: amount,
        },
        success: function(response) {
          alert("Your offer has been submitted!");
          $("#offer-form").trigger("reset");
          $("#offer-form").hide();
        },
        error: function(jqXHR, textStatus, errorThrown) {
          alert("Error submitting offer: " + jqXHR.responseText);
        },
      });
    });
  });
  