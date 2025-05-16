package com.example.setoranhafalan.ui.dashboard

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.setoranhafalanapp.ui.login.LoginViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(navController: NavController) {
    val context = LocalContext.current
    val dashboardViewModel: DashboardViewModel = viewModel(factory = DashboardViewModel.getFactory(context))
    val loginViewModel: LoginViewModel = viewModel(factory = LoginViewModel.getFactory(context))
    val dashboardState by dashboardViewModel.dashboardState.collectAsState()
    val userName by dashboardViewModel.userName.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        dashboardViewModel.fetchSetoranSaya()
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("Dashboard Setoran", color = Color.White) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent
                ),
                modifier = Modifier.background(
                    Brush.horizontalGradient(
                        colors = listOf(Color(0xFF1976D2), Color(0xFF42A5F5))
                    )
                ),
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                },
                actions = {
                    TextButton(onClick = {
                        loginViewModel.logout()
                        navController.navigate("login") {
                            popUpTo("dashboard") { inclusive = true }
                        }
                    }) {
                        Text("Logout", color = Color.White)
                    }
                }
            )
        },
        content = { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFFF5F5F5))
                    .padding(padding)
                    .padding(16.dp)
            ) {
                userName?.let {
                    Text(
                        text = "Selamat datang, $it!",
                        style = MaterialTheme.typography.headlineSmall,
                        color = Color(0xFF1976D2),
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }
                when (val state = dashboardState) {
                    is DashboardState.Loading -> {
                        CircularProgressIndicator(
                            modifier = Modifier.align(Alignment.CenterHorizontally)
                        )
                    }
                    is DashboardState.Success -> {
                        val data = state.data.data
                        // Log data untuk debugging
                        Log.d("DashboardScreen", "Data: $data")
                        if (data == null) {
                            Text(
                                text = "Data tidak tersedia",
                                color = Color.Red,
                                modifier = Modifier.align(Alignment.CenterHorizontally)
                            )
                            return@Column
                        }

                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            shape = RoundedCornerShape(12.dp),
                            elevation = CardDefaults.cardElevation(4.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White)
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp)
                            ) {
                                Text(
                                    text = "Informasi Pribadi",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = Color(0xFF1976D2)
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "Nama: ${data.info?.nama ?: "Tidak tersedia"}",
                                    style = MaterialTheme.typography.bodyMedium
                                )
                                Text(
                                    text = "NIM: ${data.info?.nim ?: "Tidak tersedia"}",
                                    style = MaterialTheme.typography.bodyMedium
                                )
                                Text(
                                    text = "Email: ${data.info?.email ?: "Tidak tersedia"}",
                                    style = MaterialTheme.typography.bodyMedium
                                )
                                Text(
                                    text = "Angkatan: ${data.info?.angkatan ?: "Tidak tersedia"}",
                                    style = MaterialTheme.typography.bodyMedium
                                )
                                Text(
                                    text = "Semester: ${data.info?.semester ?: "Tidak tersedia"}",
                                    style = MaterialTheme.typography.bodyMedium
                                )
                                Text(
                                    text = "Dosen Pembimbing: ${data.info?.dosen_pa?.nama ?: "Tidak tersedia"}",
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            shape = RoundedCornerShape(12.dp),
                            elevation = CardDefaults.cardElevation(4.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White)
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp)
                            ) {
                                Text(
                                    text = "Progres Setoran",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = Color(0xFF1976D2)
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                val progress = data.setoran?.info_dasar?.persentase_progres_setor?.toFloat() ?: 0f
                                LinearProgressIndicator(
                                    progress = { progress / 100 },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(10.dp),
                                    color = Color(0xFF4CAF50),
                                    trackColor = Color(0xFFE0E0E0)
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "${progress.toInt()}%",
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = Color(0xFF4CAF50),
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Daftar Setoran",
                            style = MaterialTheme.typography.titleMedium,
                            color = Color(0xFF1976D2)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        val setoranDetail = data.setoran?.detail
                        if (setoranDetail.isNullOrEmpty()) {
                            Text(
                                text = "Belum ada setoran",
                                color = Color.Gray,
                                modifier = Modifier.align(Alignment.CenterHorizontally)
                            )
                        } else {
                            LazyColumn {
                                itemsIndexed(setoranDetail) { index, item ->
                                    Card(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 4.dp),
                                        shape = RoundedCornerShape(8.dp),
                                        elevation = CardDefaults.cardElevation(2.dp),
                                        colors = CardDefaults.cardColors(containerColor = Color.White)
                                    ) {
                                        Row(
                                            modifier = Modifier
                                                .padding(12.dp)
                                                .fillMaxWidth(),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Text(
                                                text = "${index + 1}. ${item.nama}",
                                                style = MaterialTheme.typography.bodyLarge,
                                                color = Color(0xFF212121)
                                            )
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                if (item.sudah_setor) {
                                                    Icon(
                                                        imageVector = Icons.Filled.CheckCircle,
                                                        contentDescription = "Sudah",
                                                        tint = Color(0xFF4CAF50),
                                                        modifier = Modifier.size(20.dp)
                                                    )
                                                }
                                                Spacer(modifier = Modifier.width(4.dp))
                                                Text(
                                                    text = if (item.sudah_setor) "Sudah" else "Belum",
                                                    style = MaterialTheme.typography.bodyMedium,
                                                    color = if (item.sudah_setor) Color(0xFF4CAF50) else Color(0xFF757575)
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                    is DashboardState.Error -> {
                        LaunchedEffect(dashboardState) {
                            scope.launch {
                                snackbarHostState.showSnackbar(state.message)
                            }
                        }
                    }
                    else -> {}
                }
            }
        }
    )
}