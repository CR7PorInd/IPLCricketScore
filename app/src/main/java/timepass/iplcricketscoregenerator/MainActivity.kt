package timepass.iplcricketscoregenerator

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.toSize
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import coil.compose.rememberAsyncImagePainter
import timepass.iplcricketscoregenerator.Constants.getLogoUrl
import timepass.iplcricketscoregenerator.ui.theme.IPLCricketScoreGeneratorTheme
import timepass.iplcricketscoregenerator.ui.theme.Typography

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            IPLCricketScoreGeneratorTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    val navController = rememberNavController()
                    PublicNavigator.navController = navController
                    NavHost(navController = navController, startDestination = "home"){
                        composable("home"){
                            InitialScreen()
                        }
                        
                        composable(
                            "match/{home}/{away}",
                            arguments = listOf(
                                navArgument("home"){
                                    type = NavType.StringType
                                    nullable = false
                                    defaultValue = "RCB"
                                },
                                navArgument("away"){
                                    type = NavType.StringType
                                    nullable = false
                                    defaultValue = "CSK"
                                }
                            )
                        ){
                            MainScreen(
                                homeTeam = try{
                                    Team.valueOf(it.arguments!!.getString("home", "RCB")!!)
                                } catch (e: Exception) {
                                    Team.RCB
                                },
                                awayTeam = try{
                                    Team.valueOf(it.arguments!!.getString("away", "CSK")!!)
                                } catch (e: Exception) {
                                    Team.CSK
                                },
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
@OptIn(ExperimentalMaterialApi::class)
fun InitialScreen(){
    var homeTeam by remember { mutableStateOf(Team.RCB) }
    var awayTeam by remember { mutableStateOf(Team.GT) }
    var selectedIndex by remember { mutableStateOf(false) }
    Scaffold(
        topBar = {
            Row(
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    "TATA IPL 2022",
                    style = MaterialTheme.typography.h4,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .align(Alignment.CenterVertically)
                        .padding(8.dp)
                )
            }
        }
    ) {
        LazyVerticalGrid(
            columns = GridCells.Adaptive(140.dp),
            contentPadding = PaddingValues(8.dp, 16.dp, 8.dp, 75.dp)
        ){
            items(key = { Team.values()[it].ordinal }, count = Team.values().size){
                Card(
                    onClick = {
                        selectedIndex = !selectedIndex
                        when(selectedIndex){
                            true -> {
                                homeTeam = Team.values()[it]
                                awayTeam = Team.values()[it]
                            }
                            false -> awayTeam = Team.values()[it]
                        }
                        if(!selectedIndex){
                            PublicNavigator.navController.navigate("match/$homeTeam/$awayTeam")
                        }
                    },
                    elevation = 4.dp,
                    modifier = Modifier
                        .padding(16.dp),
                    enabled = !(selectedIndex && homeTeam == Team.values()[it])
                ){
                    Column(
                        modifier = Modifier
                            .fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.SpaceEvenly
                    ){
                        Image(
                            painter = rememberAsyncImagePainter(
                                getLogoUrl(Team.values()[it])
                            ),
                            contentDescription = Team.values()[it].name,
                            contentScale = ContentScale.Fit,
                            modifier = Modifier
                                .size(128.dp)
                                .clip(CircleShape)
                                .padding(4.dp)
                        )
                        Text(
                            Team.values()[it].name,
                            style = Typography.h6,
                            fontWeight = FontWeight.Bold,
                            softWrap = false,
                            modifier = Modifier
                                .padding(4.dp)
                        )
                        if(homeTeam == Team.values()[it]){
                            Text(
                                "Home",
                                style = Typography.h6,
                                fontWeight = FontWeight.Bold,
                                softWrap = false,
                                modifier = Modifier
                                    .padding(4.dp)
                            )
                        } else if(awayTeam == Team.values()[it]){
                            Text(
                                "Away",
                                style = Typography.h6,
                                fontWeight = FontWeight.Bold,
                                softWrap = false,
                                modifier = Modifier
                                    .padding(4.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun MainScreen(homeTeam: Team, awayTeam: Team){
    var homeBalls by remember { mutableStateOf(0) }
    var homeRuns by remember { mutableStateOf(0) }
    var homeWickets by remember { mutableStateOf(0) }

    var home by remember { mutableStateOf(homeTeam) }

    val away by remember { mutableStateOf(awayTeam) }

    var awayBalls by remember { mutableStateOf(0) }
    var awayRuns by remember { mutableStateOf(0) }
    var awayWickets by remember { mutableStateOf(0) }

    var statusMessage by remember { mutableStateOf("Match waiting...") }

    var isBatting by remember { mutableStateOf(false) }

    var hasWon by remember { mutableStateOf(false) }

    var length by remember { mutableStateOf("SLOT") }

    var foot by remember { mutableStateOf(FootValues.FRONT) }

    var shot by remember { mutableStateOf(ShotValues.DEFEND) }

    var degree by remember { mutableStateOf(0) }

    LaunchedEffect(true){
        home = homeTeam
        isBatting = listOf(true, false).random()
    }

    LaunchedEffect(homeBalls) {
        if(homeBalls == 120 || homeWickets == 10){
            if(awayBalls > 0){
                statusMessage = if(awayRuns > homeRuns){
                    hasWon = true
                    "${away.name} won by ${awayRuns - homeRuns} runs"
                } else if (awayRuns == homeRuns) {
                    hasWon = true
                    "Match drawn"
                } else {
                    hasWon = true
                    "${home.name} won by ${10 - homeWickets} wickets"
                }
            } else {
                isBatting = false
                statusMessage = "${away.name} need ${(homeRuns - awayRuns) + 1} runs in ${120 - awayBalls} balls"
            }
        } else if(awayBalls > 0){
            if(awayRuns < homeRuns){
                hasWon = true
                statusMessage = "${home.name} won by ${10 - homeWickets} wickets"
            } else {
                statusMessage = "${home.name} need ${(awayRuns - homeRuns) + 1} runs in ${120 - homeBalls} balls"
            }
        }
    }

    LaunchedEffect(awayBalls) {
        if(awayBalls == 120 || awayWickets == 10){
            if(homeBalls > 0){
                statusMessage = if(awayRuns < homeRuns){
                    hasWon = true
                    "${home.name} won by ${homeRuns - awayRuns} runs"
                } else if (awayRuns == homeRuns) {
                    hasWon = true
                    "Match drawn"
                } else {
                    hasWon = true
                    "${away.name} won by ${10 - awayWickets} wickets"
                }
            } else {
                isBatting = true
                statusMessage = "${home.name} need ${(awayRuns - homeRuns) + 1} runs in ${120 - homeBalls} balls"
            }
        } else if(homeBalls > 0){
            if(awayRuns > homeRuns){
                hasWon = true
                statusMessage = "${away.name} won by ${10 - awayWickets} wickets"
            } else {
                statusMessage = "${away.name} need ${(homeRuns - awayRuns) + 1} runs in ${120 - awayBalls} balls"
            }
        }
    }

    Scaffold(
        topBar = {
            Row(
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    "${home.name} vs ${away.name}",
                    style = MaterialTheme.typography.h4,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .align(Alignment.CenterVertically)
                        .padding(8.dp)
                )
            }
        }
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                contentPadding = PaddingValues(8.dp, 16.dp, 8.dp, 16.dp),
                userScrollEnabled = false
            ){
                item{
                    Card(
                        onClick = {

                        },
                        elevation = 0.dp,
                        modifier = Modifier
                            .padding(16.dp),
                        indication = null
                    ){
                        Column(
                            modifier = Modifier
                                .fillMaxSize(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.SpaceEvenly
                        ){
                            Image(
                                painter = rememberAsyncImagePainter(
                                    getLogoUrl(home)
                                ),
                                contentDescription = home.name,
                                contentScale = ContentScale.Fit,
                                modifier = Modifier
                                    .size(64.dp)
                                    .clip(CircleShape)
                                    .padding(4.dp)
                            )
                            Text(
                                home.name,
                                style = Typography.h6,
                                fontWeight = FontWeight.Bold,
                                softWrap = false,
                                modifier = Modifier
                                    .padding(4.dp)
                            )

                            Text(
                                "$homeRuns/$homeWickets (${homeBalls / 6}.${homeBalls % 6})",
                                style = MaterialTheme.typography.subtitle2,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier
                                    .padding(8.dp)
                            )
                        }
                    }
                }

                item{
                    Card(
                        onClick = {

                        },
                        elevation = 0.dp,
                        modifier = Modifier
                            .padding(16.dp),
                        indication = null
                    ){
                        Column(
                            modifier = Modifier
                                .fillMaxSize(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.SpaceEvenly
                        ){
                            Image(
                                painter = rememberAsyncImagePainter(
                                    getLogoUrl(away)
                                ),
                                contentDescription = away.name,
                                contentScale = ContentScale.Fit,
                                modifier = Modifier
                                    .size(64.dp)
                                    .clip(CircleShape)
                                    .padding(4.dp)
                            )
                            Text(
                                away.name,
                                style = Typography.h6,
                                fontWeight = FontWeight.Bold,
                                softWrap = false,
                                modifier = Modifier
                                    .padding(4.dp)
                            )

                            Text(
                                "$awayRuns/$awayWickets (${awayBalls / 6}.${awayBalls % 6})",
                                style = MaterialTheme.typography.subtitle2,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier
                                    .padding(8.dp)
                            )
                        }
                    }
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ){
                Text(
                    statusMessage,
                    style = MaterialTheme.typography.subtitle1,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .align(Alignment.CenterVertically)
                        .padding(8.dp)
                )
            }

            Text(
                text = "Options",
                style = MaterialTheme.typography.h5,
                modifier = Modifier
                    .padding(16.dp)
            )

            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth(),
                contentPadding = PaddingValues(8.dp, 8.dp, 8.dp, 24.dp)
            ){
                if(!hasWon){
                    when(isBatting){
                        true -> {
                            item{
                                var widthSize by remember { mutableStateOf(Size.Zero) }
                                var dropState by remember { mutableStateOf(false) }
                                Box(
                                    Modifier
                                        .clip(RoundedCornerShape(32.dp))
                                        .fillMaxWidth()
                                        .padding(horizontal = 24.dp, vertical = 12.dp)
                                ){
                                    Row(
                                        Modifier
                                            .fillMaxWidth(),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ){
                                        OutlinedTextField(
                                            value = foot.name,
                                            onValueChange = {
                                                foot = FootValues.valueOf(it)
                                            },
                                            colors = TextFieldDefaults.outlinedTextFieldColors(
                                                backgroundColor = Color.Transparent,
                                                focusedBorderColor = Color.Transparent,
                                                unfocusedBorderColor = Color.Transparent,
                                                trailingIconColor = MaterialTheme.colors.primary
                                            ),
                                            readOnly = true,
                                            modifier = Modifier
                                                .onGloballyPositioned {
                                                    widthSize = it.size.toSize()
                                                }
                                                .weight(2.5f)
                                                .padding(horizontal = 8.dp),
                                            trailingIcon = {
                                                Icon(
                                                    imageVector =
                                                    if(dropState) Icons.Rounded.KeyboardArrowUp
                                                    else Icons.Rounded.KeyboardArrowDown,
                                                    contentDescription = "Click Me!",
                                                    modifier = Modifier
                                                        .clickable{
                                                            dropState = !dropState
                                                        }
                                                )
                                            }
                                        )
                                        DropdownMenu(
                                            expanded = dropState,
                                            onDismissRequest = {
                                                dropState = false
                                            },
                                            modifier = Modifier
                                                .width(with(LocalDensity.current){widthSize.width.toDp()})
                                        ) {
                                            FootValues.values().forEach {
                                                DropdownMenuItem(
                                                    onClick = {
                                                        foot = it
                                                        dropState = false
                                                    }
                                                ){
                                                    Text(it.name, modifier = Modifier.padding(start = 8.dp))
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                            item{
                                var widthSize by remember { mutableStateOf(Size.Zero) }
                                var dropState by remember { mutableStateOf(false) }
                                Box(
                                    Modifier
                                        .clip(RoundedCornerShape(32.dp))
                                        .fillMaxWidth()
                                        .padding(horizontal = 24.dp, vertical = 12.dp)
                                ){
                                    Row(
                                        Modifier
                                            .fillMaxWidth(),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ){
                                        OutlinedTextField(
                                            value = shot.name,
                                            onValueChange = {
                                                shot = ShotValues.valueOf(it)
                                            },
                                            colors = TextFieldDefaults.outlinedTextFieldColors(
                                                backgroundColor = Color.Transparent,
                                                focusedBorderColor = Color.Transparent,
                                                unfocusedBorderColor = Color.Transparent,
                                                trailingIconColor = MaterialTheme.colors.primary
                                            ),
                                            readOnly = true,
                                            modifier = Modifier
                                                .onGloballyPositioned {
                                                    widthSize = it.size.toSize()
                                                }
                                                .weight(2.5f)
                                                .padding(horizontal = 8.dp),
                                            trailingIcon = {
                                                Icon(
                                                    imageVector =
                                                    if(dropState) Icons.Rounded.KeyboardArrowUp
                                                    else Icons.Rounded.KeyboardArrowDown,
                                                    contentDescription = "Click Me!",
                                                    modifier = Modifier
                                                        .clickable{
                                                            dropState = !dropState
                                                        }
                                                )
                                            }
                                        )
                                        DropdownMenu(
                                            expanded = dropState,
                                            onDismissRequest = {
                                                dropState = false
                                            },
                                            modifier = Modifier
                                                .width(with(LocalDensity.current){widthSize.width.toDp()})
                                        ) {
                                            ShotValues.values().forEach {
                                                DropdownMenuItem(
                                                    onClick = {
                                                        shot = it
                                                        dropState = false
                                                    }
                                                ){
                                                    Text(it.name, modifier = Modifier.padding(start = 8.dp))
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                            item{
                                Box(
                                    Modifier
                                        .clip(RoundedCornerShape(32.dp))
                                        .fillMaxWidth()
                                        .padding(horizontal = 24.dp, vertical = 12.dp)
                                ){
                                    Row(
                                        Modifier
                                            .fillMaxWidth(),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ){
                                        Slider(
                                            value = degree.toFloat(),
                                            onValueChange = {
                                                degree = it.toInt()
                                            },
                                            valueRange = 1f..360f,
                                            modifier = Modifier.weight(1f).padding(12.dp)
                                        )
                                        Text(degree.toString(), modifier = Modifier.padding(end = 8.dp))
                                    }
                                }
                            }
                        }
                        false -> {
                            item{
                                var widthSize by remember { mutableStateOf(Size.Zero) }
                                var dropState by remember { mutableStateOf(false) }
                                Box(
                                    Modifier
                                        .clip(RoundedCornerShape(32.dp))
                                        .fillMaxWidth()
                                        .padding(horizontal = 24.dp, vertical = 12.dp)
                                ){
                                    Row(
                                        Modifier
                                            .fillMaxWidth(),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ){
                                        OutlinedTextField(
                                            value = length,
                                            onValueChange = {
                                                length = it
                                            },
                                            colors = TextFieldDefaults.outlinedTextFieldColors(
                                                backgroundColor = Color.Transparent,
                                                focusedBorderColor = Color.Transparent,
                                                unfocusedBorderColor = Color.Transparent,
                                                trailingIconColor = MaterialTheme.colors.primary
                                            ),
                                            readOnly = true,
                                            modifier = Modifier
                                                .onGloballyPositioned {
                                                    widthSize = it.size.toSize()
                                                }
                                                .weight(2.5f)
                                                .padding(horizontal = 8.dp),
                                            trailingIcon = {
                                                Icon(
                                                    imageVector =
                                                    if(dropState) Icons.Rounded.KeyboardArrowUp
                                                    else Icons.Rounded.KeyboardArrowDown,
                                                    contentDescription = "Click Me!",
                                                    modifier = Modifier
                                                        .clickable{
                                                            dropState = !dropState
                                                        }
                                                )
                                            }
                                        )
                                        DropdownMenu(
                                            expanded = dropState,
                                            onDismissRequest = {
                                                dropState = false
                                            },
                                            modifier = Modifier
                                                .width(with(LocalDensity.current){widthSize.width.toDp()})
                                        ) {
                                            LengthValues.values().forEach {
                                                DropdownMenuItem(
                                                    onClick = {
                                                        length = it
                                                        dropState = false
                                                    }
                                                ){
                                                    Text(it, modifier = Modifier.padding(start = 8.dp))
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                    item{
                        Button(
                            onClick = {
                                if(isBatting){
                                    length = LengthValues.values().random()
                                } else {
                                    val lstFoot = FootValues.values().toMutableList()
                                    lstFoot += getFootFromLength(LengthValues.valueOf(length))
                                    lstFoot += getFootFromLength(LengthValues.valueOf(length))
                                    lstFoot += getFootFromLength(LengthValues.valueOf(length))
                                    lstFoot += getFootFromLength(LengthValues.valueOf(length))
                                    shot = getShotsBetter().random()
                                    foot = lstFoot.random()
                                    degree = (1..360).random()
                                }
                                val x = getRuns(Input(
                                    foot.ordinal,
                                    shot,
                                    LengthValues.valueOf(length),
                                    degree
                                ))

                                if(isBatting){
                                    homeRuns += if(x == -1) 0 else x
                                    if(x == -1){
                                        homeWickets++
                                    }
                                    homeBalls++
                                } else {
                                    awayRuns += if(x == -1) 0 else x
                                    if(x== -1){
                                        awayWickets++
                                    }
                                    awayBalls++
                                }
                            },
                            modifier = Modifier
                                .padding(12.dp)
                                .fillMaxWidth(),
                            shape = RoundedCornerShape(32.dp)
                        ){
                            Text("Done")
                        }
                    }
                }
            }

        }
    }
}

@PreviewDevices
@Composable
fun DefaultPreview() {
    IPLCricketScoreGeneratorTheme {
        InitialScreen()
    }
}