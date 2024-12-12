import 'package:flutter/material.dart';
import 'package:flutter_easyloading/flutter_easyloading.dart';
import 'package:google_fonts/google_fonts.dart';
import 'package:vietmap_map/constants/colors.dart';
import 'package:vietmap_map/core/navigators/app_route.dart';
import 'package:vietmap_map/core/navigators/app_navigator.dart';
import 'package:vietmap_map/features/bloc/bloc.dart';
import 'package:vietmap_map/features/map_screen/bloc/bloc.dart';

class App extends StatelessWidget {
  const App({super.key});

  @override
  Widget build(BuildContext context) {
    return MultiBlocProvider(
      providers: AppBloc.providers,
      child: MaterialApp(
        navigatorKey: AppNavigator.navigatorKey,
        title: 'Fuel & Brake Status',
        onGenerateRoute: AppNavigator.getRoute,
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
