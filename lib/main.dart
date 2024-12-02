import 'package:flutter/material.dart';
import 'package:flutter_dotenv/flutter_dotenv.dart';
import 'package:flutter_easyloading/flutter_easyloading.dart';
import 'package:google_fonts/google_fonts.dart';
import 'package:hive_flutter/hive_flutter.dart';
import 'package:vietmap_map/constants/colors.dart';
import 'package:vietmap_map/features/pick_address_screen/pick_address_screen.dart';
import 'package:vietmap_map/features/routing_screen/routing_screen.dart';
import 'package:vietmap_map/features/routing_screen/search_address.dart';
import 'package:vietmap_map/features/search_screen/search_screen.dart';
import 'package:path_provider/path_provider.dart';
import 'constants/route.dart';
import 'features/map_screen/bloc/bloc.dart';
import 'features/map_screen/maps_screen.dart';
import 'features/routing_screen/bloc/bloc.dart';
import 'package:vietmap_map/NavigationScreen.dart';
Future<void> main() async {
  WidgetsFlutterBinding.ensureInitialized();
  try {
    await dotenv.load(fileName: ".env");
  } catch (e) {}
  try {
    final appDocumentDirectory = await getApplicationDocumentsDirectory();
    Hive.init(appDocumentDirectory.path);
  } catch (e) {
    print(e);
  }

  runApp(NavigationScreen());
}
