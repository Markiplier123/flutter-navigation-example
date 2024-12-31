import 'package:flutter/material.dart';
import 'package:vietmap_map/core/navigators/app_route.dart';
import 'package:vietmap_map/features/home/components/fuel_brake_screen.dart';
import 'package:vietmap_map/features/map_screen/maps_screen.dart';
import 'package:vietmap_map/features/pick_address_screen/pick_address_screen.dart';
import 'package:vietmap_map/features/routing_screen/routing_screen.dart';
import 'package:vietmap_map/features/routing_screen/search_address.dart';
import 'package:vietmap_map/features/search_screen/search_screen.dart';

class AppNavigator {
  static GlobalKey<NavigatorState> navigatorKey = GlobalKey();

  static Route getRoute(RouteSettings setting) {
    final Map<String, dynamic>? arguments =
        setting.arguments as Map<String, dynamic>?;

    switch (setting.name) {
      case Routes.mapScreen:
        return MaterialPageRoute(builder: (context) {
          return MapScreen(
            isNavigator: arguments?['navigator'] ?? false,
          );
        });
      case Routes.fuelBrakeScreen:
        return MaterialPageRoute(builder: (context) {
          return const FuelBrakeScreen();
        });
      case Routes.searchScreen:
        return MaterialPageRoute(builder: (context) {
          return const SearchScreen();
        });
      case Routes.routingScreen:
        return MaterialPageRoute(builder: (context) {
          return RoutingScreen(
            paramsModel: arguments?['paramsModel'],
            position: arguments?['position'],
          );
        });
      case Routes.pickAddressScreen:
        return MaterialPageRoute(builder: (context) {
          return const PickAddressScreen();
        });
      case Routes.searchAddressForRoutingScreen:
        return MaterialPageRoute(builder: (context) {
          return SearchAddress(
            paramsModel: arguments?['paramsModel'],
          );
        });
      default:
        return MaterialPageRoute(builder: (context) {
          return const FuelBrakeScreen();
        });
    }
  }
}
