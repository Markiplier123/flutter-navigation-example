import 'dart:async';

import 'package:flutter/material.dart';
import 'package:flutter_dotenv/flutter_dotenv.dart';
import 'package:hive_flutter/hive_flutter.dart';
import 'package:path_provider/path_provider.dart';
import 'package:vietmap_map/features/home/app.dart';

void main() {
  runZonedGuarded(() async {
    WidgetsFlutterBinding.ensureInitialized();
    try {
      await dotenv.load(fileName: ".env");

      final appDocumentDirectory = await getApplicationDocumentsDirectory();
      Hive.init(appDocumentDirectory.path);
    } catch (e) {
      rethrow;
    }
    return runApp(const App());
  }, (error, stackTrace) {});
}
