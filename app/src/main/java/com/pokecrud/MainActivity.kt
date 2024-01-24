package com.pokecrud

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Address
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Parcelable
import android.provider.Settings
import android.widget.Button
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.pokecrud.databinding.ActivityMainBinding
import java.util.concurrent.atomic.AtomicInteger


class MainActivity : AppCompatActivity() {

    private lateinit var add: Button
    private lateinit var check: Button
    private lateinit var binding: ActivityMainBinding
    private lateinit var androidId: String
    private lateinit var db_ref: DatabaseReference
    private lateinit var generator: AtomicInteger

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


        add = findViewById(R.id.btn_add_pokemon)
        check = binding.btnListPokemon

        add.setOnClickListener {
            val activity = Intent(applicationContext, PokeAdd::class.java)
            startActivity(activity)
        }

        check.setOnClickListener {
            val activity = Intent(applicationContext, PokeCheck::class.java)
            startActivity(activity)
        }

        createNotificationChannel()
        androidId = Settings.Secure.getString(contentResolver, Settings.Secure.ANDROID_ID)
        db_ref = FirebaseDatabase.getInstance().reference
        generator = AtomicInteger(0)

        //CONTROLADOR NOTIFICACIONES
        db_ref.child("Pokemon").child("Pokemons")
            .addChildEventListener(object : ChildEventListener {
                override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                    val pojo_pokemon = snapshot.getValue(Pokemon::class.java)
                    if (!pojo_pokemon!!.user_notification.equals(androidId) && pojo_pokemon!!.noti_status == Status.CREATED) {
                        db_ref.child("Pokemon").child("Pokemons").child(pojo_pokemon.id!!)
                            .child("noti_status").setValue(Status.NOTIFICATED)
                        notiGenerator(generator.incrementAndGet(), pojo_pokemon,
                            "Added new Pokemon:  " + pojo_pokemon.name,
                            "New data in the app",
                            PokeCheck::class.java
                        )
                    }
                }

                override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                    val pojo_pokemon = snapshot.getValue(Pokemon::class.java)

                    if (!pojo_pokemon!!.user_notification.equals(androidId) && pojo_pokemon!!.noti_status == Status.EDITED) {
                        db_ref.child("Pokemon").child("Pokemons").child(pojo_pokemon.id!!)
                            .child("noti_status").setValue(Status.NOTIFICATED)
                        notiGenerator(
                            generator.incrementAndGet(),
                            pojo_pokemon,
                            "Pokemon edited: " + pojo_pokemon.name,
                            "Pokemon edited in the app",
                            PokeCheck::class.java
                        )
                    }
                }



                override fun onChildRemoved(snapshot: DataSnapshot) {
                    val pojo_pokemon = snapshot.getValue(Pokemon::class.java)

                    if (pojo_pokemon != null && !pojo_pokemon.user_notification.equals(androidId)) {
                        notiGenerator(
                            generator.incrementAndGet(),
                            pojo_pokemon,
                            "Pokemon deleted: " + pojo_pokemon.name,
                            "Data deleted in the app",
                            PokeCheck::class.java
                        )
                    }
                }

                override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
                    TODO("Not yet implemented")
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }
            })

    }

    //creacion de notificaciones
    private fun notiGenerator(
        id_noti: Int,
        pojo: Parcelable,
        content: String,
        title: String,
        address: Class<*>
    ) {

        val id = "Test Channel"
        val activity = Intent(applicationContext, address)
        activity.putExtra("pokemon", pojo)

        val pendingIntent =
            PendingIntent.getActivity(this, 0, activity, PendingIntent.FLAG_UPDATE_CURRENT)

        val notification = NotificationCompat.Builder(this, id)
            .setSmallIcon(R.drawable.pokeball)
            .setContentTitle(title)
            .setContentText(content)
            .setSubText("Info system")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        with(NotificationManagerCompat.from(this)) {

            if (ActivityCompat.checkSelfPermission(
                    this@MainActivity,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return
            }
            notify(id_noti, notification)
        }


    }


    private fun createNotificationChannel() {
        val name = "Basic channel"
        val id = "Test Channel"
        val desc = "Basic Notification"
        val level = NotificationManager.IMPORTANCE_DEFAULT

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(id, name, level).apply {
                description = desc
            }

            val nm: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            nm.createNotificationChannel(channel)
        }
    }






}



