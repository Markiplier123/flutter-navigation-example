import 'package:vietmap_map/features/map_screen/bloc/bloc.dart';
import 'package:vietmap_map/features/routing_screen/bloc/routing_bloc.dart';

class AppBloc {
  static final MapBloc mapBloc = MapBloc();
  static final RoutingBloc routingBloc = RoutingBloc();

  static final List<BlocProvider> providers = [
    BlocProvider<MapBloc>(create: (context) => mapBloc),
    BlocProvider<RoutingBloc>(create: (context) => routingBloc),
  ];
}
