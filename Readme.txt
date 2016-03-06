Stijn Buiteman
stijnbuiteman@gmail.com
<List>
This app features to-do lists. A main list is viewed in the MainActivity Items can be added by pressing the add button 
and removed by long-pressing an item in the list. A listview is used for the layout of the list. When an item is 
clicked a new list is openen in the EditListActivity. The first list is thus an oversight of all the lists made by the
user. List items in the EditListActivity do not open new lists when clicked. The list viewed is being stored in
shared preferences when the app is closed, on restarting the app, the user is taken back to that same list. All lists
are stored in separete textfiles which are destroyed when a list is deleted.