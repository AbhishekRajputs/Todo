package com.example.todo

import android.app.*
import android.content.Context
import android.content.Intent
import android.media.Ringtone
import android.media.RingtoneManager
import android.net.Uri
import android.os.IBinder
import android.widget.Toast
import androidx.core.app.NotificationCompat


class RingtoneService :Service() {

    companion object{
        lateinit var r : Ringtone
    }
    var id:Int=0
    var isRunning:Boolean= false

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        var state: String= intent!!.getStringExtra("extra")
        assert(state!=null)
        when(state){
            "on"-> id= 1
            "off"-> id=0
        }
        if (!this.isRunning && id == 1){
            playAlarm()
            this.isRunning= true
            this.id=0
            fireNotification()
        }
        else if (!this.isRunning && id == 0){
            r.stop()
            this.isRunning= false
            this.id=0
        }
        else if (!this.isRunning && id == 0){
            this.isRunning= false
            this.id=0
        }
        else if (!this.isRunning && id == 1){
            this.isRunning= false
            this.id=1
        }
        else{

        }

        return START_NOT_STICKY

    }

    private fun fireNotification() {
              var main_activity_intent : Intent= Intent(this,StopAlarm::class.java)
              var pi:PendingIntent= PendingIntent.getActivity(this,0,main_activity_intent,0)
              val defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

        var notify_manager:NotificationManager= getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        var notification :Notification= NotificationCompat.Builder(this).setContentTitle(
            "Alarm is going on").setSmallIcon(R.mipmap.ic_launcher).setSound(defaultSoundUri).setContentText(
            "Click Here To Stop").setContentIntent(pi).setAutoCancel(true).build()
        notify_manager.notify(0,notification)
       /* r.stop()
        this.isRunning= false
        this.id=0*/
    }

    private fun playAlarm(){
        var alramUri: Uri? = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
        if (alramUri==null){
            alramUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        }
        r = RingtoneManager.getRingtone(baseContext,alramUri)
        r.play()
    }

    override fun onDestroy() {
        playAlarm()
        this.isRunning= true
        this.id=0
    }
}