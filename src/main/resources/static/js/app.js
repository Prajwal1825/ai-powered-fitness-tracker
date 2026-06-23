// let currentUser = JSON.parse(localStorage.getItem('fitUser') || 'null');
// function $(id){return document.getElementById(id)}
// function showAuth(type){document.querySelectorAll('.tab').forEach(t=>t.classList.remove('active'));event.target.classList.add('active');$('loginForm').classList.toggle('hidden',type!=='login');$('registerForm').classList.toggle('hidden',type!=='register')}
// async function post(url,data){const r=await fetch(url,{method:'POST',headers:{'Content-Type':'application/json'},body:JSON.stringify(data)});return r.json()}
// $('registerForm').addEventListener('submit',async e=>{e.preventDefault();let res=await post('/api/register',{name:$('regName').value,email:$('regEmail').value,password:$('regPassword').value,weight:parseFloat($('regWeight').value||0),goal:$('regGoal').value});$('regMsg').innerText=res.message;if(res.success){currentUser=res.user;localStorage.setItem('fitUser',JSON.stringify(currentUser));openApp();}});
// $('loginForm').addEventListener('submit',async e=>{e.preventDefault();let res=await post('/api/login',{email:$('loginEmail').value,password:$('loginPassword').value});$('loginMsg').innerText=res.message;if(res.success){currentUser=res.user;localStorage.setItem('fitUser',JSON.stringify(currentUser));openApp();}});
// function openApp(){if(!currentUser)return;$('authPage').classList.add('hidden');$('appPage').classList.remove('hidden');$('sideUser').innerText=currentUser.name;$('welcomeText').innerText='Hello, '+currentUser.name;$('pName').innerText=currentUser.name;$('pEmail').innerText=currentUser.email;$('pWeight').innerText=currentUser.weight||'-';$('pGoal').innerText=currentUser.goal||'-';refreshDashboard();}
// function logout(){localStorage.removeItem('fitUser');location.reload()}
// function showSection(id){document.querySelectorAll('.section').forEach(s=>s.classList.remove('active-section'));$(id).classList.add('active-section');if(id==='tips')loadTips();}
// async function refreshDashboard(){let r=await fetch('/api/dashboard/'+currentUser.id);let d=await r.json(); console.log("Dashboard API response:", d);$('target').innerText=d.target;$('consumed').innerText=d.consumed;$('burned').innerText=d.burned;$('remaining').innerText=d.remaining;let pct=Math.min(100,Math.round((d.consumed/d.target)*100));$('progressBar').style.width=pct+'%';$('progressText').innerText=pct+'% of calorie target consumed';$('bar1').style.height=Math.max(20,d.target/25)+'px';$('bar2').style.height=Math.max(20,d.consumed/25)+'px';$('bar3').style.height=Math.max(20,d.burned/8)+'px';$('foodRows').innerHTML=(d.foods||[]).slice(0,6).map(f=>`<tr><td>${f.foodName}</td><td>${f.calories} cal</td><td>${f.source}</td></tr>`).join('')||'<tr><td>No foods yet</td></tr>';$('workoutRows').innerHTML=(d.workouts||[]).slice(0,6).map(w=>`<tr><td>${w.workoutName}</td><td>${w.durationMinutes} min</td><td>${w.caloriesBurned} cal</td></tr>`).join('')||'<tr><td>No workouts yet</td></tr>';renderDashboardTips(d.tips||[]);}
// function renderDashboardTips(tips){$('dashboardTips').innerHTML=(tips||[]).slice(0,3).map(t=>`<li>${t}</li>`).join('')||'<li>Add goals, food, and workouts to get personalized tips.</li>';}
// async function loadTips(){let r=await fetch('/api/health-tips/'+currentUser.id);let d=await r.json();$('tipsMode').innerText=d.mode+' - '+d.summary;$('tipsList').innerHTML=(d.tips||[]).map(t=>`<li>${t}</li>`).join('');renderDashboardTips(d.tips||[]);}
// async function saveGoal(){await post('/api/goals',{userId:currentUser.id,caloriesGoal:+$('goalCalories').value,stepsGoal:+$('goalSteps').value,workoutGoal:+$('goalWorkout').value,waterGoal:+$('goalWater').value});alert('Goal saved');showSection('dashboard');refreshDashboard()}
// async function saveFood(){await post('/api/foods',{userId:currentUser.id,foodName:$('foodName').value,quantity:$('quantity').value,calories:+$('calories').value,protein:+$('protein').value,fat:+$('fat').value,source:'Manual'});alert('Food added');showSection('dashboard');refreshDashboard()}
// // async function saveWorkout(){await post('/api/workouts',{userId:currentUser.id,workoutName:$('workoutName').value,durationMinutes:+$('duration').value,caloriesBurned:+$('burnedInput').value});alert('Workout added');showSection('dashboard');refreshDashboard()}
// // async function saveWorkout(){
// //   await post('/api/workouts',{
// //     userId:currentUser.id,
// //     workoutName:$('workoutName').value,
// //     durationMinutes:+$('duration').value
// //   });

