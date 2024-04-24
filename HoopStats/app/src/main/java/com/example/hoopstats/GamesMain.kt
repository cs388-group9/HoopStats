package com.example.hoopstats

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.hoopstats.models.Game
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import android.app.AlertDialog
import android.widget.EditText

import com.codepath.asynchttpclient.AsyncHttpClient
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler
import org.json.JSONObject
import com.codepath.asynchttpclient.RequestParams
import okhttp3.Headers
class GamesMain : AppCompatActivity() {

    private lateinit var gamesRecyclerView: RecyclerView
    private lateinit var noGamesTextView: TextView
    private lateinit var gameAdapter: GameAdapter
    private lateinit var mAuth: FirebaseAuth
    private lateinit var mGoogleSignInClient: GoogleSignInClient
    private var gamesList: MutableList<Game> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_games_main)

        gamesRecyclerView = findViewById(R.id.gamesRecyclerView)

        noGamesTextView = findViewById(R.id.noGamesTextView)
        gamesRecyclerView.layoutManager = LinearLayoutManager(this)
        gameAdapter = GameAdapter(gamesList)
        gamesRecyclerView.adapter = gameAdapter

        mAuth = FirebaseAuth.getInstance()
        mGoogleSignInClient = initializeGoogleSignIn()

        findViewById<Button>(R.id.createGameButton).setOnClickListener {
            startActivityForResult(Intent(this, CreateGame::class.java), 1001)
        }

        findViewById<Button>(R.id.logout_button).setOnClickListener {
            signOutAndStartSignInActivity()
        }

        findViewById<Button>(R.id.joinGameButton).setOnClickListener {
            startActivity(Intent(this, JoinGame::class.java))
        }


        findViewById<Button>(R.id.joinGameButton).setOnClickListener {
            startActivity(Intent(this, JoinGame::class.java))
        }

        findViewById<Button>(R.id.submitWeatherButton).setOnClickListener {
            var zipcode = findViewById<EditText>(R.id.zipcodeEditText)
            val client = AsyncHttpClient()
            val params = RequestParams()
            params["zip"] = zipcode.text.toString()+",US"
            params["appid"] = "491d5553c05bab2ee795fee79e672d3e"

            client["https://api.openweathermap.org/geo/1.0/zip", params, object :
                JsonHttpResponseHandler() {
                override fun onSuccess(statusCode: Int, headers: Headers, json: JSON) {
                    val latitude = json.jsonObject.getDouble("lat")
                    val longitude = json.jsonObject.getDouble("lon")
                    val params2 = RequestParams()
                    params2["lat"] = latitude.toString()
                    params2["lon"] = longitude.toString()
                    params2["appid"] = "491d5553c05bab2ee795fee79e672d3e"
                    params2["units"] = "imperial"
                    client["https://api.openweathermap.org/data/2.5/weather", params2, object :
                        JsonHttpResponseHandler() {
                        override fun onSuccess(statusCode: Int, headers: Headers, json: JSON) {
                            // Access a JSON object response with `json.jsonObject`
                            val firstItem = JSONObject(json.jsonObject.getJSONArray("weather").get(0).toString())
                            val weatherDescription = firstItem.getString("description")
                            val temp = json.jsonObject.getJSONObject("main").getDouble("temp").toString()
                            val tempFeelsLike = json.jsonObject.getJSONObject("main").getDouble("feels_like").toString()
                            val humidity = json.jsonObject.getJSONObject("main").getDouble("humidity").toString()
                            val wind = json.jsonObject.getJSONObject("wind").getDouble("speed").toString()
                            showWeatherDialog(temp, tempFeelsLike, weatherDescription, humidity, wind, zipcode.text.toString())
                        }

                        override fun onFailure(
                            statusCode: Int,
                            headers: Headers?,
                            response: String,
                            throwable: Throwable?
                        ) {
                        }
                    }]

                }

                override fun onFailure(
                    statusCode: Int,
                    headers: Headers?,
                    response: String,
                    throwable: Throwable?
                ) {
                }
            }]
        }

        fetchGames() // Fetch games from Firebase
    }

    private fun showWeatherDialog(temp : String, tempFeelsLike : String, weatherDescription: String, humidity : String, wind : String, zipcode : String) {
        val builder: AlertDialog.Builder = AlertDialog.Builder(this)
        builder
            .setMessage("Temperature: "+ temp + "F\n\n"+weatherDescription.get(0).uppercase()+weatherDescription.substring(1)+"\nFeels Like: "+tempFeelsLike+"F"+"\n\nHumidity: "+humidity+"%\nWinds: "+wind+"mph")
            .setTitle("Weather - "+zipcode)

        val dialog: AlertDialog = builder.create()
        dialog.show()
    }

    private fun initializeGoogleSignIn(): GoogleSignInClient {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        return GoogleSignIn.getClient(this, gso)
    }

    private fun fetchGames() {
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        if (userId == null) {
            Log.e("GamesMain", "User not logged in")
            return
        }

        val databaseReference = FirebaseDatabase.getInstance().getReference("games")
        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                gamesList.clear()
                for (snapshot in dataSnapshot.children) {
                    val game = snapshot.getValue(Game::class.java)
                    if (game != null) {
                        if (game.creatorUserId == userId || game.submittedByUserIds.contains(userId)) {
                            gamesList.add(game)
                        }
                    } else {
                        Log.e("GamesMain", "Error parsing game data: ${snapshot.key}")
                    }
                }
                gameAdapter.notifyDataSetChanged()
                updateUI()
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.e("GamesMain", "Database error: ${databaseError.message}")
            }
        })
    }


    private fun updateUI() {
        if (gamesList.isEmpty()) {
            noGamesTextView.visibility = View.VISIBLE
            gamesRecyclerView.visibility = View.GONE
        } else {
            noGamesTextView.visibility = View.GONE
            gamesRecyclerView.visibility = View.VISIBLE
        }
        gameAdapter.notifyDataSetChanged()
    }

    private fun signOutAndStartSignInActivity() {
        mAuth.signOut()
        mGoogleSignInClient.signOut().addOnCompleteListener(this) {
            startActivity(Intent(this, SignInActivity::class.java))
            finish()
        }
    }
}
