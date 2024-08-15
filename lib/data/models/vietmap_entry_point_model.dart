import 'package:hive_flutter/hive_flutter.dart';

class VietmapEntryPointModel {
  @HiveField(6)
  final String? refId;
  @HiveField(7)
  final String? display;

  VietmapEntryPointModel({this.refId, this.display});
  factory VietmapEntryPointModel.fromJson(Map<String, dynamic> json) {
    return VietmapEntryPointModel(
      refId: json['ref_id'],
      display: json['name'],
    );
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = <String, dynamic>{};
    data['ref_id'] = refId;
    data['name'] = display;
    return data;
  }
}
