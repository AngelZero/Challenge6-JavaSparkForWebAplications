var itemListTable = document.querySelector('#item-list-table');

itemListTable.addEventListener('click', function(event) {
    if (event.target.matches('a.item-name-link')) {
        event.preventDefault();
        var itemId = event.target.getAttribute('href').split('/')[2];
        window.location.href = '/ui/items/' + itemId;
    }
});
