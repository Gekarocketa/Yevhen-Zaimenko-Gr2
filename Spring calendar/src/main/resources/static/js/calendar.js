document.addEventListener('DOMContentLoaded', function() {
    // Panel Logic
    const panel = document.getElementById('subjects-panel');
    const btn = document.getElementById('show-subjects-btn');
    let panelOpen = false;
    let selectedSubjectId = null;

    if (btn && panel) {
        btn.addEventListener('click', function() {
            panel.style.transform = panelOpen ? 'translateX(-100%)' : 'translateX(0)';
            panelOpen = !panelOpen;
            if(!panelOpen) {
                selectedSubjectId = null;
                document.querySelectorAll('.subject-item').forEach(i => i.style.background = 'none');
            }
        });
    }
    
    // Subject Selection
    document.querySelectorAll('.subject-item').forEach(function(item) {
        item.addEventListener('click', function() {
             document.querySelectorAll('.subject-item').forEach(i => i.style.background = 'none');
             this.style.background = '#333';
             selectedSubjectId = this.dataset.id;
        });
    });

    // Calendar Day Click (Add Subject)
    document.querySelectorAll('.calendar__day-link').forEach(function(link) {
        link.addEventListener('click', function(e) {
            if (panelOpen && selectedSubjectId) {
                e.preventDefault();
                const date = this.dataset.date;
                // AJAX call to add subject
                fetch('/subject-date', {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/x-www-form-urlencoded',
                         'X-CSRF-TOKEN': document.querySelector('meta[name="csrf-token"]').getAttribute('content')
                    },
                    body: new URLSearchParams({
                        'subject_id': selectedSubjectId,
                        'date': date
                    })
                })
                .then(r => r.json())
                .then(data => {
                    if(data.success) {
                        location.reload();
                    } else {
                        alert('Error: ' + (data.message || 'Unknown'));
                    }
                });
            }
        });
    });

    // Filter Logic (simplified)
    const searchInput = document.getElementById('przedmioty-szukaj');
    if(searchInput) {
        searchInput.addEventListener('input', function() {
            const val = this.value.toLowerCase();
            document.querySelectorAll('.subject-item').forEach(item => {
                const text = item.innerText.toLowerCase();
                item.style.display = text.includes(val) ? 'flex' : 'none';
            });
        });
    }
});
