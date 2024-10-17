package com.example.lr4

import android.content.Context
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import com.example.lr4.ui.theme.Lr4Theme
import java.io.File

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Main()
        }
    }
}

@Composable
fun Main() {
    val navController = rememberNavController()
    Column(Modifier.padding(8.dp)) {
        NavHost(
            navController, startDestination = NavRoutes.Home.route, modifier
            = Modifier.weight(1f)
        ) {
            composable(NavRoutes.Home.route) { Greeting() }
            composable(NavRoutes.Lists.route) { Lists() }
            composable(NavRoutes.Imgs.route) { Imgs() }
            composable(NavRoutes.Picks.route) { Picks() }
        }
        BottomNavigationBar(navController = navController)
    }
}

@Composable
fun Greeting(modifier: Modifier = Modifier) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFDBD6F3)),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        var textValue by rememberSaveable { mutableStateOf("") }
        val MyName by remember { mutableStateOf("Демидовец Владислава Валерьевна") }
        Text(
            text = textValue,
            modifier = modifier.padding(3.dp),
            fontSize = 20.sp
        )
        Row(
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Button(
                onClick = {
                    textValue = MyName
                },
                shape = RoundedCornerShape(50),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2C9393)),
                modifier = Modifier.padding(end = 8.dp)
            ) {
                Text(text = "Вывести имя")
            }

            Button(
                onClick = {
                    textValue = ""
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2C9393)),
                shape = RoundedCornerShape(50),
                contentPadding = PaddingValues(0.dp), // внутренний отступ
                modifier = Modifier
                    .padding(8.dp) // внешний отступ
                    .size(40.dp)
            ) {
                Text(text = "X", fontSize = 16.sp)
            }
        }
    }
}

@Composable
@OptIn(ExperimentalFoundationApi::class)
fun Lists(modifier: Modifier = Modifier) {
    // Исходные данные
    val books = listOf(
        Book("George Orwell", "1984"),
        Book("George Orwell", "Animal Farm"),
        Book("J.K. Rowling", "Harry Potter and the Philosopher's Stone"),
        Book("J.K. Rowling", "Harry Potter and the Chamber of Secrets"),
        Book("Isaac Asimov", "Foundation"),
        Book("Isaac Asimov", "I, Robot")
    )

    // Группируем книги по авторам
    val groupedBooks = books.groupBy { it.author }

    LazyColumn(
        contentPadding = PaddingValues(5.dp),
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFDBD6F3))
    ) {
        groupedBooks.forEach { (author, booksByAuthor) ->
            stickyHeader {
                Text(
                    text = author,
                    fontSize = 28.sp,
                    color = Color.White,
                    modifier = Modifier
                        .background(Color.Gray)
                        .padding(5.dp)
                        .fillMaxWidth()
                )
            }
            items(booksByAuthor.size) { index ->
                val book = booksByAuthor[index]
                Text(
                    text = book.name,
                    modifier = Modifier.padding(5.dp),
                    fontSize = 20.sp
                )
            }
        }
    }
}

data class Book(val author: String, val name: String)

@Composable
fun Imgs(modifier: Modifier = Modifier) {
    val configuration = LocalConfiguration.current
    val isLandscape =
        configuration.orientation == Configuration.ORIENTATION_LANDSCAPE // Проверка на альбомную ориентацию

    // Установка высоты изображения и начальных/конечных отступов в зависимости от ориентации
    val imageHeight = if (isLandscape) 120 else 360 // измените высоту для альбомной ориентации
    val imageWidth = imageHeight
    val startOffset = 10 // начальный отступ
    val endOffset = if (isLandscape) {
        configuration.screenWidthDp - imageWidth - 10 // предельная позиция для альбомной ориентации
    } else {
        configuration.screenHeightDp - imageHeight // предельная позиция для портретной ориентации
    }


    var imageOffset by remember { mutableStateOf(startOffset) } // начальное положение
    val offset: Dp by animateDpAsState(
        targetValue = imageOffset.dp,
        animationSpec = if (imageOffset == endOffset) {
            spring(dampingRatio = 0.3f)
        } else {
            spring(dampingRatio = 1.0f) // отсутствие отскока
        }
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFDBD6F3)),
    ) {
        Image(
            painter = painterResource(id = R.drawable.leon),
            contentScale = ContentScale.Crop,
            contentDescription = "My pet",
            modifier = Modifier
                .padding(
                    start = if (isLandscape) offset else 0.dp, // Применяем смещение по оси X в альбомной ориентации
                    top = if (!isLandscape) offset else 0.dp // Применяем смещение по оси Y в портретной ориентации
                )
                .size(imageHeight.dp)
                .clip(CircleShape) // форма овала
                .align(if (isLandscape) Alignment.CenterStart else Alignment.Center) // размещаем изображение по центру
        )


        Button(
            onClick = {
                imageOffset = if (imageOffset == startOffset) endOffset else startOffset
            },
            modifier = Modifier
                .padding(16.dp)
                .align(Alignment.TopCenter) // фиксируем кнопку в верхней части экрана
        ) {
            Text("Start", fontSize = 22.sp)
        }
    }
}

