# HuskyMaps
HuskyMaps is an interactive map of the Greater Seattle Area that allows the user
  to find the shortest route from one location to another. The user can specify
  start and end location by double-clicking on the map. It also contains a search
  bar equipped with autocomplete features. It case sensitive, so queries must
  begin with a capital letter.
  
HuskyMaps is based on a graph implementation and the graph contains enough data to
  span roughly Seattle and halfway across the 520 bridge. Each node of the graph
  contains a longitude and latitude, and represents a real, physical location.
  Some have names (e.g. "Safeway") and others are just spots on the road. 
  HuskyMaps will automatically find a valid node in the graph nearest to where
  the user double-clicked.
