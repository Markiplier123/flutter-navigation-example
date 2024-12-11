import 'package:flutter/material.dart';
import 'package:flutter_easyloading/flutter_easyloading.dart';
import 'package:google_fonts/google_fonts.dart';
import 'package:vietmap_map/constants/colors.dart';
import 'package:vietmap_map/constants/route.dart';
import 'package:vietmap_map/features/bloc/bloc.dart';
import 'package:vietmap_map/features/home/fuel_brake_screen.dart';
import 'package:vietmap_map/features/map_screen/bloc/bloc.dart';
import 'package:vietmap_map/features/map_screen/maps_screen.dart';
import 'package:vietmap_map/features/pick_address_screen/pick_address_screen.dart';
import 'package:vietmap_map/features/routing_screen/routing_screen.dart';
import 'package:vietmap_map/features/routing_screen/search_address.dart';
import 'package:vietmap_map/features/search_screen/search_screen.dart';

GlobalKey<NavigatorState> navigatorKey = GlobalKey();

class FuelBrakeApp extends StatelessWidget {
  const FuelBrakeApp({super.key});

  @override
  Widget build(BuildContext context) {
    return MultiBlocProvider(
      providers: AppBloc.providers,
      child: MaterialApp(
        navigatorKey: navigatorKey,
        title: 'Fuel & Brake Status',
        routes: {
          Routes.fuelBrakeScreen: (context) => const FuelBrakeScreen(),
          Routes.searchScreen: (context) => const SearchScreen(),
          Routes.mapScreen: (context) => const MapScreen(),
          Routes.routingScreen: (context) => const RoutingScreen(),
          Routes.pickAddressScreen: (context) => const PickAddressScreen(),
          Routes.searchAddressForRoutingScreen: (context) =>
              const SearchAddress(),
        },
        initialRoute: Routes.fuelBrakeScreen,
        theme: ThemeData(
            useMaterial3: false,
            primarySwatch: MaterialColor(
              vietmapColor.value,
              <int, Color>{
                50: vietmapColor.withOpacity(0.05),
                100: vietmapColor.withOpacity(0.1),
                200: vietmapColor.withOpacity(0.2),
                300: vietmapColor.withOpacity(0.3),
                400: vietmapColor.withOpacity(0.4),
                500: vietmapColor,
                600: vietmapColor.withOpacity(0.6),
                700: vietmapColor.withOpacity(0.7),
                800: vietmapColor.withOpacity(0.8),
                900: vietmapColor.withOpacity(0.9),
              },
            ),
            primaryColor: vietmapColor,
            primaryColorLight: vietmapColor,
            fontFamily: GoogleFonts.montserrat().fontFamily),
        debugShowCheckedModeBanner: false,
        builder: EasyLoading.init(),
      ),
    );
  }
}
