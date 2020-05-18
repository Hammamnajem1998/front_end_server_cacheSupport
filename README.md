
Front End Server ... 
we made this server to send RES requests to catalog and order servers .

 
 
what you want to change ? 

there is three lines to change to make the server work on your machine. 

line 130 make a connection with Order_server to make buy request, so you have to change the previous order_server URL with 
your new order_server URL .

lines 40 and 94 make a connection with catalog_server to make lookup/search requests, so you have to change the previous catalog_server URL with your new catalog_server URL .
