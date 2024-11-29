package vn.vietmap.androidauto.helper

import android.util.Log
import androidx.car.app.model.Distance
import vn.vietmap.androidauto.R
import java.util.Locale

class VietMapHelper {
    companion object {
        fun getDisplayDistance(distance: Double): Distance {
            return if(distance>1000){
                Distance.create(distance/1000, Distance.UNIT_KILOMETERS)
            }else{
                Distance.create(distance, Distance.UNIT_METERS)
            }
        }

        fun getDisplayDuration(durationInMinutes: Double): String {
            return if (durationInMinutes < 60) {
                String.format(Locale.getDefault(), "%.0f phút", durationInMinutes)
            } else if (durationInMinutes < 60 * 24) {
                val hours = durationInMinutes.toInt() / 60
                val minutes = durationInMinutes.toInt() % 60
                String.format(Locale.getDefault(), "%d giờ %d phút", hours, minutes)
            } else {
                val days = durationInMinutes.toInt() / (60 * 24)
                val hours = (durationInMinutes.toInt() % (60 * 24)) / 60
                String.format(Locale.getDefault(), "%d ngày %d giờ", days, hours)
            }
        }

        fun getDrawableResId(maneuver: String): Int {
            return when (maneuver) {
                "arrive" -> R.drawable.arrive
                "arrive_left" -> R.drawable.arrive_left
                "arrive_right" -> R.drawable.arrive_right
                "arrive_straight" -> R.drawable.arrive_straight
                "close" -> R.drawable.close
                "continue" -> R.drawable.continue_img
                "continue_left" -> R.drawable.continue_left
                "continue_right" -> R.drawable.continue_right
                "continue_slight_left" -> R.drawable.continue_slight_left
                "continue_slight_right" -> R.drawable.continue_slight_right
                "continue_straight" -> R.drawable.continue_straight
                "continue_uturn" -> R.drawable.continue_uturn
                "depart" -> R.drawable.depart
                "depart_left" -> R.drawable.depart_left
                "depart_right" -> R.drawable.depart_right
                "depart_straight" -> R.drawable.depart_straight
                "end_of_road_left" -> R.drawable.end_of_road_left
                "end_of_road_right" -> R.drawable.end_of_road_right
                "fork_left" -> R.drawable.fork_left
                "fork_right" -> R.drawable.fork_right
                "fork_slight_left" -> R.drawable.fork_slight_left
                "fork_slight_right" -> R.drawable.fork_slight_right
                "fork_straight" -> R.drawable.fork_straight
                "invalid" -> R.drawable.invalid
                "invalid_left" -> R.drawable.invalid_left
                "invalid_right" -> R.drawable.invalid_right
                "invalid_slight_left" -> R.drawable.invalid_slight_left
                "invalid_slight_right" -> R.drawable.invalid_slight_right
                "invalid_straight" -> R.drawable.invalid_straight
                "invalid_uturn" -> R.drawable.invalid_uturn
                "merge_left" -> R.drawable.merge_left
                "merge_right" -> R.drawable.merge_right
                "merge_slight_left" -> R.drawable.merge_slight_left
                "merge_slight_right" -> R.drawable.merge_slight_right
                "new_name_left" -> R.drawable.new_name_left
                "new_name_right" -> R.drawable.new_name_right
                "new_name_slight_left" -> R.drawable.new_name_slight_left
                "new_name_slight_right" -> R.drawable.new_name_slight_right
                "new_name_sharp_left" -> R.drawable.new_name_sharp_left
                "new_name_sharp_right" -> R.drawable.new_name_sharp_right
                "new_name_straight" -> R.drawable.new_name_straight
                "notification_left" -> R.drawable.notification_left
                "notification_right" -> R.drawable.notification_right
                "notification_sharp_left" -> R.drawable.notification_sharp_left
                "notification_sharp_right" -> R.drawable.notificaiton_sharp_right
                "notification_slight_left" -> R.drawable.notification_slight_left
                "notification_slight_right" -> R.drawable.notification_slight_right
                "notification_straight" -> R.drawable.notification_straight
                "off_ramp_left" -> R.drawable.off_ramp_left
                "off_ramp_right" -> R.drawable.off_ramp_right
                "off_ramp_slight_left" -> R.drawable.off_ramp_slight_left
                "off_ramp_slight_right" -> R.drawable.off_ramp_slight_right
                "on_ramp_left" -> R.drawable.on_ramp_left
                "on_ramp_right" -> R.drawable.on_ramp_right
                "on_ramp_sharp_left" -> R.drawable.on_ramp_sharp_left
                "on_ramp_sharp_right" -> R.drawable.on_ramp_sharp_right
                "on_ramp_slight_left" -> R.drawable.on_ramp_slight_left
                "on_ramp_slight_right" -> R.drawable.on_ramp_slight_right
                "on_ramp_straight" -> R.drawable.on_ramp_straight
                "rotary_left" -> R.drawable.rotary_left
                "rotary_right" -> R.drawable.rotary_right
                "rotary_sharp_left" -> R.drawable.rotary_sharp_left
                "rotary_sharp_right" -> R.drawable.rotary_sharp_right
                "rotary_slight_left" -> R.drawable.rotary_slight_left
                "rotary_slight_right" -> R.drawable.rotary_slight_right
                "rotary_straight" -> R.drawable.rotary_straight
                "roundabout_left" -> R.drawable.roundabout_left
                "roundabout_right" -> R.drawable.roundabout_right
                "roundabout_sharp_left" -> R.drawable.roundabout_sharp_left
                "roundabout_sharp_right" -> R.drawable.roundabout_sharp_right
                "roundabout_slight_left" -> R.drawable.roundabout_slight_left
                "roundabout_slight_right" -> R.drawable.roundabout_slight_right
                "roundabout_straight" -> R.drawable.roundabout_straight
                "turn_left" -> R.drawable.turn_left
                "turn_right" -> R.drawable.turn_right
                "turn_sharp_left" -> R.drawable.turn_sharp_left
                "turn_sharp_right" -> R.drawable.turn_sharp_right
                "turn_slight_left" -> R.drawable.turn_slight_left
                "turn_slight_right" -> R.drawable.turn_slight_right
                "turn_straight" -> R.drawable.turn_straight
                "uturn" -> R.drawable.uturn
                else -> R.drawable.invalid
            }
        }
    }
}