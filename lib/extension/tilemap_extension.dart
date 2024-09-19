import 'package:vietmap_map/features/map_screen/components/select_map_tiles_modal.dart';

extension TileMapExtension on MapTiles {
  String getMapTiles(String apiKey) {
    assert(apiKey.isNotEmpty);
    switch (this) {
      case MapTiles.vietmapVector:
        return "https://maps.vietmap.vn/api/maps/light/styles.json?apikey=$apiKey";
      case MapTiles.vietmapRaster:
        return "https://maps.vietmap.vn/api/maps/raster/styles.json?apikey=$apiKey";
      case MapTiles.google:
        return "https://maps.vietmap.vn/api/maps/google/styles.json?apikey=$apiKey";
      case MapTiles.googleSatellite:
        return "https://maps.vietmap.vn/api/maps/google-satellite/styles.json?apikey=$apiKey";
    }
  }
}
