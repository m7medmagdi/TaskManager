document.addEventListener('DOMContentLoaded', function () {
    const taskForm = document.getElementById('taskForm');
    const taskList = document.getElementById('taskList');
    const sessionId = localStorage.getItem('sessionId'); // Get session ID from localStorage
    const baseApi = 'http://localhost:8080/demo1_war_exploded/api'; // Replace with your actual API base URL
    let editingTaskId = null; // Store the ID of the task being edited

    // Check if sessionId exists
    if (!sessionId) {
        alert('You must be logged in to access tasks.');
        window.location.href = 'login.html'; // Redirect to login if no session ID found
        return;
    }

    // Logout functionality
    const logoutBtn = document.getElementById('logoutBtn');
    logoutBtn.addEventListener('click', function () {
        localStorage.removeItem('sessionId'); // Clear session ID from localStorage
        window.location.href = 'login.html'; // Redirect to login page
    });

    // Fetch and display tasks
    function fetchTasks() {
        fetch(`${baseApi}/tasks`, {
            headers: {
                'Content-Type': 'application/json',
                'X-Session-ID': sessionId
            }
        })
        .then(response => response.json())
        .then(tasks => {
            taskList.innerHTML = ''; // Clear the task list
            tasks.forEach(task => {
                const li = document.createElement('li');
                li.className = 'list-group-item';
                li.innerHTML = `
                    <h5>${task.title}</h5>
                    <p>${task.description}</p>
                    <p>Completed: ${task.completed ? 'Yes' : 'No'}</p>
                    <button onclick="editTask(${task.id}, '${task.title}', '${task.description}', ${task.completed})" class="btn btn-warning btn-sm me-2">Edit</button>
                    <button onclick="deleteTask(${task.id})" class="btn btn-danger btn-sm">Delete</button>
                `;
                taskList.appendChild(li);
            });
        })
        .catch(error => console.error('Error fetching tasks:', error));
    }

    // Add a new task or edit an existing one
    taskForm.addEventListener('submit', function (event) {
        event.preventDefault();
        const task = {
            title: document.getElementById('taskTitle').value,
            description: document.getElementById('taskDescription').value,
            completed: document.getElementById('taskCompleted').checked
        };

        if (editingTaskId) {
            // Update the task
            fetch(`${baseApi}/tasks/${editingTaskId}`, {
                method: 'PUT',
                headers: {
                    'Content-Type': 'application/json',
                    'X-Session-ID': sessionId
                },
                body: JSON.stringify(task)
            })
            .then(response => {
                if (response.ok) {
                    fetchTasks(); // Refresh the task list
                    taskForm.reset(); // Clear the form
                    editingTaskId = null; // Reset editing task ID
                }
            })
            .catch(error => console.error('Error updating task:', error));
        } else {
            // Add a new task
            fetch(`${baseApi}/tasks`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    'X-Session-ID': sessionId
                },
                body: JSON.stringify(task)
            })
            .then(response => {
                if (response.ok) {
                    fetchTasks(); // Refresh the task list
                    taskForm.reset(); // Clear the form
                }
            })
            .catch(error => console.error('Error creating task:', error));
        }
    });

    // Edit a task
    window.editTask = function (taskId, title, description, completed) {
        // Set the task data in the form
        document.getElementById('taskTitle').value = title;
        document.getElementById('taskDescription').value = description;
        document.getElementById('taskCompleted').checked = completed;

        // Store the task ID that is being edited
        editingTaskId = taskId;
    };

    // Delete a task
    window.deleteTask = function (taskId) {
        fetch(`${baseApi}/tasks/${taskId}`, {
            method: 'DELETE',
            headers: {
                'X-Session-ID': sessionId
            }
        })
        .then(response => {
            if (response.ok) {
                fetchTasks(); // Refresh the task list
            }
        })
        .catch(error => console.error('Error deleting task:', error));
    };

    // Initial fetch of tasks
    fetchTasks();
});