class ComposeFileProvider : FileProvider(R.xml.files_paths) {
    companion object {
        fun getImageUri(context: Context): Uri {
            val directory = File(context.cacheDir, "images")
            directory.mkdirs()
            val file = File.createTempFile(
                "selected_image_",
                ".jpg",
                directory,
            )
            val authority = context.packageName + ".fileprovider"
            return getUriForFile(
                context,
                authority,
                file,
            )
        }
    }
}


@Composable
fun Picks(modifier: Modifier = Modifier) {
    var imageUri by rememberSaveable { mutableStateOf<Uri?>(null) }
    var hasImage by rememberSaveable { mutableStateOf(false) }
    var currentUri by rememberSaveable { mutableStateOf<Uri?>(null) }
    val context = LocalContext.current


    val imagePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri: Uri? ->
            hasImage = uri != null
            imageUri = uri
        }
    )
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture(),
        onResult = { success ->
            hasImage = success
            if (success) {
                imageUri = currentUri
            }
        }
    )

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission(),
        onResult ={ isGranted ->
            if (isGranted) {
                Toast.makeText(context, "Разрешение получено", Toast.LENGTH_SHORT).show()
// Запуск камеры с текущим Uri
                currentUri?.let { cameraLauncher.launch(it) }
            } else {
                Toast.makeText(context,"В разрешении отказано",Toast.LENGTH_SHORT).show()
            }
        }
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFDBD6F3)),
    ) {
        if (hasImage && imageUri != null) {
            AsyncImage(
                model = imageUri,
                modifier = Modifier.fillMaxWidth().align(Alignment.Center),
                contentDescription = "Selected Image"
            )
        }
        Column(modifier=Modifier.align(Alignment.BottomCenter).padding(bottom = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,) {
            Button(onClick = { imagePicker.launch("image/*") },) {
                Text(text = "Выбрать изображение")
            }

            Button(modifier = Modifier.padding(top = 16.dp),
                onClick = {
                    // Создаем новый Uri для каждого снимка
                    currentUri = ComposeFileProvider.getImageUri(context)
                    val permissionCheckResult = ContextCompat.checkSelfPermission(
                        context,android.Manifest.permission.CAMERA)
                    if (permissionCheckResult == PackageManager.PERMISSION_GRANTED) {
                        cameraLauncher.launch(currentUri!!) }
                    else {permissionLauncher.launch(android.Manifest.permission.CAMERA) }
                }
            ) {
                Text(text = "Сделать снимок")
            }
        }
    }

}

@Composable
fun BottomNavigationBar(navController: NavController) {
    NavigationBar {
        val backStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = backStackEntry?.destination?.route
        NavBarItems.BarItems.forEach { navItem ->
            NavigationBarItem(
                selected = currentRoute == navItem.route,
                onClick = {
                    navController.navigate(navItem.route) {
                        popUpTo(navController.graph.findStartDestination().id)
                        { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                icon = {
                    Icon(
                        imageVector = navItem.image,
                        contentDescription = navItem.title
                    )
                },
                label = {
                    Text(text = navItem.title)
                }
            )
        }
    }
}

object NavBarItems {
    val BarItems = listOf(
        BarItem(
            title = "Home",
            image = Icons.Filled.Home,
            route = "home"
        ),
        BarItem(
            title = "Lists",
            image = Icons.Filled.Menu,
            route = "lists"
        ),
        BarItem(
            title = "Imgs",
            image = Icons.Filled.Face,
            route = "imgs"
        ),
        BarItem(
            title = "Picks",
            image = Icons.Filled.CheckCircle,
            route = "picks"
        )
    )
}

data class BarItem(
    val title: String,
    val image: ImageVector,
    val route: String
)

sealed class NavRoutes(val route: String) {
    object Home : NavRoutes("home")
    object Lists : NavRoutes("lists")
    object Imgs : NavRoutes("imgs")
    object Picks : NavRoutes("picks")
}


@Preview(showBackground = true)
@Composable
fun MainPreview() {
    Lr4Theme {
        Picks()
    }
}