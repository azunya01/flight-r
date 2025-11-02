const form = document.getElementById("search-form");
const resultsContainer = document.getElementById("results");
const statusLabel = document.getElementById("result-status");
const resetBtn = document.getElementById("reset-btn");

const seatTypeMap = new Map([
    [1, "头等舱"],
    [2, "公务舱"],
    [3, "经济舱"],
    [4, "超级经济舱"],
]);

function formatDateTimeInput(value) {
    if (!value) return "";
    // datetime-local -> 2023-04-01T09:30 or 2023-04-01T09:30:00
    const normalized = value.replace("T", " ");
    return normalized.includes(":") && normalized.split(":").length === 2
        ? `${normalized}:00`
        : normalized;
}

function formatDateTimeDisplay(value) {
    if (!value) return "-";
    return value.replace("T", " ");
}

function formatPrice(value) {
    if (value === undefined || value === null || isNaN(value)) return "-";
    return Number(value).toFixed(2);
}

function updateStatus(message, isError = false) {
    statusLabel.textContent = message;
    statusLabel.style.color = isError ? "#dc2626" : "var(--text-muted)";
}

function renderEmpty(message = "未查询到符合条件的航班") {
    resultsContainer.innerHTML = `<div class="empty-hint">${message}</div>`;
}

function renderFlights(list) {
    if (!list || list.length === 0) {
        renderEmpty();
        return;
    }

    const markup = list
        .map((flight) => {
            const seats = Array.isArray(flight.seats) ? flight.seats : [];
            const seatRows = seats
                .map((seat) => {
                    const name = seat.seatTypeName || seatTypeMap.get(seat.seatTypeId) || `舱位 ${seat.seatTypeId}`;
                    return `<tr>
                        <td>${name}</td>
                        <td>¥${formatPrice(seat.price)}</td>
                        <td>${seat.availableSeats ?? "-"}</td>
                    </tr>`;
                })
                .join("");

            const seatTable = seats.length
                ? `<table class="seats-table">
                        <thead>
                            <tr>
                                <th>舱位</th>
                                <th>价格 (含折扣)</th>
                                <th>剩余座位</th>
                            </tr>
                        </thead>
                        <tbody>${seatRows}</tbody>
                   </table>`
                : `<div class="empty-hint">暂无舱位信息</div>`;

            return `<article class="flight-item">
                <div class="flight-header">
                    <h3 class="flight-header__title">${flight.flightId || "未知航班"}</h3>
                    <span class="tag">基础票价 ¥${formatPrice(flight.basePrice)}</span>
                </div>
                <div class="flight-meta">
                    <span>出发：${flight.departureCity || "-"} ｜ ${formatDateTimeDisplay(flight.departureTime)}</span>
                    <span>到达：${flight.arrivalCity || "-"} ｜ ${formatDateTimeDisplay(flight.arrivalTime)}</span>
                </div>
                ${seatTable}
            </article>`;
        })
        .join("");

    resultsContainer.innerHTML = markup;
}

async function fetchFlights(params) {
    const query = new URLSearchParams(params);
    const url = `/user/flight/list?${query.toString()}`;

    const response = await fetch(url, {
        headers: {
            "Accept": "application/json",
        },
    });

    if (!response.ok) {
        throw new Error(`请求失败：${response.status}`);
    }

    const data = await response.json();
    if (data.code !== 1) {
        throw new Error(data.msg || "后端返回异常");
    }

    return data.data || [];
}

form.addEventListener("submit", async (event) => {
    event.preventDefault();

    const formData = new FormData(form);
    const params = {};

    for (const [key, rawValue] of formData.entries()) {
        if (!rawValue) continue;
        if (key.toLowerCase().includes("time")) {
            const formatted = formatDateTimeInput(rawValue);
            if (!formatted) continue;
            params[key] = formatted;
        } else {
            params[key] = rawValue.trim();
        }
    }

    updateStatus("正在查询，请稍候…");
    resultsContainer.innerHTML = '<div class="loading-spinner">查询中</div>';

    try {
        const flights = await fetchFlights(params);
        updateStatus(`共找到 ${flights.length} 个航班`);
        renderFlights(flights);
    } catch (error) {
        console.error(error);
        updateStatus(error.message || "查询失败", true);
        renderEmpty("查询过程中出现问题，请稍后重试。");
    }
});

resetBtn.addEventListener("click", () => {
    updateStatus("请先填写查询条件");
    renderEmpty("尚未进行查询");
});

// 初始占位提示
renderEmpty("尚未进行查询");
