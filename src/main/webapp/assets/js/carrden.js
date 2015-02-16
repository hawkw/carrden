/**
 * Created by hawk on 2/7/15.
 */
function updateInventory() {
    $.ajax({
        type: 'get',
        url: '/inventory',
        data: '{}',
        success: function (data, status) {
            g_inventory = {}
            for (var i in data) {
                g_inventory[data[i]['name'].replace(' ', '_')] = {
                    amount: data[i]['amount'],
                    price:  data[i]['price']
                };
                //todo: sane way of attaching stuff to this callback
            }
            console.log("[Info] Got current inventory: " + JSON.stringify(g_inventory))
            $("#current-inventory").load(location.href + " #current-inventory");
        },
        error: function (result) {
            console.log("[Error] Something went wrong getting inventory: " + result)
        }
    });
}