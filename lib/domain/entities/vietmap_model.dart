// ignore_for_file: public_member_api_docs, sort_constructors_first
import 'package:hive_flutter/hive_flutter.dart';

class VietmapModel {
  @HiveField(1)
  double? lat;

  @HiveField(2)
  double? lng;

  @HiveField(3)
  String? address;

  @HiveField(4)
  String? name;

  @HiveField(5)
  String? display;
  VietmapModel({this.lat, this.lng, this.address, this.name, this.display});

  String? getAddress() {
    // get any address if not empty or null
    if (address != null && address!.isNotEmpty) {
      return address!;
    }
    if (display != null && display!.isNotEmpty) {
      return display!;
    }
    return name;
  }

  @override
  String toString() {
    return 'VietmapModel(lat: $lat, lng: $lng, address: $address, name: $name, display: $display)';
  }
}
