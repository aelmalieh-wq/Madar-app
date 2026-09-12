package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.HeadsetMic
import androidx.compose.material.icons.filled.QuestionAnswer
import androidx.compose.material.icons.filled.SupportAgent
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.model.AppSettings
import com.example.data.model.SupportTicket
import com.example.ui.theme.MadarEmeraldPrimary
import com.example.ui.theme.MadarNavy
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun SupportScreen(
    tickets: List<SupportTicket>,
    settings: AppSettings,
    onSubmitTicket: (subject: String, category: String, priority: String, message: String) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val isArabic = settings.language == "ar"
    var showNewTicketDialog by remember { mutableStateOf(false) }

    val faqs = listOf(
        Pair(
            if (isArabic) "كيف يتم احتساب العمولة وتحديث الأسعار؟" else "How is commission calculated and prices updated?",
            if (isArabic)
                "يتم ربط الأسعار تلقائياً بمتجر مدار الإلكتروني. الفرق بين سعر بيعك للعميل وسعر تكلفة مدار هو عمولتك الصافية. في حال تغيرت الأسعار بالمتجر يرسل التطبيق إشعاراً فورياً."
            else
                "Prices are automatically connected to Madar's live catalog. The difference between your selling price and base cost is your net commission."
        ),
        Pair(
            if (isArabic) "متى تضاف العمولة إلى رصيدي القابل للسحب؟" else "When is commission available for withdrawal?",
            if (isArabic)
                "بمجرد تسليم الشحنة للعميل بنجاح وتحصيل المبلغ، تتغير حالة الطلب إلى (تم التسليم) وتنتقل العمولة فوراً للرصيد المتاح للسحب."
            else
                "Once the package is delivered and payment collected, the order status changes to Delivered and profit is immediately withdrawable."
        ),
        Pair(
            if (isArabic) "ما هي طرق سحب الأرباح المتاحة؟" else "What payout methods are supported?",
            if (isArabic)
                "نوفر السحب عبر التحويل البنكي الفوري الآيبان، شبكة InstaPay، ومحفظة STC Pay، بدون أي رسوم تحويل إضافية لشركاء مدار."
            else
                "We support direct bank wires (IBAN), InstaPay, and STC Pay with 0 fees for Madar partners."
        )
    )

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 12.dp, bottom = 90.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Direct Help Channels Banner
        item {
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MadarNavy),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(Color.White.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.SupportAgent, contentDescription = null, tint = Color.White)
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = if (isArabic) "فريق دعم شركاء مدار (24/7)" else "Madar Partner Support (24/7)",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Text(
                                text = if (isArabic) "جاهزون لمساعدتك في كل خطوة ومتابعة الشحنات والعمولات" else "Here to assist with deliveries, payouts, and margins",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.White.copy(alpha = 0.8f),
                                fontSize = 11.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Button(
                            onClick = {
                                val url = "https://api.whatsapp.com/send?phone=966500000000&text=${Uri.encode("مرحباً، أنا شريك تسويق لدى مدار وأحتاج مساعدة:")}"
                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                                context.startActivity(intent)
                            },
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = MadarEmeraldPrimary),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Default.QuestionAnswer, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(text = if (isArabic) "واتساب مدار" else "WhatsApp", fontSize = 12.sp)
                        }

                        Button(
                            onClick = { showNewTicketDialog = true },
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color.White.copy(alpha = 0.2f)),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("open_new_ticket_button")
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp), tint = Color.White)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(text = if (isArabic) "فتح تذكرة دعم" else "New Ticket", color = Color.White, fontSize = 12.sp)
                        }
                    }
                }
            }
        }

        // Support Tickets List
        item {
            Text(
                text = if (isArabic) "تذاكر الدعم الفني المفتوحة" else "Your Support Tickets",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }

        if (tickets.isEmpty()) {
            item {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.surface,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = if (isArabic) "لا توجد تذاكر دعم مفتوحة لديك حالياً." else "No open tickets.",
                        modifier = Modifier.padding(16.dp),
                        color = MaterialTheme.colorScheme.outline
                    )
                }
            }
        } else {
            items(tickets, key = { it.id }) { ticket ->
                SupportTicketCard(ticket = ticket, isArabic = isArabic)
            }
        }

        // FAQs Section
        item {
            Text(
                text = if (isArabic) "الأسئلة الأكثر شيوعاً حول عمولات مدار" else "Frequently Asked Questions",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }

        items(faqs) { (question, answer) ->
            FaqAccordionItem(question = question, answer = answer)
        }
    }

    if (showNewTicketDialog) {
        CreateTicketDialog(
            isArabic = isArabic,
            onDismiss = { showNewTicketDialog = false },
            onSubmit = { subject, category, priority, message ->
                onSubmitTicket(subject, category, priority, message)
                showNewTicketDialog = false
            }
        )
    }
}

@Composable
fun SupportTicketCard(
    ticket: SupportTicket,
    isArabic: Boolean
) {
    val dateFormat = SimpleDateFormat("dd/MM/yyyy - hh:mm a", Locale.getDefault())
    val formattedDate = dateFormat.format(Date(ticket.createdAt))

    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = ticket.ticketNumber, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleSmall)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = ticket.category, fontSize = 11.sp, color = MaterialTheme.colorScheme.primary)
                }

                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = MadarEmeraldPrimary.copy(alpha = 0.15f)
                ) {
                    Text(
                        text = ticket.status,
                        color = MadarEmeraldPrimary,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = ticket.subject,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = ticket.message,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            if (ticket.adminReply.isNotEmpty()) {
                Spacer(modifier = Modifier.height(10.dp))
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Text(
                            text = if (isArabic) "رد فريق دعم مدار:" else "Madar Support Reply:",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = ticket.adminReply,
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun FaqAccordionItem(
    question: String,
    answer: String
) {
    var expanded by remember { mutableStateOf(false) }

    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { expanded = !expanded }
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = question,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 13.sp,
                    modifier = Modifier.weight(1f)
                )
                Icon(
                    imageVector = if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                    contentDescription = null
                )
            }
            AnimatedVisibility(visible = expanded) {
                Column {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = answer,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        lineHeight = 18.sp
                    )
                }
            }
        }
    }
}

@Composable
fun CreateTicketDialog(
    isArabic: Boolean,
    onDismiss: () -> Unit,
    onSubmit: (subject: String, category: String, priority: String, message: String) -> Unit
) {
    var subject by remember { mutableStateOf("") }
    var category by remember { mutableStateOf(if (isArabic) "استفسار عن عمولة" else "Commission Inquiry") }
    var priority by remember { mutableStateOf("NORMAL") }
    var message by remember { mutableStateOf("") }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 6.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (isArabic) "فتح تذكرة دعم جديدة" else "Open Support Ticket",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close")
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                OutlinedTextField(
                    value = subject,
                    onValueChange = { subject = it },
                    label = { Text(if (isArabic) "عنوان التذكرة / المشكلة" else "Ticket Subject") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = category,
                    onValueChange = { category = it },
                    label = { Text(if (isArabic) "التصنيف (عمولة، شحن، فني...)" else "Category") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = message,
                    onValueChange = { message = it },
                    label = { Text(if (isArabic) "تفاصيل المشكلة أو الطلب" else "Message Details") },
                    maxLines = 4,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(18.dp))

                Button(
                    onClick = {
                        if (subject.isNotBlank() && message.isNotBlank()) {
                            onSubmit(subject, category, priority, message)
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(46.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MadarEmeraldPrimary),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(text = if (isArabic) "إرسال التذكرة لفريق مدار" else "Submit Ticket")
                }
            }
        }
    }
}
