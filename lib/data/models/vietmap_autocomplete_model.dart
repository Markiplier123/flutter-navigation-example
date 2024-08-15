import 'package:hive_flutter/adapters.dart';
import 'package:vietmap_map/data/models/vietmap_entry_point_model.dart';
import 'package:vietmap_map/domain/entities/vietmap_model.dart';

import '../../constants/constants.dart';
part 'vietmap_autocomplete_model.g.dart';

@HiveType(typeId: vietmapAutocompleteModelAdapterHiveTypeId)
class VietmapAutocompleteModel extends VietmapModel {
  @HiveField(0)
  String? refId;
  @HiveField(9)
  List<VietmapEntryPointModel?>? entryPoint;

  VietmapAutocompleteModel({
    this.refId,
    this.entryPoint,
    super.address,
    super.name,
    super.display,
  });

  VietmapAutocompleteModel.fromJson(Map<String, dynamic> json) {
    refId = json['ref_id'];
    address = json['address'];
    name = json['name'];
    display = json['display'];
    entryPoint = json['entry_points'] != null
        ? List<VietmapEntryPointModel?>.from(
            json['entry_points'].map((v) => VietmapEntryPointModel.fromJson(v)))
        : null;
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = <String, dynamic>{};
    data['ref_id'] = refId;
    data['address'] = address;
    data['name'] = name;
    data['display'] = display;
    data['entry_points'] = entryPoint?.map((v) => v!.toJson()).toList();
    return data;
  }

  @override
  String toString() {
    return 'VietmapAutocompleteModel{refId: $refId, address: $address, name: $name, display: $display}';
  }

  @override
  bool operator ==(Object other) =>
      identical(this, other) ||
      other is VietmapAutocompleteModel &&
          runtimeType == other.runtimeType &&
          refId == other.refId &&
          address == other.address &&
          name == other.name &&
          display == other.display;

  @override
  int get hashCode =>
      refId.hashCode ^ address.hashCode ^ name.hashCode ^ display.hashCode;
}
