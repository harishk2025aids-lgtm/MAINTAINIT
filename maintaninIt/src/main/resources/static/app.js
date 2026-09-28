const API = {
  machines: '/api/machines',
  technicians: '/api/technicians',
  tasks: '/api/maintenance-tasks',
  usageLogs: '/api/usage-logs',
  notifications: '/api/dashboard/notifications'
};

const page = document.body.dataset.page;
const state = { machines: [], technicians: [], tasks: [], usageLogs: [] };

async function fetchJson(url, options = {}) {
  const response = await fetch(url, { headers: { 'Content-Type': 'application/json' }, ...options });
  if (!response.ok) throw new Error((await response.text()) || `Request failed (${response.status})`);
  return response.status === 204 ? null : response.json();
}

function escapeHtml(value) {
  return String(value ?? '—').replace(/[&<>'"]/g, character => ({ '&': '&amp;', '<': '&lt;', '>': '&gt;', "'": '&#39;', '"': '&quot;' }[character]));
}

function nav() {
  const links = [['/', 'Overview', 'dashboard'], ['/machines.html', 'Machines', 'machines'], ['/technicians.html', 'Technicians', 'technicians'], ['/tasks.html', 'Maintenance', 'tasks'], ['/usage-logs.html', 'Usage logs', 'usage']];
  document.getElementById('appNav').innerHTML = links.map(([href, label, key]) => `<a class="nav-link ${page === key ? 'active' : ''}" href="${href}">${label}</a>`).join('');
}

function layout(title, eyebrow, action = '') {
  document.getElementById('pageContent').innerHTML = `<div class="page-heading"><div><p class="eyebrow">${eyebrow}</p><h1>${title}</h1></div>${action}</div><div id="pageBody"></div>`;
}

function options(items, label) {
  return items.map(item => `<option value="${item.id}">${escapeHtml(label(item))}</option>`).join('');
}

function statusBadge(value) {
  return `<span class="badge badge-${String(value).toLowerCase().replaceAll('_', '-')}">${escapeHtml(value)}</span>`;
}

function actionButtons(type, item) {
  const buttons = [`<button class="button button-small" data-action="edit" data-type="${type}" data-id="${item.id}">Edit</button>`, `<button class="button button-small button-danger" data-action="delete" data-type="${type}" data-id="${item.id}">Delete</button>`];
  if (type === 'task' && ['OPEN', 'OVERDUE'].includes(item.status)) buttons.unshift(`<button class="button button-small button-dark" data-action="start" data-id="${item.id}">Start</button>`);
  if (type === 'task' && item.status === 'IN_PROGRESS') buttons.unshift(`<button class="button button-small button-success" data-action="complete" data-id="${item.id}">Complete</button>`);
  return `<div class="row-actions">${buttons.join('')}</div>`;
}

function table(headers, rows, empty = 'No records yet.') {
  return `<div class="table-wrap"><table><thead><tr>${headers.map(header => `<th>${header}</th>`).join('')}</tr></thead><tbody>${rows || `<tr><td colspan="${headers.length}" class="empty">${empty}</td></tr>`}</tbody></table></div>`;
}

async function loadState() {
  const [machines, technicians, tasks, usageLogs] = await Promise.all([
    fetchJson(API.machines), fetchJson(API.technicians), fetchJson(API.tasks), fetchJson(API.usageLogs)
  ]);
  Object.assign(state, { machines, technicians, tasks, usageLogs });
}

function formField(label, id, type = 'text', value = '', required = false, extra = '') {
  return `<label>${label}<input id="${id}" type="${type}" value="${escapeHtml(value)}" ${required ? 'required' : ''} ${extra}></label>`;
}

function machineForm(machine = {}) {
  return `<form id="machineForm" class="form-grid" data-id="${machine.id || ''}">
    ${formField('Machine name', 'machineName', 'text', machine.name, true)}
    ${formField('Machine code', 'machineCode', 'text', machine.machineCode, true)}
    ${formField('Maintenance interval (hours)', 'maintenanceInterval', 'number', machine.maintenanceIntervalHours, true, 'min="1"')}
    ${formField('Current usage (hours)', 'currentUsage', 'number', machine.currentUsageHours || 0, true, 'min="0"')}
    ${formField('Last service at (hours)', 'lastMaintenance', 'number', machine.lastMaintenanceUsageHours || 0, true, 'min="0"')}
    <label>Status<select id="machineStatus"><option>ACTIVE</option><option>INACTIVE</option><option>UNDER_MAINTENANCE</option></select></label>
    <label class="wide">Description<textarea id="machineDescription">${escapeHtml(machine.description || '')}</textarea></label>
    <div class="form-actions wide"><button class="button button-primary" type="submit">${machine.id ? 'Update machine' : 'Add machine'}</button>${machine.id ? '<button class="button" type="button" data-action="cancel-form">Cancel</button>' : ''}</div>
  </form>`;
}

async function renderMachines() {
  layout('Machines', 'Asset register', '<a class="button button-primary" href="#new">+ Add machine</a>');
  const rows = state.machines.map(machine => `<tr><td>#${machine.id}</td><td><strong>${escapeHtml(machine.name)}</strong><small>${escapeHtml(machine.description || 'No description')}</small></td><td>${escapeHtml(machine.machineCode)}</td><td>${statusBadge(machine.status)}</td><td>${machine.currentUsageHours} / ${machine.lastMaintenanceUsageHours + machine.maintenanceIntervalHours} h</td><td>${actionButtons('machine', machine)}</td></tr>`).join('');
  document.getElementById('pageBody').innerHTML = `<section class="panel form-panel" id="formPanel"><div class="section-heading"><div><p class="eyebrow">Database record</p><h2 id="machineFormTitle">Add machine</h2></div></div>${machineForm()}</section><section class="panel"><div class="section-heading"><div><p class="eyebrow">${state.machines.length} records</p><h2>Equipment inventory</h2></div><input id="machineSearch" class="search" placeholder="Search machines..." /></div>${table(['ID', 'Machine', 'Code', 'Status', 'Next service', 'Actions'], rows)}</section>`;
  document.getElementById('machineForm').addEventListener('submit', submitMachine);
  document.getElementById('machineSearch').addEventListener('input', event => filterRows(event.target.value));
  if (location.hash === '#new') document.getElementById('machineName').focus();
}

function technicianForm(technician = {}) {
  return `<form id="technicianForm" class="form-grid" data-id="${technician.id || ''}">
    ${formField('Full name', 'techName', 'text', technician.name, true)}
    ${formField('Email', 'techEmail', 'email', technician.email, true)}
    ${formField('Phone', 'techPhone', 'tel', technician.phone, true)}
    ${formField('Specialization', 'techSpecialization', 'text', technician.specialization, true)}
    <label>Status<select id="techStatus"><option>AVAILABLE</option><option>BUSY</option><option>INACTIVE</option></select></label>
    <div class="form-actions wide"><button class="button button-primary" type="submit">${technician.id ? 'Update technician' : 'Add technician'}</button>${technician.id ? '<button class="button" type="button" data-action="cancel-form">Cancel</button>' : ''}</div>
  </form>`;
}

async function renderTechnicians() {
  layout('Technicians', 'People and capacity', '<a class="button button-primary" href="#new">+ Add technician</a>');
  const rows = state.technicians.map(technician => `<tr><td>#${technician.id}</td><td><strong>${escapeHtml(technician.name)}</strong><small>${escapeHtml(technician.email)}</small></td><td>${escapeHtml(technician.phone)}</td><td>${escapeHtml(technician.specialization)}</td><td>${statusBadge(technician.status)}</td><td>${actionButtons('technician', technician)}</td></tr>`).join('');
  document.getElementById('pageBody').innerHTML = `<section class="panel form-panel"><p class="eyebrow">Database record</p><h2>Add technician</h2>${technicianForm()}</section><section class="panel"><div class="section-heading"><div><p class="eyebrow">${state.technicians.length} records</p><h2>Team directory</h2></div></div>${table(['ID', 'Technician', 'Phone', 'Specialization', 'Status', 'Actions'], rows)}</section>`;
  document.getElementById('technicianForm').addEventListener('submit', submitTechnician);
}

function taskForm(task = {}) {
  return `<form id="taskForm" class="form-grid" data-id="${task.id || ''}">
    <label>Machine<select id="taskMachine" required><option value="">Choose machine</option>${options(state.machines, item => `${item.name} (${item.machineCode})`)}</select></label>
    <label>Technician<select id="taskTechnician"><option value="">Unassigned</option>${options(state.technicians, item => item.name)}</select></label>
    ${formField('Task title', 'taskTitle', 'text', task.taskTitle, true)}
    ${formField('Scheduled usage (hours)', 'scheduledUsage', 'number', task.scheduledUsageHours, true, 'min="1"')}
    <label>Priority<select id="taskPriority"><option>LOW</option><option>MEDIUM</option><option>HIGH</option><option>CRITICAL</option></select></label>
    <label>Status<select id="taskStatus"><option>OPEN</option><option>IN_PROGRESS</option><option>COMPLETED</option><option>OVERDUE</option><option>CANCELLED</option></select></label>
    <label class="wide">Description<textarea id="taskDescription">${escapeHtml(task.description || '')}</textarea></label>
    <label class="wide">Notes<textarea id="taskNotes">${escapeHtml(task.notes || '')}</textarea></label>
    <div class="form-actions wide"><button class="button button-primary" type="submit">${task.id ? 'Update task' : 'Create task'}</button>${task.id ? '<button class="button" type="button" data-action="cancel-form">Cancel</button>' : ''}</div>
  </form>`;
}

async function renderTasks() {
  layout('Maintenance tasks', 'Work orders', '<a class="button button-primary" href="#new">+ Create task</a>');
  const rows = state.tasks.map(task => `<tr><td>#${task.id}</td><td><strong>${escapeHtml(task.taskTitle)}</strong><small>${escapeHtml(task.description || 'No description')}</small></td><td>${task.machineId}</td><td>${task.technicianId || 'Unassigned'}</td><td>${statusBadge(task.priority)}</td><td>${statusBadge(task.status)}</td><td>${actionButtons('task', task)}</td></tr>`).join('');
  document.getElementById('pageBody').innerHTML = `<section class="panel form-panel"><p class="eyebrow">Database record</p><h2>Create maintenance task</h2>${taskForm()}</section><section class="panel"><div class="section-heading"><div><p class="eyebrow">${state.tasks.length} records</p><h2>Work order queue</h2></div><select id="taskFilter" class="search"><option value="">All statuses</option><option>OPEN</option><option>IN_PROGRESS</option><option>OVERDUE</option><option>COMPLETED</option><option>CANCELLED</option></select></div>${table(['ID', 'Task', 'Machine', 'Technician', 'Priority', 'Status', 'Actions'], rows)}</section>`;
  document.getElementById('taskForm').addEventListener('submit', submitTask);
  document.getElementById('taskFilter').addEventListener('change', event => filterRows(event.target.value));
}

function usageForm(log = {}) {
  return `<form id="usageForm" class="form-grid" data-id="${log.id || ''}">
    <label>Machine<select id="logMachine" required><option value="">Choose machine</option>${options(state.machines, item => `${item.name} (${item.machineCode})`)}</select></label>
    ${formField('Usage added (hours)', 'logHours', 'number', log.usageHours, true, 'min="0"')}
    <label class="wide">Remarks<textarea id="logRemarks">${escapeHtml(log.remarks || '')}</textarea></label>
    <div class="form-actions wide"><button class="button button-primary" type="submit">${log.id ? 'Update usage log' : 'Log usage'}</button>${log.id ? '<button class="button" type="button" data-action="cancel-form">Cancel</button>' : ''}</div>
  </form>`;
}

async function renderUsage() {
  layout('Usage logs', 'Operational history', '<a class="button button-primary" href="#new">+ Log usage</a>');
  const machineName = id => state.machines.find(machine => machine.id === id)?.name || `Machine #${id}`;
  const rows = state.usageLogs.map(log => `<tr><td>#${log.id}</td><td><strong>${escapeHtml(machineName(log.machineId))}</strong><small>Machine #${log.machineId}</small></td><td>${log.usageHours} h</td><td>${new Date(log.loggedAt).toLocaleString()}</td><td>${escapeHtml(log.remarks)}</td><td>${actionButtons('usage', log)}</td></tr>`).join('');
  document.getElementById('pageBody').innerHTML = `<section class="panel form-panel"><p class="eyebrow">Database record</p><h2>Log machine usage</h2>${usageForm()}</section><section class="panel"><div class="section-heading"><div><p class="eyebrow">${state.usageLogs.length} records</p><h2>Usage history</h2></div></div>${table(['ID', 'Machine', 'Usage', 'Logged', 'Remarks', 'Actions'], rows)}</section>`;
  document.getElementById('usageForm').addEventListener('submit', submitUsage);
}

async function renderDashboard() {
  const notifications = await fetchJson(API.notifications);
  layout('Operations overview', 'Today at a glance', '<button id="dashboardRefresh" class="button button-primary">Refresh data</button>');
  const overdue = state.tasks.filter(task => task.status === 'OVERDUE').length;
  document.getElementById('pageBody').innerHTML = `<section class="metric-grid"><a class="metric" href="/machines.html"><span>Machines</span><strong>${state.machines.length}</strong><small>View asset register →</small></a><a class="metric" href="/technicians.html"><span>Technicians</span><strong>${state.technicians.length}</strong><small>Manage team →</small></a><a class="metric" href="/tasks.html"><span>Open work orders</span><strong>${state.tasks.filter(task => ['OPEN', 'IN_PROGRESS'].includes(task.status)).length}</strong><small>${overdue} overdue</small></a><a class="metric" href="/usage-logs.html"><span>Usage entries</span><strong>${state.usageLogs.length}</strong><small>Review history →</small></a></section><section class="dashboard-grid"><section class="panel"><div class="section-heading"><div><p class="eyebrow">Attention needed</p><h2>Maintenance alerts</h2></div></div><div class="alert-list">${notifications.length ? notifications.map(item => `<div class="alert ${item.type === 'OVERDUE' ? 'alert-danger' : ''}"><strong>${escapeHtml(item.machineCode)}</strong><span>${escapeHtml(item.message)}</span><b>${item.remainingHours} h</b></div>`).join('') : '<p class="empty">Everything is on schedule.</p>'}</div></section><section class="panel"><p class="eyebrow">Quick actions</p><h2>Keep operations moving</h2><div class="quick-links"><a href="/tasks.html#new">Create a work order</a><a href="/usage-logs.html#new">Record machine usage</a><a href="/machines.html#new">Register a machine</a><a href="/technicians.html#new">Add a technician</a></div></section></section>`;
  document.getElementById('dashboardRefresh').addEventListener('click', refresh);
}

function values(type) {
  if (type === 'machine') return { name: machineName.value, machineCode: machineCode.value, description: machineDescription.value, maintenanceIntervalHours: Number(maintenanceInterval.value), currentUsageHours: Number(currentUsage.value), lastMaintenanceUsageHours: Number(lastMaintenance.value), status: machineStatus.value };
  if (type === 'technician') return { name: techName.value, email: techEmail.value, phone: techPhone.value, specialization: techSpecialization.value, status: techStatus.value };
  if (type === 'task') return { machineId: Number(taskMachine.value), technicianId: taskTechnician.value ? Number(taskTechnician.value) : null, taskTitle: taskTitle.value, description: taskDescription.value, scheduledUsageHours: Number(scheduledUsage.value), status: taskStatus.value, priority: taskPriority.value, notes: taskNotes.value };
  return { machineId: Number(logMachine.value), usageHours: Number(logHours.value), remarks: logRemarks.value };
}

async function submit(type, event) {
  event.preventDefault();
  const form = event.target; const id = form.dataset.id; const endpoint = API[type === 'machine' ? 'machines' : type === 'technician' ? 'technicians' : type === 'task' ? 'tasks' : 'usageLogs'];
  try { await fetchJson(`${endpoint}${id ? `/${id}` : ''}`, { method: id ? 'PUT' : 'POST', body: JSON.stringify(values(type)) }); await refresh(); } catch (error) { alert(error.message); }
}
const submitMachine = event => submit('machine', event);
const submitTechnician = event => submit('technician', event);
const submitTask = event => submit('task', event);
const submitUsage = event => submit('usage', event);

async function edit(type, id) {
  const item = state[type === 'machine' ? 'machines' : type === 'technician' ? 'technicians' : type === 'task' ? 'tasks' : 'usageLogs'].find(record => record.id === id);
  if (!item) return;
  if (type === 'machine') { document.querySelector('.form-panel').innerHTML = `<p class="eyebrow">Database record</p><h2>Edit machine</h2>${machineForm(item)}`; machineStatus.value = item.status; document.getElementById('machineForm').addEventListener('submit', submitMachine); }
  if (type === 'technician') { document.querySelector('.form-panel').innerHTML = `<p class="eyebrow">Database record</p><h2>Edit technician</h2>${technicianForm(item)}`; techStatus.value = item.status; document.getElementById('technicianForm').addEventListener('submit', submitTechnician); }
  if (type === 'task') { document.querySelector('.form-panel').innerHTML = `<p class="eyebrow">Database record</p><h2>Edit task</h2>${taskForm(item)}`; taskMachine.value = item.machineId; taskTechnician.value = item.technicianId || ''; taskPriority.value = item.priority; taskStatus.value = item.status; document.getElementById('taskForm').addEventListener('submit', submitTask); }
  if (type === 'usage') { document.querySelector('.form-panel').innerHTML = `<p class="eyebrow">Database record</p><h2>Edit usage log</h2>${usageForm(item)}`; logMachine.value = item.machineId; document.getElementById('usageForm').addEventListener('submit', submitUsage); }
  document.querySelector('.form-panel').scrollIntoView({ behavior: 'smooth' });
}

async function remove(type, id) { if (!confirm('Delete this record? This cannot be undone.')) return; const endpoint = API[type === 'machine' ? 'machines' : type === 'technician' ? 'technicians' : type === 'task' ? 'tasks' : 'usageLogs']; try { await fetchJson(`${endpoint}/${id}`, { method: 'DELETE' }); await refresh(); } catch (error) { alert(error.message); } }
async function taskAction(action, id) { try { await fetchJson(`${API.tasks}/${id}/${action}`, { method: 'PUT' }); await refresh(); } catch (error) { alert(error.message); } }
function filterRows(value) { document.querySelectorAll('tbody tr').forEach(row => { row.hidden = Boolean(value) && !row.textContent.toLowerCase().includes(value.toLowerCase()); }); }

document.addEventListener('click', event => { const button = event.target.closest('[data-action]'); if (!button) return; const action = button.dataset.action; if (action === 'edit') edit(button.dataset.type, Number(button.dataset.id)); if (action === 'delete') remove(button.dataset.type, Number(button.dataset.id)); if (action === 'start' || action === 'complete') taskAction(action, Number(button.dataset.id)); if (action === 'cancel-form') refresh(); });
document.getElementById('refreshBtn')?.addEventListener('click', refresh);

async function refresh() { try { await loadState(); if (page === 'dashboard') await renderDashboard(); if (page === 'machines') await renderMachines(); if (page === 'technicians') await renderTechnicians(); if (page === 'tasks') await renderTasks(); if (page === 'usage') await renderUsage(); } catch (error) { document.getElementById('pageContent').innerHTML = `<section class="panel error"><h2>Could not load the workspace</h2><p>${escapeHtml(error.message)}</p><button class="button button-primary" onclick="refresh()">Try again</button></section>`; } }

nav();
refresh();