// //   alert('Workout added');
// //   showSection('dashboard');
// //   refreshDashboard();
// // }
// // async function saveWorkout(){
// //   await post('/api/workouts',{
// //     userId:currentUser.id,
// //     workoutName:$('workoutName').value,
// //     durationMinutes:+$('duration').value
// //   });

// //   alert('Workout added');

// //   showSection('dashboard');

// //   await refreshDashboard(); // 🔥 IMPORTANT FIX
// // }
// async function saveWorkout(){
//   const res = await post('/api/workouts',{
//     userId: currentUser.id,
//     workoutName: $('workoutName').value,
//     durationMinutes: Number($('duration').value)
//   });

//   console.log("Workout saved:", res);

//   alert("Workout added");

//   await refreshDashboard();  // VERY IMPORTANT
// }
// async function analyzeImage(){let file=$('foodImage').files[0];if(!file){alert('Choose image first');return}let fd=new FormData();fd.append('userId',currentUser.id);fd.append('image',file);let r=await fetch('/api/ai/analyze',{method:'POST',body:fd});let d=await r.json();$('aiResult').innerText=JSON.stringify(d,null,2);refreshDashboard()}
// if(currentUser) openApp();



console.log("app.js loaded successfully");

let currentUser = JSON.parse(localStorage.getItem('fitUser') || 'null');

function $(id) {
  return document.getElementById(id);
}

function showAuth(type) {
  document.querySelectorAll('.tab').forEach(t => t.classList.remove('active'));
  event.target.classList.add('active');
  $('loginForm').classList.toggle('hidden', type !== 'login');
  $('registerForm').classList.toggle('hidden', type !== 'register');
}

async function post(url, data) {
  const r = await fetch(url, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(data)
  });
  return r.json();
}

/* REGISTER */
$('registerForm').addEventListener('submit', async e => {
  e.preventDefault();
  let res = await post('/api/register', {
    name: $('regName').value,
    email: $('regEmail').value,
    password: $('regPassword').value,
    weight: parseFloat($('regWeight').value || 0),
    goal: $('regGoal').value
  });

  $('regMsg').innerText = res.message;

  if (res.success) {
    currentUser = res.user;
    localStorage.setItem('fitUser', JSON.stringify(currentUser));
    openApp();
  }
});

/* LOGIN */
$('loginForm').addEventListener('submit', async e => {
  e.preventDefault();
  let res = await post('/api/login', {
    email: $('loginEmail').value,
    password: $('loginPassword').value
  });

  $('loginMsg').innerText = res.message;

  if (res.success) {
    currentUser = res.user;
    localStorage.setItem('fitUser', JSON.stringify(currentUser));
    openApp();
  }
});

function openApp() {
  if (!currentUser) return;

  $('authPage').classList.add('hidden');
  $('appPage').classList.remove('hidden');

  $('sideUser').innerText = currentUser.name;
  $('welcomeText').innerText = 'Hello, ' + currentUser.name;
  $('pName').innerText = currentUser.name;
  $('pEmail').innerText = currentUser.email;
  $('pWeight').innerText = currentUser.weight || '-';
  $('pGoal').innerText = currentUser.goal || '-';

  refreshDashboard();
}

function logout() {
  localStorage.removeItem('fitUser');
  location.reload();
}

function showSection(id) {
  document.querySelectorAll('.section').forEach(s => s.classList.remove('active-section'));
  $(id).classList.add('active-section');

  if (id === 'tips') loadTips();
}

/* DASHBOARD */
async function refreshDashboard() {
  let r = await fetch('/api/dashboard/' + currentUser.id);
  let d = await r.json();

  console.log("Dashboard API response:", d);

  $('target').innerText = d.target;
  $('consumed').innerText = d.consumed;
  $('burned').innerText = d.burned;
  $('remaining').innerText = d.remaining;

  let pct = Math.min(100, Math.round((d.consumed / d.target) * 100));
  $('progressBar').style.width = pct + '%';
  $('progressText').innerText = pct + '% of calorie target consumed';

  $('foodRows').innerHTML =
    (d.foods || []).slice(0, 6).map(f =>
      `<tr><td>${f.foodName}</td><td>${f.calories} cal</td><td>${f.source}</td></tr>`
    ).join('') || '<tr><td>No foods yet</td></tr>';

  $('workoutRows').innerHTML =
    (d.workouts || []).slice(0, 6).map(w =>
      `<tr><td>${w.workoutName}</td><td>${w.durationMinutes} min</td><td>${w.caloriesBurned || 0} cal</td></tr>`
    ).join('') || '<tr><td>No workouts yet</td></tr>';

  renderDashboardTips(d.tips || []);
}

function renderDashboardTips(tips) {
  $('dashboardTips').innerHTML =
    (tips || []).slice(0, 3).map(t => `<li>${t}</li>`).join('')
    || '<li>Add goals, food, and workouts to get tips.</li>';
}

/* TIPS */
async function loadTips() {
  let r = await fetch('/api/health-tips/' + currentUser.id);
  let d = await r.json();

  $('tipsMode').innerText = d.mode + ' - ' + d.summary;
  $('tipsList').innerHTML = (d.tips || []).map(t => `<li>${t}</li>`).join('');

  renderDashboardTips(d.tips || []);
}

/* GOAL */
async function saveGoal() {
  await post('/api/goals', {
    userId: currentUser.id,
    caloriesGoal: +$('goalCalories').value,
    stepsGoal: +$('goalSteps').value,
    workoutGoal: +$('goalWorkout').value,
    waterGoal: +$('goalWater').value
  });

  alert('Goal saved');
  showSection('dashboard');
  refreshDashboard();
}

/* FOOD */
async function saveFood() {
  await post('/api/foods', {
    userId: currentUser.id,
    foodName: $('foodName').value,
    quantity: $('quantity').value,
    calories: +$('calories').value,
    protein: +$('protein').value,
    fat: +$('fat').value,
    source: 'Manual'
  });

  alert('Food added');
  showSection('dashboard');
  refreshDashboard();
}

/* WORKOUT (FIXED) */
// async function saveWorkout() {
//   const name = document.getElementById('workoutName');
//   const duration = document.getElementById('duration');

//   if (!name || !duration) {
//     alert("Workout inputs not found");
//     return;
//   }

//   const res = await post('/api/workouts', {
//     userId: currentUser.id,
//     workoutName: name.value,
//     durationMinutes: Number(duration.value)
//   });

//   console.log("Workout saved:", res);

//   alert("Workout added");

//   await refreshDashboard();
// }

async function saveWorkout() {

  console.log("saveWorkout clicked");

  const name = document.getElementById("workoutName");
  const duration = document.getElementById("duration");

  console.log("name:", name);
  console.log("duration:", duration);

  if (!name || !duration) {
    alert("Workout input fields NOT found in page");
    return;
  }

  if (!name.value || !duration.value) {
    alert("Please enter workout name and duration");
    return;
  }

  const res = await post("/api/workouts", {
    userId: currentUser.id,
    workoutName: name.value,
    durationMinutes: Number(duration.value)
  });

  console.log("Workout saved:", res);

  alert("Workout added successfully");

  await refreshDashboard();
}

/* AI */
async function analyzeImage() {
  let file = $('foodImage').files[0];
  if (!file) {
    alert('Choose image first');
    return;
  }

  let fd = new FormData();
  fd.append('userId', currentUser.id);
  fd.append('image', file);

  let r = await fetch('/api/ai/analyze', {
    method: 'POST',
    body: fd
  });

  let d = await r.json();
  $('aiResult').innerText = JSON.stringify(d, null, 2);

  refreshDashboard();
}

if (currentUser) openApp();
